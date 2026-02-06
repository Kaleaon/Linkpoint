/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd

import java.io.IOException
import java.io.Writer
import java.net.URI
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A serializer for converting LLSD (Linden Lab Structured Data) objects into
 * their JSON representation.
 *
 * This class takes an [LLSD] object and writes its content to a
 * [Writer] in JSON format. It adheres to the specific conventions used
 * by LLSD for representing data types like dates, URIs, and binary data within
 * the JSON structure.
 *
 * For example, a [java.util.Date] object is not serialized as a simple
 * number, but as a JSON object with a special key: `{"d": "YYYY-MM-DDTHH:MM:SSZ"}`.
 * This class handles all such conversions automatically.
 *
 * @see LLSD
 * @see LLSDJsonParser
 * @see [LLSD JSON Specification](http://wiki.secondlife.com/wiki/LLSD#JSON_Serialization)
 */
class LLSDJsonSerializer {
    
    /**
     * Serializes an LLSD document into its JSON representation and writes it to
     * the provided [Writer].
     *
     * @param llsd   The [LLSD] document to be serialized.
     * @param writer The [Writer] to which the JSON output will be sent.
     * @throws IOException   if an I/O error occurs while writing.
     * @throws LLSDException if the document contains an unserializable data type.
     */
    @Throws(IOException::class, LLSDException::class)
    fun serialize(llsd: LLSD, writer: Writer) {
        serializeValue(llsd.content, writer)
    }
    
    /**
     * Recursively serializes a single LLSD value into its JSON representation.
     *
     * This method is the core of the serializer. It inspects the type of the
     * value and calls the appropriate helper method to format it as JSON. It
     * handles the special LLSD conventions for types like Date, URI, UUID, and
     * binary data.
     *
     * @param value  The object to serialize.
     * @param writer The writer to output the JSON to.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the value type is not supported.
     */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeValue(value: Any?, writer: Writer) {
        when {
            value == null -> {
                writer.write("null")
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
                writer.write(value.toString())
            }
            value is Double -> {
                when {
                    value.isNaN() -> writer.write("\"NaN\"")
                    value.isInfinite() -> writer.write(if (value > 0) "\"Infinity\"" else "\"-Infinity\"")
                    else -> writer.write(value.toString())
                }
            }
            value is Boolean -> {
                writer.write(value.toString())
            }
            value is Date -> {
                writer.write("{\"d\":\"")
                writer.write(ISO8601_FORMATTER.format(value.toInstant()))
                writer.write("\"}")
            }
            value is URI -> {
                writer.write("{\"u\":")
                serializeString(value.toString(), writer)
                writer.write("}")
            }
            value is UUID -> {
                writer.write("{\"i\":")
                serializeString(value.toString(), writer)
                writer.write("}")
            }
            value is ByteArray -> {
                writer.write("{\"b\":")
                serializeString(Base64.getEncoder().encodeToString(value), writer)
                writer.write("}")
            }
            value is LLSDUndefined -> {
                writer.write("null")
            }
            else -> {
                // Fallback to string representation
                serializeString(value.toString(), writer)
            }
        }
    }
    
    /** Serializes a Map into a JSON object. */
    @Throws(IOException::class, LLSDException::class)
    private fun serializeMap(map: Map<String, Any?>, writer: Writer) {
        writer.write("{")
        var first = true
        for ((key, value) in map) {
            if (!first) {
                writer.write(",")
            }
            first = false
            
            serializeString(key, writer)
            writer.write(":")
            serializeValue(value, writer)
        }
        writer.write("}")
    }
    
    /** Serializes a List into a JSON array. */
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
    
    /** Serializes a String into a properly-escaped JSON string. */
    @Throws(IOException::class)
    private fun serializeString(str: String, writer: Writer) {
        writer.write("\"")
        
        for (c in str) {
            when (c) {
                '"' -> writer.write("\\\"")
                '\\' -> writer.write("\\\\")
                '\b' -> writer.write("\\b")
                '\u000C' -> writer.write("\\f")
                '\n' -> writer.write("\\n")
                '\r' -> writer.write("\\r")
                '\t' -> writer.write("\\t")
                else -> {
                    if (c < ' ' || c.code > 127) {
                        // Unicode escape for control characters and non-ASCII
                        writer.write(String.format("\\u%04x", c.code))
                    } else {
                        writer.write(c.toString())
                    }
                }
            }
        }
        
        writer.write("\"")
    }
    
    companion object {
        private val ISO8601_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"))
    }
}
