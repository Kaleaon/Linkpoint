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
 * A parser for LLSD (Linden Lab Structured Data) in its JSON representation.
 *
 * This class handles the conversion of a JSON-formatted LLSD document from an
 * [InputStream] into a standard Java object structure, wrapped within an
 * [LLSD] object. It includes a basic, self-contained JSON tokenizer and
 * parser designed specifically for the conventions of LLSD.
 *
 * LLSD has special representations for certain data types within JSON:
 * - **Undefined:** `null`
 * - **Date:** A map with a single "d" key, e.g., `{"d": "2024-01-01T00:00:00Z"}`
 * - **URI:** A map with a single "u" key, e.g., `{"u": "http://example.com"}`
 * - **UUID:** A map with a single "i" key, e.g., `{"i": "550e8400-e29b-41d4-a716-446655440000"}`
 * - **Binary:** A map with a single "b" key containing base64-encoded data, e.g., `{"b": "SGVsbG8="}`
 *
 * Standard JSON types like strings, numbers, booleans, arrays, and objects are
 * mapped directly to their corresponding Java types.
 *
 * @see LLSD
 * @see LLSDJsonSerializer
 * @see [LLSD JSON Specification](http://wiki.secondlife.com/wiki/LLSD#JSON_Serialization)
 */
class LLSDJsonParser {
    
    /**
     * Parses an LLSD document from a JSON input stream.
     *
     * This method reads the entire stream, parses it as JSON, and then converts
     * the resulting structure into the appropriate LLSD object representation.
     *
     * @param jsonInput The input stream containing the JSON-formatted LLSD data.
     * @return An [LLSD] object representing the parsed data.
     * @throws IOException   if an I/O error occurs while reading from the stream.
     * @throws LLSDException if the input is not valid JSON or does not conform
     *                       to LLSD's JSON conventions (e.g., invalid date format).
     */
    @Throws(IOException::class, LLSDException::class)
    fun parse(jsonInput: InputStream): LLSD {
        // Read entire stream into string for simpler parsing
        val jsonString = InputStreamReader(jsonInput, StandardCharsets.UTF_8).use { reader ->
            reader.readText()
        }.trim()
        
        val tokenizer = JsonTokenizer(jsonString)
        val parsedJson = parseJsonValue(tokenizer)
        val llsdContent = convertJsonToLlsd(parsedJson)
        return LLSD(llsdContent)
    }
    
    /**
     * A simple, internal tokenizer for breaking a JSON string into a sequence of tokens.
     *
     * This tokenizer is not a general-purpose JSON parser but is sufficient for
     * the needs of LLSD. It handles basic JSON syntax elements like strings
     * (with escapes), numbers, literals (true, false, null), and structural
     * characters ({, }, [, ], :, ,).
     */
    private class JsonTokenizer(private val json: String) {
        private var position = 0
        
        fun skipWhitespace() {
            while (position < json.length && json[position].isWhitespace()) {
                position++
            }
        }
        
        @Throws(LLSDException::class)
        fun peek(): Char {
            skipWhitespace()
            if (position >= json.length) {
                throw LLSDException("Unexpected end of JSON input")
            }
            return json[position]
        }
        
        @Throws(LLSDException::class)
        fun consume(): Char {
            skipWhitespace()
            if (position >= json.length) {
                throw LLSDException("Unexpected end of JSON input")
            }
            return json[position++]
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
            return position < json.length
        }
        
        @Throws(LLSDException::class)
        fun consumeString(): String {
            expect('"')
            val sb = StringBuilder()
            
            while (position < json.length) {
                var c = json[position++]
                when {
                    c == '"' -> return sb.toString()
                    c == '\\' -> {
                        if (position >= json.length) {
                            throw LLSDException("Unterminated string escape")
                        }
                        c = json[position++]
                        when (c) {
                            '"', '\\', '/' -> sb.append(c)
                            'b' -> sb.append('\b')
                            'f' -> sb.append('\u000C')
                            'n' -> sb.append('\n')
                            'r' -> sb.append('\r')
                            't' -> sb.append('\t')
                            'u' -> {
                                // Unicode escape
                                if (position + 4 > json.length) {
                                    throw LLSDException("Invalid unicode escape")
                                }
                                val hex = json.substring(position, position + 4)
                                position += 4
                                try {
                                    sb.append(hex.toInt(16).toChar())
                                } catch (e: NumberFormatException) {
                                    throw LLSDException("Invalid unicode escape: $hex")
                                }
                            }
                            else -> throw LLSDException("Invalid escape character: $c")
                        }
                    }
                    else -> sb.append(c)
                }
            }
            
            throw LLSDException("Unterminated string")
        }
        
        @Throws(LLSDException::class)
        fun consumeNumber(): Any {
            val sb = StringBuilder()
            var hasDecimal = false
            
            while (position < json.length) {
                val c = json[position]
                when {
                    c.isDigit() || c == '-' || c == '+' -> {
                        sb.append(c)
                        position++
                    }
                    c == '.' && !hasDecimal -> {
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
                if (hasDecimal || numStr.contains("e", ignoreCase = true)) {
                    numStr.toDouble()
                } else {
                    numStr.toInt()
                }
            } catch (e: NumberFormatException) {
                throw LLSDException("Invalid number format: $numStr", e)
            }
        }
        
        @Throws(LLSDException::class)
        fun consumeLiteral(): Any? {
            val sb = StringBuilder()
            while (position < json.length && json[position].isLetter()) {
                sb.append(json[position++])
            }
            
            return when (val literal = sb.toString()) {
                "true" -> true
                "false" -> false
                "null" -> null
                else -> throw LLSDException("Invalid literal: $literal")
            }
        }
    }
    
    /**
     * Parses a single JSON value from the token stream.
     *
     * This is the entry point for the recursive descent parser. It looks at the
     * next token to decide whether to parse an object, array, string, number,
     * or literal.
     *
     * @param tokenizer The tokenizer providing the JSON tokens.
     * @return A Java object representing the parsed JSON value (e.g., Map, List, String).
     * @throws LLSDException if the token stream is invalid.
     */
    @Throws(LLSDException::class)
    private fun parseJsonValue(tokenizer: JsonTokenizer): Any? {
        val ch = tokenizer.peek()
        
        return when (ch) {
            '{' -> parseJsonObject(tokenizer)
            '[' -> parseJsonArray(tokenizer)
            '"' -> tokenizer.consumeString()
            't', 'f', 'n' -> tokenizer.consumeLiteral()
            else -> {
                if (ch.isDigit() || ch == '-') {
                    tokenizer.consumeNumber()
                } else {
                    throw LLSDException("Unexpected character in JSON: $ch")
                }
            }
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseJsonObject(tokenizer: JsonTokenizer): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        tokenizer.expect('{')
        
        if (tokenizer.peek() == '}') {
            tokenizer.consume() // consume '}'
            return map // Empty object
        }
        
        while (true) {
            // Parse key
            val key = tokenizer.consumeString()
            tokenizer.expect(':')
            val value = parseJsonValue(tokenizer)
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
                else -> throw LLSDException("Expected ',' or '}' in object, got: $next")
            }
        }
        
        return map
    }
    
    @Throws(LLSDException::class)
    private fun parseJsonArray(tokenizer: JsonTokenizer): List<Any?> {
        val list = mutableListOf<Any?>()
        tokenizer.expect('[')
        
        if (tokenizer.peek() == ']') {
            tokenizer.consume() // consume ']'
            return list // Empty array
        }
        
        while (true) {
            val value = parseJsonValue(tokenizer)
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
    
    /**
     * Recursively converts a parsed JSON object structure into an LLSD object structure.
     *
     * This method traverses the parsed JSON tree (composed of Maps, Lists, and
     * primitive wrappers) and transforms it into an LLSD-compatible structure.
     * It specifically looks for the special map-based encodings for types like
     * Date, URI, and UUID, and converts them accordingly.
     *
     * @param jsonObj The input object parsed from JSON.
     * @return An object compatible with LLSD data types.
     * @throws LLSDException if a special LLSD type has an invalid format.
     */
    @Throws(LLSDException::class)
    private fun convertJsonToLlsd(jsonObj: Any?): Any? {
        if (jsonObj == null) {
            return ""  // LLSD represents undefined as empty string
        }
        
        if (jsonObj is Map<*, *>) {
            @Suppress("UNCHECKED_CAST")
            val jsonMap = jsonObj as Map<String, Any?>
            
            // Check for LLSD type indicators
            if (jsonMap.size == 1) {
                val (key, value) = jsonMap.entries.first()
                
                when (key) {
                    "d" -> if (value is String) return parseDate(value)
                    "u" -> if (value is String) return parseUri(value)
                    "i" -> if (value is String) return parseUuid(value)
                    "b" -> if (value is String) return parseBinary(value)
                }
            }
            
            // Regular map - convert all values recursively
            val llsdMap = mutableMapOf<String, Any?>()
            for ((key, value) in jsonMap) {
                llsdMap[key] = convertJsonToLlsd(value)
            }
            return llsdMap
        }
        
        if (jsonObj is List<*>) {
            return jsonObj.map { convertJsonToLlsd(it) }
        }
        
        // Primitive types remain as-is
        return jsonObj
    }
    
    @Throws(LLSDException::class)
    private fun parseDate(dateStr: String): Date {
        return try {
            // Create a new DateFormat instance for thread safety
            val dateFormat = SimpleDateFormat(ISO8601_PATTERN).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            dateFormat.parse(dateStr)
        } catch (e: java.text.ParseException) {
            throw LLSDException("Invalid date format: '$dateStr'. Expected ISO 8601 format: '$ISO8601_PATTERN'", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseUri(uriStr: String): URI {
        return try {
            URI(uriStr)
        } catch (e: URISyntaxException) {
            throw LLSDException("Invalid URI format: $uriStr", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseUuid(uuidStr: String): UUID {
        if (!UUID_PATTERN.matches(uuidStr)) {
            throw LLSDException("Invalid UUID format: '$uuidStr'. Expected format: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'")
        }
        return try {
            UUID.fromString(uuidStr)
        } catch (e: IllegalArgumentException) {
            throw LLSDException("Invalid UUID: $uuidStr", e)
        }
    }
    
    @Throws(LLSDException::class)
    private fun parseBinary(base64Str: String): ByteArray {
        return try {
            Base64.getDecoder().decode(base64Str)
        } catch (e: IllegalArgumentException) {
            throw LLSDException("Invalid base64 data: $base64Str", e)
        }
    }
    
    companion object {
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        private val UUID_PATTERN = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
    }
}
