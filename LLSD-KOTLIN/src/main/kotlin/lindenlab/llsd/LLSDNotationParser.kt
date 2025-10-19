/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

/**
 * A parser for LLSD (Linden Lab Structured Data) in its notation format.
 *
 * This class is responsible for converting a text-based LLSD notation document
 * from an [InputStream] into a standard Java object structure, wrapped in
 * an [LLSD] object. The notation format is a compact, human-readable
 * representation that resembles programming language literals.
 *
 * The format uses single-character prefixes to denote types:
 * - `!` - Undefined
 * - `1` or `true` - Boolean true
 * - `0` or `false` - Boolean false
 * - `i` - Integer (e.g., `i42`)
 * - `r` - Real/Double (e.g., `r3.14`)
 * - `s` - String (e.g., `s'hello'` or `s"hello"`)
 * - `u` - UUID (e.g., `u...`)
 * - `d` - Date (e.g., `d2024-01-01T00:00:00Z`)
 * - `l` - URI (e.g., `lhttp://example.com`)
 * - `b` - Binary (e.g., `b64"SGVsbG8="`)
 * - `[...]` - Array
 * - `{...}` - Map
 *
 * This parser includes a self-contained tokenizer and a recursive-descent parser
 * to interpret this format.
 *
 * @see LLSD
 * @see LLSDNotationSerializer
 * @see [LLSD Notation Specification](http://wiki.secondlife.com/wiki/LLSD#Notation_Serialization)
 */
class LLSDNotationParser {
    
    /**
     * Parses an LLSD document from a notation-formatted input stream.
     *
     * This is the main entry point for parsing. It reads the entire stream,
     * tokenizes the content, and recursively parses the notation structure.
     *
     * @param notationInput The input stream containing the notation-formatted data.
     * @return An [LLSD] object representing the parsed data.
     * @throws IOException   if an I/O error occurs while reading from the stream.
     * @throws LLSDException if the input is not valid LLSD notation (e.g., syntax
     *                       error, invalid value format).
     */
    @Throws(IOException::class, LLSDException::class)
    fun parse(notationInput: InputStream): LLSD {
        // Read entire stream into string for simpler parsing
        val notationString = InputStreamReader(notationInput, StandardCharsets.UTF_8).use { reader ->
            reader.readText()
        }.trim()
        
        val tokenizer = NotationTokenizer(notationString)
        val parsedNotation = parseNotationValue(tokenizer)
        return LLSD(parsedNotation)
    }
    
    /**
     * A simple, internal tokenizer for breaking LLSD notation into a sequence of tokens.
     *
     * This class is designed to handle the specific syntax of LLSD notation,
     * including type markers, delimited strings, and structural characters.
     */
    private class NotationTokenizer(private val notation: String) {
        var position = 0
        
        fun skipWhitespace() {
            while (position < notation.length && notation[position].isWhitespace()) {
                position++
            }
        }
        
        @Throws(LLSDException::class)
        fun peek(): Char {
            skipWhitespace()
            if (position >= notation.length) {
                throw LLSDException("Unexpected end of notation input")
            }
            return notation[position]
        }
        
        @Throws(LLSDException::class)
        fun consume(): Char {
            skipWhitespace()
            if (position >= notation.length) {
                throw LLSDException("Unexpected end of notation input")
            }
            return notation[position++]
        }
        
        @Throws(LLSDException::class)
        fun expect(expected: Char) {
            val actual = consume()
            if (actual != expected) {
                throw LLSDException("Expected '$expected' but got '$actual'")
            }
        }
        
        fun hasMore(): Boolean {
            skipWhitespace()
            return position < notation.length
        }
        
        @Throws(LLSDException::class)
        fun consumeString(delimiter: Char): String {
            val sb = StringBuilder()
            
            while (position < notation.length) {
                var c = notation[position++]
                when {
                    c == delimiter -> return sb.toString()
                    c == '\\' -> {
                        if (position >= notation.length) {
                            throw LLSDException("Unterminated string escape")
                        }
                        c = notation[position++]
                        when (c) {
                            '"', '\'', '\\' -> sb.append(c)
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            else -> sb.append('\\').append(c) // Keep original if not recognized
                        }
                    }
                    else -> sb.append(c)
                }
            }
            
            throw LLSDException("Unterminated string")
        }
        
