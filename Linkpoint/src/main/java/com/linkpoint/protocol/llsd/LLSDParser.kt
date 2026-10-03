package com.linkpoint.protocol.llsd

import android.util.Log
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.io.PushbackInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

/**
 * LLSD Parser - handles Binary, XML, and Notation formats.
 * See https://wiki.secondlife.com/wiki/LLSD for canonical formatting details.
 */
object LLSDParser {
    private const val TAG = "LLSDParser"

    private fun logWarning(tag: String, message: String, throwable: Throwable? = null) {
        try {
            Log.w(tag, message, throwable)
        } catch (_: Throwable) {
            System.err.println("[$tag] $message: ${throwable?.message}")
        }
    }

    private data class ParseLimits(
        val maxStringBytes: Int = 1024 * 1024,
        val maxBinaryBytes: Int = 1024 * 1024,
        val maxArrayLength: Int = 10_000,
        val maxMapEntries: Int = 10_000,
        val maxCollectionElementsTotal: Int = 20_000,
        val maxNestingDepth: Int = 128,
        val maxTotalBytes: Int = 4 * 1024 * 1024,
    )

    private data class ParseLimitsState(
        var currentDepth: Int = 0,
        var accumulatedBytes: Int = 0,
        var collectionElementsRead: Int = 0,
    )

    private open class LLSDParseException(message: String) : RuntimeException(message)
    private class TruncatedBinaryPayloadException(expectedBytes: Int) :
        LLSDParseException("Truncated binary LLSD payload while reading $expectedBytes bytes.")
    private class ParseLimitExceededException(message: String) : LLSDParseException(message)
    private class MalformedBinaryDataException(message: String) : LLSDParseException(message)

    /**
     * Parse LLSD from bytes (auto-detect format).
     *
     * Detection priority:
     *   1. `<?llsd/notation?>` magic header → notation
     *   2. `<?llsd/binary?>` magic header → binary
     *   3. Anything starting with `<` → XML (covers `<?xml`, `<llsd>`)
     *   4. Notation bare-form (first non-whitespace byte is one of
     *      `{ [ i r u s d b l ! ' " 0 1 T t F f`)
     *   5. Fallback → binary
     */
    fun parse(data: ByteArray): LLSDValue {
        if (data.isEmpty()) return LLSDUndefined

        if (startsWithBytes(data, "<?llsd/notation?>")) return parseNotation(data)
        if (startsWithBytes(data, "<?llsd/binary?>")) return parseBinary(data)

        return when {
            looksLikeXml(data) -> parseXML(data)
            looksLikeNotation(data) -> parseNotation(data)
            else -> parseBinary(data)
        }
    }

    /**
     * Parse LLSD Notation (the human-readable form). See
     * [LLSDNotationParser] for the spec mapping; this wrapper exists
     * so the auto-detect path and API surface match the binary / XML
     * forms.
     */
    fun parseNotation(data: ByteArray): LLSDValue = LLSDNotationParser.parse(data)

    private fun startsWithBytes(data: ByteArray, s: String): Boolean {
        if (data.size < s.length) return false
        for (i in s.indices) if (data[i].toInt().toChar() != s[i]) return false
        return true
    }

    private fun looksLikeXml(data: ByteArray): Boolean {
        if (data.isEmpty()) return false
        var i = 0
        while (i < data.size && data[i].toInt().toChar().isWhitespace()) i++
        if (i >= data.size) return false
        if (data[i] != '<'.code.toByte()) return false

        return startsWithBytesAt(data, i, "<?xml") || startsWithBytesAt(data, i, "<llsd") || data[i] == '<'.code.toByte()
    }

    private fun startsWithBytesAt(data: ByteArray, start: Int, s: String): Boolean {
        if (start + s.length > data.size) return false
        for (idx in s.indices) {
            if (data[start + idx].toInt().toChar() != s[idx]) return false
        }
        return true
    }

