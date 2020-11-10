package com.thunderdogge.qread.presentation

import android.content.Intent
import android.net.Uri
import com.github.terrakok.cicerone.androidx.ActivityScreen
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.thunderdogge.qread.presentation.history.HistoryFragment
import com.thunderdogge.qread.presentation.scan.ScanFragment

object Screens {
    object Scan : FragmentScreen(null, { ScanFragment.newInstance() })

    object History : FragmentScreen(null, { HistoryFragment.newInstance() })

    object ApplicationSettings : ActivityScreen(
        null,
        { context ->
            Intent().also {
                it.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                it.data = Uri.fromParts("package", context.packageName, null)
            }
        }
    )
}
