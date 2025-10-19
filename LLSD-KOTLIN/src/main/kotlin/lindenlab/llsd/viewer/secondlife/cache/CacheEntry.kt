/*
 * Cache Entry - Represents a single cache entry with metadata
 */

package lindenlab.llsd.viewer.secondlife.cache

/**
 * Represents a single cache entry with metadata for tracking.
 */
class CacheEntry(
    val key: String,
    val type: CacheManager.CacheType,
    val size: Long,
    val creationTime: Long
) {
    @Volatile
    private var _lastAccessTime: Long = creationTime
    
    @Volatile
    private var _accessCount: Int = 0
    
    val lastAccessTime: Long
        get() = _lastAccessTime
    
    val accessCount: Int
        get() = _accessCount
    
    fun updateAccessTime() {
        _lastAccessTime = System.currentTimeMillis()
        _accessCount++
    }
    
    fun getAge(): Long {
        return System.currentTimeMillis() - creationTime
    }
    
    fun getTimeSinceLastAccess(): Long {
        return System.currentTimeMillis() - lastAccessTime
    }
    
    override fun toString(): String {
        return "CacheEntry(" +
                "key='$key', " +
                "type=$type, " +
                "size=${CacheManager.formatBytes(size)}, " +
                "age=${getAge()}ms, " +
                "accessCount=$accessCount)"
    }
}
