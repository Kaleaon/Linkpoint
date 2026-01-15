package com.linkpoint.protocol.llsd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LLSDMapTest {

    @Test
    fun `getString returns value for LLSDString`() {
        val map = LLSDMap(mutableMapOf("name" to LLSDString("Kaleaon")))
        assertEquals("Kaleaon", map.getString("name"))
    }

    @Test
    fun `getString returns value for LLSDURI`() {
        val map = LLSDMap(mutableMapOf("cap" to LLSDURI("https://example.com/cap")))
        assertEquals("https://example.com/cap", map.getString("cap"))
    }

    @Test
    fun `getString returns null for non-string values`() {
        val map = LLSDMap(mutableMapOf("flag" to LLSDBoolean(true)))
        assertNull(map.getString("flag"))
    }
}
