/*
 * Cache Statistics - Provides detailed cache performance and usage statistics
 */

package lindenlab.llsd.viewer.secondlife.cache

/**
 * Immutable container for cache statistics and performance metrics.
 */
data class CacheStatistics(
    val totalSize: Long,
    val maxSize: Long,
    val totalHits: Long,
    val totalMisses: Long,
    val totalWrites: Long,
    val totalCleanups: Long,
    val typeSizes: Map<CacheManager.CacheType, Long>,
    val typeLimits: Map<CacheManager.CacheType, Long>,
    val storageLocation: CacheManager.StorageLocation,
    val basePath: String
) {
    // Derived statistics
    fun getUsagePercent(): Double {
        return if (maxSize == 0L) 0.0 else totalSize.toDouble() / maxSize * 100.0
    }
    
    fun getHitRatio(): Double {
        val total = totalHits + totalMisses
        return if (total == 0L) 0.0 else totalHits.toDouble() / total
    }
    
    fun getAvailableSpace(): Long {
        return maxSize - totalSize
    }
    
    fun getTotalRequests(): Long {
        return totalHits + totalMisses
    }
    
    // Type-specific statistics
    fun getTypeSize(type: CacheManager.CacheType): Long {
        return typeSizes[type] ?: 0L
    }
    
    fun getTypeLimit(type: CacheManager.CacheType): Long {
        return typeLimits[type] ?: 0L
    }
    
    fun getTypeUsagePercent(type: CacheManager.CacheType): Double {
        val limit = getTypeLimit(type)
        return if (limit == 0L) 0.0 else getTypeSize(type).toDouble() / limit * 100.0
    }
    
    override fun toString(): String {
        return buildString {
            append("Cache Statistics:\n")
            append("  Total Size: ${CacheManager.formatBytes(totalSize)} / ${CacheManager.formatBytes(maxSize)}")
            append(" (${String.format("%.1f", getUsagePercent())}%)\n")
            append("  Available: ${CacheManager.formatBytes(getAvailableSpace())}\n")
            append("  Hit Ratio: ${String.format("%.2f", getHitRatio() * 100)}%\n")
            append("  Requests: ${getTotalRequests()} ($totalHits hits, $totalMisses misses)\n")
            append("  Writes: $totalWrites\n")
            append("  Cleanups: $totalCleanups\n")
            append("  Storage: ${storageLocation.displayName}\n")
            append("  Path: $basePath\n")
            
            append("\n  Type Breakdown:\n")
            for (type in CacheManager.CacheType.values()) {
                val size = getTypeSize(type)
                val limit = getTypeLimit(type)
                val percent = getTypeUsagePercent(type)
                append("    ${type.name}: ${CacheManager.formatBytes(size)} / ${CacheManager.formatBytes(limit)}")
                append(" (${String.format("%.1f", percent)}%)\n")
            }
        }
    }
}
