package com.thunderdogge.qread.presentation.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(resourceId: Int): View {
    return LayoutInflater.from(context).inflate(resourceId, this, false)
}