        @Throws(LLSDException::class)
        fun consumeNumber(typeMarker: Char): Any {
            val sb = StringBuilder()
            var hasDecimal = false
            
            while (position < notation.length) {
                val c = notation[position]
                when {
                    c.isDigit() || c == '-' || c == '+' -> {
                        sb.append(c)
                        position++
                    }
                    c == '.' && !hasDecimal && typeMarker == 'r' -> {
                        hasDecimal = true
                        sb.append(c)
                        position++
                    }
                    c == 'e' || c == 'E' -> {
                        sb.append(c)
                        position++
                    }
                    else -> break
                }
            }
            
            val numStr = sb.toString()
            return try {
                when (typeMarker) {
                    'i' -> numStr.toInt()
                    'r' -> numStr.toDouble()
                    else -> throw LLSDException("Unknown number type marker: $typeMarker")
                }
            } catch (e: NumberFormatException) {
                throw LLSDException("Invalid number format: $numStr", e)
            }
        }
        
        fun consumeUntil(vararg delimiters: Char): String {
            val sb = StringBuilder()
            val delimiterSet = delimiters.toSet()
            
            while (position < notation.length) {
                val c = notation[position]
                if (c in delimiterSet || c.isWhitespace()) {
                    break
                }
                sb.append(c)
                position++
            }
            
            return sb.toString()
        }
    }
    
