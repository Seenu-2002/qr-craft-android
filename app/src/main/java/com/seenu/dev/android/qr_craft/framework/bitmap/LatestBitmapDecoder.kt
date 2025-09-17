package com.seenu.dev.android.qr_craft.framework.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi

class LatestBitmapDecoder : BitmapDecoder {
    @RequiresApi(Build.VERSION_CODES.P)
    override fun decode(context: Context, uri: Uri): Bitmap {
        val source = ImageDecoder.createSource(context.contentResolver, uri)
        return ImageDecoder.decodeBitmap(source)
    }
}