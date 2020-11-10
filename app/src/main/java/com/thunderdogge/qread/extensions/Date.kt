package com.thunderdogge.qread.extensions

import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter

enum class DateFormat(val format: DateTimeFormatter) {
    D_MMMM(DateTimeFormatter.ofPattern("d MMMM")),
    D_MMMM_YYYY(DateTimeFormatter.ofPattern("d MMMM yyyy"))
}

fun LocalDate.format(format: DateFormat): String {
    return format.format.format(this)
}
