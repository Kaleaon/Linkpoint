/*
 * LLSD Enhanced Type System - Java implementation based on Second Life/Firestorm C++ code
 *
 * Converted from indra/llcommon/llsd.h
 * Copyright (C) 2010, Linden Research, Inc.
 * Java conversion Copyright (C) 2024
 */

package lindenlab.llsd.viewer;

import java.net.URI;
import java.util.*;
import java.util.UUID;

/**
 * Provides an enhanced type system for LLSD (Linden Lab Structured Data), based
 * on the C++ implementation found in the Second Life viewer.
 * <p>
 * This class serves as a namespace for a collection of nested classes and enums
 * that offer a more detailed and type-safe way to work with LLSD data than the
 * basic Java object representation allows. It includes:
 * <ul>
 *   <li>An extended {@link Type} enum with viewer-specific types like ULONG and UINT.</li>
 *   <li>Utility classes for type detection ({@link TypeDetection}) and classification
 *       ({@link TypeUtils}).</li>
 *   <li>Type-safe builders for creating maps ({@link MapBuilder}) and arrays
 *       ({@link ArrayBuilder}).</li>
 *   <li>A {@link Factory} for convenient construction of LLSD objects.</li>
 *   <li>A collection of useful {@link Constants}.</li>
 * </ul>
 * This class is final and cannot be instantiated.
 *
 * @see <a href="https://github.com/secondlife/viewer/blob/main/indra/llcommon/llsd.h">llsd.h</a>
 */
public final class LLSDViewerTypes {
    
    private LLSDViewerTypes() {
        // Utility class - no instances
    }
    
    /**
     * An enumeration of all supported LLSD data types, including extensions from
     * the Second Life viewer. This provides a more granular type system than
     * the standard LLSD specification.
     */
    public enum Type {
        /** Undefined/null value */
        UNDEFINED(0, "Undefined"),
        
        /** Boolean true/false */
        BOOLEAN(1, "Boolean"),
        
        /** 32-bit signed integer */
        INTEGER(2, "Integer"),
        
        /** 64-bit IEEE 754 floating point */
        REAL(3, "Real"),
        
        /** UTF-8 string */
        STRING(4, "String"),
        
        /** 128-bit UUID */
        UUID(5, "UUID"),
        
        /** Date/time value */
        DATE(6, "Date"),
        
        /** URI string */
        URI(7, "URI"),
        
        /** Binary data */
        BINARY(8, "Binary"),
        
        /** Map/dictionary */
        MAP(9, "Map"),
        
        /** Array/list */
        ARRAY(10, "Array"),
        
        /** A 64-bit unsigned integer (viewer-specific extension). */
        ULONG(11, "ULong"),
        
        /** A 32-bit unsigned integer (viewer-specific extension). */
        UINT(12, "UInt"),
        
        /** A 32-bit single-precision float (viewer-specific extension). */
        FLOAT(13, "Float"),
        
        /** A marker for the end of the type enumeration, not a real data type. */
        TYPE_END(14, "TypeEnd");
        
        private final int value;
        private final String name;
        
        Type(int value, String name) {
            this.value = value;
            this.name = name;
        }
        
        public int getValue() { return value; }
        public String getName() { return name; }
        
        @Override
        public String toString() { return name; }
        
        /**
         * Retrieves a {@code Type} enum constant from its integer value.
         *
         * @param value The integer representation of the type.
         * @return The corresponding {@code Type} enum constant.
         * @throws IllegalArgumentException if the integer value does not match any known type.
         */
        public static Type fromValue(int value) {
            for (Type type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            throw new IllegalArgumentException("Invalid LLSD type value: " + value);
        }
    }
    
    /**
     * A utility class for classifying and comparing LLSD types.
     */
    public static class TypeUtils {
        
        /**
         * Checks if a given {@link Type} is a scalar type (i.e., not a map, array, or undefined).
         *
         * @param type The type to check.
         * @return {@code true} if the type is a scalar, {@code false} otherwise.
         */
        public static boolean isScalar(Type type) {
            return type != Type.MAP && type != Type.ARRAY && type != Type.UNDEFINED;
        }
        
