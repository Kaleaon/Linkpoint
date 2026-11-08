package com.linkpoint.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DiagnosticsLogSanitizerTest {

    @Test
    fun `redacts sensitive headers tokens and capability urls`() {
        val input = """
            Authorization: Bearer abc.def.ghi
            Cookie: sessionid=topsecret
            URL: https://example.com/CAPS/12345678-AAAA-BBBB-CCCC-1234567890AB?auth_token=1234&safe=1
            apiKey=abcd1234
        """.trimIndent()

        val sanitized = DiagnosticsLogSanitizer.sanitize(input)

        assertFalse(sanitized.contains("abc.def.ghi"))
        assertFalse(sanitized.contains("topsecret"))
        assertFalse(sanitized.contains("12345678-AAAA-BBBB-CCCC-1234567890AB"))
        assertFalse(sanitized.contains("auth_token=1234"))
        assertFalse(sanitized.contains("abcd1234"))
        assertTrue(sanitized.contains("<redacted>"))
    }

    @Test
    fun `sanitize header leaves non sensitive header intact`() {
        val value = DiagnosticsLogSanitizer.sanitizeHeader("Content-Type", "application/json")
        assertTrue(value == "application/json")
    }
}
