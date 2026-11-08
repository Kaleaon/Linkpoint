package com.linkpoint.linden.llimage

import android.graphics.BitmapFactory

object ImageRawBMP {
    fun encode(image: ImageRaw): ByteArray = BmpImage().encode(image)

    fun decode(data: ByteArray): ImageRaw? = runCatching {
        BitmapFactory.decodeByteArray(data, 0, data.size)?.let { bitmapToImageRaw(it) }
    }.getOrNull()
}
