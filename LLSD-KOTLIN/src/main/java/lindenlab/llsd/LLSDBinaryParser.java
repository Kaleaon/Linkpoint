/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * A parser for LLSD (Linden Lab Structured Data) in its binary representation.
 * <p>
 * This class is responsible for taking an {@link InputStream} containing LLSD
 * binary data and converting it into a Java object representation, wrapped in an
 * {@link LLSD} object. The binary format is a compact, efficient, and strongly-typed
 * serialization format designed for high-performance applications.
 * <p>
 * The parser handles all standard LLSD data types, including integers, reals,
 * strings, UUIDs, dates, URIs, binary data, arrays, and maps. It correctly
 * interprets the specific byte markers and data layouts defined in the LLSD
 * binary specification.
 * <p>
 * An optional header, {@code "<?llsd/binary?>"}, may be present at the beginning
 * of the stream. This parser can handle streams with or without this header.
 *
 * @see LLSD
 * @see LLSDBinarySerializer
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD#Binary_Serialization">LLSD Binary Specification</a>
 */
public class LLSDBinaryParser {
    private static final String LLSD_BINARY_HEADER = "<?llsd/binary?>";
    private static final byte[] LLSD_BINARY_HEADER_BYTES = LLSD_BINARY_HEADER.getBytes(StandardCharsets.US_ASCII);
    private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    
    // Security limits to prevent memory exhaustion attacks
    private static final int MAX_COLLECTION_SIZE = 1_000_000; // Maximum array/map size
    private static final int MAX_RECURSION_DEPTH = 1000; // Maximum nesting depth

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
     * Initializes a new instance of the {@code LLSDBinaryParser}.
     */
    public LLSDBinaryParser() {
        // No initialization needed - formatters are created locally for thread safety
    }

    /**
     * Parses an LLSD document from a binary input stream.
     * <p>
     * This is the main entry point for parsing. It reads the stream, optionally
     * validates the binary header, and then recursively parses the LLSD data
     * structure.
     *
     * @param binaryInput The input stream containing the binary LLSD data.
     * @return An {@link LLSD} object representing the parsed data.
     * @throws IOException   if an I/O error occurs while reading from the stream.
     * @throws LLSDException if the data is not valid LLSD binary format, for
     *                       example, due to an unknown marker or unexpected end of stream.
     */
    public LLSD parse(final InputStream binaryInput) throws IOException, LLSDException {
        BinaryReader reader = new BinaryReader(binaryInput);
        
        // Check for optional header
        skipWhitespace(reader);
        if (reader.peek() == '<') {
            // Read and verify header
            byte[] header = reader.readBytes(LLSD_BINARY_HEADER_BYTES.length);
            if (!Arrays.equals(header, LLSD_BINARY_HEADER_BYTES)) {
                String actualHeader = new String(header, StandardCharsets.US_ASCII);
                throw new LLSDException("Invalid binary LLSD header: expected '" + LLSD_BINARY_HEADER + "', got '" + actualHeader + "'");
            }
            skipWhitespace(reader);
        }
        
        Object parsedBinary = parseBinaryValue(reader, 0);
        return new LLSD(parsedBinary);
    }

    /**
     * An internal helper class for reading binary data from an input stream.
     * It provides methods to read specific data types (like integers and doubles)
     * and handles peeking at the next byte without consuming it.
     */
    private static class BinaryReader {
        private final InputStream input;
        private byte[] buffer = new byte[1];
        private boolean hasPeeked = false;
        private byte peekedByte;

        /**
         * Constructs a BinaryReader that reads from the given InputStream.
         * @param input The stream to read from.
         */
        public BinaryReader(InputStream input) {
            this.input = input;
        }

        /**
         * Peeks at the next byte in the stream without consuming it.
         * @return The next byte.
         * @throws IOException if an I/O error occurs.
         * @throws LLSDException if the end of the stream is reached.
         */
        public byte peek() throws IOException, LLSDException {
            if (!hasPeeked) {
                int result = input.read(buffer);
                if (result == -1) {
                    throw new LLSDException("Unexpected end of binary stream");
                }
                peekedByte = buffer[0];
                hasPeeked = true;
            }
            return peekedByte;
        }

        /**
         * Reads and consumes the next byte from the stream.
         * @return The byte that was read.
         * @throws IOException if an I/O error occurs.
         * @throws LLSDException if the end of the stream is reached.
         */
        public byte readByte() throws IOException, LLSDException {
            if (hasPeeked) {
                hasPeeked = false;
                return peekedByte;
            }
            
            int result = input.read(buffer);
            if (result == -1) {
                throw new LLSDException("Unexpected end of binary stream");
            }
            return buffer[0];
        }

