package lindenlab.llsd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;

@DisplayName("LLSD Utilities Tests")
class LLSDUtilsTest {

    @Nested
    @DisplayName("Path Navigation Tests")
    class PathNavigationTests {

        @Test
        @DisplayName("Should navigate simple paths")
        void testSimplePathNavigation() {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "John Doe");
            data.put("age", 30);
            data.put("active", true);

            assertEquals("John Doe", LLSDUtils.getString(data, "name", "Unknown"));
            assertEquals(30, LLSDUtils.getInteger(data, "age", 0));
            assertTrue(LLSDUtils.getBoolean(data, "active", false));
        }

        @Test
        @DisplayName("Should navigate nested paths")
        void testNestedPathNavigation() {
            Map<String, Object> profile = new HashMap<>();
            profile.put("name", "Jane Doe");
            profile.put("age", 25);

            Map<String, Object> user = new HashMap<>();
            user.put("profile", profile);
            user.put("id", 123);

            Map<String, Object> data = new HashMap<>();
            data.put("user", user);

            assertEquals("Jane Doe", LLSDUtils.getString(data, "user.profile.name", "Unknown"));
            assertEquals(25, LLSDUtils.getInteger(data, "user.profile.age", 0));
            assertEquals(123, LLSDUtils.getInteger(data, "user.id", 0));
        }

        @Test
        @DisplayName("Should return default values for missing paths")
        void testMissingPaths() {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "Test");

