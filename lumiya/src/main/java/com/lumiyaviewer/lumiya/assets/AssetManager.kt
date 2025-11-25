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
    
    private val memoryCache = ConcurrentHashMap<String, CachedAsset>()
    private val downloadQueue = ConcurrentHashMap<String, Job>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    enum class AssetType {
        TEXTURE, MESH, SOUND, ANIMATION, SCRIPT, UNKNOWN
    }
    
    private data class CachedAsset(
        val data: ByteArray,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    fun downloadAsset(
        assetId: String,
        url: String,
        type: AssetType = AssetType.UNKNOWN,
        onProgress: ((Float) -> Unit)? = null,
        onComplete: (String) -> Unit,
        onError: (Exception) -> Unit
    ) {
        if (downloadQueue.containsKey(assetId)) {
            Log.d(TAG, "Asset $assetId is already being downloaded")
            return
        }
        
        val cachedFile = getCachedFile(assetId)
        if (cachedFile.exists() && !isExpired(cachedFile)) {
            Log.d(TAG, "Asset $assetId found in cache")
            onComplete(cachedFile.absolutePath)
            return
        }
        
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
    
    fun getCachedAsset(assetId: String): File? {
        val file = getCachedFile(assetId)
        return if (file.exists() && !isExpired(file)) file else null
    }
    
    fun isCached(assetId: String): Boolean {
        val file = getCachedFile(assetId)
        return file.exists() && !isExpired(file)
    }
    
    suspend fun decodeTexture(assetId: String): Bitmap? = withContext(Dispatchers.IO) {
        try {
            val file = getCachedFile(assetId)
            if (!file.exists()) {
                Log.w(TAG, "Texture $assetId not found in cache")
                return@withContext null
            }
            
            memoryCache[assetId]?.let { cached ->
                return@withContext BitmapFactory.decodeByteArray(cached.data, 0, cached.data.size)
            }
            
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            
            if (bitmap != null) {
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
    
    fun clearCache() {
        scope.launch {
            try {
                Log.i(TAG, "Clearing asset cache")
                memoryCache.clear()
                cacheDir.listFiles()?.forEach { file ->
                    file.delete()
                }
                Log.i(TAG, "Asset cache cleared")
            } catch (e: Exception) {
                Log.e(TAG, "Error clearing cache", e)
            }
        }
    }
    
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
    
    fun getCacheSize(): Long {
        return cacheDir.listFiles()?.sumOf { it.length() } ?: 0L
    }
    
    fun getCacheSizeMB(): Long {
        return getCacheSize() / (1024 * 1024)
    }
    
    fun enforceCacheSizeLimit() {
        scope.launch {
            try {
                val currentSize = getCacheSizeMB()
                
                if (currentSize > MAX_CACHE_SIZE_MB) {
                    Log.i(TAG, "Cache size ($currentSize MB) exceeds limit ($MAX_CACHE_SIZE_MB MB)")
                    
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
    
    fun cancelDownload(assetId: String) {
        downloadQueue[assetId]?.cancel()
        downloadQueue.remove(assetId)
        Log.d(TAG, "Download cancelled for asset $assetId")
    }
    
    fun cancelAllDownloads() {
        downloadQueue.values.forEach { it.cancel() }
        downloadQueue.clear()
        Log.i(TAG, "All downloads cancelled")
    }
    
    fun isDownloading(assetId: String): Boolean {
        return downloadQueue.containsKey(assetId)
    }
    
    fun cleanup() {
        cancelAllDownloads()
        memoryCache.clear()
        scope.cancel()
        Log.i(TAG, "AssetManager cleaned up")
    }
    
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

    fun getCacheStats(): AssetCacheStats {
        val files = cacheDir.listFiles() ?: emptyArray()
        
        return AssetCacheStats(
            totalSize = files.sumOf { it.length() },
            fileCount = files.size,
            oldestFile = files.minOfOrNull { it.lastModified() } ?: 0L,
            newestFile = files.maxOfOrNull { it.lastModified() } ?: 0L
        )
    }
}

sealed class AssetDownloadResult {
    data class Success(val filePath: String) : AssetDownloadResult()
    data class Error(val exception: Exception) : AssetDownloadResult()
    data class Progress(val progress: Float) : AssetDownloadResult()
}

data class AssetCacheStats(
    val totalSize: Long,
    val fileCount: Int,
    val oldestFile: Long,
    val newestFile: Long
)