    private fun looksLikeNotation(data: ByteArray): Boolean {
        var i = 0
        while (i < data.size && data[i].toInt().toChar().isWhitespace()) i++
        if (i >= data.size) return false
        return when (data[i].toInt().toChar()) {
            '{', '[', '!', '\'', '"', 'i', 'r', 'u', 's', 'd', 'b', 'l',
            '0', '1', 'T', 't', 'F', 'f' -> true
            else -> false
        }
    }

    fun parseAuto(data: ByteArray, contentType: String?): LLSDValue {
        if (data.isEmpty()) return LLSDUndefined
        val stream = ByteArrayInputStream(data)
        val buffered = java.io.BufferedInputStream(stream, 65536)
        return when (LLSDContentTypeDetector.detect(buffered, contentType)) {
            LLSDContentTypeDetector.LLSDContentType.LLSD_BINARY -> parseBinary(data)
            LLSDContentTypeDetector.LLSDContentType.LLSD_XML -> parseXML(data)
        }
    }

    fun parseBinary(data: ByteArray): LLSDValue {
        val stripped = stripBinaryMagicHeader(data)
        val stream = PushbackInputStream(ByteArrayInputStream(stripped), 1)
        val limits = ParseLimits()
        val state = ParseLimitsState()

        return try {
            parseBinaryValue(stream, state, limits)
        } catch (e: LLSDParseException) {
            logWarning(TAG, "Failed to parse binary LLSD: ${e.message}", e)
            LLSDUndefined
        } catch (e: IllegalArgumentException) {
            logWarning(TAG, "Failed to parse binary LLSD due to illegal argument: ${e.message}", e)
            LLSDUndefined
        }
    }

    fun parseBinaryAndConsumed(data: ByteArray): Pair<LLSDValue, Int> {
        val backing = ByteArrayInputStream(data)
        val stream = PushbackInputStream(backing, 1)
        val limits = ParseLimits()
        val state = ParseLimitsState()
        val value = try {
            parseBinaryValue(stream, state, limits)
        } catch (e: LLSDParseException) {
            logWarning(TAG, "Failed in parseBinaryAndConsumed: ${e.message}", e)
            return LLSDUndefined to -1
        } catch (e: IllegalArgumentException) {
            logWarning(TAG, "Failed in parseBinaryAndConsumed due to illegal argument: ${e.message}", e)
            return LLSDUndefined to -1
        }
        val consumed = data.size - backing.available()
        return value to consumed
    }

    private fun stripBinaryMagicHeader(data: ByteArray): ByteArray {
        val magic = "<?llsd/binary?>".toByteArray(Charsets.US_ASCII)
        if (data.size < magic.size + 1) return data
        for (i in magic.indices) {
            if (data[i] != magic[i]) return data
        }
        var idx = magic.size
        if (idx < data.size && data[idx] == '\r'.code.toByte()) idx++
        if (idx < data.size && data[idx] == '\n'.code.toByte()) idx++ else return data
        return data.copyOfRange(idx, data.size)
    }

    private fun parseBinaryValue(
        stream: PushbackInputStream,
        state: ParseLimitsState,
        limits: ParseLimits,
    ): LLSDValue {
        if (state.currentDepth >= limits.maxNestingDepth) {
            throw ParseLimitExceededException("Maximum nesting depth exceeded.")
        }

        state.currentDepth++
        try {
            val marker = readByte(stream, state, limits)
            return parseByMarker(marker.toChar(), stream, state, limits)
        } finally {
            state.currentDepth--
        }
    }

