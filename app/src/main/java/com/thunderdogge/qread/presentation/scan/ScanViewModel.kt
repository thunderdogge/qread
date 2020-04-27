package com.thunderdogge.qread.presentation.scan

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.vision.barcode.Barcode
import com.thunderdogge.qread.R
import com.thunderdogge.qread.extensions.observeOnUi
import com.thunderdogge.qread.interactor.ClipboardInteractor
import com.thunderdogge.qread.interactor.ResourceProvider
import com.thunderdogge.qread.interactor.ScanInteractor
import com.thunderdogge.qread.presentation.Screens
import com.thunderdogge.qread.presentation.base.BaseViewModel
import com.thunderdogge.qread.presentation.common.DialogLiveEvent
import com.thunderdogge.qread.presentation.common.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import ru.terrakok.cicerone.Router
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScanViewModel @Inject constructor(
    private val router: Router,
    private val scanInteractor: ScanInteractor,
    private val resourceProvider: ResourceProvider,
    private val clipboardInteractor: ClipboardInteractor
) : BaseViewModel() {

    val isFlashOn = SingleLiveEvent<Boolean>()

    val isScanSucceed = SingleLiveEvent<Boolean>()

    val snackbarMessage = SingleLiveEvent<String>()

    val scanResultFormat = ObservableField<String>()

    val scanResultValue = ObservableField<String>()

    val isScanResultActive = ObservableBoolean()

    val isAutoFocusForced = MutableLiveData<Boolean>()

    val cameraStartFailureDialog = DialogLiveEvent()

    val cameraPermissionDeniedDialog = DialogLiveEvent()

    private val scannerBarcodeSubject = PublishSubject.create<Barcode>()

    init {
        initScannerObserver()
        initAutofocusObserver()
    }

    fun toggleFlash() {
        val isOn = isFlashOn.value?.not() ?: true
        isFlashOn.value = isOn
    }

    fun copyScanResult() {
        val format = scanResultFormat.get().orEmpty()
        val value = scanResultValue.get().orEmpty()
        clipboardInteractor.copyValue(value, format)

        snackbarMessage.value = resourceProvider.getString(R.string.scan_result_copied)
    }

    fun closeScanResult() {
        isScanResultActive.set(false)
    }

    fun navigateHistory() {
        router.navigateTo(Screens.History)
    }

    fun navigateAppSettings() {
        router.navigateTo(Screens.ApplicationSettings)
    }

    fun onBarcodeDetection(barcode: Barcode) {
        scannerBarcodeSubject.onNext(barcode)
    }

    fun onCameraStartFailed(error: Throwable) {
        Timber.e(error)
        cameraStartFailureDialog.show()
    }

    fun onCameraPermissionDenied() {
        cameraPermissionDeniedDialog.show()
    }

    private fun initScannerObserver() {
        scannerBarcodeSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .flatMapSingle { scanInteractor.saveScanResult(it) }
            .observeOnUi()
            .doOnNext { isScanSucceed.value = true }
            .subscribe(
                {
                    scanResultValue.set(it.value)
                    scanResultFormat.set(it.format.toString())
                    isScanResultActive.set(true)
                },
                { Timber.e(it, "Object scanning failed") }
            )
            .disposeLater()
    }

    private fun initAutofocusObserver() {
        Observable.interval(AUTO_FOCUS_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
            .subscribe(
                { isAutoFocusForced.postValue(true) },
                { Timber.e(it, "Force camera focus failed") }
            )
            .disposeLater()
    }

    companion object {
        private const val AUTO_FOCUS_INTERVAL_MILLIS = 3_000L
    }
}