            assertEquals("Unknown", LLSDUtils.getString(data, "missing", "Unknown"));
            assertEquals("Unknown", LLSDUtils.getString(data, "nested.missing", "Unknown"));
            assertEquals(42, LLSDUtils.getInteger(data, "missing.number", 42));
            assertFalse(LLSDUtils.getBoolean(data, "missing.flag", false));
        }

        @Test
        @DisplayName("Should handle UUID navigation")
        void testUuidNavigation() {
            UUID testUuid = UUID.randomUUID();
            Map<String, Object> data = new HashMap<>();
            data.put("id", testUuid);
            data.put("id_string", testUuid.toString());
            data.put("invalid_uuid", "not-a-uuid");

            assertEquals(testUuid, LLSDUtils.getUUID(data, "id", null));
            assertEquals(testUuid, LLSDUtils.getUUID(data, "id_string", null));
            assertNull(LLSDUtils.getUUID(data, "invalid_uuid", null));
        }
    }

    @Nested
    @DisplayName("Type Conversion Tests")
    class TypeConversionTests {

        @Test
        @DisplayName("Should safely convert to Map")
        void testAsMap() {
            Map<String, Object> validMap = new HashMap<>();
            validMap.put("key", "value");

            Map<String, Object> result1 = LLSDUtils.asMap(validMap);
            assertEquals(validMap, result1);

            Map<String, Object> result2 = LLSDUtils.asMap("not a map");
            assertTrue(result2.isEmpty());

            Map<String, Object> result3 = LLSDUtils.asMap(null);
            assertTrue(result3.isEmpty());
        }

        @Test
        @DisplayName("Should safely convert to List")
        void testAsList() {
            List<Object> validList = Arrays.asList("item1", "item2", "item3");

            List<Object> result1 = LLSDUtils.asList(validList);
            assertEquals(validList, result1);

            List<Object> result2 = LLSDUtils.asList("not a list");
            assertTrue(result2.isEmpty());

            List<Object> result3 = LLSDUtils.asList(null);
            assertTrue(result3.isEmpty());
        }
    }

    @Nested
    @DisplayName("Empty Value Tests")
    class EmptyValueTests {

        @Test
        @DisplayName("Should correctly identify empty values")
        void testIsEmpty() {
            assertTrue(LLSDUtils.isEmpty(null));
            assertTrue(LLSDUtils.isEmpty(""));
            assertTrue(LLSDUtils.isEmpty(new ArrayList<>()));
            assertTrue(LLSDUtils.isEmpty(new HashMap<>()));
            assertTrue(LLSDUtils.isEmpty(LLSDUndefined.STRING));
            assertTrue(LLSDUtils.isEmpty(new byte[0]));

            assertFalse(LLSDUtils.isEmpty("not empty"));
            assertFalse(LLSDUtils.isEmpty(Arrays.asList("item")));
            assertFalse(LLSDUtils.isEmpty(Collections.singletonMap("key", "value")));
            assertFalse(LLSDUtils.isEmpty(new byte[]{1, 2, 3}));
            assertFalse(LLSDUtils.isEmpty(42));
            assertFalse(LLSDUtils.isEmpty(true));
        }
    }

    @Nested
    @DisplayName("Deep Copy Tests")
    class DeepCopyTests {

        @Test
        @DisplayName("Should create deep copy of nested structures")
        void testDeepCopy() {
            Map<String, Object> original = new HashMap<>();
            original.put("string", "value");
            original.put("number", 42);

            Map<String, Object> nested = new HashMap<>();
            nested.put("nested_value", "nested");
            original.put("nested", nested);

            List<Object> list = new ArrayList<>();
            list.add("item1");
            list.add("item2");
            original.put("list", list);

            // Deep copy
            Object copied = LLSDUtils.deepCopy(original);

            assertTrue(copied instanceof Map);
            @SuppressWarnings("unchecked")
            Map<String, Object> copiedMap = (Map<String, Object>) copied;

            // Verify values are equal but objects are different
            assertEquals(original.get("string"), copiedMap.get("string"));
            assertEquals(original.get("number"), copiedMap.get("number"));
            
            // Verify nested structures are different objects
            assertNotSame(original.get("nested"), copiedMap.get("nested"));
            assertNotSame(original.get("list"), copiedMap.get("list"));

            // Verify nested values are still equal
            @SuppressWarnings("unchecked")
            Map<String, Object> copiedNested = (Map<String, Object>) copiedMap.get("nested");
            assertEquals("nested", copiedNested.get("nested_value"));
        }

        @Test
        @DisplayName("Should copy binary data correctly")
        void testDeepCopyBinary() {
            byte[] original = {1, 2, 3, 4, 5};
            Object copied = LLSDUtils.deepCopy(original);

            assertTrue(copied instanceof byte[]);
            byte[] copiedBytes = (byte[]) copied;
            
            assertArrayEquals(original, copiedBytes);
            assertNotSame(original, copiedBytes); // Different array objects
        }
    }

    @Nested
    @DisplayName("Map Merge Tests")
    class MapMergeTests {

        @Test
        @DisplayName("Should merge maps correctly")
        void testMergeMaps() {
            Map<String, Object> target = new HashMap<>();
            target.put("name", "John");
            target.put("age", 30);

            Map<String, Object> source = new HashMap<>();
            source.put("name", "Jane"); // Should override
            source.put("city", "New York"); // Should add

            Map<String, Object> result = LLSDUtils.mergeMaps(target, source);

            assertEquals("Jane", result.get("name")); // Overridden
            assertEquals(30, result.get("age")); // Preserved
            assertEquals("New York", result.get("city")); // Added
        }

        @Test
        @DisplayName("Should merge nested maps recursively")
        void testMergeNestedMaps() {
            Map<String, Object> targetProfile = new HashMap<>();
            targetProfile.put("name", "John");
            targetProfile.put("age", 30);

            Map<String, Object> target = new HashMap<>();
            target.put("profile", targetProfile);
            target.put("id", 123);

            Map<String, Object> sourceProfile = new HashMap<>();
            sourceProfile.put("age", 31); // Should override
            sourceProfile.put("city", "Boston"); // Should add

            Map<String, Object> source = new HashMap<>();
            source.put("profile", sourceProfile);

            Map<String, Object> result = LLSDUtils.mergeMaps(target, source);

            assertEquals(123, result.get("id")); // Preserved at root level

            @SuppressWarnings("unchecked")
            Map<String, Object> resultProfile = (Map<String, Object>) result.get("profile");
            assertEquals("John", resultProfile.get("name")); // Preserved in nested
            assertEquals(31, resultProfile.get("age")); // Overridden in nested
            assertEquals("Boston", resultProfile.get("city")); // Added in nested
        }
    }

    @Nested
    @DisplayName("Validation Tests")
    class ValidationTests {

        @Test
        @DisplayName("Should validate required fields")
        void testValidateRequiredFields() {
            Map<String, Object> profile = new HashMap<>();
            profile.put("name", "John");
            // age is missing

            Map<String, Object> data = new HashMap<>();
            data.put("profile", profile);
            data.put("id", 123);

            List<String> missing = LLSDUtils.validateRequiredFields(
                data, "profile.name", "profile.age", "id", "missing.field"
            );

            assertEquals(2, missing.size());
            assertTrue(missing.contains("profile.age"));
            assertTrue(missing.contains("missing.field"));
            assertFalse(missing.contains("profile.name"));
            assertFalse(missing.contains("id"));
        }

        @Test
        @DisplayName("Should return empty list for valid data")
        void testValidateComplete() {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);

            List<String> missing = LLSDUtils.validateRequiredFields(data, "name", "age");
            assertTrue(missing.isEmpty());
        }
    }

    @Nested
    @DisplayName("Pretty Print Tests")
    class PrettyPrintTests {

        @Test
        @DisplayName("Should format simple structures")
        void testPrettyPrintSimple() {
            Map<String, Object> data = new HashMap<>();
            data.put("name", "John");
            data.put("age", 30);

            String result = LLSDUtils.prettyPrint(data);
            assertNotNull(result);
            assertTrue(result.contains("\"name\": \"John\""));
            assertTrue(result.contains("\"age\": 30"));
        }

        @Test
        @DisplayName("Should format lists")
        void testPrettyPrintList() {
            List<Object> data = Arrays.asList("item1", 42, true);

            String result = LLSDUtils.prettyPrint(data);
            assertNotNull(result);
            assertTrue(result.contains("\"item1\""));
            assertTrue(result.contains("42"));
            assertTrue(result.contains("true"));
        }

        @Test
        @DisplayName("Should handle binary data")
        void testPrettyPrintBinary() {
            byte[] binaryData = {1, 2, 3, 4, 5};

            String result = LLSDUtils.prettyPrint(binaryData);
            assertNotNull(result);
            assertTrue(result.contains("binary[5 bytes]"));
        }
    }
}