    private fun parseByMarker(
        marker: Char,
        stream: PushbackInputStream,
        state: ParseLimitsState,
        limits: ParseLimits,
    ): LLSDValue {
        return when (marker) {
            LLSDValue.MARKER_UNDEF -> LLSDUndefined
            LLSDValue.MARKER_TRUE -> LLSDBoolean(true)
            LLSDValue.MARKER_FALSE -> LLSDBoolean(false)
            LLSDValue.MARKER_INTEGER -> {
                val bytes = readExact(stream, 4, state, limits)
                LLSDInteger(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).int)
            }
            LLSDValue.MARKER_REAL -> {
                val bytes = readExact(stream, 8, state, limits)
                LLSDReal(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).double)
            }
            LLSDValue.MARKER_UUID -> {
                val bytes = readExact(stream, 16, state, limits)
                val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
                LLSDUUID(UUID(buffer.long, buffer.long))
            }
            LLSDValue.MARKER_STRING, 's' -> {
                val len = readLength(stream, state, limits)
                if (len > limits.maxStringBytes) {
                    throw ParseLimitExceededException("String length exceeds maxStringBytes.")
                }
                LLSDString(String(readExact(stream, len, state, limits), Charsets.UTF_8))
            }
            LLSDValue.MARKER_BINARY, 'b' -> {
                val len = readLength(stream, state, limits)
                if (len > limits.maxBinaryBytes) {
                    throw ParseLimitExceededException("Binary length exceeds maxBinaryBytes.")
                }
                LLSDBinary(readExact(stream, len, state, limits))
            }
            LLSDValue.MARKER_DATE -> {
                val bytes = readExact(stream, 8, state, limits)
                val seconds = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).double
                LLSDDate((seconds * 1000).toLong())
            }
            LLSDValue.MARKER_URI, 'l' -> {
                val len = readLength(stream, state, limits)
                if (len > limits.maxStringBytes) {
                    throw ParseLimitExceededException("URI length exceeds maxStringBytes.")
                }
                LLSDURI(String(readExact(stream, len, state, limits), Charsets.UTF_8))
            }
            LLSDValue.MARKER_MAP, '{' -> {
                val map = LLSDMap()
                var entries = 0

                val declaredEntries = readLength(stream, state, limits)
                if (declaredEntries > limits.maxMapEntries) {
                    throw ParseLimitExceededException("Map entry count exceeds maxMapEntries.")
                }

                while (true) {
                    val keyMarker = readByte(stream, state, limits).toChar()
                    if (keyMarker == LLSDValue.MARKER_MAP_END) break
                    if (keyMarker != 'k') {
                        throw MalformedBinaryDataException("Malformed map: expected key marker 'k'.")
                    }

                    entries++
                    state.collectionElementsRead++
                    if (entries > limits.maxMapEntries) {
                        throw ParseLimitExceededException("Map entry count exceeds maxMapEntries.")
                    }
                    enforceCollectionElementLimit(state, limits)

                    val keyLength = readLength(stream, state, limits)
                    if (keyLength > limits.maxStringBytes) {
                        throw ParseLimitExceededException("Map key length exceeds maxStringBytes.")
                    }
                    val key = String(readExact(stream, keyLength, state, limits), Charsets.UTF_8)
                    map[key] = parseBinaryValue(stream, state, limits)
                }

                map
            }
            LLSDValue.MARKER_ARRAY, '[' -> {
                val array = LLSDArray()
                var elements = 0

                val declaredElements = readLength(stream, state, limits)
                if (declaredElements > limits.maxArrayLength) {
                    throw ParseLimitExceededException("Array length exceeds maxArrayLength.")
                }

                while (true) {
                    if (peekByte(stream, state, limits).toChar() == LLSDValue.MARKER_ARRAY_END) {
                        readByte(stream, state, limits)
                        break
                    }

                    elements++
                    state.collectionElementsRead++
                    if (elements > limits.maxArrayLength) {
                        throw ParseLimitExceededException("Array length exceeds maxArrayLength.")
                    }
                    enforceCollectionElementLimit(state, limits)

                    array.add(parseBinaryValue(stream, state, limits))
                }

                array
            }
            else -> LLSDUndefined
        }
    }

    private fun readLength(
        stream: PushbackInputStream,
        state: ParseLimitsState,
        limits: ParseLimits,
    ): Int {
        val lenBytes = readExact(stream, 4, state, limits)
        val len = ByteBuffer.wrap(lenBytes).order(ByteOrder.BIG_ENDIAN).int
        if (len < 0) {
            throw MalformedBinaryDataException("Negative length in binary LLSD payload.")
        }
        return len
    }

    private fun readByte(stream: InputStream, state: ParseLimitsState, limits: ParseLimits): Int {
        val value = stream.read()
        if (value == -1) {
            throw TruncatedBinaryPayloadException(1)
        }
        state.accumulatedBytes++
        enforceTotalBytesLimit(state, limits)
        return value
    }

    private fun peekByte(
        stream: PushbackInputStream,
        state: ParseLimitsState,
        limits: ParseLimits,
    ): Int {
        val value = readByte(stream, state, limits)
        stream.unread(value)
        state.accumulatedBytes--
        return value
    }

    private fun readExact(
        stream: InputStream,
        size: Int,
        state: ParseLimitsState,
        limits: ParseLimits,
    ): ByteArray {
        val bytes = ByteArray(size)
        var offset = 0
        while (offset < size) {
            val read = stream.read(bytes, offset, size - offset)
            if (read == -1) {
                throw TruncatedBinaryPayloadException(size)
            }
            offset += read
            state.accumulatedBytes += read
            enforceTotalBytesLimit(state, limits)
        }
        return bytes
    }

    private fun enforceTotalBytesLimit(state: ParseLimitsState, limits: ParseLimits) {
        if (state.accumulatedBytes > limits.maxTotalBytes) {
            throw ParseLimitExceededException("Total bytes exceed maxTotalBytes.")
        }
    }

    private fun enforceCollectionElementLimit(state: ParseLimitsState, limits: ParseLimits) {
        if (state.collectionElementsRead > limits.maxCollectionElementsTotal) {
            throw ParseLimitExceededException("Total collection elements exceed maxCollectionElementsTotal.")
        }
    }

    fun parseXML(xml: String): LLSDValue = parseXML(xml.toByteArray(Charsets.UTF_8))

    fun parseXML(data: ByteArray, offset: Int = 0, length: Int = data.size - offset): LLSDValue {
        if (data.isEmpty() || length <= 0 || offset < 0 || offset + length > data.size) return LLSDUndefined
        val limits = ParseLimits()
        val state = ParseLimitsState()
        val cursor = XmlByteCursor(data, offset, offset + length, limits, state)
        return try {
            cursor.skipMisc()
            if (cursor.atEnd()) LLSDUndefined else parseXMLElementFromCursor(cursor)
        } catch (e: Exception) {
            logWarning(TAG, "Failed to parse XML LLSD: ${e.message}", e)
            LLSDUndefined
        }
    }

    private class XmlByteCursor(
        val bytes: ByteArray,
        var pos: Int = 0,
        val limit: Int = bytes.size,
        val limits: ParseLimits = ParseLimits(),
        val state: ParseLimitsState = ParseLimitsState()
    ) {
        fun atEnd(): Boolean = pos >= limit

        fun skipWhitespace() {
            while (pos < limit) {
                val b = bytes[pos].toInt() and 0xFF
                if (b == ' '.code || b == '\t'.code || b == '\r'.code || b == '\n'.code) {
                    pos++
                } else {
                    break
                }
            }
            enforceTotalBytesLimit()
        }

        fun skipMisc() {
            while (pos < limit) {
                skipWhitespace()
                if (pos >= limit) break
                if (bytes[pos] == '<'.code.toByte()) {
                    if (startsWith("<!--")) {
                        pos += 4
                        val end = indexOf("-->", pos)
                        pos = if (end == -1) limit else end + 3
                        continue
                    } else if (startsWith("<?")) {
                        pos += 2
                        val end = indexOf("?>", pos)
                        pos = if (end == -1) limit else end + 2
                        continue
                    } else if (startsWith("<!DOCTYPE", ignoreCase = true)) {
                        val end = indexOf(">", pos)
                        pos = if (end == -1) limit else end + 1
                        continue
                    }
                }
                break
            }
            enforceTotalBytesLimit()
        }

        fun startsWith(prefix: String, ignoreCase: Boolean = false): Boolean {
            if (pos + prefix.length > limit) return false
            for (i in prefix.indices) {
                val b = (bytes[pos + i].toInt() and 0xFF).toChar()
                val c = prefix[i]
                if (ignoreCase) {
                    if (!b.equals(c, ignoreCase = true)) return false
                } else {
                    if (b != c) return false
                }
            }
            return true
        }

        fun indexOf(target: String, startFrom: Int = pos): Int {
            val targetLen = target.length
            val maxSearch = limit - targetLen
            if (maxSearch < startFrom) return -1
            val firstCharLower = target[0].lowercaseChar()
            val firstCharUpper = target[0].uppercaseChar()

            for (i in startFrom..maxSearch) {
                val b = (bytes[i].toInt() and 0xFF).toChar()
                if (b == firstCharLower || b == firstCharUpper) {
                    var match = true
                    for (j in 1 until targetLen) {
                        val bJ = (bytes[i + j].toInt() and 0xFF).toChar()
                        val cJ = target[j]
                        if (!bJ.equals(cJ, ignoreCase = true)) {
                            match = false
                            break
                        }
                    }
                    if (match) return i
                }
            }
            return -1
        }

        fun enforceTotalBytesLimit() {
            if (pos > limits.maxTotalBytes) {
                throw ParseLimitExceededException("Total bytes exceed maxTotalBytes.")
            }
        }
    }

    private data class XmlTagHeader(
        val name: String,
        val isClosing: Boolean,
        val isSelfClosing: Boolean
    )

    private fun parseTagHeader(c: XmlByteCursor): XmlTagHeader? {
        c.skipMisc()
        if (c.atEnd() || c.bytes[c.pos] != '<'.code.toByte()) return null

        val startPos = c.pos
        c.pos++ // consume '<'

        val isClosing = !c.atEnd() && c.bytes[c.pos] == '/'.code.toByte()
        if (isClosing) c.pos++

        val nameStart = c.pos
        while (!c.atEnd()) {
            val b = (c.bytes[c.pos].toInt() and 0xFF).toChar()
            if (b == '>' || b == '/' || b.isWhitespace()) break
            c.pos++
        }
        if (nameStart == c.pos) {
            c.pos = startPos
            return null
        }

        val tagName = String(c.bytes, nameStart, c.pos - nameStart, Charsets.US_ASCII).lowercase(Locale.US)

        var isSelfClosing = false
        while (!c.atEnd()) {
            val b = (c.bytes[c.pos].toInt() and 0xFF).toChar()
            if (b == '>') {
                c.pos++
                break
            } else if (b == '/') {
                isSelfClosing = true
                c.pos++
            } else {
                c.pos++
            }
        }

        c.enforceTotalBytesLimit()
        return XmlTagHeader(tagName, isClosing, isSelfClosing)
    }

    private fun parseXMLElementFromCursor(c: XmlByteCursor): LLSDValue {
        val tag = parseTagHeader(c) ?: return LLSDUndefined
        if (tag.isClosing) return LLSDUndefined

        if (tag.isSelfClosing) {
            return when (tag.name) {
                "undef" -> LLSDUndefined
                "boolean" -> LLSDBoolean(false)
                "integer" -> LLSDInteger(0)
                "real" -> LLSDReal(0.0)
                "string" -> LLSDString("")
                "uuid" -> LLSDUUID.ZERO
                "binary" -> LLSDBinary(byteArrayOf())
                "date" -> LLSDDate(0L)
                "uri" -> LLSDURI("")
                "map" -> LLSDMap()
                "array" -> LLSDArray()
                else -> LLSDUndefined
            }
        }

        return when (tag.name) {
            "llsd" -> {
                val valRes = parseXMLElementFromCursor(c)
                c.skipMisc()
                if (c.startsWith("</llsd>", ignoreCase = true)) {
                    c.pos += "</llsd>".length
                }
                valRes
            }
            "undef" -> {
                consumeClosingTag(c, "undef")
                LLSDUndefined
            }
            "boolean" -> {
                val content = readTextContentUntilCloseTag(c, "boolean").trim()
                LLSDBoolean(content == "true" || content == "1")
            }
            "integer" -> {
                val content = readTextContentUntilCloseTag(c, "integer").trim()
                LLSDInteger(content.toIntOrNull() ?: 0)
            }
            "real" -> {
                val content = readTextContentUntilCloseTag(c, "real").trim()
                LLSDReal(content.toDoubleOrNull() ?: 0.0)
            }
            "string" -> {
                val content = readTextContentUntilCloseTag(c, "string")
                if (content.length > c.limits.maxStringBytes) {
                    throw ParseLimitExceededException("String length exceeds maxStringBytes.")
                }
                LLSDString(content)
            }
            "uuid" -> {
                val content = readTextContentUntilCloseTag(c, "uuid").trim()
                val uuid = try { UUID.fromString(content) } catch (_: Exception) { UUID(0, 0) }
                LLSDUUID(uuid)
            }
            "binary" -> {
                val content = readTextContentUntilCloseTag(c, "binary").trim().filter { !it.isWhitespace() }
                val bytes = try { Base64.getDecoder().decode(content) } catch (_: Exception) { byteArrayOf() }
                if (bytes.size > c.limits.maxBinaryBytes) {
                    throw ParseLimitExceededException("Binary length exceeds maxBinaryBytes.")
                }
                LLSDBinary(bytes)
            }
            "date" -> {
                val content = readTextContentUntilCloseTag(c, "date").trim()
                val date = parseLlsdDate(content) ?: Date(0L)
                LLSDDate(date)
            }
            "uri" -> {
                val content = readTextContentUntilCloseTag(c, "uri").trim()
                if (content.length > c.limits.maxStringBytes) {
                    throw ParseLimitExceededException("URI length exceeds maxStringBytes.")
                }
                LLSDURI(content)
            }
            "map" -> {
                if (c.state.currentDepth >= c.limits.maxNestingDepth) {
                    throw ParseLimitExceededException("Maximum nesting depth exceeded.")
                }
                c.state.currentDepth++
                val map = LLSDMap()
                var entries = 0
                try {
                    while (!c.atEnd()) {
                        c.skipMisc()
                        if (c.atEnd()) break
                        if (c.startsWith("</map>", ignoreCase = true)) {
                            c.pos += "</map>".length
                            break
                        }
                        val keyTag = parseTagHeader(c) ?: break
                        if (keyTag.name != "key" || keyTag.isClosing) break

                        val key = if (keyTag.isSelfClosing) "" else readTextContentUntilCloseTag(c, "key")
                        val value = parseXMLElementFromCursor(c)
                        map[key] = value

                        entries++
                        c.state.collectionElementsRead++
                        if (entries > c.limits.maxMapEntries) {
                            throw ParseLimitExceededException("Map entry count exceeds maxMapEntries.")
                        }
                        enforceCollectionElementLimit(c.state, c.limits)
                    }
                } finally {
                    c.state.currentDepth--
                }
                map
            }
            "array" -> {
                if (c.state.currentDepth >= c.limits.maxNestingDepth) {
                    throw ParseLimitExceededException("Maximum nesting depth exceeded.")
                }
                c.state.currentDepth++
                val array = LLSDArray()
                var elements = 0
                try {
                    while (!c.atEnd()) {
                        c.skipMisc()
                        if (c.atEnd()) break
                        if (c.startsWith("</array>", ignoreCase = true)) {
                            c.pos += "</array>".length
                            break
                        }
                        val value = parseXMLElementFromCursor(c)
                        array.add(value)

                        elements++
                        c.state.collectionElementsRead++
                        if (elements > c.limits.maxArrayLength) {
                            throw ParseLimitExceededException("Array length exceeds maxArrayLength.")
                        }
                        enforceCollectionElementLimit(c.state, c.limits)
                    }
                } finally {
                    c.state.currentDepth--
                }
                array
            }
            else -> {
                consumeClosingTag(c, tag.name)
                LLSDUndefined
            }
        }
    }

    private fun consumeClosingTag(c: XmlByteCursor, tagName: String) {
        val closeTag = "</$tagName>"
        val idx = c.indexOf(closeTag)
        if (idx != -1) {
            c.pos = idx + closeTag.length
        }
    }

    private fun readTextContentUntilCloseTag(c: XmlByteCursor, tagName: String): String {
        val closeTag = "</$tagName>"
        val sb = StringBuilder()

        while (!c.atEnd()) {
            if (c.startsWith("<![CDATA[")) {
                c.pos += 9
                val cdataEnd = c.indexOf("]]>", c.pos)
                if (cdataEnd == -1) {
                    val len = c.limit - c.pos
                    sb.append(String(c.bytes, c.pos, len, Charsets.UTF_8))
                    c.pos = c.limit
                    break
                } else {
                    val len = cdataEnd - c.pos
                    sb.append(String(c.bytes, c.pos, len, Charsets.UTF_8))
                    c.pos = cdataEnd + 3
                }
            } else if (c.startsWith(closeTag, ignoreCase = true)) {
                c.pos += closeTag.length
                break
            } else {
                val nextStart = c.indexOf("<", c.pos)
                if (nextStart == -1) {
                    val len = c.limit - c.pos
                    val raw = String(c.bytes, c.pos, len, Charsets.UTF_8)
                    sb.append(unescapeXML(raw))
                    c.pos = c.limit
                    break
                } else if (nextStart > c.pos) {
                    val len = nextStart - c.pos
                    val raw = String(c.bytes, c.pos, len, Charsets.UTF_8)
                    sb.append(unescapeXML(raw))
                    c.pos = nextStart
                } else {
                    if (c.startsWith("<!--")) {
                        c.pos += 4
                        val commentEnd = c.indexOf("-->", c.pos)
                        c.pos = if (commentEnd == -1) c.limit else commentEnd + 3
                    } else if (c.startsWith("<?")) {
                        c.pos += 2
                        val piEnd = c.indexOf("?>", c.pos)
                        c.pos = if (piEnd == -1) c.limit else piEnd + 2
                    } else {
                        sb.append('<')
                        c.pos++
                    }
                }
            }
            c.enforceTotalBytesLimit()
        }

        return sb.toString()
    }

    private fun unescapeXML(s: String): String {
        if (!s.contains('&')) return s
        val sb = StringBuilder(s.length)
        var i = 0
        while (i < s.length) {
            val ch = s[i]
            if (ch == '&') {
                val semi = s.indexOf(';', i)
                if (semi != -1 && semi - i <= 10) {
                    val entity = s.substring(i + 1, semi)
                    val replacement = when {
                        entity == "lt" -> "<"
                        entity == "gt" -> ">"
                        entity == "amp" -> "&"
                        entity == "quot" -> "\""
                        entity == "apos" -> "'"
                        entity.startsWith("#x", ignoreCase = true) -> {
                            val code = entity.substring(2).toIntOrNull(16)
                            if (code != null) String(Character.toChars(code)) else null
                        }
                        entity.startsWith("#") -> {
                            val code = entity.substring(1).toIntOrNull(10)
                            if (code != null) String(Character.toChars(code)) else null
                        }
                        else -> null
                    }
                    if (replacement != null) {
                        sb.append(replacement)
                        i = semi + 1
                        continue
                    }
                }
            }
            sb.append(ch)
            i++
        }
        return sb.toString()
    }

    fun parseLlsdDate(value: String): Date? {
        val normalised = value.trim().let { v ->
            val tzPattern = Regex("""[+-]\d{2}:?\d{2}""")
            if (v.endsWith("Z") && tzPattern.containsMatchIn(v.dropLast(1))) v.dropLast(1) else v
        }
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX"
        )
        for (pattern in patterns) {
            val formatter = java.text.SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            try {
                return formatter.parse(normalised)
            } catch (e: Exception) {
                // Try next
            }
        }
        return null
    }
}
