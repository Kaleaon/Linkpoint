/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd

import java.io.IOException
import java.io.Writer
import java.net.URI
import java.text.SimpleDateFormat
import java.util.*

/**
 * A serializer for converting LLSD (Linden Lab Structured Data) objects into
 * their notation format.
 *
 * This class takes an [LLSD] object and writes its content to a
 * [Writer] as a compact, human-readable string. The notation format uses
 * single-character prefixes to denote data types, making it more concise than
 * XML or JSON but still easy to read and edit by hand.
 *
 * The serializer handles all standard LLSD types, converting them to their
 * corresponding notation representation (e.g., an integer `42` becomes
 * `i42`). It also correctly formats arrays, maps, and special values
 * like dates and UUIDs.
 *
 * @see LLSD
 * @see LLSDNotationParser
 * @see [LLSD Notation Specification](http://wiki.secondlife.com/wiki/LLSD#Notation_Serialization)
 */
class LLSDNotationSerializer {
    
    /**
     * Serializes an LLSD document into its notation representation and writes it
     * to the provided [Writer].
     *
     * @param llsd   The [LLSD] document to be serialized.
     * @param writer The [Writer] to which the notation output will be sent.
     * @throws IOException   if an I/O error occurs while writing.
     * @throws LLSDException if the document contains an unserializable data type.
     */
    @Throws(IOException::class, LLSDException::class)
    fun serialize(llsd: LLSD, writer: Writer) {
        serializeValue(llsd.content, writer)
    }
    
    /**
     * Recursively serializes a single LLSD value into its notation representation.
     *
     * This is the core of the serializer. It inspects the object's type and
     * writes the corresponding type marker and formatted value.
     *
     * @param value  The object to serialize.
     * @param writer The writer for the output.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the value type is not supported.
     */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeValue(value: Any?, writer: Writer) {
        when {
            value == null || (value is String && value.isEmpty()) -> {
                writer.write("!")
            }
            value is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                serializeMap(value as Map<String, Any?>, writer)
            }
            value is List<*> -> {
                serializeArray(value, writer)
            }
            value is String -> {
                serializeString(value, writer)
            }
            value is Int -> {
                writer.write("i")
                writer.write(value.toString())
            }
            value is Double -> {
                writer.write("r")
                when {
                    value.isNaN() -> writer.write("nan")
                    value.isInfinite() -> writer.write(if (value > 0) "inf" else "-inf")
                    else -> writer.write(value.toString())
                }
            }
            value is Boolean -> {
                writer.write(if (value) "1" else "0")
            }
            value is Date -> {
                writer.write("d")
                // Create a new DateFormat instance for thread safety
                val dateFormat = SimpleDateFormat(ISO8601_PATTERN).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                writer.write(dateFormat.format(value))
            }
            value is URI -> {
                writer.write("l")
                writer.write(value.toString())
            }
            value is UUID -> {
                writer.write("u")
                writer.write(value.toString())
            }
            value is ByteArray -> {
                serializeBinary(value, writer)
            }
            value is LLSDUndefined -> {
                writer.write("!")
            }
            else -> {
                // Fallback to string representation
                serializeString(value.toString(), writer)
            }
        }
    }
    
    /** Serializes a Map into notation as `{key:value,...}`. */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeMap(map: Map<String, Any?>, writer: Writer) {
        writer.write("{")
        var first = true
        for ((key, value) in map) {
            if (!first) {
                writer.write(",")
            }
            first = false
            
            // Serialize key
            if (isValidIdentifier(key)) {
                writer.write(key)
            } else {
                serializeString(key, writer)
            }
            
            writer.write(":")
            serializeValue(value, writer)
        }
        writer.write("}")
    }
    
    /** Serializes a List into notation as `[value1,value2,...]`. */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeArray(list: List<*>, writer: Writer) {
        writer.write("[")
        var first = true
        for (item in list) {
            if (!first) {
                writer.write(",")
            }
            first = false
            
            serializeValue(item, writer)
        }
        writer.write("]")
    }
    
    /** Serializes a String into notation as `s'...'`. */
    @Throws(IOException::class)
    private fun serializeString(str: String, writer: Writer) {
        writer.write("s'")
        // Escape single quotes and backslashes
        for (c in str) {
            when (c) {
                '\'' -> writer.write("\\'")
                '\\' -> writer.write("\\\\")
                '\n' -> writer.write("\\n")
                '\r' -> writer.write("\\r")
                '\t' -> writer.write("\\t")
                else -> writer.write(c.toString())
            }
        }
        writer.write("'")
    }
    
    /** Serializes a byte array into base64 notation as `b64"..."`. */
    @Throws(IOException::class)
    private fun serializeBinary(data: ByteArray, writer: Writer) {
        writer.write("b64\"")
        writer.write(Base64.getEncoder().encodeToString(data))
        writer.write("\"")
    }
    
    /**
     * Checks if a string is a valid identifier in LLSD notation.
     *
     * A valid identifier can be used as an unquoted map key. It must start
     * with a letter or underscore, followed by letters, digits, or underscores.
     *
     * @param str The string to check.
     * @return `true` if the string is a valid identifier, `false` otherwise.
     */
    private fun isValidIdentifier(str: String): Boolean {
        if (str.isEmpty()) {
            return false
        }
        
        val first = str[0]
        if (!first.isLetter() && first != '_') {
            return false
        }
        
        for (i in 1 until str.length) {
            val c = str[i]
            if (!c.isLetterOrDigit() && c != '_') {
                return false
            }
        }
        
        return true
    }
    
    companion object {
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
    }
}
