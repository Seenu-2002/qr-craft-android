package com.seenu.dev.android.qr_craft.presentation.scan_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.domain.repository.QrRepository
import com.seenu.dev.android.qr_craft.presentation.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class QrDetailViewModel constructor(
    private val qrRepository: QrRepository
) : ViewModel(), KoinComponent {

    private val _qrData: MutableStateFlow<UiState<QrData>> = MutableStateFlow(UiState.Empty())
    val qrData: StateFlow<UiState<QrData>> = _qrData.asStateFlow()

    fun getQrData(id: Long) {
        viewModelScope.launch {
            _qrData.value = UiState.Loading()
            try {
                qrRepository.getQrDataAsFlow(id).collectLatest { data ->
                    if (data == null) {
                        _qrData.value = UiState.Empty("INVALID_ID")
                    } else {
                        _qrData.value = UiState.Success(data)
                    }
                }
            } catch (e: Exception) {
                _qrData.value = UiState.Error(e)
            }

        }
    }

    fun updateTitle(id: Long, title: String) {
        viewModelScope.launch {
            qrRepository.updateQrTitle(title, id)
        }
    }

    fun updateFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            qrRepository.updateQrFavourite(isFavorite, id)
        }
    }

}