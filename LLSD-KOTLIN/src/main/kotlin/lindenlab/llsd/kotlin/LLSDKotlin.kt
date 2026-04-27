/*
 * LLSD Kotlin - Modern Kotlin implementation of LLSD with DSL support
 *
 * Based on Java LLSD implementation with Kotlin-specific features
 * Copyright (C) 2010, Linden Research, Inc.
 * Kotlin conversion Copyright (C) 2024
 */

package lindenlab.llsd.kotlin

import lindenlab.llsd.LLSD
import lindenlab.llsd.LLSDException
import java.net.URI
import java.time.Instant
import java.util.*

/**
 * A modern, type-safe Kotlin representation of an LLSD value, designed to be
 * used with Kotlin's features like sealed classes, data classes, and DSLs.
 *
 * This sealed class hierarchy represents all possible types of data that can be
 * stored in an LLSD structure. Each specific type is a data class, providing
 * immutability and useful generated methods like `equals()`, `hashCode()`, and `copy()`.
 *
 * @see llsdOf
 * @see llsdArray
 * @see llsdMap
 */
sealed class LLSDValue {
    
    /** Represents an undefined or null LLSD value. */
    object Undefined : LLSDValue()
    
    /**
     * Represents an LLSD boolean value.
     * @property value The underlying [kotlin.Boolean] value.
     */
    data class Boolean(val value: kotlin.Boolean) : LLSDValue()
    
    /**
     * Represents an LLSD integer value.
     * @property value The underlying [Int] value.
     */
    data class Integer(val value: Int) : LLSDValue()
    
    /**
     * Represents an LLSD real (floating-point) value.
     * @property value The underlying [Double] value.
     */
    data class Real(val value: Double) : LLSDValue()
    
    /**
     * Represents an LLSD string value.
     * @property value The underlying [kotlin.String] value.
     */
    data class String(val value: kotlin.String) : LLSDValue()
    
    /**
     * Represents an LLSD UUID value.
     * @property value The underlying [java.util.UUID] value.
     */
    data class UUID(val value: java.util.UUID) : LLSDValue()
    
    /**
     * Represents an LLSD date value.
     * @property value The underlying [Instant] value.
     */
    data class Date(val value: Instant) : LLSDValue()
    
    /**
     * Represents an LLSD URI value.
     * @property value The underlying [java.net.URI] value.
     */
    data class URI(val value: java.net.URI) : LLSDValue()
    
    /**
     * Represents an LLSD binary data value.
     * @property value The underlying [ByteArray].
     */
    data class Binary(val value: ByteArray) : LLSDValue() {
        override fun equals(other: Any?): kotlin.Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            other as Binary
            return value.contentEquals(other.value)
        }
        
