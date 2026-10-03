/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * A parser for LLSD (Linden Lab Structured Data) in its notation format.
 * <p>
 * This class is responsible for converting a text-based LLSD notation document
 * from an {@link InputStream} into a standard Java object structure, wrapped in
 * an {@link LLSD} object. The notation format is a compact, human-readable
 * representation that resembles programming language literals.
 * <p>
 * The format uses single-character prefixes to denote types:
 * <ul>
 *   <li><b>{@code !}</b> - Undefined</li>
 *   <li><b>{@code 1}</b> or <b>{@code true}</b> - Boolean true</li>
 *   <li><b>{@code 0}</b> or <b>{@code false}</b> - Boolean false</li>
 *   <li><b>{@code i}</b> - Integer (e.g., {@code i42})</li>
 *   <li><b>{@code r}</b> - Real/Double (e.g., {@code r3.14})</li>
 *   <li><b>{@code s}</b> - String (e.g., {@code s'hello'} or {@code s"hello"})</li>
 *   <li><b>{@code u}</b> - UUID (e.g., {@code u...})</li>
 *   <li><b>{@code d}</b> - Date (e.g., {@code d2024-01-01T00:00:00Z})</li>
 *   <li><b>{@code l}</b> - URI (e.g., {@code lhttp://example.com})</li>
 *   <li><b>{@code b}</b> - Binary (e.g., {@code b64"SGVsbG8="})</li>
 *   <li><b>{@code [...]}</b> - Array</li>
 *   <li><b>{@code {...}}</b> - Map</li>
 * </ul>
 * This parser includes a self-contained tokenizer and a recursive-descent parser
 * to interpret this format.
 *
 * @see LLSD
 * @see LLSDNotationSerializer
 * @see <a href="http://wiki.secondlife.com/wiki/LLSD#Notation_Serialization">LLSD Notation Specification</a>
 */
public class LLSDNotationParser {
    private static final String ISO8601_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Initializes a new instance of the {@code LLSDNotationParser}.
     */
    public LLSDNotationParser() {
        // No initialization needed - date formatters are created locally for thread safety
    }

    /**
     * Parses an LLSD document from a notation-formatted input stream.
     * <p>
     * This is the main entry point for parsing. It reads the entire stream,
     * tokenizes the content, and recursively parses the notation structure.
     *
     * @param notationInput The input stream containing the notation-formatted data.
     * @return An {@link LLSD} object representing the parsed data.
     * @throws IOException   if an I/O error occurs while reading from the stream.
     * @throws LLSDException if the input is not valid LLSD notation (e.g., syntax
     *                       error, invalid value format).
     */
    public LLSD parse(final InputStream notationInput) throws IOException, LLSDException {
        // Read entire stream into string for simpler parsing
        StringBuilder sb = new StringBuilder();
        try (Reader reader = new InputStreamReader(notationInput, StandardCharsets.UTF_8)) {
            char[] buffer = new char[1024];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, charsRead);
            }
        }
        
        String notationString = sb.toString().trim();
        NotationTokenizer tokenizer = new NotationTokenizer(notationString);
        Object parsedNotation = parseNotationValue(tokenizer);
        return new LLSD(parsedNotation);
    }

    /**
     * A simple, internal tokenizer for breaking LLSD notation into a sequence of tokens.
     * <p>
     * This class is designed to handle the specific syntax of LLSD notation,
     * including type markers, delimited strings, and structural characters.
     */
    private static class NotationTokenizer {
        private final String notation;
        private int position = 0;

        public NotationTokenizer(String notation) {
            this.notation = notation;
        }

        public void skipWhitespace() {
            while (position < notation.length() && Character.isWhitespace(notation.charAt(position))) {
                position++;
            }
        }

        public char peek() throws LLSDException {
            skipWhitespace();
            if (position >= notation.length()) {
                throw new LLSDException("Unexpected end of notation input");
            }
            return notation.charAt(position);
        }

        public char consume() throws LLSDException {
            skipWhitespace();
            if (position >= notation.length()) {
                throw new LLSDException("Unexpected end of notation input");
            }
            return notation.charAt(position++);
        }

        public void expect(char expected) throws LLSDException {
            char actual = consume();
            if (actual != expected) {
                throw new LLSDException("Expected '" + expected + "' but got '" + actual + "'");
            }
        }

        public boolean hasMore() {
            skipWhitespace();
            return position < notation.length();
        }

        public String consumeString(char delimiter) throws LLSDException {
            // Note: delimiter should already be consumed by caller
            StringBuilder sb = new StringBuilder();
            
            while (position < notation.length()) {
                char c = notation.charAt(position++);
                if (c == delimiter) {
                    return sb.toString();
                } else if (c == '\\') {
                    if (position >= notation.length()) {
                        throw new LLSDException("Unterminated string escape");
                    }
                    c = notation.charAt(position++);
                    switch (c) {
                        case '"':
                        case '\'':
                        case '\\':
                            sb.append(c);
                            break;
                        case 'n':
                            sb.append('\n');
                            break;
                        case 'r':
                            sb.append('\r');
                            break;
                        case 't':
                            sb.append('\t');
                            break;
                        default:
                            sb.append('\\').append(c); // Keep original if not recognized
                            break;
                    }
                } else {
                    sb.append(c);
                }
            }
            
            throw new LLSDException("Unterminated string");
        }

        public Object consumeNumber(char typeMarker) throws LLSDException {
            StringBuilder sb = new StringBuilder();
            boolean hasDecimal = false;
            
            while (position < notation.length()) {
                char c = notation.charAt(position);
                if (Character.isDigit(c) || c == '-' || c == '+') {
                    sb.append(c);
                    position++;
                } else if (c == '.' && !hasDecimal && typeMarker == 'r') {
                    hasDecimal = true;
                    sb.append(c);
                    position++;
                } else if (c == 'e' || c == 'E') {
                    sb.append(c);
                    position++;
                } else {
                    break;
                }
            }

            String numStr = sb.toString();
            try {
                if (typeMarker == 'i') {
                    return Integer.valueOf(numStr);
                } else if (typeMarker == 'r') {
                    return Double.valueOf(numStr);
                } else {
                    throw new LLSDException("Unknown number type marker: " + typeMarker);
                }
            } catch (NumberFormatException e) {
                throw new LLSDException("Invalid number format: " + numStr, e);
            }
        }

        public String consumeUntil(char... delimiters) {
            StringBuilder sb = new StringBuilder();
            Set<Character> delimiterSet = new HashSet<>();
            for (char delimiter : delimiters) {
                delimiterSet.add(delimiter);
            }
            
            while (position < notation.length()) {
                char c = notation.charAt(position);
                if (delimiterSet.contains(c) || Character.isWhitespace(c)) {
                    break;
                }
                sb.append(c);
                position++;
            }
            
            return sb.toString();
        }
    }

    /**
     * Parses a single LLSD value from the token stream based on its type marker.
     * <p>
     * This is the core of the recursive descent parser. It inspects the next
     * character to determine which type of value to parse and delegates to the
     * appropriate helper method.
     *
     * @param tokenizer The tokenizer providing the notation tokens.
     * @return A Java object representing the parsed value.
     * @throws LLSDException if an unknown type marker or syntax error is found.
     */
    private Object parseNotationValue(NotationTokenizer tokenizer) throws LLSDException {
        if (!tokenizer.hasMore()) {
            throw new LLSDException("Expected value but found end of input");
        }

        char ch = tokenizer.peek();

        switch (ch) {
            case '!':
                tokenizer.consume(); // consume '!'
                return ""; // Undefined value represented as empty string
            case '1':
            case 't':
            case 'T':
                return parseBoolean(tokenizer, true);
            case '0':
            case 'f':
            case 'F':
                return parseBoolean(tokenizer, false);
            case 'i':
                tokenizer.consume(); // consume 'i'
                return tokenizer.consumeNumber('i');
            case 'r':
                tokenizer.consume(); // consume 'r'
                return tokenizer.consumeNumber('r');
            case 's':
                return parseString(tokenizer);
            case 'u':
                return parseUUID(tokenizer);
            case 'd':
                return parseDate(tokenizer);
            case 'l':
                return parseURI(tokenizer);
            case 'b':
                return parseBinary(tokenizer);
            case '[':
                return parseArray(tokenizer);
            case '{':
                return parseMap(tokenizer);
            default:
                throw new LLSDException("Unexpected character in notation: " + ch);
        }
    }

    private Boolean parseBoolean(NotationTokenizer tokenizer, boolean expectedValue) throws LLSDException {
        char ch = tokenizer.consume();
        
        if (ch == '1' && expectedValue) {
            return Boolean.TRUE;
        } else if (ch == '0' && !expectedValue) {
            return Boolean.FALSE;
        } else if (ch == 't' || ch == 'T') {
            // Check for full word
            String remaining = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r');
            String fullWord = ch + remaining;
            if (fullWord.equalsIgnoreCase("true") || fullWord.equalsIgnoreCase("t")) {
                return Boolean.TRUE;
            } else {
                throw new LLSDException("Invalid boolean value: " + fullWord);
            }
        } else if (ch == 'f' || ch == 'F') {
            // Check for full word
            String remaining = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r');
            String fullWord = ch + remaining;
            if (fullWord.equalsIgnoreCase("false") || fullWord.equalsIgnoreCase("f")) {
                return Boolean.FALSE;
            } else {
                throw new LLSDException("Invalid boolean value: " + fullWord);
            }
        }
        
        throw new LLSDException("Invalid boolean notation: " + ch);
    }

    private String parseString(NotationTokenizer tokenizer) throws LLSDException {
        tokenizer.expect('s'); // consume 's'
        char delimiter = tokenizer.peek();
        if (delimiter == '\'' || delimiter == '"') {
            tokenizer.consume(); // consume the delimiter
            return tokenizer.consumeString(delimiter);
        } else {
            throw new LLSDException("Expected string delimiter (' or \") after 's' but got: " + delimiter);
        }
    }

    private UUID parseUUID(NotationTokenizer tokenizer) throws LLSDException {
        tokenizer.expect('u'); // consume 'u'
        String uuidStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r');
        
        if (!LLSDUtils.UUID_PATTERN.matcher(uuidStr).matches()) {
            throw new LLSDException("Invalid UUID format: '" + uuidStr + "'. Expected format: 'xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx'");
        }
        
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new LLSDException("Invalid UUID: " + uuidStr, e);
        }
    }

    private Date parseDate(NotationTokenizer tokenizer) throws LLSDException {
        tokenizer.expect('d'); // consume 'd'
        String dateStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r');
        
        try {
            // Create a new DateFormat instance for thread safety
            DateFormat dateFormat = new SimpleDateFormat(ISO8601_PATTERN);
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            throw new LLSDException("Invalid date format: " + dateStr, e);
        }
    }

    private URI parseURI(NotationTokenizer tokenizer) throws LLSDException {
        tokenizer.expect('l'); // consume 'l'
        String uriStr = tokenizer.consumeUntil(',', ']', '}', ' ', '\t', '\n', '\r');
        
        try {
            return new URI(uriStr);
        } catch (URISyntaxException e) {
            throw new LLSDException("Invalid URI format: " + uriStr, e);
        }
    }

    private byte[] parseBinary(NotationTokenizer tokenizer) throws LLSDException {
        tokenizer.expect('b'); // consume 'b'
        
        // Check for size specification b64"data" or b(size)"data"
        char next = tokenizer.peek();
        if (Character.isDigit(next)) {
            // Handle b64"data" format
            String sizeStr = tokenizer.consumeUntil('"', '\'');
            if (sizeStr.equals("64")) {
                char delimiter = tokenizer.consume(); // consume quote
                if (delimiter == '"' || delimiter == '\'') {
                    String base64Data = tokenizer.consumeString(delimiter);
                    try {
                        return Base64.getDecoder().decode(base64Data);
                    } catch (IllegalArgumentException e) {
                        throw new LLSDException("Invalid base64 data: " + base64Data, e);
                    }
                } else {
                    throw new LLSDException("Expected quote delimiter after b64 but got: " + delimiter);
                }
            } else {
                throw new LLSDException("Unsupported binary size specification: " + sizeStr);
            }
        } else if (next == '(') {
            // Handle b(size)"data" format  
            tokenizer.consume(); // consume '('
            String sizeStr = tokenizer.consumeUntil(')');
            tokenizer.expect(')');
            int size = Integer.parseInt(sizeStr);
            
            char delimiter = tokenizer.consume(); // should be quote
            if (delimiter == '"' || delimiter == '\'') {
                String binaryData = tokenizer.consumeString(delimiter);
                
                if (size != binaryData.length()) {
                    throw new LLSDException("Binary size mismatch: expected " + size + " but got " + binaryData.length());
                }
                
                return binaryData.getBytes(StandardCharsets.UTF_8);
            } else {
                throw new LLSDException("Expected quote delimiter after size specification but got: " + delimiter);
            }
        } else {
            throw new LLSDException("Invalid binary notation format - expected digit or '(' after 'b' but got: " + next);
        }
    }

    private List<Object> parseArray(NotationTokenizer tokenizer) throws LLSDException {
        List<Object> list = new ArrayList<>();
        tokenizer.expect('[');

        if (tokenizer.peek() == ']') {
            tokenizer.consume(); // consume ']'
            return list; // Empty array
        }

        while (true) {
            Object value = parseNotationValue(tokenizer);
            list.add(value);

            char next = tokenizer.peek();
            if (next == ']') {
                tokenizer.consume();
                break;
            } else if (next == ',') {
                tokenizer.consume();
                continue;
            } else {
                throw new LLSDException("Expected ',' or ']' in array, got: " + next);
            }
        }

        return list;
    }

    private Map<String, Object> parseMap(NotationTokenizer tokenizer) throws LLSDException {
        Map<String, Object> map = new HashMap<>();
        tokenizer.expect('{');

        if (tokenizer.peek() == '}') {
            tokenizer.consume(); // consume '}'
            return map; // Empty map
        }

        while (true) {
            // Parse key (should be an identifier or string)
            String key;
            char keyStart = tokenizer.peek();

            if (keyStart == 's') {
                // This could be a string literal `s'...'` or an identifier like `status`.
                // We can try to parse it as a string and if that fails, treat it as an identifier.
                int savedPos = tokenizer.position;
                try {
                    key = parseString(tokenizer);
                } catch (LLSDException e) {
                    // It's not a string literal, so it must be an identifier.
                    tokenizer.position = savedPos; // backtrack
                    key = tokenizer.consumeUntil(':', ' ', '\t', '\n', '\r');
                }
            } else if (Character.isLetter(keyStart) || keyStart == '_') {
                // Regular identifier key
                key = tokenizer.consumeUntil(':', ' ', '\t', '\n', '\r');
            } else {
                throw new LLSDException("Invalid map key format, got: " + keyStart);
            }

            tokenizer.expect(':');
            Object value = parseNotationValue(tokenizer);
            map.put(key, value);

            char next = tokenizer.peek();
            if (next == '}') {
                tokenizer.consume();
                break;
            } else if (next == ',') {
                tokenizer.consume();
                continue;
            } else {
                throw new LLSDException("Expected ',' or '}' in map, got: " + next);
            }
        }

        return map;
    }
}