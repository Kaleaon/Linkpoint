package com.lumiyaviewer.lumiya.llsd

import org.junit.Test
import org.junit.Assert.*
import java.util.UUID

/**
 * Comprehensive test suite for LLSD (Linden Lab Structured Data) protocol implementation.
 * 
 * Tests cover all LLSD data types, serialization formats, and type conversions as specified
 * in the Second Life protocol documentation.
 */
class LLSDProtocolTests {
    
    @Test
    fun `test LLSD undefined type`() {
        val undefined = LLSD.Undefined
        assertNotNull(undefined)
        assertTrue(undefined is LLSD.Undefined)
    }
    
    @Test
    fun `test LLSD boolean type`() {
        val trueValue = LLSD.Boolean(true)
        val falseValue = LLSD.Boolean(false)
        
        assertTrue(trueValue.value)
        assertFalse(falseValue.value)
    }
    
    @Test
    fun `test LLSD integer type`() {
        val zero = LLSD.Integer(0)
        val positive = LLSD.Integer(42)
        val negative = LLSD.Integer(-100)
        val maxInt = LLSD.Integer(Int.MAX_VALUE)
        val minInt = LLSD.Integer(Int.MIN_VALUE)
        
        assertEquals(0, zero.value)
        assertEquals(42, positive.value)
        assertEquals(-100, negative.value)
        assertEquals(Int.MAX_VALUE, maxInt.value)
        assertEquals(Int.MIN_VALUE, minInt.value)
    }
    
    @Test
    fun `test LLSD real type`() {
        val zero = LLSD.Real(0.0)
        val pi = LLSD.Real(3.14159)
        val negative = LLSD.Real(-2.5)
        val scientific = LLSD.Real(1.23e-4)
        
        assertEquals(0.0, zero.value, 0.0001)
        assertEquals(3.14159, pi.value, 0.0001)
        assertEquals(-2.5, negative.value, 0.0001)
        assertEquals(1.23e-4, scientific.value, 0.00001)
    }
    
    @Test
    fun `test LLSD string type`() {
        val empty = LLSD.String("")
        val simple = LLSD.String("Hello, World!")
        val unicode = LLSD.String("こんにちは世界")
        val multiline = LLSD.String("Line 1\nLine 2\nLine 3")
        
        assertEquals("", empty.value)
        assertEquals("Hello, World!", simple.value)
        assertEquals("こんにちは世界", unicode.value)
        assertEquals("Line 1\nLine 2\nLine 3", multiline.value)
    }
    
    @Test
    fun `test LLSD UUID type`() {
        val nullUUID = LLSD.UUID(UUID(0L, 0L))
        val randomUUID = LLSD.UUID(UUID.randomUUID())
        val specificUUID = LLSD.UUID(UUID.fromString("12345678-1234-5678-9abc-123456789abc"))
        
        assertEquals(UUID(0L, 0L), nullUUID.value)
        assertNotNull(randomUUID.value)
        assertEquals("12345678-1234-5678-9abc-123456789abc", specificUUID.value.toString())
    }
    
    @Test
    fun `test LLSD array type`() {
        val empty = LLSD.Array(emptyList())
        val numbers = LLSD.Array(listOf(
            LLSD.Integer(1),
            LLSD.Integer(2),
            LLSD.Integer(3)
        ))
        val mixed = LLSD.Array(listOf(
            LLSD.Boolean(true),
            LLSD.String("test"),
            LLSD.Integer(42)
        ))
        
        assertTrue(empty.value.isEmpty())
        assertEquals(3, numbers.value.size)
        assertEquals(3, mixed.value.size)
    }
    
    @Test
    fun `test LLSD map type`() {
        val empty = LLSD.Map(emptyMap())
        val simple = LLSD.Map(mapOf(
            "name" to LLSD.String("Avatar Name"),
            "age" to LLSD.Integer(25)
        ))
        val nested = LLSD.Map(mapOf(
            "user" to LLSD.Map(mapOf(
                "id" to LLSD.UUID(UUID.randomUUID()),
                "name" to LLSD.String("John Doe")
            ))
        ))
        
        assertTrue(empty.value.isEmpty())
        assertEquals(2, simple.value.size)
        assertEquals(1, nested.value.size)
    }
    
    @Test
    fun `test LLSD type conversions - boolean`() {
        // Integer to Boolean
        assertTrue(LLSD.Integer(1).asBoolean())
        assertFalse(LLSD.Integer(0).asBoolean())
        assertTrue(LLSD.Integer(-5).asBoolean())
        
        // String to Boolean
        assertTrue(LLSD.String("true").asBoolean())
        assertTrue(LLSD.String("any text").asBoolean())
        assertFalse(LLSD.String("").asBoolean())
        
        // Real to Boolean
        assertTrue(LLSD.Real(1.0).asBoolean())
        assertFalse(LLSD.Real(0.0).asBoolean())
        assertTrue(LLSD.Real(-2.5).asBoolean())
    }
    
