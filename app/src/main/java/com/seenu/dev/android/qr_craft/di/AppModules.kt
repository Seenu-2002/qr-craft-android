package com.seenu.dev.android.qr_craft.di

import com.seenu.dev.android.qr_craft.presentation.common.QrDetailsViewModel
import com.seenu.dev.android.qr_craft.presentation.scanner.QrScannerViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModules = module {
    viewModel {
        QrScannerViewModel()
    }
    viewModel {
        QrDetailsViewModel()
    }
}