package com.lumiyaviewer.lumiya.assets

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.security.MessageDigest
import java.util.concurrent.ConcurrentHashMap

/**
 * Asset Manager for Linkpoint
 * 
 * Manages downloading, caching, and decoding of Second Life assets including:
 * - Textures
 * - Meshes
 * - Sounds
 * - Animations
 * - Scripts
 * 
 * Features:
 * - Automatic caching
 * - Memory management
 * - Background downloading
 * - Cache expiration
 * - Asset decoding
 */
class AssetManager(private val context: Context) {
    
    companion object {
        private const val TAG = "AssetManager"
        private const val CACHE_DIR_NAME = "asset_cache"
        private const val MAX_CACHE_SIZE_MB = 500L
        private const val CACHE_EXPIRATION_DAYS = 7L
        
        @Volatile
        private var instance: AssetManager? = null
        
        fun getInstance(context: Context): AssetManager {
            return instance ?: synchronized(this) {
                instance ?: AssetManager(context.applicationContext).also { instance = it }
            }
        }
    }
    
    // Cache directory
    private val cacheDir: File by lazy {
        File(context.cacheDir, CACHE_DIR_NAME).apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }
    
    // In-memory cache for frequently accessed assets
    private val memoryCache = ConcurrentHashMap<String, CachedAsset>()
    
    // Download queue
    private val downloadQueue = ConcurrentHashMap<String, Job>()
    
    // Coroutine scope for background operations
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    /**
     * Asset types
     */
    enum class AssetType {
        TEXTURE,
        MESH,
        SOUND,
        ANIMATION,
        SCRIPT,
        UNKNOWN
    }
    
