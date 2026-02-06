package com.lumiyaviewer.lumiya.slproto.llsd

import android.util.Xml
import com.google.common.logging.nano.Vr
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDArray
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBinary
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDBoolean
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDate
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDDouble
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDInt
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDMap
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDString
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDURI
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUUID
import com.lumiyaviewer.lumiya.slproto.llsd.types.LLSDUndefined
import java.io.BufferedInputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.StringWriter
import java.net.URI
import java.util.Date
import java.util.HashMap
import java.util.UUID
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer

abstract class LLSDNode {
    companion object {
        @Throws(LLSDXMLException::class)
        fun fromAny(input: InputStream, contentType: String?): LLSDNode {
            try {
                val buffered = BufferedInputStream(input, 65536)
                return when (LLSDContentTypeDetector.DetectContentType(buffered, contentType)) {
                    LLSDContentTypeDetector.LLSDContentType.llsdBinary -> fromBinary(DataInputStream(buffered))
                    LLSDContentTypeDetector.LLSDContentType.llsdXML -> parseXML(buffered, "UTF-8")
                }
            } catch (e: IOException) {
                val exception = LLSDXMLException("I/O error")
                exception.initCause(e)
                throw exception
            }
        }

        @Throws(LLSDXMLException::class)
        fun fromBinary(input: DataInputStream): LLSDNode {
            var index = 0
            while (true) {
                try {
                    when (val value = input.readByte()) {
                        10.toByte() -> Unit
                        33.toByte() -> return LLSDUndefined()
                        48.toByte() -> return LLSDBoolean(false)
                        49.toByte() -> return LLSDBoolean(true)
                        60.toByte() -> {
                            while (input.readByte() != 62.toByte()) {
                                Unit
                            }
                        }
                        91.toByte() -> {
                            val count = input.readInt()
                            val array = LLSDArray()
                            while (index < count) {
                                array.add(fromBinary(input))
                                index++
                            }
                            if (input.readByte() != 93.toByte()) {
                                throw LLSDXMLException("Array terminator expected")
                            }
                            return array
                        }
                        98.toByte() -> {
                            val bytes = ByteArray(input.readInt())
                            input.readFully(bytes)
                            return LLSDBinary(bytes)
                        }
                        100.toByte() -> return LLSDDate(Date(Math.round(input.readDouble() * 1000.0)))
                        105.toByte() -> return LLSDInt(input.readInt())
                        108.toByte() -> {
                            val length = input.readInt()
                            if (length == 0) {
                                return LLSDURI("")
                            }
                            val bytes = ByteArray(length)
                            input.readFully(bytes)
                            return LLSDURI(SLMessage.stringFromVariableUTF(bytes))
                        }
                        114.toByte() -> return LLSDDouble(input.readDouble())
                        115.toByte() -> {
                            val length = input.readInt()
                            if (length == 0) {
                                return LLSDString("")
                            }
                            val bytes = ByteArray(length)
                            input.readFully(bytes)
                            return LLSDString(SLMessage.stringFromVariableUTF(bytes))
                        }
                        117.toByte() -> return LLSDUUID(UUID(input.readLong(), input.readLong()))
                        Vr.VREvent.VrCore.ErrorCode.CONTROLLER_GATT_CHARACTERISTIC_NOT_FOUND.toByte() -> {
                            val count = input.readInt()
                            val map = HashMap<String, LLSDNode>(count)
                            while (index < count) {
                                if (input.readByte() != 107.toByte()) {
                                    throw LLSDXMLException("Map key expected")
                                }
                                val bytes = ByteArray(input.readInt())
                                input.readFully(bytes)
                                map[SLMessage.stringFromVariableUTF(bytes)] = fromBinary(input)
                                index++
                            }
                            val result = LLSDMap(map)
                            if (input.readByte() != 125.toByte()) {
                                throw LLSDXMLException("Map terminator expected")
                            }
                            return result
                        }
                        else -> throw LLSDXMLException("Unknown LLSD element 0x${Integer.toHexString(value.toInt())}")
                    }
                } catch (e: IOException) {
                    val exception = LLSDXMLException(e.message ?: "I/O error")
                    exception.initCause(e)
                    throw exception
                }
            }
        }

        @Throws(LLSDXMLException::class)
        fun fromBinaryFile(file: File): LLSDNode {
            try {
                DataInputStream(FileInputStream(file)).use { input ->
                    return fromBinary(input)
                }
            } catch (e: IOException) {
                val exception = LLSDXMLException(e.message ?: "I/O error")
                exception.initCause(e)
                throw exception
            }
        }

        @Throws(LLSDXMLException::class)
        fun parseXML(input: InputStream, encoding: String?): LLSDNode {
            try {
                val parser = XmlPullParserFactory.newInstance().newPullParser()
                parser.setInput(input, encoding)
                parser.nextTag()
                parser.require(2, null, "llsd")
                parser.nextTag()
                val node = LLSDNodeFactory.parseNode(parser)
                parser.nextTag()
                parser.require(3, null, "llsd")
                return node
            } catch (_: IOException) {
                throw LLSDXMLException("Input stream error")
            } catch (e: XmlPullParserException) {
                Debug.Log("XmlPullParserException: ${e.message}")
                e.printStackTrace()
                val exception = LLSDXMLException("Malformed XML")
                exception.initCause(e)
                throw exception
            }
        }
    }

