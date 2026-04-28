package com.linkpoint.protocol.llsd

import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4

import java.io.ByteArrayInputStream
import java.io.DataInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@RunWith(AndroidJUnit4::class)
class LLSDStreamingParserLimitsTest {

    @Test
    fun `parseBinary rejects oversized string payload before allocation`() {
        val payload = byteArrayOf('s'.code.toByte()) + beInt(10)

        assertParseFailsWith(
            expectedMessagePart = "string length 10 exceeds 4"
        ) {
            LLSDStreamingParser.parseBinary(
                DataInputStream(ByteArrayInputStream(payload)),
                LLSDStreamingParser.LLSDDefaultContentHandler(),
                LLSDStreamingParser.ParseLimits(maxStringBytes = 4)
            )
        }
    }

    @Test
    fun `parseBinary rejects oversized binary payload before allocation`() {
        val payload = byteArrayOf('b'.code.toByte()) + beInt(11)

        assertParseFailsWith(
            expectedMessagePart = "binary length 11 exceeds 4"
        ) {
            LLSDStreamingParser.parseBinary(
                DataInputStream(ByteArrayInputStream(payload)),
                LLSDStreamingParser.LLSDDefaultContentHandler(),
                LLSDStreamingParser.ParseLimits(maxBinaryBytes = 4)
            )
        }
    }

    @Test
    fun `parseBinary rejects negative string length`() {
        val payload = byteArrayOf('s'.code.toByte()) + beInt(-1)

        assertParseFailsWith(
            expectedMessagePart = "negative string length"
        ) {
            LLSDStreamingParser.parseBinary(
                DataInputStream(ByteArrayInputStream(payload)),
                LLSDStreamingParser.LLSDDefaultContentHandler()
            )
        }
    }

    @Test
    fun `parseXML rejects deep nesting`() {
        val nested = buildString {
            append("<llsd>")
            repeat(8) { append("<array>") }
            append("<integer>1</integer>")
            repeat(8) { append("</array>") }
            append("</llsd>")
        }

        assertParseFailsWith(
            expectedMessagePart = "maximum nesting depth 4"
        ) {
            LLSDStreamingParser.parseXML(
                ByteArrayInputStream(nested.toByteArray(Charsets.UTF_8)),
                "UTF-8",
                LLSDStreamingParser.LLSDDefaultContentHandler(),
                LLSDStreamingParser.ParseLimits(maxNestingDepth = 4)
            )
        }
    }

    @Test
    fun `parseBinary rejects huge arrays`() {
        val payload = ByteArray(8) { '['.code.toByte() } + ByteArray(8) { ']'.code.toByte() }

        assertParseFailsWith(
            expectedMessagePart = "array length exceeds 3"
        ) {
            LLSDStreamingParser.parseBinary(
                DataInputStream(ByteArrayInputStream(payload)),
                LLSDStreamingParser.LLSDDefaultContentHandler(),
                LLSDStreamingParser.ParseLimits(maxArrayLength = 3)
            )
        }
    }

    @Test
    fun `parseBinary rejects huge maps`() {
        val entry = byteArrayOf(
            'k'.code.toByte(),
            0x00,
            0x00,
            0x00,
            0x01,
            'a'.code.toByte(),
            '1'.code.toByte(),
        )
        val payload = ByteArrayOutputBuilder().apply {
            append('{'.code.toByte())
            repeat(5) { append(entry) }
            append('}'.code.toByte())
        }.toByteArray()

        assertParseFailsWith(
            expectedMessagePart = "map entries exceed 3"
        ) {
            LLSDStreamingParser.parseBinary(
                DataInputStream(ByteArrayInputStream(payload)),
                LLSDStreamingParser.LLSDDefaultContentHandler(),
                LLSDStreamingParser.ParseLimits(maxMapEntries = 3)
            )
        }
    }

    private fun assertParseFailsWith(expectedMessagePart: String, block: () -> Unit) {
        try {
            block()
            fail("Expected IOException containing: $expectedMessagePart")
        } catch (e: IOException) {
            assertTrue(
                "Expected message to contain '$expectedMessagePart', got '${e.message}'",
                e.message?.contains(expectedMessagePart) == true
            )
        }
    }

    private fun beInt(value: Int): ByteArray =
        ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(value).array()

    private class ByteArrayOutputBuilder {
        private val bytes = ArrayList<Byte>()

        fun append(value: Byte) {
            bytes.add(value)
        }

        fun append(values: ByteArray) {
            values.forEach { bytes.add(it) }
        }

        fun toByteArray(): ByteArray = ByteArray(bytes.size) { idx -> bytes[idx] }
    }
}
