package com.seenu.dev.android.qr_craft.framework.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore

@Suppress("DEPRECATION")
object LegacyBitmapDecoder : BitmapDecoder {
    override fun decode(context: Context, uri: Uri): Bitmap {
        return MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
    }
}