package com.linkpoint.assets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manages texture downloading and decoding
 * Handles JPEG2000 (J2K) format used by Second Life
 */
class TextureManager(
    private val context: android.content.Context,
    private val cache: AssetCache,
    private val capabilityManager: com.linkpoint.protocol.capabilities.CapabilityManager? = null
) {
    private val capabilityUrl: String? get() = 
        capabilityManager?.getCapability(com.linkpoint.protocol.capabilities.CapabilityManager.CAP_GET_TEXTURE)
    companion object {
        private const val TAG = "TextureManager"
        private const val MAX_CONCURRENT_DOWNLOADS = 4
        private const val TEXTURE_FETCH_TIMEOUT_MS = 30000L
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    // Download queue with priority
    private val downloadQueue = PriorityBlockingQueue<TextureRequest>(100)
    private val activeDownloads = AtomicInteger(0)
    private val pendingTextures = ConcurrentHashMap<UUID, Deferred<Bitmap?>>()
    
    // Decoded texture cache
    private val textureCache = ConcurrentHashMap<UUID, Bitmap>()
    
    // Statistics
    private val _stats = MutableStateFlow(TextureStats())
    val stats: StateFlow<TextureStats> = _stats
    
    init {
        // Start download workers
        repeat(MAX_CONCURRENT_DOWNLOADS) {
            scope.launch {
                downloadWorker()
            }
        }
    }
    
    /**
     * Request a texture with priority
     */
    suspend fun getTexture(
        textureId: UUID,
        priority: TexturePriority = TexturePriority.NORMAL,
        discard: Int = 0
    ): Bitmap? {
        // Check decoded cache
        textureCache[textureId]?.let { return it }
        
        // Check pending requests
        pendingTextures[textureId]?.let { return it.await() }
        
        // Create new request
        val deferred = scope.async {
            fetchTexture(textureId, priority, discard)
        }
        pendingTextures[textureId] = deferred
        
        return try {
            deferred.await()
        } finally {
            pendingTextures.remove(textureId)
        }
    }
    
    /**
     * Prefetch textures in background
     */
    fun prefetch(textureIds: List<UUID>, priority: TexturePriority = TexturePriority.LOW) {
        textureIds.forEach { id ->
            if (!textureCache.containsKey(id)) {
                downloadQueue.offer(TextureRequest(id, priority, 0))
            }
        }
    }
    
    /**
     * Clear texture from cache
     */
    fun evict(textureId: UUID) {
        textureCache.remove(textureId)?.recycle()
    }
    
    /**
     * Clear all decoded textures
     */
    fun clearDecodedCache() {
        textureCache.values.forEach { it.recycle() }
        textureCache.clear()
    }
    
    private suspend fun fetchTexture(
        textureId: UUID,
        priority: TexturePriority,
        discard: Int
    ): Bitmap? {
        // Check raw data cache
        val cachedData = cache.get(textureId, AssetType.TEXTURE)
        if (cachedData != null) {
            return decodeTexture(textureId, cachedData)
        }
        
        // Download from server
        val data = downloadTexture(textureId, discard) ?: return null
        
        // Cache raw data
        cache.put(textureId, AssetType.TEXTURE, data)
        
        // Decode and cache
        return decodeTexture(textureId, data)
    }
    
    private suspend fun downloadTexture(textureId: UUID, discard: Int): ByteArray? = withContext(Dispatchers.IO) {
        updateStats { it.copy(pendingDownloads = it.pendingDownloads + 1) }
        
        try {
            // Build texture URL
            val url = buildTextureUrl(textureId, discard)
            
            val request = Request.Builder()
                .url(url)
                .header("Accept", "image/x-j2c, image/jp2, image/jpeg, image/*")
                .build()
            
            val response = httpClient.newCall(request).execute()
            
            if (response.isSuccessful) {
                val data = response.body?.bytes()
                updateStats { it.copy(
                    downloadedCount = it.downloadedCount + 1,
                    downloadedBytes = it.downloadedBytes + (data?.size ?: 0)
                )}
                data
            } else {
                Log.w(TAG, "Texture download failed: ${response.code}")
                updateStats { it.copy(failedCount = it.failedCount + 1) }
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Texture download error: $textureId", e)
            updateStats { it.copy(failedCount = it.failedCount + 1) }
            null
        } finally {
            updateStats { it.copy(pendingDownloads = it.pendingDownloads - 1) }
        }
    }
    
    private fun buildTextureUrl(textureId: UUID, discard: Int): String {
        // Use capability URL if available, otherwise fall back to asset server
        return capabilityUrl?.let {
            "$it?texture_id=$textureId&discard_level=$discard"
        } ?: "https://asset-cdn.glb.agni.lindenlab.com/?texture_id=$textureId"
    }
    
    private fun decodeTexture(textureId: UUID, data: ByteArray): Bitmap? {
        return try {
            // Check if it's JPEG2000 (J2K/JP2)
            val bitmap = if (isJPEG2000(data)) {
                decodeJPEG2000(data)
            } else {
                // Try standard formats (PNG, JPEG)
                BitmapFactory.decodeByteArray(data, 0, data.size)
            }
            
            bitmap?.let {
                textureCache[textureId] = it
                updateStats { s -> s.copy(decodedCount = s.decodedCount + 1) }
            }
            bitmap
        } catch (e: Exception) {
            Log.e(TAG, "Texture decode error: $textureId", e)
            updateStats { it.copy(decodeFailedCount = it.decodeFailedCount + 1) }
            null
        }
    }
    
    private fun isJPEG2000(data: ByteArray): Boolean {
        if (data.size < 12) return false
        // JPEG2000 magic bytes
        return (data[0] == 0x00.toByte() && data[1] == 0x00.toByte() && 
                data[2] == 0x00.toByte() && data[3] == 0x0C.toByte()) ||
               // J2C codestream
               (data[0] == 0xFF.toByte() && data[1] == 0x4F.toByte())
    }
    
    private fun decodeJPEG2000(data: ByteArray): Bitmap? {
        return try {
            JPEG2000Decoder.decode(data)
        } catch (e: Exception) {
            Log.e(TAG, "JPEG2000 decode failed", e)
            null
        }
    }
    
    private suspend fun downloadWorker() {
        while (true) {
            val request = downloadQueue.take()
            if (activeDownloads.get() < MAX_CONCURRENT_DOWNLOADS) {
                activeDownloads.incrementAndGet()
                try {
                    if (!textureCache.containsKey(request.textureId)) {
                        fetchTexture(request.textureId, request.priority, request.discard)
                    }
                } finally {
                    activeDownloads.decrementAndGet()
                }
            } else {
                // Re-queue if too many active
                downloadQueue.offer(request)
                delay(100)
            }
        }
    }
    
    private fun updateStats(update: (TextureStats) -> TextureStats) {
        _stats.value = update(_stats.value)
    }
    
    fun shutdown() {
        scope.cancel()
        clearDecodedCache()
    }
    
    /**
     * Called when capabilities are ready after login.
     * 
     * Note: The TextureManager already uses capability-based fetching dynamically
     * via the capabilityUrl property (see buildTextureUrl). This method is primarily
     * for logging and notification purposes, similar to Lumiya's TextureCache.setFetcher()
     * pattern where the fetcher is set but the actual fetching logic already supports
     * the capability URL when available.
     */
    fun onCapabilitiesReady() {
        val textureCapUrl = capabilityUrl
        if (textureCapUrl != null) {
            Log.i(TAG, "Texture fetching enabled via capability: ${textureCapUrl.take(50)}...")
        } else {
            Log.w(TAG, "GetTexture capability not available - using fallback asset server")
        }
    }
}

enum class TexturePriority(val value: Int) {
    CRITICAL(0),    // Avatar skin, UI elements
    HIGH(1),        // Nearby objects
    NORMAL(2),      // Standard priority
    LOW(3),         // Background, far away
    PREFETCH(4)     // Speculative loading
}

data class TextureRequest(
    val textureId: UUID,
    val priority: TexturePriority,
    val discard: Int
) : Comparable<TextureRequest> {
    override fun compareTo(other: TextureRequest): Int {
        return priority.value.compareTo(other.priority.value)
    }
}

data class TextureStats(
    val pendingDownloads: Int = 0,
    val downloadedCount: Int = 0,
    val downloadedBytes: Long = 0,
    val failedCount: Int = 0,
    val decodedCount: Int = 0,
    val decodeFailedCount: Int = 0
)
