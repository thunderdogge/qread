package com.thunderdogge.qread.repository.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "History")
data class History(
    @PrimaryKey
    val id: String,
    val date: LocalDateTime,
    val format: String,
    val value: String
)
