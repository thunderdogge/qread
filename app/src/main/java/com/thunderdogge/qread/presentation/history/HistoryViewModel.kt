package com.thunderdogge.qread.presentation.history

import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableBoolean
import com.thunderdogge.qread.R
import com.thunderdogge.qread.extensions.DateFormat
import com.thunderdogge.qread.extensions.observeOnUi
import com.thunderdogge.qread.interactor.ClipboardInteractor
import com.thunderdogge.qread.interactor.ResourceProvider
import com.thunderdogge.qread.interactor.ScanInteractor
import com.thunderdogge.qread.presentation.base.BaseViewModel
import com.thunderdogge.qread.presentation.common.DateTimeFormatter
import com.thunderdogge.qread.presentation.common.DialogLiveEvent
import com.thunderdogge.qread.presentation.common.SingleLiveEvent
import com.thunderdogge.qread.repository.model.History
import org.threeten.bp.LocalDate
import timber.log.Timber
import javax.inject.Inject

class HistoryViewModel @Inject constructor(
    private val scanInteractor: ScanInteractor,
    private val resourceProvider: ResourceProvider,
    private val dateTimeFormatter: DateTimeFormatter,
    private val clipboardInteractor: ClipboardInteractor
) : BaseViewModel() {

    val isLoading = ObservableBoolean()

    val entities = ObservableArrayList<HistoryEntityViewModel>()

    val snackbarMessage = SingleLiveEvent<String>()

    val clearHistoryPromptDialog = DialogLiveEvent()

    init {
        loadHistory()
    }

    fun clearHistory() {
        scanInteractor.clearScanHistory()
            .observeOnUi()
            .subscribe({ entities.clear() }, { Timber.e(it) })
            .disposeLater()
    }

    fun selectHistoryItem(item: HistoryEntityViewModel.Item) {
        val message = resourceProvider.getString(R.string.scan_result_copied)
        clipboardInteractor.copyValue(item.value)
        snackbarMessage.value = message
    }

    fun promptClearHistory() {
        clearHistoryPromptDialog.show()
    }

    private fun loadHistory() {
        scanInteractor.getScanHistory()
            .map(::createViewModels)
            .observeOnUi()
            .doOnSubscribe { isLoading.set(true) }
            .doAfterTerminate { isLoading.set(false) }
            .subscribe({ entities.addAll(it) }, { handleLoadHistoryFailed(it) })
            .disposeLater()
    }

    private fun handleLoadHistoryFailed(throwable: Throwable) {
        Timber.e(throwable)

        val message = resourceProvider.getString(R.string.history_load_error)
        snackbarMessage.value = message
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