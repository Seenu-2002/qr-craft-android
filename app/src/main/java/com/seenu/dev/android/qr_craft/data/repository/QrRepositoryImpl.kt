package com.seenu.dev.android.qr_craft.data.repository

import com.seenu.dev.android.qr_craft.data.dao.QrDataDao
import com.seenu.dev.android.qr_craft.data.entity.QrDataEntity
import com.seenu.dev.android.qr_craft.data.mapper.toDomain
import com.seenu.dev.android.qr_craft.data.mapper.toEntity
import com.seenu.dev.android.qr_craft.domain.model.QrData
import com.seenu.dev.android.qr_craft.domain.repository.QrRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class QrRepositoryImpl constructor() : QrRepository, KoinComponent {

    private val qrDataDao: QrDataDao by inject()

    override suspend fun getAllQrData(isScanned: Boolean): Flow<List<QrData>> {
        return qrDataDao.getAllQrData(isScanned = isScanned).map {
            it.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun getQrData(id: Long): QrData? {
        return qrDataDao.getQrData(id)?.toDomain()
    }

    override suspend fun insertQrData(qrData: QrData): Long {
        return qrDataDao.insertQrData(qrData.toEntity())
    }

    override suspend fun updateQrData(qrData: QrData) {
        qrDataDao.updateQrData(qrData.toEntity())
    }

    override suspend fun updateQrTitle(title: String, id: Long) {
        qrDataDao.updateQrTitle(title, id)
    }

    override suspend fun deleteQrData(id: Long) {
        qrDataDao.deleteQrData(id)
    }


}