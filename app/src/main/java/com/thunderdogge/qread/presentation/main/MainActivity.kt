package com.thunderdogge.qread.presentation.main

import android.os.Bundle
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.thunderdogge.qread.R
import com.thunderdogge.qread.databinding.ActivityMainBinding
import com.thunderdogge.qread.extensions.lazily
import com.thunderdogge.qread.presentation.base.BaseActivity
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.android.support.SupportAppNavigator
import toothpick.ktp.delegate.inject

class MainActivity : BaseActivity() {

    private val viewModel by viewModel<MainViewModel>()

    private val navigator by lazily {
        SupportAppNavigator(this, R.id.flContent)
    }

    private val navigationHolder by inject<NavigatorHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.vm = viewModel
    }

    override fun onResumeFragments() {
        super.onResumeFragments()

        navigationHolder.setNavigator(navigator)
    }

    override fun onPause() {
        super.onPause()

        navigationHolder.removeNavigator()
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