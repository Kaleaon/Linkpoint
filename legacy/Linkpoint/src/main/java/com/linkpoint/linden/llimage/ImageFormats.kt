package com.linkpoint.linden.llimage

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

interface ImageCodec {
    val extension: String
    fun encode(raw: ImageRaw): ByteArray
    fun decode(data: ByteArray): ImageRaw?
}

private fun Bitmap.toImageRaw(): ImageRaw {
    val components = if (hasAlpha()) 4 else 3
    val pixels = IntArray(width * height)
    getPixels(pixels, 0, width, 0, 0, width, height)
    val result = ByteArray(width * height * components)
    var idx = 0
    for (pixel in pixels) {
        result[idx++] = ((pixel shr 16) and 0xFF).toByte()
        result[idx++] = ((pixel shr 8) and 0xFF).toByte()
        result[idx++] = (pixel and 0xFF).toByte()
        if (components == 4) result[idx++] = ((pixel shr 24) and 0xFF).toByte()
    }
    return ImageRaw(width, height, components, result)
}

private fun ImageRaw.toBitmap(): Bitmap {
    val pixels = IntArray(width * height)
    var idx = 0
    for (i in pixels.indices) {
        val r = data.getOrElse(idx) { 0 }.toInt() and 0xFF
        val g = data.getOrElse(idx + 1) { 0 }.toInt() and 0xFF
        val b = data.getOrElse(idx + 2) { 0 }.toInt() and 0xFF
        val a = if (components == 4) data.getOrElse(idx + 3) { -1 }.toInt() and 0xFF else 0xFF
        pixels[i] = (a shl 24) or (r shl 16) or (g shl 8) or b
        idx += components
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}

class PngImage : ImageCodec {
    override val extension: String = "png"

    override fun encode(raw: ImageRaw): ByteArray {
        val bitmap = raw.toBitmap()
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        return out.toByteArray()
    }

    override fun decode(data: ByteArray): ImageRaw? = runCatching {
        BitmapFactory.decodeByteArray(data, 0, data.size)?.toImageRaw()
    }.getOrNull()
}

class JpegImage(private val quality: Int = 75) : ImageCodec {
    override val extension: String = "jpg"

    override fun encode(raw: ImageRaw): ByteArray {
        val src = if (raw.components == 4) raw.convertToType(3) else raw
        val bitmap = src.toBitmap()
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality.coerceIn(1, 100), out)
        return out.toByteArray()
    }

    override fun decode(data: ByteArray): ImageRaw? = runCatching {
        BitmapFactory.decodeByteArray(data, 0, data.size)?.toImageRaw()
    }.getOrNull()

    fun setEncodeQuality(q: Int): JpegImage = JpegImage(q.coerceIn(1, 100))
}

class BmpImage : ImageCodec {
    override val extension: String = "bmp"

    override fun encode(raw: ImageRaw): ByteArray {
        // Android Bitmap.compress doesn't support BMP; emit a minimal 24-bit BGR BMP manually.
        val src = when (raw.components) {
            4 -> raw.convertToType(3)
            1 -> raw.convertToType(3)
            else -> raw
        }
        val rowStride = ((src.width * 3 + 3) / 4) * 4
        val pixelDataSize = rowStride * src.height
        val fileSize = 54 + pixelDataSize
        val out = ByteArrayOutputStream(fileSize)
        // BMP header (14 bytes)
        out.write(0x42); out.write(0x4D)                                     // "BM"
        out.write(fileSize and 0xFF); out.write((fileSize shr 8) and 0xFF)
        out.write((fileSize shr 16) and 0xFF); out.write((fileSize shr 24) and 0xFF)
        out.write(ByteArray(4))                                              // reserved
        out.write(54); out.write(0); out.write(0); out.write(0)              // pixel data offset
        // DIB header (40 bytes)
        out.write(40); out.write(0); out.write(0); out.write(0)              // header size
        out.write(src.width and 0xFF); out.write((src.width shr 8) and 0xFF)
        out.write((src.width shr 16) and 0xFF); out.write((src.width shr 24) and 0xFF)
        out.write(src.height and 0xFF); out.write((src.height shr 8) and 0xFF)
        out.write((src.height shr 16) and 0xFF); out.write((src.height shr 24) and 0xFF)
        out.write(1); out.write(0)                                           // planes
        out.write(24); out.write(0)                                          // bpp
        out.write(ByteArray(24))                                             // compression + sizes + ppm + colors
        // Pixel data (bottom-up, BGR, padded rows)
        val padding = ByteArray(rowStride - src.width * 3)
        for (row in src.height - 1 downTo 0) {
            for (x in 0 until src.width) {
                val px = (row * src.width + x) * 3
                val r = src.data.getOrElse(px) { 0 }.toInt() and 0xFF
                val g = src.data.getOrElse(px + 1) { 0 }.toInt() and 0xFF
                val b = src.data.getOrElse(px + 2) { 0 }.toInt() and 0xFF
                out.write(b); out.write(g); out.write(r)
            }
            out.write(padding)
        }
        return out.toByteArray()
    }

