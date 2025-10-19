package lindenlab.llsd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@DisplayName("LLSD Notation Format Tests")
class LLSDNotationTest {

    @Nested
    @DisplayName("Notation Parser Tests")
    class NotationParserTests {

        @Test
        @DisplayName("Should parse simple notation values")
        void testParseSimpleValues() throws Exception {
            assertNotationEquals("!", "");
            assertNotationEquals("1", true);
            assertNotationEquals("0", false);
            assertNotationEquals("TRUE", true);
            assertNotationEquals("false", false);
            assertNotationEquals("i42", 42);
            assertNotationEquals("r3.14159", 3.14159);
            assertNotationEquals("s'hello world'", "hello world");
            assertNotationEquals("s\"quoted string\"", "quoted string");
        }

        @Test
        @DisplayName("Should parse single-character boolean notation")
        void testParseSingleCharBoolean() throws Exception {
            assertNotationEquals("t", true);
            assertNotationEquals("f", false);
        }

        @Test
        @DisplayName("Should parse UUID notation")
        void testParseUUID() throws Exception {
            String uuidStr = "550e8400-e29b-41d4-a716-446655440000";
            String notation = "u" + uuidStr;
            
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof UUID);
                assertEquals(uuidStr, result.getContent().toString());
            }
        }

        @Test
        @DisplayName("Should parse date notation")
        void testParseDate() throws Exception {
            String notation = "d2024-01-01T00:00:00Z";
            
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof Date);
                assertNotNull(result.getContent());
            }
        }

        @Test
        @DisplayName("Should parse binary notation")
        void testParseBinary() throws Exception {
            String testData = "Hello World";
            String base64 = Base64.getEncoder().encodeToString(testData.getBytes(StandardCharsets.UTF_8));
            String notation = "b64\"" + base64 + "\"";
            
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof byte[]);
                byte[] resultData = (byte[]) result.getContent();
                assertEquals(testData, new String(resultData, StandardCharsets.UTF_8));
            }
        }

        @Test
        @DisplayName("Should parse array notation")
        void testParseArray() throws Exception {
            String notation = "[i1,i2,s'three']";
            
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof List);
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) result.getContent();
                
                assertEquals(3, list.size());
                assertEquals(1, list.get(0));
                assertEquals(2, list.get(1));
                assertEquals("three", list.get(2));
            }
        }

        @Test
        @DisplayName("Should parse map notation")
        void testParseMap() throws Exception {
            String notation = "{name:s'John',age:i30,active:1}";
            
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof Map);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result.getContent();
                
                assertEquals("John", map.get("name"));
                assertEquals(30, map.get("age"));
                assertEquals(true, map.get("active"));
            }
        }

        @Test
        @DisplayName("Should parse map with quoted string key")
        void testParseMapWithQuotedKey() throws Exception {
            String notation = "{s'quoted-key':i123}";

            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);

                assertTrue(result.getContent() instanceof Map);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result.getContent();

                assertTrue(map.containsKey("quoted-key"));
                assertEquals(123, map.get("quoted-key"));
            }
        }

        @Test
        @DisplayName("Should parse nested structures")
        void testParseNested() throws Exception {
            String notation = "{users:[{name:s'Alice',age:i25},{name:s'Bob',age:i30}]}";
            
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof Map);
                @SuppressWarnings("unchecked")
                Map<String, Object> rootMap = (Map<String, Object>) result.getContent();
                
                assertTrue(rootMap.containsKey("users"));
                @SuppressWarnings("unchecked")
                List<Object> users = (List<Object>) rootMap.get("users");
                
                assertEquals(2, users.size());
                
                @SuppressWarnings("unchecked")
                Map<String, Object> alice = (Map<String, Object>) users.get(0);
                assertEquals("Alice", alice.get("name"));
                assertEquals(25, alice.get("age"));
            }
        }

        private void assertNotationEquals(String notation, Object expected) throws Exception {
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                LLSDNotationParser parser = new LLSDNotationParser();
                LLSD result = parser.parse(input);
                assertEquals(expected, result.getContent());
            }
        }
    }

    @Nested
    @DisplayName("Notation Serializer Tests")
    class NotationSerializerTests {

        @Test
        @DisplayName("Should serialize simple values to notation")
        void testSerializeSimpleValues() throws Exception {
            assertSerializesTo("", "!");
            assertSerializesTo(true, "1");
            assertSerializesTo(false, "0");
            assertSerializesTo(42, "i42");
            assertSerializesTo(3.14159, "r3.14159");
            assertSerializesTo("hello world", "s'hello world'");
        }

        @Test
        @DisplayName("Should serialize UUID to notation")
        void testSerializeUUID() throws Exception {
            UUID testUUID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            LLSD llsd = new LLSD(testUUID);
            LLSDNotationSerializer serializer = new LLSDNotationSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String notation = writer.toString();
                
                assertEquals("u550e8400-e29b-41d4-a716-446655440000", notation);
            }
        }

        @Test
        @DisplayName("Should serialize binary data to notation")
        void testSerializeBinary() throws Exception {
            byte[] binaryData = "Hello World".getBytes(StandardCharsets.UTF_8);
            LLSD llsd = new LLSD(binaryData);
            LLSDNotationSerializer serializer = new LLSDNotationSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String notation = writer.toString();
                
                String expectedBase64 = Base64.getEncoder().encodeToString(binaryData);
                assertEquals("b64\"" + expectedBase64 + "\"", notation);
            }
        }

        @Test
        @DisplayName("Should serialize arrays to notation")
        void testSerializeArray() throws Exception {
            List<Object> array = Arrays.asList(1, 2, "three", true);
            LLSD llsd = new LLSD(array);
            LLSDNotationSerializer serializer = new LLSDNotationSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String notation = writer.toString();
                
                assertEquals("[i1,i2,s'three',1]", notation);
            }
        }

        @Test
        @DisplayName("Should serialize maps to notation")
        void testSerializeMap() throws Exception {
            Map<String, Object> map = new LinkedHashMap<>(); // Use LinkedHashMap for predictable order
            map.put("name", "John");
            map.put("age", 30);
            map.put("active", true);
            
            LLSD llsd = new LLSD(map);
            LLSDNotationSerializer serializer = new LLSDNotationSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String notation = writer.toString();
                
                // Should use unquoted keys for valid identifiers
                assertEquals("{name:s'John',age:i30,active:1}", notation);
            }
        }

        @Test
        @DisplayName("Should handle special characters in strings")
        void testSerializeStringEscaping() throws Exception {
            String testString = "Hello 'world' with\nnewlines and\ttabs";
            LLSD llsd = new LLSD(testString);
            LLSDNotationSerializer serializer = new LLSDNotationSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String notation = writer.toString();
                
                assertTrue(notation.startsWith("s'"));
                assertTrue(notation.endsWith("'"));
                assertTrue(notation.contains("\\'world\\'")); // Escaped quotes
                assertTrue(notation.contains("\\n")); // Escaped newlines
                assertTrue(notation.contains("\\t")); // Escaped tabs
            }
        }

        private void assertSerializesTo(Object value, String expectedNotation) throws Exception {
            LLSD llsd = new LLSD(value);
            LLSDNotationSerializer serializer = new LLSDNotationSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                assertEquals(expectedNotation, writer.toString());
            }
        }
    }

    @Nested
    @DisplayName("Notation Round-trip Tests")
    class NotationRoundTripTests {

        @Test
        @DisplayName("Should round-trip complex structures")
        void testComplexRoundTrip() throws Exception {
            Map<String, Object> originalData = new HashMap<>();
            originalData.put("name", "Test User");
            originalData.put("age", 25);
            originalData.put("active", true);
            originalData.put("score", 98.7);
            
            List<Object> hobbies = Arrays.asList("reading", "coding", "gaming");
            originalData.put("hobbies", hobbies);
            
            Map<String, Object> address = new HashMap<>();
            address.put("street", "123 Main St");
            address.put("city", "Anytown");
            originalData.put("address", address);
            
            // Serialize to notation
            LLSD originalLlsd = new LLSD(originalData);
            LLSDNotationSerializer serializer = new LLSDNotationSerializer();
            String notation;
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(originalLlsd, writer);
                notation = writer.toString();
            }
            
            // Parse back from notation
            LLSDNotationParser parser = new LLSDNotationParser();
            LLSD parsedLlsd;
            try (InputStream input = new ByteArrayInputStream(notation.getBytes(StandardCharsets.UTF_8))) {
                parsedLlsd = parser.parse(input);
            }
            
            // Verify the data matches
            assertTrue(parsedLlsd.getContent() instanceof Map);
            @SuppressWarnings("unchecked")
            Map<String, Object> parsedData = (Map<String, Object>) parsedLlsd.getContent();
            
            assertEquals("Test User", parsedData.get("name"));
            assertEquals(25, parsedData.get("age"));
            assertEquals(true, parsedData.get("active"));
            assertEquals(98.7, parsedData.get("score"));
            
            assertTrue(parsedData.get("hobbies") instanceof List);
            assertTrue(parsedData.get("address") instanceof Map);
        }
    }
}