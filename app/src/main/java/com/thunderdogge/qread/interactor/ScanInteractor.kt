package com.thunderdogge.qread.interactor

import com.google.android.gms.vision.barcode.Barcode
import com.thunderdogge.qread.entity.ScanResult
import com.thunderdogge.qread.entity.ScanResultFormat
import com.thunderdogge.qread.entity.ScanResultValueFormat
import com.thunderdogge.qread.repository.HistoryRepository
import com.thunderdogge.qread.repository.model.History
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ScanInteractor @Inject constructor(
    private val dateTimeProvider: DateTimeProvider,
    private val historyRepository: HistoryRepository
) {
    fun getScanHistory(): Single<List<History>> {
        return historyRepository.getHistory()
    }

    fun clearScanHistory(): Completable {
        return historyRepository.clearHistory()
    }

    fun saveScanResult(barcode: Barcode): Single<ScanResult> {
        val date = dateTimeProvider.getCurrentDateTime()
        val result = createScanResult(barcode)
        return historyRepository.addHistoryItem(result.format.toString(), result.value, date).toSingleDefault(result)
    }

    private fun createScanResult(barcode: Barcode): ScanResult {
        val format = when (barcode.format) {
            Barcode.EAN_13 -> ScanResultFormat.Ean13
            Barcode.EAN_8 -> ScanResultFormat.Ean8
            Barcode.QR_CODE -> ScanResultFormat.QR
            Barcode.UPC_A -> ScanResultFormat.UpcA
            Barcode.UPC_E -> ScanResultFormat.UpcE
            Barcode.PDF417 -> ScanResultFormat.Pdf417
            else -> ScanResultFormat.Other
        }
        val valueFormat = when (barcode.valueFormat) {
            Barcode.TEXT -> ScanResultValueFormat.Text
            Barcode.URL -> ScanResultValueFormat.URL
            Barcode.EMAIL -> ScanResultValueFormat.Email
            Barcode.PHONE -> ScanResultValueFormat.Phone
            Barcode.PRODUCT -> ScanResultValueFormat.Product
            Barcode.CONTACT_INFO -> ScanResultValueFormat.ContactInfo
            else -> ScanResultValueFormat.Other
        }

        return ScanResult(barcode.rawValue, format, valueFormat)
    }
}
