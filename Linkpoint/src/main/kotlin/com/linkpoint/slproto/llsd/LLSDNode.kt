package com.linkpoint.slproto.llsd

import android.util.Xml
import com.linkpoint.Debug
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
import java.io.DataOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.io.StringWriter
import java.net.URI
import java.util.Date
import java.util.UUID
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer

/**
 * Base class for LLSD nodes, supporting XML and binary representations.
 */
abstract class LLSDNode {

    open fun asBinary(): ByteArray = throw LLSDValueTypeException("binary", this)
    open fun asBoolean(): Boolean = throw LLSDValueTypeException("boolean", this)
    open fun asDate(): Date = throw LLSDValueTypeException("date", this)
    open fun asDouble(): Double = throw LLSDValueTypeException("real", this)
    open fun asInt(): Int = throw LLSDValueTypeException("integer", this)
    open fun asLong(): Long = throw LLSDValueTypeException("long", this)
    open fun asString(): String = throw LLSDValueTypeException("string", this)
    open fun asURI(): URI = throw LLSDValueTypeException("uri", this)
    open fun asUUID(): UUID = throw LLSDValueTypeException("uuid", this)

    open fun byIndex(index: Int): LLSDNode = throw LLSDValueTypeException("array", this)
    open fun byKey(key: String): LLSDNode = throw LLSDValueTypeException("map", this)
    open fun getCount(): Int = throw LLSDValueTypeException("array", this)
    open fun keyExists(key: String): Boolean = throw LLSDValueTypeException("map", this)

    fun isBinary(): Boolean = this is LLSDBinary
    fun isBoolean(): Boolean = this is LLSDBoolean
    fun isDate(): Boolean = this is LLSDDate
    fun isDouble(): Boolean = this is LLSDDouble
    fun isInt(): Boolean = this is LLSDInt
    fun isLong(): Boolean = this is LLSDInt
    fun isString(): Boolean = this is LLSDString
    fun isURI(): Boolean = this is LLSDURI
    fun isUUID(): Boolean = this is LLSDUUID

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
    fun serializeToXML(outputStream: OutputStream, encoding: String = "UTF-8") {
        val serializer = Xml.newSerializer()
        serializer.setOutput(outputStream, encoding)
        serializer.startTag("", "llsd")
        toXML(serializer)
        serializer.endTag("", "llsd")
        serializer.endDocument()
    }

    @Throws(IOException::class)
    abstract fun toBinary(stream: DataOutputStream)

    @Throws(LLSDException::class)
    open fun <T> toObject(type: Class<out T>): T {
        throw LLSDException("Cannot deserialize ${javaClass.name}")
    }

    @Throws(IOException::class)
    abstract fun toXML(serializer: XmlSerializer)

