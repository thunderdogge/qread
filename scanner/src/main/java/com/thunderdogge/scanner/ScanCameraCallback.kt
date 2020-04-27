package com.thunderdogge.scanner

interface ScanCameraCallback {
    fun onStartFailed(error: Throwable) {
        // no-op
    }

    fun onStopFailed(error: Throwable) {
        // no-op
    }

    fun onReleaseFailed(error: Throwable) {
        // no-op
    }

    fun onForceFocusFailed(error: Throwable) {
        // no-op
    }

    fun onCatchPictureFailed(error: Throwable) {
        // no-op
    }

    fun onPermissionDenied(shouldShowRationale: Boolean) {
        // no-op
    }

    fun onPermissionGranted() {
        // no-op
    }
}