package com.seenu.dev.android.qr_craft.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.domain.repository.QrRepository
import com.seenu.dev.android.qr_craft.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class CreateQrViewModel constructor(
    private val qrRepository: QrRepository
) : ViewModel(), KoinComponent {

    private val _qrData: MutableStateFlow<UiState<QrData>> = MutableStateFlow(UiState.Empty())
    val qrData: StateFlow<UiState<QrData>> = _qrData.asStateFlow()

    @OptIn(ExperimentalTime::class)
    fun insertQrData(data: String) {
        viewModelScope.launch {
            _qrData.value = UiState.Loading()
            try {
                val qrData = QrData(
                    id = 0L,
                    customTitle = null,
                    createdAt = Clock.System.now(),
                    lastUpdatedAt = Clock.System.now(),
                    isScanned = false,
                    isFavourite = false,
                    data = data,
                )
                val id = qrRepository.insertQrData(qrData)
                _qrData.value = UiState.Success(qrData.copy(id = id))
            } catch (e: Exception) {
                _qrData.value = UiState.Error(e.message)
            }
        }
    }

}