        /**
         * Checks if a given {@link Type} is a container type (i.e., a map or an array).
         *
         * @param type The type to check.
         * @return {@code true} if the type is a container, {@code false} otherwise.
         */
        public static boolean isContainer(Type type) {
            return type == Type.MAP || type == Type.ARRAY;
        }
        
        /**
         * Checks if a given {@link Type} is a numeric type.
         * <p>
         * Numeric types include INTEGER, REAL, BOOLEAN, UINT, ULONG, and FLOAT.
         *
         * @param type The type to check.
         * @return {@code true} if the type is numeric, {@code false} otherwise.
         */
        public static boolean isNumeric(Type type) {
            return type == Type.INTEGER || type == Type.REAL || 
                   type == Type.BOOLEAN || type == Type.UINT || 
                   type == Type.ULONG || type == Type.FLOAT;
        }
        
        /**
         * Checks if a given {@link Type} can be meaningfully converted to a string.
         * <p>
         * This includes all numeric types as well as STRING, UUID, DATE, and URI.
         *
         * @param type The type to check.
         * @return {@code true} if the type can be converted to a string.
         */
        public static boolean isStringConvertible(Type type) {
            return type == Type.STRING || type == Type.BOOLEAN || 
                   type == Type.INTEGER || type == Type.REAL ||
                   type == Type.UUID || type == Type.DATE ||
                   type == Type.URI || type == Type.UINT ||
                   type == Type.ULONG || type == Type.FLOAT;
        }
        
        /**
         * Gets the natural ordering priority for a type, used for comparisons.
         * <p>
         * Lower values indicate a higher priority (e.g., UNDEFINED is highest).
         *
         * @param type The type to get the priority for.
         * @return An integer representing the type's priority.
         */
        public static int getTypePriority(Type type) {
            switch (type) {
                case UNDEFINED: return 0;
                case BOOLEAN: return 1;
                case INTEGER: return 2;
                case UINT: return 3;
                case ULONG: return 4;
                case FLOAT: return 5;
                case REAL: return 6;
                case STRING: return 7;
                case UUID: return 8;
                case DATE: return 9;
                case URI: return 10;
                case BINARY: return 11;
                case ARRAY: return 12;
                case MAP: return 13;
                default: return Integer.MAX_VALUE;
            }
        }
    }
    
    /**
     * A utility class for detecting the LLSD type of a given Java object.
     */
    public static class TypeDetection {
        
        /**
         * Detects the most appropriate LLSD {@link Type} for a given Java object.
         * <p>
         * This method uses {@code instanceof} checks to determine the type. Note
         * that it makes certain assumptions, such as mapping {@link Long} to
         * {@link Type#ULONG}. Unknown object types default to {@link Type#STRING}.
         *
         * @param obj The object whose LLSD type is to be detected.
         * @return The detected {@link Type}.
         */
        public static Type detectType(Object obj) {
            if (obj == null) {
                return Type.UNDEFINED;
            } else if (obj instanceof Boolean) {
                return Type.BOOLEAN;
            } else if (obj instanceof Integer) {
                return Type.INTEGER;
            } else if (obj instanceof Long) {
                return Type.ULONG;  // Assume long values are unsigned in viewer context
            } else if (obj instanceof Float) {
                return Type.FLOAT;
            } else if (obj instanceof Double) {
                return Type.REAL;
            } else if (obj instanceof String) {
                return Type.STRING;
            } else if (obj instanceof java.util.UUID) {
                return Type.UUID;
            } else if (obj instanceof Date) {
                return Type.DATE;
            } else if (obj instanceof URI) {
                return Type.URI;
            } else if (obj instanceof byte[]) {
                return Type.BINARY;
            } else if (obj instanceof List) {
                return Type.ARRAY;
            } else if (obj instanceof Map) {
                return Type.MAP;
            } else {
                // Default to string for unknown types
                return Type.STRING;
            }
        }
        
        /**
         * Checks if a Java object corresponds to a specific LLSD {@link Type}.
         *
         * @param obj          The object to check.
         * @param expectedType The LLSD type to check against.
         * @return {@code true} if the detected type of the object matches the
         *         expected type, {@code false} otherwise.
         */
        public static boolean matchesType(Object obj, Type expectedType) {
            return detectType(obj) == expectedType;
        }
        
