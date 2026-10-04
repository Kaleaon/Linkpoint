package com.linkpoint.protocol.llsd

import java.io.BufferedInputStream
import java.io.InputStream
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.Base64
import java.util.UUID

/**
 * LLSD Notation format parser.
 *
 * Notation is the human-readable LLSD form, used by some LL debug
 * endpoints and the `EnvironmentSettings` cap. Spec:
 * https://wiki.secondlife.com/wiki/LLSD#Notation_Serialization
 *
 * Tokens (terminator characters in parentheses):
 *
 *   `!`                          undef
 *   `1` / `0`                    boolean (also `true|false|T|F|t|f|TRUE|FALSE`)
 *   `i<int>`                     integer (decimal text terminated by any non-digit)
 *   `r<real>`                    real (`r3.14159`, `rNaN`, `rInf`)
 *   `u<uuid>`                    uuid (36 chars: 8-4-4-4-12 hex)
 *   `"..."` / `'...'`            string with C-style escapes (`\n \t \r \\ \"`)
 *   `s(N)"...."`                 explicit-length string, exactly N raw bytes
 *   `l"escaped-uri"`             URI (string-escaped, NOT %-encoded)
 *   `d"YYYY-MM-DDTHH:MM:SS.fffZ"` date
 *   `b16"hexpairs"` / `b64"b64"` binary, hex or base-64
 *   `b(N)"....."`                explicit-length binary, exactly N raw bytes
 *   `{ "key":value, "key":value }` map (whitespace tolerant)
 *   `[ value, value ]`           array (whitespace tolerant)
 *
 * Optional `<?llsd/notation?>\n` magic header is tolerated and skipped.
 *
 * Implementation strategy: direct byte-stream / byte-cursor reader.
 */
internal object LLSDNotationParser {

    private const val TAG = "LLSDNotationParser"
    private const val MAGIC = "<?llsd/notation?>"
    private const val MAX_DEPTH = 128
    private const val MAX_STRING_BYTES = 1024 * 1024
    private const val MAX_BINARY_BYTES = 1024 * 1024
    private const val MAX_COLLECTION_ELEMENTS = 20_000

    private val ISO_8601_MS: DateTimeFormatter = DateTimeFormatterBuilder()
        .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
        .appendFraction(ChronoField.NANO_OF_SECOND, 1, 9, true)
        .appendLiteral('Z')
        .toFormatter()
        .withZone(ZoneOffset.UTC)

    private val ISO_8601_S: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").withZone(ZoneOffset.UTC)

    fun parse(bytes: ByteArray): LLSDValue {
        if (bytes.isEmpty()) return LLSDUndefined
        return parse(java.io.ByteArrayInputStream(bytes))
    }

    fun parse(input: InputStream): LLSDValue {
        val stream = if (input is BufferedInputStream) input else BufferedInputStream(input, 65536)
        skipMagicStream(stream)
        val cursor = ByteCursor(stream)
        return try {
            cursor.skipWhitespace()
            parseValue(cursor, depth = 0)
        } catch (e: Exception) {
            LLSDUndefined
        }
    }

    private fun skipMagicStream(stream: InputStream) {
        if (!stream.markSupported()) return
        val magicBytes = MAGIC.toByteArray(Charsets.US_ASCII)
        stream.mark(magicBytes.size + 2)
        val buf = ByteArray(magicBytes.size)
        var readTotal = 0
        while (readTotal < magicBytes.size) {
            val r = stream.read(buf, readTotal, magicBytes.size - readTotal)
            if (r == -1) break
            readTotal += r
        }
        if (readTotal == magicBytes.size && buf.contentEquals(magicBytes)) {
            // Magic matches. Consume optional \r and \n.
            stream.mark(2)
            val r1 = stream.read()
            if (r1 == '\r'.code) {
                val r2 = stream.read()
                if (r2 != '\n'.code && r2 != -1) {
                    stream.reset()
                    stream.skip(1)
                }
            } else if (r1 != '\n'.code && r1 != -1) {
                stream.reset()
            }
        } else {
            stream.reset()
        }
    }

    private class ByteCursor(private val input: InputStream) {
        private var peeked: Int = -2
        var position: Int = 0
            private set

        fun atEnd(): Boolean {
            if (peeked == -2) {
                peeked = input.read()
            }
            return peeked == -1
        }

