package com.linkpoint.protocol.lumiya

import java.lang.ref.WeakReference
import java.util.UUID
import java.util.WeakHashMap

/**
 * UUID Interning Pool (from Lumiya's UUIDPool.java)
 * 
 * Memory optimization that reuses UUID instances across the application.
 * In Second Life, the same UUIDs appear repeatedly (agent IDs, object IDs, etc.)
 * so interning them reduces memory allocation and improves equality checks.
 * 
 * ## How It Works
 * 
 * Uses a WeakHashMap to store UUIDs:
 * - When a UUID is requested, check if we already have an equal instance
 * - If yes, return the existing instance
 * - If no, store the new instance and return it
 * - WeakReferences allow GC to clean up unused UUIDs
 * 
 * ## Benefits
 * 
 * 1. **Memory Savings**: Same UUID referenced multiple times uses single object
 * 2. **Faster Equality**: Identity comparison (===) instead of equals()
 * 3. **GC Friendly**: Unused UUIDs are automatically collected
 * 
 * ## Usage
 * 
 * ```kotlin
 * // Instead of: val uuid = UUID.fromString(str)
 * val uuid = UUIDPool.get(str)
 * 
 * // Instead of: val uuid = UUID(msb, lsb)
 * val uuid = UUIDPool.get(msb, lsb)
 * ```
 */
object UUIDPool {
    
    /** The zero UUID constant (like Lumiya's ZeroUUID) */
    val ZERO: UUID = UUID(0L, 0L)
    
    /** Internal pool using WeakHashMap for automatic cleanup */
    private val pool = WeakHashMap<UUID, WeakReference<UUID>>()
    
    /** Synchronization lock for thread safety */
    private val lock = Any()
    
    /**
     * Get or create an interned UUID from most/least significant bits
     */
    fun get(mostSignificantBits: Long, leastSignificantBits: Long): UUID {
        // Optimize for zero UUID
        if (mostSignificantBits == 0L && leastSignificantBits == 0L) {
            return ZERO
        }
        
        return intern(UUID(mostSignificantBits, leastSignificantBits))
    }
    
    /**
     * Get or create an interned UUID from string
     * Returns null if string is null or empty
     */
    fun get(uuidString: String?): UUID? {
        if (uuidString.isNullOrEmpty()) {
            return null
        }
        
        return try {
            intern(UUID.fromString(uuidString))
        } catch (e: IllegalArgumentException) {
            null
        }
    }
    
    /**
     * Get or create an interned UUID from an existing UUID
     */
    fun get(uuid: UUID): UUID {
        if (uuid == ZERO) {
            return ZERO
        }
        return intern(uuid)
    }
    
    /**
     * Set UUID with interning - returns existing if same bits, or creates new
     * Like Lumiya's setUUID method
     */
    fun set(existing: UUID?, mostSignificantBits: Long, leastSignificantBits: Long): UUID {
        // If existing UUID has same bits, just return it
        if (existing != null && 
            existing.mostSignificantBits == mostSignificantBits && 
            existing.leastSignificantBits == leastSignificantBits) {
            return existing
        }
        
        return get(mostSignificantBits, leastSignificantBits)
    }
    
    /**
     * Internal interning logic
     */
    private fun intern(uuid: UUID): UUID {
        synchronized(lock) {
            // Check if we already have this UUID
            val existing = pool[uuid]?.get()
            if (existing != null) {
                return existing
            }
            
            // Store new UUID
            pool[uuid] = WeakReference(uuid)
            return uuid
        }
    }
    
    /**
     * Get approximate pool size (for debugging)
     */
    fun poolSize(): Int = synchronized(lock) { pool.size }
    
    /**
     * Clear the pool (for testing)
     */
    fun clear() = synchronized(lock) { pool.clear() }
}

/**
 * Extension function to intern a UUID
 */
fun UUID.intern(): UUID = UUIDPool.get(this)

/**
 * Extension function to parse and intern a UUID string
 */
fun String?.toInternedUUID(): UUID? = UUIDPool.get(this)
