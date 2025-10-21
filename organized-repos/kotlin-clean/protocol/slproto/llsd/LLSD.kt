package com.linkpoint.slproto.llsd

import java.util.UUID
import java.util.Date

/**
 * LLSD - Linden Lab Structured Data
 * 
 * Flexible data system similar to JSON but with more types.
 * Based on Firestorm's llsd.h/cpp implementation.
 * 
 * Supports types:
 * - Undefined, Boolean, Integer, Real, String
 * - UUID, Date, URI, Binary
 * - Array, Map
 * 
 * Thread Safety: LLSD objects are NOT thread-safe.
 * Use separate instances per thread or synchronize access.
 */
class LLSD {
    
    private var type: LLSDType = LLSDType.Undefined
    private var value: Any? = null
    
    enum class LLSDType {
        Undefined,
        Boolean,
        Integer,
        Real,
        String,
        UUID,
        Date,
        URI,
        Binary,
        Array,
        Map
    }
    
    // Constructors
    constructor() {
        // Undefined by default
    }
    
    constructor(b: Boolean) {
        type = LLSDType.Boolean
        value = b
    }
    
    constructor(i: Int) {
        type = LLSDType.Integer
        value = i
    }
    
    constructor(d: Double) {
        type = LLSDType.Real
        value = d
    }
    
    constructor(s: String) {
        type = LLSDType.String
        value = s
    }
    
    constructor(uuid: UUID) {
        type = LLSDType.UUID
        value = uuid
    }
    
    constructor(date: Date) {
        type = LLSDType.Date
        value = date
    }
    
    constructor(binary: ByteArray) {
        type = LLSDType.Binary
        value = binary.clone()
    }
    
    // Copy constructor
    constructor(other: LLSD) {
        type = other.type
        value = when (other.type) {
            LLSDType.Array -> ArrayList((other.value as ArrayList<*>))
            LLSDType.Map -> HashMap((other.value as HashMap<*, *>))
            LLSDType.Binary -> (other.value as ByteArray).clone()
            else -> other.value
        }
    }
    
    // Type checking
    fun isUndefined() = type == LLSDType.Undefined
    fun isBoolean() = type == LLSDType.Boolean
    fun isInteger() = type == LLSDType.Integer
    fun isReal() = type == LLSDType.Real
    fun isString() = type == LLSDType.String
    fun isUUID() = type == LLSDType.UUID
    fun isDate() = type == LLSDType.Date
    fun isURI() = type == LLSDType.URI
    fun isBinary() = type == LLSDType.Binary
    fun isArray() = type == LLSDType.Array
    fun isMap() = type == LLSDType.Map
    
    fun isDefined() = type != LLSDType.Undefined
    
    // Type accessors with automatic conversion
    fun asBoolean(): Boolean {
        return when (type) {
            LLSDType.Boolean -> value as Boolean
            LLSDType.Integer -> (value as Int) != 0
            LLSDType.Real -> (value as Double) != 0.0
            LLSDType.String -> (value as String).isNotEmpty()
            else -> false
        }
    }
    
    fun asInteger(): Int {
        return when (type) {
            LLSDType.Boolean -> if (value as Boolean) 1 else 0
            LLSDType.Integer -> value as Int
            LLSDType.Real -> (value as Double).toInt()
            LLSDType.String -> (value as String).toIntOrNull() ?: 0
            else -> 0
        }
    }
    
    fun asReal(): Double {
        return when (type) {
            LLSDType.Boolean -> if (value as Boolean) 1.0 else 0.0
            LLSDType.Integer -> (value as Int).toDouble()
            LLSDType.Real -> value as Double
            LLSDType.String -> (value as String).toDoubleOrNull() ?: 0.0
            else -> 0.0
        }
    }
    
    fun asFloat(): Float = asReal().toFloat()
    
    fun asString(): String {
        return when (type) {
            LLSDType.Boolean -> (value as Boolean).toString()
            LLSDType.Integer -> (value as Int).toString()
            LLSDType.Real -> (value as Double).toString()
            LLSDType.String -> value as String
            LLSDType.UUID -> (value as UUID).toString()
            LLSDType.URI -> value as String
            else -> ""
        }
    }
    
    fun asUUID(): UUID {
        return when (type) {
            LLSDType.UUID -> value as UUID
            LLSDType.String -> try { UUID.fromString(value as String) } catch (e: Exception) { UUID(0, 0) }
            else -> UUID(0, 0)
        }
    }
    
    fun asDate(): Date {
        return when (type) {
            LLSDType.Date -> value as Date
            LLSDType.Integer -> Date((value as Int).toLong() * 1000)
            LLSDType.Real -> Date((value as Double).toLong() * 1000)
            else -> Date(0)
        }
    }
    
    fun asBinary(): ByteArray {
        return when (type) {
            LLSDType.Binary -> value as ByteArray
            LLSDType.String -> (value as String).toByteArray()
            else -> ByteArray(0)
        }
    }
    
    // Array operations
    fun asArray(): List<LLSD> {
        if (type == LLSDType.Array) {
            @Suppress("UNCHECKED_CAST")
            return value as ArrayList<LLSD>
        }
        return emptyList()
    }
    
    fun size(): Int {
        return when (type) {
            LLSDType.Array -> (value as ArrayList<*>).size
            LLSDType.Map -> (value as HashMap<*, *>).size
            LLSDType.Binary -> (value as ByteArray).size
            LLSDType.String -> (value as String).length
            else -> 0
        }
    }
    
