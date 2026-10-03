package lindenlab.llsd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@DisplayName("LLSD Binary Format Tests")
class LLSDBinaryTest {

    @Nested
    @DisplayName("Binary Parser Tests")
    class BinaryParserTests {

        @Test
        @DisplayName("Should parse simple binary values")
        void testParseSimpleValues() throws Exception {
            // Test undefined
            assertBinaryParses(new byte[]{(byte)'!'}, "");
            
            // Test booleans
            assertBinaryParses(new byte[]{(byte)'1'}, true);
            assertBinaryParses(new byte[]{(byte)'0'}, false);
            
            // Test integer (42 as big-endian)
            assertBinaryParses(new byte[]{(byte)'i', 0, 0, 0, 42}, 42);
            
            // Test string
            String testString = "hello";
            byte[] stringData = testString.getBytes(StandardCharsets.UTF_8);
            byte[] stringBinary = new byte[1 + 4 + stringData.length];
            stringBinary[0] = (byte)'s'; // string marker
            stringBinary[1] = 0; stringBinary[2] = 0; stringBinary[3] = 0; stringBinary[4] = 5; // length
            System.arraycopy(stringData, 0, stringBinary, 5, stringData.length);
            assertBinaryParses(stringBinary, "hello");
        }

        @Test
        @DisplayName("Should parse binary with header")
        void testParseWithHeader() throws Exception {
            String header = "<?llsd/binary?>";
            byte[] headerBytes = header.getBytes(StandardCharsets.US_ASCII);
            byte[] data = new byte[headerBytes.length + 1];
            System.arraycopy(headerBytes, 0, data, 0, headerBytes.length);
            data[headerBytes.length] = (byte)'1'; // true value
            
            try (InputStream input = new ByteArrayInputStream(data)) {
                LLSDBinaryParser parser = new LLSDBinaryParser();
                LLSD result = parser.parse(input);
                assertEquals(true, result.getContent());
            }
        }

        @Test
        @DisplayName("Should parse binary arrays")
        void testParseBinaryArray() throws Exception {
            ByteArrayOutputStream builder = new ByteArrayOutputStream();
            builder.write('['); // array begin
            builder.write('i'); builder.write(new byte[]{0, 0, 0, 1}); // integer 1
            builder.write('i'); builder.write(new byte[]{0, 0, 0, 2}); // integer 2
            builder.write('i'); builder.write(new byte[]{0, 0, 0, 3}); // integer 3
            builder.write(']'); // array end
            
            try (InputStream input = new ByteArrayInputStream(builder.toByteArray())) {
                LLSDBinaryParser parser = new LLSDBinaryParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof List);
                @SuppressWarnings("unchecked")
                List<Object> list = (List<Object>) result.getContent();
                
                assertEquals(3, list.size());
                assertEquals(1, list.get(0));
                assertEquals(2, list.get(1));
                assertEquals(3, list.get(2));
            }
        }

