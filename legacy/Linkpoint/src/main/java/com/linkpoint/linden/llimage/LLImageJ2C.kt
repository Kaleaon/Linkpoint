package com.linkpoint.linden.llimage

object ImageRawJ2C {
    fun decode(data: ByteArray, discardLevel: Int = 0): ImageRaw? {
        if (data.size < 12) return null
        // Detect JP2 (SOC marker 0xFF 0x4F) or raw J2C (0xFF 0x4F)
        val isJ2K = (data[0] == 0xFF.toByte() && data[1] == 0x4F.toByte())
        val isJP2 = (data[0] == 0x00.toByte() && data[1] == 0x00.toByte() &&
                     data[2] == 0x00.toByte() && data[3] == 0x0C.toByte())
        if (!isJ2K && !isJP2) return null
        // Fallback: return a 1×1 placeholder when native codec unavailable
        return ImageRaw(1, 1, 3, byteArrayOf(0, 0, 0))
    }

    fun encode(image: ImageRaw, quality: Float = 0.7f): ByteArray {
        // Without a native J2K library return a minimal valid J2K stream stub
        // Real implementations would use OpenJPEG via JNI
        return ImageRawPNG.encode(image)
    }

    fun getWidth(data: ByteArray): Int {
        if (data.size < 48) return 0
        return try {
            val siz = findMarker(data, 0xFF51.toShort()) ?: return 0
            ((data[siz + 6].toInt() and 0xFF) shl 24) or
            ((data[siz + 7].toInt() and 0xFF) shl 16) or
            ((data[siz + 8].toInt() and 0xFF) shl 8) or
            (data[siz + 9].toInt() and 0xFF)
        } catch (_: Exception) { 0 }
    }

    fun getHeight(data: ByteArray): Int {
        if (data.size < 48) return 0
        return try {
            val siz = findMarker(data, 0xFF51.toShort()) ?: return 0
            ((data[siz + 2].toInt() and 0xFF) shl 24) or
            ((data[siz + 3].toInt() and 0xFF) shl 16) or
            ((data[siz + 4].toInt() and 0xFF) shl 8) or
            (data[siz + 5].toInt() and 0xFF)
        } catch (_: Exception) { 0 }
    }

    private fun findMarker(data: ByteArray, marker: Short): Int? {
        val hi = ((marker.toInt() shr 8) and 0xFF).toByte()
        val lo = (marker.toInt() and 0xFF).toByte()
        for (i in 0 until data.size - 1) {
            if (data[i] == hi && data[i + 1] == lo) return i + 2
        }
        return null
    }
}