    operator fun get(index: Int): LLSD {
        if (type != LLSDType.Array) {
            ensureArray()
        }
        @Suppress("UNCHECKED_CAST")
        val list = value as ArrayList<LLSD>
        return if (index < list.size) list[index] else LLSD()
    }
    
    operator fun set(index: Int, element: LLSD) {
        if (type != LLSDType.Array) {
            ensureArray()
        }
        @Suppress("UNCHECKED_CAST")
        val list = value as ArrayList<LLSD>
        
        // Expand array if needed
        while (list.size <= index) {
            list.add(LLSD())
        }
        list[index] = element
    }
    
    fun add(element: LLSD) {
        if (type != LLSDType.Array) {
            ensureArray()
        }
        @Suppress("UNCHECKED_CAST")
        (value as ArrayList<LLSD>).add(element)
    }
    
    // Map operations
    fun asMap(): Map<String, LLSD> {
        if (type == LLSDType.Map) {
            @Suppress("UNCHECKED_CAST")
            return value as HashMap<String, LLSD>
        }
        return emptyMap()
    }
    
    fun has(key: String): Boolean {
        if (type != LLSDType.Map) return false
        @Suppress("UNCHECKED_CAST")
        return (value as HashMap<String, LLSD>).containsKey(key)
    }
    
    operator fun get(key: String): LLSD {
        if (type != LLSDType.Map) {
            ensureMap()
        }
        @Suppress("UNCHECKED_CAST")
        return (value as HashMap<String, LLSD>)[key] ?: LLSD()
    }
    
    operator fun set(key: String, element: LLSD) {
        if (type != LLSDType.Map) {
            ensureMap()
        }
        @Suppress("UNCHECKED_CAST")
        (value as HashMap<String, LLSD>)[key] = element
    }
    
    fun keys(): Set<String> {
        if (type != LLSDType.Map) return emptySet()
        @Suppress("UNCHECKED_CAST")
        return (value as HashMap<String, LLSD>).keys
    }
    
    // Clear/reset
    fun clear() {
        type = LLSDType.Undefined
        value = null
    }
    
    // Ensure type
    private fun ensureArray() {
        if (type != LLSDType.Array) {
            type = LLSDType.Array
            value = ArrayList<LLSD>()
        }
    }
    
    private fun ensureMap() {
        if (type != LLSDType.Map) {
            type = LLSDType.Map
            value = HashMap<String, LLSD>()
        }
    }
    
    // Static constructors for containers
    companion object {
        fun emptyArray(): LLSD {
            val llsd = LLSD()
            llsd.type = LLSDType.Array
            llsd.value = ArrayList<LLSD>()
            return llsd
        }
        
        fun emptyMap(): LLSD {
            val llsd = LLSD()
            llsd.type = LLSDType.Map
            llsd.value = HashMap<String, LLSD>()
            return llsd
        }
        
        /**
         * Parse LLSD from binary format
         */
        fun parseBinary(data: ByteArray): LLSD {
            // TODO: Implement binary LLSD parser
            return LLSD()
        }
        
        /**
         * Parse LLSD from XML
         */
        fun parseXML(xml: String): LLSD {
            // TODO: Implement XML LLSD parser
            return LLSD()
        }
        
        /**
         * Parse LLSD from JSON-like notation
         */
        fun parseNotation(notation: String): LLSD {
            // TODO: Implement notation parser
            return LLSD()
        }
    }
    
    // Serialization
    fun toBinary(): ByteArray {
        // TODO: Implement binary serialization
        return ByteArray(0)
    }
    
    fun toXML(): String {
        // TODO: Implement XML serialization
        return "<llsd></llsd>"
    }
    
    fun toNotation(): String {
        // TODO: Implement notation serialization
        return when (type) {
            LLSDType.Undefined -> "!"
            LLSDType.Boolean -> if (value as Boolean) "true" else "false"
            LLSDType.Integer -> "i${value}"
            LLSDType.Real -> "r${value}"
            LLSDType.String -> "\"${value}\""
            LLSDType.UUID -> "u${value}"
            LLSDType.Array -> {
                val items = (value as ArrayList<*>).joinToString(",") { (it as LLSD).toNotation() }
                "[$items]"
            }
            LLSDType.Map -> {
                @Suppress("UNCHECKED_CAST")
                val items = (value as HashMap<String, LLSD>).entries.joinToString(",") {
                    "'${it.key}':${it.value.toNotation()}"
                }
                "{$items}"
            }
            else -> "!"
        }
    }
    
    override fun toString(): String = toNotation()
    
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is LLSD) return false
        if (type != other.type) return false
        
        return when (type) {
            LLSDType.Array -> {
                @Suppress("UNCHECKED_CAST")
                val thisArray = value as ArrayList<LLSD>
                @Suppress("UNCHECKED_CAST")
                val otherArray = other.value as ArrayList<LLSD>
                thisArray == otherArray
            }
            LLSDType.Map -> {
                @Suppress("UNCHECKED_CAST")
                val thisMap = value as HashMap<String, LLSD>
                @Suppress("UNCHECKED_CAST")
                val otherMap = other.value as HashMap<String, LLSD>
                thisMap == otherMap
            }
            LLSDType.Binary -> {
                (value as ByteArray).contentEquals(other.value as ByteArray)
            }
            else -> value == other.value
        }
    }
    
    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (value?.hashCode() ?: 0)
        return result
    }
}
