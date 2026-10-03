/*
 * LLSDJ - LLSD in Java example
 *
 * Copyright(C) 2008 University of St. Andrews
 */

package lindenlab.llsd;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Represents a complete LLSD document, which is a container for a single LLSD element.
 * <p>
 * LLSD (Linden Lab Structured Data) is a serialization format used in Second Life
 * for various data interchange tasks. This class encapsulates a single LLSD value,
 * which can be of any type supported by the LLSD specification (e.g., integer,
 * string, map, array).
 * <p>
 * This class provides methods to:
 * <ul>
 *     <li>Hold any valid LLSD data type as its content.</li>
 *     <li>Serialize the contained data into LLSD XML format.</li>
 *     <li>Provide a string representation of the data in XML format.</li>
 * </ul>
 *
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD">LLSD Specification</a>
 * @see LLSDParser
 * @see LLSDJsonSerializer
 * @see LLSDBinarySerializer
 * @see LLSDNotationSerializer
 */
public class LLSD {
    private final Object content;
    private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Constructs a new LLSD document containing the given object.
     * <p>
     * The provided object should be a valid LLSD data type, such as:
     * <ul>
     *     <li>{@link java.lang.Boolean}</li>
     *     <li>{@link java.lang.Integer}</li>
     *     <li>{@link java.lang.Double}</li>
     *     <li>{@link java.lang.String}</li>
     *     <li>{@link java.util.Date}</li>
     *     <li>{@link java.net.URI}</li>
     *     <li>{@link java.util.UUID}</li>
     *     <li>{@link java.util.List} of LLSD objects</li>
     *     <li>{@link java.util.Map} with String keys and LLSD object values</li>
     *     <li>{@code byte[]} for binary data</li>
     *     <li>{@link LLSDUndefined} for an undefined value</li>
     * </ul>
     *
     * @param setContent The object to be wrapped in this LLSD document.
     */
    public  LLSD(final Object setContent) {
        this.content = setContent;
    }

    /**
     * Encodes a string for safe inclusion in an XML document.
     * <p>
     * This method replaces special XML characters like {@code <, >, &, "}
     * with their corresponding XML entities (e.g., {@code &lt;}, {@code &gt;}).
     *
     * @param text The string to be encoded.
     * @return An XML-safe string. Returns "null" if the input is null.
     */
    public    static        String        encodeXML(final String text) {
        final char[] encodeBuffer;
        int          encodeBufferSize;
        int          textLength;
        String       output;

        if (text == null) {
            return "null";
        }

        textLength = text.length();
        if (textLength == 0) {
            return text;
        }

        encodeBufferSize = textLength;
        for (int i = 0; i < textLength; i++) {
            switch (text.charAt(i)) {
            case '<':
            case '>':
                encodeBufferSize = encodeBufferSize + 3;
                break;

            case '&':
            case '-':
                encodeBufferSize = encodeBufferSize + 4;
                break;

            case '\"':
                encodeBufferSize = encodeBufferSize + 5;
                break;
                

            default:
                break;
            }
        }

        if (encodeBufferSize == textLength) {
            return text;
        }

        encodeBuffer = new char[encodeBufferSize];

        /**
         * The longest possibility from a single character is "&quot;"
         * This means that a multiplication of 6 times is enough to
         * hold the longest possible encoded string.
         */
        for (int i = 0, j = 0; i < textLength; i++, j++) {
            char currentChar = text.charAt(i);

            switch (currentChar) {
            case '<':
                encodeBuffer[j] = '&'; j++;
                encodeBuffer[j] = 'l'; j++;
                encodeBuffer[j] = 't'; j++;
                encodeBuffer[j] = ';';
                break;

            case '>':
                encodeBuffer[j] = '&'; j++;
                encodeBuffer[j] = 'g'; j++;
                encodeBuffer[j] = 't'; j++;
                encodeBuffer[j] = ';';
                break;

            case '&':
                encodeBuffer[j] = '&'; j++;
                encodeBuffer[j] = 'a'; j++;
                encodeBuffer[j] = 'm'; j++;
                encodeBuffer[j] = 'p'; j++;
                encodeBuffer[j] = ';';
                break;

            case '\"':
                encodeBuffer[j] = '&'; j++;
                encodeBuffer[j] = 'q'; j++;
                encodeBuffer[j] = 'u'; j++;
                encodeBuffer[j] = 'o'; j++;
                encodeBuffer[j] = 't'; j++;
                encodeBuffer[j] = ';';
                break;

            case '-':
                encodeBuffer[j] = '&'; j++;
                encodeBuffer[j] = '#'; j++;
                encodeBuffer[j] = '4'; j++;
                encodeBuffer[j] = '5'; j++;
                encodeBuffer[j] = ';';
                break;

            default:
                encodeBuffer[j] = currentChar;
            }
        }

        output    = new String(encodeBuffer);

        return output;
    }

    /**
     * Retrieves the root object contained within this LLSD document.
     * <p>
     * The returned object will be one of the supported LLSD data types.
     * It is the responsibility of the caller to cast the object to the
     * expected type.
     *
     * @return The raw object held by this LLSD document.
     */
    public  Object  getContent() {
        return this.content;
    }

