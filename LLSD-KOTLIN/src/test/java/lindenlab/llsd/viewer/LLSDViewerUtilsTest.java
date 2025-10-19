/*
 * Tests for LLSD Viewer Utilities
 */

package lindenlab.llsd.viewer;

import lindenlab.llsd.viewer.LLSDViewerUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test suite for LLSD Viewer utilities converted from C++ implementation.
 */
@DisplayName("LLSD Viewer Utils Tests")
public class LLSDViewerUtilsTest {
    
    @Nested
    @DisplayName("Type Conversion Tests")
    class TypeConversionTests {
        
        @Test
        @DisplayName("U32 conversion should handle unsigned 32-bit range")
        void testU32Conversion() {
            // Test valid range
            assertEquals(0L, LLSDViewerUtils.toU32(LLSDViewerUtils.fromU32(0L)));
            assertEquals(0xFFFFFFFFL, LLSDViewerUtils.toU32(LLSDViewerUtils.fromU32(0xFFFFFFFFL)));
            
            // Test boundary values
            assertThrows(IllegalArgumentException.class, () -> LLSDViewerUtils.fromU32(-1L));
            assertThrows(IllegalArgumentException.class, () -> LLSDViewerUtils.fromU32(0x100000000L));
        }
        
        @Test
        @DisplayName("U64 conversion should handle full long range")
        void testU64Conversion() {
            assertEquals(Long.MAX_VALUE, LLSDViewerUtils.toU64(LLSDViewerUtils.fromU64(Long.MAX_VALUE)));
            assertEquals(Long.MIN_VALUE, LLSDViewerUtils.toU64(LLSDViewerUtils.fromU64(Long.MIN_VALUE)));
            assertEquals(0L, LLSDViewerUtils.toU64(LLSDViewerUtils.fromU64(0L)));
        }
        
        @Test
        @DisplayName("IP address conversion should preserve values")
        void testIPAddressConversion() {
            int testIP = 0x7F000001; // 127.0.0.1
            assertEquals(testIP, LLSDViewerUtils.toIPAddress(LLSDViewerUtils.fromIPAddress(testIP)));
        }
        
        @Test
        @DisplayName("Binary/string conversion should be symmetric")
        void testBinaryStringConversion() {
            byte[] testBinary = "Hello, World!".getBytes(java.nio.charset.StandardCharsets.UTF_8);
            String encoded = LLSDViewerUtils.stringFromBinary(testBinary);
            byte[] recovered = LLSDViewerUtils.binaryFromString(encoded);
            assertArrayEquals(testBinary, recovered);
        }
    }
    
    @Nested
    @DisplayName("Template Comparison Tests")
    class TemplateComparisonTests {
        
        @Test
        @DisplayName("Template comparison should validate structure")
        void testTemplateComparison() {
            Map<String, Object> template = new HashMap<>();
            template.put("name", "");
            template.put("age", 0);
            template.put("active", true);
            
            Map<String, Object> testData = new HashMap<>();
            testData.put("name", "John");
            testData.put("age", 25);
            testData.put("active", false);
            testData.put("extra", "ignored");
            
            Map<String, Object> result = new HashMap<>();
            assertTrue(LLSDViewerUtils.compareWithTemplate(testData, template, result));
            
            assertEquals("John", result.get("name"));
            assertEquals(25, result.get("age"));
            assertEquals(false, result.get("active"));
            assertFalse(result.containsKey("extra")); // Should be filtered out
        }
        
        @Test
        @DisplayName("Template filtering should support wildcards")
        void testTemplateFiltering() {
            Map<String, Object> template = new HashMap<>();
            template.put("required", "");
            template.put("*", 0); // Wildcard matches any unspecified field
            
            Map<String, Object> testData = new HashMap<>();
            testData.put("required", "value");
            testData.put("optional1", 42);
            testData.put("optional2", 3.14);
            
            Map<String, Object> result = new HashMap<>();
            assertTrue(LLSDViewerUtils.filterWithTemplate(testData, template, result));
            
            assertTrue(result.containsKey("required"));
            // Wildcards should allow additional fields through
        }
    }
    
    @Nested
    @DisplayName("LLSD Matching Tests")
    class LLSDMatchingTests {
        
        @Test
        @DisplayName("LLSD matching should validate type compatibility")
        void testLLSDMatches() {
            // String prototype should match string-convertible types
            String result = LLSDViewerUtils.llsdMatches("string", "test", "");
            assertTrue(result.isEmpty());
            
            result = LLSDViewerUtils.llsdMatches("string", 42, "");
            assertTrue(result.isEmpty()); // Integer should be convertible to string
            
            result = LLSDViewerUtils.llsdMatches("string", new byte[]{1, 2, 3}, "");
            assertFalse(result.isEmpty()); // Binary should not be convertible to string
        }
        
        @Test
        @DisplayName("Array matching should validate size and elements")
        void testArrayMatching() {
            List<Object> prototype = Arrays.asList("", 0);
            List<Object> validData = Arrays.asList("test", 42, "extra"); // Extra elements OK
            List<Object> invalidData = Arrays.asList("test"); // Too short
            
            String result = LLSDViewerUtils.llsdMatches(prototype, validData, "");
            assertTrue(result.isEmpty());
            
            result = LLSDViewerUtils.llsdMatches(prototype, invalidData, "");
            assertFalse(result.isEmpty());
        }
        
