package com.linkpoint.protocol.llsd

import android.util.Log
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Date
import java.util.Base64
import java.util.UUID
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

object LLSDStreamingParser {
    private const val TAG = "LLSDStreamingParser"

    interface LLSDContentHandler {
        fun onArrayBegin(name: String?): LLSDContentHandler?
        fun onArrayEnd(name: String?)
        fun onMapBegin(name: String?): LLSDContentHandler?
        fun onMapEnd(name: String?)
        fun onPrimitiveValue(name: String?, value: LLSDValue)
    }

    open class LLSDDefaultContentHandler : LLSDContentHandler {
        override fun onArrayBegin(name: String?): LLSDContentHandler = LLSDDefaultContentHandler()
        override fun onArrayEnd(name: String?) = Unit
        override fun onMapBegin(name: String?): LLSDContentHandler = LLSDDefaultContentHandler()
        override fun onMapEnd(name: String?) = Unit
        override fun onPrimitiveValue(name: String?, value: LLSDValue) = Unit
    }

    @Throws(IOException::class)
    fun parseAny(input: InputStream, contentType: String?, handler: LLSDContentHandler) {
        val buffered = BufferedInputStream(input, 65536)
        when (LLSDContentTypeDetector.detect(buffered, contentType)) {
            LLSDContentTypeDetector.LLSDContentType.LLSD_BINARY -> parseBinary(DataInputStream(buffered), handler)
            LLSDContentTypeDetector.LLSDContentType.LLSD_XML -> parseXML(buffered, "UTF-8", handler)
        }
    }

    @Throws(IOException::class)
    fun parseBinary(input: DataInputStream, handler: LLSDContentHandler) {
        parseBinaryNode(1, null, input, handler)
    }