        override fun hashCode(): Int = value.contentHashCode()
    }
    
    /**
     * Represents an LLSD array, which is a mutable list of [LLSDValue] objects.
     * @property values The underlying [MutableList] holding the array elements.
     */
    data class Array(val values: MutableList<LLSDValue> = mutableListOf()) : LLSDValue() {
        
        constructor(vararg values: LLSDValue) : this(values.toMutableList())
        
        /**
         * Gets the value at the specified index, returning a default value if the
         * index is out of bounds.
         * @param index The index of the element to retrieve.
         * @param default The value to return if the index is out of bounds.
         * @return The [LLSDValue] at the given index, or the default value.
         */
        operator fun get(index: Int, default: LLSDValue = Undefined): LLSDValue {
            return values.getOrElse(index) { default }
        }
        
        /**
         * Sets the value at the specified index.
         * If the index is out of bounds, the array will be automatically expanded
         * with [LLSDValue.Undefined] placeholders.
         * @param index The index to set.
         * @param value The value to place at the index.
         */
        operator fun set(index: Int, value: LLSDValue) {
            // Expand array if necessary
            while (values.size <= index) {
                values.add(Undefined)
            }
            values[index] = value
        }
        
        /**
         * Appends a value to the end of the array.
         * @param value The [LLSDValue] to add.
         * @return This [Array] instance for method chaining.
         */
        fun add(value: LLSDValue): Array {
            values.add(value)
            return this
        }
        
        /**
         * Appends multiple values to the end of the array.
         * @param values The values to add.
         * @return This [Array] instance for method chaining.
         */
        fun addAll(vararg values: LLSDValue): Array {
            this.values.addAll(values)
            return this
        }
        
        /**
         * Size of array
         */
        val size: Int get() = values.size
        
        /**
         * Check if empty
         */
        fun isEmpty(): kotlin.Boolean = values.isEmpty()
        
        /**
         * Iterate over values
         */
        fun forEach(action: (LLSDValue) -> Unit) = values.forEach(action)
        
        /**
         * Map values
         */
        fun <T> map(transform: (LLSDValue) -> T): List<T> = values.map(transform)
        
        /**
         * Filter values
         */
        fun filter(predicate: (LLSDValue) -> kotlin.Boolean): List<LLSDValue> = values.filter(predicate)
    }
    
    /**
     * Represents an LLSD map, which is a mutable map from [String] to [LLSDValue].
     * @property values The underlying [MutableMap] holding the key-value pairs.
     */
    data class Map(val values: MutableMap<kotlin.String, LLSDValue> = mutableMapOf()) : LLSDValue() {
        
        constructor(vararg pairs: Pair<kotlin.String, LLSDValue>) : this(mutableMapOf(*pairs))
        
        /**
         * Gets the value for the specified key, returning a default value if the
         * key is not found.
         * @param key The key to look up.
         * @param default The value to return if the key is not found.
         * @return The [LLSDValue] for the given key, or the default value.
         */
        operator fun get(key: kotlin.String, default: LLSDValue = Undefined): LLSDValue {
            return values[key] ?: default
        }
        
        /**
         * Sets the value for the specified key.
         * @param key The key to set.
         * @param value The value to associate with the key.
         */
        operator fun set(key: kotlin.String, value: LLSDValue) {
            values[key] = value
        }
        
        /**
         * Check if key exists
         */
        fun containsKey(key: kotlin.String): kotlin.Boolean = values.containsKey(key)
        
        /**
         * Remove key
         */
        fun remove(key: kotlin.String): LLSDValue? = values.remove(key)
        
        /**
         * Size of map
         */
        val size: Int get() = values.size
        
        /**
         * Check if empty
         */
        fun isEmpty(): kotlin.Boolean = values.isEmpty()
        
        /**
         * Get all keys
         */
        val keys: Set<kotlin.String> get() = values.keys
        
        /**
         * Iterate over entries
         */
        fun forEach(action: (kotlin.String, LLSDValue) -> Unit) {
            values.forEach { (key, value) -> action(key, value) }
        }
        
        /**
         * Map entries
         */
        fun <T> mapValues(transform: (LLSDValue) -> T): kotlin.collections.Map<kotlin.String, T> {
            return values.mapValues { transform(it.value) }
        }
        
        /**
         * Filter entries
         */
        fun filter(predicate: (kotlin.String, LLSDValue) -> kotlin.Boolean): kotlin.collections.Map<kotlin.String, LLSDValue> {
            return values.filter { predicate(it.key, it.value) }
        }
    }
}

/**
 * A DSL builder for constructing [LLSDValue.Array] instances in a declarative way.
 *
 * Example:
 * ```kotlin
 * val myArray = llsdArray {
 *     add(1)
 *     add("hello")
 *     array {
 *         add(true)
 *     }
 * }
 * ```
 */
@LLSDDslMarker
class LLSDArrayBuilder {
    private val values = mutableListOf<LLSDValue>()
    
    /**
     * Add value to array
     */
    fun add(value: LLSDValue) {
        values.add(value)
    }
    
    /**
     * Add primitive values with automatic conversion
     */
    fun add(value: kotlin.Boolean) = add(LLSDValue.Boolean(value))
    fun add(value: Int) = add(LLSDValue.Integer(value))
    fun add(value: Double) = add(LLSDValue.Real(value))
    fun add(value: kotlin.String) = add(LLSDValue.String(value))
    fun add(value: java.util.UUID) = add(LLSDValue.UUID(value))
    fun add(value: Instant) = add(LLSDValue.Date(value))
    fun add(value: java.net.URI) = add(LLSDValue.URI(value))
    fun add(value: ByteArray) = add(LLSDValue.Binary(value))
    
    /**
     * Add nested array
     */
    fun array(init: LLSDArrayBuilder.() -> Unit) {
        val builder = LLSDArrayBuilder()
        builder.init()
        add(builder.build())
    }
    
