package com.linkpoint.protocol.llsd

import java.io.ByteArrayInputStream
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

/**
 * LLSD Parser - handles Binary, XML, and Notation formats
 */
object LLSDParser {
    
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
     * Parse LLSD Binary format
     */
    fun parseBinary(data: ByteArray): LLSDValue {
        val stream = ByteArrayInputStream(data)
        return parseBinaryValue(stream)
    }
    
    private fun parseBinaryValue(stream: InputStream): LLSDValue {
        val marker = stream.read()
        if (marker == -1) return LLSDUndefined
        
        return when (marker.toChar()) {
            LLSDValue.MARKER_UNDEF -> LLSDUndefined
            LLSDValue.MARKER_TRUE -> LLSDBoolean(true)
            LLSDValue.MARKER_FALSE -> LLSDBoolean(false)
            LLSDValue.MARKER_INTEGER -> {
                val bytes = ByteArray(4)
                stream.read(bytes)
                LLSDInteger(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).int)
            }
            LLSDValue.MARKER_REAL -> {
                val bytes = ByteArray(8)
                stream.read(bytes)
                LLSDReal(ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).double)
            }
            LLSDValue.MARKER_UUID -> {
                val bytes = ByteArray(16)
                stream.read(bytes)
                val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN)
                LLSDUUID(UUID(buffer.long, buffer.long))
            }
            LLSDValue.MARKER_STRING, 's' -> {
                val lenBytes = ByteArray(4)
                stream.read(lenBytes)
                val len = ByteBuffer.wrap(lenBytes).order(ByteOrder.BIG_ENDIAN).int
                val bytes = ByteArray(len)
                stream.read(bytes)
                LLSDString(String(bytes, Charsets.UTF_8))
            }
            LLSDValue.MARKER_BINARY, 'b' -> {
                val lenBytes = ByteArray(4)
                stream.read(lenBytes)
                val len = ByteBuffer.wrap(lenBytes).order(ByteOrder.BIG_ENDIAN).int
                val bytes = ByteArray(len)
                stream.read(bytes)
                LLSDBinary(bytes)
            }
            LLSDValue.MARKER_DATE -> {
                val bytes = ByteArray(8)
                stream.read(bytes)
                val seconds = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).double
                LLSDDate((seconds * 1000).toLong())
            }
            LLSDValue.MARKER_URI, 'l' -> {
                val lenBytes = ByteArray(4)
                stream.read(lenBytes)
                val len = ByteBuffer.wrap(lenBytes).order(ByteOrder.BIG_ENDIAN).int
                val bytes = ByteArray(len)
                stream.read(bytes)
                LLSDURI(String(bytes, Charsets.UTF_8))
            }
            LLSDValue.MARKER_MAP, '{' -> {
                val map = LLSDMap()
                while (true) {
                    val keyMarker = stream.read()
                    if (keyMarker == -1 || keyMarker.toChar() == LLSDValue.MARKER_MAP_END) break
                    
                    if (keyMarker.toChar() == 'k') {
                        val lenBytes = ByteArray(4)
                        stream.read(lenBytes)
                        val len = ByteBuffer.wrap(lenBytes).order(ByteOrder.BIG_ENDIAN).int
                        val keyBytes = ByteArray(len)
                        stream.read(keyBytes)
                        val key = String(keyBytes, Charsets.UTF_8)
                        val value = parseBinaryValue(stream)
                        map[key] = value
                    }
                }
                map
            }
            LLSDValue.MARKER_ARRAY, '[' -> {
                val array = LLSDArray()
                while (true) {
                    val peek = stream.read()
                    if (peek == -1 || peek.toChar() == LLSDValue.MARKER_ARRAY_END) break
                    
                    // We need to unread this byte - simulate by wrapping
                    val wrappedStream = ByteArrayInputStream(byteArrayOf(peek.toByte()) + stream.readBytes())
                    val value = parseBinaryValue(wrappedStream)
                    array.add(value)
                }
                array
            }
            else -> LLSDUndefined
        }
    }
    
    /**
     * Parse LLSD XML format
     */
    fun parseXML(xml: String): LLSDValue {
        // Simple XML parser for LLSD
        val cleaned = xml.trim()
        
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
                val date = try {
                    java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).parse(content)
                } catch (e: Exception) { Date() }
                LLSDDate(date ?: Date()) to endPos
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
}
