package com.seenu.dev.android.qr_craft.presentation.mapper

import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.presentation.state.QrDataUiModel
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private val dateTimeFormatter = LocalDateTime.Format {
    day()
    char(' ')
    monthName(MonthNames.ENGLISH_ABBREVIATED)
    char(' ')
    year()
    char(',')
    hour()
    char(':')
    minute()
}

@OptIn(ExperimentalTime::class)
private fun Instant.formatDateTime(timeZone: TimeZone = TimeZone.currentSystemDefault()): String {
    return dateTimeFormatter.format(this.toLocalDateTime(timeZone))
}

@OptIn(ExperimentalTime::class)
fun QrData.toUiModel(): QrDataUiModel {
    return QrDataUiModel(
        id = id,
        customTitle = customTitle,
        isScanned = isScanned,
        isFavourite = isFavourite,
        createdAtLabel = createdAt.formatDateTime(),
        lastUpdatedAt = lastUpdatedAt.formatDateTime(),
        data = QrUiModelDataParser.parse(qrData = this)
    )
}