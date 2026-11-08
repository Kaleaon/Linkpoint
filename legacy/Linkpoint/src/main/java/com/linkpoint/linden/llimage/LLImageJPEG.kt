package com.linkpoint.linden.llimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

object ImageRawJPEG {
    fun encode(image: ImageRaw, quality: Float = 0.75f): ByteArray {
        val src = if (image.components == 4) image.convertToType(3) else image
        val bitmap = imageRawToBitmap(src)
        val out = ByteArrayOutputStream()
        val q = (quality.coerceIn(0f, 1f) * 100f).toInt().coerceIn(1, 100)
        bitmap.compress(Bitmap.CompressFormat.JPEG, q, out)
        return out.toByteArray()
    }

    fun decode(data: ByteArray): ImageRaw? = runCatching {
        BitmapFactory.decodeByteArray(data, 0, data.size)?.let { bitmapToImageRaw(it) }
    }.getOrNull()
}

internal fun bitmapToImageRaw(bitmap: Bitmap): ImageRaw {
    val width = bitmap.width
    val height = bitmap.height
    val components = if (bitmap.hasAlpha()) 4 else 3
    val pixels = IntArray(width * height)
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
    val raw = ByteArray(width * height * components)
    var idx = 0
    for (pixel in pixels) {
        raw[idx++] = ((pixel shr 16) and 0xFF).toByte()
        raw[idx++] = ((pixel shr 8) and 0xFF).toByte()
        raw[idx++] = (pixel and 0xFF).toByte()
        if (components == 4) raw[idx++] = ((pixel shr 24) and 0xFF).toByte()
    }
    return ImageRaw(width, height, components, raw)
}

internal fun imageRawToBitmap(image: ImageRaw): Bitmap {
    val width = image.width
    val height = image.height
    val pixels = IntArray(width * height)
    var idx = 0
    for (i in pixels.indices) {
        val r = image.data.getOrElse(idx) { 0 }.toInt() and 0xFF
        val g = image.data.getOrElse(idx + 1) { 0 }.toInt() and 0xFF
        val b = image.data.getOrElse(idx + 2) { 0 }.toInt() and 0xFF
        val a = if (image.components == 4) image.data.getOrElse(idx + 3) { -1 }.toInt() and 0xFF else 0xFF
        pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        idx += image.components
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}
