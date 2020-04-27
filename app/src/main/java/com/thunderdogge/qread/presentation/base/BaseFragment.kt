package com.thunderdogge.qread.presentation.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import toothpick.config.Module
import toothpick.ktp.KTP
import toothpick.smoothie.lifecycle.closeOnDestroy

abstract class BaseFragment : Fragment() {

    protected fun setSupportActionBar(toolbar: Toolbar) {
        (activity as? AppCompatActivity)?.setSupportActionBar(toolbar)
    }

    protected fun setDisplayHomeAsUpEnabled() {
        (activity as? AppCompatActivity)?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        KTP.openRootScope().openSubScope(this).closeOnDestroy(this).inject(this)
    }

    inline fun <reified T : ViewModel> viewModel(noinline bindings: ((Module) -> Unit)? = null): Lazy<T> {
        return lazy(LazyThreadSafetyMode.NONE) {
            val factory = object : ViewModelProvider.Factory {
                override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                    val scope = KTP.openRootScope().openSubScope(this@BaseFragment)
                    if (bindings != null) {
                        scope.installModules(Module().also(bindings))
                    }

                    return scope.getInstance(modelClass)
                }
            }

            ViewModelProvider(this, factory).get(T::class.java)
        }
    }
}