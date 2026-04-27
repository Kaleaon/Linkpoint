package com.linkpoint.slproto.llsd

import java.io.DataInputStream
import java.io.IOException
import java.io.InputStream
import java.util.Date
import java.util.UUID

/**
 * LLSDBinaryParser - Parse binary LLSD format
 * 
 * Binary LLSD format:
 * - Type markers (1 byte): '!', '1', '0', 'i', 'r', 'u', 's', 'd', 'b', '[', '{'
 * - Data follows type marker
 * 
 * Based on Firestorm's LLSDBinaryParser
 */
object LLSDBinaryParser {
    
    // Type markers
    private const val LLSD_UNDEFINED = '!'.code.toByte()
    private const val LLSD_TRUE = '1'.code.toByte()
    private const val LLSD_FALSE = '0'.code.toByte()
    private const val LLSD_INTEGER = 'i'.code.toByte()
    private const val LLSD_REAL = 'r'.code.toByte()
    private const val LLSD_UUID = 'u'.code.toByte()
    private const val LLSD_STRING = 's'.code.toByte()
    private const val LLSD_DATE = 'd'.code.toByte()
    private const val LLSD_URI = 'l'.code.toByte()
    private const val LLSD_BINARY = 'b'.code.toByte()
    private const val LLSD_ARRAY = '['.code.toByte()
    private const val LLSD_MAP = '{'.code.toByte()
    private const val LLSD_ARRAY_END = ']'.code.toByte()
    private const val LLSD_MAP_END = '}'.code.toByte()
    private const val LLSD_KEY = 'k'.code.toByte()
    
    /**
     * Parse LLSD from binary stream
     */
    @Throws(LLSDException::class, IOException::class)
    fun parse(inputStream: InputStream): LLSD {
        val dataStream = if (inputStream is DataInputStream) {
            inputStream
        } else {
            DataInputStream(inputStream)
        }
        
        return parseValue(dataStream)
    }
    
    /**
     * Parse a single LLSD value
     */
    @Throws(LLSDException::class, IOException::class)
    private fun parseValue(stream: DataInputStream): LLSD {
        // Read type marker
        val typeByte = stream.readByte()
        
        return when (typeByte) {
            LLSD_UNDEFINED -> LLSD()  // Undefined
            
            LLSD_TRUE -> LLSD(true)
            LLSD_FALSE -> LLSD(false)
            
            LLSD_INTEGER -> {
                val value = stream.readInt()
                LLSD(value)
            }
            
            LLSD_REAL -> {
                val value = stream.readDouble()
                LLSD(value)
            }
            
            LLSD_UUID -> {
                val mostSig = stream.readLong()
                val leastSig = stream.readLong()
                LLSD(UUID(mostSig, leastSig))
            }
            
            LLSD_STRING -> {
                val length = stream.readInt()
                if (length < 0 || length > 1024 * 1024) {
                    throw LLSDException("Invalid string length: $length")
                }
                val bytes = ByteArray(length)
                stream.readFully(bytes)
                LLSD(String(bytes, Charsets.UTF_8))
            }
            
            LLSD_DATE -> {
                val seconds = stream.readDouble()
                LLSD(Date((seconds * 1000).toLong()))
            }
            
            LLSD_URI -> {
                val length = stream.readInt()
                val bytes = ByteArray(length)
                stream.readFully(bytes)
                LLSD(String(bytes, Charsets.UTF_8))  // URI as string
            }
            
            LLSD_BINARY -> {
                val length = stream.readInt()
                if (length < 0 || length > 16 * 1024 * 1024) {
                    throw LLSDException("Invalid binary length: $length")
                }
                val bytes = ByteArray(length)
                stream.readFully(bytes)
                LLSD(bytes)
            }
            
            LLSD_ARRAY -> {
                val count = stream.readInt()
                val array = LLSD.emptyArray()
                
                // Read array elements
                for (i in 0 until count) {
                    val element = parseValue(stream)
                    array.add(element)
                }
                
                // Read array end marker
                val endMarker = stream.readByte()
                if (endMarker != LLSD_ARRAY_END) {
                    throw LLSDException("Expected array end marker, got: $endMarker")
                }
                
                array
            }
            
            LLSD_MAP -> {
                val count = stream.readInt()
                val map = LLSD.emptyMap()
                
                // Read map entries
                for (i in 0 until count) {
                    // Read key marker
                    val keyMarker = stream.readByte()
                    if (keyMarker != LLSD_KEY) {
                        throw LLSDException("Expected key marker, got: $keyMarker")
                    }
                    
                    // Read key string
                    val keyLength = stream.readInt()
                    val keyBytes = ByteArray(keyLength)
                    stream.readFully(keyBytes)
                    val key = String(keyBytes, Charsets.UTF_8)
                    
                    // Read value
                    val value = parseValue(stream)
                    map[key] = value
                }
                
                // Read map end marker
                val endMarker = stream.readByte()
                if (endMarker != LLSD_MAP_END) {
                    throw LLSDException("Expected map end marker, got: $endMarker")
                }
                
                map
            }
            
            else -> throw LLSDException("Unknown LLSD type marker: $typeByte")
        }
    }
    
