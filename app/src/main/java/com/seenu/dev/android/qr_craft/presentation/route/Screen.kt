package com.seenu.dev.android.qr_craft.presentation.route

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen constructor(val route: String) {

    @Serializable
    data object Scanner : Screen(route = "scanner")

    @Serializable
    data object ScanDetails : Screen(route = "scan_details")

    @Serializable
    data object ChooseQrType : Screen(route = "choose_qr_type")

    @Serializable
    data object CreateQr : Screen(route = "create_qr")

    @Serializable
    data object QrHistory : Screen(route = "qr_history")

}