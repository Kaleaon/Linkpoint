package com.linkpoint.assets

import android.content.Context
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID

/**
 * Asset caching system with memory and disk levels
 * 
 * Now uses CacheManager for configurable cache sizes:
 * - Memory: 100MB - 2GB (default 512MB)
 * - Disk: 512MB - 10GB (default 2GB)
 */
class AssetCache(private val context: Context) {
    
    companion object {
        private const val TAG = "AssetCache"
        private const val DISK_CACHE_DIR = "asset_cache"
    }
    
    // Cache manager for getting configured sizes
    private val cacheManager by lazy { CacheManager(context) }
    
    // Memory cache (LRU) - uses configured size from CacheManager
    private val memoryCache: LruCache<String, ByteArray> by lazy {
        val configuredMB = cacheManager.getMemoryCacheSizeMB()
        // Clamp the MB value in case a persisted preference is outside the supported range,
        // then compute bytes in Long to avoid Int overflow. LruCache's maxSize is an Int,
        // so at >= 2048 MB the naive Int multiplication wraps to <= 0 and crashes the
        // constructor with "maxSize <= 0".
        val clampedMB = configuredMB.coerceIn(
            CacheManager.MIN_MEMORY_CACHE_MB,
            CacheManager.MAX_MEMORY_CACHE_MB
        )
        val maxBytes = (clampedMB.toLong() * 1024L * 1024L)
            .coerceAtMost(Int.MAX_VALUE.toLong())
            .toInt()
        if (clampedMB != configuredMB) {
            Log.w(TAG, "Memory cache size ${configuredMB}MB out of range; clamped to ${clampedMB}MB")
        }
        Log.i(TAG, "Initializing memory cache: ${maxBytes / 1024 / 1024}MB")
        object : LruCache<String, ByteArray>(maxBytes) {
            override fun sizeOf(key: String, value: ByteArray): Int = value.size
        }
    }
    
    // Fallback disk cache directory for asset types without CacheManager mapping
    private val diskCacheDir: File by lazy {
        File(context.cacheDir, DISK_CACHE_DIR).also { it.mkdirs() }
    }
    
    // Configured disk cache size in bytes
    private val maxDiskCacheBytes: Long
        get() = cacheManager.getDiskCacheSizeMB().toLong() * 1024 * 1024
    
    /**
     * Get an asset from cache (memory first, then disk)
     */
    suspend fun get(assetId: UUID, assetType: AssetType): ByteArray? {
        val key = getCacheKey(assetId, assetType)
        
        // Check memory cache
        memoryCache.get(key)?.let {
            Log.d(TAG, "Memory cache hit: $assetId")
            return it
        }
        
        // Check disk cache
        return withContext(Dispatchers.IO) {
            val file = getDiskFile(assetId, assetType)
            if (file.exists()) {
                try {
                    val data = file.readBytes()
                    // Promote to memory cache
                    memoryCache.put(key, data)
                    Log.d(TAG, "Disk cache hit: $assetId")
                    data
                } catch (e: Exception) {
                    Log.w(TAG, "Failed to read cached asset", e)
                    null
                }
            } else {
                null
            }
        }
    }
    
    /**
     * Store an asset in cache
     */
    suspend fun put(assetId: UUID, assetType: AssetType, data: ByteArray) {
        val key = getCacheKey(assetId, assetType)
        
        // Store in memory
        memoryCache.put(key, data)
        
        // Store on disk using Public/<Grid>/<assetType>/<uuid> structure
        withContext(Dispatchers.IO) {
            try {
                val file = getDiskFile(assetId, assetType)
                file.writeBytes(data)
                Log.d(TAG, "Cached asset: $assetId (${data.size} bytes)")
            } catch (e: Exception) {
                Log.w(TAG, "Failed to cache asset to disk", e)
            }
        }
    }
    
    /**
     * Check if asset exists in cache
     */
    suspend fun contains(assetId: UUID, assetType: AssetType): Boolean {
        val key = getCacheKey(assetId, assetType)
        if (memoryCache.get(key) != null) return true
        return withContext(Dispatchers.IO) {
            getDiskFile(assetId, assetType).exists()
        }
    }
    
    /**
     * Remove an asset from cache
     */
    suspend fun remove(assetId: UUID, assetType: AssetType) {
        val key = getCacheKey(assetId, assetType)
        memoryCache.remove(key)
        withContext(Dispatchers.IO) {
            getDiskFile(assetId, assetType).delete()
        }
    }
    
    /**
     * Clear all caches
     */
    suspend fun clear() {
        memoryCache.evictAll()
        withContext(Dispatchers.IO) {
            // Clear all CacheManager asset directories
            CacheableAssetType.values().forEach { type ->
                cacheManager.getPublicAssetDirectory(type).listFiles()?.forEach { it.delete() }
            }
            // Clear fallback directory
            diskCacheDir.listFiles()?.forEach { it.delete() }
        }
        Log.i(TAG, "Cache cleared")
    }
    
    /**
     * Collect all disk cache files across CacheManager directories and fallback.
     */
    private fun allDiskFiles(): List<File> {
        val files = mutableListOf<File>()
        CacheableAssetType.values().forEach { type ->
            cacheManager.getPublicAssetDirectory(type).listFiles()?.let { files.addAll(it) }
        }
        diskCacheDir.listFiles()?.let { files.addAll(it) }
        return files
    }
    
