/*
 * LLSD Serialization Framework - Java implementation based on Second Life/Firestorm C++ code
 *
 * Converted from indra/llcommon/llsdserialize.h/cpp  
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer;

import lindenlab.llsd.LLSD;
import lindenlab.llsd.LLSDException;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * An abstract base class for LLSD parsers, based on the serialization framework
 * from the Second Life viewer's C++ codebase (LLSDSerialize).
 * <p>
 * This class introduces advanced features not found in the basic parsers, such
 * as configurable limits for parsing depth and total bytes consumed. These features
 * are crucial for security and stability when parsing untrusted data.
 * <p>
 * Subclasses must implement the {@link #doParse(InputStream, int)} method to provide
 * the parsing logic for a specific LLSD format (e.g., XML, Binary).
 *
 * @see <a href="https://github.com/secondlife/viewer/blob/main/indra/llcommon/llsdserialize.h">llsdserialize.h</a>
 * @see LLSD
 */
public abstract class LLSDViewerSerializer {
    
    /**
     * Parser failure constant.
     */
    public static final int PARSE_FAILURE = -1;
    
    /**
     * Unlimited size constant.
     */
    public static final long SIZE_UNLIMITED = -1;
    
    /**
     * Default maximum parsing depth.
     */
    public static final int DEFAULT_MAX_DEPTH = 64;
    
    protected boolean checkLimits = true;
    protected long maxBytesLeft = SIZE_UNLIMITED;
    protected boolean parseLines = false;
    
    /**
     * Parses LLSD data from an {@link InputStream} with specified byte and depth limits.
     * <p>
     * This is the main entry point for parsing with this framework. It sets up the
     * parsing limits and then calls the abstract {@link #doParse} method to perform
     * the actual parsing.
     *
     * @param input    The input stream to read from.
     * @param maxBytes The maximum number of bytes to consume from the stream. Use
     *                 {@link #SIZE_UNLIMITED} for no limit.
     * @param maxDepth The maximum allowed nesting depth of the data structure. Use
     *                 -1 for the default limit.
     * @return A parsed {@link LLSD} object.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if a parsing error occurs, or if the configured
     *                       byte or depth limits are exceeded.
     */
    public LLSD parse(InputStream input, long maxBytes, int maxDepth) throws IOException, LLSDException {
        this.maxBytesLeft = maxBytes;
        this.checkLimits = (maxBytes != SIZE_UNLIMITED);
        
        try {
            Object content = doParse(input, maxDepth == -1 ? DEFAULT_MAX_DEPTH : maxDepth);
            return new LLSD(content);
        } catch (Exception e) {
            if (e instanceof LLSDException) {
                throw e;
            } else if (e instanceof IOException) {
                throw e;
            } else {
                throw new LLSDException("Parse error: " + e.getMessage(), e);
            }
        }
    }
    
    /**
     * Parses LLSD data from an {@link InputStream} with default (unlimited) limits.
     *
     * @param input The input stream to read from.
     * @return A parsed {@link LLSD} object.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if a parsing error occurs.
     */
    public LLSD parse(InputStream input) throws IOException, LLSDException {
        return parse(input, SIZE_UNLIMITED, -1);
    }
    
    /**
     * Parses LLSD data using line-based reading, which can be more robust for
     * certain formats like XML.
     *
     * @param input The input stream to read from.
     * @return A parsed {@link LLSD} object.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if a parsing error occurs.
     */
    public LLSD parseLines(InputStream input) throws IOException, LLSDException {
        this.parseLines = true;
        try {
            return parse(input);
        } finally {
            this.parseLines = false;
        }
    }
    
    /**
     * Resets the internal state of the parser, allowing it to be reused for
     * another parsing operation.
     * <p>
     * This method resets byte counters and any format-specific state by calling
     * the {@link #doReset()} hook.
     */
    public void reset() {
        doReset();
        maxBytesLeft = SIZE_UNLIMITED;
        checkLimits = true;
        parseLines = false;
    }
    
    /**
     * The abstract method that subclasses must implement to perform the actual
     * parsing for a specific format.
     *
     * @param input    The input stream to parse.
     * @param maxDepth The maximum allowed nesting depth.
     * @return The parsed LLSD content as a Java object (e.g., Map, List, etc.).
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if a format-specific parsing error occurs.
     */
    protected abstract Object doParse(InputStream input, int maxDepth) throws IOException, LLSDException;
    
    /**
     * A hook for subclasses to implement format-specific reset logic.
     * <p>
     * This method is called by {@link #reset()}. The default implementation is a no-op.
     */
    protected void doReset() {
        // Default: no-op
    }
    
    /**
     * Reads a single byte from the stream while enforcing the byte limit.
     *
     * @param input The input stream to read from.
     * @return The byte read as an integer (0-255), or -1 on EOF.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the byte limit is exceeded.
     */
    protected int getByte(InputStream input) throws IOException, LLSDException {
        if (checkLimits && maxBytesLeft <= 0) {
            throw new LLSDException("Byte limit exceeded during parsing");
        }
        
        int b = input.read();
        if (b != -1 && checkLimits) {
            maxBytesLeft--;
        }
        return b;
    }
    
