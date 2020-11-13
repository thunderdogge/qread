package com.thunderdogge.qread.presentation.main

import com.github.terrakok.cicerone.Router
import com.thunderdogge.qread.presentation.Screens
import com.thunderdogge.qread.presentation.base.BaseViewModel
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel() {
    fun onStart() {
        router.newRootScreen(Screens.Scan)
    }

    fun navigateBack() {
        router.exit()
    }
}
