package com.seenu.dev.android.qr_craft.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.seenu.dev.android.qr_craft.data.entity.QrDataEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Dao
interface QrDataDao {

    @Insert
    suspend fun insertQrData(qrDataEntity: QrDataEntity): Long

    @Query("SELECT * FROM qr_data ORDER BY created_at DESC")
    fun getAllQrData(): Flow<List<QrDataEntity>>

    @Query("SELECT * FROM qr_data WHERE is_scanned = :isScanned ORDER BY created_at DESC")
    fun getAllQrData(isScanned: Boolean): Flow<List<QrDataEntity>>

    @Query("SELECT * FROM qr_data WHERE id = :id")
    suspend fun getQrData(id: Long): QrDataEntity?

    @Query("SELECT * FROM qr_data WHERE id = :id")
    fun getQrDataAsFlow(id: Long): Flow<QrDataEntity?>

    @Upsert
    suspend fun updateQrData(qrDataDao: QrDataEntity)

    @Query("UPDATE qr_data SET title = :title, last_updated_at = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun updateQrTitle(title: String, id: Long)

    @Query("UPDATE qr_data SET is_favourite = :isFavourite, last_updated_at = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun updateQrFavourite(isFavourite: Boolean, id: Long)

    @Query("DELETE FROM qr_data WHERE id = :id")
    suspend fun deleteQrData(id: Long)
}