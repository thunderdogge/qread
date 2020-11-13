package com.thunderdogge.qread.presentation.history

import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class HistoryAdapter(selectHandler: (HistoryEntityViewModel.Item) -> Unit) : ListDelegationAdapter<List<HistoryEntityViewModel>>() {
    init {
        delegatesManager
            .addDelegate(HistoryGroupAdapterDelegate())
            .addDelegate(HistoryItemAdapterDelegate(selectHandler))
    }
}
