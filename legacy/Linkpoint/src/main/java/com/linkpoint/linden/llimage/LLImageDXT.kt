package com.linkpoint.linden.llimage

object ImageRawDXT {
    enum class Format { DXT1, DXT3, DXT5 }

    fun decode(data: ByteArray, width: Int, height: Int, format: Format): ImageRaw? {
        if (data.size < 8) return null
        val pixels = ByteArray(width * height * 4)
        val blocksX = maxOf(1, (width + 3) / 4)
        val blocksY = maxOf(1, (height + 3) / 4)
        var offset = 0
        for (by in 0 until blocksY) {
            for (bx in 0 until blocksX) {
                when (format) {
                    Format.DXT1 -> {
                        if (offset + 8 > data.size) break
                        decodeDXT1Block(data, offset, pixels, bx * 4, by * 4, width)
                        offset += 8
                    }
                    Format.DXT3 -> {
                        if (offset + 16 > data.size) break
                        val alphaBlock = data.copyOfRange(offset, offset + 8)
                        decodeDXT1Block(data, offset + 8, pixels, bx * 4, by * 4, width)
                        decodeDXT3Alpha(alphaBlock, pixels, bx * 4, by * 4, width)
                        offset += 16
                    }
                    Format.DXT5 -> {
                        if (offset + 16 > data.size) break
                        val alphaBlock = data.copyOfRange(offset, offset + 8)
                        decodeDXT1Block(data, offset + 8, pixels, bx * 4, by * 4, width)
                        decodeDXT5Alpha(alphaBlock, pixels, bx * 4, by * 4, width)
                        offset += 16
                    }
                }
            }
        }
        return ImageRaw(width, height, 4, pixels)
    }

    private fun decodeDXT1Block(data: ByteArray, off: Int, pixels: ByteArray, bx: Int, by: Int, imgW: Int) {
        val c0 = (data[off].toInt() and 0xFF) or ((data[off + 1].toInt() and 0xFF) shl 8)
        val c1 = (data[off + 2].toInt() and 0xFF) or ((data[off + 3].toInt() and 0xFF) shl 8)
        val r0 = ((c0 shr 11) and 0x1F) * 255 / 31
        val g0 = ((c0 shr 5) and 0x3F) * 255 / 63
        val b0 = (c0 and 0x1F) * 255 / 31
        val r1 = ((c1 shr 11) and 0x1F) * 255 / 31
        val g1 = ((c1 shr 5) and 0x3F) * 255 / 63
        val b1 = (c1 and 0x1F) * 255 / 31
        val colors = arrayOf(
            intArrayOf(r0, g0, b0, 255),
            intArrayOf(r1, g1, b1, 255),
            if (c0 > c1) intArrayOf((2*r0+r1)/3, (2*g0+g1)/3, (2*b0+b1)/3, 255)
                         else intArrayOf((r0+r1)/2, (g0+g1)/2, (b0+b1)/2, 255),
            if (c0 > c1) intArrayOf((r0+2*r1)/3, (g0+2*g1)/3, (b0+2*b1)/3, 255)
                         else intArrayOf(0, 0, 0, 0)
        )
        var lookup = (data[off+4].toLong() and 0xFF) or ((data[off+5].toLong() and 0xFF) shl 8) or
                     ((data[off+6].toLong() and 0xFF) shl 16) or ((data[off+7].toLong() and 0xFF) shl 24)
        for (py in 0..3) for (px in 0..3) {
            val idx = (lookup and 3L).toInt(); lookup = lookup shr 2
            val col = colors[idx]
            val iy = by + py; val ix = bx + px
            if (iy < (pixels.size / 4 / imgW) && ix < imgW) {
                val p = (iy * imgW + ix) * 4
                pixels[p] = col[0].toByte(); pixels[p+1] = col[1].toByte()
                pixels[p+2] = col[2].toByte(); pixels[p+3] = col[3].toByte()
            }
        }
    }

    private fun decodeDXT3Alpha(alpha: ByteArray, pixels: ByteArray, bx: Int, by: Int, imgW: Int) {
        var i = 0
        for (py in 0..3) for (px in 0..1) {
            val b = alpha[i++].toInt() and 0xFF
            for (half in 0..1) {
                val a = ((b shr (half * 4)) and 0xF) * 17
                val ix = bx + px * 2 + half; val iy = by + py
                if (iy < (pixels.size / 4 / imgW) && ix < imgW) pixels[(iy * imgW + ix) * 4 + 3] = a.toByte()
            }
        }
    }

    private fun decodeDXT5Alpha(alpha: ByteArray, pixels: ByteArray, bx: Int, by: Int, imgW: Int) {
        val a0 = alpha[0].toInt() and 0xFF; val a1 = alpha[1].toInt() and 0xFF
        val alphaTable = IntArray(8)
        alphaTable[0] = a0; alphaTable[1] = a1
        if (a0 > a1) { for (i in 2..7) alphaTable[i] = ((7-i)*a0 + (i-1)*a1) / 6 }
        else { for (i in 2..5) alphaTable[i] = ((5-i)*a0 + (i-1)*a1) / 4; alphaTable[6] = 0; alphaTable[7] = 255 }
        var bits = 0L
        for (i in 2..7) bits = bits or ((alpha[i].toLong() and 0xFF) shl ((i-2)*8))
        for (py in 0..3) for (px in 0..3) {
            val idx = (bits and 7L).toInt(); bits = bits shr 3
            val ix = bx + px; val iy = by + py
            if (iy < (pixels.size / 4 / imgW) && ix < imgW) pixels[(iy * imgW + ix) * 4 + 3] = alphaTable[idx].toByte()
        }
    }
}
