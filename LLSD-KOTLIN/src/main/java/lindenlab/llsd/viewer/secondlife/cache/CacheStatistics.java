/*
 * Cache Statistics - Provides detailed cache performance and usage statistics
 */

package lindenlab.llsd.viewer.secondlife.cache;

import java.util.Map;

/**
 * Immutable container for cache statistics and performance metrics.
 */
public class CacheStatistics {
    private final long totalSize;
    private final long maxSize;
    private final long totalHits;
    private final long totalMisses;
    private final long totalWrites;
    private final long totalCleanups;
    private final Map<CacheManager.CacheType, Long> typeSizes;
    private final Map<CacheManager.CacheType, Long> typeLimits;
    private final CacheManager.StorageLocation storageLocation;
    private final String basePath;
    
    public CacheStatistics(long totalSize, long maxSize, long totalHits, long totalMisses,
                          long totalWrites, long totalCleanups,
                          Map<CacheManager.CacheType, Long> typeSizes,
                          Map<CacheManager.CacheType, Long> typeLimits,
                          CacheManager.StorageLocation storageLocation,
                          String basePath) {
        this.totalSize = totalSize;
        this.maxSize = maxSize;
        this.totalHits = totalHits;
        this.totalMisses = totalMisses;
        this.totalWrites = totalWrites;
        this.totalCleanups = totalCleanups;
        this.typeSizes = typeSizes;
        this.typeLimits = typeLimits;
        this.storageLocation = storageLocation;
        this.basePath = basePath;
    }
    
    // Basic statistics
    public long getTotalSize() { return totalSize; }
    public long getMaxSize() { return maxSize; }
    public long getTotalHits() { return totalHits; }
    public long getTotalMisses() { return totalMisses; }
    public long getTotalWrites() { return totalWrites; }
    public long getTotalCleanups() { return totalCleanups; }
    
    // Derived statistics
    public double getUsagePercent() {
        return maxSize == 0 ? 0.0 : (double) totalSize / maxSize * 100.0;
    }
    
    public double getHitRatio() {
        long total = totalHits + totalMisses;
        return total == 0 ? 0.0 : (double) totalHits / total;
    }
    
    public long getAvailableSpace() {
        return maxSize - totalSize;
    }
    
    public long getTotalRequests() {
        return totalHits + totalMisses;
    }
    
    // Type-specific statistics
    public Map<CacheManager.CacheType, Long> getTypeSizes() { return typeSizes; }
    public Map<CacheManager.CacheType, Long> getTypeLimits() { return typeLimits; }
    
    public long getTypeSize(CacheManager.CacheType type) {
        return typeSizes.getOrDefault(type, 0L);
    }
    
    public long getTypeLimit(CacheManager.CacheType type) {
        return typeLimits.getOrDefault(type, 0L);
    }
    
    public double getTypeUsagePercent(CacheManager.CacheType type) {
        long limit = getTypeLimit(type);
        return limit == 0 ? 0.0 : (double) getTypeSize(type) / limit * 100.0;
    }
    
    // Configuration
    public CacheManager.StorageLocation getStorageLocation() { return storageLocation; }
    public String getBasePath() { return basePath; }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Cache Statistics:\n");
        sb.append("  Total Size: ").append(CacheManager.formatBytes(totalSize))
          .append(" / ").append(CacheManager.formatBytes(maxSize))
          .append(" (").append(String.format("%.1f", getUsagePercent())).append("%)\n");
        sb.append("  Available: ").append(CacheManager.formatBytes(getAvailableSpace())).append("\n");
        sb.append("  Hit Ratio: ").append(String.format("%.2f", getHitRatio() * 100)).append("%\n");
        sb.append("  Requests: ").append(getTotalRequests())
          .append(" (").append(totalHits).append(" hits, ").append(totalMisses).append(" misses)\n");
        sb.append("  Writes: ").append(totalWrites).append("\n");
        sb.append("  Cleanups: ").append(totalCleanups).append("\n");
        sb.append("  Storage: ").append(storageLocation.getDisplayName()).append("\n");
        sb.append("  Path: ").append(basePath).append("\n");
        
        sb.append("\n  Type Breakdown:\n");
        for (CacheManager.CacheType type : CacheManager.CacheType.values()) {
            long size = getTypeSize(type);
            long limit = getTypeLimit(type);
            double percent = getTypeUsagePercent(type);
            sb.append("    ").append(type.name()).append(": ")
              .append(CacheManager.formatBytes(size))
              .append(" / ").append(CacheManager.formatBytes(limit))
              .append(" (").append(String.format("%.1f", percent)).append("%)\n");
        }
        
        return sb.toString();
    }
}