        /**
         * Validates whether a Java object can be safely converted to a target LLSD type.
         * <p>
         * This method checks the convertibility based on rules from the C++
         * viewer implementation (e.g., any numeric type can be converted to
         * another numeric type, but binary can only be converted to binary).
         *
         * @param obj        The object to validate.
         * @param targetType The target LLSD type for the conversion.
         * @return {@code true} if the conversion is considered safe, {@code false} otherwise.
         */
        public static boolean canConvertTo(Object obj, Type targetType) {
            Type sourceType = detectType(obj);
            
            if (sourceType == targetType) {
                return true;
            }
            
            // Check conversion rules from C++ implementation
            switch (targetType) {
                case STRING:
                    return TypeUtils.isStringConvertible(sourceType);
                    
                case BOOLEAN:
                case INTEGER:
                case REAL:
                case FLOAT:
                case UINT:
                case ULONG:
                    return TypeUtils.isNumeric(sourceType) || sourceType == Type.STRING;
                    
                case UUID:
                    return sourceType == Type.STRING;
                    
                case DATE:
                    return sourceType == Type.STRING;
                    
                case URI:
                    return sourceType == Type.STRING;
                    
                case BINARY:
                    return false; // Binary only converts to/from Binary
                    
                default:
                    return false;
            }
        }
    }
    
    /**
     * A type-safe builder for creating LLSD arrays (represented as {@code List<Object>}).
     * <p>
     * This class provides a fluent API for constructing arrays, ensuring that
     * the final result is an immutable list.
     */
    public static class ArrayBuilder {
        private final List<Object> items = new ArrayList<>();
        
        /**
         * Adds a single item to the array.
         *
         * @param item The item to add.
         * @return This {@code ArrayBuilder} instance for method chaining.
         */
        public ArrayBuilder add(Object item) {
            items.add(item);
            return this;
        }
        
        /**
         * Adds multiple items to the array.
         *
         * @param items A varargs array of items to add.
         * @return This {@code ArrayBuilder} instance for method chaining.
         */
        public ArrayBuilder addAll(Object... items) {
            Collections.addAll(this.items, items);
            return this;
        }
        
        /**
         * Builds the final, immutable list from the items added to the builder.
         *
         * @return An unmodifiable {@link List} representing the LLSD array.
         */
        public List<Object> build() {
            return Collections.unmodifiableList(new ArrayList<>(items));
        }
        
        /**
         * Gets the current number of items in the builder.
         *
         * @return The size of the array being built.
         */
        public int size() {
            return items.size();
        }
    }
    
    /**
     * A type-safe builder for creating LLSD maps (represented as {@code Map<String, Object>}).
     * <p>
     * This class provides a fluent API for constructing maps and returns an
     * immutable map upon completion.
     */
    public static class MapBuilder {
        private final Map<String, Object> entries = new HashMap<>();
        
        /**
         * Adds or updates a key-value pair in the map.
         *
         * @param key   The string key for the entry.
         * @param value The object value for the entry.
         * @return This {@code MapBuilder} instance for method chaining.
         */
        public MapBuilder put(String key, Object value) {
            entries.put(key, value);
            return this;
        }
        
        /**
         * Adds all key-value pairs from another map to this builder.
         *
         * @param map The map containing the entries to add.
         * @return This {@code MapBuilder} instance for method chaining.
         */
        public MapBuilder putAll(Map<String, Object> map) {
            entries.putAll(map);
            return this;
        }
        
        /**
         * Removes an entry from the map by its key.
         *
         * @param key The key of the entry to remove.
         * @return This {@code MapBuilder} instance for method chaining.
         */
        public MapBuilder remove(String key) {
            entries.remove(key);
            return this;
        }
        
        /**
         * Checks if the map being built contains the specified key.
         *
         * @param key The key to check for.
         * @return {@code true} if the key exists, {@code false} otherwise.
         */
        public boolean containsKey(String key) {
            return entries.containsKey(key);
        }
        
        /**
         * Builds the final, immutable map from the entries added to the builder.
         *
         * @return An unmodifiable {@link Map} representing the LLSD map.
         */
        public Map<String, Object> build() {
            return Collections.unmodifiableMap(new HashMap<>(entries));
        }
        
