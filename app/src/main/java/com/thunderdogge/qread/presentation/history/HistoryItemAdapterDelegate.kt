package com.thunderdogge.qread.presentation.history

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.thunderdogge.qread.R
import com.thunderdogge.qread.presentation.extensions.inflate

class HistoryItemAdapterDelegate(private val selectHandler: (HistoryEntityViewModel.Item) -> Unit) :
    AbsListItemAdapterDelegate<HistoryEntityViewModel.Item, HistoryEntityViewModel, HistoryItemAdapterDelegate.ItemViewHolder>() {
    override fun isForViewType(item: HistoryEntityViewModel, items: MutableList<HistoryEntityViewModel>, position: Int): Boolean {
        return item is HistoryEntityViewModel.Item
    }

    override fun onCreateViewHolder(parent: ViewGroup): ItemViewHolder {
        val view = parent.inflate(R.layout.vh_item_history_item)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(item: HistoryEntityViewModel.Item, holder: ItemViewHolder, payloads: MutableList<Any>) {
        holder.nameTextView.text = item.value
        holder.itemView.setOnClickListener { selectHandler.invoke(item) }
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.tvName)
    }
}
