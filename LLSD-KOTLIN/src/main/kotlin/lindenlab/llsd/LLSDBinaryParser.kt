/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd

import java.io.IOException
import java.io.InputStream
import java.net.URI
import java.net.URISyntaxException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.charset.StandardCharsets
import java.util.*

/**
 * A parser for LLSD (Linden Lab Structured Data) in its binary representation.
 *
 * This class is responsible for taking an [InputStream] containing LLSD
 * binary data and converting it into a Java object representation, wrapped in an
 * [LLSD] object. The binary format is a compact, efficient, and strongly-typed
 * serialization format designed for high-performance applications.
 *
 * The parser handles all standard LLSD data types, including integers, reals,
 * strings, UUIDs, dates, URIs, binary data, arrays, and maps. It correctly
 * interprets the specific byte markers and data layouts defined in the LLSD
 * binary specification.
 *
 * An optional header, `"<?llsd/binary?>"`, may be present at the beginning
 * of the stream. This parser can handle streams with or without this header.
 *
 * @see LLSD
 * @see LLSDBinarySerializer
 * @see [LLSD Binary Specification](http://wiki.secondlife.com/wiki/LLSD#Binary_Serialization)
 */
class LLSDBinaryParser {
    
    /**
     * Parses an LLSD document from a binary input stream.
     *
     * This is the main entry point for parsing. It reads the stream, optionally
     * validates the binary header, and then recursively parses the LLSD data
     * structure.
     *
     * @param binaryInput The input stream containing the binary LLSD data.
     * @return An [LLSD] object representing the parsed data.
     * @throws IOException   if an I/O error occurs while reading from the stream.
     * @throws LLSDException if the data is not valid LLSD binary format, for
     *                       example, due to an unknown marker or unexpected end of stream.
     */
    @Throws(IOException::class, LLSDException::class)
    fun parse(binaryInput: InputStream): LLSD {
        val reader = BinaryReader(binaryInput)
        
        // Check for optional header
        skipWhitespace(reader)
        if (reader.peek() == '<'.code.toByte()) {
            // Read and verify header
            val header = reader.readBytes(LLSD_BINARY_HEADER_BYTES.size)
            if (!header.contentEquals(LLSD_BINARY_HEADER_BYTES)) {
                val actualHeader = String(header, StandardCharsets.US_ASCII)
                throw LLSDException("Invalid binary LLSD header: expected '$LLSD_BINARY_HEADER', got '$actualHeader'")
            }
            skipWhitespace(reader)
        }
        
        val parsedBinary = parseBinaryValue(reader, 0)
        return LLSD(parsedBinary)
    }
    
    /**
     * An internal helper class for reading binary data from an input stream.
     * It provides methods to read specific data types (like integers and doubles)
     * and handles peeking at the next byte without consuming it.
     */
    private class BinaryReader(private val input: InputStream) {
        private val buffer = ByteArray(1)
        private var hasPeeked = false
        private var peekedByte: Byte = 0
        
        /**
         * Peeks at the next byte in the stream without consuming it.
         */
        @Throws(IOException::class, LLSDException::class)
        fun peek(): Byte {
            if (!hasPeeked) {
                val result = input.read(buffer)
                if (result == -1) {
                    throw LLSDException("Unexpected end of binary stream")
                }
                peekedByte = buffer[0]
                hasPeeked = true
            }
            return peekedByte
        }
        
        /**
         * Reads and consumes the next byte from the stream.
         */
        @Throws(IOException::class, LLSDException::class)
        fun readByte(): Byte {
            if (hasPeeked) {
                hasPeeked = false
                return peekedByte
            }
            
            val result = input.read(buffer)
            if (result == -1) {
                throw LLSDException("Unexpected end of binary stream")
            }
            return buffer[0]
        }
        
        /**
         * Reads a specified number of bytes from the stream.
         */
        @Throws(IOException::class, LLSDException::class)
        fun readBytes(count: Int): ByteArray {
            when {
                count < 0 -> throw LLSDException("Cannot read negative number of bytes: $count")
                count == 0 -> return ByteArray(0)
                count > 100_000_000 -> throw LLSDException("Attempting to read excessively large amount of data: $count bytes")
            }
            
            val bytes = ByteArray(count)
            var totalRead = 0
            
            // First use any peeked byte
            if (hasPeeked && count > 0) {
                bytes[0] = peekedByte
                totalRead = 1
                hasPeeked = false
            }
            
            while (totalRead < count) {
                val bytesRead = input.read(bytes, totalRead, count - totalRead)
                if (bytesRead == -1) {
                    throw LLSDException("Unexpected end of binary stream: expected $count bytes, got $totalRead")
                }
                totalRead += bytesRead
            }
            
            return bytes
        }
        
        /**
         * Reads a 32-bit integer in big-endian order.
         */
        @Throws(IOException::class, LLSDException::class)
        fun readInt32(): Int {
            val bytes = readBytes(4)
            return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).int
        }
        
