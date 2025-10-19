/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd

import java.io.IOException
import java.io.StringWriter
import java.io.Writer
import java.net.URI
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Represents a complete LLSD document, which is a container for a single LLSD element.
 *
 * LLSD (Linden Lab Structured Data) is a serialization format used in Second Life
 * for various data interchange tasks. This class encapsulates a single LLSD value,
 * which can be of any type supported by the LLSD specification (e.g., integer,
 * string, map, array).
 *
 * This class provides methods to:
 * - Hold any valid LLSD data type as its content.
 * - Serialize the contained data into LLSD XML format.
 * - Provide a string representation of the data in XML format.
 *
 * @see [LLSD Specification](http://wiki.secondlife.com/wiki/LLSD)
 * @see LLSDParser
 * @see LLSDJsonSerializer
 * @see LLSDBinarySerializer
 * @see LLSDNotationSerializer
 */
class LLSD(val content: Any?) {
    
    /**
     * Serializes the contained LLSD data into its XML representation and writes it
     * to the provided [Writer].
     *
     * @param writer  The [Writer] to which the XML data will be written.
     * @param charset The character encoding to declare in the XML prolog (e.g., "UTF-8").
     * @throws IOException   if an I/O error occurs while writing to the writer.
     * @throws LLSDException if the content contains an unserializable data type.
     */
    @Throws(IOException::class, LLSDException::class)
    fun serialise(writer: Writer, charset: String) {
        writer.write("<?xml version=\"1.0\" encoding=\"$charset\"?>\n")
        writer.write("<llsd>\n")
        if (content == null) {
            writer.write("<undef/>\n")
        } else {
            serialiseElement(writer, content)
        }
        writer.write("</llsd>\n")
    }
    
    /**
     * Recursively serializes a single LLSD element to the writer.
     *
     * This method is called by [serialise] to handle the
     * serialization of the document's content. It determines the type of the
     * object and writes the corresponding LLSD XML tag and value.
     *
     * @param writer      The writer to output the XML to.
     * @param toSerialise The object to be serialized.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the object's type is not a valid LLSD type.
     */
    @Throws(IOException::class, LLSDException::class)
    private fun serialiseElement(writer: Writer, toSerialise: Any) {
        // Create thread-safe local formatter instances
        val dateFormat = SimpleDateFormat(ISO8601_PATTERN).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val decimalFormat = DecimalFormat("#0.0#")
        
        when (toSerialise) {
            is Map<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                val serialiseMap = toSerialise as Map<String, Any?>
                writer.write("<map>\n")
                for ((key, value) in serialiseMap) {
                    writer.write("\t<key>${encodeXML(key)}</key>\n\t")
                    value?.let { serialiseElement(writer, it) }
                }
                writer.write("</map>\n")
            }
            is List<*> -> {
                writer.write("<array>\n")
                for (current in toSerialise) {
                    writer.write("\t")
                    current?.let { serialiseElement(writer, it) }
                }
                writer.write("</array>\n")
            }
            is Boolean -> {
                writer.write("<boolean>$toSerialise</boolean>\n")
            }
            is Int -> {
                writer.write("<integer>$toSerialise</integer>\n")
            }
            is Double -> {
                if (toSerialise.isNaN()) {
                    writer.write("<real>nan</real>\n")
                } else {
                    writer.write("<real>${decimalFormat.format(toSerialise)}</real>\n")
                }
            }
            is Float -> {
                if (toSerialise.isNaN()) {
                    writer.write("<real>nan</real>\n")
                } else {
                    writer.write("<real>${decimalFormat.format(toSerialise)}</real>\n")
                }
            }
            is UUID -> {
                writer.write("<uuid>$toSerialise</uuid>\n")
            }
            is String -> {
                writer.write("<string>${encodeXML(toSerialise)}</string>\n")
            }
            is Date -> {
                writer.write("<date>${dateFormat.format(toSerialise)}</date>")
            }
            is URI -> {
                writer.write("<uri>${encodeXML(toSerialise.toString())}</uri>")
            }
            is ByteArray -> {
                // Handle binary data as base64 encoded
                val base64Data = Base64.getEncoder().encodeToString(toSerialise)
                writer.write("<binary>$base64Data</binary>\n")
            }
            is LLSDUndefined -> {
                when (toSerialise) {
                    LLSDUndefined.BINARY -> writer.write("<binary><undef /></binary>\n")
                    LLSDUndefined.BOOLEAN -> writer.write("<boolean><undef /></boolean>\n")
                    LLSDUndefined.DATE -> writer.write("<date><undef /></date>\n")
                    LLSDUndefined.INTEGER -> writer.write("<integer><undef /></integer>\n")
                    LLSDUndefined.REAL -> writer.write("<real><undef /></real>\n")
                    LLSDUndefined.STRING -> writer.write("<string><undef /></string>\n")
                    LLSDUndefined.URI -> writer.write("<uri><undef /></uri>\n")
                    LLSDUndefined.UUID -> writer.write("<uuid><undef /></uuid>\n")
                }
            }
            else -> {
                throw LLSDException("Unable to serialise type \"${toSerialise::class.java.name}\".")
            }
        }
    }
    
    /**
     * Returns the string representation of this LLSD document in XML format.
     *
     * This method is a convenience for debugging and logging. It serializes the
     * content to an LLSD XML string. If serialization fails, it returns an
     * error message instead of throwing an exception.
     *
     * @return A string containing the LLSD data in XML format, or an error
     *         message if serialization fails.
     */
    override fun toString(): String {
        val writer = StringWriter()
        return try {
            serialise(writer, "UTF-8")
            writer.toString()
        } catch (e: Exception) {
            "Unable to serialise LLSD for display: ${e.message}"
        }
    }
    
    companion object {
        private const val ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'"
        
        /**
         * Encodes a string for safe inclusion in an XML document.
         *
         * This method replaces special XML characters like `<, >, &, "`
         * with their corresponding XML entities (e.g., `&lt;`, `&gt;`).
         *
         * @param text The string to be encoded.
         * @return An XML-safe string. Returns "null" if the input is null.
         */
        @JvmStatic
        fun encodeXML(text: String?): String {
            if (text == null) return "null"
            if (text.isEmpty()) return text
            
            val textLength = text.length
            var encodeBufferSize = textLength
            
            // Calculate required buffer size
            for (i in 0 until textLength) {
                when (text[i]) {
                    '<', '>' -> encodeBufferSize += 3
                    '&', '-' -> encodeBufferSize += 4
                    '"' -> encodeBufferSize += 5
                }
            }
            
            if (encodeBufferSize == textLength) return text
            
            val encodeBuffer = CharArray(encodeBufferSize)
            var j = 0
            
            for (i in 0 until textLength) {
                val currentChar = text[i]
                when (currentChar) {
                    '<' -> {
                        encodeBuffer[j++] = '&'
                        encodeBuffer[j++] = 'l'
                        encodeBuffer[j++] = 't'
                        encodeBuffer[j++] = ';'
                    }
                    '>' -> {
                        encodeBuffer[j++] = '&'
                        encodeBuffer[j++] = 'g'
                        encodeBuffer[j++] = 't'
                        encodeBuffer[j++] = ';'
                    }
                    '&' -> {
                        encodeBuffer[j++] = '&'
                        encodeBuffer[j++] = 'a'
                        encodeBuffer[j++] = 'm'
                        encodeBuffer[j++] = 'p'
                        encodeBuffer[j++] = ';'
                    }
                    '"' -> {
                        encodeBuffer[j++] = '&'
                        encodeBuffer[j++] = 'q'
                        encodeBuffer[j++] = 'u'
                        encodeBuffer[j++] = 'o'
                        encodeBuffer[j++] = 't'
                        encodeBuffer[j++] = ';'
                    }
                    '-' -> {
                        encodeBuffer[j++] = '&'
                        encodeBuffer[j++] = '#'
                        encodeBuffer[j++] = '4'
                        encodeBuffer[j++] = '5'
                        encodeBuffer[j++] = ';'
                    }
                    else -> encodeBuffer[j++] = currentChar
                }
            }
            
            return String(encodeBuffer)
        }
    }
}
