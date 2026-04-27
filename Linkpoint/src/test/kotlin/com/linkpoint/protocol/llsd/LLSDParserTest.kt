package com.linkpoint.protocol.llsd

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.io.ByteArrayOutputStream
import java.util.Locale
import java.util.TimeZone
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LLSDParserTest {

    @Test
    fun `parseXML handles UTC date with milliseconds`() {
        val xml = "<llsd><date>2024-01-02T03:04:05.678Z</date></llsd>"
        val value = LLSDParser.parseXML(xml)

        assertTrue(value is LLSDDate)
        val expected = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse("2024-01-02T03:04:05.678Z")

        assertEquals(expected?.time, (value as LLSDDate).value.time)
    }

    @Test
    fun `parseXML handles UTC date without milliseconds`() {
        val xml = "<llsd><date>2024-01-02T03:04:05Z</date></llsd>"
        val value = LLSDParser.parseXML(xml)

        assertTrue(value is LLSDDate)
        val expected = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }.parse("2024-01-02T03:04:05Z")

        assertEquals(expected?.time, (value as LLSDDate).value.time)
    }

    @Test
    fun `parseBinary returns undefined for truncated integer payload`() {
        val payload = byteArrayOf('i'.code.toByte(), 0x00, 0x00)

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for truncated string payload`() {
        val payload = byteArrayOf(
            's'.code.toByte(),
            0x00,
            0x00,
            0x00,
            0x05,
            'h'.code.toByte(),
            'i'.code.toByte(),
        )

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for truncated binary payload`() {
        val payload = byteArrayOf(
            'b'.code.toByte(),
            0x00,
            0x00,
            0x00,
            0x04,
            0x01,
        )

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for oversized string field`() {
        val length = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(1024 * 1024 + 1).array()
        val payload = byteArrayOf('s'.code.toByte()) + length

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for oversized binary field`() {
        val length = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(1024 * 1024 + 1).array()
        val payload = byteArrayOf('b'.code.toByte()) + length

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for excessive nesting`() {
        val payload = ByteArray(130) { '['.code.toByte() } + ByteArray(130) { ']'.code.toByte() }

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for malformed map termination`() {
        val payload = byteArrayOf(
            '{'.code.toByte(),
            'k'.code.toByte(),
            0x00,
            0x00,
            0x00,
            0x01,
            'a'.code.toByte(),
            '1'.code.toByte(),
        )

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for malformed array termination`() {
        val payload = byteArrayOf('['.code.toByte(), '1'.code.toByte())

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for truncated map key bytes`() {
        val payload = byteArrayOf(
            '{'.code.toByte(),
            'k'.code.toByte(),
            0x00,
            0x00,
            0x00,
            0x03,
            'a'.code.toByte(),
            'b'.code.toByte(),
        )

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for malformed map key marker`() {
        val payload = byteArrayOf(
            '{'.code.toByte(),
            'x'.code.toByte(),
            '}'.code.toByte(),
        )

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for negative binary length`() {
        val payload = byteArrayOf(
            'b'.code.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
            0xFF.toByte(),
        )

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary returns undefined for oversized map entries`() {
        val entry = byteArrayOf(
            'k'.code.toByte(),
            0x00,
            0x00,
            0x00,
            0x01,
            'a'.code.toByte(),
            '1'.code.toByte(),
        )
        val payload = ByteArrayOutputStream().apply {
            write('{'.code)
            repeat(10_001) {
                write(entry)
            }
            write('}'.code)
        }.toByteArray()

        val result = LLSDParser.parseBinary(payload)

        assertEquals(LLSDUndefined, result)
    }

    @Test
    fun `parseBinary fuzz smoke never throws`() {
        repeat(2_000) { seed ->
            val random = Random(seed)
            val payload = ByteArray(random.nextInt(0, 512)) { random.nextInt(0, 256).toByte() }
            LLSDParser.parseBinary(payload)
        }
    }
}
