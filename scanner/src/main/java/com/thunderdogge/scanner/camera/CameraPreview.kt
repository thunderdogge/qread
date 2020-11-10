package com.thunderdogge.scanner.camera

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.AttributeSet
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import java.io.IOException

@SuppressWarnings("deprecation")
class CameraPreview : ViewGroup {
    private var surfaceView = SurfaceView(context)
    private var isStartRequested: Boolean = false
    private var isSurfaceAvailable: Boolean = false
    private var cameraSource: CameraSource? = null
    private var cameraCallback: CameraCallback? = null

    constructor(context: Context) : super(context) {
        initiate()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        initiate()
    }

    private fun initiate() {
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }

    fun start(source: CameraSource, callback: CameraCallback) {
        cameraSource?.stop()
        cameraSource = source
        cameraCallback = callback
        isStartRequested = true
        startIfReady()
    }

    fun stop() {
        cameraSource?.stop()
    }

    fun release() {
        cameraSource?.release()
        cameraSource = null
    }

    fun autoFocus(autoFocusCallback: CameraSource.AutoFocusCallback? = null) {
        cameraSource?.autoFocus(autoFocusCallback)
    }

    fun takePicture(successCallback: (ByteArray) -> Unit, failedCallback: (Throwable) -> Unit) {
        try {
            cameraSource?.takePicture({}, successCallback)
        } catch (throwable: Throwable) {
            failedCallback.invoke(throwable)
        }
    }

    fun toggleFlashMode(isActive: Boolean): Boolean? {
        val mode = if (isActive) Camera.Parameters.FLASH_MODE_TORCH else Camera.Parameters.FLASH_MODE_OFF
        return cameraSource?.setFlashMode(mode)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        var width = 320
        var height = 240
        if (cameraSource != null) {
            val size = cameraSource!!.previewSize
            if (size != null) {
                width = size.width
                height = size.height
            }
        }

        // Swap width and height sizes when in portrait, since it will be rotated 90 degrees
        if (isPortraitMode()) {
            width = this.width
            height = this.height
        }

        val layoutWidth = right - left
        val layoutHeight = bottom - top

        // Computes height and width for potentially doing fit width.
        var childWidth = layoutWidth
        var childHeight = (layoutWidth.toFloat() / width.toFloat() * height).toInt()

        // If height is too tall using fit width, does fit height instead.
        if (childHeight > layoutHeight) {
            childHeight = layoutHeight
            childWidth = (layoutHeight.toFloat() / height.toFloat() * width).toInt()
        }

        for (i in 0 until childCount) {
            getChildAt(i).layout(0, 0, childWidth, childHeight)
        }

        try {
            startIfReady()
        } catch (se: SecurityException) {
            // Timber.e(se, "Do not have permission to start the camera")
        } catch (e: Throwable) {
            // Timber.e(e, "Could not start camera source.")
        }
    }

    @Throws(IOException::class, SecurityException::class)
    private fun startIfReady() {
        if (isStartRequested && isSurfaceAvailable && cameraSource != null) {
            cameraSource!!.start(surfaceView.holder)
            isStartRequested = false
        }
    }

    private fun isPortraitMode(): Boolean {
        val orientation = this.context.resources.configuration.orientation
        return orientation == Configuration.ORIENTATION_PORTRAIT
    }

    interface CameraCallback {
        fun onStartFailed(error: Throwable)
    }

    private inner class SurfaceCallback : SurfaceHolder.Callback {
        override fun surfaceCreated(surface: SurfaceHolder) {
            isSurfaceAvailable = true

            try {
                startIfReady()
            } catch (error: Throwable) {
                cameraCallback?.onStartFailed(error)
            }
        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            isSurfaceAvailable = false
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        }
    }
}
