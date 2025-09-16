package com.seenu.dev.android.qr_craft.framework.image

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi

object MediaStoreImageSaver : ImageSaver {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun save(context: Context, image: Bitmap, name: String): Result<Unit> {
        return try {
            val contentResolver = context.contentResolver
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, name)
                put(MediaStore.Images.Media.IS_PENDING, true)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

            }
            val uri = contentResolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )

            if (uri != null) {

                contentResolver.openOutputStream(uri)?.use { it ->
                    image.compress(Bitmap.CompressFormat.PNG, 100, it)
                } ?: throw Exception("Failed to get output stream.")
                contentValues.put(MediaStore.Images.Media.IS_PENDING, false)
                contentResolver.update(uri, contentValues, null, null)

                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to create new MediaStore record."))
            }

        } catch (exp: Exception) {
            Result.failure(exp)
        }
    }

}