package com.thunderdogge.scanner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.lang.ref.WeakReference

abstract class BaseScanDetectorCallback(context: Activity) : ScanDetectorCallback {
    private val activity = WeakReference(context)

    override fun onUnavailable() {
        val context = activity.get() ?: return
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.com_thunderdogge_scanner_dialog_detector_unavailable_error_title)
            .setMessage(R.string.com_thunderdogge_scanner_dialog_detector_unavailable_error_text)
            .setPositiveButton(R.string.com_thunderdogge_scanner_dialog_detector_unavailable_positive) { _, _ -> navigateServiceSettings() }
            .setNegativeButton(R.string.com_thunderdogge_scanner_dialog_detector_unavailable_negative, null)
            .show()
    }

    private fun navigateServiceSettings() {
        val context = activity.get() ?: return
        val intent = Intent().also {
            it.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            it.data = Uri.fromParts("package", "com.google.android.gms", null)
        }

        context.startActivity(intent)
    }
}