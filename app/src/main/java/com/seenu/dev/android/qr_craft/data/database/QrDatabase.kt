package com.seenu.dev.android.qr_craft.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.seenu.dev.android.qr_craft.data.dao.QrDataDao
import com.seenu.dev.android.qr_craft.data.entity.QrDataEntity

@Database(
    entities = [QrDataEntity::class],
    version = 1,
    exportSchema = false
)
abstract class QrDatabase : RoomDatabase() {

    abstract val qrDataDao: QrDataDao

}