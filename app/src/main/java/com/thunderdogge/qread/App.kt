package com.thunderdogge.qread

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import com.thunderdogge.qread.di.AppModule
import timber.log.Timber
import toothpick.configuration.Configuration
import toothpick.ktp.KTP
import toothpick.smoothie.module.SmoothieApplicationModule

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        initDi()
        initTime()
        initLogger()
    }

    private fun initDi() {
        if (BuildConfig.DEBUG) {
            KTP.setConfiguration(Configuration.forDevelopment().preventMultipleRootScopes())
        } else {
            KTP.setConfiguration(Configuration.forProduction())
        }

        val scope = KTP.openRootScope()
        scope.installModules(SmoothieApplicationModule(this), AppModule)
    }

    private fun initTime() {
        AndroidThreeTen.init(this)
    }

    private fun initLogger() {
        Timber.plant(Timber.DebugTree())
    }
}
