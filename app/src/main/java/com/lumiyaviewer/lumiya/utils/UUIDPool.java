package com.lumiyaviewer.lumiya.utils;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * UUID pooling utility for Second Life protocol
 */
public class UUIDPool {
    private static final ConcurrentHashMap<String, UUID> uuidCache = new ConcurrentHashMap<>();
    
    /**
     * Get UUID from string, with caching
     */
    public static UUID getUUID(String uuidString) {
        if (uuidString == null || uuidString.isEmpty()) {
            return UUID.randomUUID();
        }
        
        return uuidCache.computeIfAbsent(uuidString, key -> {
            try {
                return UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                // If string is not a valid UUID, generate new one
                return UUID.randomUUID();
            }
        });
    }
    
    /**
     * Generate new random UUID
     */
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
    
    /**
     * Clear UUID cache
     */
    public static void clearCache() {
        uuidCache.clear();
    }
}