package com.thunderdogge.qread.presentation.extensions

import android.app.Activity
import android.view.View
import com.google.android.material.snackbar.Snackbar

fun Activity.showSnackbar(text: String) {
    val view = findViewById<View>(android.R.id.content)
    if (view != null) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }
}