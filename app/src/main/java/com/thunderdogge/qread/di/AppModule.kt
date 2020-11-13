package com.thunderdogge.qread.di

import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.NavigatorHolder
import com.github.terrakok.cicerone.Router
import com.thunderdogge.messaggio.MessageDispatcher
import com.thunderdogge.messaggio.Messaggio
import com.thunderdogge.messaggio.Messenger
import com.thunderdogge.qread.interactor.ClipboardInteractor
import com.thunderdogge.qread.interactor.DateTimeProvider
import com.thunderdogge.qread.interactor.ResourceProvider
import com.thunderdogge.qread.interactor.ScanInteractor
import com.thunderdogge.qread.presentation.common.DateTimeFormatter
import com.thunderdogge.qread.repository.Database
import com.thunderdogge.qread.repository.HistoryRepository
import toothpick.config.Module

object AppModule : Module() {
    init {
        val cicerone = Cicerone.create()
        bind(Router::class.java).toInstance(cicerone.router)
        bind(NavigatorHolder::class.java).toInstance(cicerone.getNavigatorHolder())

        val messaggio = Messaggio.create()
        bind(Messenger::class.java).toInstance(messaggio.messenger)
        bind(MessageDispatcher::class.java).toInstance(messaggio.dispatcher)

        bind(ResourceProvider::class.java).singleton()
        bind(DateTimeProvider::class.java).singleton()
        bind(DateTimeFormatter::class.java).singleton()

        bind(Database::class.java).toProvider(DatabaseProvider::class.java).providesSingleton()
        bind(HistoryRepository::class.java).singleton()

        bind(ScanInteractor::class.java).singleton()
        bind(ClipboardInteractor::class.java).singleton()
    }
}
