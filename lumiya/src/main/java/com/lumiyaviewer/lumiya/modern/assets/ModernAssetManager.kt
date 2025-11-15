package com.lumiyaviewer.lumiya.modern.assets

import android.content.Context
import android.os.SystemClock
import android.util.Log
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.min

/**
 * Modern asset streaming manager with intelligent caching, LOD support, and priority-based loading
 * Implements advanced asset management features for mobile virtual world clients
 */
class ModernAssetManager(private val context: Context) {
    
    private val TAG = "ModernAssetManager"
    
    // Cache size limits (256MB total as per specifications)
    private val MAX_CACHE_SIZE = 256 * 1024 * 1024L // 256MB
    private val MAX_CACHE_ENTRIES = 1000
    
    private val loadingExecutor: ExecutorService
    private val assetCache = ConcurrentHashMap<String, CachedAsset>()
    private val totalCacheSize = AtomicLong(0)
    private val networkMonitor = NetworkQualityMonitor()
    private val priorityQueue = AssetPriorityQueue()
    
    // Quality settings
    @Volatile private var currentQuality = QualityLevel.MEDIUM
    private var adaptiveQuality = true
    
    /**
     * Asset loading priority levels
     */
    enum class AssetPriority(val value: Int) {
        CRITICAL(0),    // UI, avatar textures - load immediately
        HIGH(1),        // Nearby objects - high priority
        NORMAL(2),      // General world content - normal priority  
        LOW(3)          // Distant objects, cached content - low priority
    }
    
    /**
     * Quality levels for LOD system
     */
    enum class QualityLevel(val scaleFactor: Float) {
        ULTRA(1.0f),    // Full resolution
        HIGH(0.75f),    // 75% resolution
        MEDIUM(0.5f),   // 50% resolution  
        LOW(0.25f)      // 25% resolution
    }
    
    /**
     * Asset type enumeration
     */
    enum class AssetType {
        TEXTURE, MESH, ANIMATION, SOUND
    }
    
