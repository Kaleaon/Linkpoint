package com.linkpoint.scripts.lsl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LSLLanguageTest {

    @Test
    fun `mergeRanges merges empty, overlapping, adjacent, nested, and unordered ranges correctly`() {
        // Empty list
        assertEquals(emptyList<IntRange>(), LSLLanguage.mergeRanges(emptyList()))

        // Single range
        assertEquals(listOf(1..5), LSLLanguage.mergeRanges(listOf(1..5)))

        // Overlapping ranges
        assertEquals(listOf(1..8), LSLLanguage.mergeRanges(listOf(1..5, 3..8)))

        // Adjacent ranges
        assertEquals(listOf(1..10), LSLLanguage.mergeRanges(listOf(1..5, 6..10)))

        // Disjoint non-adjacent ranges
        assertEquals(listOf(1..5, 8..12), LSLLanguage.mergeRanges(listOf(1..5, 8..12)))

        // Unordered ranges
        assertEquals(
            listOf(1..5, 8..12, 20..30),
            LSLLanguage.mergeRanges(listOf(20..30, 1..5, 8..12))
        )

        // Nested ranges
        assertEquals(listOf(1..20), LSLLanguage.mergeRanges(listOf(1..20, 5..10)))
    }

    @Test
    fun `isInStyledRange handles empty ranges and boundary positions via binary search`() {
        val ranges = listOf(5..10, 20..30, 40..50)

        // Empty ranges
        assertFalse(LSLLanguage.isInStyledRange(5, emptyList()))

        // Before first range
        assertFalse(LSLLanguage.isInStyledRange(0, ranges))
        assertFalse(LSLLanguage.isInStyledRange(4, ranges))

        // First range boundaries and interior
        assertTrue(LSLLanguage.isInStyledRange(5, ranges))
        assertTrue(LSLLanguage.isInStyledRange(7, ranges))
        assertTrue(LSLLanguage.isInStyledRange(10, ranges))

        // In gap between first and second range
        assertFalse(LSLLanguage.isInStyledRange(11, ranges))
        assertFalse(LSLLanguage.isInStyledRange(19, ranges))

        // Middle range boundaries and interior
        assertTrue(LSLLanguage.isInStyledRange(20, ranges))
        assertTrue(LSLLanguage.isInStyledRange(25, ranges))
        assertTrue(LSLLanguage.isInStyledRange(30, ranges))

        // After last range
        assertFalse(LSLLanguage.isInStyledRange(51, ranges))
        assertFalse(LSLLanguage.isInStyledRange(100, ranges))
    }

    @Test
    fun `highlight styles script tokens and prevents double-styling inside comments and strings`() {
        val script = """
            default
            {
                state_entry()
                {
                    // llSay(0, "Inside single-line comment");
                    string msg = "llGetPos inside string";
                    /*
                      integer x = 10;
                    */
                    llSay(0, msg);
                }
            }
        """.trimIndent()

        val highlighted = LSLLanguage.highlight(script)
        assertEquals(script, highlighted.text)

        // Ensure styles exist
        val styles = highlighted.spanStyles
        assertTrue("Highlighted string should contain span styles", styles.isNotEmpty())

        // Verify function style exists for llSay outside comment/string
        val llSayIndex = script.lastIndexOf("llSay")
        val llSayStyle = styles.find { it.start == llSayIndex && it.item.color == LSLLanguage.Colors.FUNCTION }
        assertTrue("llSay outside comment/string should be styled as function", llSayStyle != null)

        // Verify commented llSay is styled as comment, not function
        val commentedLlSayIndex = script.indexOf("llSay")
        val commentStyle = styles.find {
            it.start <= commentedLlSayIndex &&
            it.end >= commentedLlSayIndex + 5 &&
            it.item.color == LSLLanguage.Colors.COMMENT
        }
        assertTrue("Commented llSay should be styled as comment", commentStyle != null)
        val commentedFunctionStyle = styles.find {
            it.start == commentedLlSayIndex && it.item.color == LSLLanguage.Colors.FUNCTION
        }
        assertTrue("Commented llSay should NOT be styled as function", commentedFunctionStyle == null)
    }

    @Test
    fun `highlight performs efficiently on large scripts with thousands of tokens`() {
        // Construct a large LSL script with ~1500 lines and thousands of tokens
        val lineBuilder = StringBuilder()
        lineBuilder.append("default {\n state_entry() {\n")
        for (i in 0 until 1500) {
            lineBuilder.append("  integer val_$i = $i;\n")
            lineBuilder.append("  string msg_$i = \"Message number $i with llSay inside string\";\n")
            lineBuilder.append("  // Single line comment $i with integer and llGetPos\n")
            lineBuilder.append("  /* Multi line comment $i\n     with state_entry and TRUE */\n")
            lineBuilder.append("  if (val_$i > 0) { llSay(0, msg_$i); }\n")
        }
        lineBuilder.append(" }\n}\n")

        val largeScript = lineBuilder.toString()
        assertTrue("Large script should have over 50k characters", largeScript.length > 50_000)

        // Warm up pass
        LSLLanguage.highlight(largeScript)

        // Benchmark pass
        val startTime = System.currentTimeMillis()
        val iterations = 5
        for (i in 0 until iterations) {
            LSLLanguage.highlight(largeScript)
        }
        val elapsedTime = System.currentTimeMillis() - startTime
        val avgTime = elapsedTime / iterations.toDouble()

        assertTrue(
            "Average highlight time ($avgTime ms) should be fast (< 500 ms)",
            avgTime < 500.0
        )
    }
}
