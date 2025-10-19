/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A serializer for converting LLSD (Linden Lab Structured Data) objects into
 * their binary representation.
 * <p>
 * This class takes an {@link LLSD} object and writes its content to an
 * {@link OutputStream} or returns it as a {@code byte[]}. The binary format
 * is a compact and efficient way to represent LLSD data, suitable for network
 * transmission or storage.
 * <p>
 * The serializer correctly handles all standard LLSD data types, converting them
 * into the byte-level format specified by the LLSD binary protocol. This includes
 * using specific markers for each data type and encoding values in big-endian order.
 *
 * @see LLSD
 * @see LLSDBinaryParser
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD#Binary_Serialization">LLSD Binary Specification</a>
 */
public class LLSDBinarySerializer {
    private static final String LLSD_BINARY_HEADER = "<?llsd/binary?>";
    private static final byte[] LLSD_BINARY_HEADER_BYTES = LLSD_BINARY_HEADER.getBytes(StandardCharsets.US_ASCII);

    // Binary markers
    private static final byte UNDEF_MARKER = (byte) '!';
    private static final byte TRUE_MARKER = (byte) '1';
    private static final byte FALSE_MARKER = (byte) '0';
    private static final byte INTEGER_MARKER = (byte) 'i';
    private static final byte REAL_MARKER = (byte) 'r';
    private static final byte STRING_MARKER = (byte) 's';
    private static final byte UUID_MARKER = (byte) 'u';
    private static final byte DATE_MARKER = (byte) 'd';
    private static final byte URI_MARKER = (byte) 'l';
    private static final byte BINARY_MARKER = (byte) 'b';
    private static final byte ARRAY_BEGIN_MARKER = (byte) '[';
    private static final byte ARRAY_END_MARKER = (byte) ']';
    private static final byte MAP_BEGIN_MARKER = (byte) '{';
    private static final byte MAP_END_MARKER = (byte) '}';
    private static final byte KEY_MARKER = (byte) 'k';

    /**
     * Initializes a new instance of the {@code LLSDBinarySerializer}.
     */
    public LLSDBinarySerializer() {
    }

    /**
     * Serializes an LLSD document into its binary representation and writes it to
     * an output stream.
     *
     * @param llsd          The {@link LLSD} document to serialize.
     * @param output        The {@link OutputStream} to which the binary data will be written.
     * @param includeHeader If true, the standard binary header ({@code "<?llsd/binary?>"})
     *                      will be written at the beginning of the stream.
     * @throws IOException   if an I/O error occurs while writing to the stream.
     * @throws LLSDException if the LLSD object contains data that cannot be serialized.
     */
    public void serialize(LLSD llsd, OutputStream output, boolean includeHeader)
            throws IOException, LLSDException {
        if (includeHeader) {
            output.write(LLSD_BINARY_HEADER_BYTES);
        }
        serializeValue(llsd.getContent(), output);
    }

    /**
     * Serializes an LLSD document to an output stream, including the binary header by default.
     *
     * @param llsd   The {@link LLSD} document to serialize.
     * @param output The {@link OutputStream} to write to.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the data cannot be serialized.
     */
    public void serialize(LLSD llsd, OutputStream output) throws IOException, LLSDException {
        serialize(llsd, output, true);
    }

