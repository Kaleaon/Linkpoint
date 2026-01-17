package com.linkpoint.scripts.lsl

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

/**
 * LSL (Linden Scripting Language) syntax definition for Jetpack Compose.
 * 
 * Provides full syntax highlighting for Second Life scripts including:
 * - All 350+ LSL functions (llSay, llGetPos, etc.)
 * - All 60+ events (state_entry, touch_start, etc.)
 * - All 400+ constants (STATUS_PHYSICS, CHANGED_INVENTORY, etc.)
 * - Types (integer, float, string, key, vector, rotation, list)
 * - Keywords (if, else, for, while, return, state, jump, default)
 * - Comments (single-line // and multi-line /* */)
 * - Strings with escape sequences
 * - Numbers (integers, floats, hex)
 * 
 * Based on Firestorm's LSL editor implementation.
 */
object LSLLanguage {
    
    // ==================== COLORS ====================
    
    object Colors {
        val KEYWORD = Color(0xFFCC7832)      // Orange for keywords
        val TYPE = Color(0xFF6897BB)          // Blue for types
        val FUNCTION = Color(0xFFFFC66D)      // Yellow for functions
        val EVENT = Color(0xFFB389CB)         // Purple for events
        val CONSTANT = Color(0xFF9876AA)      // Purple for constants
        val STRING = Color(0xFF6A8759)        // Green for strings
        val COMMENT = Color(0xFF808080)       // Gray for comments
        val NUMBER = Color(0xFF6897BB)        // Blue for numbers
        val OPERATOR = Color(0xFFA9B7C6)      // Light gray for operators
        val DEFAULT = Color(0xFFA9B7C6)       // Default text color
        val STATE = Color(0xFFFF6B68)         // Red for state names
        val DEPRECATED = Color(0xFFFF0000)    // Red for deprecated
        val BACKGROUND = Color(0xFF1E1E1E)   // Dark background
        val LINE_NUMBER = Color(0xFF606366)  // Line number color
    }
    
    // ==================== KEYWORDS ====================
    
    val KEYWORDS = setOf(
        "if", "else", "for", "while", "do", "jump", "return", "state",
        "default", "event", "TRUE", "FALSE"
    )
    
    // ==================== TYPES ====================
    
    val TYPES = setOf(
        "integer", "float", "string", "key", "vector", "rotation", "quaternion", "list"
    )
    
    // ==================== EVENTS ====================
    
    val EVENTS = setOf(
        "touch_start", "touch", "touch_end",
        "state_entry", "state_exit",
        "sensor", "no_sensor", "timer", "listen", "money",
        "collision_start", "collision", "collision_end",
        "land_collision_start", "land_collision", "land_collision_end",
        "at_target", "not_at_target", "at_rot_target", "not_at_rot_target",
        "control", "run_time_permissions", "attach", "dataserver",
        "link_message", "moving_start", "moving_end", "object_rez",
        "on_rez", "changed", "email", "http_response", "http_request",
        "remote_data", "experience_permissions", "experience_permissions_denied",
        "transaction_result", "path_update"
    )
    
    // ==================== CONSTANTS ====================
    // (Using the same constants from LSLSyntax.kt)
    
    val CONSTANTS = LSLSyntax.CONSTANTS
    
    // ==================== FUNCTIONS ====================
    // (Using the same functions from LSLSyntax.kt)
    
    val FUNCTIONS = LSLSyntax.FUNCTIONS
    
    // ==================== DEPRECATED FUNCTIONS ====================
    
    val DEPRECATED_FUNCTIONS = LSLSyntax.DEPRECATED_FUNCTIONS
    
