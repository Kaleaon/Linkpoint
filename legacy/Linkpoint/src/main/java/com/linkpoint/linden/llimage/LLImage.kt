package com.linkpoint.linden.llimage

object ImageConstants {
    const val MIN_IMAGE_MIP = 2
    const val MAX_IMAGE_MIP = 12
    const val MAX_DISCARD_LEVEL = 5
    const val MIN_IMAGE_SIZE = 1 shl MIN_IMAGE_MIP
    const val MAX_IMAGE_SIZE = 1 shl MAX_IMAGE_MIP
    const val MIN_IMAGE_AREA = MIN_IMAGE_SIZE * MIN_IMAGE_SIZE
    const val MAX_IMAGE_AREA = MAX_IMAGE_SIZE * MAX_IMAGE_SIZE
    const val MAX_IMAGE_COMPONENTS = 8
    const val MAX_IMAGE_DATA_SIZE = MAX_IMAGE_AREA * MAX_IMAGE_COMPONENTS
    const val FIRST_PACKET_SIZE = 600
    const val MAX_IMG_PACKET_SIZE = 1000
    const val HTTP_PACKET_SIZE = 1496

    const val TYPE_NORMAL = 0
    const val TYPE_AVATAR_BAKE = 1
}

enum class ImageCodecType(val id: Int, val extension: String) {
    INVALID(0, ""),
    RGB(1, "rgb"),
    J2C(2, "j2c"),
    BMP(3, "bmp"),
    TGA(4, "tga"),
    JPEG(5, "jpg"),
    DXT(6, "dxt"),
    PNG(7, "png");

    companion object {
        fun fromExtension(ext: String): ImageCodecType =
            entries.firstOrNull { it.extension == ext.lowercase().trimStart('.') } ?: INVALID
    }
}

