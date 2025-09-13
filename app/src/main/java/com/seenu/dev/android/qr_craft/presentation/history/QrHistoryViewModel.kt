package com.seenu.dev.android.qr_craft.presentation.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.domain.repository.QrRepository
import com.seenu.dev.android.qr_craft.presentation.UiState
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import timber.log.Timber

class QrHistoryViewModel constructor(
    private val qrRepository: QrRepository
) : ViewModel(), KoinComponent {

    private val _selectedOption: MutableStateFlow<HistoryType> =
        MutableStateFlow(HistoryType.SCANNED)
    val selectedOption: StateFlow<HistoryType> = _selectedOption.asStateFlow()

    private val _qrHistoryData: MutableStateFlow<UiState<List<QrData>>> =
        MutableStateFlow(UiState.Empty())
    val qrHistoryData: StateFlow<UiState<List<QrData>>> = _qrHistoryData.asStateFlow()

    private val _itemLongPressed: MutableStateFlow<QrDataUiModel?> = MutableStateFlow(null)
    val itemLongPressed: StateFlow<QrDataUiModel?> = _itemLongPressed.asStateFlow()

    private val _deleteStatus: MutableStateFlow<UiState<Unit>> = MutableStateFlow(UiState.Empty())
    val deleteStatus: StateFlow<UiState<Unit>> = _deleteStatus.asStateFlow()

    fun selectOption(option: HistoryType) {
        if (_selectedOption.value != option || qrHistoryData.value is UiState.Empty) {
            _selectedOption.value = option
            getScannedHistory(option)
        }
    }

    private fun getScannedHistory(type: HistoryType) {
        viewModelScope.launch {
            _qrHistoryData.value = UiState.Loading()
            try {
                qrRepository.getAllQrData(type == HistoryType.SCANNED).collectLatest {
                    _qrHistoryData.value = UiState.Success(it)
                }
            } catch (exp: Exception) {
                Timber.e(exp, "Error fetching scanned history")
                _qrHistoryData.value = UiState.Error(exp.message)
            }
        }
    }

    fun onItemLongPressed(item: QrDataUiModel?) {
        _itemLongPressed.value = item
    }

    fun delete(item: QrDataUiModel) {
        viewModelScope.launch {
            _deleteStatus.value = UiState.Loading()
            try {
                qrRepository.deleteQrData(item.id)
                _deleteStatus.value = UiState.Success(Unit)
            } catch (exp: Exception) {
                Timber.e(exp, "Error deleting item with id: ${item.id}")
                _deleteStatus.value = UiState.Error(exp.message)
            }
        }
    }

    fun clearDeleteStatus() {
        _deleteStatus.value = UiState.Empty()
    }

    fun clearItemLongPressed() {
        _itemLongPressed.value = null
    }

}