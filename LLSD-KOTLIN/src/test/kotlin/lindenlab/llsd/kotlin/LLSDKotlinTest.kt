/*
 * Tests for LLSD Kotlin DSL and functionality
 */

package lindenlab.llsd.kotlin

import lindenlab.llsd.kotlin.serialization.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*
import java.time.Instant
import java.util.UUID

/**
 * Test suite for Kotlin LLSD implementation
 */
@DisplayName("LLSD Kotlin Tests")
class LLSDKotlinTest {
    
    @Nested
    @DisplayName("DSL Builder Tests")
    inner class DSLBuilderTests {
        
        @Test
        @DisplayName("Should build simple map using DSL")
        fun testSimpleMapDSL() {
            val map = llsdMap {
                "name" to "John Doe"
                "age" to 30
                "active" to true
                "score" to 98.5
            }
            
            assertEquals("John Doe", map["name"].asString())
            assertEquals(30, map["age"].asInt())
            assertEquals(true, map["active"].asBoolean())
            assertEquals(98.5, map["score"].asDouble(), 0.001)
        }
        
        @Test
        @DisplayName("Should build simple array using DSL")
        fun testSimpleArrayDSL() {
            val array = llsdArray {
                add(1)
                add(2.5)
                add("hello")
                add(true)
            }
            
            assertEquals(4, array.size)
            assertEquals(1, array[0].asInt())
            assertEquals(2.5, array[1].asDouble(), 0.001)
            assertEquals("hello", array[2].asString())
            assertEquals(true, array[3].asBoolean())
        }
        
        @Test
        @DisplayName("Should build nested structures using DSL")
        fun testNestedStructuresDSL() {
            val complex = llsdMap {
                "user" to llsdMap {
                    "name" to "Alice"
                    "preferences" to llsdMap {
                        "theme" to "dark"
                        "notifications" to true
                    }
                }
                "scores" to llsdArray {
                    add(95)
                    add(87)
                    add(92)
                }
                "metadata" to llsdMap {
                    "created" to Instant.now()
                    "version" to 1
                }
            }
            
            assertEquals("Alice", complex["user", "name"].asString())
            assertEquals("dark", complex["user", "preferences", "theme"].asString())
            assertEquals(true, complex["user", "preferences", "notifications"].asBoolean())
            assertEquals(3, complex["scores"].asArray().size)
            assertEquals(95, complex["scores"].asArray()[0].asInt())
        }
    }
    
    @Nested
    @DisplayName("Type Safety Tests")
    inner class TypeSafetyTests {
        
        @Test
        @DisplayName("Should handle type conversions safely")
        fun testTypeSafety() {
            val value = LLSDValue.Integer(42)
            
            // Safe conversions
            assertEquals(42, value.asInt())
            assertEquals(42.0, value.asDouble(), 0.001)
            assertEquals("42", value.asString())
            assertEquals(true, value.asBoolean()) // Non-zero is true
            
            // Default fallbacks
            assertEquals(null, value.asUUID())
            assertEquals(null, value.asDate())
            assertEquals(null, value.asURI())
        }
        
        @Test
        @DisplayName("Should handle missing keys gracefully")
        fun testMissingKeys() {
            val map = llsdMap {
                "existing" to "value"
            }
            
            assertEquals("value", map["existing"].asString())
            assertEquals(LLSDValue.Undefined, map["missing"])
            assertEquals("default", map["missing"].asString("default"))
            assertEquals(42, map["missing"].asInt(42))
        }
        
        @Test
        @DisplayName("Should handle array bounds safely")
        fun testArrayBounds() {
            val array = llsdArray {
                +"first"
                +"second"
            }
            
            assertEquals("first", array[0].asString())
            assertEquals("second", array[1].asString())
            assertEquals(LLSDValue.Undefined, array[2])
            assertEquals("default", array[10].asString("default"))
        }
    }
    
    @Nested
    @DisplayName("Path Navigation Tests")
    inner class PathNavigationTests {
        
        @Test
        @DisplayName("Should navigate complex paths")
        fun testPathNavigation() {
            val data = llsdMap {
                "level1" to llsdMap {
                    "level2" to llsdArray {
                        +llsdMap {
                            "name" to "item1"
                            "value" to 100
                        }
                        +llsdMap {
                            "name" to "item2"
                            "value" to 200
                        }
                    }
                }
            }
            
            // Navigate using path function
            assertEquals("item1", data.path("level1", "level2", 0, "name").asString())
            assertEquals(200, data.path("level1", "level2", 1, "value").asInt())
            
            // Invalid path should return Undefined
            assertEquals(LLSDValue.Undefined, data.path("nonexistent", "path"))
        }
    }
    
