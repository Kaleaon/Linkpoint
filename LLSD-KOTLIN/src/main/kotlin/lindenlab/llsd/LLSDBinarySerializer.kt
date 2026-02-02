/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.OutputStream
import java.net.URI
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * A serializer for converting LLSD (Linden Lab Structured Data) objects into
 * their binary representation.
 *
 * This class takes an [LLSD] object and writes its content to an
 * [OutputStream] or returns it as a `byte[]`. The binary format
 * is a compact and efficient way to represent LLSD data, suitable for network
 * transmission or storage.
 *
 * The serializer correctly handles all standard LLSD data types, converting them
 * into the byte-level format specified by the LLSD binary protocol. This includes
 * using specific markers for each data type and encoding values in big-endian order.
 *
 * @see LLSD
 * @see LLSDBinaryParser
 * @see [LLSD Binary Specification](http://wiki.secondlife.com/wiki/LLSD#Binary_Serialization)
 */
class LLSDBinarySerializer {
    
    /**
     * Serializes an LLSD document into its binary representation and writes it to
     * an output stream.
     *
     * @param llsd          The [LLSD] document to serialize.
     * @param output        The [OutputStream] to which the binary data will be written.
     * @param includeHeader If true, the standard binary header (`"<?llsd/binary?>"`)
     *                      will be written at the beginning of the stream.
     * @throws IOException   if an I/O error occurs while writing to the stream.
     * @throws LLSDException if the LLSD object contains data that cannot be serialized.
     */
    @Throws(IOException::class, LLSDException::class)
    fun serialize(llsd: LLSD, output: OutputStream, includeHeader: Boolean = true) {
        if (includeHeader) {
            output.write(LLSD_BINARY_HEADER_BYTES)
        }
        serializeValue(llsd.content, output)
    }
    
    /**
     * Serializes an LLSD document into a binary byte array.
     *
     * @param llsd          The [LLSD] document to serialize.
     * @param includeHeader If true, the header is included in the returned byte array.
     * @return A `byte[]` containing the serialized binary data.
     * @throws LLSDException if the serialization fails.
     */
    @Throws(LLSDException::class)
    fun serialize(llsd: LLSD, includeHeader: Boolean = true): ByteArray {
        return try {
            ByteArrayOutputStream().use { output ->
                serialize(llsd, output, includeHeader)
                output.toByteArray()
            }
        } catch (e: IOException) {
            throw LLSDException("Failed to serialize binary LLSD", e)
        }
    }
    
    /**
     * Recursively serializes a single LLSD value to the output stream.
     *
     * This method determines the type of the given value and calls the appropriate
     * helper method to write the corresponding binary representation.
     *
     * @param value  The object to serialize.
     * @param output The stream to write to.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the value's type is not serializable.
     */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeValue(value: Any?, output: OutputStream) {
        when {
            value == null -> {
                output.write(UNDEF_MARKER.toInt())
            }
            value is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                serializeMap(value as Map<String, Any?>, output)
            }
            value is List<*> -> {
                serializeArray(value, output)
            }
            value is String -> {
                serializeString(value, output)
            }
            value is Int -> {
                serializeInteger(value, output)
            }
            value is Double -> {
                serializeReal(value, output)
            }
            value is Boolean -> {
                serializeBoolean(value, output)
            }
            value is Date -> {
                serializeDate(value, output)
            }
            value is URI -> {
                serializeURI(value, output)
            }
            value is UUID -> {
                serializeUUID(value, output)
            }
            value is ByteArray -> {
                serializeBinary(value, output)
            }
            value is LLSDUndefined -> {
                output.write(UNDEF_MARKER.toInt())
            }
            else -> {
                // Fallback to string representation
                serializeString(value.toString(), output)
            }
        }
    }
    
    /** Writes a boolean value as a single byte marker ('1' or '0'). */
    @Throws(IOException::class)
    private fun serializeBoolean(value: Boolean, output: OutputStream) {
        output.write(if (value) TRUE_MARKER.toInt() else FALSE_MARKER.toInt())
    }
    
    /** Writes an integer value with its marker and 4-byte big-endian representation. */
    @Throws(IOException::class)
    private fun serializeInteger(value: Int, output: OutputStream) {
        output.write(INTEGER_MARKER.toInt())
        val buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(value)
        output.write(buffer.array())
    }
    
    /** Writes a double value with its marker and 8-byte big-endian representation. */
    @Throws(IOException::class)
    private fun serializeReal(value: Double, output: OutputStream) {
        output.write(REAL_MARKER.toInt())
        val buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN)
        buffer.putDouble(value)
        output.write(buffer.array())
    }
    
    /** Writes a string value with its marker, a 4-byte length prefix, and UTF-8 bytes. */
    @Throws(IOException::class)
    private fun serializeString(value: String, output: OutputStream) {
        output.write(STRING_MARKER.toInt())
        val stringBytes = value.toByteArray(StandardCharsets.UTF_8)
        writeInt32(output, stringBytes.size)
        output.write(stringBytes)
    }
    
    /** Writes a UUID value with its marker and 16-byte representation. */
    @Throws(IOException::class)
    private fun serializeUUID(value: UUID, output: OutputStream) {
        output.write(UUID_MARKER.toInt())
        val buffer = ByteBuffer.allocate(16)
        buffer.putLong(value.mostSignificantBits)
        buffer.putLong(value.leastSignificantBits)
        output.write(buffer.array())
    }
    
    /** Writes a Date value with its marker and 8-byte double representation (seconds since epoch). */
    @Throws(IOException::class)
    private fun serializeDate(value: Date, output: OutputStream) {
        output.write(DATE_MARKER.toInt())
        val secondsSinceEpoch = value.time / 1000.0
        val buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN)
        buffer.putDouble(secondsSinceEpoch)
        output.write(buffer.array())
    }
    
    /** Writes a URI value with its marker, a 4-byte length prefix, and UTF-8 bytes. */
    @Throws(IOException::class)
    private fun serializeURI(value: URI, output: OutputStream) {
        output.write(URI_MARKER.toInt())
        val uriBytes = value.toString().toByteArray(StandardCharsets.UTF_8)
        writeInt32(output, uriBytes.size)
        output.write(uriBytes)
    }
    
    /** Writes a binary data block with its marker, a 4-byte length prefix, and raw bytes. */
    @Throws(IOException::class)
    private fun serializeBinary(value: ByteArray, output: OutputStream) {
        output.write(BINARY_MARKER.toInt())
        writeInt32(output, value.size)
        output.write(value)
    }
    
    /** Serializes a List into an LLSD array, enclosed in array markers. */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeArray(list: List<*>, output: OutputStream) {
        output.write(ARRAY_BEGIN_MARKER.toInt())
        for (item in list) {
            serializeValue(item, output)
        }
        output.write(ARRAY_END_MARKER.toInt())
    }
    
    /** Serializes a Map into an LLSD map, enclosed in map markers, with keys and values. */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeMap(map: Map<String, Any?>, output: OutputStream) {
        output.write(MAP_BEGIN_MARKER.toInt())
        for ((key, value) in map) {
            // Write key
            output.write(KEY_MARKER.toInt())
            val keyBytes = key.toByteArray(StandardCharsets.UTF_8)
            writeInt32(output, keyBytes.size)
            output.write(keyBytes)
            
            // Write value
            serializeValue(value, output)
        }
        output.write(MAP_END_MARKER.toInt())
    }
    
    /**
     * Helper method to write a 32-bit integer in big-endian format to the stream.
     */
    @Throws(IOException::class)
    private fun writeInt32(output: OutputStream, value: Int) {
        val buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
        buffer.putInt(value)
        output.write(buffer.array())
    }
    
    companion object {
        private const val LLSD_BINARY_HEADER = "<?llsd/binary?>"
        private val LLSD_BINARY_HEADER_BYTES = LLSD_BINARY_HEADER.toByteArray(StandardCharsets.US_ASCII)
        
        // Binary markers
        private const val UNDEF_MARKER = '!'.code.toByte()
        private const val TRUE_MARKER = '1'.code.toByte()
        private const val FALSE_MARKER = '0'.code.toByte()
        private const val INTEGER_MARKER = 'i'.code.toByte()
        private const val REAL_MARKER = 'r'.code.toByte()
        private const val STRING_MARKER = 's'.code.toByte()
        private const val UUID_MARKER = 'u'.code.toByte()
        private const val DATE_MARKER = 'd'.code.toByte()
        private const val URI_MARKER = 'l'.code.toByte()
        private const val BINARY_MARKER = 'b'.code.toByte()
        private const val ARRAY_BEGIN_MARKER = '['.code.toByte()
        private const val ARRAY_END_MARKER = ']'.code.toByte()
        private const val MAP_BEGIN_MARKER = '{'.code.toByte()
        private const val MAP_END_MARKER = '}'.code.toByte()
        private const val KEY_MARKER = 'k'.code.toByte()
    }
}
