package com.linkpoint.assets.mesh

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.util.zip.Deflater

class SafeMeshDecompressorTest {

    private lateinit var decompressor: SafeMeshDecompressor

    @Before
    fun setUp() {
        decompressor = SafeMeshDecompressor()
    }

    @Test
    fun testDecompressValidData() {
        val originalText = "Hello Linkpoint Decoupled Mesh Pipeline!".repeat(50)
        val originalBytes = originalText.toByteArray(Charsets.UTF_8)
        val compressed = compress(originalBytes)

        val decompressed = decompressor.decompress(compressed)
        assertArrayEquals(originalBytes, decompressed)

        val telemetry = decompressor.getTelemetry()
        assertEquals(1L, telemetry.decompressionCount)
        assertEquals(compressed.size.toLong(), telemetry.totalCompressedBytes)
        assertEquals(originalBytes.size.toLong(), telemetry.totalDecompressedBytes)
        assertTrue(telemetry.decompressionRatio > 1.0)
        assertEquals(0L, telemetry.quotaExceededCount)
        assertEquals(0L, telemetry.stallCount)
    }

    @Test(expected = QuotaExceededException::class)
    fun testDecompressExceedsQuotaThrowsException() {
        val smallQuotaDecompressor = SafeMeshDecompressor(maxQuotaBytes = 50L)
        val originalBytes = "Data exceeding fifty bytes threshold".repeat(10).toByteArray(Charsets.UTF_8)
        val compressed = compress(originalBytes)

        try {
            smallQuotaDecompressor.decompress(compressed)
        } catch (e: QuotaExceededException) {
            val telemetry = smallQuotaDecompressor.getTelemetry()
            assertEquals(1L, telemetry.quotaExceededCount)
            throw e
        }
    }

    @Test
    fun testLowMemoryProfileQuota32MB() {
        val lowMemDecompressor = SafeMeshDecompressor(maxQuotaBytes = SafeMeshDecompressor.LOW_MEMORY_QUOTA_BYTES)
        assertEquals(32 * 1024 * 1024L, lowMemDecompressor.maxQuotaBytes)
    }

    @Test
    fun testDefaultQuotaIs32MB() {
        val defaultDecompressor = SafeMeshDecompressor()
        assertEquals(32 * 1024 * 1024L, defaultDecompressor.maxQuotaBytes)
    }

    @Test(expected = StreamStallException::class)
    fun testStallDetectionOnTruncatedPayload() {
        // Invalid compressed data payload that stalls inflater progress
        val corruptHeader = byteArrayOf(0x78, 0x9C.toByte(), 0x00, 0x00, 0x00, 0x00)
        try {
            decompressor.decompress(corruptHeader)
        } catch (e: StreamStallException) {
            val telemetry = decompressor.getTelemetry()
            assertEquals(1L, telemetry.stallCount)
            throw e
        }
    }

    private fun compress(data: ByteArray): ByteArray {
        val deflater = Deflater()
        deflater.setInput(data)
        deflater.finish()
        val bos = ByteArrayOutputStream()
        val buffer = ByteArray(1024)
        while (!deflater.finished()) {
            val count = deflater.deflate(buffer)
            bos.write(buffer, 0, count)
        }
        deflater.end()
        return bos.toByteArray()
    }
}