    /**
     * Serialize LLSD to binary format
     */
    @Throws(IOException::class)
    fun serialize(llsd: LLSD, outputStream: java.io.OutputStream) {
        val dataStream = if (outputStream is java.io.DataOutputStream) {
            outputStream
        } else {
            java.io.DataOutputStream(outputStream)
        }
        
        serializeValue(llsd, dataStream)
    }
    
    /**
     * Serialize a single LLSD value
     */
    @Throws(IOException::class)
    private fun serializeValue(llsd: LLSD, stream: java.io.DataOutputStream) {
        when {
            llsd.isUndefined() -> stream.writeByte(LLSD_UNDEFINED.toInt())
            
            llsd.isBoolean() -> {
                stream.writeByte(if (llsd.asBoolean()) LLSD_TRUE.toInt() else LLSD_FALSE.toInt())
            }
            
            llsd.isInteger() -> {
                stream.writeByte(LLSD_INTEGER.toInt())
                stream.writeInt(llsd.asInteger())
            }
            
            llsd.isReal() -> {
                stream.writeByte(LLSD_REAL.toInt())
                stream.writeDouble(llsd.asReal())
            }
            
            llsd.isUUID() -> {
                stream.writeByte(LLSD_UUID.toInt())
                val uuid = llsd.asUUID()
                stream.writeLong(uuid.mostSignificantBits)
                stream.writeLong(uuid.leastSignificantBits)
            }
            
            llsd.isString() -> {
                stream.writeByte(LLSD_STRING.toInt())
                val bytes = llsd.asString().toByteArray(Charsets.UTF_8)
                stream.writeInt(bytes.size)
                stream.write(bytes)
            }
            
            llsd.isDate() -> {
                stream.writeByte(LLSD_DATE.toInt())
                val seconds = llsd.asDate().time / 1000.0
                stream.writeDouble(seconds)
            }
            
            llsd.isBinary() -> {
                stream.writeByte(LLSD_BINARY.toInt())
                val bytes = llsd.asBinary()
                stream.writeInt(bytes.size)
                stream.write(bytes)
            }
            
            llsd.isArray() -> {
                stream.writeByte(LLSD_ARRAY.toInt())
                val array = llsd.asArray()
                stream.writeInt(array.size)
                
                for (element in array) {
                    serializeValue(element, stream)
                }
                
                stream.writeByte(LLSD_ARRAY_END.toInt())
            }
            
            llsd.isMap() -> {
                stream.writeByte(LLSD_MAP.toInt())
                val map = llsd.asMap()
                stream.writeInt(map.size)
                
                for ((key, value) in map) {
                    // Write key marker
                    stream.writeByte(LLSD_KEY.toInt())
                    
                    // Write key
                    val keyBytes = key.toByteArray(Charsets.UTF_8)
                    stream.writeInt(keyBytes.size)
                    stream.write(keyBytes)
                    
                    // Write value
                    serializeValue(value, stream)
                }
                
                stream.writeByte(LLSD_MAP_END.toInt())
            }
        }
    }
}
