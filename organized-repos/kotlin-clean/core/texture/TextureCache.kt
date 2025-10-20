package com.linkpoint.texture

import android.util.LruCache
import com.linkpoint.Debug
import kotlinx.coroutines.*
import java.io.File
import java.security.MessageDigest
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * TextureCache - Manages texture loading, caching, and GPU upload
 * 
 * Features:
 * - Memory cache (LRU)
 * - Disk cache
 * - Async downloading
 * - GPU texture management
 * - Automatic eviction
 * 
 * Based on Firestorm's texture cache system
 */
class TextureCache(
    private val assetManager: com.linkpoint.assets.AssetManager,
    private val cacheDir: File,
    memoryCacheSizeMB: Int = 100
) {
    
    companion object {
        const val MAX_DISK_CACHE_MB = 500
        const val TEXTURE_FETCH_TIMEOUT_MS = 30000L
    }
    
    // Memory cache (LRU)
    private val memoryCache = LruCache<UUID, CachedTexture>(memoryCacheSizeMB * 1024 * 1024)
    
    // Loading textures (prevent duplicate requests)
    private val loadingTextures = ConcurrentHashMap<UUID, Deferred<GLTexture?>>()
    
    // Disk cache directory
    private val diskCacheDir = File(cacheDir, "textures").apply {
        if (!exists()) mkdirs()
    }
    
    /**
     * Get texture (async, returns cached or downloads)
     */
    suspend fun getTexture(textureID: UUID): GLTexture? = withContext(Dispatchers.IO) {
        try {
            // Check memory cache
            val cached = memoryCache.get(textureID)
            if (cached != null && cached.glTexture != null) {
                return@withContext cached.glTexture
            }
            
            // Check if already loading
            val loading = loadingTextures[textureID]
            if (loading != null) {
                return@withContext loading.await()
            }
            
            // Start loading
            val deferred = async {
                loadTextureInternal(textureID)
            }
            
            loadingTextures[textureID] = deferred
            
            val result = try {
                withTimeout(TEXTURE_FETCH_TIMEOUT_MS) {
                    deferred.await()
                }
            } finally {
                loadingTextures.remove(textureID)
            }
            
            result
            
        } catch (e: TimeoutCancellationException) {
            Debug.Log("Texture fetch timeout: $textureID")
            null
        } catch (e: Exception) {
            Debug.Log("Texture fetch error: ${e.message}")
            null
        }
    }
    
    /**
     * Internal texture loading
     */
    private suspend fun loadTextureInternal(textureID: UUID): GLTexture? {
        // Check disk cache first
        val diskCached = loadFromDiskCache(textureID)
        if (diskCached != null) {
            val glTexture = createGLTexture(diskCached.rgba, diskCached.width, diskCached.height)
            cacheInMemory(textureID, diskCached.width, diskCached.height, diskCached.rgba, glTexture)
            return glTexture
        }
        
        // Download from asset server
        val j2kData = assetManager.downloadAsset(textureID, com.linkpoint.assets.AssetType.TEXTURE)
        if (j2kData == null) {
            Debug.Log("Failed to download texture: $textureID")
            return null
        }
        
        // Decode JPEG2000
        val decoded = if (OpenJPEG.isNativeAvailable()) {
            OpenJPEG.decodeWithInfo(j2kData)
        } else {
            OpenJPEG.decodeFallback(j2kData)
        }
        
        if (!decoded.success || decoded.rgba == null) {
            Debug.Log("Failed to decode texture: $textureID - ${decoded.error}")
            return null
        }
        
        // Save to disk cache
        saveToDiskCache(textureID, decoded.rgba, decoded.width, decoded.height)
        
        // Create GL texture
        val glTexture = createGLTexture(decoded.rgba, decoded.width, decoded.height)
        
        // Cache in memory
        cacheInMemory(textureID, decoded.width, decoded.height, decoded.rgba, glTexture)
        
        return glTexture
    }
    
    /**
     * Create OpenGL texture
     */
    private suspend fun createGLTexture(rgba: ByteArray, width: Int, height: Int): GLTexture {
        return withContext(Dispatchers.Main) {
            GLTexture.create(rgba, width, height)
        }
    }
    
    /**
     * Cache texture in memory
     */
    private fun cacheInMemory(
        textureID: UUID,
        width: Int,
        height: Int,
        rgba: ByteArray,
        glTexture: GLTexture
    ) {
        val cached = CachedTexture(
            textureID = textureID,
            width = width,
            height = height,
            rgba = rgba,
            glTexture = glTexture,
            size = rgba.size
        )
        memoryCache.put(textureID, cached)
    }
    
    /**
     * Load texture from disk cache
     */
    private fun loadFromDiskCache(textureID: UUID): DecodedTexture? {
        val file = getDiskCacheFile(textureID)
        if (!file.exists()) return null
        
        try {
            val data = file.readBytes()
            // First 8 bytes: width (4) + height (4)
            val width = ((data[0].toInt() and 0xFF) shl 24) or
                       ((data[1].toInt() and 0xFF) shl 16) or
                       ((data[2].toInt() and 0xFF) shl 8) or
                       (data[3].toInt() and 0xFF)
            
            val height = ((data[4].toInt() and 0xFF) shl 24) or
                        ((data[5].toInt() and 0xFF) shl 16) or
                        ((data[6].toInt() and 0xFF) shl 8) or
                        (data[7].toInt() and 0xFF)
            
            val rgba = data.copyOfRange(8, data.size)
            
            return DecodedTexture(rgba, width, height)
        } catch (e: Exception) {
            Debug.Log("Failed to load from disk cache: ${e.message}")
            return null
        }
    }
    
    /**
     * Save texture to disk cache
     */
    private fun saveToDiskCache(textureID: UUID, rgba: ByteArray, width: Int, height: Int) {
        try {
            val file = getDiskCacheFile(textureID)
            
            // Write width and height as first 8 bytes
            val data = ByteArray(8 + rgba.size)
            data[0] = (width shr 24).toByte()
            data[1] = (width shr 16).toByte()
            data[2] = (width shr 8).toByte()
            data[3] = width.toByte()
            data[4] = (height shr 24).toByte()
            data[5] = (height shr 16).toByte()
            data[6] = (height shr 8).toByte()
            data[7] = height.toByte()
            System.arraycopy(rgba, 0, data, 8, rgba.size)
            
            file.writeBytes(data)
        } catch (e: Exception) {
            Debug.Log("Failed to save to disk cache: ${e.message}")
        }
    }
    
    /**
     * Get disk cache file for texture
     */
    private fun getDiskCacheFile(textureID: UUID): File {
        val hash = md5(textureID.toString())
        return File(diskCacheDir, "$hash.tex")
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
     * Clear memory cache
     */
    fun clearMemoryCache() {
        memoryCache.evictAll()
    }
    
    /**
     * Clear disk cache
     */
    fun clearDiskCache() {
        diskCacheDir.listFiles()?.forEach { it.delete() }
    }
    
    /**
     * Get cache statistics
     */
    fun getCacheStats(): CacheStats {
        val memorySize = memoryCache.size()
        val diskSize = diskCacheDir.listFiles()?.sumOf { it.length() } ?: 0L
        val diskCount = diskCacheDir.listFiles()?.size ?: 0
        
        return CacheStats(
            memorySize = memorySize,
            diskSize = diskSize,
            diskCount = diskCount
        )
    }
    
    /**
     * Cached texture data
     */
    private data class CachedTexture(
        val textureID: UUID,
        val width: Int,
        val height: Int,
        val rgba: ByteArray,
        val glTexture: GLTexture?,
        val size: Int
    )
    
    /**
     * Decoded texture data
     */
    private data class DecodedTexture(
        val rgba: ByteArray,
        val width: Int,
        val height: Int
    )
    
    /**
     * Cache statistics
     */
    data class CacheStats(
        val memorySize: Int,
        val diskSize: Long,
        val diskCount: Int
    )
}
