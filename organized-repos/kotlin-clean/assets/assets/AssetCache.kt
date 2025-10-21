package com.linkpoint.assets

import com.linkpoint.Debug
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * AssetCache - Persistent disk cache for assets
 * 
 * Features:
 * - Disk-based storage
 * - LRU eviction
 * - Size limits
 * - Thread-safe
 * 
 * Based on Firestorm's asset cache
 */
class AssetCache(
    private val cacheDir: File,
    private val maxCacheSizeMB: Int = 500
) {
    
    companion object {
        const val CACHE_VERSION = 1
        const val INDEX_FILE = "cache.idx"
    }
    
    // Cache directory
    private val assetCacheDir = File(cacheDir, "assets").apply {
        if (!exists()) mkdirs()
    }
    
    // Index file
    private val indexFile = File(assetCacheDir, INDEX_FILE)
    
    // In-memory index
    private val index = mutableMapOf<String, CacheEntry>()
    private val lock = ReentrantReadWriteLock()
    
    init {
        loadIndex()
        enforceSize Limit()
    }
    
    /**
     * Get asset from cache
     */
    fun get(assetID: UUID, assetType: AssetType): ByteArray? = lock.read {
        val key = makeKey(assetID, assetType)
        val entry = index[key] ?: return null
        
        val file = File(assetCacheDir, entry.filename)
        if (!file.exists()) {
            // Entry exists but file is missing, remove from index
            lock.write {
                index.remove(key)
            }
            return null
        }
        
        try {
            // Update access time
            lock.write {
                index[key] = entry.copy(lastAccessTime = System.currentTimeMillis())
            }
            
            file.readBytes()
        } catch (e: Exception) {
            Debug.Log("Failed to read cached asset: ${e.message}")
            null
        }
    }
    
    /**
     * Put asset in cache
     */
    fun put(assetID: UUID, assetType: AssetType, data: ByteArray) = lock.write {
        val key = makeKey(assetID, assetType)
        val filename = makeFilename(assetID, assetType)
        val file = File(assetCacheDir, filename)
        
        try {
            file.writeBytes(data)
            
            val entry = CacheEntry(
                key = key,
                filename = filename,
                size = data.size,
                assetType = assetType,
                createdTime = System.currentTimeMillis(),
                lastAccessTime = System.currentTimeMillis()
            )
            
            index[key] = entry
            
            // Enforce size limit
            enforceSizeLimit()
            
            // Save index periodically
            if (index.size % 100 == 0) {
                saveIndex()
            }
            
        } catch (e: Exception) {
            Debug.Log("Failed to write cached asset: ${e.message}")
        }
    }
    
    /**
     * Check if asset is cached
     */
    fun contains(assetID: UUID, assetType: AssetType): Boolean = lock.read {
        val key = makeKey(assetID, assetType)
        index.containsKey(key)
    }
    
    /**
     * Remove asset from cache
     */
    fun remove(assetID: UUID, assetType: AssetType) = lock.write {
        val key = makeKey(assetID, assetType)
        val entry = index.remove(key) ?: return@write
        
        val file = File(assetCacheDir, entry.filename)
        file.delete()
    }
    
    /**
     * Clear entire cache
     */
    fun clear() = lock.write {
        assetCacheDir.listFiles()?.forEach { it.delete() }
        index.clear()
        saveIndex()
    }
    
    /**
     * Get cache size in bytes
     */
    fun getSize(): Long = lock.read {
        index.values.sumOf { it.size.toLong() }
    }
    
    /**
     * Get number of cached assets
     */
    fun getCount(): Int = lock.read {
        index.size
    }
    
    /**
     * Enforce cache size limit (LRU eviction)
     */
    private fun enforceSizeLimit() {
        val maxSizeBytes = maxCacheSizeMB * 1024L * 1024L
        var currentSize = index.values.sumOf { it.size.toLong() }
        
        if (currentSize <= maxSizeBytes) return
        
        // Sort by last access time (oldest first)
        val sorted = index.values.sortedBy { it.lastAccessTime }
        
        for (entry in sorted) {
            if (currentSize <= maxSizeBytes) break
            
            // Delete file
            val file = File(assetCacheDir, entry.filename)
            file.delete()
            
            // Remove from index
            index.remove(entry.key)
            currentSize -= entry.size
        }
        
        Debug.Log("Cache eviction: removed ${sorted.size - index.size} assets")
    }
    
    /**
     * Load index from disk
     */
    private fun loadIndex() {
        if (!indexFile.exists()) return
        
        try {
            indexFile.forEachLine { line ->
                val parts = line.split('|')
                if (parts.size == 6) {
                    val entry = CacheEntry(
                        key = parts[0],
                        filename = parts[1],
                        size = parts[2].toIntOrNull() ?: 0,
                        assetType = AssetType.fromId(parts[3].toIntOrNull() ?: 0) ?: AssetType.TEXTURE,
                        createdTime = parts[4].toLongOrNull() ?: 0L,
                        lastAccessTime = parts[5].toLongOrNull() ?: 0L
                    )
                    index[entry.key] = entry
                }
            }
            Debug.Log("Loaded asset cache index: ${index.size} entries")
        } catch (e: Exception) {
            Debug.Log("Failed to load cache index: ${e.message}")
        }
    }
    
    /**
     * Save index to disk
     */
    private fun saveIndex() {
        try {
            indexFile.bufferedWriter().use { writer ->
                for (entry in index.values) {
                    writer.write("${entry.key}|${entry.filename}|${entry.size}|${entry.assetType.typeId}|${entry.createdTime}|${entry.lastAccessTime}\n")
                }
            }
        } catch (e: Exception) {
            Debug.Log("Failed to save cache index: ${e.message}")
        }
    }
    
    /**
     * Make cache key
     */
    private fun makeKey(assetID: UUID, assetType: AssetType): String {
        return "${assetID}_${assetType.typeId}"
    }
    
    /**
     * Make filename
     */
    private fun makeFilename(assetID: UUID, assetType: AssetType): String {
        val hash = md5(assetID.toString())
        return "${hash}_${assetType.typeId}.asset"
    }
    
    /**
     * MD5 hash
     */
    private fun md5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Cache entry
     */
    private data class CacheEntry(
        val key: String,
        val filename: String,
        val size: Int,
        val assetType: AssetType,
        val createdTime: Long,
        val lastAccessTime: Long
    )
}