        /**
         * Gets the current number of entries in the builder.
         *
         * @return The size of the map being built.
         */
        public int size() {
            return entries.size();
        }
    }
    
    /**
     * A factory class providing convenient static methods for creating LLSD
     * builders and performing cloning operations.
     */
    public static class Factory {
        
        /**
         * Creates a new, empty {@link ArrayBuilder}.
         *
         * @return A new instance of {@code ArrayBuilder}.
         */
        public static ArrayBuilder array() {
            return new ArrayBuilder();
        }
        
        /**
         * Creates a new {@link ArrayBuilder} pre-populated with the given items.
         *
         * @param items The initial items to add to the array.
         * @return A new instance of {@code ArrayBuilder}.
         */
        public static ArrayBuilder array(Object... items) {
            return new ArrayBuilder().addAll(items);
        }
        
        /**
         * Creates a new, empty {@link MapBuilder}.
         *
         * @return A new instance of {@code MapBuilder}.
         */
        public static MapBuilder map() {
            return new MapBuilder();
        }
        
        /**
         * Creates a new {@link MapBuilder} pre-populated with an initial key-value pair.
         *
         * @param key   The initial key.
         * @param value The initial value.
         * @return A new instance of {@code MapBuilder}.
         */
        public static MapBuilder map(String key, Object value) {
            return new MapBuilder().put(key, value);
        }
        
        /**
         * Creates a deep copy of an LLSD data structure.
         * <p>
         * This is a convenience method that delegates to {@link LLSDViewerUtils#llsdClone(Object)}.
         *
         * @param source The object to be copied.
         * @return A deep copy of the source object.
         */
        public static Object clone(Object source) {
            return LLSDViewerUtils.llsdClone(source);
        }
        
        /**
         * Creates a shallow copy of an LLSD data structure.
         * <p>
         * This is a convenience method that delegates to {@link LLSDViewerUtils#llsdShallow(Object)}.
         *
         * @param source The object to be copied.
         * @return A shallow copy of the source object.
         */
        public static Object shallow(Object source) {
            return LLSDViewerUtils.llsdShallow(source);
        }
    }
    
    /**
     * A collection of commonly used constants in LLSD, including pre-instantiated
     * empty or zero-value objects and safety limits derived from the viewer.
     */
    public static class Constants {
        
        /** Empty string constant */
        public static final String EMPTY_STRING = "";
        
        /** Empty array constant */
        public static final List<Object> EMPTY_ARRAY = Collections.emptyList();
        
        /** Empty map constant */
        public static final Map<String, Object> EMPTY_MAP = Collections.emptyMap();
        
        /** Zero integer constant */
        public static final Integer ZERO_INTEGER = 0;
        
        /** Zero real constant */
        public static final Double ZERO_REAL = 0.0;
        
        /** False boolean constant */
        public static final Boolean FALSE_BOOLEAN = Boolean.FALSE;
        
        /** Null UUID constant */
        public static final java.util.UUID NULL_UUID = new java.util.UUID(0L, 0L);
        
        /** Unix epoch date constant */
        public static final Date EPOCH_DATE = new Date(0L);
        
        /** Empty binary constant */
        public static final byte[] EMPTY_BINARY = new byte[0];
        
        /** Maximum safe integer value for LLSD */
        public static final int MAX_SAFE_INTEGER = Integer.MAX_VALUE;
        
        /** Minimum safe integer value for LLSD */
        public static final int MIN_SAFE_INTEGER = Integer.MIN_VALUE;
        
        /** The maximum safe string length, derived from viewer limits. */
        public static final int MAX_SAFE_STRING_LENGTH = 65535;
        
        /** The maximum safe array size, derived from viewer limits. */
        public static final int MAX_SAFE_ARRAY_SIZE = 65535;
        
        /** The maximum safe map size, derived from viewer limits. */
        public static final int MAX_SAFE_MAP_SIZE = 65535;
        
        /** The maximum safe binary size (16MB), derived from viewer limits. */
        public static final int MAX_SAFE_BINARY_SIZE = 16 * 1024 * 1024;
    }
}