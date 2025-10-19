package lindenlab.llsd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.net.URI;
import java.util.Date;

@DisplayName("LLSD Parser Tests")
class LLSDParserTest {
    private static final String VALID_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<llsd>\n<map>\n  <key>region_id</key>\n    <uuid>67153d5b-3659-afb4-8510-adda2c034649</uuid>\n  <key>scale</key>\n    <string>one minute</string>\n  <key>simulator statistics</key>\n  <map>\n    <key>time dilation</key><real>0.9878624</real>\n    <key>sim fps</key><real>44.38898</real>\n    <key>pysics fps</key><real>44.38906</real>\n    <key>agent updates per second</key><real>nan</real>\n    <key>lsl instructions per second</key><real>0</real>\n    <key>total task count</key><real>4</real>\n    <key>active task count</key><real>0</real>\n    <key>active script count</key><real>4</real>\n    <key>main agent count</key><real>0</real>\n    <key>child agent count</key><real>0</real>\n    <key>inbound packets per second</key><real>1.228283</real>\n    <key>outbound packets per second</key><real>1.277508</real>\n    <key>pending downloads</key><real>0</real>\n    <key>pending uploads</key><real>0.0001096525</real>\n    <key>frame ms</key><real>0.7757886</real>\n    <key>net ms</key><real>0.3152919</real>\n    <key>sim other ms</key><real>0.1826937</real>\n    <key>sim physics ms</key><real>0.04323055</real>\n    <key>agent ms</key><real>0.01599029</real>\n    <key>image ms</key><real>0.01865955</real>\n    <key>script ms</key><real>0.1338836</real>\n  </map>\n</map>\n</llsd>";
    
    private static final String UNDEF_DOCUMENT ="<?xml version=\"1.0\" ?><llsd><map><key>volume_serial</key><undef/><key>event_type</key><string>login</string><key>success</key><boolean>true</boolean><key>session_id</key><uuid>8e56ef5c-a7be-4b92-a85c-3f1dee7a58f7</uuid><key>source</key><string>openid_do_login</string><key>done</key><boolean>true</boolean><key>agent_id</key><string>82d18a94-8c8d-44f7-a893-9dd3d7b6245d</string><key>mac_address</key><undef/><key>grid</key><string>agni</string><key>validation_stage</key><string>logged_in</string><key>ip_address</key><string>93.37.137.201</string><key>user_agent</key><string>Mozilla/5.0 (Windows NT 6.2; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36</string></map></llsd>";

    @Test
    @DisplayName("Should parse valid LLSD document without errors")
    void testParse() throws Exception {
        try (InputStream inputStream = new ByteArrayInputStream(VALID_DOCUMENT.getBytes(StandardCharsets.UTF_8))) {
            LLSDParser parser = new LLSDParser();
            LLSD result = parser.parse(inputStream);
            assertNotNull(result, "Parser should return a non-null LLSD object");
            assertNotNull(result.getContent(), "LLSD content should not be null");
        }
    }
    
    @Test
    @DisplayName("Should parse LLSD document with undefined values correctly")
    void testParseUndef() throws Exception {
        try (InputStream inputStream = new ByteArrayInputStream(UNDEF_DOCUMENT.getBytes(StandardCharsets.UTF_8))) {
            LLSDParser parser = new LLSDParser();
            LLSD output = parser.parse(inputStream);
            
            assertNotNull(output, "Parser should return a non-null LLSD object");
            Object content = output.getContent();
            assertTrue(content instanceof Map, "Content should be a Map");
            
            @SuppressWarnings("unchecked")
            Map<String, Object> contentMap = (Map<String, Object>) content;
            
            assertTrue(contentMap.containsKey("volume_serial"), "Map should contain volume_serial key");
            Object volumeSerial = contentMap.get("volume_serial");
            assertNotNull(volumeSerial, "volume_serial should not be null");
            assertEquals("", volumeSerial.toString(), "volume_serial should be empty string");
        }
    }

    @Nested
    @DisplayName("Data Type Tests")
    class DataTypeTests {

