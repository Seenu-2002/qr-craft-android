package com.seenu.dev.android.qr_craft.presentation.route

import com.seenu.dev.android.qr_craft.presentation.state.QrData
import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Scanner : Screen

    @Serializable
    data object ScanDetails : Screen

}