    /**
     * Serializes the contained LLSD data into its XML representation and writes it
     * to the provided {@link Writer}.
     *
     * @param writer  The {@link Writer} to which the XML data will be written.
     * @param charset The character encoding to declare in the XML prolog (e.g., "UTF-8").
     * @throws IOException   if an I/O error occurs while writing to the writer.
     * @throws LLSDException if the content contains an unserializable data type.
     */
    public  void    serialise(final Writer writer, final String charset)
        throws IOException, LLSDException {
        writer.write("<?xml version=\"1.0\" encoding=\""
            + charset + "\"?>\n");
        writer.write("<llsd>\n");
        if (null != content) {
            serialiseElement(writer, content);
        }
        writer.write("</llsd>\n");
    }

    /**
     * Recursively serializes a single LLSD element to the writer.
     * <p>
     * This method is called by {@link #serialise(Writer, String)} to handle the
     * serialization of the document's content. It determines the type of the
     * object and writes the corresponding LLSD XML tag and value.
     *
     * @param writer      The writer to output the XML to.
     * @param toSerialise The object to be serialized.
     * @throws IOException   if an I/O error occurs.
     * @throws LLSDException if the object's type is not a valid LLSD type.
     */
    private void serialiseElement(final Writer writer, final Object toSerialise)
        throws IOException, LLSDException {
        // Create thread-safe local formatter instances
        DateFormat dateFormat = new SimpleDateFormat(ISO8601_PATTERN);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DecimalFormat decimalFormat = new DecimalFormat("#0.0#");

        assert null != toSerialise;

        if (toSerialise instanceof Map) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> serialiseMap = (Map<String, Object>)toSerialise;

            writer.write("<map>\n");
            for (String key: serialiseMap.keySet()) {
                final Object value = serialiseMap.get(key);
                writer.write("\t<key>"
                    + encodeXML(key) + "</key>\n\t");
                serialiseElement(writer, value);
            }
            writer.write("</map>\n");
        } else if (toSerialise instanceof List) {
            @SuppressWarnings("unchecked")
            final List<Object> serialiseList = (List<Object>)toSerialise;
            writer.write("<array>\n");
            for (Object current: serialiseList) {
                writer.write("\t");
                serialiseElement(writer, current);
            }
            writer.write("</array>\n");
        } else if (toSerialise instanceof Boolean) {
            writer.write("<boolean>"
                + toSerialise.toString() + "</boolean>\n");
        } else if (toSerialise instanceof Integer) {
            writer.write("<integer>"
                + toSerialise.toString() + "</integer>\n");
        } else if (toSerialise instanceof Double) {
            if (toSerialise.equals(Double.NaN)) {
                writer.write("<real>nan</real>\n");
            } else {
                writer.write("<real>"
                    + decimalFormat.format(toSerialise) + "</real>\n");
            }
        } else if (toSerialise instanceof Float) {
            if (toSerialise.equals(Float.NaN)) {
                writer.write("<real>nan</real>\n");
            } else {
                writer.write("<real>"
                    + decimalFormat.format(toSerialise) + "</real>\n");
            }
        } else if (toSerialise instanceof UUID) {
            writer.write("<uuid>"
                + toSerialise.toString() + "</uuid>\n");
        } else if (toSerialise instanceof String) {
            writer.write("<string>"
                + encodeXML((String)toSerialise) + "</string>\n");
        } else if (toSerialise instanceof Date) {
            writer.write("<date>"
                + dateFormat.format((Date)toSerialise) + "</date>");
        } else if (toSerialise instanceof URI) {
            writer.write("<uri>"
                + encodeXML(toSerialise.toString()) + "</uri>");
        } else if (toSerialise instanceof byte[]) {
            // Handle binary data as base64 encoded
            byte[] binaryData = (byte[]) toSerialise;
            String base64Data = Base64.getEncoder().encodeToString(binaryData);
            writer.write("<binary>");
            writer.write(base64Data);
            writer.write("</binary>\n");
        } else if (toSerialise instanceof LLSDUndefined) {
            switch((LLSDUndefined)toSerialise) {
            case BINARY:
                writer.write("<binary><undef /></binary>\n");
                break;
            case BOOLEAN:
                writer.write("<boolean><undef /></boolean>\n");
                break;
            case DATE:
                writer.write("<date><undef /></date>\n");
                break;
            case INTEGER:
                writer.write("<integer><undef /></integer>\n");
                break;
            case REAL:
                writer.write("<real><undef /></real>\n");
                break;
            case STRING:
                writer.write("<string><undef /></string>\n");
                break;
            case URI:
                writer.write("<uri><undef /></uri>\n");
                break;
            case UUID:
                writer.write("<uuid><undef /></uuid>\n");
                break;
            }
        } else {
            throw new LLSDException("Unable to serialise type \""
                + toSerialise.getClass().getName() + "\".");
        }

    }

    /**
     * Returns the string representation of this LLSD document in XML format.
     * <p>
     * This method is a convenience for debugging and logging. It serializes the
     * content to an LLSD XML string. If serialization fails, it returns an
     * error message instead of throwing an exception.
     *
     * @return A string containing the LLSD data in XML format, or an error
     *         message if serialization fails.
     */
    @Override
    public String toString() {
        final StringWriter writer = new StringWriter();

        try {
            serialise(writer, "UTF-8");
        } catch(IOException | LLSDException e) {
            return "Unable to serialise LLSD for display: " + e.getMessage();
        }

        return writer.toString();
    }
}
