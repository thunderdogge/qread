package com.thunderdogge.qread.presentation.common

class DialogLiveEvent : SingleLiveEvent<Boolean>() {
    fun show() {
        super.setValue(true)
    }

    fun hide() {
        super.setValue(false)
    }
}