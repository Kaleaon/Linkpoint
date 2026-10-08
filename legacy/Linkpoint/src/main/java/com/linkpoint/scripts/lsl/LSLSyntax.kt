package com.linkpoint.scripts.lsl

import java.util.regex.Pattern

/**
 * LSL (Linden Scripting Language) Syntax Definition
 *
 * Framework adapter providing LSL syntax highlighting definitions for Android View graphics.
 * Delegates canonical token sets, color palette values, and pattern definitions
 * to [LSLTokenRegistry].
 *
 * Based on Firestorm's LSL editor syntax highlighting.
 */
object LSLSyntax {

    // ==================== COLORS ====================

    object Colors {
        val KEYWORD: Int get() = LSLTokenRegistry.Colors.KEYWORD
        val TYPE: Int get() = LSLTokenRegistry.Colors.TYPE
        val FUNCTION: Int get() = LSLTokenRegistry.Colors.FUNCTION
        val EVENT: Int get() = LSLTokenRegistry.Colors.EVENT
        val CONSTANT: Int get() = LSLTokenRegistry.Colors.CONSTANT
        val STRING: Int get() = LSLTokenRegistry.Colors.STRING
        val COMMENT: Int get() = LSLTokenRegistry.Colors.COMMENT
        val NUMBER: Int get() = LSLTokenRegistry.Colors.NUMBER
        val OPERATOR: Int get() = LSLTokenRegistry.Colors.OPERATOR
        val DEFAULT: Int get() = LSLTokenRegistry.Colors.DEFAULT
        val STATE: Int get() = LSLTokenRegistry.Colors.STATE
        val DEPRECATED: Int get() = LSLTokenRegistry.Colors.DEPRECATED
    }

    // ==================== TOKEN SETS ====================

    val KEYWORDS: Set<String> get() = LSLTokenRegistry.KEYWORDS
    val TYPES: Set<String> get() = LSLTokenRegistry.TYPES
    val EVENTS: Set<String> get() = LSLTokenRegistry.EVENTS
    val CONSTANTS: Set<String> get() = LSLTokenRegistry.CONSTANTS
    val FUNCTIONS: Set<String> get() = LSLTokenRegistry.FUNCTIONS
    val DEPRECATED_FUNCTIONS: Set<String> get() = LSLTokenRegistry.DEPRECATED_FUNCTIONS

    // ==================== PATTERNS ====================

    object Patterns {
        val STRING: Pattern get() = LSLTokenRegistry.Patterns.STRING
        val SINGLE_LINE_COMMENT: Pattern get() = LSLTokenRegistry.Patterns.SINGLE_LINE_COMMENT
        val MULTI_LINE_COMMENT: Pattern get() = LSLTokenRegistry.Patterns.MULTI_LINE_COMMENT
        val NUMBER: Pattern get() = LSLTokenRegistry.Patterns.NUMBER
        val IDENTIFIER: Pattern get() = LSLTokenRegistry.Patterns.IDENTIFIER
        val OPERATOR: Pattern get() = LSLTokenRegistry.Patterns.OPERATOR
        val VECTOR_ROTATION: Pattern get() = LSLTokenRegistry.Patterns.VECTOR_ROTATION
    }

    /**
     * Check if a word is an LSL keyword.
     */
    fun isKeyword(word: String): Boolean = LSLTokenRegistry.isKeyword(word)

    /**
     * Check if a word is an LSL type.
     */
    fun isType(word: String): Boolean = LSLTokenRegistry.isType(word)

    /**
     * Check if a word is an LSL event.
     */
    fun isEvent(word: String): Boolean = LSLTokenRegistry.isEvent(word)

    /**
     * Check if a word is an LSL constant.
     */
    fun isConstant(word: String): Boolean = LSLTokenRegistry.isConstant(word)

    /**
     * Check if a word is an LSL function.
     */
    fun isFunction(word: String): Boolean = LSLTokenRegistry.isFunction(word)

    /**
     * Check if a function is deprecated.
     */
    fun isDeprecated(word: String): Boolean = LSLTokenRegistry.isDeprecated(word)

    /**
     * Get all LSL function names for autocomplete.
     */
    fun getAllFunctions(): List<String> = LSLTokenRegistry.getAllFunctions()

    /**
     * Get all LSL constants for autocomplete.
     */
    fun getAllConstants(): List<String> = LSLTokenRegistry.getAllConstants()

    /**
     * Get all LSL events for autocomplete.
     */
    fun getAllEvents(): List<String> = LSLTokenRegistry.getAllEvents()
}
