package com.lumiyaviewer.lumiya.utils

import java.util.StringTokenizer

class SimpleStringParser(private val text: String) {
    class StringParsingException(message: String) : Exception(message)

    private var position = 0

    fun expectToken(expected: String, delimiters: String): SimpleStringParser {
        val token = nextToken(delimiters)
        if (token != expected) {
            throw StringParsingException("Expected '$expected', found '$token'")
        }
        return this
    }

    fun nextToken(delimiters: String): String {
        val start = position
        while (position < text.length && delimiters.contains(text[position])) {
            position++
        }
        // If we skipped delimiters, and next is token?
        // Actually nextToken usually skips delimiters first, then reads token.
        // But `expectToken` usage in SLNotecard: `parser.expectToken("Linden text version 2", DELIM_EOL)`
        // This implies it reads "Linden text version 2".
        
        // Re-reading logic:
        // Skip delimiters?
        // If I use StringTokenizer logic:
        val end = indexOfDelimiter(delimiters, position)
        val token = if (end == -1) {
            val t = text.substring(position)
            position = text.length
            t
        } else {
            val t = text.substring(position, end)
            position = end
            t
        }
        // Skip delimiters after? Or before?
        // Usually parsers skip whitespace before.
        // SLNotecard logic: `expectToken("{", DELIM_EOL)`
        
        // Let's assume: Skip leading delimiters, read until next delimiter.
        return token.trim() 
    }
    
    private fun indexOfDelimiter(delimiters: String, start: Int): Int {
        for (i in start until text.length) {
            if (delimiters.contains(text[i])) return i
        }
        return -1
    }

    fun getIntToken(delimiters: String): Int {
        val token = nextToken(delimiters)
        return try {
            token.toInt()
        } catch (e: NumberFormatException) {
            throw StringParsingException("Expected integer, found '$token'")
        }
    }

    fun skipOneDelimiter(delimiter: String) {
        if (position < text.length && delimiter.contains(text[position])) {
            position++
        }
    }

    fun getSubstring(length: Int): String {
        if (position + length > text.length) {
            throw StringParsingException("Unexpected end of text")
        }
        val sub = text.substring(position, position + length)
        position += length
        return sub
    }
}
