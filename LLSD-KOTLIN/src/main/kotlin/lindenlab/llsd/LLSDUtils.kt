/*
 * LLSDJ - LLSD in Java implementation
 *
 * Copyright(C) 2024 - Modernized implementation
 */

package lindenlab.llsd

import java.util.*

/**
 * A utility object providing helper methods for common operations on LLSD data.
 *
 * This object contains static methods that simplify tasks such as:
 * - Navigating nested LLSD structures (maps and arrays) using a simple path syntax.
 * - Safely extracting values of a specific type with default fallbacks.
 * - Performing deep copies of LLSD objects.
 * - Merging LLSD maps.
 * - Validating the presence of required fields.
 * - Pretty-printing LLSD data for debugging.
 *
 * @see LLSD
 */
object LLSDUtils {
    
    /**
     * Shared UUID validation pattern for consistent UUID parsing across all parsers.
     */
    @JvmField
    val UUID_PATTERN = Regex("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")
    
    /**
     * Safely retrieves a string value from a nested LLSD structure.
     *
     * This method navigates through a series of nested [Map] objects using a
     * dot-separated path (e.g., "user.profile.name"). If the path is valid and
     * the final value is a [String], it is returned. Otherwise, the
     * specified default value is returned.
     *
     * @param root         The root of the LLSD data structure (typically a [Map]).
     * @param path         The dot-separated path to the desired value.
     * @param defaultValue The value to return if the path is not found or the
     *                     value is not a string.
     * @return The extracted string or the default value.
     */
    @JvmStatic
    fun getString(root: Any?, path: String, defaultValue: String): String {
        val value = navigatePath(root, path)
        return if (value is String) value else defaultValue
    }
    
    /**
     * Safely retrieves an integer value from a nested LLSD structure.
     */
    @JvmStatic
    fun getInteger(root: Any?, path: String, defaultValue: Int): Int {
        val value = navigatePath(root, path)
        return if (value is Int) value else defaultValue
    }
    
    /**
     * Safely retrieves a double value from a nested LLSD structure.
     *
     * If the value at the specified path is an integer, it will be safely
     * converted to a double.
     */
    @JvmStatic
    fun getDouble(root: Any?, path: String, defaultValue: Double): Double {
        val value = navigatePath(root, path)
        return when (value) {
            is Double -> value
            is Int -> value.toDouble()
            else -> defaultValue
        }
    }
    
    /**
     * Safely retrieves a boolean value from a nested LLSD structure.
     */
    @JvmStatic
    fun getBoolean(root: Any?, path: String, defaultValue: Boolean): Boolean {
        val value = navigatePath(root, path)
        return if (value is Boolean) value else defaultValue
    }
    
    /**
     * Safely retrieves a UUID value from a nested LLSD structure.
     *
     * If the value at the specified path is a string, this method will attempt
     * to parse it as a UUID.
     */
    @JvmStatic
    fun getUUID(root: Any?, path: String, defaultValue: UUID): UUID {
        val value = navigatePath(root, path)
        return when (value) {
            is UUID -> value
            is String -> try {
                UUID.fromString(value)
            } catch (e: IllegalArgumentException) {
                defaultValue
            }
            else -> defaultValue
        }
    }
    
    /**
     * Safely casts an object to a `Map<String, Any?>`.
     *
     * If the object is not an instance of [Map], an empty [HashMap]
     * is returned to prevent [ClassCastException] and null pointer issues.
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun asMap(obj: Any?): Map<String, Any?> {
        return if (obj is Map<*, *>) obj as Map<String, Any?> else HashMap()
    }
    
    /**
     * Safely casts an object to a `List<Any?>`.
     *
     * If the object is not an instance of [List], an empty [ArrayList]
     * is returned.
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun asList(obj: Any?): List<Any?> {
        return if (obj is List<*>) obj as List<Any?> else ArrayList()
    }
    
    /**
     * Checks if an LLSD value is considered "empty".
     *
     * A value is considered empty if it is:
     * - `null`
     * - An instance of [LLSDUndefined]
     * - An empty [String], [Collection], or [Map]
     * - A zero-length byte array
     */
    @JvmStatic
    fun isEmpty(value: Any?): Boolean {
        return when (value) {
            null -> true
            is String -> value.isEmpty()
            is Collection<*> -> value.isEmpty()
            is Map<*, *> -> value.isEmpty()
            is LLSDUndefined -> true
            is ByteArray -> value.isEmpty()
            else -> false
        }
    }
    
