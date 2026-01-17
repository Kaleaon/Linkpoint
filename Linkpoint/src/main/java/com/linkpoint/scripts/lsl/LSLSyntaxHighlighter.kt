package com.linkpoint.scripts.lsl

import java.util.regex.Pattern

/**
 * LSL Syntax Highlighter - Provides syntax highlighting for Linden Scripting Language.
 * 
 * Uses pattern matching to identify and highlight different LSL constructs.
 */
class LSLSyntaxHighlighter {
    
    /**
     * Represents a highlighted region in text.
     */
    data class HighlightedRegion(
        val startIndex: Int,
        val endIndex: Int,
        val color: Int
    )
    
    /**
     * Highlight LSL source code and return list of highlighted regions.
     */
    fun highlight(text: CharSequence, colorScheme: LSLColorScheme = LSLDarkColorScheme()): List<HighlightedRegion> {
        val highlights = mutableListOf<HighlightedRegion>()
        val textStr = text.toString()
        
        // Highlight multi-line comments first (highest priority)
        highlightPattern(textStr, LSLSyntax.PATTERNS.MULTI_LINE_COMMENT, LSLSyntax.Colors.COMMENT, highlights)
        
        // Highlight single-line comments
        highlightPattern(textStr, LSLSyntax.PATTERNS.SINGLE_LINE_COMMENT, LSLSyntax.Colors.COMMENT, highlights)
        
        // Highlight strings
        highlightPattern(textStr, LSLSyntax.PATTERNS.STRING, LSLSyntax.Colors.STRING, highlights)
        
        // Highlight numbers
        highlightPattern(textStr, LSLSyntax.PATTERNS.NUMBER, LSLSyntax.Colors.NUMBER, highlights)
        
        // Highlight identifiers (keywords, types, functions, etc.)
        val identifierMatcher = LSLSyntax.PATTERNS.IDENTIFIER.matcher(textStr)
        while (identifierMatcher.find()) {
            val word = identifierMatcher.group()
            val start = identifierMatcher.start()
            val end = identifierMatcher.end()
            
            // Skip if already highlighted (in comment or string)
            if (isInHighlightedRegion(start, highlights)) continue
            
            val color = when {
                LSLSyntax.isKeyword(word) -> LSLSyntax.Colors.KEYWORD
                LSLSyntax.isType(word) -> LSLSyntax.Colors.TYPE
                LSLSyntax.isEvent(word) -> LSLSyntax.Colors.EVENT
                LSLSyntax.isDeprecated(word) -> LSLSyntax.Colors.DEPRECATED
                LSLSyntax.isFunction(word) -> LSLSyntax.Colors.FUNCTION
                LSLSyntax.isConstant(word) -> LSLSyntax.Colors.CONSTANT
                word == "default" -> LSLSyntax.Colors.STATE
                word.startsWith("state_") -> LSLSyntax.Colors.STATE
                else -> null
            }
            
            if (color != null) {
                highlights.add(HighlightedRegion(start, end, color))
            }
        }
        
        return highlights.sortedBy { it.startIndex }
    }
    
    private fun highlightPattern(
        text: String, 
        pattern: Pattern, 
        color: Int, 
        highlights: MutableList<HighlightedRegion>
    ) {
        val matcher = pattern.matcher(text)
        while (matcher.find()) {
            highlights.add(HighlightedRegion(
                matcher.start(),
                matcher.end(),
                color
            ))
        }
    }
    
    private fun isInHighlightedRegion(
        position: Int, 
        highlights: List<HighlightedRegion>
    ): Boolean {
        return highlights.any { position >= it.startIndex && position < it.endIndex }
    }
}

/**
 * Color scheme interface for LSL syntax highlighting.
 */
interface LSLColorScheme {
    val lineNumberColor: Int
    val lineNumberBackgroundColor: Int
    val backgroundColor: Int
    val textColor: Int
}

/**
 * Dark color scheme optimized for LSL scripts.
 */
class LSLDarkColorScheme : LSLColorScheme {
    override val lineNumberColor: Int = LSLSyntax.Colors.COMMENT
    override val lineNumberBackgroundColor: Int = 0xFF1E1E1E.toInt()
    override val backgroundColor: Int = 0xFF1E1E1E.toInt()
    override val textColor: Int = LSLSyntax.Colors.DEFAULT
}

/**
 * Light color scheme for LSL scripts.
 */
class LSLLightColorScheme : LSLColorScheme {
    override val lineNumberColor: Int = 0xFF808080.toInt()
    override val lineNumberBackgroundColor: Int = 0xFFF5F5F5.toInt()
    override val backgroundColor: Int = 0xFFFFFFFF.toInt()
    override val textColor: Int = 0xFF000000.toInt()
}