    @Throws(LLSDValueTypeException::class)
    open fun asBinary(): ByteArray = throw LLSDValueTypeException("binary", this)

    @Throws(LLSDValueTypeException::class)
    open fun asBoolean(): Boolean = throw LLSDValueTypeException("boolean", this)

    @Throws(LLSDValueTypeException::class)
    open fun asDate(): Date = throw LLSDValueTypeException("date", this)

    @Throws(LLSDValueTypeException::class)
    open fun asDouble(): Double = throw LLSDValueTypeException("real", this)

    @Throws(LLSDValueTypeException::class)
    open fun asInt(): Int = throw LLSDValueTypeException("integer", this)

    @Throws(LLSDValueTypeException::class)
    open fun asLong(): Long = throw LLSDValueTypeException("long", this)

    @Throws(LLSDValueTypeException::class)
    open fun asString(): String = throw LLSDValueTypeException("string", this)

    @Throws(LLSDValueTypeException::class)
    open fun asURI(): URI = throw LLSDValueTypeException("uri", this)

    @Throws(LLSDValueTypeException::class)
    open fun asUUID(): UUID = throw LLSDValueTypeException("uuid", this)

    @Throws(LLSDException::class)
    open fun byIndex(index: Int): LLSDNode = throw LLSDValueTypeException("array", this)

    @Throws(LLSDException::class)
    open fun byKey(key: String): LLSDNode = throw LLSDValueTypeException("map", this)

    @Throws(LLSDException::class)
    open fun getCount(): Int = throw LLSDValueTypeException("array", this)

    fun isBinary(): Boolean = this is LLSDBinary

    fun isBoolean(): Boolean = this is LLSDBoolean

    fun isDate(): Boolean = this is LLSDDate

    fun isDouble(): Boolean = this is LLSDDouble

    fun isInt(): Boolean = this is LLSDInt

    fun isLong(): Boolean = this is LLSDInt

    fun isString(): Boolean = this is LLSDString

    fun isURI(): Boolean = this is LLSDURI

    fun isUUID(): Boolean = this is LLSDUUID

    @Throws(LLSDException::class)
    open fun keyExists(key: String): Boolean = throw LLSDValueTypeException("map", this)

    @Throws(IOException::class)
    fun serializeToXML(): String {
        val serializer = Xml.newSerializer()
        val writer = StringWriter()
        serializer.setOutput(writer)
        serializer.startTag("", "llsd")
        toXML(serializer)
        serializer.endTag("", "llsd")
        serializer.endDocument()
        return writer.toString()
    }

    @Throws(IOException::class)
    fun serializeToXML(outputStream: OutputStream, encoding: String?) {
        val serializer = Xml.newSerializer()
        serializer.setOutput(outputStream, encoding)
        serializer.startTag("", "llsd")
        toXML(serializer)
        serializer.endTag("", "llsd")
        serializer.endDocument()
    }

    @Throws(IOException::class)
    abstract fun toBinary(output: DataOutputStream)

    @Throws(LLSDException::class)
    open fun <T> toObject(cls: Class<out T>): T {
        throw LLSDException("Cannot deserialize ${javaClass.name}")
    }

    @Throws(IOException::class)
    abstract fun toXML(serializer: XmlSerializer)
}