        /**
         * Reads a specified number of bytes from the stream.
         * @param count The number of bytes to read.
         * @return A byte array containing the read bytes.
         * @throws IOException if an I/O error occurs.
         * @throws LLSDException if the end of the stream is reached before all bytes are read, 
         *                       or if count is negative or excessively large.
         */
        public byte[] readBytes(int count) throws IOException, LLSDException {
            if (count < 0) {
                throw new LLSDException("Cannot read negative number of bytes: " + count);
            }
            if (count == 0) {
                return new byte[0];
            }
            // Prevent potential memory exhaustion attacks
            if (count > 100_000_000) { // 100MB limit
                throw new LLSDException("Attempting to read excessively large amount of data: " + count + " bytes");
            }
            
            byte[] bytes = new byte[count];
            int totalRead = 0;
            
            // First use any peeked byte
            if (hasPeeked && count > 0) {
                bytes[0] = peekedByte;
                totalRead = 1;
                hasPeeked = false;
            }
            
            while (totalRead < count) {
                int bytesRead = input.read(bytes, totalRead, count - totalRead);
                if (bytesRead == -1) {
                    throw new LLSDException("Unexpected end of binary stream: expected " + count + " bytes, got " + totalRead);
                }
                totalRead += bytesRead;
            }
            
            return bytes;
        }

        /**
         * Reads a 32-bit integer in big-endian order.
         * @return The integer value.
         * @throws IOException if an I/O error occurs.
         * @throws LLSDException if the stream ends prematurely.
         */
        public int readInt32() throws IOException, LLSDException {
            byte[] bytes = readBytes(4);
            return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
        }

        /**
         * Reads a 64-bit double-precision floating-point number in big-endian order.
         * @return The double value.
         * @throws IOException if an I/O error occurs.
         * @throws LLSDException if the stream ends prematurely.
         */
        public double readDouble() throws IOException, LLSDException {
            byte[] bytes = readBytes(8);
            return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getDouble();
        }

        /**
         * Reads a string, which is prefixed by its 32-bit length.
         * @return The string value.
         * @throws IOException if an I/O error occurs.
         * @throws LLSDException if the stream ends prematurely or if the string length is invalid.
         */
        public String readString() throws IOException, LLSDException {
            int length = readInt32();
            if (length < 0) {
                throw new LLSDException("Invalid string length: " + length);
            }
            if (length == 0) {
                return "";
            }
            // Prevent potential memory exhaustion attacks with reasonable string size limit
            if (length > 10_000_000) { // 10MB limit for strings
                throw new LLSDException("String length too large: " + length + " bytes");
            }
            byte[] bytes = readBytes(length);
            return new String(bytes, StandardCharsets.UTF_8);
        }

        /**
         * Checks if there is more data available to be read from the stream.
         * @return true if more data is available, false otherwise.
         * @throws IOException if an I/O error occurs.
         */
        public boolean isAvailable() throws IOException {
            return input.available() > 0 || hasPeeked;
        }
    }

    /**
     * Skips any whitespace characters from the input stream.
     * @param reader The BinaryReader to read from.
     * @throws IOException if an I/O error occurs.
     */
    private void skipWhitespace(BinaryReader reader) throws IOException {
        try {
            while (reader.isAvailable()) {
                byte b = reader.peek();
                if (Character.isWhitespace((char) b)) {
                    reader.readByte(); // consume whitespace
                } else {
                    break;
                }
            }
        } catch (LLSDException e) {
            // End of stream is ok when skipping whitespace
        }
    }

    /**
     * Parses a single LLSD value from the stream based on its type marker.
     * This is the core of the recursive descent parser.
     * @param reader The BinaryReader to use for reading data.
     * @return The parsed Java object.
     * @throws IOException if an I/O error occurs.
     * @throws LLSDException if an unknown marker or invalid data is encountered.
     */
    private Object parseBinaryValue(BinaryReader reader, int depth) throws IOException, LLSDException {
        if (depth > MAX_RECURSION_DEPTH) {
            throw new LLSDException("Maximum recursion depth exceeded: " + depth);
        }
        
        byte marker = reader.readByte();

        switch (marker) {
            case UNDEF_MARKER:
                return ""; // Undefined value represented as empty string

            case TRUE_MARKER:
                return Boolean.TRUE;

            case FALSE_MARKER:
                return Boolean.FALSE;

            case INTEGER_MARKER:
                return reader.readInt32();

            case REAL_MARKER:
                return reader.readDouble();

            case STRING_MARKER:
                return reader.readString();

            case UUID_MARKER:
                return parseUUID(reader);

            case DATE_MARKER:
                return parseDate(reader);

            case URI_MARKER:
                return parseURI(reader);

            case BINARY_MARKER:
                return parseBinary(reader);

            case ARRAY_BEGIN_MARKER:
                return parseArray(reader, depth + 1);

            case MAP_BEGIN_MARKER:
                return parseMap(reader, depth + 1);

            default:
                throw new LLSDException("Unknown binary LLSD marker: 0x" + 
                    Integer.toHexString(marker & 0xFF).toUpperCase());
        }
    }

