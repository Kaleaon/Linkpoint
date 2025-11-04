package com.linkpoint.slproto.llsd

import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.https.LLSDContentTypeDetector
import com.linkpoint.slproto.llsd.types.LLSDArray
import com.linkpoint.slproto.llsd.types.LLSDBinary
import com.linkpoint.slproto.llsd.types.LLSDBoolean
import com.linkpoint.slproto.llsd.types.LLSDDate
import com.linkpoint.slproto.llsd.types.LLSDDouble
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDURI
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.llsd.types.LLSDUndefined
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Date
import java.util.UUID
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

object LLSDStreamingParser {

    interface LLSDContentHandler {
        @Throws(LLSDXMLException::class)
        fun onArrayBegin(name: String?): LLSDContentHandler? = null

        @Throws(LLSDXMLException::class)
        fun onArrayEnd(name: String?) {}

        @Throws(LLSDXMLException::class)
        fun onMapBegin(name: String?): LLSDContentHandler? = null

        @Throws(LLSDXMLException::class, InterruptedException::class)
        fun onMapEnd(name: String?) {}

        @Throws(LLSDXMLException::class, LLSDValueTypeException::class)
        fun onPrimitiveValue(name: String?, value: LLSDNode)
    }

    open class LLSDDefaultContentHandler : LLSDContentHandler {
        override fun onArrayBegin(name: String?): LLSDContentHandler? = LLSDDefaultContentHandler()
        override fun onMapBegin(name: String?): LLSDContentHandler? = LLSDDefaultContentHandler()
        override fun onPrimitiveValue(name: String?, value: LLSDNode) {}
    }

