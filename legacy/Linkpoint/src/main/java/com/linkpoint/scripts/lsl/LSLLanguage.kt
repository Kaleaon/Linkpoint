package com.linkpoint.scripts.lsl

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * LSL (Linden Scripting Language) syntax definition for Jetpack Compose.
 *
 * Framework adapter providing full syntax highlighting for Second Life scripts in Compose UI.
 * Delegates canonical token sets, color palette values, pattern definitions,
 * autocomplete suggestions, and range calculation to [LSLTokenRegistry].
 *
 * Based on Firestorm's LSL editor implementation.
 */
object LSLLanguage {

    // ==================== COLORS ====================

    object Colors {
        val KEYWORD = Color(LSLTokenRegistry.Colors.KEYWORD)
        val TYPE = Color(LSLTokenRegistry.Colors.TYPE)
        val FUNCTION = Color(LSLTokenRegistry.Colors.FUNCTION)
        val EVENT = Color(LSLTokenRegistry.Colors.EVENT)
        val CONSTANT = Color(LSLTokenRegistry.Colors.CONSTANT)
        val STRING = Color(LSLTokenRegistry.Colors.STRING)
        val COMMENT = Color(LSLTokenRegistry.Colors.COMMENT)
        val NUMBER = Color(LSLTokenRegistry.Colors.NUMBER)
        val OPERATOR = Color(LSLTokenRegistry.Colors.OPERATOR)
        val DEFAULT = Color(LSLTokenRegistry.Colors.DEFAULT)
        val STATE = Color(LSLTokenRegistry.Colors.STATE)
        val DEPRECATED = Color(LSLTokenRegistry.Colors.DEPRECATED)
        val BACKGROUND = Color(LSLTokenRegistry.Colors.BACKGROUND)
        val LINE_NUMBER = Color(LSLTokenRegistry.Colors.LINE_NUMBER)
    }

    // ==================== TOKEN SETS ====================

    val KEYWORDS: Set<String> get() = LSLTokenRegistry.KEYWORDS
    val TYPES: Set<String> get() = LSLTokenRegistry.TYPES
    val EVENTS: Set<String> get() = LSLTokenRegistry.EVENTS
    val CONSTANTS: Set<String> get() = LSLTokenRegistry.CONSTANTS
    val FUNCTIONS: Set<String> get() = LSLTokenRegistry.FUNCTIONS
    val DEPRECATED_FUNCTIONS: Set<String> get() = LSLTokenRegistry.DEPRECATED_FUNCTIONS

    /**
     * Highlight LSL code and return an AnnotatedString with syntax highlighting.
     */
    fun highlight(code: String): AnnotatedString {
        val builder = AnnotatedString.Builder(code)

        // 1. Highlight multi-line comments first (highest priority)
        val multiLineCommentRanges = mutableListOf<IntRange>()
        LSLTokenRegistry.Patterns.MULTI_LINE_COMMENT_REGEX.findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Colors.COMMENT, fontStyle = FontStyle.Italic),
                match.range.first,
                match.range.last + 1
            )
            multiLineCommentRanges.add(match.range)
        }

        // Merge multi-line comment ranges before matching single-line comments
        var styledRanges = LSLTokenRegistry.mergeRanges(multiLineCommentRanges)

        // 2. Highlight single-line comments
        val singleLineCommentRanges = mutableListOf<IntRange>()
        LSLTokenRegistry.Patterns.SINGLE_LINE_COMMENT_REGEX.findAll(code).forEach { match ->
            if (!LSLTokenRegistry.isInStyledRange(match.range.first, styledRanges)) {
                builder.addStyle(
                    SpanStyle(color = Colors.COMMENT, fontStyle = FontStyle.Italic),
                    match.range.first,
                    match.range.last + 1
                )
                singleLineCommentRanges.add(match.range)
            }
        }

        // Merge all comment ranges (multi-line + single-line) before matching strings
        styledRanges = LSLTokenRegistry.mergeRanges(styledRanges + singleLineCommentRanges)

        // 3. Highlight strings
        val stringRanges = mutableListOf<IntRange>()
        LSLTokenRegistry.Patterns.STRING_REGEX.findAll(code).forEach { match ->
            if (!LSLTokenRegistry.isInStyledRange(match.range.first, styledRanges)) {
                builder.addStyle(
                    SpanStyle(color = Colors.STRING),
                    match.range.first,
                    match.range.last + 1
                )
                stringRanges.add(match.range)
            }
        }

        // Merge comment and string ranges before matching numbers and identifiers
        styledRanges = LSLTokenRegistry.mergeRanges(styledRanges + stringRanges)

        // 4. Highlight numbers (hex and decimal)
        LSLTokenRegistry.Patterns.NUMBER_REGEX.findAll(code).forEach { match ->
            if (!LSLTokenRegistry.isInStyledRange(match.range.first, styledRanges)) {
                builder.addStyle(
                    SpanStyle(color = Colors.NUMBER),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }

        // 5. Highlight identifiers (keywords, types, functions, events, constants)
        LSLTokenRegistry.Patterns.IDENTIFIER_REGEX.findAll(code).forEach { match ->
            if (!LSLTokenRegistry.isInStyledRange(match.range.first, styledRanges)) {
                val word = match.value
                val style = when (LSLTokenRegistry.getTokenType(word)) {
                    LSLTokenType.KEYWORD -> SpanStyle(color = Colors.KEYWORD, fontWeight = FontWeight.Bold)
                    LSLTokenType.TYPE -> SpanStyle(color = Colors.TYPE, fontWeight = FontWeight.Bold)
                    LSLTokenType.EVENT -> SpanStyle(color = Colors.EVENT, fontWeight = FontWeight.Bold)
                    LSLTokenType.DEPRECATED_FUNCTION -> SpanStyle(color = Colors.DEPRECATED, fontStyle = FontStyle.Italic)
                    LSLTokenType.FUNCTION -> SpanStyle(color = Colors.FUNCTION)
                    LSLTokenType.CONSTANT -> SpanStyle(color = Colors.CONSTANT)
                    LSLTokenType.UNKNOWN -> null
                }

                style?.let {
                    builder.addStyle(it, match.range.first, match.range.last + 1)
                }
            }
        }

        return builder.toAnnotatedString()
    }

    /**
     * Merges overlapping or adjacent IntRanges into a sorted list of disjoint ranges.
     */
    internal fun mergeRanges(ranges: List<IntRange>): List<IntRange> {
        return LSLTokenRegistry.mergeRanges(ranges)
    }

    /**
     * Binary search to check if a position falls within any range in a sorted list of disjoint IntRanges.
     */
    internal fun isInStyledRange(position: Int, ranges: List<IntRange>): Boolean {
        return LSLTokenRegistry.isInStyledRange(position, ranges)
    }

    /**
     * Get autocomplete suggestions for the given prefix.
     */
    fun getAutocompleteSuggestions(prefix: String): List<AutocompleteSuggestion> {
        return LSLTokenRegistry.getAutocompleteSuggestions(prefix)
    }

    /**
     * Default LSL script template.
     */
    val DEFAULT_SCRIPT = """
        default
        {
            state_entry()
            {
                llSay(0, "Hello, Avatar!");
            }

            touch_start(integer total_number)
            {
                llSay(0, "Touched.");
            }
        }
    """.trimIndent()
}

/**
 * Autocomplete suggestion types.
 */
enum class SuggestionType {
    FUNCTION,
    EVENT,
    CONSTANT,
    KEYWORD,
    TYPE
}

/**
 * Autocomplete suggestion data class.
 */
data class AutocompleteSuggestion(
    val text: String,
    val type: SuggestionType
)
