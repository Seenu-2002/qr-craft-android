package com.seenu.dev.android.qr_craft.data.mapper

import com.seenu.dev.android.qr_craft.data.entity.QrDataEntity
import com.seenu.dev.android.qr_craft.domain.model.QrData
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun QrDataEntity.toDomain(): QrData {
    return QrData(
        id = this.id,
        customTitle = this.title,
        data = this.data,
        isFavourite = isFavourite,
        isScanned = this.isScanned,
        createdAt = Instant.fromEpochMilliseconds(this.createdAt),
        lastUpdatedAt = Instant.fromEpochMilliseconds(this.lastUpdatedAt)
    )
}

@OptIn(ExperimentalTime::class)
fun QrData.toEntity(): QrDataEntity {
    return QrDataEntity(
        id = this.id,
        title = this.customTitle,
        data = this.data,
        isFavourite = this.isFavourite,
        isScanned = this.isScanned,
        createdAt = this.createdAt.toEpochMilliseconds(),
        lastUpdatedAt = this.lastUpdatedAt.toEpochMilliseconds()
    )
}