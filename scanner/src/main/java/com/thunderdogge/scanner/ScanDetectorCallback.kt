package com.thunderdogge.scanner

import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode

interface ScanDetectorCallback {
    fun onDetect(detections: Detector.Detections<Barcode>)

    fun onRelease() {
        // no-op
    }

    fun onUnavailable() {
        // no-op
    }
}
