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
 * their JSON representation.
 * <p>
 * This class takes an {@link LLSD} object and writes its content to a
 * {@link Writer} in JSON format. It adheres to the specific conventions used
 * by LLSD for representing data types like dates, URIs, and binary data within
 * the JSON structure.
 * <p>
 * For example, a {@link java.util.Date} object is not serialized as a simple
 * number, but as a JSON object with a special key: {@code {"d": "YYYY-MM-DDTHH:MM:SSZ"}}.
 * This class handles all such conversions automatically.
 *
 * @see LLSD
 * @see LLSDJsonParser
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD#JSON_Serialization">LLSD JSON Specification</a>
 */
public class LLSDJsonSerializer {
    private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Initializes a new instance of the {@code LLSDJsonSerializer}.
     */
    public LLSDJsonSerializer() {
        // No initialization needed - date formatters are created locally for thread safety
    }

    /**
     * Serializes an LLSD document into its JSON representation and writes it to
     * the provided {@link Writer}.
     *
     * @param llsd   The {@link LLSD} document to be serialized.
     * @param writer The {@link Writer} to which the JSON output will be sent.
     * @throws IOException   if an I/O error occurs while writing.
     * @throws LLSDException if the document contains an unserializable data type.
     */
    public void serialize(LLSD llsd, Writer writer) throws IOException, LLSDException {
        serializeValue(llsd.getContent(), writer);
    }

    /**
     * Recursively serializes a single LLSD value into its JSON representation.
     * <p>
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
    private void serializeValue(Object value, Writer writer) throws IOException, LLSDException {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            writer.write("null");
            return;
        }

        if (value instanceof Map) {
            serializeMap(value, writer);
        } else if (value instanceof List) {
            serializeArray(value, writer);
        } else if (value instanceof String) {
            serializeString((String) value, writer);
        } else if (value instanceof Integer) {
            writer.write(value.toString());
        } else if (value instanceof Double) {
            Double d = (Double) value;
            if (d.isNaN()) {
                writer.write("\"NaN\""); // Special case for NaN
            } else if (d.isInfinite()) {
                writer.write(d > 0 ? "\"Infinity\"" : "\"-Infinity\"");
            } else {
                writer.write(d.toString());
            }
        } else if (value instanceof Boolean) {
            writer.write(value.toString());
        } else if (value instanceof Date) {
            writer.write("{\"d\":\"");
            // Create a new DateFormat instance for thread safety
            DateFormat dateFormat = new SimpleDateFormat(ISO8601_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            writer.write(dateFormat.format((Date) value));
            writer.write("\"}");
        } else if (value instanceof URI) {
            writer.write("{\"u\":");
            serializeString(value.toString(), writer);
            writer.write("}");
        } else if (value instanceof UUID) {
            writer.write("{\"i\":");
            serializeString(value.toString(), writer);
            writer.write("}");
        } else if (value instanceof byte[]) {
            writer.write("{\"b\":");
            serializeString(Base64.getEncoder().encodeToString((byte[]) value), writer);
            writer.write("}");
        } else if (value instanceof LLSDUndefined) {
            writer.write("null");
        } else {
            // Fallback to string representation
            serializeString(value.toString(), writer);
        }
    }

    /** Serializes a Map into a JSON object. */
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
            
            serializeString(entry.getKey(), writer);
            writer.write(":");
            serializeValue(entry.getValue(), writer);
        }
        writer.write("}");
    }

    /** Serializes a List into a JSON array. */
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

    /**
     * Serializes a String into a JSON string, properly escaping special characters.
     * This includes quotes, backslashes, and control characters.
     *
     * @param str    The string to serialize.
     * @param writer The writer to output the JSON to.
     * @throws IOException if an I/O error occurs.
     */
    private void serializeString(String str, Writer writer) throws IOException {
        writer.write("\"");
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"':
                    writer.write("\\\"");
                    break;
                case '\\':
                    writer.write("\\\\");
                    break;
                case '\b':
                    writer.write("\\b");
                    break;
                case '\f':
                    writer.write("\\f");
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
                    if (c < 0x20 || c > 0x7e) {
                        writer.write(String.format("\\u%04x", (int) c));
                    } else {
                        writer.write(c);
                    }
                    break;
            }
        }
        writer.write("\"");
    }
}