    override fun decode(data: ByteArray): ImageRaw? = runCatching {
        BitmapFactory.decodeByteArray(data, 0, data.size)?.toImageRaw()
    }.getOrNull()
}

class TgaImage : ImageCodec {
    override val extension: String = "tga"

    override fun encode(raw: ImageRaw): ByteArray {
        val out = ByteArrayOutputStream()
        val hasAlpha = raw.components == 4
        val pixelDepth = if (hasAlpha) 32 else 24

        // TGA header (18 bytes)
        out.write(0)             // ID length
        out.write(0)             // Color map type (none)
        out.write(2)             // Image type: uncompressed true-color
        out.write(ByteArray(5))  // Color map spec (unused)
        out.write(0); out.write(0)   // X origin
        out.write(0); out.write(0)   // Y origin
        out.write(raw.width and 0xFF); out.write((raw.width shr 8) and 0xFF)
        out.write(raw.height and 0xFF); out.write((raw.height shr 8) and 0xFF)
        out.write(pixelDepth)    // Bits per pixel
        out.write(if (hasAlpha) 0x08 else 0x00)  // Attribute bits (alpha depth)

        // TGA stores bottom-to-top by default; flip row order
        val rowSize = raw.width * raw.components
        for (row in raw.height - 1 downTo 0) {
            val base = row * rowSize
            for (x in 0 until raw.width) {
                val px = base + x * raw.components
                val r = raw.data.getOrElse(px) { 0 }.toInt() and 0xFF
                val g = raw.data.getOrElse(px + 1) { 0 }.toInt() and 0xFF
                val b = raw.data.getOrElse(px + 2) { 0 }.toInt() and 0xFF
                // TGA stores BGR(A)
                out.write(b); out.write(g); out.write(r)
                if (hasAlpha) out.write(raw.data.getOrElse(px + 3) { -1 }.toInt() and 0xFF)
            }
        }
        return out.toByteArray()
    }

    override fun decode(data: ByteArray): ImageRaw? = runCatching {
        if (data.size < 18) return null
        val idLength = data[0].toInt() and 0xFF
        val imageType = data[2].toInt() and 0xFF
        if (imageType != 2) return null  // only uncompressed true-color supported

        val width = ((data[12].toInt() and 0xFF) or ((data[13].toInt() and 0xFF) shl 8))
        val height = ((data[14].toInt() and 0xFF) or ((data[15].toInt() and 0xFF) shl 8))
        val bpp = data[16].toInt() and 0xFF
        val components = when (bpp) { 32 -> 4; 24 -> 3; else -> return null }

        val headerSize = 18 + idLength
        val pixels = ByteArray(width * height * components)
        var src = headerSize
        for (row in height - 1 downTo 0) {
            for (x in 0 until width) {
                val dst = (row * width + x) * components
                val b = data.getOrElse(src++) { 0 }.toInt() and 0xFF
                val g = data.getOrElse(src++) { 0 }.toInt() and 0xFF
                val r = data.getOrElse(src++) { 0 }.toInt() and 0xFF
                pixels[dst] = r.toByte()
                pixels[dst + 1] = g.toByte()
                pixels[dst + 2] = b.toByte()
                if (components == 4) pixels[dst + 3] = data.getOrElse(src++) { -1 }
            }
        }
        ImageRaw(width, height, components, pixels)
    }.getOrNull()
}

object ImageCodecRegistry {
    private val codecs: Map<String, ImageCodec> = mapOf(
        "png"  to PngImage(),
        "jpg"  to JpegImage(),
        "jpeg" to JpegImage(),
        "bmp"  to BmpImage(),
        "tga"  to TgaImage()
    )

    fun get(extension: String): ImageCodec? = codecs[extension.lowercase().trimStart('.')]

    fun encode(raw: ImageRaw, extension: String): ByteArray =
        get(extension)?.encode(raw) ?: error("No codec for extension: $extension")

    fun decode(data: ByteArray, extension: String): ImageRaw? =
        get(extension)?.decode(data)
}