        fun peek(): Int {
            if (peeked == -2) {
                peeked = input.read()
            }
            return peeked
        }

        fun read(): Int {
            val b = if (peeked != -2) {
                val temp = peeked
                peeked = -2
                temp
            } else {
                input.read()
            }
            if (b != -1) {
                position++
            }
            return b
        }

        fun expect(c: Char) {
            val b = read()
            if (b != c.code) {
                val actual = if (b == -1) "<eof>" else b.toChar().toString()
                throw IllegalStateException("expected '$c' at $position, got '$actual'")
            }
        }

        fun skipWhitespace() {
            while (!atEnd()) {
                val p = peek()
                if (p == ' '.code || p == '\t'.code || p == '\n'.code || p == '\r'.code) {
                    read()
                } else {
                    break
                }
            }
        }

        fun readExact(n: Int): ByteArray {
            val bytes = ByteArray(n)
            var count = 0
            while (count < n) {
                val b = read()
                if (b == -1) throw IllegalStateException("truncated payload while reading $n bytes")
                bytes[count++] = b.toByte()
            }
            return bytes
        }
    }

    private fun parseValue(c: ByteCursor, depth: Int): LLSDValue {
        if (depth > MAX_DEPTH) throw IllegalStateException("LLSD notation too deeply nested")
        c.skipWhitespace()
        if (c.atEnd()) return LLSDUndefined
        val ch = c.peek().toChar()
        return when (ch) {
            '!' -> { c.read(); LLSDUndefined }
            '1' -> { c.read(); LLSDBoolean(true) }
            '0' -> { c.read(); LLSDBoolean(false) }
            'T', 't' -> parseTrueWord(c)
            'F', 'f' -> parseFalseWord(c)
            'i' -> { c.read(); LLSDInteger(parseDigits(c).toIntOrNull() ?: 0) }
            'r' -> { c.read(); LLSDReal(parseRealBody(c)) }
            'u' -> { c.read(); LLSDUUID(parseUuidBody(c)) }
            '"', '\'' -> { val q = c.read().toChar(); LLSDString(parseDelimitedString(c, q)) }
            's' -> { c.read(); LLSDString(parseExplicitLengthString(c)) }
            'l' -> { c.read(); val q = c.read().toChar(); LLSDURI(parseDelimitedString(c, q)) }
            'd' -> { c.read(); val q = c.read().toChar(); LLSDDate(parseDate(parseDelimitedString(c, q))) }
            'b' -> { c.read(); parseBinary(c) }
            '{' -> { c.read(); parseMap(c, depth) }
            '[' -> { c.read(); parseArray(c, depth) }
            else -> throw IllegalStateException("unexpected '$ch' at ${c.position}")
        }
    }

    private fun parseTrueWord(c: ByteCursor): LLSDBoolean {
        val p = c.peek()
        if (p == 't'.code || p == 'T'.code) {
            c.read()
            val nextP = c.peek()
            if (nextP == 'r'.code || nextP == 'R'.code) {
                c.read()
                val b3 = c.read()
                val b4 = c.read()
                if ((b3 == 'u'.code || b3 == 'U'.code) && (b4 == 'e'.code || b4 == 'E'.code)) {
                    return LLSDBoolean(true)
                }
                throw IllegalStateException("invalid true word at ${c.position}")
            }
            return LLSDBoolean(true)
        }
        throw IllegalStateException("not a true literal at ${c.position}")
    }

    private fun parseFalseWord(c: ByteCursor): LLSDBoolean {
        val p = c.peek()
        if (p == 'f'.code || p == 'F'.code) {
            c.read()
            val nextP = c.peek()
            if (nextP == 'a'.code || nextP == 'A'.code) {
                c.read()
                val b3 = c.read()
                val b4 = c.read()
                val b5 = c.read()
                if ((b3 == 'l'.code || b3 == 'L'.code) &&
                    (b4 == 's'.code || b4 == 'S'.code) &&
                    (b5 == 'e'.code || b5 == 'E'.code)) {
                    return LLSDBoolean(false)
                }
                throw IllegalStateException("invalid false word at ${c.position}")
            }
            return LLSDBoolean(false)
        }
        throw IllegalStateException("not a false literal at ${c.position}")
    }

