package com.thunderdogge.qread.presentation.main

import com.thunderdogge.qread.presentation.Screens
import com.thunderdogge.qread.presentation.base.BaseViewModel
import ru.terrakok.cicerone.Router
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val router: Router
) : BaseViewModel() {
    init {
        initNavigation()
    }

    fun navigateBack() {
        router.exit()
    }

    private fun initNavigation() {
        router.newRootScreen(Screens.Scan)
    }
}