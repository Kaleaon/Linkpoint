package com.linkpoint.slproto.llsd

import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.URI
import java.net.URISyntaxException
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

object LLSDNotationParser {

    private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"

    @Throws(IOException::class, LLSDException::class)
    fun parse(notation: String): LLSD {
        val tokenizer = NotationTokenizer(notation.trim())
        val value = parseValue(tokenizer)
        if (tokenizer.hasMore()) {
            throw LLSDException("Unexpected trailing characters in notation")
        }
        return value
    }

    @Throws(IOException::class, LLSDException::class)
    fun parse(stream: InputStream): LLSD {
        val notation = InputStreamReader(stream, StandardCharsets.UTF_8).use { it.readText() }
        return parse(notation)
    }

    private class NotationTokenizer(private val source: String) {
        var position: Int = 0

        fun hasMore(): Boolean {
            skipWhitespace()
            return position < source.length
        }

        fun peek(): Char {
            skipWhitespace()
            if (position >= source.length) {
                throw LLSDException("Unexpected end of notation input")
            }
            return source[position]
        }

        fun consume(): Char {
            skipWhitespace()
            if (position >= source.length) {
                throw LLSDException("Unexpected end of notation input")
            }
            return source[position++]
        }

        fun expect(expected: Char) {
            val actual = consume()
            if (actual != expected) {
                throw LLSDException("Expected '$expected' but got '$actual'")
            }
        }

        fun consumeString(delimiter: Char): String {
            val builder = StringBuilder()
            while (position < source.length) {
                var c = source[position++]
                when {
                    c == delimiter -> return builder.toString()
                    c == '\\' -> {
                        if (position >= source.length) {
                            throw LLSDException("Unterminated escape sequence in string literal")
                        }
                        c = source[position++]
                        builder.append(
                            when (c) {
                                '\\', '\'', '"' -> c
                                'n' -> '\n'
                                'r' -> '\r'
                                't' -> '\t'
                                else -> c
                            }
                        )
                    }
                    else -> builder.append(c)
                }
            }
            throw LLSDException("Unterminated string literal in notation")
        }

        fun consumeNumber(doubleAllowed: Boolean): String {
            val start = position
            var hasDecimal = false
            while (position < source.length) {
                val c = source[position]
                if (c.isDigit() || c == '+' || c == '-') {
                    position++
                } else if (c == '.' && doubleAllowed && !hasDecimal) {
                    hasDecimal = true
                    position++
                } else if (c == 'e' || c == 'E') {
                    position++
                } else {
                    break
                }
            }
            if (start == position) {
                throw LLSDException("Expected numeric value")
            }
            return source.substring(start, position)
        }

        fun consumeUntil(vararg delimiters: Char): String {
            val builder = StringBuilder()
            val set = delimiters.toSet()
            while (position < source.length) {
                val c = source[position]
                if (c in set || c.isWhitespace()) {
                    break
                }
                builder.append(c)
                position++
            }
            return builder.toString()
        }

        fun skipWhitespace() {
            while (position < source.length && source[position].isWhitespace()) {
                position++
            }
        }
    }

    private fun parseValue(tokenizer: NotationTokenizer): LLSD {
        if (!tokenizer.hasMore()) {
            throw LLSDException("Expected value but reached end of notation")
        }
        return when (val marker = tokenizer.peek()) {
            '!' -> {
                tokenizer.consume()
                LLSD()
            }
            '1', 't', 'T' -> parseBoolean(tokenizer, true)
            '0', 'f', 'F' -> parseBoolean(tokenizer, false)
            'i' -> {
                tokenizer.consume()
                val number = tokenizer.consumeNumber(doubleAllowed = false)
                LLSD(number.toInt())
            }
            'r' -> {
                tokenizer.consume()
                val number = tokenizer.consumeNumber(doubleAllowed = true)
                LLSD(number.toDouble())
            }
            's' -> LLSD(parseString(tokenizer))
            'u' -> LLSD(parseUUID(tokenizer))
            'd' -> LLSD(parseDate(tokenizer))
            'l' -> parseURI(tokenizer)
            'b' -> LLSD(parseBinary(tokenizer))
            '[' -> parseArray(tokenizer)
            '{' -> parseMap(tokenizer)
            else -> throw LLSDException("Unexpected character '$marker' in notation")
        }
    }

    private fun parseBoolean(tokenizer: NotationTokenizer, expectedValue: Boolean): LLSD {
        val first = tokenizer.consume()
        return when (first) {
            '1' -> {
                if (!expectedValue) throw LLSDException("Unexpected boolean literal '1'")
                LLSD(true)
            }
            '0' -> {
                if (expectedValue) throw LLSDException("Unexpected boolean literal '0'")
                LLSD(false)
            }
            't', 'T', 'f', 'F' -> {
                val remainder = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
                val token = ("" + first + remainder).lowercase(Locale.US)
                when (token) {
                    "true", "t" -> LLSD(true)
                    "false", "f" -> LLSD(false)
                    else -> throw LLSDException("Invalid boolean literal: $token")
                }
            }
            else -> throw LLSDException("Invalid boolean literal start: $first")
        }
    }

