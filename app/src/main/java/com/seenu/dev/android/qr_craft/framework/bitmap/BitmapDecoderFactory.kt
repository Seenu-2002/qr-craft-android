package com.seenu.dev.android.qr_craft.framework.bitmap

import android.os.Build

object BitmapDecoderFactory {
    fun getDecoder(): BitmapDecoder {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            LegacyBitmapDecoder
        } else {
            LatestBitmapDecoder()
        }
    }
}