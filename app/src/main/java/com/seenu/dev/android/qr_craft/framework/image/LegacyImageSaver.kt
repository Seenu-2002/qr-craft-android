package com.seenu.dev.android.qr_craft.framework.image

import android.content.Context
import android.graphics.Bitmap

object LegacyImageSaver : ImageSaver {
    override fun save(
        context: Context,
        image: Bitmap,
        name: String
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}