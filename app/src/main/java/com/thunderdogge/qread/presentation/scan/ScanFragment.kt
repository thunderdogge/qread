package com.thunderdogge.qread.presentation.scan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thunderdogge.qread.R
import com.thunderdogge.qread.databinding.FragmentScanBinding
import com.thunderdogge.qread.extensions.lazily
import com.thunderdogge.qread.presentation.base.BaseFragment
import com.thunderdogge.qread.presentation.extensions.showSnackbar
import com.thunderdogge.scanner.BaseScanDetectorCallback
import com.thunderdogge.scanner.ScanCameraCallback
import com.thunderdogge.scanner.ScanConfig
import com.thunderdogge.scanner.ScanManager
import com.thunderdogge.scanner.camera.CameraFocusMode
import kotlinx.android.synthetic.main.fragment_scan.*
import timber.log.Timber

class ScanFragment : BaseFragment() {

    private val viewModel by viewModel<ScanViewModel>()

    private val scanManager by lazily {
        val config = ScanConfig.Builder()
            .setBarcodeFormats(Barcode.QR_CODE or Barcode.EAN_13)
            .setCameraFocusMode(CameraFocusMode.ContinuousPicture)
            .build()

        ScanManager(config, this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, bundle: Bundle?): View? {
        val binding = FragmentScanBinding.inflate(inflater, container, false)
        binding.vm = viewModel

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initScanner()
        initObservers()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        scanManager.requestCameraPermissions()
    }

    override fun onResume() {
        super.onResume()

        scanManager.startScannerDelayed()
    }

    override fun onPause() {
        super.onPause()

        scanManager.stopScanner()
    }

    override fun onDestroy() {
        super.onDestroy()

        scanManager.releaseScanner()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        scanManager.handleCameraPermissionsResult(requestCode, grantResults)
    }

    private fun initScanner() {
        scanManager.setCameraPreview(cpPreview)
        scanManager.setCameraCallback(CameraCallback())
        scanManager.setDetectorCallback(DetectorCallback())
    }

    private fun initObservers() {
        viewModel.isFlashOn.observe(viewLifecycleOwner, Observer { toggleCameraFlash(it == true) })
        viewModel.isScanSucceed.observe(viewLifecycleOwner, Observer { fvScanRect.toggle(it == true) })
        viewModel.snackbarMessage.observe(viewLifecycleOwner, Observer(::showSnackbar))
        viewModel.isAutoFocusForced.observe(viewLifecycleOwner, Observer { if (it == true) tryForceCameraAutoFocus() })
        viewModel.cameraStartFailureDialog.observe(viewLifecycleOwner, Observer { if (it == true) showCameraStartFailureDialog() })
        viewModel.cameraPermissionDeniedDialog.observe(viewLifecycleOwner, Observer { if (it == true) showCameraStartFailureDialog() })
    }

    private fun toggleCameraFlash(flag: Boolean) {
        val isSuccessful = cpPreview.toggleFlashMode(flag)
        if (flag && isSuccessful == false) {
            showCameraFlashToggleFailedDialog()
        }

        val iconResource = if (flag) R.drawable.ic_flash_off else R.drawable.ic_flash_on
        val iconDrawable = ContextCompat.getDrawable(requireContext(), iconResource)
        ibActionFlash.setImageDrawable(iconDrawable)
    }

    private fun tryForceCameraAutoFocus() {
        try {
            cpPreview.autoFocus()
        } catch (e: Throwable) {
            Timber.w(e, "Force camera auto focus failed")
        }
    }

    private fun showCameraStartFailureDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_camera_open_failed_title)
            .setMessage(R.string.dialog_camera_open_failed_text)
            .setPositiveButton(R.string.dialog_camera_open_failed_positive_text) { _, _ -> viewModel.navigateAppSettings() }
            .show()
    }

    private fun showCameraFlashToggleFailedDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.dialog_camera_flash_failed_title)
            .setMessage(R.string.dialog_camera_flash_failed_text)
            .setPositiveButton(R.string.dialog_common_button_ok) { _, _ -> viewModel.isFlashOn.postValue(false) }
            .show()
    }

    private inner class CameraCallback : ScanCameraCallback {
        override fun onStartFailed(error: Throwable) {
            viewModel.onCameraStartFailed(error)
        }

        override fun onPermissionDenied(shouldShowRationale: Boolean) {
            viewModel.onCameraPermissionDenied()
        }
    }

    private inner class DetectorCallback : BaseScanDetectorCallback(requireActivity()) {
        override fun onDetect(detections: Detector.Detections<Barcode>) {
            if (detections.detectedItems.size() > 0) {
                val firstItem = detections.detectedItems.valueAt(0)
                viewModel.onBarcodeDetection(firstItem)
            }
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return ScanFragment()
        }
    }
}