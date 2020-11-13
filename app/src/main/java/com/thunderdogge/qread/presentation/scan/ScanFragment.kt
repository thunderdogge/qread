package com.thunderdogge.qread.presentation.scan

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.thunderdogge.qread.R
import com.thunderdogge.qread.databinding.FragmentScanBinding
import com.thunderdogge.qread.extensions.lazily
import com.thunderdogge.qread.presentation.base.BaseFragment
import com.thunderdogge.scanner.BaseScanDetectorCallback
import com.thunderdogge.scanner.ScanCameraCallback
import com.thunderdogge.scanner.ScanConfig
import com.thunderdogge.scanner.ScanManager
import com.thunderdogge.scanner.camera.CameraFocusMode
import timber.log.Timber

class ScanFragment : BaseFragment(R.layout.fragment_scan) {

    private val viewModel by viewModel<ScanViewModel>()

    private val viewBinding by viewBinding<FragmentScanBinding>()

    private val scanManager by lazily {
        val config = ScanConfig.Builder()
            .setBarcodeFormats(Barcode.ALL_FORMATS)
            .setCameraFocusMode(CameraFocusMode.ContinuousPicture)
            .build()

        ScanManager(config, this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupScanner()
        setupBinding()
        setupListeners()
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

    private fun setupScanner() {
        scanManager.setCameraPreview(viewBinding.cpPreview)
        scanManager.setCameraCallback(CameraCallback())
        scanManager.setDetectorCallback(DetectorCallback())
    }

    private fun setupBinding() {
        viewModel.isFlashOn.observe(viewLifecycleOwner, { toggleCameraFlash(it == true) })
        viewModel.isScanSucceed.observe(viewLifecycleOwner, { viewBinding.fvScanRect.toggle(it == true) })
        viewModel.scanResultFormat.observe(viewLifecycleOwner, { viewBinding.resultFormatTextView.text = it })
        viewModel.scanResultValue.observe(viewLifecycleOwner, { viewBinding.resultValueTextView.text = it })
        viewModel.isScanResultActive.observe(viewLifecycleOwner, { toggleScanResultActive(it == true) })
        viewModel.isAutoFocusForced.observe(viewLifecycleOwner, { if (it == true) tryForceCameraAutoFocus() })
        viewModel.cameraStartFailureDialog.observe(viewLifecycleOwner, { if (it == true) showCameraStartFailureDialog() })
        viewModel.cameraPermissionDeniedDialog.observe(viewLifecycleOwner, { if (it == true) showCameraStartFailureDialog() })
    }

    private fun setupListeners() {
        viewBinding.ibActionFlash.setOnClickListener { viewModel.onFlashClick() }
        viewBinding.ibActionHistory.setOnClickListener { viewModel.onHistoryClick() }
        viewBinding.resultCopyButton.setOnClickListener { viewModel.onResultCopyClick() }
        viewBinding.resultCloseButton.setOnClickListener { viewModel.onResultCloseClick() }
    }

    private fun toggleCameraFlash(flag: Boolean) {
        val isSuccessful = viewBinding.cpPreview.toggleFlashMode(flag)
        if (flag && isSuccessful == false) {
            showCameraFlashToggleFailedDialog()
        }

        val iconResource = if (flag) R.drawable.ic_flash_off else R.drawable.ic_flash_on
        val iconDrawable = ContextCompat.getDrawable(requireContext(), iconResource)
        viewBinding.ibActionFlash.setImageDrawable(iconDrawable)
    }

    private fun toggleScanResultActive(isActive: Boolean) {
        val behaviour = BottomSheetBehavior.from(viewBinding.scanResultLayout)
        behaviour.state = if (isActive) {
            BottomSheetBehavior.STATE_EXPANDED
        } else {
            BottomSheetBehavior.STATE_HIDDEN
        }
    }

    private fun tryForceCameraAutoFocus() {
        try {
            viewBinding.cpPreview.autoFocus()
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