    /**
     * Enhanced asset data with metadata
     */
    data class AssetData(
        val id: String,
        val type: AssetType,
        val data: ByteArray,
        val quality: QualityLevel = QualityLevel.MEDIUM
    ) {
        val loadTime: Long = SystemClock.elapsedRealtime()
        val size: Int get() = data.size
        
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AssetData) return false
            return id == other.id && type == other.type
        }
        
        override fun hashCode(): Int {
            return 31 * id.hashCode() + type.hashCode()
        }
    }
    
    /**
     * Cached asset wrapper with LRU metadata
     */
    private class CachedAsset(val assetData: AssetData) {
        private val EXPIRE_TIME = 30 * 60 * 1000L // 30 minutes
        
        @Volatile private var lastAccess = SystemClock.elapsedRealtime()
        private val cacheTime = lastAccess
        
        fun updateAccessTime() {
            lastAccess = SystemClock.elapsedRealtime()
        }
        
        fun isExpired(): Boolean {
            return SystemClock.elapsedRealtime() - cacheTime > EXPIRE_TIME
        }
        
        fun getLastAccess(): Long = lastAccess
    }
    
    /**
     * Priority-based asset loading task
     */
    private inner class PrioritizedAssetTask(
        private val assetId: String,
        private val type: AssetType,
        private val priority: AssetPriority,
        private val quality: QualityLevel,
        private val cacheKey: String
    ) : Runnable, Comparable<PrioritizedAssetTask> {
        
        private val submitTime = SystemClock.elapsedRealtime()
        
        override fun compareTo(other: PrioritizedAssetTask): Int {
            // Higher priority first (lower value = higher priority)
            val priorityComp = priority.value.compareTo(other.priority.value)
            if (priorityComp != 0) return priorityComp
            
            // Then by submit time (FIFO within same priority)
            return submitTime.compareTo(other.submitTime)
        }
        
        override fun run() {
            // Placeholder for Runnable implementation
        }
        
        fun get(): AssetData? {
            return try {
                val data = loadAssetData(assetId, type, quality)
                data?.let { cacheAsset(cacheKey, it) }
                data
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load prioritized asset: $assetId", e)
                throw e
            }
        }
    }
    
    /**
     * Network quality monitor for adaptive streaming
     */
    private inner class NetworkQualityMonitor {
        @Volatile private var currentBandwidth = 1024 * 1024L // 1MB/s default
        @Volatile private var monitoring = false
        
        fun startMonitoring() {
            monitoring = true
            Log.d(TAG, "Network quality monitoring started")
        }
        
        fun stopMonitoring() {
            monitoring = false
            Log.d(TAG, "Network quality monitoring stopped")
        }
        
        fun getCurrentBandwidth(): Long = currentBandwidth
        
        fun recommendQuality(): QualityLevel {
            if (!monitoring) return currentQuality
            
            return when {
                currentBandwidth > 2 * 1024 * 1024 -> QualityLevel.ULTRA
                currentBandwidth > 1024 * 1024 -> QualityLevel.HIGH
                currentBandwidth > 512 * 1024 -> QualityLevel.MEDIUM
                else -> QualityLevel.LOW
            }
        }
    }
    
    /**
     * Asset priority queue for advanced scheduling
     */
    private class AssetPriorityQueue {
        fun addRequest(assetId: String, priority: AssetPriority) {
            // Implementation would handle queue ordering
        }
    }
    
    /**
     * Cache statistics
     */
    data class CacheStats(
        val entryCount: Int,
        val totalSize: Long,
        val maxSize: Long,
        val networkBandwidth: Long,
        val currentQuality: QualityLevel
    ) {
        val cacheUtilization: Float
            get() = if (maxSize > 0) totalSize.toFloat() / maxSize else 0f
    }

    init {
        loadingExecutor = createPriorityExecutor()
        
        // Start background cache management
        startCacheManagement()
        
        Log.i(TAG, "Modern asset manager initialized with ${MAX_CACHE_SIZE / (1024 * 1024)}MB cache")
    }
    
    /**
     * Create priority-based executor for asset loading
     */
    private fun createPriorityExecutor(): ExecutorService {
        return ThreadPoolExecutor(
            2, // Core threads
            4, // Max threads
            60L, TimeUnit.SECONDS,
            PriorityBlockingQueue<Runnable>()
        ) { r -> Thread(r, "AssetLoader-${System.currentTimeMillis()}") }
    }
    
    /**
     * Load asset with priority and LOD support
     */
    fun loadAsset(assetId: String, type: AssetType, priority: AssetPriority): CompletableFuture<AssetData?> {
        return loadAssetWithLOD(assetId, type, priority, currentQuality)
    }
    
    /**
     * Load asset with automatic priority detection
     */
    fun loadAsset(assetId: String, type: AssetType): CompletableFuture<AssetData?> {
        val priority = determinePriority(assetId, type)
        return loadAssetWithLOD(assetId, type, priority, currentQuality)
    }
    
    /**
     * Load asset with specific LOD quality level
     */
    fun loadAssetWithLOD(
        assetId: String,
        type: AssetType,
        priority: AssetPriority,
        quality: QualityLevel
    ): CompletableFuture<AssetData?> {
        val cacheKey = generateCacheKey(assetId, quality)
        
        // Check cache first
        assetCache[cacheKey]?.let { cached ->
            if (!cached.isExpired()) {
                cached.updateAccessTime() // LRU update
                Log.d(TAG, "Cache hit for $assetId at quality $quality")
                return CompletableFuture.completedFuture(cached.assetData)
            }
        }
        
        // Create prioritized loading task
        val task = PrioritizedAssetTask(assetId, type, priority, quality, cacheKey)
        
        return CompletableFuture.supplyAsync({
            try {
                task.get()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to load asset with LOD: $assetId", e)
                null
            }
        }, loadingExecutor)
    }
    
    /**
     * Set quality level
     */
    fun setQualityLevel(quality: QualityLevel) {
        currentQuality = quality
        Log.i(TAG, "Quality level set to: $quality")
    }
    
    /**
     * Set adaptive quality
     */
    fun setAdaptiveQuality(adaptive: Boolean) {
        adaptiveQuality = adaptive
        if (adaptive) {
            networkMonitor.startMonitoring()
        } else {
            networkMonitor.stopMonitoring()
        }
        Log.i(TAG, "Adaptive quality ${if (adaptive) "enabled" else "disabled"}")
    }
    
    /**
     * Get current cache statistics
     */
    fun getCacheStats(): CacheStats {
        return CacheStats(
            assetCache.size,
            totalCacheSize.get(),
            MAX_CACHE_SIZE,
            networkMonitor.getCurrentBandwidth(),
            currentQuality
        )
    }
    
    /**
     * Clear cache and free memory
     */
    fun clearCache() {
        assetCache.clear()
        totalCacheSize.set(0)
        Log.i(TAG, "Asset cache cleared")
    }
    
    // Private helper methods
    
    private fun generateCacheKey(assetId: String, quality: QualityLevel): String {
        return "${assetId}_${quality.name}"
    }
    
    private fun determinePriority(assetId: String, type: AssetType): AssetPriority {
        // Auto-detect priority based on asset characteristics
        return when {
            assetId.contains("avatar") || assetId.contains("ui") -> AssetPriority.CRITICAL
            type == AssetType.TEXTURE && assetId.contains("nearby") -> AssetPriority.HIGH
            else -> AssetPriority.NORMAL
        }
    }
    
    private fun loadAssetData(assetId: String, type: AssetType, quality: QualityLevel): AssetData? {
        return try {
            Log.d(TAG, "Loading $assetId at quality $quality (${quality.scaleFactor}x)")
            
            // Simulate network delay based on quality
            val delay = (500 * quality.scaleFactor).toLong()
            Thread.sleep(delay)
            
            // Generate asset data with quality-based size
            val baseSize = getBaseSizeForType(type)
            val scaledSize = (baseSize * quality.scaleFactor).toInt()
            val data = ByteArray(scaledSize) { (it % 256).toByte() }
            
            Log.d(TAG, "Loaded $assetId: $scaledSize bytes at quality $quality")
            AssetData(assetId, type, data, quality)
            
        } catch (e: InterruptedException) {
            Log.w(TAG, "Asset loading interrupted: $assetId")
            Thread.currentThread().interrupt()
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load asset: $assetId", e)
            null
        }
    }
    
    private fun getBaseSizeForType(type: AssetType): Int {
        return when (type) {
            AssetType.TEXTURE -> 256 * 256 * 4 // 256KB base texture
            AssetType.MESH -> 64 * 1024 // 64KB base mesh
            AssetType.ANIMATION -> 32 * 1024 // 32KB base animation
            AssetType.SOUND -> 128 * 1024 // 128KB base sound
        }
    }
    
    private fun cacheAsset(cacheKey: String, assetData: AssetData) {
        val cached = CachedAsset(assetData)
        
        // Check cache size limits
        val newSize = totalCacheSize.addAndGet(assetData.size.toLong())
        
        if (newSize > MAX_CACHE_SIZE || assetCache.size >= MAX_CACHE_ENTRIES) {
            evictLRUAssets()
        }
        
        assetCache[cacheKey] = cached
        Log.d(TAG, "Cached ${assetData.id} (${assetData.size} bytes)")
    }
    
    private fun evictLRUAssets() {
        Log.d(TAG, "Cache eviction triggered - size: ${totalCacheSize.get() / 1024}KB, entries: ${assetCache.size}")
        
        // Find LRU assets to evict
        val entries = assetCache.entries.toList()
            .sortedBy { it.value.getLastAccess() }
        
        // Evict oldest 25% of cache
        val evictCount = (entries.size / 4).coerceAtLeast(1)
        
        entries.take(evictCount).forEach { entry ->
            assetCache.remove(entry.key)
            totalCacheSize.addAndGet(-entry.value.assetData.size.toLong())
        }
        
        Log.d(TAG, "Evicted $evictCount assets. New size: ${totalCacheSize.get() / 1024}KB")
    }
    
    private fun startCacheManagement() {
        // Background thread for cache maintenance
        val cacheManager = Thread({
            while (!Thread.currentThread().isInterrupted) {
                try {
                    Thread.sleep(60000) // Check every minute
                    
                    // Remove expired assets
                    val expired = assetCache.entries
                        .filter { it.value.isExpired() }
                        .map { it.key }
                    
                    expired.forEach { key ->
                        assetCache.remove(key)?.let { removed ->
                            totalCacheSize.addAndGet(-removed.assetData.size.toLong())
                        }
                    }
                    
                    if (expired.isNotEmpty()) {
                        Log.d(TAG, "Removed ${expired.size} expired cache entries")
                    }
                    
                    // Adaptive quality adjustment
                    if (adaptiveQuality) {
                        val recommended = networkMonitor.recommendQuality()
                        if (recommended != currentQuality) {
                            Log.i(TAG, "Adaptive quality change: $currentQuality -> $recommended")
                            currentQuality = recommended
                        }
                    }
                    
                } catch (e: InterruptedException) {
                    Thread.currentThread().interrupt()
                    break
                }
            }
        }, "CacheManager")
        
        cacheManager.isDaemon = true
        cacheManager.start()
    }
    
    /**
     * Clean up resources
     */
    fun shutdown() {
        if (!loadingExecutor.isShutdown) {
            loadingExecutor.shutdown()
        }
        assetCache.clear()
        Log.i(TAG, "Modern asset manager shut down")
    }
}