    @Nested
    @DisplayName("Serialization Tests")
    inner class SerializationTests {
        
        private lateinit var serializer: LLSDKotlinSerializer
        
        @BeforeEach
        fun setup() {
            serializer = LLSDKotlinSerializer()
        }
        
        @Test
        @DisplayName("Should serialize to JSON")
        fun testJSONSerialization() {
            val data = llsdMap {
                "name" to "Test"
                "value" to 42
                "active" to true
                "items" to llsdArray {
                    +1
                    +2
                    +3
                }
            }
            
            val json = data.toJson(prettyPrint = false)
            assertNotNull(json)
            assertTrue(json.contains("\"name\""))
            assertTrue(json.contains("\"Test\""))
            assertTrue(json.contains("42"))
            assertTrue(json.contains("true"))
        }
        
        @Test
        @DisplayName("Should parse from JSON")
        fun testJSONParsing() {
            val json = """{"name":"Alice","age":30,"scores":[95,87,92]}"""
            val parsed = json.parseLLSDFromJson()
            
            assertTrue(parsed is LLSDValue.Map)
            val map = parsed as LLSDValue.Map
            assertEquals("Alice", map["name"].asString())
            assertEquals(30, map["age"].asInt())
            assertEquals(3, map["scores"].asArray().size)
            assertEquals(95, map["scores"].asArray()[0].asInt())
        }
        
        @Test
        @DisplayName("Should handle round-trip serialization")
        fun testRoundTripSerialization() {
            val original = llsdMap {
                "string" to "hello world"
                "integer" to 42
                "real" to 3.14159
                "boolean" to true
                "array" to llsdArray {
                    +1
                    +2
                    +3
                }
                "nested" to llsdMap {
                    "inner" to "value"
                }
            }
            
            // JSON round trip
            val json = original.toJson()
            val fromJson = json.parseLLSDFromJson()
            assertTrue(original.deepEquals(fromJson))
            
            // Notation round trip
            val notation = original.toNotation()
            val fromNotation = notation.parseLLSDFromNotation()
            assertTrue(original.deepEquals(fromNotation))
        }
    }
    
    @Nested
    @DisplayName("Utility Function Tests")
    inner class UtilityTests {
        
        @Test
        @DisplayName("Should create LLSD from various types")
        fun testLLSDOf() {
            assertEquals(LLSDValue.Undefined, llsdOf(null))
            assertEquals(LLSDValue.Boolean(true), llsdOf(true))
            assertEquals(LLSDValue.Integer(42), llsdOf(42))
            assertEquals(LLSDValue.Real(3.14), llsdOf(3.14))
            assertEquals(LLSDValue.String("hello"), llsdOf("hello"))
            
            val uuid = UUID.randomUUID()
            assertEquals(LLSDValue.UUID(uuid), llsdOf(uuid))
            
            val list = listOf(1, 2, 3)
            val llsdArray = llsdOf(list) as LLSDValue.Array
            assertEquals(3, llsdArray.size)
            assertEquals(1, llsdArray[0].asInt())
            
            val map = mapOf("key" to "value", "num" to 42)
            val llsdMap = llsdOf(map) as LLSDValue.Map
            assertEquals("value", llsdMap["key"].asString())
            assertEquals(42, llsdMap["num"].asInt())
        }
        
        @Test
        @DisplayName("Should perform deep equality checks")
        fun testDeepEquals() {
            val map1 = llsdMap {
                "name" to "test"
                "values" to llsdArray { +1; +2; +3 }
            }
            
            val map2 = llsdMap {
                "name" to "test"
                "values" to llsdArray { +1; +2; +3 }
            }
            
            val map3 = llsdMap {
                "name" to "different"
                "values" to llsdArray { +1; +2; +3 }
            }
            
            assertTrue(map1.deepEquals(map2))
            assertFalse(map1.deepEquals(map3))
        }
        
        @Test
        @DisplayName("Should create deep copies")
        fun testDeepCopy() {
            val original = llsdMap {
                "mutable" to llsdArray {
                    +llsdMap { "value" to 1 }
                }
            }
            
            val copy = original.deepCopy()
            assertTrue(original.deepEquals(copy))
            assertNotSame(original, copy)
            
            // Modify original
            original["new"] = LLSDValue.String("added")
            assertFalse(original.deepEquals(copy))
        }
        
        @Test
        @DisplayName("Should format pretty strings")
        fun testPrettyString() {
            val data = llsdMap {
                "simple" to "value"
                "array" to llsdArray {
                    add(1)
                    add(2)
                }
            }
            
            val pretty = data.toPrettyString()
            assertNotNull(pretty)
            assertTrue(pretty.contains("simple"))
            assertTrue(pretty.contains("array"))
            assertTrue(pretty.contains("[\n"))
            assertTrue(pretty.contains("{\n"))
        }
    }
    
    @Nested
    @DisplayName("Edge Case Tests")
    inner class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle empty structures")
        fun testEmptyStructures() {
            val emptyMap = llsdMap { }
            val emptyArray = llsdArray { }
            
            assertEquals(0, emptyMap.size)
            assertEquals(0, emptyArray.size)
            assertTrue(emptyMap.isEmpty())
            assertTrue(emptyArray.isEmpty())
        }
        
        @Test
        @DisplayName("Should handle special values")
        fun testSpecialValues() {
            val data = llsdMap {
                "null" to llsdOf(null)
                "empty_string" to ""
                "zero" to 0
                "negative" to -42
                "infinity" to Double.POSITIVE_INFINITY
            }
            
            assertEquals(LLSDValue.Undefined, data["null"])
            assertEquals("", data["empty_string"].asString())
            assertEquals(0, data["zero"].asInt())
            assertEquals(-42, data["negative"].asInt())
            assertEquals(Double.POSITIVE_INFINITY, data["infinity"].asDouble(), 0.0)
        }
        
        @Test
        @DisplayName("Should handle binary data")
        fun testBinaryData() {
            val binaryData = "Hello World!".toByteArray()
            val map = llsdMap {
                "data" to binaryData
            }
            
            assertArrayEquals(binaryData, map["data"].asByteArray())
        }
        
        @Test
        @DisplayName("Should handle dates and UUIDs")
        fun testSpecialTypes() {
            val uuid = UUID.randomUUID()
            val now = Instant.now()
            
            val map = llsdMap {
                "id" to uuid
                "timestamp" to now
            }
            
            assertEquals(uuid, map["id"].asUUID())
            assertEquals(now, map["timestamp"].asDate())
        }
    }
}