    /**
     * Serializes an LLSD document into a binary byte array.
     *
     * @param llsd          The {@link LLSD} document to serialize.
     * @param includeHeader If true, the header is included in the returned byte array.
     * @return A {@code byte[]} containing the serialized binary data.
     * @throws LLSDException if the serialization fails.
     */
    public byte[] serialize(LLSD llsd, boolean includeHeader) throws LLSDException {
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            serialize(llsd, output, includeHeader);
            return output.toByteArray();
        } catch (IOException e) {
            throw new LLSDException("Failed to serialize binary LLSD", e);
        }
    }

    /**
     * Serializes an LLSD document into a binary byte array, including the header by default.
     *
     * @param llsd The {@link LLSD} document to serialize.
     * @return A {@code byte[]} containing the serialized binary data.
     * @throws LLSDException if the serialization fails.
     */
    public byte[] serialize(LLSD llsd) throws LLSDException {
        return serialize(llsd, true);
    }

    /**
     * Recursively serializes a single LLSD value to the output stream.
     * <p>
     * This method determines the type of the given value and calls the appropriate
     * helper method to write the corresponding binary representation.
     *
     * @param value  The object to serialize.
     * @param output The stream to write to.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the value's type is not serializable.
     */
    private void serializeValue(Object value, OutputStream output) throws IOException, LLSDException {
        if (value == null || (value instanceof String && ((String) value).isEmpty())) {
            output.write(UNDEF_MARKER);
            return;
        }

        if (value instanceof Map) {
            serializeMap(value, output);
        } else if (value instanceof List) {
            serializeArray(value, output);
        } else if (value instanceof String) {
            serializeString((String) value, output);
        } else if (value instanceof Integer) {
            serializeInteger((Integer) value, output);
        } else if (value instanceof Double) {
            serializeReal((Double) value, output);
        } else if (value instanceof Boolean) {
            serializeBoolean((Boolean) value, output);
        } else if (value instanceof Date) {
            serializeDate((Date) value, output);
        } else if (value instanceof URI) {
            serializeURI((URI) value, output);
        } else if (value instanceof UUID) {
            serializeUUID((UUID) value, output);
        } else if (value instanceof byte[]) {
            serializeBinary((byte[]) value, output);
        } else if (value instanceof LLSDUndefined) {
            output.write(UNDEF_MARKER);
        } else {
            // Fallback to string representation
            if (value == null) {
                output.write(UNDEF_MARKER);
            } else {
                serializeString(value.toString(), output);
            }
        }
    }

    /** Writes a boolean value as a single byte marker ('1' or '0'). */
    private void serializeBoolean(Boolean value, OutputStream output) throws IOException, LLSDException {
        if (value == null) {
            throw new LLSDException("Cannot serialize null boolean");
        }
        output.write(value ? TRUE_MARKER : FALSE_MARKER);
    }

    /** Writes an integer value with its marker and 4-byte big-endian representation. */
    private void serializeInteger(Integer value, OutputStream output) throws IOException, LLSDException {
        if (value == null) {
            throw new LLSDException("Cannot serialize null integer");
        }
        output.write(INTEGER_MARKER);
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(value);
        output.write(buffer.array());
    }

    /** Writes a double value with its marker and 8-byte big-endian representation. */
    private void serializeReal(Double value, OutputStream output) throws IOException, LLSDException {
        if (value == null) {
            throw new LLSDException("Cannot serialize null double");
        }
        output.write(REAL_MARKER);
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        buffer.putDouble(value);
        output.write(buffer.array());
    }

    /** Writes a string value with its marker, a 4-byte length prefix, and UTF-8 bytes. */
    private void serializeString(String value, OutputStream output) throws IOException, LLSDException {
        if (value == null) {
            throw new LLSDException("Cannot serialize null string");
        }
        output.write(STRING_MARKER);
        byte[] stringBytes = value.getBytes(StandardCharsets.UTF_8);
        writeInt32(output, stringBytes.length);
        output.write(stringBytes);
    }

    /** Writes a UUID value with its marker and 16-byte representation. */
    private void serializeUUID(UUID value, OutputStream output) throws IOException {
        output.write(UUID_MARKER);
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putLong(value.getMostSignificantBits());
        buffer.putLong(value.getLeastSignificantBits());
        output.write(buffer.array());
    }

    /** Writes a Date value with its marker and 8-byte double representation (seconds since epoch). */
    private void serializeDate(Date value, OutputStream output) throws IOException {
        output.write(DATE_MARKER);
        double secondsSinceEpoch = value.getTime() / 1000.0;
        ByteBuffer buffer = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN);
        buffer.putDouble(secondsSinceEpoch);
        output.write(buffer.array());
    }

    /** Writes a URI value with its marker, a 4-byte length prefix, and UTF-8 bytes. */
    private void serializeURI(URI value, OutputStream output) throws IOException {
        output.write(URI_MARKER);
        byte[] uriBytes = value.toString().getBytes(StandardCharsets.UTF_8);
        writeInt32(output, uriBytes.length);
        output.write(uriBytes);
    }

    /** Writes a binary data block with its marker, a 4-byte length prefix, and raw bytes. */
    private void serializeBinary(byte[] value, OutputStream output) throws IOException {
        output.write(BINARY_MARKER);
        writeInt32(output, value.length);
        output.write(value);
    }

    /** Serializes a List into an LLSD array, enclosed in array markers. */
    @SuppressWarnings("unchecked")
    private void serializeArray(Object value, OutputStream output) throws IOException, LLSDException {
        List<Object> list = (List<Object>) value;
        
        output.write(ARRAY_BEGIN_MARKER);
        for (Object item : list) {
            serializeValue(item, output);
        }
        output.write(ARRAY_END_MARKER);
    }

    /** Serializes a Map into an LLSD map, enclosed in map markers, with keys and values. */
    @SuppressWarnings("unchecked")
    private void serializeMap(Object value, OutputStream output) throws IOException, LLSDException {
        Map<String, Object> map = (Map<String, Object>) value;
        
        output.write(MAP_BEGIN_MARKER);
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            // Write key
            output.write(KEY_MARKER);
            byte[] keyBytes = entry.getKey().getBytes(StandardCharsets.UTF_8);
            writeInt32(output, keyBytes.length);
            output.write(keyBytes);
            
            // Write value
            serializeValue(entry.getValue(), output);
        }
        output.write(MAP_END_MARKER);
    }

    /**
     * Helper method to write a 32-bit integer in big-endian format to the stream.
     * @param output The stream to write to.
     * @param value The integer value to write.
     * @throws IOException if an I/O error occurs.
     */
    private void writeInt32(OutputStream output, int value) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        buffer.putInt(value);
        output.write(buffer.array());
    }
}