package com.seenu.dev.android.qr_craft.presentation.design_system

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class ScanDetailPage constructor(
    val cardHorizontalPadding: Dp,
    val cardWidth: Dp?
)

data class CreateQrPage constructor(
    val contentHorizontalPadding: Dp,
    val spanCount: Int
)

data class CreateQrFormPage constructor(
    val cardHorizontalPadding: Dp,
    val cardWidth: Dp?
)

data class QrHistoryPage constructor(
    val contentWidth: Dp?,
    val bottomSheetWidth: Dp?
)

data class Dimen constructor(
    val scanDetailPage: ScanDetailPage,
    val createQrPage: CreateQrPage,
    val createQrFormPage: CreateQrFormPage,
    val qrHistoryPage: QrHistoryPage
)

val dimenMobilePortrait = Dimen(
    scanDetailPage = ScanDetailPage(
        cardHorizontalPadding = 8.dp,
        cardWidth = null
    ),
    createQrPage = CreateQrPage(
        contentHorizontalPadding = 8.dp,
        spanCount = 2
    ),
    createQrFormPage = CreateQrFormPage(
        cardHorizontalPadding = 8.dp,
        cardWidth = null
    ),
    qrHistoryPage = QrHistoryPage(
        contentWidth = null,
        bottomSheetWidth = null
    )
)

val dimen600dp = Dimen(
    scanDetailPage = ScanDetailPage(
        cardHorizontalPadding = 8.dp,
        cardWidth = 480.dp
    ),
    createQrPage = CreateQrPage(
        contentHorizontalPadding = 8.dp,
        spanCount = 3
    ),
    createQrFormPage = CreateQrFormPage(
        cardHorizontalPadding = 8.dp,
        cardWidth = 480.dp
    ),
    qrHistoryPage = QrHistoryPage(
        contentWidth = 552.dp,
        bottomSheetWidth = 412.dp
    )
)

val LocalDimen = compositionLocalOf {
    dimenMobilePortrait
}