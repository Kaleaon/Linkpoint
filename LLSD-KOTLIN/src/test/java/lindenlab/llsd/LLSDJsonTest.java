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
import java.util.Arrays;
import java.util.Base64;

@DisplayName("LLSD JSON Parser and Serializer Tests")
class LLSDJsonTest {

    @Nested
    @DisplayName("JSON Parser Tests")
    class JsonParserTests {

        @Test
        @DisplayName("Should parse simple JSON object")
        void testParseSimpleObject() throws Exception {
            String json = "{\"name\":\"test\",\"value\":42}";
            try (InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                LLSDJsonParser parser = new LLSDJsonParser();
                LLSD result = parser.parse(input);
                
                assertNotNull(result.getContent());
                // Additional validation would go here
            }
        }

        @Test
        @DisplayName("Should parse JSON array")
        void testParseArray() throws Exception {
            String json = "[1, 2, \"three\", true]";
            try (InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                LLSDJsonParser parser = new LLSDJsonParser();
                LLSD result = parser.parse(input);
                
                assertNotNull(result.getContent());
                assertTrue(result.getContent() instanceof java.util.List);
            }
        }

        @Test
        @DisplayName("Should parse LLSD date format")
        void testParseLlsdDate() throws Exception {
            String json = "{\"d\":\"2024-01-01T00:00:00Z\"}";
            try (InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                LLSDJsonParser parser = new LLSDJsonParser();
                LLSD result = parser.parse(input);
                
                assertNotNull(result.getContent());
                assertTrue(result.getContent() instanceof java.util.Date);
            }
        }

        @Test
        @DisplayName("Should parse LLSD UUID format")
        void testParseLlsdUuid() throws Exception {
            String json = "{\"i\":\"550e8400-e29b-41d4-a716-446655440000\"}";
            try (InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                LLSDJsonParser parser = new LLSDJsonParser();
                LLSD result = parser.parse(input);
                
                assertNotNull(result.getContent());
                assertTrue(result.getContent() instanceof java.util.UUID);
                assertEquals("550e8400-e29b-41d4-a716-446655440000", result.getContent().toString());
            }
        }

        @Test
        @DisplayName("Should parse LLSD binary format")
        void testParseLlsdBinary() throws Exception {
            String testData = "Hello World";
            String base64 = Base64.getEncoder().encodeToString(testData.getBytes(StandardCharsets.UTF_8));
            String json = "{\"b\":\"" + base64 + "\"}";
            
            try (InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                LLSDJsonParser parser = new LLSDJsonParser();
                LLSD result = parser.parse(input);
                
                assertNotNull(result.getContent());
                assertTrue(result.getContent() instanceof byte[]);
                
                byte[] resultData = (byte[]) result.getContent();
                assertEquals(testData, new String(resultData, StandardCharsets.UTF_8));
            }
        }

        @Test
        @DisplayName("Should handle malformed JSON")
        void testMalformedJson() {
            String malformedJson = "{\"test\": }";
            try (InputStream input = new ByteArrayInputStream(malformedJson.getBytes(StandardCharsets.UTF_8))) {
                LLSDJsonParser parser = new LLSDJsonParser();
                assertThrows(LLSDException.class, () -> parser.parse(input));
            } catch (IOException e) {
                fail("Should not throw IOException in test setup");
            }
        }
    }

    @Nested
    @DisplayName("JSON Serializer Tests")
    class JsonSerializerTests {

        @Test
        @DisplayName("Should serialize simple map to JSON")
        void testSerializeSimpleMap() throws Exception {
            java.util.Map<String, Object> data = new java.util.HashMap<>();
            data.put("name", "test");
            data.put("value", 42);
            
            LLSD llsd = new LLSD(data);
            LLSDJsonSerializer serializer = new LLSDJsonSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String json = writer.toString();
                
                assertNotNull(json);
                assertTrue(json.contains("\"name\""));
                assertTrue(json.contains("\"test\""));
                assertTrue(json.contains("\"value\""));
                assertTrue(json.contains("42"));
            }
        }

