/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * A serializer for converting LLSD (Linden Lab Structured Data) objects into
 * their notation format.
 * <p>
 * This class takes an {@link LLSD} object and writes its content to a
 * {@link Writer} as a compact, human-readable string. The notation format uses
 * single-character prefixes to denote data types, making it more concise than
 * XML or JSON but still easy to read and edit by hand.
 * <p>
 * The serializer handles all standard LLSD types, converting them to their
 * corresponding notation representation (e.g., an integer {@code 42} becomes
 * {@code i42}). It also correctly formats arrays, maps, and special values
 * like dates and UUIDs.
 *
 * @see LLSD
 * @see LLSDNotationParser
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD#Notation_Serialization">LLSD Notation Specification</a>
 */
public class LLSDNotationSerializer {
    private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Initializes a new instance of the {@code LLSDNotationSerializer}.
     */
    public LLSDNotationSerializer() {
        // No initialization needed - date formatters are created locally for thread safety
    }

    /**
     * Serializes an LLSD document into its notation representation and writes it
     * to the provided {@link Writer}.
     *
     * @param llsd   The {@link LLSD} document to be serialized.
     * @param writer The {@link Writer} to which the notation output will be sent.
     * @throws IOException   if an I/O error occurs while writing.
     * @throws LLSDException if the document contains an unserializable data type.
     */
    public void serialize(LLSD llsd, Writer writer) throws IOException, LLSDException {
        serializeValue(llsd.getContent(), writer);
    }

    /**
     * Recursively serializes a single LLSD value into its notation representation.
     * <p>
     * This is the core of the serializer. It inspects the object's type and
     * writes the corresponding type marker and formatted value.
     *
     * @param value  The object to serialize.
     * @param writer The writer for the output.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the value type is not supported.
     */
    private void serializeValue(Object value, Writer writer) throws IOException, LLSDException {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            writer.write("!");
            return;
        }

        if (value instanceof Map) {
            serializeMap(value, writer);
        } else if (value instanceof List) {
            serializeArray(value, writer);
        } else if (value instanceof String) {
            serializeString((String) value, writer);
        } else if (value instanceof Integer) {
            writer.write("i");
            writer.write(value.toString());
        } else if (value instanceof Double) {
            Double d = (Double) value;
            writer.write("r");
            if (d.isNaN()) {
                writer.write("nan");
            } else if (d.isInfinite()) {
                writer.write(d > 0 ? "inf" : "-inf");
            } else {
                writer.write(d.toString());
            }
        } else if (value instanceof Boolean) {
            writer.write(((Boolean) value) ? "1" : "0");
        } else if (value instanceof Date) {
            writer.write("d");
            // Create a new DateFormat instance for thread safety
            DateFormat dateFormat = new SimpleDateFormat(ISO8601_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            writer.write(dateFormat.format((Date) value));
        } else if (value instanceof URI) {
            writer.write("l");
            writer.write(value.toString());
        } else if (value instanceof UUID) {
            writer.write("u");
            writer.write(value.toString());
        } else if (value instanceof byte[]) {
            serializeBinary((byte[]) value, writer);
        } else if (value instanceof LLSDUndefined) {
            writer.write("!");
        } else {
            // Fallback to string representation
            serializeString(value.toString(), writer);
        }
    }

    /** Serializes a Map into notation as {@code {key:value,...}}. */
    @SuppressWarnings("unchecked")
    private void serializeMap(Object value, Writer writer) throws IOException, LLSDException {
        Map<String, Object> map = (Map<String, Object>) value;
        
        writer.write("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) {
                writer.write(",");
            }
            first = false;
            
            // Serialize key
            String key = entry.getKey();
            if (isValidIdentifier(key)) {
                writer.write(key);
            } else {
                serializeString(key, writer);
            }
            
            writer.write(":");
            serializeValue(entry.getValue(), writer);
        }
        writer.write("}");
    }

    /** Serializes a List into notation as {@code [value1,value2,...]}. */
    @SuppressWarnings("unchecked")
    private void serializeArray(Object value, Writer writer) throws IOException, LLSDException {
        List<Object> list = (List<Object>) value;
        
        writer.write("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) {
                writer.write(",");
            }
            first = false;
            
            serializeValue(item, writer);
        }
        writer.write("]");
    }

    /** Serializes a String into notation as {@code s'...'}. */
    private void serializeString(String str, Writer writer) throws IOException {
        writer.write("s'");
        // Escape single quotes and backslashes
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '\'':
                    writer.write("\\'");
                    break;
                case '\\':
                    writer.write("\\\\");
                    break;
                case '\n':
                    writer.write("\\n");
                    break;
                case '\r':
                    writer.write("\\r");
                    break;
                case '\t':
                    writer.write("\\t");
                    break;
                default:
                    writer.write(c);
                    break;
            }
        }
        writer.write("'");
    }

    /** Serializes a byte array into base64 notation as {@code b64"..."}. */
    private void serializeBinary(byte[] data, Writer writer) throws IOException {
        writer.write("b64\"");
        writer.write(Base64.getEncoder().encodeToString(data));
        writer.write("\"");
    }

    /**
     * Checks if a string is a valid identifier in LLSD notation.
     * <p>
     * A valid identifier can be used as an unquoted map key. It must start
     * with a letter or underscore, followed by letters, digits, or underscores.
     *
     * @param str The string to check.
     * @return {@code true} if the string is a valid identifier, {@code false} otherwise.
     */
    private boolean isValidIdentifier(String str) {
        if (str.isEmpty()) {
            return false;
        }
        
        char first = str.charAt(0);
        if (!Character.isLetter(first) && first != '_') {
            return false;
        }
        
        for (int i = 1; i < str.length(); i++) {
            char c = str.charAt(i);
            if (!Character.isLetterOrDigit(c) && c != '_') {
                return false;
            }
        }
        
        return true;
    }
}