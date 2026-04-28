package com.linkpoint.assets

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class JPEG2000DecoderHeaderTest {

    @Test
    fun `parses jp2 ihdr dimensions`() {
        val data = buildFakeJp2(width = 512, height = 256)
        val size = JPEG2000Decoder.getImageSize(data)
        assertNotNull(size)
        assertEquals(512, size!!.first)
        assertEquals(256, size.second)
    }

    @Test
    fun `parses j2k siz dimensions`() {
        val data = buildFakeJ2k(width = 1024, height = 512)
        val size = JPEG2000Decoder.getImageSize(data)
        assertNotNull(size)
        assertEquals(1024, size!!.first)
        assertEquals(512, size.second)
    }

    private fun buildFakeJp2(width: Int, height: Int): ByteArray {
        val bytes = ByteArray(80)
        // Signature box length/type: 0x0000000C 'jP  '
        bytes[0] = 0x00
        bytes[1] = 0x00
        bytes[2] = 0x00
        bytes[3] = 0x0C
        bytes[4] = 0x6A
        bytes[5] = 0x50
        bytes[6] = 0x20
        bytes[7] = 0x20

        // Fake ihdr box at offset 16
        val pos = 16
        bytes[pos + 0] = 0x00
        bytes[pos + 1] = 0x00
        bytes[pos + 2] = 0x00
        bytes[pos + 3] = 0x16
        bytes[pos + 4] = 0x69
        bytes[pos + 5] = 0x68
        bytes[pos + 6] = 0x64
        bytes[pos + 7] = 0x72

        putInt(bytes, pos + 8, height)
        putInt(bytes, pos + 12, width)
        return bytes
    }

    private fun buildFakeJ2k(width: Int, height: Int): ByteArray {
        val bytes = ByteArray(64)
        bytes[0] = 0xFF.toByte()
        bytes[1] = 0x4F.toByte()

        // SIZ marker at offset 6
        bytes[6] = 0xFF.toByte()
        bytes[7] = 0x51.toByte()
        putInt(bytes, 10, width)  // Xsiz
        putInt(bytes, 14, height) // Ysiz
        putInt(bytes, 18, 0)      // XOsiz
        putInt(bytes, 22, 0)      // YOsiz
        return bytes
    }

    private fun putInt(array: ByteArray, offset: Int, value: Int) {
        array[offset] = ((value ushr 24) and 0xFF).toByte()
        array[offset + 1] = ((value ushr 16) and 0xFF).toByte()
        array[offset + 2] = ((value ushr 8) and 0xFF).toByte()
        array[offset + 3] = (value and 0xFF).toByte()
    }
}
