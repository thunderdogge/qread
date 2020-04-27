package com.thunderdogge.scanner

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.thunderdogge.scanner.camera.CameraFlashMode
import com.thunderdogge.scanner.camera.CameraPreview
import com.thunderdogge.scanner.camera.CameraSource

class ScanManager private constructor(
    private val config: ScanConfig,
    private val activity: Activity,
    private val fragment: Fragment?
) {
    constructor(config: ScanConfig, activity: Activity) : this(config, activity, null)

    constructor(config: ScanConfig, fragment: Fragment) : this(config, fragment.requireActivity(), fragment)

    private var cameraPreview: CameraPreview? = null

    private var cameraCallback: ScanCameraCallback? = null

    private var detectorCallback: ScanDetectorCallback? = null

    private val handler = Handler()

    private val detector by lazy(LazyThreadSafetyMode.NONE) {
        createBarcodeDetector(activity, config)
    }

    private val cameraSource by lazy(LazyThreadSafetyMode.NONE) {
        createCameraSource(activity, config)
    }

    private val cameraPreviewCallback = object : CameraPreview.CameraCallback {
        override fun onStartFailed(error: Throwable) {
            cameraCallback?.onStartFailed(error)
        }
    }

    fun startScanner() {
        val googleApiChecker = GoogleApiAvailability.getInstance()
        val googleServiceCheckCode = googleApiChecker.isGooglePlayServicesAvailable(activity)
        if (googleServiceCheckCode != ConnectionResult.SUCCESS) {
            googleApiChecker.getErrorDialog(activity, googleServiceCheckCode, GOOGLE_SERVICE_RESULT_CODE)?.show()
            return
        }

        if (!detector.isOperational) {
            detectorCallback?.onUnavailable()
            return
        }

        if (!isCameraPermissionGranted()) {
            return
        }

        try {
            cameraPreview?.start(cameraSource, cameraPreviewCallback)
        } catch (error: Throwable) {
            try {
                cameraPreview?.release()
            } catch (innerError: Throwable) {
                cameraCallback?.onReleaseFailed(innerError)
            } finally {
                cameraCallback?.onStartFailed(error)
            }
        }
    }

    fun startScannerDelayed(delayMillis: Long = 1) {
        handler.postDelayed(::startScanner, delayMillis)
    }

    fun stopScanner() {
        try {
            cameraPreview?.stop()
        } catch (error: Throwable) {
            cameraCallback?.onStopFailed(error)
        }
    }

    fun releaseScanner() {
        try {
            cameraPreview?.release()
        } catch (error: Throwable) {
            cameraCallback?.onReleaseFailed(error)
        }
    }

    fun setCameraFlash(value: Boolean): Boolean {
        val mode = if (value) {
            CameraFlashMode.Torch
        } else {
            CameraFlashMode.Off
        }

        return setCameraFlashMode(mode)
    }

    fun setCameraFlashMode(value: CameraFlashMode): Boolean {
        return cameraSource.setFlashMode(value.id)
    }

    fun catchCameraPicture(callback: (ByteArray) -> Unit) {
        try {
            cameraSource.takePicture({}, callback)
        } catch (error: Throwable) {
            cameraCallback?.onCatchPictureFailed(error)
        }
    }

    fun forceCameraFocus(callback: CameraSource.AutoFocusCallback? = null) {
        try {
            cameraPreview?.autoFocus(callback)
        } catch (error: Throwable) {
            cameraCallback?.onForceFocusFailed(error)
        }
    }

    fun setCameraPreview(value: CameraPreview) {
        cameraPreview = value
    }

    fun setDetectorCallback(value: ScanDetectorCallback) {
        detectorCallback?.onRelease()
        detectorCallback = value
    }

    fun setCameraCallback(value: ScanCameraCallback) {
        cameraCallback = value
    }

    fun requestCameraPermissions() {
        if (isCameraPermissionGranted()) {
            cameraCallback?.onPermissionGranted()
            return
        }

        val permissions = arrayOf(CAMERA_PERMISSION)
        if (fragment == null) {
            ActivityCompat.requestPermissions(activity, permissions, CAMERA_PERMISSION_REQUEST_CODE)
        } else {
            fragment.requestPermissions(permissions, CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    fun handleCameraPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode != CAMERA_PERMISSION_REQUEST_CODE) {
            return
        }

        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            cameraCallback?.onPermissionGranted()
            return
        }

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, CAMERA_PERMISSION)) {
            cameraCallback?.onPermissionDenied(true)
            return
        }

        cameraCallback?.onPermissionDenied(false)
    }

    private fun createCameraSource(context: Context, config: ScanConfig): CameraSource {
        return CameraSource.Builder(context, detector)
            .setFacing(config.cameraFacing.id)
            .setFocusMode(config.cameraFocusMode.id)
            .setRequestedFps(config.cameraFps)
            .setRequestedPreviewSize(config.cameraPreviewWidth, config.cameraPreviewHeight)
            .build()
    }

    private fun createBarcodeDetector(context: Context, config: ScanConfig): BarcodeDetector {
        val processor = object : Detector.Processor<Barcode> {
            override fun release() {
                detectorCallback?.onRelease()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                if (detections.detectedItems.size() > 0) {
                    detectorCallback?.onDetect(detections)
                }
            }
        }

        val detector = BarcodeDetector.Builder(context)
            .setBarcodeFormats(config.barcodeFormats)
            .build()

        return detector.also { it.setProcessor(processor) }
    }

    private fun isCameraPermissionGranted(): Boolean {
        val checkResult = ContextCompat.checkSelfPermission(activity, CAMERA_PERMISSION)
        return checkResult == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val GOOGLE_SERVICE_RESULT_CODE = 9001
        private const val CAMERA_PERMISSION = Manifest.permission.CAMERA
        private const val CAMERA_PERMISSION_REQUEST_CODE = 10001
    }
}