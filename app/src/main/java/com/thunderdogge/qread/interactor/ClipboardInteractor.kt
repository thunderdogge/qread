package com.thunderdogge.qread.interactor

import android.content.ClipData
import android.content.ClipboardManager
import javax.inject.Inject

class ClipboardInteractor @Inject constructor(
    private val clipboardManager: ClipboardManager
) {
    fun copyValue(value: String, label: String = "") {
        val clipData = ClipData.newPlainText(label, value)
        clipboardManager.setPrimaryClip(clipData)
    }
}