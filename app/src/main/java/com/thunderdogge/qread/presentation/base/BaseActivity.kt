package com.thunderdogge.qread.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import toothpick.ktp.KTP
import toothpick.smoothie.lifecycle.closeOnDestroy

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openRootScope().openSubScope(this).closeOnDestroy(this).inject(this)
    }

    inline fun <reified T : ViewModel> viewModel(): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) {
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val scope = KTP.openRootScope().openSubScope(this@BaseActivity)
                    return scope.getInstance(modelClass)
                }
            }

            ViewModelProvider(this, factory).get(T::class.java)
        }
    }
}