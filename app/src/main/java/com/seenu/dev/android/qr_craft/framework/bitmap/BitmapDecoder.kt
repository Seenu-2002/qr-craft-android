package com.seenu.dev.android.qr_craft.framework.bitmap

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri

interface BitmapDecoder {
    fun decode(context: Context, uri: Uri): Bitmap
}