    /**
     * Reads a block of bytes from the stream while enforcing the byte limit.
     *
     * @param input  The input stream to read from.
     * @param buffer The buffer to store the read data.
     * @param offset The start offset in the buffer.
     * @param length The maximum number of bytes to read.
     * @return The total number of bytes read into the buffer.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the byte limit is exceeded.
     */
    protected int readBytes(InputStream input, byte[] buffer, int offset, int length) throws IOException, LLSDException {
        if (checkLimits && maxBytesLeft <= 0) {
            throw new LLSDException("Byte limit exceeded during parsing");
        }
        
        if (checkLimits && length > maxBytesLeft) {
            length = (int) maxBytesLeft;
        }
        
        int bytesRead = input.read(buffer, offset, length);
        if (bytesRead > 0 && checkLimits) {
            maxBytesLeft -= bytesRead;
        }
        return bytesRead;
    }
    
    /**
     * Manually accounts for a number of bytes consumed, decrementing the
     * internal byte limit counter.
     *
     * @param bytes The number of bytes that have been consumed.
     * @throws LLSDException if this consumption exceeds the byte limit.
     */
    protected void accountBytes(long bytes) throws LLSDException {
        if (checkLimits) {
            maxBytesLeft -= bytes;
            if (maxBytesLeft < 0) {
                throw new LLSDException("Byte limit exceeded during parsing");
            }
        }
    }
    
    /**
     * Checks if the current parsing depth has exceeded the configured maximum.
     *
     * @param currentDepth The current nesting level of the parser.
     * @param maxDepth     The maximum allowed nesting level.
     * @throws LLSDException if {@code currentDepth} is greater than {@code maxDepth}.
     */
    protected void checkDepthLimit(int currentDepth, int maxDepth) throws LLSDException {
        if (maxDepth > 0 && currentDepth > maxDepth) {
            throw new LLSDException("Maximum parsing depth exceeded: " + currentDepth + " > " + maxDepth);
        }
    }
    
