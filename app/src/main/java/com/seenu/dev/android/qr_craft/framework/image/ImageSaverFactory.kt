package com.seenu.dev.android.qr_craft.framework.image

import android.os.Build

object ImageSaverFactory {
    fun getSaver(): ImageSaver {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStoreImageSaver
        } else {
            LegacyImageSaver
        }
    }
}