package lindenlab.llsd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@DisplayName("LLSD XML Serialization Tests")
class LLSDSerializationTest {

    @Nested
    @DisplayName("Binary Serialization Tests")
    class BinarySerializationTests {

        @Test
        @DisplayName("Should serialize binary data to XML with base64 encoding")
        void testSerializeBinaryData() throws Exception {
            String testData = "Hello World";
            byte[] binaryData = testData.getBytes(StandardCharsets.UTF_8);
            
            LLSD llsd = new LLSD(binaryData);
            
            try (StringWriter writer = new StringWriter()) {
                llsd.serialise(writer, "UTF-8");
                String xml = writer.toString();
                
                assertNotNull(xml);
                assertTrue(xml.contains("<binary>"));
                assertTrue(xml.contains("</binary>"));
                
                String expectedBase64 = Base64.getEncoder().encodeToString(binaryData);
                assertTrue(xml.contains(expectedBase64), 
                    "XML should contain base64-encoded data: " + expectedBase64);
            }
        }

        @Test
        @DisplayName("Should serialize empty binary data")
        void testSerializeEmptyBinaryData() throws Exception {
            byte[] emptyData = new byte[0];
            
            LLSD llsd = new LLSD(emptyData);
            
            try (StringWriter writer = new StringWriter()) {
                llsd.serialise(writer, "UTF-8");
                String xml = writer.toString();
                
                assertNotNull(xml);
                assertTrue(xml.contains("<binary></binary>"), 
                    "Empty binary should serialize as empty element");
            }
        }

        @Test
        @DisplayName("Should serialize undefined binary data")
        void testSerializeUndefinedBinary() throws Exception {
            LLSD llsd = new LLSD(LLSDUndefined.BINARY);
            
            try (StringWriter writer = new StringWriter()) {
                llsd.serialise(writer, "UTF-8");
                String xml = writer.toString();
                
                assertNotNull(xml);
                assertTrue(xml.contains("<binary><undef /></binary>"), 
                    "Undefined binary should serialize correctly");
            }
        }
    }

    @Nested
    @DisplayName("Round-trip Binary Tests")
    class RoundTripBinaryTests {

        @Test
        @DisplayName("Should round-trip binary data correctly")
        void testBinaryRoundTrip() throws Exception {
            // Test with various binary data
            String[] testStrings = {
                "Hello World",
                "Binary data with special chars: \0\1\2\3\4\5",
                "Unicode: 你好世界 🌍",
                "" // Empty string
            };
            
            for (String testString : testStrings) {
                byte[] originalData = testString.getBytes(StandardCharsets.UTF_8);
                
                // Serialize to XML
                LLSD originalLlsd = new LLSD(originalData);
                String xml;
                try (StringWriter writer = new StringWriter()) {
                    originalLlsd.serialise(writer, "UTF-8");
                    xml = writer.toString();
                }
                
                // Parse back from XML
                LLSDParser parser = new LLSDParser();
                LLSD parsedLlsd;
                try (java.io.InputStream input = new java.io.ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8))) {
                    parsedLlsd = parser.parse(input);
                }
                
                // Verify the data matches
                assertNotNull(parsedLlsd.getContent(), "Parsed content should not be null for: " + testString);
                if (originalData.length == 0) {
                    assertTrue(parsedLlsd.getContent() instanceof byte[], "Empty data should be byte array");
                    byte[] parsedData = (byte[]) parsedLlsd.getContent();
                    assertEquals(0, parsedData.length, "Empty data should remain empty");
                } else {
                    assertTrue(parsedLlsd.getContent() instanceof byte[], 
                        "Content should be byte array for: " + testString);
                    byte[] parsedData = (byte[]) parsedLlsd.getContent();
                    assertArrayEquals(originalData, parsedData, 
                        "Binary data should match original for: " + testString);
                }
            }
        }

        @Test
        @DisplayName("Should handle malformed base64 gracefully")
        void testMalformedBase64() throws Exception {
            String malformedXml = "<?xml version=\"1.0\"?><llsd><binary>invalid-base64!</binary></llsd>";
            
            try (java.io.InputStream input = new java.io.ByteArrayInputStream(malformedXml.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                assertThrows(LLSDException.class, () -> parser.parse(input), 
                    "Should throw LLSDException for invalid base64 data");
            }
        }
    }
}