package com.linkpoint.protocol.llsd

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
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
}
