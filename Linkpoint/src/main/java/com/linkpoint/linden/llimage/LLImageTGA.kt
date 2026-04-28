package com.linkpoint.linden.llimage

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream

object ImageRawTGA {
    fun encode(image: ImageRaw): ByteArray {
        val out = ByteArrayOutputStream()
        val dos = DataOutputStream(out)
        // TGA header
        dos.write(0)          // ID length
        dos.write(0)          // color map type
        dos.write(if (image.components >= 3) 2 else 3)  // image type: 2=RGB, 3=grey
        dos.writeShort(0); dos.writeShort(0); dos.write(0)  // color map spec
        dos.writeShort(0); dos.writeShort(0)  // x/y origin
        dos.writeShort(image.width)
        dos.writeShort(image.height)
        val bpp = if (image.components == 4) 32 else if (image.components == 3) 24 else 8
        dos.write(bpp)
        dos.write(if (image.components == 4) 0x28 else 0x20)  // image descriptor (top-left origin)
        val data = image.data
        // Write pixels bottom-to-top for standard TGA orientation
        for (y in image.height - 1 downTo 0) {
            for (x in 0 until image.width) {
                val off = (y * image.width + x) * image.components
                when (image.components) {
                    4 -> { dos.write(data[off + 2].toInt()); dos.write(data[off + 1].toInt()); dos.write(data[off].toInt()); dos.write(data[off + 3].toInt()) }
                    3 -> { dos.write(data[off + 2].toInt()); dos.write(data[off + 1].toInt()); dos.write(data[off].toInt()) }
                    else -> dos.write(data[off].toInt())
                }
            }
        }
        dos.flush()
        return out.toByteArray()
    }

    fun decode(data: ByteArray): ImageRaw? = runCatching {
        val inp = ByteArrayInputStream(data)
        val idLen   = inp.read()
        val cmType  = inp.read()
        val imgType = inp.read()
        inp.skip(5)  // color map spec
        inp.skip(4)  // x/y origin
        val width   = (inp.read() or (inp.read() shl 8))
        val height  = (inp.read() or (inp.read() shl 8))
        val bpp     = inp.read()
        val imgDesc = inp.read()
        inp.skip(idLen.toLong())
        val components = bpp / 8
        if (width <= 0 || height <= 0 || components !in 1..4) return null
        val topDown = (imgDesc and 0x20) != 0
        val pixels = ByteArray(width * height * components)
        when (imgType) {
            2, 3 -> {
                val rowData = ByteArray(width * components)
                for (row in 0 until height) {
                    inp.read(rowData)
                    val destRow = if (topDown) row else height - 1 - row
                    System.arraycopy(rowData, 0, pixels, destRow * width * components, rowData.size)
                }
                // BGR → RGB
                if (components >= 3) {
                    for (i in pixels.indices step components) { val tmp = pixels[i]; pixels[i] = pixels[i + 2]; pixels[i + 2] = tmp }
                }
            }
            else -> return null
        }
        ImageRaw(width, height, components, pixels)
    }.getOrNull()
}
