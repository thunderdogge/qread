package com.thunderdogge.qread.interactor

import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

class DateTimeProvider @Inject constructor() {
    fun getCurrentDate(): LocalDate {
        return LocalDate.now()
    }

    fun getCurrentDateTime(): LocalDateTime {
        return LocalDateTime.now()
    }
}
