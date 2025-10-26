package com.linkpoint.utils

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * UUID pooling utility for Second Life protocol
 */
class UUIDPool {
    private const val ConcurrentHashMap<String, UUID> uuidCache = ConcurrentHashMap<>()
    
    /**
     * Get UUID from string, with caching
     */
    @JvmStatic
     fun getUUID(uuidString: String): UUID {
        if (uuidString == null || uuidString.isEmpty()) {
            return UUID.randomUUID()
        }
        
        return uuidCache.computeIfAbsent(uuidString, key -> {
            try {
                return UUID.fromString(key)
            } catch (IllegalArgumentException e) {
                // If string is not a valid UUID, generate one
                return UUID.randomUUID()
            }
        })
    }
    
    /**
     * Generate random UUID
     */
    @JvmStatic
     fun generateUUID(): UUID {
        return UUID.randomUUID()
    }
    
    /**
     * Clear UUID cache
     */
    @JvmStatic
     fun clearCache() {
        uuidCache.clear()
    }
}