package com.seenu.dev.android.qr_craft.presentation.common

import androidx.lifecycle.ViewModel
import com.seenu.dev.android.qr_craft.presentation.state.QrData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent

class QrDetailsViewModel : ViewModel(), KoinComponent {

    private val _qrData: MutableStateFlow<QrData?> = MutableStateFlow(null)
    val qrData: StateFlow<QrData?> = _qrData.asStateFlow()

    fun setQrData(data: QrData) {
        _qrData.value = data
    }

}