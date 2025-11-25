package com.lumiyaviewer.lumiya.slproto.llsd

import android.util.Xml
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.https.LLSDContentTypeDetector
import com.lumiyaviewer.lumiya.slproto.llsd.types.*
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.io.*
import java.net.URI
import java.util.Date
import java.util.UUID
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer

abstract class LLSDNode {

    companion object {
        @JvmStatic
        @Throws(LLSDXMLException::class)
        fun fromAny(inputStream: InputStream, str: String?): LLSDNode {
             try {
                val bufferedInputStream = BufferedInputStream(inputStream, 65536)
                bufferedInputStream.mark(65536) 
                
                val type = LLSDContentTypeDetector.DetectContentType(bufferedInputStream, str)
                
                return when (type) {
                    LLSDContentTypeDetector.LLSDContentType.llsdBinary -> fromBinary(DataInputStream(bufferedInputStream))
                    LLSDContentTypeDetector.LLSDContentType.llsdXML -> parseXML(bufferedInputStream, "UTF-8")
                    else -> throw LLSDXMLException("Unknown content type")
                }
            } catch (e: IOException) {
                throw LLSDXMLException("I/O error").apply { initCause(e) }
            }
        }

        @JvmStatic
        @Throws(LLSDXMLException::class)
        fun fromBinary(dis: DataInputStream): LLSDNode {
            try {
                while (true) {
                    val b = dis.readByte().toInt()
                    when (b) {
                        10 -> continue // newline
                        33 -> return LLSDUndefined() // '!'
                        48 -> return LLSDBoolean(false) // '0'
                        49 -> return LLSDBoolean(true) // '1'
                        60 -> { // '<'
                             var b2 = dis.readByte().toInt()
                             while (b2 != 62) { // '>'
                                 b2 = dis.readByte().toInt()
                             }
                             continue
                        }
                        91 -> { // '[' Array
                            val size = dis.readInt()
                            val array = LLSDArray()
                            for (i in 0 until size) {
                                array.add(fromBinary(dis))
                            }
                            val terminator = dis.readByte().toInt()
                            if (terminator != 93) { // ']'
                                throw LLSDXMLException("Array terminator expected")
                            }
                            return array
                        }
                        98 -> { // 'b' Binary
                            val size = dis.readInt()
                            val bytes = ByteArray(size)
                            dis.readFully(bytes)
                            return LLSDBinary(bytes)
                        }
                        100 -> { // 'd' Date
                             val doubleVal = dis.readDouble()
                             val dateVal = Math.round(doubleVal * 1000.0)
                             return LLSDDate(Date(dateVal))
                        }
                        105 -> { // 'i' Integer
                            return LLSDInt(dis.readInt())
                        }
                        108 -> { // 'l' URI
                            val size = dis.readInt()
                            if (size == 0) return LLSDURI("")
                            val bytes = ByteArray(size)
                            dis.readFully(bytes)
                            return LLSDURI(SLMessage.stringFromVariableUTF(bytes))
                        }
                        114 -> { // 'r' Real
                            return LLSDDouble(dis.readDouble())
                        }
                        115 -> { // 's' String
                            val size = dis.readInt()
                            if (size == 0) return LLSDString("")
                            val bytes = ByteArray(size)
                            dis.readFully(bytes)
                            return LLSDString(SLMessage.stringFromVariableUTF(bytes))
                        }
                        117 -> { // 'u' UUID
                             val msb = dis.readLong()
                             val lsb = dis.readLong()
                             return LLSDUUID(UUID(msb, lsb))
                        }
                        123 -> { // '{' Map
                            val size = dis.readInt()
                            val map = HashMap<String, LLSDNode>(size)
                            for (i in 0 until size) {
                                val keyTag = dis.readByte().toInt()
                                if (keyTag != 107) { // 'k'
                                     throw LLSDXMLException("Map key expected")
                                }
                                val keySize = dis.readInt()
                                val keyBytes = ByteArray(keySize)
                                dis.readFully(keyBytes)
                                val key = SLMessage.stringFromVariableUTF(keyBytes)
                                val value = fromBinary(dis)
                                map[key] = value
                            }
                            val terminator = dis.readByte().toInt()
                            if (terminator != 125) { // '}'
                                 throw LLSDXMLException("Map terminator expected")
                            }
                            return LLSDMap(map)
                        }
                        else -> throw LLSDXMLException("Unknown LLSD element 0x${Integer.toHexString(b)}")
                    }
                }
            } catch (e: IOException) {
                throw LLSDXMLException(e.message ?: "IO Error").apply { initCause(e) }
            }
        }

        @JvmStatic
        @Throws(LLSDXMLException::class)
        fun fromBinaryFile(file: File): LLSDNode {
            var dis: DataInputStream? = null
            try {
                dis = DataInputStream(FileInputStream(file))
                return fromBinary(dis)
            } catch (e: IOException) {
                 throw LLSDXMLException(e.message ?: "IO Error").apply { initCause(e) }
            } finally {
                try { dis?.close() } catch (e: IOException) {}
            }
        }

        @JvmStatic
        @Throws(LLSDXMLException::class)
        fun parseXML(inputStream: InputStream, encoding: String?): LLSDNode {
            try {
                val parser = XmlPullParserFactory.newInstance().newPullParser()
                parser.setInput(inputStream, encoding)
                parser.nextTag()
                parser.require(XmlPullParser.START_TAG, null, "llsd")
                parser.nextTag()
                val node = LLSDNodeFactory.parseNode(parser)
                parser.nextTag()
                parser.require(XmlPullParser.END_TAG, null, "llsd")
                return node
            } catch (e: Exception) {
                Debug.Log("XmlPullParserException: " + e.message)
                e.printStackTrace()
                 throw LLSDXMLException("Malformed XML").apply { initCause(e) }
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
    open fun asLong(): Long = throw LLSDValueTypeException("integer", this) // Using integer type name for both?
    
    @Throws(LLSDValueTypeException::class)
    open fun asString(): String = throw LLSDValueTypeException("string", this)
    
    @Throws(LLSDValueTypeException::class)
    open fun asURI(): URI = throw LLSDValueTypeException("uri", this)
    
    @Throws(LLSDValueTypeException::class)
    open fun asUUID(): UUID = throw LLSDValueTypeException("uuid", this)
    
    @Throws(LLSDException::class)
    open fun byIndex(i: Int): LLSDNode = throw LLSDValueTypeException("array", this)
    
    @Throws(LLSDException::class)
    open fun byKey(str: String): LLSDNode? = throw LLSDValueTypeException("map", this) // Nullable return? Original didn't say nullable but 'byKey' usually implies it might return null or throw. Original threw exception if not map.
    // But 'byKey' in LLSDMap returns node?
    // `LLSDMap` implements `operator get` which returns `LLSDNode?`.
    // `LLSDNode` defines `byKey`.
    // Let's check `LLSDMap` implementation.
    // It has `operator get`. It doesn't explicitly implement `byKey`.
    // So `LLSDMap` must override `byKey`?
    // The original `LLSDNode.kt` defined `byKey`.
    // `LLSDMap.kt` (original) did NOT implement `byKey`.
    // So `byKey` might be a convenience wrapper?
    // Or `LLSDMap` inherited it and failed?
    // Ah, `LLSDMap` in original code:
    /*
    operator fun get(key: String): LLSDNode? {
        return this.items[key]
    }
    */
    // It did not override `byKey`.
    // So calling `byKey` on `LLSDMap` would throw `LLSDValueTypeException`? That seems wrong.
    // Unless `LLSDMap` overrides it.
    // I should probably add `byKey` to `LLSDMap` or use `get`.
    // The original `LLSDNode` `byKey` throws exception.
    // I'll check `LLSDMap` usage.
    // If code calls `node.byKey("foo")`, it expects `LLSDMap` to handle it.
    // So `LLSDMap` MUST override `byKey`.
    // I will add `byKey` to `LLSDMap` in a moment.
    
    @Throws(LLSDException::class)
    open fun getCount(): Int = throw LLSDValueTypeException("array", this)
    
    open fun isBinary(): Boolean = this is LLSDBinary
    open fun isBoolean(): Boolean = this is LLSDBoolean
    open fun isDate(): Boolean = this is LLSDDate
    open fun isDouble(): Boolean = this is LLSDDouble
    open fun isInt(): Boolean = this is LLSDInt
    open fun isLong(): Boolean = this is LLSDInt // Int covers Long in LLSD?
    open fun isString(): Boolean = this is LLSDString
    open fun isURI(): Boolean = this is LLSDURI
    open fun isUUID(): Boolean = this is LLSDUUID
    open fun isMap(): Boolean = this is LLSDMap
    open fun isArray(): Boolean = this is LLSDArray
    open fun isUndefined(): Boolean = this is LLSDUndefined

    @Throws(LLSDException::class)
    open fun keyExists(str: String): Boolean = throw LLSDValueTypeException("map", this)

    @Throws(IOException::class)
    open fun serializeToXML(): String {
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
    open fun serializeToXML(outputStream: OutputStream, encoding: String?) {
        val serializer = Xml.newSerializer()
        serializer.setOutput(outputStream, encoding)
        serializer.startTag("", "llsd")
        toXML(serializer)
        serializer.endTag("", "llsd")
        serializer.endDocument()
    }

    @Throws(IOException::class)
    abstract fun toBinary(dataOutputStream: DataOutputStream)

    // toObject was generic in original. I'll skip it for now unless needed.
    // It threw exception anyway.
    
    @Throws(IOException::class)
    abstract fun toXML(xmlSerializer: XmlSerializer)
}