    /**
     * Add nested map
     */
    fun map(init: LLSDMapBuilder.() -> Unit) {
        val builder = LLSDMapBuilder()
        builder.init()
        add(builder.build())
    }
    
    /**
     * Operator overload for adding values
     */
    operator fun LLSDValue.unaryPlus() {
        add(this)
    }
    
    operator fun kotlin.Boolean.unaryPlus() = add(this)
    operator fun kotlin.String.unaryPlus() = add(this)
    operator fun java.util.UUID.unaryPlus() = add(this)
    operator fun Instant.unaryPlus() = add(this)
    operator fun java.net.URI.unaryPlus() = add(this)
    operator fun ByteArray.unaryPlus() = add(this)
    
    fun build(): LLSDValue.Array = LLSDValue.Array(values)
}

/**
 * A DSL builder for constructing [LLSDValue.Map] instances in a declarative way.
 *
 * Example:
 * ```kotlin
 * val myMap = llsdMap {
 *     "name" to "John"
 *     "age" to 30
 *     "items".array {
 *         add("one")
 *         add("two")
 *     }
 * }
 * ```
 */
@LLSDDslMarker
class LLSDMapBuilder {
    private val values = mutableMapOf<kotlin.String, LLSDValue>()
    
    /**
     * Set key-value pair
     */
    infix fun kotlin.String.to(value: LLSDValue) {
        values[this] = value
    }
    
    /**
     * Automatic conversion for primitive types
     */
    infix fun kotlin.String.to(value: kotlin.Boolean) = this to LLSDValue.Boolean(value)
    infix fun kotlin.String.to(value: Int) = this to LLSDValue.Integer(value)
    infix fun kotlin.String.to(value: Double) = this to LLSDValue.Real(value)
    infix fun kotlin.String.to(value: kotlin.String) = this to LLSDValue.String(value)
    infix fun kotlin.String.to(value: java.util.UUID) = this to LLSDValue.UUID(value)
    infix fun kotlin.String.to(value: Instant) = this to LLSDValue.Date(value)
    infix fun kotlin.String.to(value: java.net.URI) = this to LLSDValue.URI(value)
    infix fun kotlin.String.to(value: ByteArray) = this to LLSDValue.Binary(value)
    
    /**
     * Set value directly
     */
    operator fun set(key: kotlin.String, value: LLSDValue) {
        values[key] = value
    }
    
    /**
     * Nested array builder
     */
    fun kotlin.String.array(init: LLSDArrayBuilder.() -> Unit) {
        val builder = LLSDArrayBuilder()
        builder.init()
        values[this] = builder.build()
    }
    
    /**
     * Nested map builder
     */
    fun kotlin.String.map(init: LLSDMapBuilder.() -> Unit) {
        val builder = LLSDMapBuilder()
        builder.init()
        values[this] = builder.build()
    }
    
    fun build(): LLSDValue.Map = LLSDValue.Map(values)
}

/**
 * A DSL marker annotation to prevent improper nesting of LLSD builders.
 * This ensures that, for example, an `array` block inside a `map` block cannot
 * access the `map` block's methods.
 */
@DslMarker
annotation class LLSDDslMarker

/**
 * Top-level DSL functions for creating LLSD structures
 */

/**
 * Creates an [LLSDValue.Array] using a type-safe DSL.
 *
 * @param init A lambda with receiver for [LLSDArrayBuilder] where the array
 *             contents are defined.
 * @return The newly constructed [LLSDValue.Array].
 */
fun llsdArray(init: LLSDArrayBuilder.() -> Unit): LLSDValue.Array {
    val builder = LLSDArrayBuilder()
    builder.init()
    return builder.build()
}

/**
 * Creates an [LLSDValue.Map] using a type-safe DSL.
 *
 * @param init A lambda with receiver for [LLSDMapBuilder] where the map
 *             contents are defined.
 * @return The newly constructed [LLSDValue.Map].
 */
fun llsdMap(init: LLSDMapBuilder.() -> Unit): LLSDValue.Map {
    val builder = LLSDMapBuilder()
    builder.init()
    return builder.build()
}

/**
 * Creates an [LLSDValue] by converting a standard Kotlin/Java object.
 *
 * This function attempts to find the most appropriate [LLSDValue] type for the
 * given input. It handles primitives, strings, collections, and maps. Unknown
 * types are converted to their string representation.
 *
 * @param value The object to convert.
 * @return The corresponding [LLSDValue].
 */
