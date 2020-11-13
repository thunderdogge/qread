package com.thunderdogge.qread.presentation.main

import android.os.Bundle
import android.view.MenuItem
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.androidx.AppNavigator
import com.thunderdogge.messaggio.MessageDispatcher
import com.thunderdogge.messaggio.MessageReceiver
import com.thunderdogge.qread.R
import com.thunderdogge.qread.presentation.base.BaseActivity
import toothpick.ktp.delegate.inject

class MainActivity : BaseActivity(R.layout.activity_main) {

    private val viewModel by viewModel<MainViewModel>()

    private val navigator = AppNavigator(this, R.id.flContent)

    private val navigationHolder by inject<NavigatorHolder>()

    private val messageReceiver = MessageReceiver(this)

    private val messageDispatcher by inject<MessageDispatcher>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            viewModel.onStart()
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        navigationHolder.setNavigator(navigator)
        messageDispatcher.attachReceiver(messageReceiver)
    }

    override fun onPause() {
        super.onPause()

        navigationHolder.removeNavigator()
        messageDispatcher.detachReceiver()
    }

    override fun onBackPressed() {
        viewModel.navigateBack()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            viewModel.navigateBack()

            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