    @Throws(LLSDXMLException::class)
    fun parseAny(input: InputStream, contentType: String?, handler: LLSDContentHandler) {
        try {
            val buffered = BufferedInputStream(input, 65_536)
            when (LLSDContentTypeDetector.detectContentType(buffered, contentType)) {
                LLSDContentTypeDetector.LLSDContentType.llsdBinary -> parseBinary(DataInputStream(buffered), handler)
                LLSDContentTypeDetector.LLSDContentType.llsdXML -> parseXML(buffered, "UTF-8", handler)
            }
        } catch (io: IOException) {
            throw LLSDXMLException("I/O error", io)
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseBinary(stream: DataInputStream, handler: LLSDContentHandler) {
        try {
            parseBinaryNode(1, null, stream, handler)
        } catch (ex: LLSDValueTypeException) {
            throw LLSDXMLException("Invalid value type", ex)
        } catch (ex: InterruptedException) {
            throw LLSDXMLException("Interrupted", ex)
        } catch (io: IOException) {
            throw LLSDXMLException("I/O error", io)
        }
    }

    @Throws(LLSDXMLException::class, LLSDValueTypeException::class, InterruptedException::class, IOException::class)
    private fun parseBinaryNode(count: Int, name: String?, stream: DataInputStream, handler: LLSDContentHandler) {
        var remaining = count
        while (remaining > 0) {
            when (val marker = stream.readByte().toInt()) {
                '!'.code -> {
                    handler.onPrimitiveValue(name, LLSDUndefined())
                }
                '1'.code -> handler.onPrimitiveValue(name, LLSDBoolean(true))
                '0'.code -> handler.onPrimitiveValue(name, LLSDBoolean(false))
                'i'.code -> handler.onPrimitiveValue(name, LLSDInt(stream.readInt()))
                'r'.code -> handler.onPrimitiveValue(name, LLSDDouble(stream.readDouble()))
                'u'.code -> handler.onPrimitiveValue(name, LLSDUUID(UUID(stream.readLong(), stream.readLong())))
                's'.code -> handler.onPrimitiveValue(name, LLSDString(readVariableString(stream)))
                'd'.code -> handler.onPrimitiveValue(name, LLSDDate(Date(Math.round(stream.readDouble() * 1000.0))))
                'l'.code -> handler.onPrimitiveValue(name, LLSDURI(readVariableString(stream)))
                'b'.code -> handler.onPrimitiveValue(name, LLSDBinary(readBinaryBlob(stream)))
                '['.code -> {
                    val elements = stream.readInt()
                    val arrayHandler = handler.onArrayBegin(name) ?: handler
                    parseBinaryNode(elements, null, stream, arrayHandler)
                    val end = stream.readByte().toInt()
                    if (end != ']'.code) {
                        throw LLSDXMLException("Array terminator expected")
                    }
                    arrayHandler.onArrayEnd(name)
                }
                '{'.code -> {
                    val elements = stream.readInt()
                    val mapHandler = handler.onMapBegin(name) ?: handler
                    repeat(elements) {
                        val keyMarker = stream.readByte().toInt()
                        if (keyMarker != 'k'.code) {
                            throw LLSDXMLException("Map key expected")
                        }
                        val key = readVariableString(stream)
                        parseBinaryNode(1, key, stream, mapHandler)
                    }
                    val end = stream.readByte().toInt()
                    if (end != '}'.code) {
                        throw LLSDXMLException("Map terminator expected")
                    }
                    mapHandler.onMapEnd(name)
                }
                '<'.code -> {
                    skipComment(stream)
                    continue
                }
                else -> throw LLSDXMLException("Unknown LLSD element 0x${marker.toString(16)}")
            }
            remaining--
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseXML(input: InputStream, encoding: String?, handler: LLSDContentHandler) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(input, encoding)
            parser.nextTag()
            parser.require(XmlPullParser.START_TAG, null, "llsd")
            parser.nextTag()
            parseXMLNode(null, parser, handler)
            parser.require(XmlPullParser.END_TAG, null, "llsd")
        } catch (ex: XmlPullParserException) {
            Debug.Log("XmlPullParserException: ${ex.message}")
            throw LLSDXMLException("Malformed XML", ex)
        } catch (ex: IOException) {
            throw LLSDXMLException("Input stream error", ex)
        } catch (ex: LLSDValueTypeException) {
            throw LLSDXMLException("Malformed XML", ex)
        } catch (ex: InterruptedException) {
            throw LLSDXMLException("Interrupted", ex)
        }
    }

    @Throws(
        LLSDXMLException::class,
        XmlPullParserException::class,
        IOException::class,
        LLSDValueTypeException::class,
        InterruptedException::class
    )
    private fun parseXMLNode(name: String?, parser: XmlPullParser, handler: LLSDContentHandler) {
        val type = LLSDNodeType.fromTag(parser.name)
            ?: throw LLSDXMLException("Unknown tag: ${parser.name}")

        when (type) {
            LLSDNodeType.llsdArray -> {
                val arrayHandler = handler.onArrayBegin(name) ?: handler
                parser.nextTag()
                while (parser.eventType != XmlPullParser.END_TAG) {
                    parseXMLNode(null, parser, arrayHandler)
                }
                arrayHandler.onArrayEnd(name)
                parser.nextTag()
            }
            LLSDNodeType.llsdBinary -> {
                handler.onPrimitiveValue(name, LLSDBinary(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdBoolean -> {
                handler.onPrimitiveValue(name, LLSDBoolean(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdDate -> {
                handler.onPrimitiveValue(name, LLSDDate(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdDouble -> {
                handler.onPrimitiveValue(name, LLSDDouble(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdInteger -> {
                handler.onPrimitiveValue(name, LLSDInt(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdKey -> throw LLSDXMLException("Unexpected tag: ${parser.name}")
            LLSDNodeType.llsdMap -> {
                val mapHandler = handler.onMapBegin(name) ?: handler
                parser.nextTag()
                while (parser.eventType != XmlPullParser.END_TAG) {
                    val keyName = parser.name
                    if (!keyName.equals("key", ignoreCase = true)) {
                        throw LLSDXMLException("Unexpected tag: $keyName")
                    }
                    val key = parser.nextText()
                    parser.nextTag()
                    parseXMLNode(key, parser, mapHandler)
                }
                mapHandler.onMapEnd(name)
                parser.nextTag()
            }
            LLSDNodeType.llsdRoot -> throw LLSDXMLException("Unexpected tag: ${parser.name}")
            LLSDNodeType.llsdString -> {
                handler.onPrimitiveValue(name, LLSDString(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdURI -> {
                handler.onPrimitiveValue(name, LLSDURI(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdUUID -> {
                handler.onPrimitiveValue(name, LLSDUUID(parser.nextText()))
                parser.nextTag()
            }
            LLSDNodeType.llsdUndef -> {
                parser.nextTag()
                handler.onPrimitiveValue(name, LLSDUndefined())
            }
        }
    }

    @Throws(IOException::class)
    private fun readVariableString(stream: DataInputStream): String {
        val length = stream.readInt()
        if (length == 0) {
            return ""
        }
        val bytes = ByteArray(length)
        stream.readFully(bytes)
        return SLMessage.stringFromVariableUTF(bytes)
    }

    @Throws(IOException::class)
    private fun readBinaryBlob(stream: DataInputStream): ByteArray {
        val length = stream.readInt()
        val data = ByteArray(length)
        stream.readFully(data)
        return data
    }

    @Throws(IOException::class)
    private fun skipComment(stream: DataInputStream) {
        var previous = -1
        while (true) {
            val current = stream.readByte().toInt()
            if (previous == '-'.code && current == '>'.code) {
                return
            }
            previous = current
        }
    }
}