    /**
     * A utility method for reading a string that is prefixed with its 32-bit,
     * big-endian length. This is a common pattern in binary serialization.
     *
     * @param input The input stream to read from.
     * @return The string that was read.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the stream ends prematurely or the length is invalid.
     */
    protected String readLengthPrefixedString(InputStream input) throws IOException, LLSDException {
        // Read 4-byte length prefix (big-endian)
        byte[] lengthBytes = new byte[4];
        if (readBytes(input, lengthBytes, 0, 4) != 4) {
            throw new LLSDException("Unexpected EOF reading string length");
        }
        
        int length = ((lengthBytes[0] & 0xFF) << 24) |
                    ((lengthBytes[1] & 0xFF) << 16) |
                    ((lengthBytes[2] & 0xFF) << 8) |
                    (lengthBytes[3] & 0xFF);
        
        if (length < 0) {
            throw new LLSDException("Invalid string length: " + length);
        }
        
        if (length == 0) {
            return "";
        }
        
        byte[] stringBytes = new byte[length];
        if (readBytes(input, stringBytes, 0, length) != length) {
            throw new LLSDException("Unexpected EOF reading string data");
        }
        
        return new String(stringBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * A utility method to consume and discard whitespace characters from the stream.
     *
     * @param input The input stream.
     * @return The first non-whitespace character encountered, or -1 on EOF.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the byte limit is exceeded.
     */
    protected int skipWhitespace(InputStream input) throws IOException, LLSDException {
        int ch;
        do {
            ch = getByte(input);
        } while (ch != -1 && Character.isWhitespace(ch));
        return ch;
    }
    
    /**
     * A utility method to read characters from the stream until a specific
     * delimiter character is found.
     *
     * @param input     The input stream.
     * @param delimiter The character to stop reading at (this character is consumed
     *                  but not included in the returned string).
     * @return The string of characters read before the delimiter.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the byte limit is exceeded.
     */
    protected String readUntil(InputStream input, char delimiter) throws IOException, LLSDException {
        StringBuilder sb = new StringBuilder();
        int ch;
        while ((ch = getByte(input)) != -1 && ch != delimiter) {
            sb.append((char) ch);
        }
        return sb.toString();
    }
}

/**
 * An abstract utility class providing static methods for LLSD serialization and
 * deserialization, based on the C++ LLSDSerialize class from the Second Life viewer.
 * <p>
 * This class is designed to provide a high-level interface for handling different
 * LLSD formats, including auto-detection of the format from an input stream.
 * <p>
 * <b>Note:</b> The methods in this class are currently placeholders and are not
 * implemented. They are intended to define the future API for a unified
 * serialization framework.
 */
abstract class LLSDViewerSerializationUtils {
    
    /**
     * An enumeration of the LLSD serialization formats supported by the viewer.
     */
    public enum SerializationFormat {
        /** Binary LLSD format. */
        LLSD_BINARY,
        /** XML LLSD format. */
        LLSD_XML,
        /** Notation LLSD format. */
        LLSD_NOTATION,
        /** JSON LLSD format. */
        LLSD_JSON
    }
    
    /**
     * Serializes an LLSD object to an output stream using the specified format.
     * <p>
     * <b>Note: This method is not yet implemented.</b>
     *
     * @param llsd   The {@link LLSD} object to serialize.
     * @param output The stream to write the serialized data to.
     * @param format The desired serialization format.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if serialization fails for any reason.
     */
    public static void serialize(LLSD llsd, OutputStream output, SerializationFormat format)
            throws IOException, LLSDException {
        switch (format) {
            case LLSD_XML:
                serializeXML(llsd, output);
                break;
            case LLSD_BINARY:
                serializeBinary(llsd, output);
                break;
            case LLSD_NOTATION:
                serializeNotation(llsd, output);
                break;
            case LLSD_JSON:
                serializeJSON(llsd, output);
                break;
            default:
                throw new LLSDException("Unsupported serialization format: " + format);
        }
    }
    
    /**
     * Auto-detects the LLSD format from an input stream and deserializes it.
     * <p>
     * It peeks at the first few bytes of the stream to determine if the format is
     * XML, JSON, Notation, or Binary, and then delegates to the appropriate parser.
     * <p>
     * <b>Note: This method is not yet implemented.</b>
     *
     * @param input    The input stream containing the LLSD data.
     * @param maxBytes The maximum number of bytes to read for security.
     * @return A deserialized {@link LLSD} object.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the format cannot be detected or if deserialization fails.
     */
    public static LLSD deserialize(InputStream input, long maxBytes) throws IOException, LLSDException {
        // Use mark/reset to peek at the beginning
        if (!input.markSupported()) {
            input = new BufferedInputStream(input);
        }
        
        input.mark(16);
        byte[] header = new byte[16];
        int headerLen = input.read(header);
        input.reset();
        
        if (headerLen == 0) {
            throw new LLSDException("Empty input stream");
        }
        
        // Detect format from header
        String headerStr = new String(header, 0, Math.min(headerLen, 16), StandardCharsets.UTF_8);
        
        if (headerStr.startsWith("<?xml") || headerStr.startsWith("<llsd>")) {
            return deserializeXML(input, maxBytes);
        } else if (headerStr.startsWith("[") || headerStr.startsWith("{") || headerStr.startsWith("'") || 
                   headerStr.startsWith("\"") || Character.isDigit(headerStr.charAt(0))) {
            return deserializeNotation(input, maxBytes);
        } else if (headerStr.startsWith("{\"") || headerStr.startsWith("[{")) {
            return deserializeJSON(input, maxBytes);
        } else {
            // Assume binary format
            return deserializeBinary(input, maxBytes);
        }
    }
    
    private static void serializeXML(LLSD llsd, OutputStream output) throws IOException, LLSDException {
        // Use existing XML serializer (placeholder - would use actual implementation)
        throw new LLSDException("XML serialization not yet implemented in viewer utils");
    }
    
    private static void serializeBinary(LLSD llsd, OutputStream output) throws IOException, LLSDException {
        // Use existing binary serializer (placeholder - would use actual implementation)  
        throw new LLSDException("Binary serialization not yet implemented in viewer utils");
    }
    
    private static void serializeNotation(LLSD llsd, OutputStream output) throws IOException, LLSDException {
        // Use existing notation serializer (placeholder - would use actual implementation)
        throw new LLSDException("Notation serialization not yet implemented in viewer utils");
    }
    
    private static void serializeJSON(LLSD llsd, OutputStream output) throws IOException, LLSDException {
        // Use existing JSON serializer (placeholder - would use actual implementation)
        throw new LLSDException("JSON serialization not yet implemented in viewer utils");
    }
    
    private static LLSD deserializeXML(InputStream input, long maxBytes) throws IOException, LLSDException {
        // Use existing XML parser (placeholder - would use actual implementation)
        throw new LLSDException("XML deserialization not yet implemented in viewer utils");
    }
    
    private static LLSD deserializeBinary(InputStream input, long maxBytes) throws IOException, LLSDException {
        // Use existing binary parser (placeholder - would use actual implementation)
        throw new LLSDException("Binary deserialization not yet implemented in viewer utils");
    }
    
    private static LLSD deserializeNotation(InputStream input, long maxBytes) throws IOException, LLSDException {
        // Use existing notation parser (placeholder - would use actual implementation)
        throw new LLSDException("Notation deserialization not yet implemented in viewer utils");
    }
    
    private static LLSD deserializeJSON(InputStream input, long maxBytes) throws IOException, LLSDException {
        // Use existing JSON parser (placeholder - would use actual implementation)
        throw new LLSDException("JSON deserialization not yet implemented in viewer utils");
    }
}