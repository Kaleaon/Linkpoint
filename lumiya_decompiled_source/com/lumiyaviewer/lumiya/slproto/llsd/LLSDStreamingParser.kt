package com.lumiyaviewer.lumiya.slproto.llsd

import com.google.common.logging.nano.Vr
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined
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

    open class LLSDDefaultContentHandler : LLSDContentHandler {
        override fun onArrayBegin(name: String?): LLSDContentHandler = LLSDDefaultContentHandler()

        override fun onArrayEnd(name: String?) = Unit

        override fun onMapBegin(name: String?): LLSDContentHandler = LLSDDefaultContentHandler()

        override fun onMapEnd(name: String?) = Unit

        override fun onPrimitiveValue(name: String?, node: LLSDNode) = Unit
    }

    @Throws(LLSDXMLException::class)
    fun parseAny(input: InputStream, contentType: String?, handler: LLSDContentHandler) {
        try {
            val buffered = BufferedInputStream(input, 65536)
            when (LLSDContentTypeDetector.DetectContentType(buffered, contentType)) {
                LLSDContentTypeDetector.LLSDContentType.llsdBinary -> parseBinary(DataInputStream(buffered), handler)
                LLSDContentTypeDetector.LLSDContentType.llsdXML -> parseXML(buffered, "UTF-8", handler)
            }
        } catch (e: IOException) {
            val exception = LLSDXMLException("I/O error")
            exception.initCause(e)
            throw exception
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseBinary(input: DataInputStream, handler: LLSDContentHandler) {
        try {
            parseBinaryNode(1, null, input, handler)
        } catch (e: LLSDValueTypeException) {
            val exception = LLSDXMLException("Invalid value type")
            exception.initCause(e)
            throw exception
        } catch (e: IOException) {
            val exception = LLSDXMLException("I/O error")
            exception.initCause(e)
            throw exception
        } catch (e: InterruptedException) {
            val exception = LLSDXMLException("Interrupted")
            exception.initCause(e)
            throw exception
        }
    }

    @Throws(LLSDXMLException::class, LLSDValueTypeException::class, InterruptedException::class, IOException::class)
    private fun parseBinaryNode(
        count: Int,
        name: String?,
        input: DataInputStream,
        handler: LLSDContentHandler
    ) {
        var remaining = count
        while (remaining > 0) {
            when (val value = input.readByte()) {
                10.toByte() -> Unit
                33.toByte() -> {
                    handler.onPrimitiveValue(name, LLSDUndefined())
                    remaining--
                }
                48.toByte() -> {
                    handler.onPrimitiveValue(name, LLSDBoolean(false))
                    remaining--
                }
                49.toByte() -> {
                    handler.onPrimitiveValue(name, LLSDBoolean(true))
                    remaining--
                }
                60.toByte() -> {
                    while (input.readByte() != 62.toByte()) {
                        Unit
                    }
                }
                91.toByte() -> {
                    val arrayCount = input.readInt()
                    var arrayHandler = handler.onArrayBegin(name)
                    if (arrayHandler == null) {
                        arrayHandler = handler
                    }
                    parseBinaryNode(arrayCount, null, input, arrayHandler)
                    if (input.readByte() != 93.toByte()) {
                        throw LLSDXMLException("Array terminator expected")
                    }
                    arrayHandler.onMapEnd(name)
                    remaining--
                }
                98.toByte() -> {
                    val bytes = ByteArray(input.readInt())
                    input.readFully(bytes)
                    handler.onPrimitiveValue(name, LLSDBinary(bytes))
                    remaining--
                }
                100.toByte() -> {
                    handler.onPrimitiveValue(name, LLSDDate(Date(Math.round(input.readDouble() * 1000.0))))
                    remaining--
                }
                105.toByte() -> {
                    handler.onPrimitiveValue(name, LLSDInt(input.readInt()))
                    remaining--
                }
                108.toByte() -> {
                    val length = input.readInt()
                    if (length == 0) {
                        handler.onPrimitiveValue(name, LLSDURI(""))
                    } else {
                        val bytes = ByteArray(length)
                        input.readFully(bytes)
                        handler.onPrimitiveValue(name, LLSDURI(SLMessage.stringFromVariableUTF(bytes)))
                    }
                    remaining--
                }
                114.toByte() -> {
                    handler.onPrimitiveValue(name, LLSDDouble(input.readDouble()))
                    remaining--
                }
                115.toByte() -> {
                    val length = input.readInt()
                    if (length == 0) {
                        handler.onPrimitiveValue(name, LLSDString(""))
                    } else {
                        val bytes = ByteArray(length)
                        input.readFully(bytes)
                        handler.onPrimitiveValue(name, LLSDString(SLMessage.stringFromVariableUTF(bytes)))
                    }
                    remaining--
                }
                117.toByte() -> {
                    handler.onPrimitiveValue(name, LLSDUUID(UUID(input.readLong(), input.readLong())))
                    remaining--
                }
                Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND.toByte() -> {
                    val mapCount = input.readInt()
                    var mapHandler = handler.onMapBegin(name)
                    if (mapHandler == null) {
                        mapHandler = handler
                    }
                    for (index in 0 until mapCount) {
                        if (input.readByte() != 107.toByte()) {
                            throw LLSDXMLException("Map key expected")
                        }
                        val bytes = ByteArray(input.readInt())
                        input.readFully(bytes)
                        parseBinaryNode(1, SLMessage.stringFromVariableUTF(bytes), input, mapHandler)
                    }
                    if (input.readByte() != 125.toByte()) {
                        throw LLSDXMLException("Map terminator expected")
                    }
                    mapHandler.onMapEnd(name)
                    remaining--
                }
                else -> throw LLSDXMLException("Unknown LLSD element 0x${Integer.toHexString(value.toInt())}")
            }
        }
    }

    @Throws(LLSDXMLException::class)
    fun parseXML(input: InputStream, encoding: String?, handler: LLSDContentHandler) {
        try {
            val parser = XmlPullParserFactory.newInstance().newPullParser()
            parser.setInput(input, encoding)
            parser.nextTag()
            parser.require(2, null, "llsd")
            parser.nextTag()
            parseXMLNode(null, parser, handler)
            parser.require(3, null, "llsd")
        } catch (e: LLSDValueTypeException) {
            e.printStackTrace()
            val exception = LLSDXMLException("Malformed XML")
            exception.initCause(e)
            throw exception
        } catch (_: IOException) {
            throw LLSDXMLException("Input stream error")
        } catch (e: InterruptedException) {
            e.printStackTrace()
            val exception = LLSDXMLException("Interrupted")
            exception.initCause(e)
            throw exception
        } catch (e: XmlPullParserException) {
            Debug.Log("XmlPullParserException: ${e.message}")
            e.printStackTrace()
            val exception = LLSDXMLException("Malformed XML")
            exception.initCause(e)
            throw exception
        }
    }

    @Throws(
        LLSDXMLException::class,
        XmlPullParserException::class,
        IOException::class,
        LLSDValueTypeException::class,
        InterruptedException::class
    )
    private fun parseXMLNode(
        name: String?,
        parser: XmlPullParser,
        handler: LLSDContentHandler
    ) {
        val tag = parser.name
        val nodeType = LLSDNodeType.byTag(tag) ?: throw LLSDXMLException("Unknown tag: $tag")
        when (nodeType) {
            LLSDNodeType.llsdArray -> {
                var effectiveHandler = handler.onArrayBegin(name)
                parser.nextTag()
                if (effectiveHandler == null) {
                    effectiveHandler = handler
                }
                while (parser.eventType != 3) {
                    parseXMLNode(null, parser, effectiveHandler)
                }
                effectiveHandler.onArrayEnd(name)
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
            LLSDNodeType.llsdKey -> throw LLSDXMLException("Unexpected tag: $tag")
            LLSDNodeType.llsdMap -> {
                var effectiveHandler = handler.onMapBegin(name)
                parser.nextTag()
                if (effectiveHandler == null) {
                    effectiveHandler = handler
                }
                while (parser.eventType != 3) {
                    val keyTag = parser.name
                    if (!keyTag.equals("key", ignoreCase = true)) {
                        throw LLSDXMLException("Unexpected tag: $keyTag")
                    }
                    val key = parser.nextText()
                    parser.nextTag()
                    parseXMLNode(key, parser, effectiveHandler)
                }
                effectiveHandler.onMapEnd(name)
                parser.nextTag()
            }
            LLSDNodeType.llsdRoot -> throw LLSDXMLException("Unexpected tag: $tag")
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
                handler.onPrimitiveValue(name, LLSDUndefined())
                parser.nextTag()
            }
        }
    }
}
