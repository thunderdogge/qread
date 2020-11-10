package com.thunderdogge.qread.presentation.history

sealed class HistoryEntityViewModel {
    data class Group(
        val date: String
    ) : HistoryEntityViewModel()

    data class Item(
        val value: String
    ) : HistoryEntityViewModel()
}
