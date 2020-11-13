package com.thunderdogge.qread.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.thunderdogge.qread.repository.model.History
import com.thunderdogge.qread.repository.dao.HistoryDao

@Database(entities = [History::class], version = 1, exportSchema = false)
@TypeConverters(DateConverter::class)
abstract class Database : RoomDatabase() {
    abstract val historyDao: HistoryDao
}
