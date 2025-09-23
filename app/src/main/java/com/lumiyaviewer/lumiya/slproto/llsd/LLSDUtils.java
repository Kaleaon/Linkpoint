package com.lumiyaviewer.lumiya.slproto.llsd;

import com.lumiyaviewer.lumiya.slproto.llsd.types.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced LLSD utility class demonstrating Java best practices
 * Features:
 * - Null safety with Optional
 * - Builder pattern for complex LLSD structures  
 * - Thread-safe caching for performance
 * - Fluent API design
 * - Proper exception handling
 */
public final class LLSDUtils {
    
    // Thread-safe cache for commonly used LLSD values
    private static final Map<String, LLSDNode> CACHE = new ConcurrentHashMap<>();
    
    // Common LLSD constants
    public static final LLSDString EMPTY_STRING = new LLSDString("");
    public static final LLSDInt ZERO = new LLSDInt(0);
    public static final LLSDUUID NULL_UUID = new LLSDUUID();
    
    private LLSDUtils() {
        // Utility class - prevent instantiation
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    /**
     * Builder for creating LLSD Map structures with fluent API
     */
    public static class MapBuilder {
        private final Map<String, LLSDNode> map = new HashMap<>();
        
        public MapBuilder put(String key, String value) {
            if (key != null) {
                map.put(key, value != null ? new LLSDString(value) : EMPTY_STRING);
            }
            return this;
        }
        
        public MapBuilder put(String key, int value) {
            if (key != null) {
                map.put(key, new LLSDInt(value));
            }
            return this;
        }
        
        public MapBuilder put(String key, UUID value) {
            if (key != null) {
                map.put(key, value != null ? new LLSDUUID(value) : NULL_UUID);
            }
            return this;
        }
        
        public MapBuilder put(String key, LLSDNode value) {
            if (key != null && value != null) {
                map.put(key, value);
            }
            return this;
        }
        
        public LLSDMap build() {
            return new LLSDMap(new HashMap<>(map));
        }
    }
    
    /**
     * Builder for creating LLSD Array structures
     */
    public static class ArrayBuilder {
        private final List<LLSDNode> list = new ArrayList<>();
        
        public ArrayBuilder add(String value) {
            list.add(value != null ? new LLSDString(value) : EMPTY_STRING);
            return this;
        }
        
        public ArrayBuilder add(int value) {
            list.add(new LLSDInt(value));
            return this;
        }
        
        public ArrayBuilder add(UUID value) {
            list.add(value != null ? new LLSDUUID(value) : NULL_UUID);
            return this;
        }
        
        public ArrayBuilder add(LLSDNode value) {
            if (value != null) {
                list.add(value);
            }
            return this;
        }
        
        public LLSDArray build() {
            LLSDArray array = new LLSDArray();
            list.forEach(array::add);
            return array;
        }
    }
    
    // Factory methods with null safety
    public static MapBuilder mapBuilder() {
        return new MapBuilder();
    }
    
    public static ArrayBuilder arrayBuilder() {
        return new ArrayBuilder();
    }
    
    public static Optional<LLSDString> createString(String value) {
        if (value == null) {
            return Optional.empty();
        }
        
        // Cache empty strings to reduce memory usage
        if (value.isEmpty()) {
            return Optional.of(EMPTY_STRING);
        }
        
        return Optional.of(new LLSDString(value));
    }
    
    public static Optional<LLSDUUID> createUUID(UUID value) {
        if (value == null) {
            return Optional.empty();
        }
        
        // Cache null UUIDs
        if (value.equals(new UUID(0L, 0L))) {
            return Optional.of(NULL_UUID);
        }
        
        return Optional.of(new LLSDUUID(value));
    }
    
    public static Optional<LLSDUUID> createUUID(String value) {
        if (value == null || value.trim().isEmpty()) {
            return Optional.empty();
        }
        
        try {
            return Optional.of(new LLSDUUID(value));
        } catch (IllegalArgumentException e) {
            // Invalid UUID string
            return Optional.empty();
        }
    }
    
    public static LLSDInt createInt(int value) {
        // Cache common integer values
        if (value == 0) {
            return ZERO;
        }
        
        String key = "int_" + value;
        return (LLSDInt) CACHE.computeIfAbsent(key, k -> new LLSDInt(value));
    }
    
    /**
     * Safe value extraction with default fallbacks
     */
    public static String safeGetString(LLSDNode node, String defaultValue) {
        if (node == null) {
            return defaultValue;
        }
        
        try {
            return node.asString();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static int safeGetInt(LLSDNode node, int defaultValue) {
        if (node == null) {
            return defaultValue;
        }
        
        try {
            return node.asInt();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    public static UUID safeGetUUID(LLSDNode node, UUID defaultValue) {
        if (node == null) {
            return defaultValue;
        }
        
        try {
            return node.asUUID();
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    /**
     * Performance optimization: clear cache when needed
     */
    public static void clearCache() {
        CACHE.clear();
    }
    
    /**
     * Get cache statistics for monitoring
     */
    public static int getCacheSize() {
        return CACHE.size();
    }
}