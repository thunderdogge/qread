package com.thunderdogge.qread.repository

import androidx.room.TypeConverter
import org.threeten.bp.LocalDateTime
import org.threeten.bp.format.DateTimeFormatter

object DateConverter {
    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    @JvmStatic
    @TypeConverter
    fun toLocalDateTime(source: String?): LocalDateTime? {
        if (source == null) {
            return null
        }

        return formatter.parse(source, LocalDateTime::from)
    }

    @JvmStatic
    @TypeConverter
    fun fromLocalDateTime(source: LocalDateTime?): String? {
        return source?.format(formatter)
    }
}