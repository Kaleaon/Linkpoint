package com.linkpoint.utils

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * UUID pooling utility for Second Life protocol.
 *
 * Certain protocol responses send the same UUID strings repeatedly. Caching the
 * parsed instances dramatically reduces allocations when parsing large
 * responses (login buddy lists, inventory trees, etc.).
 */
object UUIDPool {

    private val uuidCache = ConcurrentHashMap<String, UUID>()

    /**
     * Obtain a stable [UUID] for the provided [uuidString]. Invalid inputs fall
     * back to a randomly generated UUID so callers never receive null.
     */
    @JvmStatic
    fun getUUID(uuidString: String?): UUID {
        if (uuidString.isNullOrBlank()) {
            return UUID.randomUUID()
        }

        return uuidCache.computeIfAbsent(uuidString) { key ->
            try {
                UUID.fromString(key)
            } catch (_: IllegalArgumentException) {
                UUID.randomUUID()
            }
        }
    }

    /**
     * Generate a brand new random [UUID].
     */
    @JvmStatic
    fun generateUUID(): UUID = UUID.randomUUID()

    /**
     * Clear the internal cache. Primarily useful for tests.
     */
    @JvmStatic
    fun clearCache() {
        uuidCache.clear()
    }
}