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

    @Test
    fun `decode returns synthetic placeholder with header dimensions when native decoder fails`() {
        val data = buildFakeJp2(width = 256, height = 256)
        val bitmap = JPEG2000Decoder.decode(data)
        assertNotNull("Decoder should never return null for valid binary payloads", bitmap)
        assertEquals(256, bitmap!!.width)
        assertEquals(256, bitmap.height)
    }

    @Test
    fun `decode returns 128x128 synthetic grid placeholder when header parsing fails`() {
        val corruptData = byteArrayOf(0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08)
        val bitmap = JPEG2000Decoder.decode(corruptData)
        assertNotNull("Decoder should generate synthetic fallback bitmap for corrupt payloads", bitmap)
        assertEquals(128, bitmap!!.width)
        assertEquals(128, bitmap.height)
    }

    @Test
    fun `pipeline returns non-null output for unhandled Basis payloads`() {
        val basisBytes = byteArrayOf(0xAB.toByte(), 0xCD.toByte(), 0xEF.toByte())
        val req = TextureDecodeTranscodePipeline.Request(
            backend = TextureFormatPolicy.Backend.FILAMENT,
            semantic = TextureFormatPolicy.TextureSemantic.ALBEDO,
            capabilities = TextureFormatPolicy.DeviceCapabilities(supportsEtc2Rgba = true, supportsBasisTranscoding = false)
        )
        val output = TextureDecodeTranscodePipeline.fromBasisKtx2(java.util.UUID.randomUUID(), basisBytes, req)
        assertNotNull("Pipeline must return non-null output for Basis payloads", output)
        assertEquals(128, output!!.width)
        assertEquals(128, output.height)
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

    @Test
    fun `parses jp2 ihdr dimensions with 64-bit extended box lengths and superbox traversal`() {
        val data = buildFakeJp264BitAndSuperbox(width = 1024, height = 768)
        val size = JPEG2000Decoder.getImageSize(data)
        assertNotNull(size)
        assertEquals(1024, size!!.first)
        assertEquals(768, size.second)
    }

    private fun buildFakeJp264BitAndSuperbox(width: Int, height: Int): ByteArray {
        val bytes = ByteArray(128)
        // 1. Signature box: LBox = 12 (0x0000000C), TBox = 'jP  '
        bytes[0] = 0x00; bytes[1] = 0x00; bytes[2] = 0x00; bytes[3] = 0x0C
        bytes[4] = 0x6A; bytes[5] = 0x50; bytes[6] = 0x20; bytes[7] = 0x20

        // 2. Superbox 'jp2h' with 64-bit XLBox:
        // LBox = 1 (0x00000001), TBox = 'jp2h' (0x6A703268), XLBox = 100 bytes
        var pos = 12
        bytes[pos + 0] = 0x00; bytes[pos + 1] = 0x00; bytes[pos + 2] = 0x00; bytes[pos + 3] = 0x01
        bytes[pos + 4] = 0x6A; bytes[pos + 5] = 0x70; bytes[pos + 6] = 0x32; bytes[pos + 7] = 0x68
        putLong(bytes, pos + 8, 100L) // XLBox length 100

        // 3. Child box 'ihdr' inside 'jp2h' superbox (header offset = 16)
        pos = 28 // 12 + 16
        bytes[pos + 0] = 0x00; bytes[pos + 1] = 0x00; bytes[pos + 2] = 0x00; bytes[pos + 3] = 0x16 // LBox = 22
        bytes[pos + 4] = 0x69; bytes[pos + 5] = 0x68; bytes[pos + 6] = 0x64; bytes[pos + 7] = 0x72 // 'ihdr'
        putInt(bytes, pos + 8, height)
        putInt(bytes, pos + 12, width)

        return bytes
    }

    private fun putInt(array: ByteArray, offset: Int, value: Int) {
        array[offset] = ((value ushr 24) and 0xFF).toByte()
        array[offset + 1] = ((value ushr 16) and 0xFF).toByte()
        array[offset + 2] = ((value ushr 8) and 0xFF).toByte()
        array[offset + 3] = (value and 0xFF).toByte()
    }

    private fun putLong(array: ByteArray, offset: Int, value: Long) {
        array[offset + 0] = ((value ushr 56) and 0xFF).toByte()
        array[offset + 1] = ((value ushr 48) and 0xFF).toByte()
        array[offset + 2] = ((value ushr 40) and 0xFF).toByte()
        array[offset + 3] = ((value ushr 32) and 0xFF).toByte()
        array[offset + 4] = ((value ushr 24) and 0xFF).toByte()
        array[offset + 5] = ((value ushr 16) and 0xFF).toByte()
        array[offset + 6] = ((value ushr 8) and 0xFF).toByte()
        array[offset + 7] = (value and 0xFF).toByte()
    }
}
