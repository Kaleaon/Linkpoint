package com.lumiyaviewer.lumiya.slproto.llsd

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector
import com.lumiyaviewer.lumiya.slproto.llsd.types.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.*
import java.util.UUID
import kotlin.math.roundToLong

class LLSDStreamingParser {

    interface LLSDContentHandler {
        @Throws(LLSDXMLException::class)
        fun onArrayBegin(name: String?): LLSDContentHandler?

        @Throws(LLSDXMLException::class)
        fun onArrayEnd(name: String?)

        @Throws(LLSDXMLException::class)
        fun onMapBegin(name: String?): LLSDContentHandler?

        @Throws(LLSDXMLException::class, InterruptedException::class)
        fun onMapEnd(name: String?)

        @Throws(LLSDXMLException::class, LLSDValueTypeException::class)
        fun onPrimitiveValue(name: String?, node: LLSDNode)
    }

    class LLSDDefaultContentHandler : LLSDContentHandler {
        override fun onArrayBegin(name: String?): LLSDContentHandler? = LLSDDefaultContentHandler()
        override fun onArrayEnd(name: String?) {}
        override fun onMapBegin(name: String?): LLSDContentHandler? = LLSDDefaultContentHandler()
        override fun onMapEnd(name: String?) {}
        override fun onPrimitiveValue(name: String?, node: LLSDNode) {}
    }

    @Throws(LLSDXMLException::class)
    fun parseAny(inputStream: InputStream, name: String?, handler: LLSDContentHandler) {
        try {
            val buffered = if (inputStream.markSupported()) inputStream else BufferedInputStream(inputStream, 65536)
            buffered.mark(65536)
            val type = LLSDContentTypeDetector.DetectContentType(buffered, name ?: "")
            buffered.reset()

            when (type) {
                LLSDContentTypeDetector.LLSDContentType.llsdBinary -> parseBinary(DataInputStream(buffered), handler)
                LLSDContentTypeDetector.LLSDContentType.llsdXML -> parseXML(buffered, "UTF-8", handler)
                else -> throw LLSDXMLException("Unknown content type")
            }
        } catch (e: IOException) {
            throw LLSDXMLException("I/O error: ${e.message}", e)
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseBinary(dataIn: DataInputStream, handler: LLSDContentHandler) {
        try {
            parseBinaryNode(1, null, dataIn, handler)
        } catch (e: Exception) {
            when (e) {
                is LLSDValueTypeException -> throw LLSDXMLException("Invalid value type", e)
                is InterruptedException -> throw LLSDXMLException("Interrupted", e)
                is IOException -> throw LLSDXMLException("I/O error", e)
                else -> throw e
            }
        }
    }

    @Throws(LLSDXMLException::class, LLSDValueTypeException::class, InterruptedException::class, IOException::class)
    private fun parseBinaryNode(count: Int, name: String?, dataIn: DataInputStream, handler: LLSDContentHandler) {
        var c = count
        while (c > 0) {
            val type = dataIn.readByte().toInt()
            when (type) {
                '!'.code -> { // Undefined
                    handler.onPrimitiveValue(name, LLSDUndefined())
                }
                '1'.code -> { // True
                    handler.onPrimitiveValue(name, LLSDBoolean(true))
                }
                '0'.code -> { // False
                    handler.onPrimitiveValue(name, LLSDBoolean(false))
                }
                'i'.code -> { // Integer
                    val v = dataIn.readInt()
                    handler.onPrimitiveValue(name, LLSDInt(v))
                }
                'r'.code -> { // Real
                    val v = dataIn.readDouble()
                    handler.onPrimitiveValue(name, LLSDDouble(v))
                }
                'u'.code -> { // UUID
                    val msb = dataIn.readLong()
                    val lsb = dataIn.readLong()
                    handler.onPrimitiveValue(name, LLSDUUID(UUID(msb, lsb)))
                }
                's'.code -> { // String
                    val len = dataIn.readInt()
                    val b = ByteArray(len)
                    dataIn.readFully(b)
                    handler.onPrimitiveValue(name, LLSDString(SLMessage.stringFromVariableUTF(b)))
                }
                'l'.code -> { // URI
                    val len = dataIn.readInt()
                    val b = ByteArray(len)
                    dataIn.readFully(b)
                    handler.onPrimitiveValue(name, LLSDURI(SLMessage.stringFromVariableUTF(b)))
                }
                'd'.code -> { // Date
                    val v = dataIn.readDouble()
                    handler.onPrimitiveValue(name, LLSDDate(java.util.Date((v * 1000.0).roundToLong())))
                }
                'b'.code -> { // Binary
                    val len = dataIn.readInt()
                    val b = ByteArray(len)
                    dataIn.readFully(b)
                    handler.onPrimitiveValue(name, LLSDBinary(b))
                }
                '['.code -> { // Array
                    val len = dataIn.readInt()
                    val subHandler = handler.onArrayBegin(name) ?: handler
                    parseBinaryNode(len, null, dataIn, subHandler)
                    if (dataIn.readByte().toInt() != ']'.code) {
                        throw LLSDXMLException("Array terminator expected")
                    }
                    handler.onArrayEnd(name)
                }
                '{'.code -> { // Map
                    val len = dataIn.readInt()
                    val subHandler = handler.onMapBegin(name) ?: handler
                    for (i in 0 until len) {
                        if (dataIn.readByte().toInt() != 'k'.code) {
                            throw LLSDXMLException("Map key expected")
                        }
                        val keyLen = dataIn.readInt()
                        val keyBytes = ByteArray(keyLen)
                        dataIn.readFully(keyBytes)
                        val key = SLMessage.stringFromVariableUTF(keyBytes)
                        parseBinaryNode(1, key, dataIn, subHandler)
                    }
                    if (dataIn.readByte().toInt() != '}'.code) {
                        throw LLSDXMLException("Map terminator expected")
                    }
                    handler.onMapEnd(name)
                }
                '}'.code, ']'.code -> {
                    // Should be handled by caller looking for terminator
                    // But if we hit it here unexpectedly, it might be an error or end of loop
                    throw LLSDXMLException("Unexpected terminator")
                }
                else -> throw LLSDXMLException("Unknown LLSD element 0x${Integer.toHexString(type)}")
            }
            c--
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseXML(inputStream: InputStream, charset: String?, handler: LLSDContentHandler) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(inputStream, charset)
            
            var eventType = parser.eventType
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && parser.name == "llsd") {
                    parser.nextTag() // Go to first child
                    parseXMLNode(null, parser, handler)
                    break
                }
                eventType = parser.next()
            }
        } catch (e: Exception) {
             when (e) {
                is XmlPullParserException -> throw LLSDXMLException("Malformed XML: ${e.message}", e)
                is IOException -> throw LLSDXMLException("Input stream error", e)
                else -> throw LLSDXMLException("Error parsing XML", e)
            }
        }
    }

