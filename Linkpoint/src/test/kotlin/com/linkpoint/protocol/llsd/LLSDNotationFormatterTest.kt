package com.linkpoint.protocol.llsd

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Date
import java.util.UUID
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class LLSDNotationFormatterTest {

    @Test
    fun `format scalar types`() {
        assertEquals("!", LLSDNotationFormatter.format(LLSDUndefined))
        assertEquals("true", LLSDNotationFormatter.format(LLSDBoolean(true)))
        assertEquals("false", LLSDNotationFormatter.format(LLSDBoolean(false)))
        assertEquals("i42", LLSDNotationFormatter.format(LLSDInteger(42)))
        assertEquals("r3.14", LLSDNotationFormatter.format(LLSDReal(3.14)))
        assertEquals("rNaN", LLSDNotationFormatter.format(LLSDReal(Double.NaN)))
        assertEquals("rInf", LLSDNotationFormatter.format(LLSDReal(Double.POSITIVE_INFINITY)))
        assertEquals("r-Inf", LLSDNotationFormatter.format(LLSDReal(Double.NEGATIVE_INFINITY)))

        val uuid = UUID.fromString("f496d6bf-8235-4ebf-bd56-4f7f04a64a27")
        assertEquals("uf496d6bf-8235-4ebf-bd56-4f7f04a64a27", LLSDNotationFormatter.format(LLSDUUID(uuid)))
        assertEquals("\"hello\"", LLSDNotationFormatter.format(LLSDString("hello")))
        assertEquals("l\"https://example.com\"", LLSDNotationFormatter.format(LLSDURI("https://example.com")))
        assertEquals("b64\"AQID\"", LLSDNotationFormatter.format(LLSDBinary(byteArrayOf(1, 2, 3))))
    }

    @Test
    fun `format date`() {
        val timestamp = 1704164645678L // 2024-01-02T03:04:05.678Z
        val dateValue = LLSDDate(Date(timestamp))
        val formatted = LLSDNotationFormatter.format(dateValue)
        assertEquals("d\"2024-01-02T03:04:05.678Z\"", formatted)
    }

    @Test
    fun `format map and array`() {
        val map = LLSDMap().apply {
            this["key"] = LLSDString("val")
            this["num"] = LLSDInteger(123)
        }
        assertEquals("{\"key\":\"val\",\"num\":i123}", LLSDNotationFormatter.format(map))

        val array = LLSDArray().apply {
            add(LLSDInteger(1))
            add(LLSDBoolean(true))
        }
        assertEquals("[i1,true]", LLSDNotationFormatter.format(array))
    }

    @Test
    fun `format with header`() {
        val formatted = LLSDNotationFormatter.format(LLSDInteger(10), includeHeader = true)
        assertEquals("<?llsd/notation?>\ni10", formatted)
    }

    @Test
    fun `concurrent date formatting and parsing thread safety`() {
        val threadCount = 16
        val iterationsPerThread = 500
        val executor = Executors.newFixedThreadPool(threadCount)

        val timestamp = 1704164645678L
        val dateValue = LLSDDate(Date(timestamp))
        val dateString = "2024-01-02T03:04:05.678Z"
        val notationDateString = "d\"2024-01-02T03:04:05.678Z\""

        val tasks = (0 until threadCount).map {
            Callable {
                repeat(iterationsPerThread) {
                    // Test LLSDNotationFormatter
                    val formattedNotation = LLSDNotationFormatter.format(dateValue)
                    assertEquals("d\"2024-01-02T03:04:05.678Z\"", formattedNotation)

                    // Test LLSDValue.toXML()
                    val xml = dateValue.toXML()
                    assertEquals("<date>2024-01-02T03:04:05.678Z</date>", xml)

                    // Test LLSDNotationParser date parsing via LLSDParser
                    val parsedNotation = LLSDParser.parseNotation(notationDateString.toByteArray(Charsets.UTF_8))
                    assertTrue(parsedNotation is LLSDDate)
                    assertEquals(timestamp, (parsedNotation as LLSDDate).value.time)

                    // Test LLSDParser.parseLlsdDate
                    val parsedDate = LLSDParser.parseLlsdDate(dateString)
                    assertEquals(timestamp, parsedDate?.time)
                }
            }
        }

        val futures = executor.invokeAll(tasks)
        for (future in futures) {
            future.get()
        }

        executor.shutdown()
    }
}
