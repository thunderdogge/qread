package com.thunderdogge.qread.presentation.scan

import androidx.lifecycle.MutableLiveData
import com.github.terrakok.cicerone.Router
import com.google.android.gms.vision.barcode.Barcode
import com.thunderdogge.messaggio.Messenger
import com.thunderdogge.qread.R
import com.thunderdogge.qread.extensions.observeOnUi
import com.thunderdogge.qread.interactor.ClipboardInteractor
import com.thunderdogge.qread.interactor.ScanInteractor
import com.thunderdogge.qread.presentation.Screens
import com.thunderdogge.qread.presentation.base.BaseViewModel
import com.thunderdogge.qread.presentation.common.DialogLiveEvent
import com.thunderdogge.qread.presentation.common.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScanViewModel @Inject constructor(
    private val router: Router,
    private val messenger: Messenger,
    private val scanInteractor: ScanInteractor,
    private val clipboardInteractor: ClipboardInteractor
) : BaseViewModel() {

    val isFlashOn = SingleLiveEvent<Boolean>()

    val isScanSucceed = SingleLiveEvent<Boolean>()

    val scanResultFormat = MutableLiveData<String>()

    val scanResultValue = MutableLiveData<String>()

    val isScanResultActive = MutableLiveData<Boolean>()

    val isAutoFocusForced = MutableLiveData<Boolean>()

    val cameraStartFailureDialog = DialogLiveEvent()

    val cameraPermissionDeniedDialog = DialogLiveEvent()

    private val scannerBarcodeSubject = PublishSubject.create<Barcode>()

    init {
        initState()
        initScannerObserver()
        initAutofocusObserver()
    }

    fun onFlashClick() {
        val isOn = isFlashOn.value?.not() ?: true
        isFlashOn.value = isOn
    }

    fun onResultCopyClick() {
        val format = scanResultFormat.value.orEmpty()
        val value = scanResultValue.value.orEmpty()
        clipboardInteractor.copyValue(value, format)

        messenger.showSnackbar(R.string.scan_result_copied)
    }

    fun onResultCloseClick() {
        isScanResultActive.value = false
    }

    fun onHistoryClick() {
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

    private fun initState() {
        isScanResultActive.value = false
    }

    private fun initScannerObserver() {
        scannerBarcodeSubject
            .throttleFirst(1, TimeUnit.SECONDS)
            .flatMapSingle { scanInteractor.saveScanResult(it) }
            .observeOnUi()
            .doOnNext { isScanSucceed.value = true }
            .subscribe(
                {
                    scanResultValue.value = it.value
                    scanResultFormat.value = it.format.toString()
                    isScanResultActive.value = true
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
