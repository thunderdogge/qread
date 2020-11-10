package com.thunderdogge.qread.presentation.history

import androidx.lifecycle.MutableLiveData
import com.thunderdogge.messaggio.Messenger
import com.thunderdogge.qread.R
import com.thunderdogge.qread.extensions.DateFormat
import com.thunderdogge.qread.extensions.observeOnUi
import com.thunderdogge.qread.interactor.ClipboardInteractor
import com.thunderdogge.qread.interactor.ScanInteractor
import com.thunderdogge.qread.presentation.base.BaseViewModel
import com.thunderdogge.qread.presentation.common.DateTimeFormatter
import com.thunderdogge.qread.presentation.common.DialogLiveEvent
import com.thunderdogge.qread.repository.model.History
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val messenger: Messenger,
    private val scanInteractor: ScanInteractor,
    private val dateTimeFormatter: DateTimeFormatter,
    private val clipboardInteractor: ClipboardInteractor
) : BaseViewModel() {

    val isLoading = MutableLiveData<Boolean>()

    val entities = MutableLiveData<List<HistoryEntityViewModel>>()

    val clearHistoryPromptDialog = DialogLiveEvent()

    init {
        loadHistory()
    }

    fun onHistoryClearClick() {
        scanInteractor.clearScanHistory()
            .observeOnUi()
            .subscribe({ entities.value = emptyList() }, { Timber.e(it) })
            .disposeLater()
    }

    fun onHistoryItemClick(item: HistoryEntityViewModel.Item) {
        clipboardInteractor.copyValue(item.value)
        messenger.showSnackbar(R.string.scan_result_copied)
    }

    fun promptClearHistory() {
        clearHistoryPromptDialog.show()
    }

    private fun loadHistory() {
        scanInteractor.getScanHistory()
            .map { createViewModels(it) }
            .observeOnUi()
            .doOnSubscribe { isLoading.value = true }
            .doAfterTerminate { isLoading.value = false }
            .subscribe(
                { entities.value = it },
                { handleLoadHistoryFailed(it) }
            )
            .disposeLater()
    }

    private fun handleLoadHistoryFailed(throwable: Throwable) {
        Timber.e(throwable)
        messenger.showSnackbar(R.string.history_load_error)
    }

    private fun createViewModels(source: List<History>): List<HistoryEntityViewModel> {
        return source.asSequence()
            .sortedByDescending { it.date }
            .groupBy { it.date.toLocalDate() }
            .flatMap { (date, items) ->
                val groupViewModels = createGroupViewModels(date)
                val itemViewModels = createItemViewModels(items)
                groupViewModels + itemViewModels
            }
    }

    private fun createGroupViewModels(date: LocalDate): List<HistoryEntityViewModel.Group> {
        val dateFormatted = dateTimeFormatter.formatRelative(date, DateFormat.D_MMMM, DateFormat.D_MMMM_YYYY)
        return listOf(HistoryEntityViewModel.Group(dateFormatted))
    }

    private fun createItemViewModels(items: List<History>): List<HistoryEntityViewModel.Item> {
        return items
            .asSequence()
            .distinctBy { it.value }
            .map { HistoryEntityViewModel.Item(it.value) }
            .toList()
    }
}
