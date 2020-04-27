package com.thunderdogge.scanner.camera

@Suppress("DEPRECATION")
enum class CameraFocusMode(val id: String) {
    Auto(android.hardware.Camera.Parameters.FOCUS_MODE_AUTO),
    Edof(android.hardware.Camera.Parameters.FOCUS_MODE_EDOF),
    Fixed(android.hardware.Camera.Parameters.FOCUS_MODE_FIXED),
    Macro(android.hardware.Camera.Parameters.FOCUS_MODE_MACRO),
    Infinity(android.hardware.Camera.Parameters.FOCUS_MODE_INFINITY),
    ContinuousVideo(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO),
    ContinuousPicture(android.hardware.Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
}