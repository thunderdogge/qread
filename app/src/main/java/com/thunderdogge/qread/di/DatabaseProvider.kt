package com.thunderdogge.qread.di

import android.app.Application
import androidx.room.Room
import com.thunderdogge.qread.repository.Database
import javax.inject.Inject
import javax.inject.Provider

class DatabaseProvider @Inject constructor(private val application: Application) : Provider<Database> {
    override fun get(): Database {
        return Room.databaseBuilder(application, Database::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}
