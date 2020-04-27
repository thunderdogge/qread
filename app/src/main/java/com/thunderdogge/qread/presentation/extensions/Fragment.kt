package com.thunderdogge.qread.presentation.extensions

import androidx.fragment.app.Fragment

fun Fragment.showSnackbar(text: String) {
    requireActivity().showSnackbar(text)
}