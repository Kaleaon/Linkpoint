package com.linkpoint.scripts.lsl

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LSLTokenRegistryTest {

    @Test
    fun `token sets contain canonical LSL definitions`() {
        // Keywords
        assertTrue(LSLTokenRegistry.isKeyword("if"))
        assertTrue(LSLTokenRegistry.isKeyword("else"))
        assertTrue(LSLTokenRegistry.isKeyword("default"))
        assertTrue(LSLTokenRegistry.isKeyword("state"))

        // Types
        assertTrue(LSLTokenRegistry.isType("integer"))
        assertTrue(LSLTokenRegistry.isType("float"))
        assertTrue(LSLTokenRegistry.isType("string"))
        assertTrue(LSLTokenRegistry.isType("key"))
        assertTrue(LSLTokenRegistry.isType("vector"))
        assertTrue(LSLTokenRegistry.isType("rotation"))
        assertTrue(LSLTokenRegistry.isType("list"))

        // Events
        assertTrue(LSLTokenRegistry.isEvent("state_entry"))
        assertTrue(LSLTokenRegistry.isEvent("touch_start"))
        assertTrue(LSLTokenRegistry.isEvent("http_request"))

        // Functions
        assertTrue(LSLTokenRegistry.isFunction("llSay"))
        assertTrue(LSLTokenRegistry.isFunction("llGetPos"))
        assertTrue(LSLTokenRegistry.isFunction("llList2String"))

        // Deprecated functions
        assertTrue(LSLTokenRegistry.isDeprecated("llMakeExplosion"))
        assertTrue(LSLTokenRegistry.isDeprecated("llSound"))

        // Constants
        assertTrue(LSLTokenRegistry.isConstant("STATUS_PHYSICS"))
        assertTrue(LSLTokenRegistry.isConstant("CHANGED_INVENTORY"))
        assertTrue(LSLTokenRegistry.isConstant("NULL_KEY"))
    }

    @Test
    fun `getTokenType returns expected classification token type`() {
        assertEquals(LSLTokenType.KEYWORD, LSLTokenRegistry.getTokenType("if"))
        assertEquals(LSLTokenType.TYPE, LSLTokenRegistry.getTokenType("integer"))
        assertEquals(LSLTokenType.EVENT, LSLTokenRegistry.getTokenType("state_entry"))
        assertEquals(LSLTokenType.DEPRECATED_FUNCTION, LSLTokenRegistry.getTokenType("llMakeExplosion"))
        assertEquals(LSLTokenType.FUNCTION, LSLTokenRegistry.getTokenType("llSay"))
        assertEquals(LSLTokenType.CONSTANT, LSLTokenRegistry.getTokenType("STATUS_PHYSICS"))
        assertEquals(LSLTokenType.UNKNOWN, LSLTokenRegistry.getTokenType("myCustomVariable"))
    }

    @Test
    fun `raw ARGB hex color definitions match expected values`() {
        assertEquals(0xFFCC7832.toInt(), LSLTokenRegistry.Colors.KEYWORD)
        assertEquals(0xFF6897BB.toInt(), LSLTokenRegistry.Colors.TYPE)
        assertEquals(0xFFFFC66D.toInt(), LSLTokenRegistry.Colors.FUNCTION)
        assertEquals(0xFFB389CB.toInt(), LSLTokenRegistry.Colors.EVENT)
        assertEquals(0xFF9876AA.toInt(), LSLTokenRegistry.Colors.CONSTANT)
        assertEquals(0xFF6A8759.toInt(), LSLTokenRegistry.Colors.STRING)
        assertEquals(0xFF808080.toInt(), LSLTokenRegistry.Colors.COMMENT)
        assertEquals(0xFF6897BB.toInt(), LSLTokenRegistry.Colors.NUMBER)
        assertEquals(0xFFA9B7C6.toInt(), LSLTokenRegistry.Colors.OPERATOR)
        assertEquals(0xFFA9B7C6.toInt(), LSLTokenRegistry.Colors.DEFAULT)
        assertEquals(0xFFFF6B68.toInt(), LSLTokenRegistry.Colors.STATE)
        assertEquals(0xFFFF0000.toInt(), LSLTokenRegistry.Colors.DEPRECATED)
        assertEquals(0xFF1E1E1E.toInt(), LSLTokenRegistry.Colors.BACKGROUND)
        assertEquals(0xFF606366.toInt(), LSLTokenRegistry.Colors.LINE_NUMBER)
    }

    @Test
    fun `LSLSyntax and LSLLanguage adapters delegate seamlessly to LSLTokenRegistry`() {
        assertEquals(LSLTokenRegistry.KEYWORDS, LSLSyntax.KEYWORDS)
        assertEquals(LSLTokenRegistry.KEYWORDS, LSLLanguage.KEYWORDS)

        assertEquals(LSLTokenRegistry.TYPES, LSLSyntax.TYPES)
        assertEquals(LSLTokenRegistry.TYPES, LSLLanguage.TYPES)

        assertEquals(LSLTokenRegistry.EVENTS, LSLSyntax.EVENTS)
        assertEquals(LSLTokenRegistry.EVENTS, LSLLanguage.EVENTS)

        assertEquals(LSLTokenRegistry.CONSTANTS, LSLSyntax.CONSTANTS)
        assertEquals(LSLTokenRegistry.CONSTANTS, LSLLanguage.CONSTANTS)

        assertEquals(LSLTokenRegistry.FUNCTIONS, LSLSyntax.FUNCTIONS)
        assertEquals(LSLTokenRegistry.FUNCTIONS, LSLLanguage.FUNCTIONS)

        assertEquals(LSLTokenRegistry.DEPRECATED_FUNCTIONS, LSLSyntax.DEPRECATED_FUNCTIONS)
        assertEquals(LSLTokenRegistry.DEPRECATED_FUNCTIONS, LSLLanguage.DEPRECATED_FUNCTIONS)

        assertEquals(LSLTokenRegistry.Colors.KEYWORD, LSLSyntax.Colors.KEYWORD)
        assertEquals(LSLTokenRegistry.Colors.TYPE, LSLSyntax.Colors.TYPE)
        assertEquals(LSLTokenRegistry.Colors.FUNCTION, LSLSyntax.Colors.FUNCTION)
    }

    @Test
    fun `getAutocompleteSuggestions returns matching token suggestions sorted`() {
        val llSuggestions = LSLTokenRegistry.getAutocompleteSuggestions("ll")
        assertTrue(llSuggestions.isNotEmpty())
        assertTrue(llSuggestions.all { it.text.lowercase().startsWith("ll") })
        val llSaSuggestions = LSLTokenRegistry.getAutocompleteSuggestions("llSa")
        assertTrue(llSaSuggestions.any { it.text == "llSay" && it.type == SuggestionType.FUNCTION })

        val touchSuggestions = LSLTokenRegistry.getAutocompleteSuggestions("touch")
        assertTrue(touchSuggestions.any { it.text == "touch_start" && it.type == SuggestionType.EVENT })

        val shortPrefixSuggestions = LSLTokenRegistry.getAutocompleteSuggestions("a")
        assertTrue(shortPrefixSuggestions.isEmpty())
    }

    @Test
    fun `patterns match LSL constructs correctly`() {
        assertTrue(LSLTokenRegistry.Patterns.SINGLE_LINE_COMMENT.matcher("// comment").matches())
        assertTrue(LSLTokenRegistry.Patterns.STRING.matcher("\"hello world\"").matches())
        assertTrue(LSLTokenRegistry.Patterns.NUMBER.matcher("0xFF").matches())
        assertTrue(LSLTokenRegistry.Patterns.NUMBER.matcher("3.14").matches())
        assertTrue(LSLTokenRegistry.Patterns.VECTOR_ROTATION.matcher("<1.0, 2.0, 3.0>").matches())
    }

    @Test
    fun `range utilities function correctly`() {
        val merged = LSLTokenRegistry.mergeRanges(listOf(1..5, 3..8, 12..15))
        assertEquals(listOf(1..8, 12..15), merged)

        assertTrue(LSLTokenRegistry.isInStyledRange(5, merged))
        assertFalse(LSLTokenRegistry.isInStyledRange(10, merged))
        assertTrue(LSLTokenRegistry.isInStyledRange(14, merged))
    }
}