fun llsdOf(value: Any?): LLSDValue = when (value) {
    null -> LLSDValue.Undefined
    is kotlin.Boolean -> LLSDValue.Boolean(value)
    is Int -> LLSDValue.Integer(value)
    is Long -> LLSDValue.Integer(value.toInt())
    is Float -> LLSDValue.Real(value.toDouble())
    is Double -> LLSDValue.Real(value)
    is kotlin.String -> LLSDValue.String(value)
    is java.util.UUID -> LLSDValue.UUID(value)
    is Instant -> LLSDValue.Date(value)
    is java.util.Date -> LLSDValue.Date(value.toInstant())
    is java.net.URI -> LLSDValue.URI(value)
    is ByteArray -> LLSDValue.Binary(value)
    is List<*> -> {
        val array = LLSDValue.Array()
        value.forEach { item ->
            array.add(llsdOf(item))
        }
        array
    }
    is kotlin.collections.Map<*, *> -> {
        val map = LLSDValue.Map()
        value.forEach { (key, v) ->
            if (key is kotlin.String) {
                map[key] = llsdOf(v)
            }
        }
        map
    }
    is LLSDValue -> value
    else -> LLSDValue.String(value.toString())
}

/**
 * Extension functions for type-safe value extraction
 */

/**
 * Safely extracts the value as a [kotlin.Boolean], with a default fallback.
 * Tries to convert from Integer (0 is false, non-zero is true) and String.
 * @param default The value to return if extraction or conversion is not possible.
 * @return The boolean value.
 */
fun LLSDValue.asBoolean(default: kotlin.Boolean = false): kotlin.Boolean = when (this) {
    is LLSDValue.Boolean -> value
    is LLSDValue.Integer -> value != 0
    is LLSDValue.String -> value.toBooleanStrictOrNull() ?: default
    else -> default
}

/**
 * Safely extracts the value as an [Int], with a default fallback.
 * Tries to convert from Real (by truncation), Boolean (true=1, false=0), and String.
 * @param default The value to return if extraction or conversion is not possible.
 * @return The integer value.
 */
fun LLSDValue.asInt(default: Int = 0): Int = when (this) {
    is LLSDValue.Integer -> value
    is LLSDValue.Real -> value.toInt()
    is LLSDValue.Boolean -> if (value) 1 else 0
    is LLSDValue.String -> value.toIntOrNull() ?: default
    else -> default
}

/**
 * Safely extracts the value as a [Double], with a default fallback.
 * Tries to convert from Integer and String.
 * @param default The value to return if extraction or conversion is not possible.
 * @return The double value.
 */
fun LLSDValue.asDouble(default: Double = 0.0): Double = when (this) {
    is LLSDValue.Real -> value
    is LLSDValue.Integer -> value.toDouble()
    is LLSDValue.String -> value.toDoubleOrNull() ?: default
    else -> default
}

/**
 * Safely extracts the value as a [String], with a default fallback.
 * Converts most primitive types to their string representation.
 * @param default The value to return if extraction is not possible.
 * @return The string value.
 */
fun LLSDValue.asString(default: kotlin.String = ""): kotlin.String = when (this) {
    is LLSDValue.String -> value
    is LLSDValue.Integer -> value.toString()
    is LLSDValue.Real -> value.toString()
    is LLSDValue.Boolean -> value.toString()
    is LLSDValue.UUID -> value.toString()
    is LLSDValue.Date -> value.toString()
    is LLSDValue.URI -> value.toString()
    LLSDValue.Undefined -> default
    else -> default
}

/**
 * Safely extracts the value as a [java.util.UUID], with a default fallback.
 * Tries to parse from a String.
 * @param default The value to return if extraction or parsing is not possible.
 * @return The UUID value, or null.
 */
fun LLSDValue.asUUID(default: java.util.UUID? = null): java.util.UUID? = when (this) {
    is LLSDValue.UUID -> value
    is LLSDValue.String -> try { java.util.UUID.fromString(value) } catch (e: Exception) { default }
    else -> default
}

/**
 * Safely extracts the value as an [Instant], with a default fallback.
 * Tries to parse from an ISO 8601 string.
 * @param default The value to return if extraction or parsing is not possible.
 * @return The Instant value, or null.
 */
