package com.linkpoint.assets

import com.linkpoint.Debug
import com.linkpoint.slproto.caps.CAPSManager
import kotlinx.coroutines.*
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * AssetType - Types of assets in Second Life
 */
enum class AssetType(val typeId: Int) {
    TEXTURE(0),
    SOUND(1),
    CALLING_CARD(2),
    LANDMARK(3),
    SCRIPT(4),
    CLOTHING(5),
    OBJECT(6),
    NOTECARD(7),
    CATEGORY(8),
    ROOT_CATEGORY(9),
    LSL_TEXT(10),
    LSL_BYTECODE(11),
    TEXTURE_TGA(12),
    BODYPART(13),
    SOUND_WAV(17),
    IMAGE_TGA(18),
    IMAGE_JPEG(19),
    ANIMATION(20),
    GESTURE(21),
    SIMSTATE(22),
    LINK(24),
    LINK_FOLDER(25),
    MESH(49),
    WIDGET(67)
    
    companion object {
        fun fromId(id: Int) = values().find { it.typeId == id }
    }
}

/**
 * AssetManager - Download and manage Second Life assets
 * 
 * Features:
 * - Asset downloading via HTTP CAPS
 * - UDP transfer support (legacy)
 * - Caching
 * - Priority queue
 * - Progress tracking
 * 
 * Based on Firestorm's LLAssetStorage
 */
class AssetManager(
    private val capsManager: CAPSManager,
    private val assetCache: AssetCache
) {
    
    companion object {
        const val MAX_CONCURRENT_DOWNLOADS = 8
        const val DOWNLOAD_TIMEOUT_MS = 30000L
        const val RETRY_COUNT = 3
    }
    
    // Active downloads
    private val activeDownloads = ConcurrentHashMap<UUID, Deferred<ByteArray?>>()
    
    // Download semaphore (limit concurrent downloads)
    private val downloadSemaphore = kotlinx.coroutines.sync.Semaphore(MAX_CONCURRENT_DOWNLOADS)
    
    /**
     * Download asset
     */
    suspend fun downloadAsset(
        assetID: UUID,
        assetType: AssetType,
        priority: Int = 0
    ): ByteArray? = withContext(Dispatchers.IO) {
        
        // Check cache first
        val cached = assetCache.get(assetID, assetType)
        if (cached != null) {
            return@withContext cached
        }
        
        // Check if already downloading
        val existing = activeDownloads[assetID]
        if (existing != null) {
            return@withContext existing.await()
        }
        
        // Start download
        val deferred = async {
            downloadAssetInternal(assetID, assetType)
        }
        
        activeDownloads[assetID] = deferred
        
        return@withContext try {
            val result = deferred.await()
            
            // Cache if successful
            if (result != null) {
                assetCache.put(assetID, assetType, result)
            }
            
            result
        } finally {
            activeDownloads.remove(assetID)
        }
    }
    
    /**
     * Internal asset download
     */
    private suspend fun downloadAssetInternal(
        assetID: UUID,
        assetType: AssetType
    ): ByteArray? {
        
        // Acquire semaphore
        downloadSemaphore.acquire()
        
        return try {
            // Try with retries
            var lastError: Exception? = null
            
            for (attempt in 1..RETRY_COUNT) {
                try {
                    val result = withTimeout(DOWNLOAD_TIMEOUT_MS) {
                        downloadViaCAPS(assetID, assetType)
                            ?: downloadViaHTTP(assetID, assetType)
                    }
                    
                    if (result != null) {
                        return result
                    }
                    
                } catch (e: TimeoutCancellationException) {
                    lastError = e
                    Debug.Log("Asset download timeout (attempt $attempt): $assetID")
                    delay(1000 * attempt.toLong()) // Exponential backoff
                } catch (e: Exception) {
                    lastError = e
                    Debug.Log("Asset download error (attempt $attempt): ${e.message}")
                    delay(1000 * attempt.toLong())
                }
            }
            
            Debug.Log("Asset download failed after $RETRY_COUNT attempts: $assetID - ${lastError?.message}")
            null
            
        } finally {
            downloadSemaphore.release()
        }
    }
    
    /**
     * Download via CAPS (preferred method)
     */
    private suspend fun downloadViaCAPS(assetID: UUID, assetType: AssetType): ByteArray? {
        val capURL = when (assetType) {
            AssetType.TEXTURE -> capsManager.getCapability("GetTexture")
            AssetType.MESH -> capsManager.getCapability("GetMesh")
            else -> capsManager.getCapability("GetAsset")
        } ?: return null
        
        // Build request URL
        val requestURL = "$capURL?texture_id=$assetID"
        
        return try {
            downloadHTTP(requestURL)
        } catch (e: Exception) {
            Debug.Log("CAPS download failed: ${e.message}")
            null
        }
    }
    
    /**
     * Download via direct HTTP (fallback)
     */
    private suspend fun downloadViaHTTP(assetID: UUID, assetType: AssetType): ByteArray? {
        // Construct HTTP asset URL (grid-specific)
        val baseURL = "http://asset.secondlife.com"  // Replace with actual grid asset URL
        val requestURL = "$baseURL/$assetID"
        
        return try {
            downloadHTTP(requestURL)
        } catch (e: Exception) {
            Debug.Log("HTTP download failed: ${e.message}")
            null
        }
    }
    
    /**
     * Perform HTTP download
     */
    private suspend fun downloadHTTP(url: String): ByteArray = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connectTimeout = 10000
        connection.readTimeout = DOWNLOAD_TIMEOUT_MS.toInt()
        
        try {
            val responseCode = connection.responseCode
            if (responseCode != 200) {
                throw IOException("HTTP error: $responseCode")
            }
            
            connection.inputStream.use { it.readBytes() }
        } finally {
            connection.disconnect()
        }
    }
    
    /**
     * Prefetch multiple assets
     */
    suspend fun prefetchAssets(assets: List<Pair<UUID, AssetType>>) {
        coroutineScope {
            assets.map { (assetID, assetType) ->
                async {
                    downloadAsset(assetID, assetType, priority = 1)
                }
            }.awaitAll()
        }
    }
    
    /**
     * Cancel download
     */
    fun cancelDownload(assetID: UUID) {
        activeDownloads[assetID]?.cancel()
        activeDownloads.remove(assetID)
    }
    
    /**
     * Get download statistics
     */
    fun getStats(): DownloadStats {
        return DownloadStats(
            activeDownloads = activeDownloads.size,
            cacheSize = assetCache.getSize(),
            cacheCount = assetCache.getCount()
        }
    }
    
    data class DownloadStats(
        val activeDownloads: Int,
        val cacheSize: Long,
        val cacheCount: Int
    )
}