class ImageRaw(
    var width: Int,
    var height: Int,
    var components: Int,
    val data: ByteArray = ByteArray(width * height * components)
) {
    constructor() : this(0, 0, 0, ByteArray(0))
    constructor(width: Int, height: Int, components: Int) :
        this(width, height, components, ByteArray(width * height * components))

    val dataSize: Int get() = data.size
    var comment: String = ""

    fun isValid(): Boolean = width > 0 && height > 0 && components > 0 && data.size == width * height * components

    fun clear(r: Byte = 0, g: Byte = 0, b: Byte = 0, a: Byte = -1) {
        var i = 0
        while (i < data.size) {
            when (components) {
                1 -> data[i] = r
                2 -> { data[i] = r; data[i + 1] = a }
                3 -> { data[i] = r; data[i + 1] = g; data[i + 2] = b }
                4 -> { data[i] = r; data[i + 1] = g; data[i + 2] = b; data[i + 3] = a }
            }
            i += components
        }
    }

    fun verticalFlip(): ImageRaw {
        val result = ByteArray(data.size)
        val rowSize = width * components
        for (row in 0 until height) {
            val srcRow = (height - 1 - row) * rowSize
            val dstRow = row * rowSize
            data.copyInto(result, dstRow, srcRow, srcRow + rowSize)
        }
        return ImageRaw(width, height, components, result)
    }

    fun scale(newWidth: Int, newHeight: Int): ImageRaw {
        if (newWidth == width && newHeight == height) return ImageRaw(width, height, components, data.copyOf())
        val dest = ByteArray(newWidth * newHeight * components)
        val xRatio = width.toFloat() / newWidth
        val yRatio = height.toFloat() / newHeight
        for (y in 0 until newHeight) {
            val srcY = (y * yRatio).toInt().coerceIn(0, height - 1)
            for (x in 0 until newWidth) {
                val srcX = (x * xRatio).toInt().coerceIn(0, width - 1)
                val srcIdx = (srcY * width + srcX) * components
                val dstIdx = (y * newWidth + x) * components
                for (c in 0 until components) dest[dstIdx + c] = data[srcIdx + c]
            }
        }
        return ImageRaw(newWidth, newHeight, components, dest)
    }

    fun crop(x: Int, y: Int, cropWidth: Int, cropHeight: Int): ImageRaw {
        val clampX = x.coerceIn(0, width - 1)
        val clampY = y.coerceIn(0, height - 1)
        val clampW = cropWidth.coerceAtMost(width - clampX)
        val clampH = cropHeight.coerceAtMost(height - clampY)
        val dest = ByteArray(clampW * clampH * components)
        for (row in 0 until clampH) {
            val srcOffset = ((clampY + row) * width + clampX) * components
            val dstOffset = row * clampW * components
            data.copyInto(dest, dstOffset, srcOffset, srcOffset + clampW * components)
        }
        return ImageRaw(clampW, clampH, components, dest)
    }

    fun compositeScaled(src: ImageRaw): ImageRaw {
        val scaled = src.scale(width, height)
        return composite(scaled)
    }

    fun composite(src: ImageRaw): ImageRaw {
        require(src.width == width && src.height == height)
        val result = data.copyOf()
        if (src.components == 4 && components == 3) {
            var si = 0; var di = 0
            while (si < src.data.size) {
                val a = (src.data[si + 3].toInt() and 0xFF) / 255f
                val ia = 1f - a
                result[di] = ((src.data[si].toInt() and 0xFF) * a + (result[di].toInt() and 0xFF) * ia).toInt().toByte()
                result[di + 1] = ((src.data[si + 1].toInt() and 0xFF) * a + (result[di + 1].toInt() and 0xFF) * ia).toInt().toByte()
                result[di + 2] = ((src.data[si + 2].toInt() and 0xFF) * a + (result[di + 2].toInt() and 0xFF) * ia).toInt().toByte()
                si += 4; di += 3
            }
        } else {
            src.data.copyInto(result, 0, 0, minOf(src.data.size, result.size))
        }
        return ImageRaw(width, height, components, result)
    }

    fun convertToType(newComponents: Int): ImageRaw {
        if (newComponents == components) return ImageRaw(width, height, components, data.copyOf())
        val dest = ByteArray(width * height * newComponents)
        val pixels = width * height
        for (i in 0 until pixels) {
            val srcBase = i * components
            val dstBase = i * newComponents
            val r = if (components > 0) data.getOrElse(srcBase) { 0 } else 0
            val g = if (components > 1) data.getOrElse(srcBase + 1) { r } else r
            val b = if (components > 2) data.getOrElse(srcBase + 2) { r } else r
            val a = if (components > 3) data.getOrElse(srcBase + 3) { -1 } else -1
            when (newComponents) {
                1 -> dest[dstBase] = r
                2 -> { dest[dstBase] = r; dest[dstBase + 1] = a.toByte() }
                3 -> { dest[dstBase] = r; dest[dstBase + 1] = g; dest[dstBase + 2] = b }
                4 -> { dest[dstBase] = r; dest[dstBase + 1] = g; dest[dstBase + 2] = b; dest[dstBase + 3] = a.toByte() }
            }
        }
        return ImageRaw(width, height, newComponents, dest)
    }

    fun checkHasTransparentPixels(): Boolean {
        if (components < 4) return false
        var i = 3
        while (i < data.size) {
            if (data[i] != (-1).toByte()) return true
            i += components
        }
        return false
    }

    fun resize(newWidth: Int, newHeight: Int, newComponents: Int): ImageRaw =
        scale(newWidth, newHeight).let {
            if (newComponents != components) it.convertToType(newComponents) else it
        }

    companion object {
        fun biasedDimToPowerOfTwo(dim: Int, maxDim: Int = ImageConstants.MAX_IMAGE_SIZE): Int {
            var result = 1
            while (result < dim && result < maxDim) result = result shl 1
            return if (result > maxDim) maxDim else result
        }

        fun expandDimToPowerOfTwo(dim: Int, maxDim: Int = ImageConstants.MAX_IMAGE_SIZE): Int {
            var result = 1
            while (result < dim && result < maxDim) result = result shl 1
            return result
        }

        fun contractDimToPowerOfTwo(dim: Int, minDim: Int = ImageConstants.MIN_IMAGE_SIZE): Int {
            var result = dim
            while (result > minDim && result % 2 == 0) result = result shr 1
            return result.coerceAtLeast(minDim)
        }

        fun calcDownloadPriority(virtualSize: Float, visibleArea: Float, bytesSent: Int): Float {
            if (virtualSize <= 0f || visibleArea <= 0f) return 0f
            return (virtualSize / visibleArea) * (1f - bytesSent.toFloat() / ImageConstants.MAX_IMAGE_DATA_SIZE)
        }
    }
}
