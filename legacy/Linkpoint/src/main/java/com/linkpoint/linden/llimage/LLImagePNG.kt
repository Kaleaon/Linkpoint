package com.linkpoint.linden.llimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ImageRawPNG {
    fun encode(image: ImageRaw): ByteArray {
        val bitmap = imageRawToBitmap(image)
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        return out.toByteArray()
    }

    fun decode(data: ByteArray): ImageRaw? = runCatching {
        BitmapFactory.decodeByteArray(data, 0, data.size)?.let { bitmapToImageRaw(it) }
    }.getOrNull()
}