fun LLSDValue.asDate(default: Instant? = null): Instant? = when (this) {
    is LLSDValue.Date -> value
    is LLSDValue.String -> try { Instant.parse(value) } catch (e: Exception) { default }
    else -> default
}

/**
 * Safely extracts the value as a [java.net.URI], with a default fallback.
 * Tries to parse from a String.
 * @param default The value to return if extraction or parsing is not possible.
 * @return The URI value, or null.
 */
fun LLSDValue.asURI(default: java.net.URI? = null): java.net.URI? = when (this) {
    is LLSDValue.URI -> value
    is LLSDValue.String -> try { java.net.URI(value) } catch (e: Exception) { default }
    else -> default
}

/**
 * Safely extracts the value as a [ByteArray], with a default fallback.
 * Tries to convert from a String.
 * @param default The value to return if extraction or conversion is not possible.
 * @return The byte array.
 */
fun LLSDValue.asByteArray(default: ByteArray = byteArrayOf()): ByteArray = when (this) {
    is LLSDValue.Binary -> value
    is LLSDValue.String -> value.toByteArray()
    else -> default
}

/**
 * Safely casts the value as an [LLSDValue.Array], with a default fallback.
 * @param default The empty array to return if the value is not an array.
 * @return The [LLSDValue.Array].
 */
fun LLSDValue.asArray(default: LLSDValue.Array = LLSDValue.Array()): LLSDValue.Array = when (this) {
    is LLSDValue.Array -> this
    else -> default
}

/**
 * Safely casts the value as an [LLSDValue.Map], with a default fallback.
 * @param default The empty map to return if the value is not a map.
 * @return The [LLSDValue.Map].
 */
fun LLSDValue.asMap(default: LLSDValue.Map = LLSDValue.Map()): LLSDValue.Map = when (this) {
    is LLSDValue.Map -> this
    else -> default
}

/**
 * Provides safe, path-like access to nested arrays.
 *
 * Example: `myArray[0, 1, 2]` to access `myArray.values[0].values[1].values[2]`.
 *
 * @param indices A vararg of indices representing the path into the nested arrays.
 * @return The value at the specified path, or [LLSDValue.Undefined] if the path is invalid.
 */
operator fun LLSDValue.Array.get(vararg indices: Int): LLSDValue {
    var current: LLSDValue = this
    for (index in indices) {
        current = when (current) {
            is LLSDValue.Array -> current[index]
            else -> LLSDValue.Undefined
        }
        if (current == LLSDValue.Undefined) break
    }
    return current
}

/**
 * Provides safe, path-like access to nested maps.
 *
 * Example: `myMap["user", "profile", "name"]`
 *
 * @param keys A vararg of keys representing the path into the nested maps.
 * @return The value at the specified path, or [LLSDValue.Undefined] if the path is invalid.
 */
operator fun LLSDValue.Map.get(vararg keys: kotlin.String): LLSDValue {
    var current: LLSDValue = this
    for (key in keys) {
        current = when (current) {
            is LLSDValue.Map -> current[key]
            else -> LLSDValue.Undefined
        }
        if (current == LLSDValue.Undefined) break
    }
    return current
}

/**
 * Provides safe, path-based access for navigating a mix of nested arrays and maps.
 *
 * Example: `myValue.path("users", 0, "name")`
 *
 * @param elements A vararg of path elements, which can be [Int] for array indices
 *                 or [String] for map keys.
 * @return The value at the specified path, or [LLSDValue.Undefined] if the path is invalid.
 */
fun LLSDValue.path(vararg elements: Any): LLSDValue {
    var current: LLSDValue = this
    for (element in elements) {
        current = when {
            current is LLSDValue.Array && element is Int -> current[element]
            current is LLSDValue.Map && element is kotlin.String -> current[element]
            else -> LLSDValue.Undefined
        }
        if (current == LLSDValue.Undefined) break
    }
    return current
}

/**
 * Converts this Kotlin [LLSDValue] into its equivalent Java [LLSD] representation.
 *
 * This is useful for interoperability with older Java-based LLSD code.
 *
 * @return The equivalent [LLSD] object.
 */
fun LLSDValue.toJavaLLSD(): LLSD {
    return LLSD(toJavaObject())
}

