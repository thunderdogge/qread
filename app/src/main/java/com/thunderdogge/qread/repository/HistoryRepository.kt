package com.thunderdogge.qread.repository

import com.thunderdogge.qread.repository.model.History
import com.thunderdogge.qread.extensions.subscribeOnIo
import io.reactivex.Completable
import io.reactivex.Single
import org.threeten.bp.LocalDateTime
import java.util.UUID
import javax.inject.Inject

class HistoryRepository @Inject constructor(
    private val database: Database
) {
    fun getHistory(): Single<List<History>> {
        return database.historyDao.getAll().subscribeOnIo()
    }

    fun addHistoryItem(format: String, value: String, date: LocalDateTime): Completable {
        return Completable.fromCallable {
            val id = UUID.randomUUID().toString()
            val entity = History(id, date, format, value)
            database.historyDao.write(entity)
        }.subscribeOnIo()
    }

    fun clearHistory(): Completable {
        return Completable.fromCallable {
            database.historyDao.deleteAll()
        }.subscribeOnIo()
    }
}
