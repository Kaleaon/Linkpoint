package com.linkpoint.slproto.llsd

import com.linkpoint.Debug
import com.linkpoint.slproto.https.LLSDContentTypeDetector
import com.linkpoint.slproto.llsd.types.LLSDBinary
import com.linkpoint.slproto.llsd.types.LLSDBoolean
import com.linkpoint.slproto.llsd.types.LLSDDate
import com.linkpoint.slproto.llsd.types.LLSDDouble
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDURI
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.llsd.types.LLSDUndefined
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

class LLSDStreamingParser {

    private var contentTypeSwitchesValues: IntArray? = null
    private var nodeTypeSwitchesValues: IntArray? = null

    interface LLSDContentHandler {
        @Throws(LLSDXMLException::class)
        fun onArrayBegin(str: String?): LLSDContentHandler?

        @Throws(LLSDXMLException::class)
        fun onArrayEnd(str: String?)

        @Throws(LLSDXMLException::class)
        fun onMapBegin(str: String?): LLSDContentHandler?

        @Throws(LLSDXMLException::class, InterruptedException::class)
        fun onMapEnd(str: String?)

        @Throws(LLSDXMLException::class, LLSDValueTypeException::class)
        fun onPrimitiveValue(str: String?, lLSDNode: LLSDNode)
    }

    open class LLSDDefaultContentHandler : LLSDContentHandler {
        @Throws(LLSDXMLException::class)
        override fun onArrayBegin(str: String?): LLSDContentHandler {
            return LLSDDefaultContentHandler()
        }

        @Throws(LLSDXMLException::class)
        override fun onArrayEnd(str: String?) {
        }

        @Throws(LLSDXMLException::class)
        override fun onMapBegin(str: String?): LLSDContentHandler {
            return LLSDDefaultContentHandler()
        }

        @Throws(LLSDXMLException::class, InterruptedException::class)
        override fun onMapEnd(str: String?) {
        }

        @Throws(LLSDXMLException::class, LLSDValueTypeException::class)
        override fun onPrimitiveValue(str: String?, lLSDNode: LLSDNode) {
        }
    }

