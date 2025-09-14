package com.seenu.dev.android.qr_craft.domain.repository

import com.seenu.dev.android.qr_craft.domain.model.QrData
import kotlinx.coroutines.flow.Flow

interface QrRepository {

    suspend fun getAllQrData(isScanned: Boolean): Flow<List<QrData>>
    suspend fun getQrData(id: Long): QrData?

    suspend fun insertQrData(qrData: QrData): Long

    suspend fun updateQrData(qrData: QrData)

    suspend fun updateQrTitle(title: String, id: Long)

    suspend fun deleteQrData(id: Long)

}