    companion object {

        private const val ARRAY_BEGIN = '['.code.toByte()
        private const val ARRAY_END = ']'.code.toByte()
        private const val BINARY = 'b'.code.toByte()
        private const val BOOLEAN_FALSE = '0'.code.toByte()
        private const val BOOLEAN_TRUE = '1'.code.toByte()
        private const val DATE = 'd'.code.toByte()
        private const val INTEGER = 'i'.code.toByte()
        private const val MAP_BEGIN = '{'.code.toByte()
        private const val MAP_END = '}'.code.toByte()
        private const val MAP_KEY = 'k'.code.toByte()
        private const val REAL = 'r'.code.toByte()
        private const val STRING = 's'.code.toByte()
        private const val UNDEFINED = '!'.code.toByte()
        private const val URI = 'l'.code.toByte()
        private const val UUID_TYPE = 'u'.code.toByte()

        @Throws(LLSDXMLException::class)
        fun fromAny(stream: InputStream, contentType: String?): LLSDNode {
            val buffered = BufferedInputStream(stream, 65_536)
            return try {
                when (LLSDContentTypeDetector.detectContentType(buffered, contentType)) {
                    LLSDContentTypeDetector.LLSDContentType.llsdBinary -> fromBinary(DataInputStream(buffered))
                    LLSDContentTypeDetector.LLSDContentType.llsdXML -> parseXML(buffered, "UTF-8")
                }
            } catch (io: IOException) {
                throw LLSDXMLException("I/O error", io)
            }
        }

        @Throws(LLSDXMLException::class)
        fun fromBinary(stream: DataInputStream): LLSDNode {
            return try {
                when (val marker = stream.readByte()) {
                    UNDEFINED -> LLSDUndefined()
                    BOOLEAN_TRUE -> LLSDBoolean(true)
                    BOOLEAN_FALSE -> LLSDBoolean(false)
                    INTEGER -> LLSDInt(stream.readInt())
                    REAL -> LLSDDouble(stream.readDouble())
                    UUID_TYPE -> LLSDUUID(UUID(stream.readLong(), stream.readLong()))
                    STRING -> readString(stream)
                    DATE -> LLSDDate(Date(Math.round(stream.readDouble() * 1000.0)))
                    URI -> readURI(stream)
                    BINARY -> readBinary(stream)
                    ARRAY_BEGIN -> readArray(stream)
                    MAP_BEGIN -> readMap(stream)
                    '<'.code.toByte() -> {
                        skipComment(stream)
                        fromBinary(stream)
                    }
                    else -> throw LLSDXMLException("Unknown LLSD element 0x${marker.toInt().toString(16)}")
                }
            } catch (io: IOException) {
                throw LLSDXMLException(io.message ?: "I/O error", io)
            }
        }

        @Throws(LLSDXMLException::class)
        fun fromBinaryFile(file: File): LLSDNode {
            DataInputStream(FileInputStream(file)).use { stream ->
                return fromBinary(stream)
            }
        }

        @Throws(LLSDXMLException::class)
        fun parseXML(stream: InputStream, encoding: String? = "UTF-8"): LLSDNode {
            return try {
                val parser = XmlPullParserFactory.newInstance().newPullParser()
                parser.setInput(stream, encoding)
                parser.nextTag()
                parser.require(XmlPullParser.START_TAG, null, "llsd")
                parser.nextTag()
                val node = LLSDNodeFactory.parseNode(parser)
                parser.nextTag()
                parser.require(XmlPullParser.END_TAG, null, "llsd")
                node
            } catch (xml: XmlPullParserException) {
                Debug.Log("XmlPullParserException: ${xml.message}")
                throw LLSDXMLException("Malformed XML", xml)
            } catch (io: IOException) {
                throw LLSDXMLException("Input stream error", io)
            }
        }

        @Throws(IOException::class)
        private fun readString(stream: DataInputStream): LLSDNode {
            val length = stream.readInt()
            if (length == 0) {
                return LLSDString("")
            }
            val bytes = ByteArray(length)
            stream.readFully(bytes)
            return LLSDString(com.linkpoint.slproto.SLMessage.stringFromVariableUTF(bytes))
        }

        @Throws(IOException::class)
        private fun readURI(stream: DataInputStream): LLSDNode {
            val length = stream.readInt()
            if (length == 0) {
                return LLSDURI("")
            }
            val bytes = ByteArray(length)
            stream.readFully(bytes)
            return LLSDURI(com.linkpoint.slproto.SLMessage.stringFromVariableUTF(bytes))
        }

        @Throws(IOException::class)
        private fun readBinary(stream: DataInputStream): LLSDNode {
            val length = stream.readInt()
            val bytes = ByteArray(length)
            stream.readFully(bytes)
            return LLSDBinary(bytes)
        }

        @Throws(IOException::class, LLSDXMLException::class)
        private fun readArray(stream: DataInputStream): LLSDNode {
            val count = stream.readInt()
            val array = LLSDArray()
            repeat(count) {
                array.add(fromBinary(stream))
            }
            val terminator = stream.readByte()
            if (terminator != ARRAY_END) {
                throw LLSDXMLException("Array terminator expected")
            }
            return array
        }

        @Throws(IOException::class, LLSDXMLException::class)
        private fun readMap(stream: DataInputStream): LLSDNode {
            val count = stream.readInt()
            val map = HashMap<String, LLSDNode>(count)
            repeat(count) {
                val marker = stream.readByte()
                if (marker != MAP_KEY) {
                    throw LLSDXMLException("Map key expected")
                }
                val keyLength = stream.readInt()
                val keyBytes = ByteArray(keyLength)
                stream.readFully(keyBytes)
                val key = com.linkpoint.slproto.SLMessage.stringFromVariableUTF(keyBytes)
                map[key] = fromBinary(stream)
            }
            val terminator = stream.readByte()
            if (terminator != MAP_END) {
                throw LLSDXMLException("Map terminator expected")
            }
            return LLSDMap(map)
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
}