    private fun parseDigits(c: ByteCursor): String {
        val sb = java.lang.StringBuilder()
        val p = c.peek()
        if (p == '-'.code || p == '+'.code) {
            sb.append(c.read().toChar())
        }
        while (!c.atEnd()) {
            val b = c.peek()
            if (b in '0'.code..'9'.code) {
                sb.append(c.read().toChar())
            } else {
                break
            }
        }
        return sb.toString()
    }

    private fun parseRealBody(c: ByteCursor): Double {
        val p = c.peek()
        if (p == 'N'.code || p == 'n'.code) {
            val b1 = c.read()
            val b2 = c.read()
            val b3 = c.read()
            if ((b1 == 'N'.code || b1 == 'n'.code) &&
                (b2 == 'a'.code || b2 == 'A'.code) &&
                (b3 == 'N'.code || b3 == 'n'.code)) {
                return Double.NaN
            }
            throw IllegalStateException("invalid real value starting with $b1")
        }
        val sb = java.lang.StringBuilder()
        if (p == '-'.code || p == '+'.code) {
            sb.append(c.read().toChar())
        }
        val nextP = c.peek()
        if (nextP == 'I'.code || nextP == 'i'.code) {
            val b1 = c.read()
            val b2 = c.read()
            val b3 = c.read()
            if ((b1 == 'I'.code || b1 == 'i'.code) &&
                (b2 == 'n'.code || b2 == 'N'.code) &&
                (b3 == 'f'.code || b3 == 'F'.code)) {
                return if (sb.startsWith("-")) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
            }
            throw IllegalStateException("invalid real value Inf")
        }
        while (!c.atEnd() && c.peek() in '0'.code..'9'.code) {
            sb.append(c.read().toChar())
        }
        if (!c.atEnd() && c.peek() == '.'.code) {
            sb.append(c.read().toChar())
            while (!c.atEnd() && c.peek() in '0'.code..'9'.code) {
                sb.append(c.read().toChar())
            }
        }
        if (!c.atEnd() && (c.peek() == 'e'.code || c.peek() == 'E'.code)) {
            sb.append(c.read().toChar())
            if (!c.atEnd() && (c.peek() == '-'.code || c.peek() == '+'.code)) {
                sb.append(c.read().toChar())
            }
            while (!c.atEnd() && c.peek() in '0'.code..'9'.code) {
                sb.append(c.read().toChar())
            }
        }
        return sb.toString().toDoubleOrNull() ?: 0.0
    }

    private fun parseUuidBody(c: ByteCursor): UUID {
        val bytes = c.readExact(36)
        val s = String(bytes, Charsets.UTF_8)
        return UUID.fromString(s)
    }

    private fun parseDelimitedString(c: ByteCursor, quote: Char): String {
        val sb = java.lang.StringBuilder()
        val quoteCode = quote.code
        while (!c.atEnd()) {
            val b = c.read()
            if (b == quoteCode) return sb.toString()
            if (b == '\\'.code && !c.atEnd()) {
                val esc = c.read().toChar()
                sb.append(when (esc) {
                    'n' -> '\n'; 't' -> '\t'; 'r' -> '\r'
                    '\\' -> '\\'; '"' -> '"'; '\'' -> '\''
                    'a' -> '\u0007'; 'b' -> '\b'
                    else -> esc
                })
                if (sb.length > MAX_STRING_BYTES) throw IllegalStateException("string too long")
            } else {
                if (b in 0..127) {
                    sb.append(b.toChar())
                } else {
                    val charBytes = readUtf8CharBytes(b, c)
                    sb.append(String(charBytes, Charsets.UTF_8))
                }
                if (sb.length > MAX_STRING_BYTES) throw IllegalStateException("string too long")
            }
        }
        throw IllegalStateException("unterminated string starting before ${c.position}")
    }

    private fun readUtf8CharBytes(firstByte: Int, c: ByteCursor): ByteArray {
        val len = when {
            (firstByte and 0xE0) == 0xC0 -> 2
            (firstByte and 0xF0) == 0xE0 -> 3
            (firstByte and 0xF8) == 0xF0 -> 4
            else -> 1
        }
        val bytes = ByteArray(len)
        bytes[0] = firstByte.toByte()
        for (i in 1 until len) {
            bytes[i] = c.read().toByte()
        }
        return bytes
    }