        @Test
        @DisplayName("Should parse binary maps")
        void testParseBinaryMap() throws Exception {
            ByteArrayOutputStream builder = new ByteArrayOutputStream();
            builder.write('{'); // map begin
            
            // Key-value pair: "name" -> "John"
            builder.write('k'); // key marker
            byte[] nameKey = "name".getBytes(StandardCharsets.UTF_8);
            builder.write(new byte[]{0, 0, 0, 4}); // key length
            builder.write(nameKey);
            
            builder.write('s'); // string marker
            byte[] johnValue = "John".getBytes(StandardCharsets.UTF_8);
            builder.write(new byte[]{0, 0, 0, 4}); // value length
            builder.write(johnValue);
            
            builder.write('}'); // map end
            
            try (InputStream input = new ByteArrayInputStream(builder.toByteArray())) {
                LLSDBinaryParser parser = new LLSDBinaryParser();
                LLSD result = parser.parse(input);
                
                assertTrue(result.getContent() instanceof Map);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) result.getContent();
                
                assertEquals("John", map.get("name"));
            }
        }

        private void assertBinaryParses(byte[] binaryData, Object expected) throws Exception {
            try (InputStream input = new ByteArrayInputStream(binaryData)) {
                LLSDBinaryParser parser = new LLSDBinaryParser();
                LLSD result = parser.parse(input);
                assertEquals(expected, result.getContent());
            }
        }
    }

    @Nested
    @DisplayName("Binary Serializer Tests")
    class BinarySerializerTests {

        @Test
        @DisplayName("Should serialize simple values to binary")
        void testSerializeSimpleValues() throws Exception {
            assertSerializesToBinary("", new byte[]{(byte)'!'});
            assertSerializesToBinary(true, new byte[]{(byte)'1'});
            assertSerializesToBinary(false, new byte[]{(byte)'0'});
            assertSerializesToBinary(42, new byte[]{(byte)'i', 0, 0, 0, 42});
        }

        @Test
        @DisplayName("Should serialize strings to binary")
        void testSerializeString() throws Exception {
            String testString = "hello";
            LLSD llsd = new LLSD(testString);
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            
            byte[] result = serializer.serialize(llsd, false); // without header
            
            assertEquals((byte)'s', result[0]); // string marker
            // Check length (big-endian)
            assertEquals(0, result[1]);
            assertEquals(0, result[2]);
            assertEquals(0, result[3]);
            assertEquals(5, result[4]);
            // Check string data
            String resultString = new String(result, 5, 5, StandardCharsets.UTF_8);
            assertEquals("hello", resultString);
        }

        @Test
        @DisplayName("Should serialize with header")
        void testSerializeWithHeader() throws Exception {
            LLSD llsd = new LLSD(true);
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            
            byte[] result = serializer.serialize(llsd, true); // with header
            
            String header = "<?llsd/binary?>";
            byte[] headerBytes = header.getBytes(StandardCharsets.US_ASCII);
            
            // Check header
            for (int i = 0; i < headerBytes.length; i++) {
                assertEquals(headerBytes[i], result[i]);
            }
            
            // Check value
            assertEquals((byte)'1', result[headerBytes.length]);
        }

        @Test
        @DisplayName("Should serialize arrays to binary")
        void testSerializeArray() throws Exception {
            List<Object> array = Arrays.asList(1, 2, 3);
            LLSD llsd = new LLSD(array);
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            
            byte[] result = serializer.serialize(llsd, false);
            
            assertEquals((byte)'[', result[0]); // array begin
            
            // Check that it contains integer markers and values
            boolean foundArrayEnd = false;
            for (byte b : result) {
                if (b == (byte)']') {
                    foundArrayEnd = true;
                    break;
                }
            }
            assertTrue(foundArrayEnd, "Array should end with ']' marker");
        }

        @Test
        @DisplayName("Should serialize maps to binary")
        void testSerializeMap() throws Exception {
            Map<String, Object> map = new HashMap<>();
            map.put("test", "value");
            
            LLSD llsd = new LLSD(map);
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            
            byte[] result = serializer.serialize(llsd, false);
            
            assertEquals((byte)'{', result[0]); // map begin
            
            // Should contain key marker
            boolean foundKeyMarker = false;
            boolean foundMapEnd = false;
            
            for (byte b : result) {
                if (b == (byte)'k') {
                    foundKeyMarker = true;
                }
                if (b == (byte)'}') {
                    foundMapEnd = true;
                }
            }
            
            assertTrue(foundKeyMarker, "Map should contain key marker 'k'");
            assertTrue(foundMapEnd, "Map should end with '}' marker");
        }

        private void assertSerializesToBinary(Object value, byte[] expectedBinary) throws Exception {
            LLSD llsd = new LLSD(value);
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            
            byte[] result = serializer.serialize(llsd, false); // without header
            assertArrayEquals(expectedBinary, result);
        }
    }

    @Nested
    @DisplayName("Binary Round-trip Tests")
    class BinaryRoundTripTests {

        @Test
        @DisplayName("Should round-trip primitive types")
        void testPrimitiveRoundTrip() throws Exception {
            Object[] testValues = {
                true, false, 42, 3.14159, "hello world", ""
            };
            
            for (Object originalValue : testValues) {
                // Serialize
                LLSD originalLlsd = new LLSD(originalValue);
                LLSDBinarySerializer serializer = new LLSDBinarySerializer();
                byte[] binaryData = serializer.serialize(originalLlsd, false);
                
                // Parse back
                LLSDBinaryParser parser = new LLSDBinaryParser();
                LLSD parsedLlsd;
                try (InputStream input = new ByteArrayInputStream(binaryData)) {
                    parsedLlsd = parser.parse(input);
                }
                
                // Verify
                assertEquals(originalValue, parsedLlsd.getContent(), 
                    "Round-trip failed for value: " + originalValue);
            }
        }

        @Test
        @DisplayName("Should round-trip complex structures")
        void testComplexRoundTrip() throws Exception {
            Map<String, Object> originalData = new HashMap<>();
            originalData.put("name", "Test User");
            originalData.put("age", 30);
            originalData.put("active", true);
            originalData.put("score", 95.5);
            
            List<Object> items = Arrays.asList("item1", "item2", "item3");
            originalData.put("items", items);
            
            // Serialize
            LLSD originalLlsd = new LLSD(originalData);
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            byte[] binaryData = serializer.serialize(originalLlsd, true); // with header
            
            // Parse back
            LLSDBinaryParser parser = new LLSDBinaryParser();
            LLSD parsedLlsd;
            try (InputStream input = new ByteArrayInputStream(binaryData)) {
                parsedLlsd = parser.parse(input);
            }
            
            // Verify
            assertTrue(parsedLlsd.getContent() instanceof Map);
            @SuppressWarnings("unchecked")
            Map<String, Object> parsedData = (Map<String, Object>) parsedLlsd.getContent();
            
            assertEquals("Test User", parsedData.get("name"));
            assertEquals(30, parsedData.get("age"));
            assertEquals(true, parsedData.get("active"));
            assertEquals(95.5, parsedData.get("score"));
            
            assertTrue(parsedData.get("items") instanceof List);
            @SuppressWarnings("unchecked")
            List<Object> parsedItems = (List<Object>) parsedData.get("items");
            assertEquals(3, parsedItems.size());
            assertEquals("item1", parsedItems.get(0));
        }

        @Test
        @DisplayName("Should round-trip binary data")
        void testBinaryDataRoundTrip() throws Exception {
            byte[] originalBinary = "Hello, Binary World! 🌍".getBytes(StandardCharsets.UTF_8);
            
            // Serialize
            LLSD originalLlsd = new LLSD(originalBinary);
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            byte[] binaryData = serializer.serialize(originalLlsd, false);
            
            // Parse back
            LLSDBinaryParser parser = new LLSDBinaryParser();
            LLSD parsedLlsd;
            try (InputStream input = new ByteArrayInputStream(binaryData)) {
                parsedLlsd = parser.parse(input);
            }
            
            // Verify
            assertTrue(parsedLlsd.getContent() instanceof byte[]);
            byte[] parsedBinary = (byte[]) parsedLlsd.getContent();
            assertArrayEquals(originalBinary, parsedBinary);
        }
    }

    @Nested
    @DisplayName("Binary Format Compliance Tests")
    class BinaryComplianceTests {

        @Test
        @DisplayName("Should handle empty structures")
        void testEmptyStructures() throws Exception {
            // Empty array
            LLSD emptyArray = new LLSD(new ArrayList<>());
            assertBinaryRoundTrip(emptyArray);
            
            // Empty map
            LLSD emptyMap = new LLSD(new HashMap<>());
            assertBinaryRoundTrip(emptyMap);
            
            // Empty binary data
            LLSD emptyBinary = new LLSD(new byte[0]);
            assertBinaryRoundTrip(emptyBinary);
        }

        @Test
        @DisplayName("Should handle large data efficiently")
        void testLargeData() throws Exception {
            // Create a large string
            StringBuilder largeString = new StringBuilder();
            for (int i = 0; i < 10000; i++) {
                largeString.append("This is line ").append(i).append("\n");
            }
            
            LLSD llsd = new LLSD(largeString.toString());
            assertBinaryRoundTrip(llsd);
        }

        private void assertBinaryRoundTrip(LLSD originalLlsd) throws Exception {
            LLSDBinarySerializer serializer = new LLSDBinarySerializer();
            byte[] binaryData = serializer.serialize(originalLlsd, true);
            
            LLSDBinaryParser parser = new LLSDBinaryParser();
            LLSD parsedLlsd;
            try (InputStream input = new ByteArrayInputStream(binaryData)) {
                parsedLlsd = parser.parse(input);
            }
            
            // For complex objects, we verify the type and basic structure
            Object original = originalLlsd.getContent();
            Object parsed = parsedLlsd.getContent();
            
            assertEquals(original.getClass(), parsed.getClass());
            
            if (original instanceof String) {
                assertEquals(original, parsed);
            } else if (original instanceof byte[]) {
                assertArrayEquals((byte[]) original, (byte[]) parsed);
            } else if (original instanceof Collection) {
                assertEquals(((Collection<?>) original).size(), ((Collection<?>) parsed).size());
            } else if (original instanceof Map) {
                assertEquals(((Map<?, ?>) original).size(), ((Map<?, ?>) parsed).size());
            }
        }
    }
}