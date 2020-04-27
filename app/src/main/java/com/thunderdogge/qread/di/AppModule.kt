package com.thunderdogge.qread.di

import com.thunderdogge.qread.interactor.DateTimeProvider
import com.thunderdogge.qread.interactor.ResourceProvider
import com.thunderdogge.qread.interactor.ClipboardInteractor
import com.thunderdogge.qread.interactor.ScanInteractor
import com.thunderdogge.qread.presentation.common.DateTimeFormatter
import com.thunderdogge.qread.repository.Database
import com.thunderdogge.qread.repository.HistoryRepository
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.NavigatorHolder
import ru.terrakok.cicerone.Router
import toothpick.config.Module

object AppModule : Module() {
    init {
        val cicerone = Cicerone.create()
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.navigatorHolder)

        bind(ResourceProvider::class.java).singleton()
        bind(DateTimeProvider::class.java).singleton()
        bind(DateTimeFormatter::class.java).singleton()

        bind(Database::class.java).toProvider(DatabaseProvider::class.java).providesSingleton()
        bind(HistoryRepository::class.java).singleton()

        bind(ScanInteractor::class.java).singleton()
        bind(ClipboardInteractor::class.java).singleton()
    }
}