    private fun parseExplicitLengthString(c: ByteCursor): String {
        c.expect('(')
        val count = parseDigits(c).toIntOrNull() ?: throw IllegalStateException("bad s(N) count")
        if (count > MAX_STRING_BYTES) throw IllegalStateException("string too long")
        c.expect(')')
        val quote = c.read().toChar()
        val rawBytes = c.readExact(count)
        val endQuote = c.read().toChar()
        if (endQuote != quote) throw IllegalStateException("s(N) end-quote missing")
        return String(rawBytes, Charsets.UTF_8)
    }

    private fun parseDate(text: String): Long {
        return try {
            ISO_8601_MS.parse(text, Instant::from).toEpochMilli()
        } catch (_: Exception) {
            try {
                ISO_8601_S.parse(text, Instant::from).toEpochMilli()
            } catch (_: Exception) { 0L }
        }
    }

    private fun parseBinary(c: ByteCursor): LLSDBinary {
        val p = c.peek().toChar()
        return when (p) {
            '1' -> {
                c.read()
                val hexHeader = c.read().toChar()
                if (hexHeader != '6') throw IllegalStateException("expected b16")
                val q = c.read().toChar()
                val raw = parseDelimitedString(c, q)
                val clean = raw.filter { !it.isWhitespace() }
                if (clean.length > MAX_BINARY_BYTES * 2) throw IllegalStateException("binary too long")
                LLSDBinary(hexDecode(clean))
            }
            '6' -> {
                c.read()
                val n = c.read().toChar()
                if (n != '4') throw IllegalStateException("expected b64")
                val q = c.read().toChar()
                val raw = parseDelimitedString(c, q)
                if (raw.length > MAX_BINARY_BYTES * 2) throw IllegalStateException("binary too long")
                LLSDBinary(Base64.getDecoder().decode(raw.filter { !it.isWhitespace() }))
            }
            '(' -> {
                c.expect('(')
                val n = parseDigits(c).toIntOrNull() ?: 0
                if (n > MAX_BINARY_BYTES) throw IllegalStateException("binary too long")
                c.expect(')')
                val q = c.read().toChar()
                val rawBytes = c.readExact(n)
                val endQuote = c.read().toChar()
                if (endQuote != q) throw IllegalStateException("b(N) end-quote missing")
                LLSDBinary(rawBytes)
            }
            else -> throw IllegalStateException("unrecognised binary form at ${c.position}")
        }
    }

    private fun hexDecode(hex: String): ByteArray {
        require(hex.length % 2 == 0) { "hex length must be even" }
        val out = ByteArray(hex.length / 2)
        for (i in out.indices) {
            out[i] = ((Character.digit(hex[i * 2], 16) shl 4) or Character.digit(hex[i * 2 + 1], 16)).toByte()
        }
        return out
    }

    private fun parseMap(c: ByteCursor, depth: Int): LLSDMap {
        val map = LLSDMap()
        var entries = 0
        while (true) {
            c.skipWhitespace()
            if (c.atEnd()) throw IllegalStateException("map unterminated")
            if (c.peek().toChar() == '}') { c.read(); return map }
            val keyValue = parseValue(c, depth + 1)
            val key = (keyValue as? LLSDString)?.value
                ?: throw IllegalStateException("map key must be string at ${c.position}")
            c.skipWhitespace()
            c.expect(':')
            val value = parseValue(c, depth + 1)
            map[key] = value
            entries++
            if (entries > MAX_COLLECTION_ELEMENTS) throw IllegalStateException("map too large")
            c.skipWhitespace()
            if (!c.atEnd() && c.peek().toChar() == ',') c.read()
        }
    }

    private fun parseArray(c: ByteCursor, depth: Int): LLSDArray {
        val arr = LLSDArray()
        var entries = 0
        while (true) {
            c.skipWhitespace()
            if (c.atEnd()) throw IllegalStateException("array unterminated")
            if (c.peek().toChar() == ']') { c.read(); return arr }
            arr.add(parseValue(c, depth + 1))
            entries++
            if (entries > MAX_COLLECTION_ELEMENTS) throw IllegalStateException("array too large")
            c.skipWhitespace()
            if (!c.atEnd() && c.peek().toChar() == ',') c.read()
        }
    }
}