        @Test
        @DisplayName("Map matching should validate required keys")
        void testMapMatching() {
            Map<String, Object> prototype = new HashMap<>();
            prototype.put("required1", "");
            prototype.put("required2", 0);
            
            Map<String, Object> validData = new HashMap<>();
            validData.put("required1", "value");
            validData.put("required2", 42);
            validData.put("optional", "ignored");
            
            Map<String, Object> invalidData = new HashMap<>();
            invalidData.put("required1", "value");
            // Missing required2
            
            String result = LLSDViewerUtils.llsdMatches(prototype, validData, "");
            assertTrue(result.isEmpty());
            
            result = LLSDViewerUtils.llsdMatches(prototype, invalidData, "");
            assertFalse(result.isEmpty());
        }
    }
    
    @Nested
    @DisplayName("Deep Equality Tests")
    class DeepEqualityTests {
        
        @Test
        @DisplayName("Deep equality should handle primitive types")
        void testPrimitiveEquality() {
            assertTrue(LLSDViewerUtils.llsdEquals(42, 42));
            assertTrue(LLSDViewerUtils.llsdEquals("test", "test"));
            assertTrue(LLSDViewerUtils.llsdEquals(true, true));
            
            assertFalse(LLSDViewerUtils.llsdEquals(42, 43));
            assertFalse(LLSDViewerUtils.llsdEquals("test", "other"));
            assertFalse(LLSDViewerUtils.llsdEquals(true, false));
        }
        
        @Test
        @DisplayName("Deep equality should handle floating point precision")
        void testFloatingPointEquality() {
            // Exact equality
            assertTrue(LLSDViewerUtils.llsdEquals(3.14159, 3.14159, -1));
            
            // Approximate equality
            assertTrue(LLSDViewerUtils.llsdEquals(3.14159, 3.14160, 10)); // 10-bit precision
            assertFalse(LLSDViewerUtils.llsdEquals(3.14159, 3.14160, 20)); // 20-bit precision
        }
        
        @Test
        @DisplayName("Deep equality should handle arrays")
        void testArrayEquality() {
            List<Object> array1 = Arrays.asList(1, 2, 3);
            List<Object> array2 = Arrays.asList(1, 2, 3);
            List<Object> array3 = Arrays.asList(1, 2, 4);
            List<Object> array4 = Arrays.asList(1, 2);
            
            assertTrue(LLSDViewerUtils.llsdEquals(array1, array2));
            assertFalse(LLSDViewerUtils.llsdEquals(array1, array3));
            assertFalse(LLSDViewerUtils.llsdEquals(array1, array4));
        }
        
        @Test
        @DisplayName("Deep equality should handle maps")
        void testMapEquality() {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("a", 1);
            map1.put("b", 2);
            
            Map<String, Object> map2 = new HashMap<>();
            map2.put("b", 2);
            map2.put("a", 1); // Different order, should still be equal
            
            Map<String, Object> map3 = new HashMap<>();
            map3.put("a", 1);
            map3.put("b", 3); // Different value
            
            assertTrue(LLSDViewerUtils.llsdEquals(map1, map2));
            assertFalse(LLSDViewerUtils.llsdEquals(map1, map3));
        }
        
        @Test
        @DisplayName("Deep equality should handle binary data")
        void testBinaryEquality() {
            byte[] binary1 = {1, 2, 3, 4};
            byte[] binary2 = {1, 2, 3, 4};
            byte[] binary3 = {1, 2, 3, 5};
            
            assertTrue(LLSDViewerUtils.llsdEquals(binary1, binary2));
            assertFalse(LLSDViewerUtils.llsdEquals(binary1, binary3));
        }
    }
    
    @Nested
    @DisplayName("Cloning Tests")
    class CloningTests {
        
        @Test
        @DisplayName("Deep cloning should create independent copies")
        void testDeepCloning() {
            Map<String, Object> original = new HashMap<>();
            original.put("name", "test");
            List<Object> nestedArray = new ArrayList<>(Arrays.asList(1, 2, 3));
            original.put("array", nestedArray);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> cloned = (Map<String, Object>) LLSDViewerUtils.llsdClone(original);
            
            // Should be equal but not same instance
            assertTrue(LLSDViewerUtils.llsdEquals(original, cloned));
            assertNotSame(original, cloned);
            assertNotSame(original.get("array"), cloned.get("array"));
            
            // Modifying original should not affect clone
            nestedArray.add(4);
            assertFalse(LLSDViewerUtils.llsdEquals(original, cloned));
        }
        
        @Test
        @DisplayName("Filtered cloning should respect filter rules")
        void testFilteredCloning() {
            Map<String, Object> original = new HashMap<>();
            original.put("include", "yes");
            original.put("exclude", "no");
            original.put("wildcard", "maybe");
            
            Map<String, Boolean> filter = new HashMap<>();
            filter.put("include", true);
            filter.put("exclude", false);
            filter.put("*", true); // Wildcard allows unspecified fields
            
            @SuppressWarnings("unchecked")
            Map<String, Object> filtered = (Map<String, Object>) LLSDViewerUtils.llsdClone(original, filter);
            
            assertTrue(filtered.containsKey("include"));
            assertFalse(filtered.containsKey("exclude"));
            assertTrue(filtered.containsKey("wildcard")); // Allowed by wildcard
        }
        
        @Test
        @DisplayName("Shallow cloning should not clone nested structures")
        void testShallowCloning() {
            Map<String, Object> original = new HashMap<>();
            List<Object> nestedArray = new ArrayList<>(Arrays.asList(1, 2, 3));
            original.put("array", nestedArray);
            
            @SuppressWarnings("unchecked")
            Map<String, Object> shallow = (Map<String, Object>) LLSDViewerUtils.llsdShallow(original);
            
            assertNotSame(original, shallow);
            assertSame(original.get("array"), shallow.get("array")); // Same reference
            
            // Modifying nested structure affects both
            nestedArray.add(4);
            assertEquals(4, ((List<?>) shallow.get("array")).size());
        }
    }
}