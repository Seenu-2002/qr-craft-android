package com.seenu.dev.android.qr_craft.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class QrData @OptIn(ExperimentalTime::class) constructor(
    val id: Long,
    val customTitle: String?,
    val data: String,
    val isFavourite: Boolean,
    val isScanned: Boolean,
    val createdAt: Instant,
    val lastUpdatedAt: Instant
)