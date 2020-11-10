package com.thunderdogge.qread.presentation.history

import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.thunderdogge.qread.R
import com.thunderdogge.qread.presentation.extensions.inflate

class HistoryGroupAdapterDelegate : AbsListItemAdapterDelegate<HistoryEntityViewModel.Group, HistoryEntityViewModel, HistoryGroupAdapterDelegate.GroupViewHolder>() {
    override fun isForViewType(item: HistoryEntityViewModel, items: MutableList<HistoryEntityViewModel>, position: Int): Boolean {
        return item is HistoryEntityViewModel.Group
    }

    override fun onCreateViewHolder(parent: ViewGroup): GroupViewHolder {
        val view = parent.inflate(R.layout.vh_item_history_group)
        return GroupViewHolder(view as TextView)
    }

    override fun onBindViewHolder(item: HistoryEntityViewModel.Group, holder: GroupViewHolder, payloads: MutableList<Any>) {
        holder.view.text = item.date
    }

    class GroupViewHolder(val view: TextView) : RecyclerView.ViewHolder(view)
}
