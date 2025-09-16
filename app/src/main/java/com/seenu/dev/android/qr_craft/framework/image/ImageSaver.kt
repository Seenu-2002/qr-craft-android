package com.seenu.dev.android.qr_craft.framework.image

import android.content.Context
import android.graphics.Bitmap

interface ImageSaver {
    fun save(context: Context, image: Bitmap, name: String): Result<Unit>
}