package com.seenu.dev.android.qrscanner.presentation.route

import kotlinx.serialization.Serializable

@Serializable
sealed interface Screen {

    @Serializable
    data object Scanner : Screen

    @Serializable
    data object ScanDetails : Screen

}