package com.thunderdogge.scanner

import com.thunderdogge.scanner.camera.CameraFacing
import com.thunderdogge.scanner.camera.CameraFocusMode

class ScanConfig private constructor(
    val cameraFps: Float,
    val cameraFacing: CameraFacing,
    val cameraFocusMode: CameraFocusMode,
    val cameraPreviewWidth: Int,
    val cameraPreviewHeight: Int,
    val barcodeFormats: Int
) {
    class Builder {
        private var cameraFps: Float? = null

        private var cameraFacing: CameraFacing? = null

        private var cameraFocusMode: CameraFocusMode? = null

        private var cameraPreviewWidth: Int? = null

        private var cameraPreviewHeight: Int? = null

        private var barcodeFormats: Int? = null

        fun setCameraFps(value: Float): Builder {
            cameraFps = value
            return this
        }

        fun setCameraFacing(value: CameraFacing): Builder {
            cameraFacing = value
            return this
        }

        fun setCameraFocusMode(value: CameraFocusMode): Builder {
            cameraFocusMode = value
            return this
        }

        fun setCameraPreviewSize(width: Int, height: Int): Builder {
            cameraPreviewWidth = width
            cameraPreviewHeight = height
            return this
        }

        fun setBarcodeFormats(value: Int): Builder {
            barcodeFormats = value
            return this
        }

        fun build(): ScanConfig {
            return ScanConfig(
                cameraFps = cameraFps ?: 15f,
                cameraFacing = cameraFacing ?: CameraFacing.Back,
                cameraFocusMode = cameraFocusMode ?: CameraFocusMode.Auto,
                cameraPreviewWidth = cameraPreviewWidth ?: 1280,
                cameraPreviewHeight = cameraPreviewHeight ?: 720,
                barcodeFormats = barcodeFormats ?: error("Barcode formats are missing")
            )
        }
    }
}
