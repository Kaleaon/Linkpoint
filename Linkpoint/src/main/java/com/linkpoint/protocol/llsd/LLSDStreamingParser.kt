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
    data class ParseLimits(
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
        var collectionElementsRead: Int = 0,
    )

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
    fun parseAny(
        input: InputStream,
        contentType: String?,
        handler: LLSDContentHandler,
        limits: ParseLimits = ParseLimits(),
    ) {
        val counted = CountingInputStream(input, limits.maxTotalBytes)
        val buffered = BufferedInputStream(counted, 65536)
        val state = ParseLimitsState()
        when (LLSDContentTypeDetector.detect(buffered, contentType)) {
            LLSDContentTypeDetector.LLSDContentType.LLSD_BINARY ->
                parseBinaryInternal(DataInputStream(buffered), handler, limits, state)
            LLSDContentTypeDetector.LLSDContentType.LLSD_XML ->
                parseXMLInternal(buffered, "UTF-8", handler, limits, state)
        }
    }

    @Throws(IOException::class)
    fun parseBinary(
        input: DataInputStream,
        handler: LLSDContentHandler,
        limits: ParseLimits = ParseLimits(),
    ) {
        val countedInput = DataInputStream(CountingInputStream(input, limits.maxTotalBytes))
        parseBinaryInternal(countedInput, handler, limits, ParseLimitsState())
    }

    @Throws(IOException::class)
    private fun parseBinaryInternal(
        input: DataInputStream,
        handler: LLSDContentHandler,
        limits: ParseLimits,
        state: ParseLimitsState,
    ) {
        parseBinaryNode(1, null, input, handler, limits, state)
    }

    @Throws(IOException::class)
    private fun parseBinaryNode(
        count: Int,
        name: String?,
        input: DataInputStream,
        handler: LLSDContentHandler,
        limits: ParseLimits,
        state: ParseLimitsState,
    ) {
        if (state.currentDepth >= limits.maxNestingDepth) {
            throw IOException("LLSD parse limit exceeded: maximum nesting depth ${limits.maxNestingDepth}.")
        }
        state.currentDepth++
        var remaining = count
        try {
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
                    validateLength(length, limits.maxStringBytes, "string")
                    val bytes = ByteArray(length)
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDString(String(bytes, Charsets.UTF_8)))
                    remaining--
                }
                LLSDValue.MARKER_BINARY -> {
                    val length = input.readInt()
                    validateLength(length, limits.maxBinaryBytes, "binary")
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
                    validateLength(length, limits.maxStringBytes, "uri")
                    val bytes = ByteArray(length)
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDURI(String(bytes, Charsets.UTF_8)))
                    remaining--
                }
                LLSDValue.MARKER_MAP -> {
                    val mapHandler = handler.onMapBegin(name) ?: handler
                    var entries = 0
                    while (true) {
                        val keyMarker = input.readByte().toInt().toChar()
                        if (keyMarker == LLSDValue.MARKER_MAP_END) break
                        if (keyMarker != 'k') {
                            throw IOException("Malformed binary LLSD map: expected key marker 'k', got '$keyMarker'.")
                        }
                        entries++
                        if (entries > limits.maxMapEntries) {
                            throw IOException("LLSD parse limit exceeded: map entries exceed ${limits.maxMapEntries}.")
                        }
                        incrementCollectionElementCount(state, limits)
                        val len = input.readInt()
                        validateLength(len, limits.maxStringBytes, "map key")
                        val keyBytes = ByteArray(len)
                        input.readFully(keyBytes)
                        val key = String(keyBytes, Charsets.UTF_8)
                        parseBinaryNode(1, key, input, mapHandler, limits, state)
                    }
                    mapHandler.onMapEnd(name)
                    remaining--
                }
                LLSDValue.MARKER_ARRAY -> {
                    val arrayHandler = handler.onArrayBegin(name) ?: handler
                    var elements = 0
                    while (true) {
                        input.mark(1)
                        val peek = input.readByte().toInt().toChar()
                        if (peek == LLSDValue.MARKER_ARRAY_END) break
                        input.reset()
                        elements++
                        if (elements > limits.maxArrayLength) {
                            throw IOException("LLSD parse limit exceeded: array length exceeds ${limits.maxArrayLength}.")
                        }
                        incrementCollectionElementCount(state, limits)
                        parseBinaryNode(1, null, input, arrayHandler, limits, state)
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
        } finally {
            state.currentDepth--
        }
    }

    @Throws(IOException::class)
    fun parseXML(
        input: InputStream,
        encoding: String?,
        handler: LLSDContentHandler,
        limits: ParseLimits = ParseLimits(),
    ) {
        parseXMLInternal(
            CountingInputStream(input, limits.maxTotalBytes),
            encoding,
            handler,
            limits,
            ParseLimitsState(),
        )
    }

    @Throws(IOException::class)
    private fun parseXMLInternal(
        input: InputStream,
        encoding: String?,
        handler: LLSDContentHandler,
        limits: ParseLimits,
        state: ParseLimitsState,
    ) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(input, encoding)
            parser.nextTag()
            parser.require(2, null, "llsd")
            parser.nextTag()
            parseXMLNode(null, parser, handler, limits, state)
            parser.require(3, null, "llsd")
        } catch (e: XmlPullParserException) {
            Log.e(TAG, "XML parse error", e)
            throw IOException("Malformed XML", e)
        }
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun parseXMLNode(
        name: String?,
        parser: XmlPullParser,
        handler: LLSDContentHandler,
        limits: ParseLimits,
        state: ParseLimitsState,
    ) {
        val tag = parser.name?.lowercase() ?: return
        if (state.currentDepth >= limits.maxNestingDepth) {
            throw IOException("LLSD parse limit exceeded: maximum nesting depth ${limits.maxNestingDepth}.")
        }
        state.currentDepth++
        try {
            when (tag) {
            "array" -> {
                val arrayHandler = handler.onArrayBegin(name) ?: handler
                var elements = 0
                parser.nextTag()
                while (parser.eventType != 3) {
                    elements++
                    if (elements > limits.maxArrayLength) {
                        throw IOException("LLSD parse limit exceeded: array length exceeds ${limits.maxArrayLength}.")
                    }
                    incrementCollectionElementCount(state, limits)
                    parseXMLNode(null, parser, arrayHandler, limits, state)
                }
                arrayHandler.onArrayEnd(name)
                parser.nextTag()
            }
            "map" -> {
                val mapHandler = handler.onMapBegin(name) ?: handler
                var entries = 0
                parser.nextTag()
                while (parser.eventType != 3) {
                    val keyTag = parser.name
                    if (!keyTag.equals("key", ignoreCase = true)) {
                        throw XmlPullParserException("Unexpected tag: $keyTag")
                    }
                    entries++
                    if (entries > limits.maxMapEntries) {
                        throw IOException("LLSD parse limit exceeded: map entries exceed ${limits.maxMapEntries}.")
                    }
                    incrementCollectionElementCount(state, limits)
                    val key = parser.nextText()
                    if (key.toByteArray(Charsets.UTF_8).size > limits.maxStringBytes) {
                        throw IOException("LLSD parse limit exceeded: map key exceeds ${limits.maxStringBytes} bytes.")
                    }
                    parser.nextTag()
                    parseXMLNode(key, parser, mapHandler, limits, state)
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
                val text = parser.nextText()
                val size = text.toByteArray(Charsets.UTF_8).size
                if (size > limits.maxStringBytes) {
                    throw IOException("LLSD parse limit exceeded: string exceeds ${limits.maxStringBytes} bytes.")
                }
                handler.onPrimitiveValue(name, LLSDString(text))
                parser.nextTag()
            }
            "uuid" -> {
                val uuid = runCatching { UUID.fromString(parser.nextText()) }.getOrDefault(UUID(0, 0))
                handler.onPrimitiveValue(name, LLSDUUID(uuid))
                parser.nextTag()
            }
            "binary" -> {
                val bytes = runCatching { Base64.getDecoder().decode(parser.nextText()) }.getOrDefault(byteArrayOf())
                if (bytes.size > limits.maxBinaryBytes) {
                    throw IOException("LLSD parse limit exceeded: binary exceeds ${limits.maxBinaryBytes} bytes.")
                }
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
        } finally {
            state.currentDepth--
        }
    }

    @Throws(IOException::class)
    fun parseAnyToValue(
        input: InputStream,
        contentType: String?,
        limits: ParseLimits = ParseLimits(),
    ): LLSDValue {
        val builder = LLSDValueBuilder()
        parseAny(input, contentType, builder, limits)
        return builder.root ?: LLSDUndefined
    }

    private fun validateLength(length: Int, maxLength: Int, fieldName: String) {
        if (length < 0) {
            throw IOException("Malformed binary LLSD: negative $fieldName length ($length).")
        }
        if (length > maxLength) {
            throw IOException("LLSD parse limit exceeded: $fieldName length $length exceeds $maxLength.")
        }
    }

    private fun incrementCollectionElementCount(state: ParseLimitsState, limits: ParseLimits) {
        state.collectionElementsRead++
        if (state.collectionElementsRead > limits.maxCollectionElementsTotal) {
            throw IOException(
                "LLSD parse limit exceeded: total collection elements exceed ${limits.maxCollectionElementsTotal}."
            )
        }
    }

    private class CountingInputStream(
        private val delegate: InputStream,
        private val maxBytes: Int,
    ) : InputStream() {
        private var bytesRead: Int = 0

        override fun read(): Int {
            val value = delegate.read()
            if (value != -1) {
                increment(1)
            }
            return value
        }

        override fun read(b: ByteArray, off: Int, len: Int): Int {
            val count = delegate.read(b, off, len)
            if (count > 0) {
                increment(count)
            }
            return count
        }

        override fun markSupported(): Boolean = delegate.markSupported()
        override fun mark(readlimit: Int) = delegate.mark(readlimit)
        override fun reset() = delegate.reset()
        override fun available(): Int = delegate.available()
        override fun skip(n: Long): Long = delegate.skip(n)
        override fun close() = delegate.close()

        private fun increment(count: Int) {
            bytesRead += count
            if (bytesRead > maxBytes) {
                throw IOException("LLSD parse limit exceeded: total bytes exceed $maxBytes.")
            }
        }
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
