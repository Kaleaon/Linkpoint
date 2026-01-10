package com.linkpoint.assets

import android.content.Context
import android.util.Log
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.MessageDigest
import java.util.UUID

/**
 * Asset caching system with memory and disk levels
 */
class AssetCache(private val context: Context) {
    
    companion object {
        private const val TAG = "AssetCache"
        private const val DISK_CACHE_DIR = "asset_cache"
        private const val MAX_DISK_CACHE_MB = 500
        private const val MAX_MEMORY_CACHE_MB = 50
    }
    
    // Memory cache (LRU)
    private val memoryCache: LruCache<String, ByteArray> = object : LruCache<String, ByteArray>(
        MAX_MEMORY_CACHE_MB * 1024 * 1024
    ) {
        override fun sizeOf(key: String, value: ByteArray): Int = value.size
    }
    
    // Disk cache directory
    private val diskCacheDir: File by lazy {
        File(context.cacheDir, DISK_CACHE_DIR).also { it.mkdirs() }
    }
    
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
            val file = getDiskFile(key)
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
        
        // Store on disk
        withContext(Dispatchers.IO) {
            try {
                val file = getDiskFile(key)
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
            getDiskFile(key).exists()
        }
    }
    
    /**
     * Remove an asset from cache
     */
    suspend fun remove(assetId: UUID, assetType: AssetType) {
        val key = getCacheKey(assetId, assetType)
        memoryCache.remove(key)
        withContext(Dispatchers.IO) {
            getDiskFile(key).delete()
        }
    }
    
    /**
     * Clear all caches
     */
    suspend fun clear() {
        memoryCache.evictAll()
        withContext(Dispatchers.IO) {
            diskCacheDir.listFiles()?.forEach { it.delete() }
        }
        Log.i(TAG, "Cache cleared")
    }
    
    /**
     * Get cache statistics
     */
    fun getStats(): CacheStats {
        val diskSize = diskCacheDir.listFiles()?.sumOf { it.length() } ?: 0
        val diskCount = diskCacheDir.listFiles()?.size ?: 0
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
     */
    suspend fun pruneIfNeeded() = withContext(Dispatchers.IO) {
        val maxBytes = MAX_DISK_CACHE_MB.toLong() * 1024 * 1024
        var totalSize = diskCacheDir.listFiles()?.sumOf { it.length() } ?: 0
        
        if (totalSize > maxBytes) {
            // Delete oldest files first
            diskCacheDir.listFiles()
                ?.sortedBy { it.lastModified() }
                ?.forEach { file ->
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
        return "${assetType.name}_${assetId}"
    }
    
    private fun getDiskFile(key: String): File {
        // Hash the key for safe filename
        val hash = MessageDigest.getInstance("SHA-1")
            .digest(key.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return File(diskCacheDir, hash)
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