    private fun parseString(tokenizer: NotationTokenizer): String {
        tokenizer.expect('s')
        val delimiter = tokenizer.consume()
        if (delimiter != '\'' && delimiter != '"') {
            throw LLSDException("Expected quote after 's' marker in notation string")
        }
        return tokenizer.consumeString(delimiter)
    }

    private fun parseUUID(tokenizer: NotationTokenizer): UUID {
        tokenizer.expect('u')
        val uuidStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
        return try {
            UUID.fromString(uuidStr)
        } catch (ex: IllegalArgumentException) {
            throw LLSDException("Invalid UUID literal: $uuidStr", ex)
        }
    }

    private fun parseDate(tokenizer: NotationTokenizer): Date {
        tokenizer.expect('d')
        val dateStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
        return try {
            SimpleDateFormat(ISO8601_PATTERN, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.parse(dateStr) ?: throw LLSDException("Invalid date literal: $dateStr")
        } catch (ex: java.text.ParseException) {
            throw LLSDException("Invalid date literal: $dateStr", ex)
        }
    }

    private fun parseURI(tokenizer: NotationTokenizer): LLSD {
        tokenizer.expect('l')
        val uriStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r')
        return try {
            URI(uriStr)
            LLSD.fromUri(uriStr)
        } catch (ex: URISyntaxException) {
            throw LLSDException("Invalid URI literal: $uriStr", ex)
        }
    }

    private fun parseBinary(tokenizer: NotationTokenizer): ByteArray {
        tokenizer.expect('b')
        val next = tokenizer.peek()
        return when {
            next.isDigit() -> {
                val prefix = tokenizer.consumeUntil('"', '\'')
                if (prefix != "64") {
                    throw LLSDException("Unsupported binary prefix: $prefix")
                }
                val delimiter = tokenizer.consume()
                if (delimiter != '"' && delimiter != '\'') {
                    throw LLSDException("Expected quote to delimit base64 data")
                }
                val data = tokenizer.consumeString(delimiter)
                try {
                    Base64.getDecoder().decode(data)
                } catch (ex: IllegalArgumentException) {
                    throw LLSDException("Invalid base64 binary literal", ex)
                }
            }
            next == '(' -> {
                tokenizer.consume()
                val sizeStr = tokenizer.consumeUntil(')')
                tokenizer.expect(')')
                val expectedSize = sizeStr.toInt()
                val delimiter = tokenizer.consume()
                if (delimiter != '"' && delimiter != '\'') {
                    throw LLSDException("Expected quote to delimit binary data")
                }
                val raw = tokenizer.consumeString(delimiter)
                val bytes = raw.toByteArray(StandardCharsets.UTF_8)
                if (bytes.size != expectedSize) {
                    throw LLSDException("Binary literal length mismatch: expected $expectedSize got ${bytes.size}")
                }
                bytes
            }
            else -> throw LLSDException("Invalid binary literal")
        }
    }

    private fun parseArray(tokenizer: NotationTokenizer): LLSD {
        val array = LLSD.emptyArray()
        tokenizer.expect('[')
        if (tokenizer.peek() == ']') {
            tokenizer.consume()
            return array
        }
        while (true) {
            val value = parseValue(tokenizer)
            array.add(value)
            when (val next = tokenizer.peek()) {
                ',' -> {
                    tokenizer.consume()
                    continue
                }
                ']' -> {
                    tokenizer.consume()
                    return array
                }
                else -> throw LLSDException("Expected ',' or ']' in array literal")
            }
        }
    }

    private fun parseMap(tokenizer: NotationTokenizer): LLSD {
        val map = LLSD.emptyMap()
        tokenizer.expect('{')
        if (tokenizer.peek() == '}') {
            tokenizer.consume()
            return map
        }
        while (true) {
            val keyToken = tokenizer.peek()
            val key = when {
                keyToken == 's' -> {
                    val saved = tokenizer.position
                    try {
                        parseString(tokenizer)
                    } catch (ex: LLSDException) {
                        tokenizer.position = saved
                        tokenizer.consumeUntil(':', ' ', '\t', '\n', '\r')
                    }
                }
                keyToken.isLetter() || keyToken == '_' -> tokenizer.consumeUntil(':', ' ', '\t', '\n', '\r')
                else -> throw LLSDException("Invalid map key")
            }
            tokenizer.expect(':')
            val value = parseValue(tokenizer)
            map[key] = value
            when (val next = tokenizer.peek()) {
                ',' -> tokenizer.consume()
                '}' -> {
                    tokenizer.consume()
                    return map
                }
                else -> throw LLSDException("Expected ',' or '}' in map literal")
            }
        }
    }
}
