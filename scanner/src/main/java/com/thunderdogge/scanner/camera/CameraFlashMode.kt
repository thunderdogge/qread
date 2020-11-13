package com.thunderdogge.scanner.camera

@Suppress("DEPRECATION")
enum class CameraFlashMode(val id: String) {
    On(android.hardware.Camera.Parameters.FLASH_MODE_ON),
    Off(android.hardware.Camera.Parameters.FLASH_MODE_OFF),
    Auto(android.hardware.Camera.Parameters.FLASH_MODE_AUTO),
    Torch(android.hardware.Camera.Parameters.FLASH_MODE_TORCH),
    RedEye(android.hardware.Camera.Parameters.FLASH_MODE_RED_EYE)
}
