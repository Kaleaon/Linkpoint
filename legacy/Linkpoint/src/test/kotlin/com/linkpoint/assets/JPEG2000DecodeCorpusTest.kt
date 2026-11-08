package com.linkpoint.assets

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class JPEG2000DecodeCorpusTest {

    @Test
    fun `discard policy prioritizes visibility and distance`() {
        assertEquals(0, TextureManager.computeDiscardLevelForRequest(TexturePriority.CRITICAL, 5f))
        assertEquals(2, TextureManager.computeDiscardLevelForRequest(TexturePriority.HIGH, 70f))
        assertEquals(5, TextureManager.computeDiscardLevelForRequest(TexturePriority.PREFETCH, 300f))
    }

    @Test
    fun `corpus header decode reports expected dimensions`() {
        val corpus = listOf(
            buildFakeJp2(width = 256, height = 128),
            buildFakeJ2k(width = 1024, height = 512)
        )

        val expected = listOf(256 to 128, 1024 to 512)
        corpus.forEachIndexed { index, payload ->
            val size = JPEG2000Decoder.getImageSize(payload)
            assertNotNull("Expected dimensions for corpus entry $index", size)
            assertEquals(expected[index], size)
        }
    }

    @Test
    fun `repeated decode cycles on malformed corpus do not leak aggressively`() {
        val malformed = listOf(
            ByteArray(0),
            ByteArray(8) { 0x01 },
            ByteArray(64) { if (it < 2) 0xFF.toByte() else 0x00 }
        )

        System.gc()
        val before = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()

        repeat(250) {
            malformed.forEach { data ->
                JPEG2000Decoder.getImageSize(data)
                JPEG2000Decoder.decode(data, discardLevel = 2)
            }
        }

        System.gc()
        val after = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()
        val growth = after - before
        assertTrue("Unexpected heap growth across repeated decode cycles: $growth", growth < 16L * 1024L * 1024L)
    }

    private fun buildFakeJp2(width: Int, height: Int): ByteArray {
        val bytes = ByteArray(80)
        bytes[0] = 0x00
        bytes[1] = 0x00
        bytes[2] = 0x00
        bytes[3] = 0x0C
        bytes[4] = 0x6A
        bytes[5] = 0x50
        bytes[6] = 0x20
        bytes[7] = 0x20

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
        bytes[6] = 0xFF.toByte()
        bytes[7] = 0x51.toByte()
        putInt(bytes, 10, width)
        putInt(bytes, 14, height)
        putInt(bytes, 18, 0)
        putInt(bytes, 22, 0)
        return bytes
    }

    private fun putInt(array: ByteArray, offset: Int, value: Int) {
        array[offset] = ((value ushr 24) and 0xFF).toByte()
        array[offset + 1] = ((value ushr 16) and 0xFF).toByte()
        array[offset + 2] = ((value ushr 8) and 0xFF).toByte()
        array[offset + 3] = (value and 0xFF).toByte()
    }
}