        @Test
        @DisplayName("Should serialize array to JSON")
        void testSerializeArray() throws Exception {
            java.util.List<Object> data = java.util.Arrays.asList(1, 2, "three", true);
            
            LLSD llsd = new LLSD(data);
            LLSDJsonSerializer serializer = new LLSDJsonSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String json = writer.toString();
                
                assertNotNull(json);
                assertTrue(json.startsWith("["));
                assertTrue(json.endsWith("]"));
                assertTrue(json.contains("\"three\""));
                assertTrue(json.contains("true"));
            }
        }

        @Test
        @DisplayName("Should serialize date in LLSD format")
        void testSerializeDate() throws Exception {
            java.util.Date date = new java.util.Date(0); // Unix epoch
            
            LLSD llsd = new LLSD(date);
            LLSDJsonSerializer serializer = new LLSDJsonSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String json = writer.toString();
                
                assertNotNull(json);
                assertTrue(json.contains("\"d\""));
                assertTrue(json.contains("1970-01-01T00:00:00Z"));
            }
        }

        @Test
        @DisplayName("Should serialize UUID in LLSD format")
        void testSerializeUuid() throws Exception {
            java.util.UUID uuid = java.util.UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            
            LLSD llsd = new LLSD(uuid);
            LLSDJsonSerializer serializer = new LLSDJsonSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String json = writer.toString();
                
                assertNotNull(json);
                assertTrue(json.contains("\"i\""));
                assertTrue(json.contains("550e8400-e29b-41d4-a716-446655440000"));
            }
        }

        @Test
        @DisplayName("Should serialize binary data in LLSD format")
        void testSerializeBinary() throws Exception {
            byte[] binaryData = "Hello World".getBytes(StandardCharsets.UTF_8);
            
            LLSD llsd = new LLSD(binaryData);
            LLSDJsonSerializer serializer = new LLSDJsonSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String json = writer.toString();
                
                assertNotNull(json);
                assertTrue(json.contains("\"b\""));
                
                String expectedBase64 = Base64.getEncoder().encodeToString(binaryData);
                assertTrue(json.contains(expectedBase64));
            }
        }

        @Test
        @DisplayName("Should handle NaN values")
        void testSerializeNaN() throws Exception {
            LLSD llsd = new LLSD(Double.NaN);
            LLSDJsonSerializer serializer = new LLSDJsonSerializer();
            
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(llsd, writer);
                String json = writer.toString();
                
                assertNotNull(json);
                assertTrue(json.contains("\"NaN\""));
            }
        }
    }

    @Nested
    @DisplayName("Round-trip Tests")
    class RoundTripTests {

        @Test
        @DisplayName("Should round-trip simple data correctly")
        void testRoundTripSimpleData() throws Exception {
            java.util.Map<String, Object> originalData = new java.util.HashMap<>();
            originalData.put("name", "test");
            originalData.put("value", 42);
            originalData.put("active", true);
            
            // Serialize to JSON
            LLSD originalLlsd = new LLSD(originalData);
            LLSDJsonSerializer serializer = new LLSDJsonSerializer();
            String json;
            try (StringWriter writer = new StringWriter()) {
                serializer.serialize(originalLlsd, writer);
                json = writer.toString();
            }
            
            // Parse back from JSON
            LLSDJsonParser parser = new LLSDJsonParser();
            LLSD parsedLlsd;
            try (InputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                parsedLlsd = parser.parse(input);
            }
            
            // Verify the data matches
            assertNotNull(parsedLlsd.getContent());
            assertTrue(parsedLlsd.getContent() instanceof java.util.Map);
            
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> parsedData = (java.util.Map<String, Object>) parsedLlsd.getContent();
            
            assertEquals("test", parsedData.get("name"));
            assertEquals(42, parsedData.get("value"));
            assertEquals(true, parsedData.get("active"));
        }
    }
}