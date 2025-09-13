package com.seenu.dev.android.qr_craft.di

import androidx.room.Room
import com.seenu.dev.android.qr_craft.data.dao.QrDataDao
import com.seenu.dev.android.qr_craft.data.database.QrDatabase
import com.seenu.dev.android.qr_craft.data.repository.QrRepositoryImpl
import com.seenu.dev.android.qr_craft.domain.repository.QrRepository
import com.seenu.dev.android.qr_craft.presentation.create.CreateQrViewModel
import com.seenu.dev.android.qr_craft.presentation.history.QrHistoryViewModel
import com.seenu.dev.android.qr_craft.presentation.scan_details.QrDetailViewModel
import com.seenu.dev.android.qr_craft.presentation.scanner.QrScannerViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.jvm.java
import kotlin.math.sin

val appModules = module {
    viewModel {
        QrScannerViewModel(get())
    }
    viewModel {
        CreateQrViewModel(get())
    }
    viewModel {
        QrHistoryViewModel(get())
    }
    viewModel {
        QrDetailViewModel(get())
    }

    single<QrRepository> {
        QrRepositoryImpl()
    }
    single<QrDatabase> {
        val context = androidContext()
        Room.databaseBuilder(
            context,
            QrDatabase::class.java,
            "qr_craft_db"
        ).build()
    }
    single<QrDataDao> {
        val database: QrDatabase = get()
        database.qrDataDao
    }
}