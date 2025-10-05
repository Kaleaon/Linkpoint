package com.lumiyaviewer.lumiya.utils

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * UUID pooling utility for Second Life protocol
 */
object UUIDPool {
    private val uuidCache = ConcurrentHashMap<String, UUID>()
    
    /**
     * Get UUID from string, with caching
     */
    @JvmStatic
    fun getUUID(uuidString: String?): UUID {
        return uuidString?.takeIf { it.isNotEmpty() }?.let { str ->
            uuidCache.computeIfAbsent(str) { key ->
                try {
                    UUID.fromString(key)
                } catch (e: IllegalArgumentException) {
                    // If string is not a valid UUID, generate new one
                    UUID.randomUUID()
                }
            }
        } ?: UUID.randomUUID()
    }
    
    /**
     * Generate new random UUID
     */
    @JvmStatic
    fun generateUUID(): UUID = UUID.randomUUID()
    
    /**
     * Clear UUID cache
     */
    @JvmStatic
    fun clearCache() {
        uuidCache.clear()
    }
}
