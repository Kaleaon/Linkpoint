package com.linkpoint.slproto.llsd

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Base64
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID
import com.linkpoint.slproto.llsd.types.LLSDArray
import com.linkpoint.slproto.llsd.types.LLSDBinary
import com.linkpoint.slproto.llsd.types.LLSDBoolean
import com.linkpoint.slproto.llsd.types.LLSDDate
import com.linkpoint.slproto.llsd.types.LLSDDouble
import com.linkpoint.slproto.llsd.types.LLSDInt
import com.linkpoint.slproto.llsd.types.LLSDMap
import com.linkpoint.slproto.llsd.types.LLSDNode
import com.linkpoint.slproto.llsd.types.LLSDString
import com.linkpoint.slproto.llsd.types.LLSDURI
import com.linkpoint.slproto.llsd.types.LLSDUUID
import com.linkpoint.slproto.llsd.types.LLSDUndefined

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

          fun fromUri(uri: String): LLSD {
              val llsd = LLSD()
              llsd.type = LLSDType.URI
              llsd.value = uri
              return llsd
          }
        
        /**
         * Parse LLSD from binary format
         */
        fun parseBinary(data: ByteArray): LLSD {
              return try {
                  ByteArrayInputStream(data).use { input ->
                      LLSDBinaryParser.parse(input)
                  }
              } catch (e: IOException) {
                  throw LLSDException("Binary parse failed: ${e.message}", e)
              }
        }
        
        /**
         * Parse LLSD from XML
         */
        fun parseXML(xml: String): LLSD {
              return LLSDXMLParser.parse(xml)
        }
        
        /**
         * Parse LLSD from JSON-like notation
         */
        fun parseNotation(notation: String): LLSD {
              return LLSDNotationParser.parse(notation)
        }
    }
    
    // Serialization
    fun toBinary(): ByteArray {
          val output = ByteArrayOutputStream()
          return try {
              LLSDBinaryParser.serialize(this, output)
              output.toByteArray()
          } catch (e: IOException) {
              throw LLSDException("Binary serialization failed: ${e.message}", e)
          }
    }
    
    fun toXML(): String {
          return try {
              toNode().serializeToXML()
          } catch (e: IOException) {
              throw LLSDException("XML serialization failed: ${e.message}", e)
          }
    }
    
    fun toNotation(): String {
        return when (type) {
            LLSDType.Undefined -> "!"
            LLSDType.Boolean -> if (asBoolean()) "true" else "false"
            LLSDType.Integer -> "i${asInteger()}"
            LLSDType.Real -> "r${asReal()}"
            LLSDType.String -> "s\"${escapeNotationString(asString())}\""
            LLSDType.UUID -> "u${asUUID()}"
            LLSDType.Date -> "d${notationDateFormat().format(asDate())}"
            LLSDType.URI -> "l${asString()}"
            LLSDType.Binary -> {
                val base64 = Base64.getEncoder().encodeToString(asBinary())
                "b64\"$base64\""
            }
            LLSDType.Array -> {
                val list = value as? ArrayList<LLSD> ?: ArrayList()
                list.joinToString(prefix = "[", postfix = "]") { it.toNotation() }
            }
            LLSDType.Map -> {
                @Suppress("UNCHECKED_CAST")
                val map = value as? HashMap<String, LLSD> ?: HashMap()
                map.entries.joinToString(prefix = "{", postfix = "}") { (k, v) ->
                    val keyNotation = if (k.all { it.isLetterOrDigit() || it == '_' }) k else "s\"${escapeNotationString(k)}\""
                    "$keyNotation:${v.toNotation()}"
                }
            }
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

    private fun toNode(): LLSDNode {
        return when (type) {
            LLSDType.Undefined -> LLSDUndefined()
            LLSDType.Boolean -> LLSDBoolean(asBoolean())
            LLSDType.Integer -> LLSDInt(asInteger())
            LLSDType.Real -> LLSDDouble(asReal())
            LLSDType.String -> LLSDString(asString())
            LLSDType.UUID -> LLSDUUID(asUUID())
            LLSDType.Date -> LLSDDate(asDate())
            LLSDType.URI -> LLSDURI(asString())
            LLSDType.Binary -> LLSDBinary(asBinary())
            LLSDType.Array -> {
                val arrayNode = LLSDArray()
                @Suppress("UNCHECKED_CAST")
                val list = value as? ArrayList<LLSD> ?: ArrayList()
                for (child in list) {
                    arrayNode.add(child.toNode())
                }
                arrayNode
            }
            LLSDType.Map -> {
                @Suppress("UNCHECKED_CAST")
                val map = value as? HashMap<String, LLSD> ?: HashMap()
                val nodeMap = map.mapValues { (_, v) -> v.toNode() }
                LLSDMap(nodeMap)
            }
        }
    }

    private fun escapeNotationString(input: String): String {
        val builder = StringBuilder(input.length)
        for (ch in input) {
            when (ch) {
                '\\' -> builder.append("\\\\")
                '"' -> builder.append("\\\"")
                '\n' -> builder.append("\\n")
                '\r' -> builder.append("\\r")
                '\t' -> builder.append("\\t")
                else -> builder.append(ch)
            }
        }
        return builder.toString()
    }

    private fun notationDateFormat(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
    }
}
