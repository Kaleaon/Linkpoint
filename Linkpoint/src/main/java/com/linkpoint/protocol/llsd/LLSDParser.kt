package com.linkpoint.protocol.llsd

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
     * Parse LLSD from bytes (auto-detect format)
     */
    fun parse(data: ByteArray): LLSDValue {
        if (data.isEmpty()) return LLSDUndefined

        return when {
            data.size >= 2 && data[0] == '<'.code.toByte() -> parseXML(String(data))
            data.size >= 4 && String(data.sliceArray(0..3)) == "<?xm" -> parseXML(String(data))
            data.size >= 6 && String(data.sliceArray(0..5)) == "<llsd>" -> parseXML(String(data))
            else -> parseBinary(data)
        }
    }

    /**
     * Parse LLSD using content-type detection and BOM handling.
     */
    fun parseAuto(data: ByteArray, contentType: String?): LLSDValue {
        if (data.isEmpty()) return LLSDUndefined
        val stream = ByteArrayInputStream(data)
        val buffered = java.io.BufferedInputStream(stream, 65536)
        return when (LLSDContentTypeDetector.detect(buffered, contentType)) {
            LLSDContentTypeDetector.LLSDContentType.LLSD_BINARY -> parseBinary(data)
            LLSDContentTypeDetector.LLSDContentType.LLSD_XML -> parseXML(String(data, Charsets.UTF_8))
        }
    }

    /**
     * Parse LLSD Binary format
     */
    fun parseBinary(data: ByteArray): LLSDValue {
        val stream = PushbackInputStream(ByteArrayInputStream(data), 1)
        val limits = ParseLimits()
        val state = ParseLimitsState()

        return try {
            parseBinaryValue(stream, state, limits)
        } catch (_: LLSDParseException) {
            LLSDUndefined
        } catch (_: IllegalArgumentException) {
            LLSDUndefined
        }
    }

    /**
     * Parse LLSD Binary and report how many bytes the parser actually
     * consumed. Required by mesh-asset readers, where the LLSD header is
     * followed by zlib-compressed LOD blobs at byte offsets relative to
     * the end of the header — and the header may itself contain
     * `LLSDBinary` values whose payloads happen to include `0x7d` (`}`)
     * bytes, which made the previous "scan for first `}`" approach
     * silently truncate headers and return garbage.
     */
    fun parseBinaryAndConsumed(data: ByteArray): Pair<LLSDValue, Int> {
        val backing = ByteArrayInputStream(data)
        val stream = PushbackInputStream(backing, 1)
        val limits = ParseLimits()
        val state = ParseLimitsState()
        val value = try {
            parseBinaryValue(stream, state, limits)
        } catch (_: LLSDParseException) {
            return LLSDUndefined to -1
        } catch (_: IllegalArgumentException) {
            return LLSDUndefined to -1
        }
        // ByteArrayInputStream.available() is the count not yet consumed.
        // The PushbackInputStream sits in front of it but only buffers
        // single bytes (size=1), so the byte count it might be holding
        // is at most one. Compensating for it keeps us conservative —
        // mesh format only requires that the offset is at-or-past the
        // real end of the header (header bytes are addressed via the
        // header itself, not by the absolute offset).
        val consumed = data.size - backing.available()
        return value to consumed
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
                val seconds = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).double
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

    /**
     * Parse LLSD XML format
     */
    fun parseXML(xml: String): LLSDValue {
        // Simple XML parser for LLSD
        var cleaned = xml.trim()

        // Strip XML declaration if present so we can parse the <llsd> root element
        if (cleaned.startsWith("<?xml", ignoreCase = true)) {
            val declEnd = cleaned.indexOf("?>")
            if (declEnd == -1) {
                return LLSDUndefined
            }
            cleaned = cleaned.substring(declEnd + 2).trimStart()
        }

        return try {
            parseXMLElement(cleaned, 0).first
        } catch (e: Exception) {
            LLSDUndefined
        }
    }

    private fun parseXMLElement(xml: String, startPos: Int): Pair<LLSDValue, Int> {
        var pos = startPos

        // Skip whitespace
        while (pos < xml.length && xml[pos].isWhitespace()) pos++

        if (pos >= xml.length || xml[pos] != '<') {
            return LLSDUndefined to pos
        }

        // Find tag name
        val tagStart = pos + 1
        var tagEnd = tagStart
        while (tagEnd < xml.length && xml[tagEnd] != '>' && xml[tagEnd] != ' ' && xml[tagEnd] != '/') {
            tagEnd++
        }

        val tagName = xml.substring(tagStart, tagEnd).lowercase()

        // Handle self-closing tags
        val closePos = xml.indexOf('>', pos)
        if (closePos == -1) return LLSDUndefined to xml.length

        val isSelfClosing = xml[closePos - 1] == '/'

        if (isSelfClosing) {
            return when (tagName) {
                "undef" -> LLSDUndefined to closePos + 1
                "boolean" -> LLSDBoolean(false) to closePos + 1
                "integer" -> LLSDInteger(0) to closePos + 1
                "real" -> LLSDReal(0.0) to closePos + 1
                "string" -> LLSDString("") to closePos + 1
                "uuid" -> LLSDUUID.ZERO to closePos + 1
                "binary" -> LLSDBinary(byteArrayOf()) to closePos + 1
                "map" -> LLSDMap() to closePos + 1
                "array" -> LLSDArray() to closePos + 1
                else -> LLSDUndefined to closePos + 1
            }
        }

        // Find content and closing tag
        val contentStart = closePos + 1
        val closingTag = "</$tagName>"
        val closingPos = xml.indexOf(closingTag, contentStart, ignoreCase = true)
        if (closingPos == -1) return LLSDUndefined to xml.length

        val content = xml.substring(contentStart, closingPos).trim()
        val endPos = closingPos + closingTag.length

        return when (tagName) {
            "llsd" -> parseXMLElement(content, 0).let { it.first to endPos }
            "undef" -> LLSDUndefined to endPos
            "boolean" -> LLSDBoolean(content == "true" || content == "1") to endPos
            "integer" -> LLSDInteger(content.toIntOrNull() ?: 0) to endPos
            "real" -> LLSDReal(content.toDoubleOrNull() ?: 0.0) to endPos
            "string" -> LLSDString(unescapeXML(content)) to endPos
            "uuid" -> {
                val uuid = try { UUID.fromString(content) } catch (e: Exception) { UUID(0, 0) }
                LLSDUUID(uuid) to endPos
            }
            "binary" -> {
                val bytes = try { Base64.getDecoder().decode(content) } catch (e: Exception) { byteArrayOf() }
                LLSDBinary(bytes) to endPos
            }
            "date" -> {
                val date = parseLlsdDate(content) ?: Date()
                LLSDDate(date) to endPos
            }
            "uri" -> LLSDURI(content) to endPos
            "map" -> {
                val map = LLSDMap()
                var mapPos = 0
                while (mapPos < content.length) {
                    // Skip whitespace
                    while (mapPos < content.length && content[mapPos].isWhitespace()) mapPos++
                    if (mapPos >= content.length) break

                    // Find key
                    val keyStart = content.indexOf("<key>", mapPos, ignoreCase = true)
                    if (keyStart == -1) break
                    val keyEnd = content.indexOf("</key>", keyStart, ignoreCase = true)
                    if (keyEnd == -1) break
                    val key = content.substring(keyStart + 5, keyEnd)

                    // Find value
                    mapPos = keyEnd + 6
                    val (value, newPos) = parseXMLElement(content, mapPos)
                    map[key] = value
                    mapPos = newPos
                }
                map to endPos
            }
            "array" -> {
                val array = LLSDArray()
                var arrayPos = 0
                while (arrayPos < content.length) {
                    // Skip whitespace
                    while (arrayPos < content.length && content[arrayPos].isWhitespace()) arrayPos++
                    if (arrayPos >= content.length) break
                    if (content[arrayPos] != '<') break

                    val (value, newPos) = parseXMLElement(content, arrayPos)
                    if (value != LLSDUndefined || content.substring(arrayPos).startsWith("<undef", ignoreCase = true)) {
                        array.add(value)
                    }
                    if (newPos <= arrayPos) break
                    arrayPos = newPos
                }
                array to endPos
            }
            else -> LLSDUndefined to endPos
        }
    }

    private fun unescapeXML(s: String): String {
        return s
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&amp;", "&")
            .replace("&quot;", "\"")
            .replace("&apos;", "'")
    }

    fun parseLlsdDate(value: String): Date? {
        val patterns = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        )
        for (pattern in patterns) {
            val formatter = java.text.SimpleDateFormat(pattern, Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }
            try {
                return formatter.parse(value)
            } catch (e: Exception) {
                // Try the next pattern.
            }
        }
        return null
    }
}
