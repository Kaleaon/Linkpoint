package com.linkpoint.assets

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class JPEG2000DecoderProgressTest {

    @Test
    fun `notifies registered progress listeners during decode`() {
        val progressEvents = mutableListOf<Pair<Int, String>>()
        val listener = DecodeProgressListener { progress, stage ->
            progressEvents.add(Pair(progress, stage))
        }

        JPEG2000Decoder.addProgressListener(listener)
        try {
            val fakeData = buildFakeJ2k(100, 100)
            JPEG2000Decoder.decode(fakeData)

            assertTrue("Expected progress events to be captured", progressEvents.isNotEmpty())
            assertEquals(0, progressEvents.first().first)
            assertEquals("HEADER_PARSING", progressEvents.first().second)
            assertEquals(100, progressEvents.last().first)
        } finally {
            JPEG2000Decoder.removeProgressListener(listener)
        }
    }

    private fun buildFakeJ2k(width: Int, height: Int): ByteArray {
        val bytes = ByteArray(64)
        bytes[0] = 0xFF.toByte()
        bytes[1] = 0x4F.toByte()
        bytes[6] = 0xFF.toByte()
        bytes[7] = 0x51.toByte()
        putInt(bytes, 10, width)
        putInt(bytes, 14, height)
        return bytes
    }

    private fun putInt(array: ByteArray, offset: Int, value: Int) {
        array[offset] = ((value ushr 24) and 0xFF).toByte()
        array[offset + 1] = ((value ushr 16) and 0xFF).toByte()
        array[offset + 2] = ((value ushr 8) and 0xFF).toByte()
        array[offset + 3] = (value and 0xFF).toByte()
    }
}