    @Throws(IOException::class)
    private fun parseBinaryNode(
        count: Int,
        name: String?,
        input: DataInputStream,
        handler: LLSDContentHandler
    ) {
        var remaining = count
        while (remaining > 0) {
            when (val marker = input.readByte().toInt().toChar()) {
                LLSDValue.MARKER_UNDEF -> {
                    handler.onPrimitiveValue(name, LLSDUndefined)
                    remaining--
                }
                LLSDValue.MARKER_TRUE -> {
                    handler.onPrimitiveValue(name, LLSDBoolean(true))
                    remaining--
                }
                LLSDValue.MARKER_FALSE -> {
                    handler.onPrimitiveValue(name, LLSDBoolean(false))
                    remaining--
                }
                LLSDValue.MARKER_INTEGER -> {
                    val bytes = ByteArray(4)
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDInteger(java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN).int))
                    remaining--
                }
                LLSDValue.MARKER_REAL -> {
                    val bytes = ByteArray(8)
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDReal(java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN).double))
                    remaining--
                }
                LLSDValue.MARKER_UUID -> {
                    val bytes = ByteArray(16)
                    input.readFully(bytes)
                    val buffer = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN)
                    handler.onPrimitiveValue(name, LLSDUUID(UUID(buffer.long, buffer.long)))
                    remaining--
                }
                LLSDValue.MARKER_STRING -> {
                    val length = input.readInt()
                    val bytes = ByteArray(length)
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDString(String(bytes, Charsets.UTF_8)))
                    remaining--
                }
                LLSDValue.MARKER_BINARY -> {
                    val length = input.readInt()
                    val bytes = ByteArray(length)
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDBinary(bytes))
                    remaining--
                }
                LLSDValue.MARKER_DATE -> {
                    val bytes = ByteArray(8)
                    input.readFully(bytes)
                    val seconds = java.nio.ByteBuffer.wrap(bytes).order(java.nio.ByteOrder.BIG_ENDIAN).double
                    handler.onPrimitiveValue(name, LLSDDate((seconds * 1000).toLong()))
                    remaining--
                }
                LLSDValue.MARKER_URI -> {
                    val length = input.readInt()
                    val bytes = ByteArray(length)
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDURI(String(bytes, Charsets.UTF_8)))
                    remaining--
                }
                LLSDValue.MARKER_MAP -> {
                    var mapHandler = handler.onMapBegin(name) ?: handler
                    while (true) {
                        val keyMarker = input.readByte().toInt().toChar()
                        if (keyMarker == LLSDValue.MARKER_MAP_END) break
                        if (keyMarker == 'k') {
                            val len = input.readInt()
                            val keyBytes = ByteArray(len)
                            input.readFully(keyBytes)
                            val key = String(keyBytes, Charsets.UTF_8)
                            parseBinaryNode(1, key, input, mapHandler)
                        }
                    }
                    mapHandler.onMapEnd(name)
                    remaining--
                }
                LLSDValue.MARKER_ARRAY -> {
                    var arrayHandler = handler.onArrayBegin(name) ?: handler
                    while (true) {
                        input.mark(1)
                        val peek = input.readByte().toInt().toChar()
                        if (peek == LLSDValue.MARKER_ARRAY_END) break
                        input.reset()
                        parseBinaryNode(1, null, input, arrayHandler)
                    }
                    arrayHandler.onArrayEnd(name)
                    remaining--
                }
                else -> {
                    Log.w(TAG, "Unknown LLSD marker: $marker")
                    remaining--
                }
            }
        }
    }

    @Throws(IOException::class)
    fun parseXML(input: InputStream, encoding: String?, handler: LLSDContentHandler) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(input, encoding)
            parser.nextTag()
            parser.require(2, null, "llsd")
            parser.nextTag()
            parseXMLNode(null, parser, handler)
            parser.require(3, null, "llsd")
        } catch (e: XmlPullParserException) {
            Log.e(TAG, "XML parse error", e)
            throw IOException("Malformed XML", e)
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun parseXMLNode(name: String?, parser: XmlPullParser, handler: LLSDContentHandler) {
        val tag = parser.name?.lowercase() ?: return
        when (tag) {
            "array" -> {
                val arrayHandler = handler.onArrayBegin(name) ?: handler
                parser.nextTag()
                while (parser.eventType != 3) {
                    parseXMLNode(null, parser, arrayHandler)
                }
                arrayHandler.onArrayEnd(name)
                parser.nextTag()
            }
            "map" -> {
                val mapHandler = handler.onMapBegin(name) ?: handler
                parser.nextTag()
                while (parser.eventType != 3) {
                    val keyTag = parser.name
                    if (!keyTag.equals("key", ignoreCase = true)) {
                        throw XmlPullParserException("Unexpected tag: $keyTag")
                    }
                    val key = parser.nextText()
                    parser.nextTag()
                    parseXMLNode(key, parser, mapHandler)
                }
                mapHandler.onMapEnd(name)
                parser.nextTag()
            }
            "boolean" -> {
                val text = parser.nextText()
                val isTrue = text == "1" || text.equals("true", ignoreCase = true)
                handler.onPrimitiveValue(name, LLSDBoolean(isTrue))
                parser.nextTag()
            }
            "integer" -> {
                handler.onPrimitiveValue(name, LLSDInteger(parser.nextText().toIntOrNull() ?: 0))
                parser.nextTag()
            }
            "real" -> {
                handler.onPrimitiveValue(name, LLSDReal(parser.nextText().toDoubleOrNull() ?: 0.0))
                parser.nextTag()
            }
            "string" -> {
                handler.onPrimitiveValue(name, LLSDString(parser.nextText()))
                parser.nextTag()
            }
            "uuid" -> {
                val uuid = runCatching { UUID.fromString(parser.nextText()) }.getOrDefault(UUID(0, 0))
                handler.onPrimitiveValue(name, LLSDUUID(uuid))
                parser.nextTag()
            }
            "binary" -> {
                val bytes = runCatching { Base64.getDecoder().decode(parser.nextText()) }.getOrDefault(byteArrayOf())
                handler.onPrimitiveValue(name, LLSDBinary(bytes))
                parser.nextTag()
            }
            "date" -> {
                val text = parser.nextText()
                val parsed = LLSDParser.parseLlsdDate(text) ?: Date()
                handler.onPrimitiveValue(name, LLSDDate(parsed))
                parser.nextTag()
            }
            "uri" -> {
                handler.onPrimitiveValue(name, LLSDURI(parser.nextText()))
                parser.nextTag()
            }
            "undef" -> {
                handler.onPrimitiveValue(name, LLSDUndefined)
                parser.nextTag()
            }
            else -> {
                parser.nextTag()
            }
        }
    }

    @Throws(IOException::class)
    fun parseAnyToValue(input: InputStream, contentType: String?): LLSDValue {
        val builder = LLSDValueBuilder()
        parseAny(input, contentType, builder)
        return builder.root ?: LLSDUndefined
    }

    private class LLSDValueBuilder : LLSDContentHandler {
        var root: LLSDValue? = null
        private val stack = ArrayDeque<Any>()

        override fun onArrayBegin(name: String?): LLSDContentHandler {
            val array = LLSDArray()
            addValue(name, array)
            stack.addLast(array)
            return this
        }

        override fun onArrayEnd(name: String?) {
            if (stack.isNotEmpty()) {
                stack.removeLast()
            }
        }

        override fun onMapBegin(name: String?): LLSDContentHandler {
            val map = LLSDMap()
            addValue(name, map)
            stack.addLast(map)
            return this
        }

        override fun onMapEnd(name: String?) {
            if (stack.isNotEmpty()) {
                stack.removeLast()
            }
        }

        override fun onPrimitiveValue(name: String?, value: LLSDValue) {
            addValue(name, value)
        }

        private fun addValue(name: String?, value: LLSDValue) {
            val current = stack.lastOrNull()
            if (current == null) {
                root = value
                return
            }
            when (current) {
                is LLSDArray -> current.add(value)
                is LLSDMap -> {
                    val key = name ?: return
                    current[key] = value
                }
            }
        }
    }
}
