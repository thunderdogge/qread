package com.thunderdogge.scanner.camera

import android.hardware.Camera

@Suppress("DEPRECATION")
enum class CameraFacing(val id: Int) {
    Back(Camera.CameraInfo.CAMERA_FACING_BACK),
    Front(Camera.CameraInfo.CAMERA_FACING_FRONT)
}
