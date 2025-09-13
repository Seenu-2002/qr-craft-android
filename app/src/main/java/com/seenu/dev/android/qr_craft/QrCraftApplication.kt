package com.seenu.dev.android.qr_craft

import android.app.Application
import com.seenu.dev.android.qr_craft.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class QrCraftApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        startKoin {
            androidContext(this@QrCraftApplication)
            modules(appModules)
        }
    }

}