private fun LLSDValue.toJavaObject(): Any? = when (this) {
    LLSDValue.Undefined -> null
    is LLSDValue.Boolean -> value
    is LLSDValue.Integer -> value
    is LLSDValue.Real -> value
    is LLSDValue.String -> value
    is LLSDValue.UUID -> value
    is LLSDValue.Date -> java.util.Date.from(value)
    is LLSDValue.URI -> value
    is LLSDValue.Binary -> value
    is LLSDValue.Array -> values.map { it.toJavaObject() }.toList()
    is LLSDValue.Map -> values.mapValues { it.value.toJavaObject() }.toMap()
}

/**
 * Converts a Java [LLSD] object into its equivalent Kotlin [LLSDValue] representation.
 *
 * @return The equivalent [LLSDValue] object.
 */
fun LLSD.toKotlinLLSD(): LLSDValue {
    return llsdOf(this.getContent())
}

/**
 * Creates a formatted, indented string representation of the [LLSDValue].
 *
 * @param indent The starting indentation level.
 * @return A pretty-printed string.
 */
fun LLSDValue.toPrettyString(indent: Int = 0): kotlin.String {
    val indentStr = "  ".repeat(indent)
    return when (this) {
        LLSDValue.Undefined -> "undefined"
        is LLSDValue.Boolean -> value.toString()
        is LLSDValue.Integer -> value.toString()
        is LLSDValue.Real -> value.toString()
        is LLSDValue.String -> "\"$value\""
        is LLSDValue.UUID -> "UUID(\"$value\")"
        is LLSDValue.Date -> "Date(\"$value\")"
        is LLSDValue.URI -> "URI(\"$value\")"
        is LLSDValue.Binary -> "Binary(${value.size} bytes)"
        is LLSDValue.Array -> {
            if (values.isEmpty()) {
                "[]"
            } else {
                val items = values.joinToString(",\n") { "${indentStr}  ${it.toPrettyString(indent + 1)}" }
                "[\n$items\n$indentStr]"
            }
        }
        is LLSDValue.Map -> {
            if (values.isEmpty()) {
                "{}"
            } else {
                val items = values.entries.joinToString(",\n") { 
                    "${indentStr}  \"${it.key}\": ${it.value.toPrettyString(indent + 1)}" 
                }
                "{\n$items\n$indentStr}"
            }
        }
    }
}

/**
 * Performs a deep equality check between two [LLSDValue] objects.
 *
 * This method recursively compares the contents of nested arrays and maps,
 * unlike the standard `equals` which performs a shallow comparison.
 *
 * @param other The other [LLSDValue] to compare against.
 * @return `true` if the values are deeply equal, `false` otherwise.
 */
fun LLSDValue.deepEquals(other: LLSDValue): kotlin.Boolean = when {
    this === other -> true
    this::class != other::class -> false
    this is LLSDValue.Array && other is LLSDValue.Array -> {
        values.size == other.values.size && values.zip(other.values).all { (a, b) -> a.deepEquals(b) }
    }
    this is LLSDValue.Map && other is LLSDValue.Map -> {
        values.size == other.values.size && 
        values.all { (key, value) -> other.values[key]?.let { value.deepEquals(it) } ?: false }
    }
    else -> this == other
}

/**
 * Creates a deep copy of this [LLSDValue].
 *
 * This method recursively copies the contents of nested arrays and maps,
 * resulting in a completely independent new object.
 *
 * @return A deep copy of the [LLSDValue].
 */
fun LLSDValue.deepCopy(): LLSDValue = when (this) {
    LLSDValue.Undefined -> LLSDValue.Undefined
    is LLSDValue.Boolean -> LLSDValue.Boolean(value)
    is LLSDValue.Integer -> LLSDValue.Integer(value)
    is LLSDValue.Real -> LLSDValue.Real(value)
    is LLSDValue.String -> LLSDValue.String(value)
    is LLSDValue.UUID -> LLSDValue.UUID(value)
    is LLSDValue.Date -> LLSDValue.Date(value)
    is LLSDValue.URI -> LLSDValue.URI(value)
    is LLSDValue.Binary -> LLSDValue.Binary(value.copyOf())
    is LLSDValue.Array -> LLSDValue.Array(values.map { it.deepCopy() }.toMutableList())
    is LLSDValue.Map -> LLSDValue.Map(values.mapValues { it.value.deepCopy() }.toMutableMap())
}