        /**
         * Reads a 64-bit double-precision floating-point number in big-endian order.
         */
        @Throws(IOException::class, LLSDException::class)
        fun readDouble(): Double {
            val bytes = readBytes(8)
            return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).double
        }
        
        /**
         * Reads a string, which is prefixed by its 32-bit length.
         */
        @Throws(IOException::class, LLSDException::class)
        fun readString(): String {
            val length = readInt32()
            when {
                length < 0 -> throw LLSDException("Invalid string length: $length")
                length == 0 -> return ""
                length > 10_000_000 -> throw LLSDException("String length too large: $length bytes")
            }
            val bytes = readBytes(length)
            return String(bytes, StandardCharsets.UTF_8)
        }
        
        /**
         * Checks if there is more data available to be read from the stream.
         */
        @Throws(IOException::class)
        fun isAvailable(): Boolean {
            return input.available() > 0 || hasPeeked
        }
    }
    
    /**
     * Skips any whitespace characters from the input stream.
     */
    @Throws(IOException::class)
    private fun skipWhitespace(reader: BinaryReader) {
        try {
            while (reader.isAvailable()) {
                val b = reader.peek()
                if ((b.toInt() and 0xFF).toChar().isWhitespace()) {
                    reader.readByte() // consume whitespace
                } else {
                    break
                }
            }
        } catch (e: LLSDException) {
            // End of stream is ok when skipping whitespace
        }
    }
    
    /**
     * Parses a single LLSD value from the stream based on its type marker.
     * This is the core of the recursive descent parser.
     */
    @Throws(IOException::class, LLSDException::class)
    private fun parseBinaryValue(reader: BinaryReader, depth: Int): Any? {
        if (depth > MAX_RECURSION_DEPTH) {
            throw LLSDException("Maximum recursion depth exceeded: $depth")
        }
        
        val marker = reader.readByte()
        
        return when (marker) {
            UNDEF_MARKER -> null // Undefined value represented as null
            TRUE_MARKER -> true
            FALSE_MARKER -> false
            INTEGER_MARKER -> reader.readInt32()
            REAL_MARKER -> reader.readDouble()
            STRING_MARKER -> reader.readString()
            UUID_MARKER -> parseUUID(reader)
            DATE_MARKER -> parseDate(reader)
            URI_MARKER -> parseURI(reader)
            BINARY_MARKER -> parseBinary(reader)
            ARRAY_BEGIN_MARKER -> parseArray(reader, depth + 1)
            MAP_BEGIN_MARKER -> parseMap(reader, depth + 1)
            else -> throw LLSDException("Unknown binary LLSD marker: 0x${Integer.toHexString(marker.toInt() and 0xFF).uppercase()}")
        }
    }
    
    /** Parses a 16-byte UUID from the stream. */
    @Throws(IOException::class, LLSDException::class)
    private fun parseUUID(reader: BinaryReader): UUID {
        val bytes = reader.readBytes(16)
        val buffer = ByteBuffer.wrap(bytes)
        val mostSigBits = buffer.long
        val leastSigBits = buffer.long
        return UUID(mostSigBits, leastSigBits)
    }
    
    /** Parses an 8-byte date value (double-precision seconds since the Unix epoch). */
    @Throws(IOException::class, LLSDException::class)
    private fun parseDate(reader: BinaryReader): Date {
        val secondsSinceEpoch = reader.readDouble()
        val millisSinceEpoch = (secondsSinceEpoch * 1000.0).toLong()
        return Date(millisSinceEpoch)
    }
    
    /** Parses a URI from the stream. */
    @Throws(IOException::class, LLSDException::class)
    private fun parseURI(reader: BinaryReader): URI {
        val uriString = reader.readString()
        return try {
            URI(uriString)
        } catch (e: URISyntaxException) {
            throw LLSDException("Invalid URI in binary LLSD: $uriString", e)
        }
    }
    
    /** Parses a block of binary data from the stream. */
    @Throws(IOException::class, LLSDException::class)
    private fun parseBinary(reader: BinaryReader): ByteArray {
        val length = reader.readInt32()
        when {
            length < 0 -> throw LLSDException("Invalid binary length: $length")
            length == 0 -> return ByteArray(0)
        }
        return reader.readBytes(length)
    }
    
    /**
     * Parses an array from the stream. It reads elements recursively until it
     * encounters an array-end marker.
     */
    @Throws(IOException::class, LLSDException::class)
    private fun parseArray(reader: BinaryReader, depth: Int): List<Any?> {
        val array = mutableListOf<Any?>()
        var elementCount = 0
        
        while (reader.isAvailable()) {
            if (elementCount >= MAX_COLLECTION_SIZE) {
                throw LLSDException("Array size limit exceeded: $MAX_COLLECTION_SIZE")
            }
            
            val marker = reader.peek()
            if (marker == ARRAY_END_MARKER) {
                reader.readByte() // consume ']'
                break
            }
            
            val value = parseBinaryValue(reader, depth)
            array.add(value)
            elementCount++
        }
        
        return array
    }
    
    /**
     * Parses a map from the stream. It reads key-value pairs recursively until
     * it encounters a map-end marker.
     */
    @Throws(IOException::class, LLSDException::class)
    private fun parseMap(reader: BinaryReader, depth: Int): Map<String, Any?> {
        val map = mutableMapOf<String, Any?>()
        var elementCount = 0
        
        while (reader.isAvailable()) {
            if (elementCount >= MAX_COLLECTION_SIZE) {
                throw LLSDException("Map size limit exceeded: $MAX_COLLECTION_SIZE")
            }
            
            val marker = reader.peek()
            if (marker == MAP_END_MARKER) {
                reader.readByte() // consume '}'
                break
            }
            
            // Read key
            if (reader.readByte() != KEY_MARKER) {
                throw LLSDException("Expected key marker 'k' in binary LLSD map")
            }
            
            val key = reader.readString()
            val value = parseBinaryValue(reader, depth)
            map[key] = value
            elementCount++
        }
        
        return map
    }
    
    companion object {
        private const val LLSD_BINARY_HEADER = "<?llsd/binary?>"
        private val LLSD_BINARY_HEADER_BYTES = LLSD_BINARY_HEADER.toByteArray(StandardCharsets.US_ASCII)
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        
        // Security limits to prevent memory exhaustion attacks
        private const val MAX_COLLECTION_SIZE = 1_000_000 // Maximum array/map size
        private const val MAX_RECURSION_DEPTH = 1000 // Maximum nesting depth
        
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