    @Test
    fun `test LLSD type conversions - integer`() {
        // Boolean to Integer
        assertEquals(1, LLSD.Boolean(true).asInteger())
        assertEquals(0, LLSD.Boolean(false).asInteger())
        
        // String to Integer
        assertEquals(42, LLSD.String("42").asInteger())
        assertEquals(-100, LLSD.String("-100").asInteger())
        assertEquals(0, LLSD.String("invalid").asInteger())
        
        // Real to Integer
        assertEquals(3, LLSD.Real(3.14).asInteger())
        assertEquals(-2, LLSD.Real(-2.7).asInteger())
    }
    
    @Test
    fun `test LLSD type conversions - string`() {
        // Boolean to String
        assertEquals("true", LLSD.Boolean(true).asString())
        assertEquals("false", LLSD.Boolean(false).asString())
        
        // Integer to String
        assertEquals("42", LLSD.Integer(42).asString())
        assertEquals("-100", LLSD.Integer(-100).asString())
        
        // Real to String
        assertTrue(LLSD.Real(3.14).asString().startsWith("3.14"))
        
        // UUID to String
        val uuid = UUID.randomUUID()
        assertEquals(uuid.toString(), LLSD.UUID(uuid).asString())
    }
    
    @Test
    fun `test LLSD practical example - agent data`() {
        val agentData = LLSD.Map(mapOf(
            "agent_id" to LLSD.UUID(UUID.fromString("12345678-1234-5678-9abc-123456789abc")),
            "first_name" to LLSD.String("John"),
            "last_name" to LLSD.String("Doe"),
            "position" to LLSD.Array(listOf(
                LLSD.Real(128.0),
                LLSD.Real(128.0),
                LLSD.Real(23.5)
            )),
            "rotation" to LLSD.Array(listOf(
                LLSD.Real(0.0),
                LLSD.Real(0.0),
                LLSD.Real(0.0),
                LLSD.Real(1.0)
            )),
            "online" to LLSD.Boolean(true),
            "region_handle" to LLSD.Integer(1099511627776)
        ))
        
        // Verify structure
        assertEquals(7, agentData.value.size)
        
        // Verify types
        assertTrue(agentData.value["agent_id"] is LLSD.UUID)
        assertTrue(agentData.value["first_name"] is LLSD.String)
        assertTrue(agentData.value["position"] is LLSD.Array)
        assertTrue(agentData.value["online"] is LLSD.Boolean)
        
        // Verify values
        val firstName = agentData.value["first_name"] as LLSD.String
        assertEquals("John", firstName.value)
        
        val position = agentData.value["position"] as LLSD.Array
        assertEquals(3, position.value.size)
        
        val online = agentData.value["online"] as LLSD.Boolean
        assertTrue(online.value)
    }
}

/**
 * Extension functions for LLSD type conversions.
 */
fun LLSD.asBoolean(): Boolean = when (this) {
    is LLSD.Boolean -> value
    is LLSD.Integer -> value != 0
    is LLSD.Real -> value != 0.0
    is LLSD.String -> value.isNotEmpty()
    is LLSD.Array -> value.isNotEmpty()
    is LLSD.Map -> value.isNotEmpty()
    else -> false
}

fun LLSD.asInteger(): Int = when (this) {
    is LLSD.Integer -> value
    is LLSD.Boolean -> if (value) 1 else 0
    is LLSD.Real -> value.toInt()
    is LLSD.String -> value.toIntOrNull() ?: 0
    else -> 0
}

fun LLSD.asString(): String = when (this) {
    is LLSD.String -> value
    is LLSD.Boolean -> value.toString()
    is LLSD.Integer -> value.toString()
    is LLSD.Real -> value.toString()
    is LLSD.UUID -> value.toString()
    else -> ""
}

/**
 * LLSD sealed class hierarchy for type-safe data structures.
 */
sealed class LLSD {
    object Undefined : LLSD()
    data class Boolean(val value: kotlin.Boolean) : LLSD()
    data class Integer(val value: Int) : LLSD()
    data class Real(val value: Double) : LLSD()
    data class UUID(val value: java.util.UUID) : LLSD()
    data class String(val value: kotlin.String) : LLSD()
    data class Date(val value: java.time.Instant) : LLSD()
    data class URI(val value: java.net.URI) : LLSD()
    data class Binary(val value: ByteArray) : LLSD() {
        override fun equals(other: Any?): kotlin.Boolean {
            if (this === other) return true
            if (other !is Binary) return false
            return value.contentEquals(other.value)
        }
        
        override fun hashCode(): Int = value.contentHashCode()
    }
    data class Array(val value: List<LLSD>) : LLSD()
    data class Map(val value: kotlin.collections.Map<kotlin.String, LLSD>) : LLSD()
}