    /**
     * Get cache statistics
     */
    fun getStats(): CacheStats {
        val allFiles = allDiskFiles()
        val diskSize = allFiles.sumOf { it.length() }
        val diskCount = allFiles.size
        return CacheStats(
            memorySizeBytes = memoryCache.size().toLong(),
            memoryMaxBytes = memoryCache.maxSize().toLong(),
            memoryHitCount = memoryCache.hitCount(),
            memoryMissCount = memoryCache.missCount(),
            diskSizeBytes = diskSize,
            diskAssetCount = diskCount
        )
    }
    
    /**
     * Prune disk cache if over limit
     * Uses configured cache size from CacheManager
     */
    suspend fun pruneIfNeeded() = withContext(Dispatchers.IO) {
        val maxBytes = maxDiskCacheBytes
        val allFiles = allDiskFiles()
        var totalSize = allFiles.sumOf { it.length() }
        
        if (totalSize > maxBytes) {
            Log.i(TAG, "Cache over limit: ${totalSize / 1024 / 1024}MB > ${maxBytes / 1024 / 1024}MB, pruning...")
            // Delete oldest files first
            allFiles
                .sortedBy { it.lastModified() }
                .forEach { file ->
                    if (totalSize > maxBytes * 0.8) { // Prune to 80%
                        val size = file.length()
                        if (file.delete()) {
                            totalSize -= size
                        }
                    }
                }
            Log.i(TAG, "Pruned cache to ${totalSize / 1024 / 1024}MB")
        }
    }
    
    private fun getCacheKey(assetId: UUID, assetType: AssetType): String {
        // Include the grid in the memory key so the in-process LRU cache can't return a
        // payload from a previously-connected grid for the same UUID. Disk paths already
        // segregate by grid via CacheManager.getPublicAssetDirectory(), but the memory
        // cache shared one keyspace before this change — fine for Second Life (UUIDs are
        // globally unique) but unsafe across OpenSim grids where UUIDs can collide.
        return "${cacheManager.getCurrentGridName()}_${assetType.name}_${assetId}"
    }
    
    /**
     * Map AssetType to CacheableAssetType for directory routing.
     */
    private fun toCacheableAssetType(assetType: AssetType): CacheableAssetType? {
        return when (assetType) {
            AssetType.TEXTURE, AssetType.TEXTURE_TGA, AssetType.IMAGE_TGA,
            AssetType.IMAGE_JPEG, AssetType.SNAPSHOT -> CacheableAssetType.TEXTURES
            AssetType.MESH, AssetType.GLTF, AssetType.GLTF_BIN -> CacheableAssetType.MESHES
            AssetType.SOUND, AssetType.SOUND_WAV -> CacheableAssetType.SOUNDS
            AssetType.ANIMATION -> CacheableAssetType.ANIMATIONS
            else -> null
        }
    }
    
    /**
     * Get the disk file for an asset using the Public/<Grid>/<assetType>/<uuid> structure.
     * Assets with a known cacheable type are stored in CacheManager directories;
     * other asset types fall back to the general disk cache.
     */
    private fun getDiskFile(assetId: UUID, assetType: AssetType): File {
        val cacheableType = toCacheableAssetType(assetType)
        val dir = if (cacheableType != null) {
            cacheManager.getPublicAssetDirectory(cacheableType)
        } else {
            diskCacheDir.also { it.mkdirs() }
        }
        return File(dir, assetId.toString())
    }
}

/**
 * Second Life asset types
 */
enum class AssetType(val value: Int) {
    TEXTURE(0),
    SOUND(1),
    CALLING_CARD(2),
    LANDMARK(3),
    SCRIPT(4),          // LSL Text
    CLOTHING(5),
    OBJECT(6),
    NOTECARD(7),
    CATEGORY(8),        // Folder
    LSL_BYTECODE(10),
    TEXTURE_TGA(12),
    BODYPART(13),
    TRASH(14),
    SNAPSHOT(15),
    LOST_AND_FOUND(16),
    SOUND_WAV(17),
    IMAGE_TGA(18),
    IMAGE_JPEG(19),
    ANIMATION(20),
    GESTURE(21),
    SIMSTATE(22),
    LINK(24),
    LINK_FOLDER(25),
    MESH(49),
    WIDGET(40),
    PERSON(45),
    SETTINGS(56),       // EEP settings
    MATERIAL(57),       // PBR material
    GLTF(58),           // GLTF model
    GLTF_BIN(59),       // GLTF binary
    UNKNOWN(-1);
    
    companion object {
        fun fromValue(value: Int): AssetType {
            return values().find { it.value == value } ?: UNKNOWN
        }
    }
}

data class CacheStats(
    val memorySizeBytes: Long,
    val memoryMaxBytes: Long,
    val memoryHitCount: Int,
    val memoryMissCount: Int,
    val diskSizeBytes: Long,
    val diskAssetCount: Int
) {
    val memoryHitRate: Float get() {
        val total = memoryHitCount + memoryMissCount
        return if (total > 0) memoryHitCount.toFloat() / total else 0f
    }
}