    /**
     * Cached asset data
     */
    private data class CachedAsset(
        val data: ByteArray,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Download an asset from URL
     * 
     * @param assetId Unique asset identifier
     * @param url Asset URL
     * @param type Asset type
     * @param onProgress Progress callback (0.0 to 1.0)
     * @param onComplete Completion callback with file path
     * @param onError Error callback
     */
    fun downloadAsset(
        assetId: String,
        url: String,
        type: AssetType = AssetType.UNKNOWN,
        onProgress: ((Float) -> Unit)? = null,
        onComplete: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        // Check if already downloading
        if (downloadQueue.containsKey(assetId)) {
            Log.d(TAG, "Asset $assetId is already being downloaded")
            return
        }
        
        // Check cache first
        val cachedFile = getCachedFile(assetId)
        if (cachedFile.exists() && !isExpired(cachedFile)) {
            Log.d(TAG, "Asset $assetId found in cache")
            onComplete(cachedFile.absolutePath)
            return
        }
        
        // Start download
        val job = scope.launch {
            try {
                Log.i(TAG, "Downloading asset $assetId from $url")
                
                val connection = URL(url).openConnection()
                val contentLength = connection.contentLength
                
                connection.getInputStream().use { input ->
                    val outputFile = File(cacheDir, assetId)
                    FileOutputStream(outputFile).use { output ->
                        val buffer = ByteArray(8192)
                        var bytesRead: Int
                        var totalBytesRead = 0L
                        
                        while (input.read(buffer).also { bytesRead = it } != -1) {
                            output.write(buffer, 0, bytesRead)
                            totalBytesRead += bytesRead
                            
                            if (contentLength > 0) {
                                val progress = totalBytesRead.toFloat() / contentLength
                                withContext(Dispatchers.Main) {
                                    onProgress?.invoke(progress)
                                }
                            }
                        }
                    }
                    
                    Log.i(TAG, "Asset $assetId downloaded successfully")
                    withContext(Dispatchers.Main) {
                        onComplete(outputFile.absolutePath)
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error downloading asset $assetId", e)
                withContext(Dispatchers.Main) {
                    onError(e)
                }
            } finally {
                downloadQueue.remove(assetId)
            }
        }
        
        downloadQueue[assetId] = job
    }
    
    /**
     * Get cached asset file
     */
    fun getCachedAsset(assetId: String): File? {
        val file = getCachedFile(assetId)
        return if (file.exists() && !isExpired(file)) file else null
    }
    
    /**
     * Check if asset is cached
     */
    fun isCached(assetId: String): Boolean {
        val file = getCachedFile(assetId)
        return file.exists() && !isExpired(file)
    }
    
    /**
     * Decode texture asset to Bitmap
     */
    suspend fun decodeTexture(assetId: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = getCachedFile(assetId)
            if (!file.exists()) {
                Log.w(TAG, "Texture $assetId not found in cache")
                return@withContext null
            }
            
            // Check memory cache first
            memoryCache[assetId]?.let { cached ->
                return@withContext BitmapFactory.decodeByteArray(cached.data, 0, cached.data.size)
            }
            
            // Decode from file
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            
            if (bitmap != null) {
                // Cache in memory for quick access
                val data = file.readBytes()
                memoryCache[assetId] = CachedAsset(data)
                Log.d(TAG, "Texture $assetId decoded and cached")
            }
            
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Error decoding texture $assetId", e)
            null
        }
    }
    
    /**
     * Clear cache
     */
    fun clearCache() {
        scope.launch {
            try {
                Log.i(TAG, "Clearing asset cache")
                
                // Clear memory cache
                memoryCache.clear()
                
                // Delete cache files
                cacheDir.listFiles()?.forEach { file ->
                    file.delete()
                }
                
                Log.i(TAG, "Asset cache cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing cache", e)
            }
        }
    }
    
    /**
     * Clear expired cache entries
     */
    fun clearExpiredCache() {
        scope.launch {
            try {
                Log.i(TAG, "Clearing expired cache entries")
                
                var deletedCount = 0
                cacheDir.listFiles()?.forEach { file ->
                    if (isExpired(file)) {
                        file.delete()
                        deletedCount++
                    }
                }
                
                Log.i(TAG, "Cleared $deletedCount expired cache entries")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing expired cache", e)
            }
        }
    }
    
    /**
     * Get cache size in bytes
     */
    fun getCacheSize(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
    
    /**
     * Get cache size in MB
     */
    fun getCacheSizeMB(): Long {
        return getCacheSize() / (1024 * 1024)
    }
    
    /**
     * Enforce cache size limit
     */
    fun enforceCacheSizeLimit() {
        scope.launch {
            try {
                val currentSize = getCacheSizeMB()
                
                if (currentSize > MAX_CACHE_SIZE_MB) {
                    Log.i(TAG, "Cache size ($currentSize MB) exceeds limit ($MAX_CACHE_SIZE_MB MB)")
                    
                    // Sort files by last modified time (oldest first)
                    val files = cacheDir.listFiles()?.sortedBy { it.lastModified() } ?: return@launch
                    
                    var deletedSize = 0L
                    for (file in files) {
                        if (getCacheSizeMB() <= MAX_CACHE_SIZE_MB * 0.8) {
                            break
                        }
                        
                        deletedSize += file.length()
                        file.delete()
                    }
                    
                    Log.i(TAG, "Deleted ${deletedSize / (1024 * 1024)} MB from cache")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error enforcing cache size limit", e)
            }
        }
    }
    
    /**
     * Cancel download
     */
    fun cancelDownload(assetId: String) {
        downloadQueue[assetId]?.cancel()
        downloadQueue.remove(assetId)
        Log.d(TAG, "Download cancelled for asset $assetId")
    }
    
    /**
     * Cancel all downloads
     */
    fun cancelAllDownloads() {
        downloadQueue.values.forEach { it.cancel() }
        downloadQueue.clear()
        Log.i(TAG, "All downloads cancelled")
    }
    
    /**
     * Get download progress
     */
    fun isDownloading(assetId: String): Boolean {
        return downloadQueue.containsKey(assetId)
    }
    
    /**
     * Cleanup resources
     */
    fun cleanup() {
        cancelAllDownloads()
        memoryCache.clear()
        scope.cancel()
        Log.i(TAG, "AssetManager cleaned up")
    }
    
    // Private helper methods
    
    private fun getCachedFile(assetId: String): File {
        return File(cacheDir, assetId)
    }
    
    private fun isExpired(file: File): Boolean {
        val age = System.currentTimeMillis() - file.lastModified()
        val expirationTime = CACHE_EXPIRATION_DAYS * 24 * 60 * 60 * 1000
        return age > expirationTime
    }
    
    private fun generateCacheKey(url: String): String {
        return try {
            val digest = MessageDigest.getInstance("MD5")
            val hash = digest.digest(url.toByteArray())
            hash.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            url.hashCode().toString()
        }
    }
}

/**
 * Asset Download Result
 */
sealed class AssetDownloadResult {
    data class Success(val filePath: String) : AssetDownloadResult()
    data class Error(val exception: Exception) : AssetDownloadResult()
    data class Progress(val progress: Float) : AssetDownloadResult()
}

/**
 * Asset Cache Statistics
 */
data class AssetCacheStats(
    val totalSize: Long,
    val fileCount: Int,
    val oldestFile: Long,
    val newestFile: Long
)

/**
 * Extension functions
 */

/**
 * Get cache statistics
 */
fun AssetManager.getCacheStats(): AssetCacheStats {
    val cacheDir = File(context.cacheDir, "asset_cache")
    val files = cacheDir.listFiles() ?: emptyArray()
    
    return AssetCacheStats(
        totalSize = files.sumOf { it.length() },
        fileCount = files.size,
        oldestFile = files.minOfOrNull { it.lastModified() } ?: 0L,
        newestFile = files.maxOfOrNull { it.lastModified() } ?: 0L
    )
}