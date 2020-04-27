package com.thunderdogge.qread.presentation.common

import com.thunderdogge.qread.R
import com.thunderdogge.qread.extensions.DateFormat
import com.thunderdogge.qread.extensions.format
import com.thunderdogge.qread.interactor.DateTimeProvider
import com.thunderdogge.qread.interactor.ResourceProvider
import org.threeten.bp.LocalDate
import java.util.Locale
import javax.inject.Inject

class DateTimeFormatter @Inject constructor(
    private val dateTimeProvider: DateTimeProvider,
    private val resourceProvider: ResourceProvider
) {
    fun formatRelative(source: LocalDate, shortFormat: DateFormat, fullFormat: DateFormat): String {
        val currentDate = dateTimeProvider.getCurrentDate()
        if (source == currentDate) {
            return resourceProvider.getString(R.string.date_relative_today)
        }

        val targetFormat = if (source.year == currentDate.year) shortFormat else fullFormat
        return source.format(targetFormat).toLowerCase(Locale.getDefault())
    }
}