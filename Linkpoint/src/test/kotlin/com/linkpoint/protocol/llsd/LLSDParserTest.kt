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

    @Test
    fun `parseXML handles byte slices directly`() {
        val xmlBytes = "<llsd><map><key>title</key><string>Linkpoint</string></map></llsd>".toByteArray(Charsets.UTF_8)
        val value = LLSDParser.parseXML(xmlBytes)
        assertTrue(value is LLSDMap)
        assertEquals(LLSDString("Linkpoint"), (value as LLSDMap)["title"])
    }

    @Test
    fun `parseXML handles CDATA and XML entities and UTF-8 characters`() {
        val xml = """
            <llsd>
              <map>
                <key>escaped</key>
                <string>&lt;hello &amp; "world"&gt;</string>
                <key>cdata</key>
                <string><![CDATA[<raw & unescaped>]]></string>
                <key>utf8</key>
                <string>こんにちは世界 🌍</string>
                <key>numeric</key>
                <string>&#60;test&#x3C;</string>
              </map>
            </llsd>
        """.trimIndent()
        val value = LLSDParser.parseXML(xml.toByteArray(Charsets.UTF_8))
        assertTrue(value is LLSDMap)
        val map = value as LLSDMap
        assertEquals(LLSDString("<hello & \"world\">"), map["escaped"])
        assertEquals(LLSDString("<raw & unescaped>"), map["cdata"])
        assertEquals(LLSDString("こんにちは世界 🌍"), map["utf8"])
        assertEquals(LLSDString("<test<"), map["numeric"])
    }

    @Test
    fun `parseXML handles self-closing tags and comments`() {
        val xml = """
            <?xml version="1.0" encoding="UTF-8"?>
            <!-- Top level comment -->
            <llsd>
              <!-- Inside llsd comment -->
              <array>
                <undef/>
                <boolean/>
                <integer/>
                <real/>
                <string/>
                <uuid/>
                <binary/>
                <map/>
                <array/>
              </array>
            </llsd>
        """.trimIndent()
        val value = LLSDParser.parseXML(xml)
        assertTrue(value is LLSDArray)
        val arr = (value as LLSDArray).value
        assertEquals(9, arr.size)
        assertEquals(LLSDUndefined, arr[0])
        assertEquals(LLSDBoolean(false), arr[1])
        assertEquals(LLSDInteger(0), arr[2])
        assertEquals(LLSDReal(0.0), arr[3])
        assertEquals(LLSDString(""), arr[4])
        assertEquals(LLSDUUID.ZERO, arr[5])
        assertEquals(LLSDBinary(byteArrayOf()), arr[6])
        assertEquals(LLSDMap(), arr[7])
        assertEquals(LLSDArray(), arr[8])
    }

    @Test
    fun `parse auto routes XML byte stream correctly`() {
        val xmlBytes = "<?xml version=\"1.0\"?><llsd><map><key>agent</key><string>linkpoint</string></map></llsd>".toByteArray(Charsets.UTF_8)
        val value = LLSDParser.parseAuto(xmlBytes, "application/llsd+xml")

        val expected = LLSDMap().apply { this["agent"] = LLSDString("linkpoint") }
        assertEquals(expected, value)
    }

    @Test
    fun `parse notation stream processes bytes without full string allocation`() {
        val notationBytes = "<?llsd/notation?>\n{'test':i99}".toByteArray(Charsets.UTF_8)
        val valueFromBytes = LLSDParser.parseNotation(notationBytes)
        val valueFromStream = LLSDParser.parseNotation(java.io.ByteArrayInputStream(notationBytes))

        val expected = LLSDMap().apply { this["test"] = LLSDInteger(99) }
        assertEquals(expected, valueFromBytes)
        assertEquals(expected, valueFromStream)
    }

    @Test
    fun `parse auto detects XML byte payload directly`() {
        val xmlBytes = "<llsd><array><integer>1</integer><integer>2</integer></array></llsd>".toByteArray(Charsets.UTF_8)
        val value = LLSDParser.parse(xmlBytes)

        val expected = LLSDArray().apply {
            add(LLSDInteger(1))
            add(LLSDInteger(2))
        }
        assertEquals(expected, value)
    }

    @Test
    fun `parseLlsdDate handles offsets sub-milliseconds and malformed strings`() {
        // Explicit offsets
        val date1 = LLSDParser.parseLlsdDate("2024-01-02T03:04:05.678+00:00")
        val date2 = LLSDParser.parseLlsdDate("2024-01-02T03:04:05Z")
        val date3 = LLSDParser.parseLlsdDate("2024-01-01T22:04:05.678-05:00") // Same instant as date1
        assertTrue(date1 != null)
        assertTrue(date2 != null)
        assertEquals(date1?.time, date3?.time)

        // Sub-millisecond precision
        val dateMicro = LLSDParser.parseLlsdDate("2024-01-02T03:04:05.678912Z")
        assertEquals(date1?.time, dateMicro?.time)

        // Malformed string returns null
        val malformed = LLSDParser.parseLlsdDate("not-a-date")
        assertEquals(null, malformed)
    }
}
