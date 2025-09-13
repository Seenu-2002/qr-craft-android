package com.seenu.dev.android.qr_craft.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "qr_data")
data class QrDataEntity constructor(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val title: String?,
    val data: String,
    @ColumnInfo(name = "is_scanned")
    val isScanned: Boolean,
    @ColumnInfo(name = "created_at")
    val createdAt: Long,
    @ColumnInfo(name = "last_updated_at")
    val lastUpdatedAt: Long,
)
