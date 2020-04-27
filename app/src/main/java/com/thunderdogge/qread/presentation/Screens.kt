package com.thunderdogge.qread.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import com.thunderdogge.qread.presentation.history.HistoryFragment
import com.thunderdogge.qread.presentation.scan.ScanFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object Screens {
    object Scan : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return ScanFragment.newInstance()
        }
    }

    object History : SupportAppScreen() {
        override fun getFragment(): Fragment {
            return HistoryFragment.newInstance()
        }
    }

    object ApplicationSettings : SupportAppScreen() {
        override fun getActivityIntent(context: Context?): Intent {
            return Intent().also {
                it.action = android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                it.data = Uri.fromParts("package", context?.packageName, null)
            }
        }
    }
}