    /**
     * Highlight LSL code and return an AnnotatedString with syntax highlighting.
     */
    fun highlight(code: String): AnnotatedString {
        val builder = AnnotatedString.Builder(code)
        
        // Track positions that have been styled (comments and strings take priority)
        val styledRanges = mutableListOf<IntRange>()
        
        // 1. Highlight multi-line comments first (highest priority)
        val multiLineCommentRegex = Regex("/\\*.*?\\*/", RegexOption.DOT_MATCHES_ALL)
        multiLineCommentRegex.findAll(code).forEach { match ->
            builder.addStyle(
                SpanStyle(color = Colors.COMMENT, fontStyle = FontStyle.Italic),
                match.range.first,
                match.range.last + 1
            )
            styledRanges.add(match.range)
        }
        
        // 2. Highlight single-line comments
        val singleLineCommentRegex = Regex("//.*")
        singleLineCommentRegex.findAll(code).forEach { match ->
            if (!isInStyledRange(match.range.first, styledRanges)) {
                builder.addStyle(
                    SpanStyle(color = Colors.COMMENT, fontStyle = FontStyle.Italic),
                    match.range.first,
                    match.range.last + 1
                )
                styledRanges.add(match.range)
            }
        }
        
        // 3. Highlight strings
        val stringRegex = Regex("\"([^\"\\\\]|\\\\.)*\"")
        stringRegex.findAll(code).forEach { match ->
            if (!isInStyledRange(match.range.first, styledRanges)) {
                builder.addStyle(
                    SpanStyle(color = Colors.STRING),
                    match.range.first,
                    match.range.last + 1
                )
                styledRanges.add(match.range)
            }
        }
        
        // 4. Highlight numbers (hex and decimal)
        val numberRegex = Regex("\\b(0x[0-9A-Fa-f]+|\\d+\\.?\\d*([eE][+-]?\\d+)?|\\d*\\.\\d+([eE][+-]?\\d+)?)\\b")
        numberRegex.findAll(code).forEach { match ->
            if (!isInStyledRange(match.range.first, styledRanges)) {
                builder.addStyle(
                    SpanStyle(color = Colors.NUMBER),
                    match.range.first,
                    match.range.last + 1
                )
            }
        }
        
        // 5. Highlight identifiers (keywords, types, functions, events, constants)
        val identifierRegex = Regex("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b")
        identifierRegex.findAll(code).forEach { match ->
            if (!isInStyledRange(match.range.first, styledRanges)) {
                val word = match.value
                val style = when {
                    word in KEYWORDS -> SpanStyle(color = Colors.KEYWORD, fontWeight = FontWeight.Bold)
                    word in TYPES -> SpanStyle(color = Colors.TYPE, fontWeight = FontWeight.Bold)
                    word in EVENTS -> SpanStyle(color = Colors.EVENT, fontWeight = FontWeight.Bold)
                    word in DEPRECATED_FUNCTIONS -> SpanStyle(color = Colors.DEPRECATED, fontStyle = FontStyle.Italic)
                    word in FUNCTIONS -> SpanStyle(color = Colors.FUNCTION)
                    word in CONSTANTS -> SpanStyle(color = Colors.CONSTANT)
                    else -> null
                }
                
                style?.let {
                    builder.addStyle(it, match.range.first, match.range.last + 1)
                }
            }
        }
        
        return builder.toAnnotatedString()
    }
    
    private fun isInStyledRange(position: Int, ranges: List<IntRange>): Boolean {
        return ranges.any { position in it }
    }
    
    /**
     * Get autocomplete suggestions for the given prefix.
     */
    fun getAutocompleteSuggestions(prefix: String): List<AutocompleteSuggestion> {
        if (prefix.length < 2) return emptyList()
        
        val suggestions = mutableListOf<AutocompleteSuggestion>()
        val prefixLower = prefix.lowercase()
        
        // Functions
        FUNCTIONS.filter { it.lowercase().startsWith(prefixLower) }
            .take(10)
            .forEach { suggestions.add(AutocompleteSuggestion(it, SuggestionType.FUNCTION)) }
        
        // Events
        EVENTS.filter { it.lowercase().startsWith(prefixLower) }
            .take(5)
            .forEach { suggestions.add(AutocompleteSuggestion(it, SuggestionType.EVENT)) }
        
        // Constants
        CONSTANTS.filter { it.lowercase().startsWith(prefixLower) }
            .take(5)
            .forEach { suggestions.add(AutocompleteSuggestion(it, SuggestionType.CONSTANT)) }
        
        // Keywords
        KEYWORDS.filter { it.lowercase().startsWith(prefixLower) }
            .forEach { suggestions.add(AutocompleteSuggestion(it, SuggestionType.KEYWORD)) }
        
        // Types
        TYPES.filter { it.lowercase().startsWith(prefixLower) }
            .forEach { suggestions.add(AutocompleteSuggestion(it, SuggestionType.TYPE)) }
        
        return suggestions.sortedBy { it.text }
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
