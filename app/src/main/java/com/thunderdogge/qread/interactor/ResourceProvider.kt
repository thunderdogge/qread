package com.thunderdogge.qread.interactor

import android.content.res.Resources
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    private val resources: Resources
) {
    fun getString(resourceId: Int): String {
        return resources.getString(resourceId)
    }
}
