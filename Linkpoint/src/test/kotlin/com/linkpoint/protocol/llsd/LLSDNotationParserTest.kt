package com.linkpoint.protocol.llsd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.UUID

/**
 * Spot-checks for the LLSD notation parser. The full conformance
 * matrix lives in [LLSDConformanceTest] (XML ↔ binary); notation is
 * exercised here against hand-written fixtures because `python-llsd`
 * doesn't include notation in the canonical generator and we want
 * the test surface targeted at the things SL caps actually emit.
 */
class LLSDNotationParserTest {

    private fun parse(s: String): LLSDValue =
        LLSDParser.parseNotation(s.toByteArray(Charsets.UTF_8))

    @Test
    fun `undef literal`() {
        assertEquals(LLSDUndefined, parse("!"))
    }

    @Test
    fun `boolean literals`() {
        assertEquals(LLSDBoolean(true), parse("1"))
        assertEquals(LLSDBoolean(false), parse("0"))
        assertEquals(LLSDBoolean(true), parse("true"))
        assertEquals(LLSDBoolean(true), parse("T"))
        assertEquals(LLSDBoolean(false), parse("FALSE"))
    }

    @Test
    fun `integer in various positions`() {
        assertEquals(LLSDInteger(42), parse("i42"))
        assertEquals(LLSDInteger(-7), parse("i-7"))
        assertEquals(LLSDInteger(0), parse("i0"))
    }

    @Test
    fun `real with sign exponent and special values`() {
        assertEquals(LLSDReal(3.14), parse("r3.14"))
        assertEquals(LLSDReal(-2.5e10), parse("r-2.5e10"))
        val nan = parse("rNaN") as LLSDReal
        assertTrue("NaN", nan.value.isNaN())
        assertEquals(LLSDReal(Double.POSITIVE_INFINITY), parse("rInf"))
    }

    @Test
    fun `uuid literal`() {
        val u = UUID.fromString("f496d6bf-8235-4ebf-bd56-4f7f04a64a27")
        assertEquals(LLSDUUID(u), parse("uf496d6bf-8235-4ebf-bd56-4f7f04a64a27"))
    }

    @Test
    fun `string with single and double quotes plus escapes`() {
        assertEquals(LLSDString("hello"), parse("\"hello\""))
        assertEquals(LLSDString("can't"), parse("\"can't\""))
        assertEquals(LLSDString("line\nbreak"), parse("\"line\\nbreak\""))
        assertEquals(LLSDString("with \"quotes\""), parse("'with \"quotes\"'"))
    }

    @Test
    fun `uri form`() {
        assertEquals(
            LLSDURI("https://example.com/cap"),
            parse("l\"https://example.com/cap\"")
        )
    }

    @Test
    fun `date with milliseconds`() {
        val v = parse("d\"2026-05-01T07:27:22.000Z\"")
        assertTrue(v is LLSDDate)
        // Just confirm it parsed to a non-zero epoch — exact ms equality is
        // covered by LLSDParserTest's date format coverage.
        assertTrue((v as LLSDDate).value > 0L)
    }

    @Test
    fun `binary base64`() {
        val v = parse("b64\"AQID\"")
        assertTrue(v is LLSDBinary)
        assertEquals(byteArrayOf(1, 2, 3).toList(), (v as LLSDBinary).value.toList())
    }

    @Test
    fun `binary hex`() {
        val v = parse("b16\"deadbeef\"")
        assertTrue(v is LLSDBinary)
        assertEquals(
            byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte()).toList(),
            (v as LLSDBinary).value.toList()
        )
    }

    @Test
    fun `array nesting`() {
        val v = parse("[i1,i2,[i3,i4]]") as LLSDArray
        assertEquals(3, v.value.size)
        val inner = v.value[2] as LLSDArray
        assertEquals(2, inner.value.size)
        assertEquals(LLSDInteger(3), inner.value[0])
    }

    @Test
    fun `map with string keys and mixed values`() {
        val v = parse("{'a':i1,'b':\"hi\",'c':[i2]}") as LLSDMap
        assertEquals(LLSDInteger(1), v["a"])
        assertEquals(LLSDString("hi"), v["b"])
        assertNotNull(v["c"])
    }

    @Test
    fun `whitespace tolerant`() {
        val v = parse("  {  'k'  :  i7  }  ") as LLSDMap
        assertEquals(LLSDInteger(7), v["k"])
    }

    @Test
    fun `magic header is skipped`() {
        val v = parse("<?llsd/notation?>\n{'k':i7}") as LLSDMap
        assertEquals(LLSDInteger(7), v["k"])
    }

    @Test
    fun `auto detect routes to notation`() {
        val v = LLSDParser.parse("{'k':i7}".toByteArray()) as LLSDMap
        assertEquals(LLSDInteger(7), v["k"])
    }
}