    private fun getContentTypeSwitchesValues(): IntArray {
        var values = contentTypeSwitchesValues
        if (values != null) {
            return values
        }
        values = IntArray(LLSDContentTypeDetector.LLSDContentType.values().size)
        try {
            values[LLSDContentTypeDetector.LLSDContentType.llsdBinary.ordinal] = 1
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDContentTypeDetector.LLSDContentType.llsdXML.ordinal] = 2
        } catch (e: NoSuchFieldError) {
        }
        contentTypeSwitchesValues = values
        return values
    }

    private fun getNodeTypeSwitchesValues(): IntArray {
        var values = nodeTypeSwitchesValues
        if (values != null) {
            return values
        }
        values = IntArray(LLSDNodeType.values().size)
        try {
            values[LLSDNodeType.llsdArray.ordinal] = 1
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdBinary.ordinal] = 2
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdBoolean.ordinal] = 3
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdDate.ordinal] = 4
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdDouble.ordinal] = 5
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdInteger.ordinal] = 6
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdKey.ordinal] = 7
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdMap.ordinal] = 8
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdRoot.ordinal] = 9
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdString.ordinal] = 10
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdURI.ordinal] = 11
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdUUID.ordinal] = 12
        } catch (e: NoSuchFieldError) {
        }
        try {
            values[LLSDNodeType.llsdUndef.ordinal] = 13
        } catch (e: NoSuchFieldError) {
        }
        nodeTypeSwitchesValues = values
        return values
    }

    @Throws(LLSDXMLException::class)
    fun parseAny(inputStream: InputStream, str: String?, lLSDContentHandler: LLSDContentHandler) {
        try {
            val bufferedInputStream = BufferedInputStream(inputStream, 65536)
            when (getContentTypeSwitchesValues()[LLSDContentTypeDetector.DetectContentType(bufferedInputStream, str).ordinal]) {
                1 -> parseBinary(DataInputStream(bufferedInputStream), lLSDContentHandler)
                2 -> parseXML(bufferedInputStream, "UTF-8", lLSDContentHandler)
            }
        } catch (e: IOException) {
            val lLSDXMLException = LLSDXMLException("I/O error")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseBinary(dataInputStream: DataInputStream, lLSDContentHandler: LLSDContentHandler) {
        try {
            parseBinaryNode(1, null, dataInputStream, lLSDContentHandler)
        } catch (e: LLSDValueTypeException) {
            val lLSDXMLException = LLSDXMLException("Invalid value type")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (e: InterruptedException) {
            val lLSDXMLException = LLSDXMLException("Interrupted")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (e: IOException) {
            val lLSDXMLException = LLSDXMLException("I/O error")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        }
    }

    @Throws(LLSDXMLException::class, LLSDValueTypeException::class, InterruptedException::class, IOException::class)
    private fun parseBinaryNode(count: Int, key: String?, dataInputStream: DataInputStream, handler: LLSDContentHandler) {
        var remaining = count
        while (remaining > 0) {
            val type = dataInputStream.readByte().toInt()
            when (type) {
                10, 60 -> { /* Skip newlines and comments */ }
                33 -> { // '!' - undefined
                    handler.onPrimitiveValue(key, LLSDUndefined())
                    remaining--
                }
                48 -> { // '0' - false
                    handler.onPrimitiveValue(key, LLSDBoolean(false))
                    remaining--
                }
                49 -> { // '1' - true
                    handler.onPrimitiveValue(key, LLSDBoolean(true))
                    remaining--
                }
                91 -> { // '[' - array
                    val arraySize = dataInputStream.readInt()
                    var arrayHandler = handler.onArrayBegin(key) ?: handler
                    parseBinaryNode(arraySize, null, dataInputStream, arrayHandler)
                    val terminator = dataInputStream.readByte().toInt()
                    if (terminator != 93) { // ']'
                        throw LLSDXMLException("Array terminator expected")
                    }
                    arrayHandler.onMapEnd(key)
                    remaining--
                }
                98 -> { // 'b' - binary
                    val binarySize = dataInputStream.readInt()
                    val binaryData = ByteArray(binarySize)
                    dataInputStream.readFully(binaryData)
                    handler.onPrimitiveValue(key, LLSDBinary(binaryData))
                    remaining--
                }
                100 -> { // 'd' - date
                    val dateValue = dataInputStream.readDouble()
                    handler.onPrimitiveValue(key, LLSDDate(java.util.Date(Math.round(dateValue * 1000.0))))
                    remaining--
                }
                105 -> { // 'i' - integer
                    val intValue = dataInputStream.readInt()
                    handler.onPrimitiveValue(key, LLSDInt(intValue))
                    remaining--
                }
                108 -> { // 'l' - uri
                    val uriSize = dataInputStream.readInt()
                    if (uriSize == 0) {
                        handler.onPrimitiveValue(key, LLSDURI(""))
                    } else {
                        val uriData = ByteArray(uriSize)
                        dataInputStream.readFully(uriData)
                        handler.onPrimitiveValue(key, LLSDURI(com.linkpoint.slproto.SLMessage.stringFromVariableUTF(uriData)))
                    }
                    remaining--
                }
                114 -> { // 'r' - real/double
                    val doubleValue = dataInputStream.readDouble()
                    handler.onPrimitiveValue(key, LLSDDouble(doubleValue))
                    remaining--
                }
                115 -> { // 's' - string
                    val strSize = dataInputStream.readInt()
                    if (strSize == 0) {
                        handler.onPrimitiveValue(key, LLSDString(""))
                    } else {
                        val strData = ByteArray(strSize)
                        dataInputStream.readFully(strData)
                        handler.onPrimitiveValue(key, LLSDString(com.linkpoint.slproto.SLMessage.stringFromVariableUTF(strData)))
                    }
                    remaining--
                }
                117 -> { // 'u' - uuid
                    val mostSig = dataInputStream.readLong()
                    val leastSig = dataInputStream.readLong()
                    handler.onPrimitiveValue(key, LLSDUUID(java.util.UUID(mostSig, leastSig)))
                    remaining--
                }
                123 -> { // '{' - map
                    val mapSize = dataInputStream.readInt()
                    var mapHandler = handler.onMapBegin(key) ?: handler
                    for (i in 0 until mapSize) {
                        val keyMarker = dataInputStream.readByte().toInt()
                        if (keyMarker != 107) { // 'k'
                            throw LLSDXMLException("Map key expected")
                        }
                        val keySize = dataInputStream.readInt()
                        val keyData = ByteArray(keySize)
                        dataInputStream.readFully(keyData)
                        val mapKey = com.linkpoint.slproto.SLMessage.stringFromVariableUTF(keyData)
                        parseBinaryNode(1, mapKey, dataInputStream, mapHandler)
                    }
                    val terminator = dataInputStream.readByte().toInt()
                    if (terminator != 125) { // '}'
                        throw LLSDXMLException("Map terminator expected")
                    }
                    mapHandler.onMapEnd(key)
                    remaining--
                }
                else -> throw LLSDXMLException("Unknown LLSD element 0x" + Integer.toHexString(type))
            }
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseXML(inputStream: InputStream, encoding: String, lLSDContentHandler: LLSDContentHandler) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(inputStream, encoding)
            parser.nextTag()
            parser.require(XmlPullParser.START_TAG, null, "llsd")
            parser.nextTag()
            parseXMLNode(null, parser, lLSDContentHandler)
            parser.require(XmlPullParser.END_TAG, null, "llsd")
        } catch (e: XmlPullParserException) {
            Debug.Log("XmlPullParserException: " + e.message)
            e.printStackTrace()
            val lLSDXMLException = LLSDXMLException("Malformed XML")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (e: IOException) {
            throw LLSDXMLException("Input stream error")
        } catch (e: LLSDValueTypeException) {
            e.printStackTrace()
            val lLSDXMLException = LLSDXMLException("Malformed XML")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        } catch (e: InterruptedException) {
            e.printStackTrace()
            val lLSDXMLException = LLSDXMLException("Interrupted")
            lLSDXMLException.initCause(e)
            throw lLSDXMLException
        }
    }

    @Throws(LLSDXMLException::class, XmlPullParserException::class, IOException::class, LLSDValueTypeException::class, InterruptedException::class)
    private fun parseXMLNode(key: String?, parser: XmlPullParser, handler: LLSDContentHandler) {
        val tagName = parser.name
        val nodeType = LLSDNodeType.byTag(tagName)
            ?: throw LLSDXMLException("Unknown tag: $tagName")
        
        when (getNodeTypeSwitchesValues()[nodeType.ordinal]) {
            1 -> { // array
                var arrayHandler = handler.onArrayBegin(key) ?: handler
                parser.nextTag()
                while (parser.eventType != XmlPullParser.END_TAG) {
                    parseXMLNode(null, parser, arrayHandler)
                }
                arrayHandler.onArrayEnd(key)
                parser.nextTag()
            }
            2 -> { // binary
                handler.onPrimitiveValue(key, LLSDBinary(parser.nextText()))
                parser.nextTag()
            }
            3 -> { // boolean
                handler.onPrimitiveValue(key, LLSDBoolean(parser.nextText()))
                parser.nextTag()
            }
            4 -> { // date
                handler.onPrimitiveValue(key, LLSDDate(parser.nextText()))
                parser.nextTag()
            }
            5 -> { // double
                handler.onPrimitiveValue(key, LLSDDouble(parser.nextText()))
                parser.nextTag()
            }
            6 -> { // integer
                handler.onPrimitiveValue(key, LLSDInt(parser.nextText()))
                parser.nextTag()
            }
            7 -> { // key (unexpected)
                throw LLSDXMLException("Unexpected tag: $tagName")
            }
            8 -> { // map
                var mapHandler = handler.onMapBegin(key) ?: handler
                parser.nextTag()
                while (parser.eventType != XmlPullParser.END_TAG) {
                    val keyTagName = parser.name
                    if (!keyTagName.equals("key", ignoreCase = true)) {
                        throw LLSDXMLException("Unexpected tag: $keyTagName")
                    }
                    val mapKey = parser.nextText()
                    parser.nextTag()
                    parseXMLNode(mapKey, parser, mapHandler)
                }
                mapHandler.onMapEnd(key)
                parser.nextTag()
            }
            9 -> { // root (unexpected)
                throw LLSDXMLException("Unexpected tag: $tagName")
            }
            10 -> { // string
                handler.onPrimitiveValue(key, LLSDString(parser.nextText()))
                parser.nextTag()
            }
            11 -> { // uri
                handler.onPrimitiveValue(key, LLSDURI(parser.nextText()))
                parser.nextTag()
            }
            12 -> { // uuid
                handler.onPrimitiveValue(key, LLSDUUID(parser.nextText()))
                parser.nextTag()
            }
            13 -> { // undefined
                handler.onPrimitiveValue(key, LLSDUndefined())
                parser.nextTag()
            }
        }
    }
}
