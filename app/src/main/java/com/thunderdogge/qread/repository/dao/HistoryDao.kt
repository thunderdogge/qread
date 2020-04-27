package com.thunderdogge.qread.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.thunderdogge.qread.repository.model.History
import io.reactivex.Single

@Dao
interface HistoryDao {
    @Query("SELECT * FROM History ORDER BY date DESC")
    fun getAll(): Single<List<History>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun write(entity: History)

    @Query("DELETE FROM History")
    fun deleteAll()
}