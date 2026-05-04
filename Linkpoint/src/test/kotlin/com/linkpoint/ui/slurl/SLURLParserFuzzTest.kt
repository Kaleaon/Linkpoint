package com.linkpoint.ui.slurl

import android.net.Uri
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class SLURLParserFuzzTest {

    @Test
    fun `accepts valid secondlife uri`() {
        val parsed = SLURLParser.parse(Uri.parse("secondlife://Linkpoint%20Bay/128/64/24"))
        assertNotNull(parsed)
        assertEquals("Linkpoint Bay", parsed?.regionName)
    }

    @Test
    fun `rejects malformed and oversized inputs without crash`() {
        val oversized = "secondlife://" + "A".repeat(3000)
        val malformedInputs = listOf(
            "",
            "not a uri",
            "ftp://maps.secondlife.com/secondlife/Region/1/2/3",
            "https://evil.example/secondlife/Region/128/128/0",
            "secondlife://Region/1/2/3/4",
            "secondlife://Region/1/two/3",
            "secondlife://user@Region/1/2/3",
            "secondlife://Region/1/2/3?x=y",
            "https://maps.secondlife.com/elsewhere/Region/1/2/3",
            "https://maps.secondlife.com/secondlife/",
            "https://maps.secondlife.com/secondlife/Region/1/2/3/4",
            "secondlife://Reg%00ion/1/2/3",
            oversized
        )

        malformedInputs.forEach { raw ->
            val result = SLURLParser.parse(Uri.parse(raw))
            assertNull("Expected null for input: $raw", result)
        }
    }

    @Test
    fun `sanitizes decoded path components and defaults coordinates`() {
        val parsed = SLURLParser.parse(Uri.parse("https://maps.secondlife.com/secondlife/%20My%20Region%20"))
        assertNotNull(parsed)
        assertEquals("My Region", parsed?.regionName)
        assertEquals(128f, parsed?.x)
        assertEquals(128f, parsed?.y)
        assertEquals(0f, parsed?.z)
    }
}
