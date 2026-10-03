/*
 * Cache Entry - Represents a single cache entry with metadata
 */

package lindenlab.llsd.viewer.secondlife.cache;

/**
 * Represents a single cache entry with metadata for tracking.
 */
public class CacheEntry {
    private final String key;
    private final CacheManager.CacheType type;
    private final long size;
    private final long creationTime;
    private volatile long lastAccessTime;
    private volatile int accessCount;
    
    public CacheEntry(String key, CacheManager.CacheType type, long size, long creationTime) {
        this.key = key;
        this.type = type;
        this.size = size;
        this.creationTime = creationTime;
        this.lastAccessTime = creationTime;
        this.accessCount = 0;
    }
    
    public void updateAccessTime() {
        this.lastAccessTime = System.currentTimeMillis();
        this.accessCount++;
    }
    
    // Getters
    public String getKey() { return key; }
    public CacheManager.CacheType getType() { return type; }
    public long getSize() { return size; }
    public long getCreationTime() { return creationTime; }
    public long getLastAccessTime() { return lastAccessTime; }
    public int getAccessCount() { return accessCount; }
    
    public long getAge() {
        return System.currentTimeMillis() - creationTime;
    }
    
    public long getTimeSinceLastAccess() {
        return System.currentTimeMillis() - lastAccessTime;
    }
    
    @Override
    public String toString() {
        return "CacheEntry{" +
                "key='" + key + '\'' +
                ", type=" + type +
                ", size=" + CacheManager.formatBytes(size) +
                ", age=" + getAge() + "ms" +
                ", accessCount=" + accessCount +
                '}';
    }
}