    /**
     * Creates a deep copy of an LLSD data structure.
     *
     * This method recursively traverses maps and lists to create a completely
     * independent copy of the original structure. Immutable types (like String,
     * Integer, etc.) are copied by reference, while mutable types (Map, List,
     * byte[]) are duplicated.
     *
     * @param obj The LLSD object (e.g., Map, List, or primitive) to copy.
     * @return A deep copy of the provided object.
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun deepCopy(obj: Any?): Any? {
        return when (obj) {
            null -> null
            is Map<*, *> -> {
                val original = obj as Map<String, Any?>
                val copy = HashMap<String, Any?>()
                for ((key, value) in original) {
                    copy[key] = deepCopy(value)
                }
                copy
            }
            is List<*> -> {
                obj.map { deepCopy(it) }
            }
            is ByteArray -> {
                obj.copyOf()
            }
            else -> obj // Immutable types can be returned as-is
        }
    }
    
    /**
     * Recursively merges two LLSD maps.
     *
     * This method combines the contents of a `source` map into a
     * `target` map. If a key exists in both maps and both corresponding
     * values are maps, it recursively merges them. Otherwise, the value from
     * the source map overwrites the value in the target map.
     *
     * @param target The map to merge into. A new map is created if this is null.
     * @param source The map to merge from.
     * @return A new map containing the merged key-value pairs.
     */
    @JvmStatic
    @Suppress("UNCHECKED_CAST")
    fun mergeMaps(target: Map<String, Any?>?, source: Map<String, Any?>?): Map<String, Any?> {
        if (target == null) {
            return source?.toMap() ?: HashMap()
        }
        if (source == null) {
            return target
        }
        
        val result = HashMap(target)
        
        for ((key, sourceValue) in source) {
            val targetValue = result[key]
            
            result[key] = if (targetValue is Map<*, *> && sourceValue is Map<*, *>) {
                // Recursively merge nested maps
                mergeMaps(targetValue as Map<String, Any?>, sourceValue as Map<String, Any?>)
            } else {
                // Overwrite with source value
                deepCopy(sourceValue)
            }
        }
        
        return result
    }
    
    /**
     * Validates that an LLSD data structure contains a set of required fields.
     *
     * It checks for the presence and non-emptiness of each field specified in
     * `requiredFields`.
     *
     * @param obj            The LLSD object (typically a Map) to validate.
     * @param requiredFields A varargs array of dot-separated paths that must exist.
     * @return A list of the paths that were missing or empty. An empty list
     *         indicates that all required fields were present.
     */
    @JvmStatic
    fun validateRequiredFields(obj: Any?, vararg requiredFields: String): List<String> {
        val missing = mutableListOf<String>()
        
        for (field in requiredFields) {
            if (isEmpty(navigatePath(obj, field))) {
                missing.add(field)
            }
        }
        
        return missing
    }
    
    /**
     * Converts an LLSD data structure into a formatted, human-readable string.
     *
     * @param obj    The LLSD object to format.
     * @param indent The number of spaces to use for each level of indentation.
     * @return A pretty-printed string representation of the object.
     */
    @JvmStatic
    fun prettyPrint(obj: Any?, indent: Int = 2): String {
        val sb = StringBuilder()
        prettyPrintRecursive(obj, indent, 0, sb)
        return sb.toString()
    }
    
    /**
     * Navigates a dot-separated path within a nested LLSD data structure.
     *
     * @param root The root object (should be a Map).
     * @param path The dot-separated path to follow.
     * @return The object at the specified path, or `null` if the path is
     *         invalid or does not exist.
     */
    private fun navigatePath(root: Any?, path: String): Any? {
        if (root == null || path.isEmpty()) {
            return null
        }
        
        val parts = path.split(".")
        var current: Any? = root
        
        for (part in parts) {
            current = if (current is Map<*, *>) {
                @Suppress("UNCHECKED_CAST")
                (current as Map<String, Any?>)[part]
            } else {
                return null // Path doesn't exist
            }
        }
        
        return current
    }
    
    /**
     * The recursive helper method for [prettyPrint].
     */
    @Suppress("UNCHECKED_CAST")
    private fun prettyPrintRecursive(obj: Any?, indentSize: Int, currentLevel: Int, sb: StringBuilder) {
        val indent = " ".repeat(indentSize * currentLevel)
        
        when (obj) {
            null -> sb.append("null")
            is Map<*, *> -> {
                val map = obj as Map<String, Any?>
                sb.append("{\n")
                var first = true
                for ((key, value) in map) {
                    if (!first) sb.append(",\n")
                    first = false
                    sb.append(indent).append("  \"").append(key).append("\": ")
                    prettyPrintRecursive(value, indentSize, currentLevel + 1, sb)
                }
                sb.append("\n").append(indent).append("}")
            }
            is List<*> -> {
                sb.append("[\n")
                var first = true
                for (item in obj) {
                    if (!first) sb.append(",\n")
                    first = false
                    sb.append(indent).append("  ")
                    prettyPrintRecursive(item, indentSize, currentLevel + 1, sb)
                }
                sb.append("\n").append(indent).append("]")
            }
            is String -> sb.append("\"").append(obj).append("\"")
            is ByteArray -> sb.append("binary[").append(obj.size).append(" bytes]")
            else -> sb.append(obj.toString())
        }
    }
}