    /**
     * Parses a single LLSD value from the token stream based on its type marker.
     *
     * This is the core of the recursive descent parser. It inspects the next
     * character to determine which type of value to parse and delegates to the
     * appropriate helper method.
     *
     * @param tokenizer The tokenizer providing the notation tokens.
     * @return A Java object representing the parsed value.
     * @throws LLSDException if an unknown type marker or syntax error is found.
     */
    @Throws(LLSDException::class)
    private fun parseNotationValue(tokenizer: NotationTokenizer): Any? {
        if (!tokenizer.hasMore()) {
            throw LLSDException("Expected value but found end of input")
        }
        
        return when (val ch = tokenizer.peek()) {
            '!' -> {
                tokenizer.consume() // consume '!'
                "" // Undefined value represented as empty string
            }
            '1', 't', 'T' -> parseBoolean(tokenizer, true)
            '0', 'f', 'F' -> parseBoolean(tokenizer, false)
            'i' -> {
                tokenizer.consume() // consume 'i'
                tokenizer.consumeNumber('i')
            }
            'r' -> {
                tokenizer.consume() // consume 'r'
                tokenizer.consumeNumber('r')
            }
            's' -> parseString(tokenizer)
            'u' -> parseUUID(tokenizer)
            'd' -> parseDate(tokenizer)
            'l' -> parseURI(tokenizer)
            'b' -> parseBinary(tokenizer)
            '[' -> parseArray(tokenizer)
            '{' -> parseMap(tokenizer)
            else -> throw LLSDException("Unexpected character in notation: $ch")
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseBoolean(tokenizer: NotationTokenizer, expectedValue: Boolean): Boolean {
        val ch = tokenizer.consume()
        
        return when {
            ch == '1' && expectedValue -> true
            ch == '0' && !expectedValue -> false
            ch == 't' || ch == 'T' -> {
                // Check for full word
                val remaining = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
                val fullWord = ch + remaining
                if (fullWord.equals("true", ignoreCase = true) || fullWord.equals("t", ignoreCase = true)) {
                    true
                } else {
                    throw LLSDException("Invalid boolean value: $fullWord")
                }
            }
            ch == 'f' || ch == 'F' -> {
                // Check for full word
                val remaining = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
                val fullWord = ch + remaining
                if (fullWord.equals("false", ignoreCase = true) || fullWord.equals("f", ignoreCase = true)) {
                    false
                } else {
                    throw LLSDException("Invalid boolean value: $fullWord")
                }
            }
            else -> throw LLSDException("Invalid boolean notation: $ch")
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseString(tokenizer: NotationTokenizer): String {
        tokenizer.expect('s') // consume 's'
        val delimiter = tokenizer.peek()
        return if (delimiter == '\'' || delimiter == '"') {
            tokenizer.consume() // consume the delimiter
            tokenizer.consumeString(delimiter)
        } else {
            throw LLSDException("Expected string delimiter (' or \") after 's' but got: $delimiter")
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseUUID(tokenizer: NotationTokenizer): UUID {
        tokenizer.expect('u') // consume 'u'
        val uuidStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
        
        val uuidPattern = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
        if (!uuidPattern.matches(uuidStr)) {
            throw LLSDException("Invalid UUID format: '$uuidStr'. Expected format: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'")
        }
        
        return try {
            UUID.fromString(uuidStr)
        } catch (e: IllegalArgumentException) {
            throw LLSDException("Invalid UUID: $uuidStr", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseDate(tokenizer: NotationTokenizer): Date {
        tokenizer.expect('d') // consume 'd'
        val dateStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
        
        return try {
            // Create a new DateFormat instance for thread safety
            val dateFormat = SimpleDateFormat(ISO8601_PATTERN).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            dateFormat.parse(dateStr)
        } catch (e: java.text.ParseException) {
            throw LLSDException("Invalid date format: $dateStr", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseURI(tokenizer: NotationTokenizer): URI {
        tokenizer.expect('l') // consume 'l'
        val uriStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
        
        return try {
            URI(uriStr)
        } catch (e: URISyntaxException) {
            throw LLSDException("Invalid URI format: $uriStr", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseBinary(tokenizer: NotationTokenizer): ByteArray {
        tokenizer.expect('b') // consume 'b'
        
        // Check for size specification b64"data" or b(size)"data"
        val next = tokenizer.peek()
        when {
            next.isDigit() -> {
                // Handle b64"data" format
                val sizeStr = tokenizer.consumeUntil('"', '\'')
                if (sizeStr == "64") {
                    val delimiter = tokenizer.consume() // consume quote
                    if (delimiter == '"' || delimiter == '\'') {
                        val base64Data = tokenizer.consumeString(delimiter)
                        return try {
                            Base64.getDecoder().decode(base64Data)
                        } catch (e: IllegalArgumentException) {
                            throw LLSDException("Invalid base64 data: $base64Data", e)
                        }
                    } else {
                        throw LLSDException("Expected quote delimiter after b64 but got: $delimiter")
                    }
                } else {
                    throw LLSDException("Unsupported binary size specification: $sizeStr")
                }
            }
            next == '(' -> {
                // Handle b(size)"data" format  
                tokenizer.consume() // consume '('
                val sizeStr = tokenizer.consumeUntil(')')
                tokenizer.expect(')')
                val size = sizeStr.toInt()
                
                val delimiter = tokenizer.consume() // should be quote
                if (delimiter == '"' || delimiter == '\'') {
                    val binaryData = tokenizer.consumeString(delimiter)
                    
                    if (size != binaryData.length) {
                        throw LLSDException("Binary size mismatch: expected $size but got ${binaryData.length}")
                    }
                    
                    return binaryData.toByteArray(StandardCharsets.UTF_8)
                } else {
                    throw LLSDException("Expected quote delimiter after size specification but got: $delimiter")
                }
            }
            else -> throw LLSDException("Invalid binary notation format - expected digit or '(' after 'b' but got: $next")
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseArray(tokenizer: NotationTokenizer): List<Any?> {
        val list = mutableListOf<Any?>()
        tokenizer.expect('[')
        
        if (tokenizer.peek() == ']') {
            tokenizer.consume() // consume ']'
            return list // Empty array
        }
        
        while (true) {
            val value = parseNotationValue(tokenizer)
            list.add(value)
            
            when (val next = tokenizer.peek()) {
                ']' -> {
                    tokenizer.consume()
                    break
                }
                ',' -> {
                    tokenizer.consume()
                    continue
                }
                else -> throw LLSDException("Expected ',' or ']' in array, got: $next")
            }
        }
        
        return list
    }
    
    @Throws(LLSDException::class)
    private fun parseMap(tokenizer: NotationTokenizer): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        tokenizer.expect('{')
        
        if (tokenizer.peek() == '}') {
            tokenizer.consume() // consume '}'
            return map // Empty map
        }
        
        while (true) {
            // Parse key (should be an identifier or string)
            val key: String
            val keyStart = tokenizer.peek()
            
            key = when {
                keyStart == 's' -> {
                    // This could be a string literal `s'...'` or an identifier like `status`.
                    // We can try to parse it as a string and if that fails, treat it as an identifier.
                    val savedPos = tokenizer.position
                    try {
                        parseString(tokenizer)
                    } catch (e: LLSDException) {
                        // It's not a string literal, so it must be an identifier.
                        tokenizer.position = savedPos // backtrack
                        tokenizer.consumeUntil(':', ' ', '\t', '\n', '\r')
                    }
                }
                keyStart.isLetter() || keyStart == '_' -> {
                    // Regular identifier key
                    tokenizer.consumeUntil(':', ' ', '\t', '\n', '\r')
                }
                else -> throw LLSDException("Invalid map key format, got: $keyStart")
            }
            
            tokenizer.expect(':')
            val value = parseNotationValue(tokenizer)
            map[key] = value
            
            when (val next = tokenizer.peek()) {
                '}' -> {
                    tokenizer.consume()
                    break
                }
                ',' -> {
                    tokenizer.consume()
                    continue
                }
                else -> throw LLSDException("Expected ',' or '}' in map, got: $next")
            }
        }
        
        return map
    }
    
    companion object {
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }
}
