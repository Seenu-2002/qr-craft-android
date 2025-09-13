package com.seenu.dev.android.qr_craft.presentation.scanner

import android.R.attr.data
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.domain.repository.QrRepository
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import com.seenu.dev.android.qr_craft.presentation.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

class QrScannerViewModel constructor(
    private val qrRepository: QrRepository
) : ViewModel(), KoinComponent {

    private val _qrData: MutableSharedFlow<UiState<QrData>> = MutableSharedFlow()
    val qrData: SharedFlow<UiState<QrData>> = _qrData.asSharedFlow()

    // Use Built-in functions to get the data.
    @OptIn(ExperimentalTime::class)
    fun parseQrData(rawValue: String) {
        viewModelScope.launch {
            _qrData.emit(UiState.Loading())
            delay(500L) // Intentional delay
            val now = Clock.System.now()
            val qrData = QrData(
                id = 0L,
                customTitle = null,
                createdAt = now,
                lastUpdatedAt = now,
                isScanned = true,
                data = rawValue,
            )
            val id = qrRepository.insertQrData(qrData)
            _qrData.emit(UiState.Success(qrData.copy(id = id)))
        }
    }

}