    @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class, LLSDValueTypeException::class, InterruptedException::class)
    private fun parseXMLNode(name: String?, parser: XmlPullParser, handler: LLSDContentHandler) {
        val tagName = parser.name
        val nodeType = LLSDNodeType.byTag(tagName) ?: throw LLSDXMLException("Unknown tag: $tagName")

        when (nodeType) {
            LLSDNodeType.llsdArray -> {
                val subHandler = handler.onArrayBegin(name) ?: handler
                parser.nextTag()
                while (parser.eventType != XmlPullParser.END_TAG) {
                    parseXMLNode(null, parser, subHandler)
                    parser.nextTag()
                }
                handler.onArrayEnd(name)
            }
            LLSDNodeType.llsdMap -> {
                val subHandler = handler.onMapBegin(name) ?: handler
                parser.nextTag()
                while (parser.eventType != XmlPullParser.END_TAG) {
                    if (parser.name != "key") throw LLSDXMLException("Unexpected tag in map: ${parser.name}")
                    val key = parser.nextText()
                    parser.nextTag() // Move to value
                    parseXMLNode(key, parser, subHandler)
                    parser.nextTag()
                }
                handler.onMapEnd(name)
            }
            LLSDNodeType.llsdBinary -> handler.onPrimitiveValue(name, LLSDBinary(parser.nextText()))
            LLSDNodeType.llsdBoolean -> handler.onPrimitiveValue(name, LLSDBoolean(parser.nextText()))
            LLSDNodeType.llsdDate -> handler.onPrimitiveValue(name, LLSDDate(parser.nextText()))
            LLSDNodeType.llsdDouble -> handler.onPrimitiveValue(name, LLSDDouble(parser.nextText()))
            LLSDNodeType.llsdInteger -> handler.onPrimitiveValue(name, LLSDInt(parser.nextText()))
            LLSDNodeType.llsdString -> handler.onPrimitiveValue(name, LLSDString(parser.nextText()))
            LLSDNodeType.llsdURI -> handler.onPrimitiveValue(name, LLSDURI(parser.nextText()))
            LLSDNodeType.llsdUUID -> handler.onPrimitiveValue(name, LLSDUUID(parser.nextText()))
            LLSDNodeType.llsdUndef -> handler.onPrimitiveValue(name, LLSDUndefined())
            else -> {} // Handle others
        }
    }
}