        @Test
        @DisplayName("Should parse integer values correctly")
        void testParseInteger() throws Exception {
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><integer>42</integer></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof Integer, "Content should be Integer");
                assertEquals(42, result.getContent(), "Integer value should be 42");
            }
        }

        @Test
        @DisplayName("Should parse real/double values correctly")
        void testParseReal() throws Exception {
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><real>3.14159</real></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof Double, "Content should be Double");
                assertEquals(3.14159, (Double)result.getContent(), 0.00001, "Double value should be 3.14159");
            }
        }

        @Test
        @DisplayName("Should parse NaN values correctly")
        void testParseNaN() throws Exception {
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><real>nan</real></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof Double, "Content should be Double");
                assertTrue(Double.isNaN((Double)result.getContent()), "Value should be NaN");
            }
        }

        @Test
        @DisplayName("Should parse boolean values correctly")
        void testParseBoolean() throws Exception {
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><boolean>true</boolean></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof Boolean, "Content should be Boolean");
                assertTrue((Boolean)result.getContent(), "Boolean value should be true");
            }
        }

        @Test
        @DisplayName("Should parse string values correctly")
        void testParseString() throws Exception {
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><string>Hello, World!</string></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof String, "Content should be String");
                assertEquals("Hello, World!", result.getContent(), "String value should match");
            }
        }

        @Test
        @DisplayName("Should parse UUID values correctly")
        void testParseUUID() throws Exception {
            String testUUID = "67153d5b-3659-afb4-8510-adda2c034649";
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><uuid>" + testUUID + "</uuid></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof UUID, "Content should be UUID");
                assertEquals(testUUID, result.getContent().toString(), "UUID value should match");
            }
        }

        @Test
        @DisplayName("Should parse binary data correctly")
        void testParseBinary() throws Exception {
            String testData = "Hello World";
            String base64Data = java.util.Base64.getEncoder().encodeToString(testData.getBytes(StandardCharsets.UTF_8));
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><binary>" + base64Data + "</binary></llsd>";
            
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof byte[], "Content should be byte array");
                byte[] resultData = (byte[]) result.getContent();
                String resultString = new String(resultData, StandardCharsets.UTF_8);
                assertEquals(testData, resultString, "Binary data should match original");
            }
        }

        @Test
        @DisplayName("Should parse empty binary data")
        void testParseEmptyBinary() throws Exception {
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><binary></binary></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof byte[], "Content should be byte array");
                byte[] resultData = (byte[]) result.getContent();
                assertEquals(0, resultData.length, "Empty binary should have zero length");
            }
        }

        @Test
        @DisplayName("Should handle undefined binary data")
        void testParseUndefinedBinary() throws Exception {
            String llsdDoc = "<?xml version=\"1.0\"?><llsd><binary><undef/></binary></llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(llsdDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                LLSD result = parser.parse(inputStream);
                
                assertTrue(result.getContent() instanceof LLSDUndefined, "Content should be LLSDUndefined");
                assertEquals(LLSDUndefined.BINARY, result.getContent(), "Should be BINARY undefined type");
            }
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should throw exception for malformed XML")
        void testMalformedXML() {
            String malformedXML = "<?xml version=\"1.0\"?><llsd><integer>42</llsd>";
            try (InputStream inputStream = new ByteArrayInputStream(malformedXML.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                assertThrows(Exception.class, () -> parser.parse(inputStream), 
                    "Should throw exception for malformed XML");
            } catch (IOException e) {
                fail("Should not throw IOException in test setup");
            } catch (Exception e) {
                fail("Should not throw Exception in test setup: " + e.getMessage());
            }
        }

        @Test
        @DisplayName("Should throw exception for missing llsd root element")
        void testMissingLLSDRoot() {
            String invalidDoc = "<?xml version=\"1.0\"?><notllsd><integer>42</integer></notllsd>";
            try (InputStream inputStream = new ByteArrayInputStream(invalidDoc.getBytes(StandardCharsets.UTF_8))) {
                LLSDParser parser = new LLSDParser();
                assertThrows(LLSDException.class, () -> parser.parse(inputStream), 
                    "Should throw LLSDException for wrong root element");
            } catch (IOException e) {
                fail("Should not throw IOException in test setup");
            } catch (Exception e) {
                fail("Should not throw Exception in test setup: " + e.getMessage());
            }
        }
    }
}