    /**
     * Parses a 16-byte UUID from the stream.
     * @param reader The BinaryReader to read from.
     * @return A {@link UUID} object.
     * @throws IOException if an I/O error occurs.
     * @throws LLSDException if the stream ends prematurely.
     */
    private UUID parseUUID(BinaryReader reader) throws IOException, LLSDException {
        byte[] bytes = reader.readBytes(16);
        
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        long mostSigBits = buffer.getLong();
        long leastSigBits = buffer.getLong();
        
        return new UUID(mostSigBits, leastSigBits);
    }

    /**
     * Parses an 8-byte date value (double-precision seconds since the Unix epoch).
     * @param reader The BinaryReader to read from.
     * @return A {@link Date} object.
     * @throws IOException if an I/O error occurs.
     * @throws LLSDException if the stream ends prematurely.
     */
    private Date parseDate(BinaryReader reader) throws IOException, LLSDException {
        double secondsSinceEpoch = reader.readDouble();
        long millisSinceEpoch = (long) (secondsSinceEpoch * 1000.0);
        return new Date(millisSinceEpoch);
    }

    /**
     * Parses a URI from the stream.
     * @param reader The BinaryReader to read from.
     * @return A {@link URI} object.
     * @throws IOException if an I/O error occurs.
     * @throws LLSDException if the URI syntax is invalid or the stream ends prematurely.
     */
    private URI parseURI(BinaryReader reader) throws IOException, LLSDException {
        String uriString = reader.readString();
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            throw new LLSDException("Invalid URI in binary LLSD: " + uriString, e);
        }
    }

    /**
     * Parses a block of binary data from the stream.
     * @param reader The BinaryReader to read from.
     * @return A byte array containing the binary data.
     * @throws IOException if an I/O error occurs.
     * @throws LLSDException if the stream ends prematurely.
     */
    private byte[] parseBinary(BinaryReader reader) throws IOException, LLSDException {
        int length = reader.readInt32();
        if (length < 0) {
            throw new LLSDException("Invalid binary length: " + length);
        }
        if (length == 0) {
            return new byte[0];
        }
        return reader.readBytes(length);
    }

    /**
     * Parses an array from the stream. It reads elements recursively until it
     * encounters an array-end marker.
     * @param reader The BinaryReader to read from.
     * @param depth Current recursion depth for stack overflow protection.
     * @return A {@link List} of parsed objects.
     * @throws IOException if an I/O error occurs.
     * @throws LLSDException if the array is malformed or the stream ends prematurely.
     */
    private List<Object> parseArray(BinaryReader reader, int depth) throws IOException, LLSDException {
        List<Object> array = new ArrayList<>();
        int elementCount = 0;

        while (reader.isAvailable()) {
            if (elementCount >= MAX_COLLECTION_SIZE) {
                throw new LLSDException("Array size limit exceeded: " + MAX_COLLECTION_SIZE);
            }
            
            byte marker = reader.peek();
            if (marker == ARRAY_END_MARKER) {
                reader.readByte(); // consume ']'
                break;
            }
            
            Object value = parseBinaryValue(reader, depth);
            array.add(value);
            elementCount++;
        }

        return array;
    }

    /**
     * Parses a map from the stream. It reads key-value pairs recursively until
     * it encounters a map-end marker.
     * @param reader The BinaryReader to read from.
     * @param depth Current recursion depth for stack overflow protection.
     * @return A {@link Map} of parsed key-value pairs.
     * @throws IOException if an I/O error occurs.
     * @throws LLSDException if the map is malformed (e.g., missing key marker)
     *                       or the stream ends prematurely.
     */
    private Map<String, Object> parseMap(BinaryReader reader, int depth) throws IOException, LLSDException {
        Map<String, Object> map = new HashMap<>();
        int elementCount = 0;

        while (reader.isAvailable()) {
            if (elementCount >= MAX_COLLECTION_SIZE) {
                throw new LLSDException("Map size limit exceeded: " + MAX_COLLECTION_SIZE);
            }
            
            byte marker = reader.peek();
            if (marker == MAP_END_MARKER) {
                reader.readByte(); // consume '}'
                break;
            }

            // Read key
            if (reader.readByte() != KEY_MARKER) {
                throw new LLSDException("Expected key marker 'k' in binary LLSD map");
            }
            
            String key = reader.readString();
            Object value = parseBinaryValue(reader, depth);
            map.put(key, value);
            elementCount++;
        }

        return map;
    }
}