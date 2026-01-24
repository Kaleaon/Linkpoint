package com.linkpoint.assets

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.linkpoint.network.NetworkLogger
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.ByteArrayOutputStream
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * Manages texture downloading and decoding
 * Handles JPEG2000 (J2K) format used by Second Life.
 * 
 * Enhanced with detailed logging for debugging texture loading issues.
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
        
        // Use priority to determine download timeout and retry behavior
        val timeoutMs = when (priority) {
            TexturePriority.CRITICAL -> 10000L   // Highest priority - avatar/UI textures
            TexturePriority.HIGH -> 12000L       // Nearby objects
            TexturePriority.NORMAL -> 15000L     // Standard objects
            TexturePriority.LOW -> 25000L        // Background/far away
            TexturePriority.PREFETCH -> 30000L   // Speculative loading
        }
        
        // Download from server with priority-based timeout
        val data = downloadTexture(textureId, discard, timeoutMs) ?: return null
        
        // Cache raw data
        cache.put(textureId, AssetType.TEXTURE, data)
        
        // Decode and cache
        return decodeTexture(textureId, data)
    }
    
    private suspend fun downloadTexture(
        textureId: UUID, 
        discard: Int, 
        timeoutMs: Long = 15000L
    ): ByteArray? = withContext(Dispatchers.IO) {
        updateStats { it.copy(pendingDownloads = it.pendingDownloads + 1) }
        
        val startTime = System.currentTimeMillis()
        
        try {
            // Build texture URL - returns null if capability is not available
            val url = buildTextureUrl(textureId, discard)
            
            if (url == null) {
                // No capability URL available - texture fetching requires GetTexture capability
                // The asset CDN fallback doesn't work without authentication
                Log.w(TAG, "🖼️ Texture download skipped: $textureId - GetTexture capability not available")
                NetworkLogger.logTextureResult(
                    textureId = textureId.toString(),
                    success = false,
                    durationMs = 0,
                    sizeBytes = null,
                    protocol = null,
                    error = "GetTexture capability not available"
                )
                
                lastError = "GetTexture capability not available"
                lastErrorTime = System.currentTimeMillis()
                updateStats { it.copy(failedCount = it.failedCount + 1) }
                return@withContext null
            }
            
            Log.d(TAG, "🖼️ Starting texture download: $textureId")
            NetworkLogger.logTextureRequest(textureId.toString(), url, "NORMAL")
            
            val request = Request.Builder()
                .url(url)
                .header("Accept", "image/x-j2c, image/jp2, image/jpeg, image/*")
                .build()
            
            val response = httpClient.newCall(request).execute()
            val durationMs = System.currentTimeMillis() - startTime
            val protocol = response.protocol.toString()
            
            // Use try-finally to ensure response is always closed to prevent connection leaks
            // This fixes "ProtocolException: Unexpected status line" errors caused by
            // unconsumed response bodies polluting the connection pool
            try {
                if (response.isSuccessful) {
                    val data = response.body?.bytes()
                    val sizeBytes = data?.size ?: 0
                    
                    Log.d(TAG, "🖼️ Texture downloaded: $textureId ($sizeBytes bytes, ${durationMs}ms, $protocol)")
                    NetworkLogger.logTextureResult(
                        textureId = textureId.toString(),
                        success = true,
                        durationMs = durationMs,
                        sizeBytes = sizeBytes,
                        protocol = protocol
                    )
                    
                    updateStats { it.copy(
                        downloadedCount = it.downloadedCount + 1,
                        downloadedBytes = it.downloadedBytes + sizeBytes
                    )}
                    data
                } else {
                    // Consume and log error body to release connection properly and aid debugging
                    val errorBody = response.body?.string()
                    val errorDetails = if (errorBody != null && errorBody.length < 500) {
                        " (${errorBody.take(200)})"
                    } else ""
                    Log.w(TAG, "🖼️ Texture download failed: $textureId - HTTP ${response.code}$errorDetails")
                    NetworkLogger.logTextureResult(
                        textureId = textureId.toString(),
                        success = false,
                        durationMs = durationMs,
                        sizeBytes = null,
                        protocol = protocol,
                        error = "HTTP ${response.code}: ${response.message}"
                    )
                    
                    lastError = "HTTP ${response.code}: Download failed"
                    lastErrorTime = System.currentTimeMillis()
                    updateStats { it.copy(failedCount = it.failedCount + 1) }
                    null
                }
            } finally {
                // Always close response to release connection back to pool
                response.close()
            }
        } catch (e: Exception) {
            val durationMs = System.currentTimeMillis() - startTime
            Log.e(TAG, "🖼️ Texture download error: $textureId - ${e.javaClass.simpleName}: ${e.message}")
            NetworkLogger.logTextureResult(
                textureId = textureId.toString(),
                success = false,
                durationMs = durationMs,
                sizeBytes = null,
                error = "${e.javaClass.simpleName}: ${e.message}"
            )
            
            lastError = "${e.javaClass.simpleName}: ${e.message}"
            lastErrorTime = System.currentTimeMillis()
            updateStats { it.copy(failedCount = it.failedCount + 1) }
            null
        } finally {
            updateStats { it.copy(pendingDownloads = it.pendingDownloads - 1) }
        }
    }
    
    /**
     * Handle ImageNotInDatabase message from server.
     * This indicates the requested texture doesn't exist.
     */
    fun handleImageNotInDatabase(textureId: UUID) {
        Log.w(TAG, "🖼️ Texture not in database: $textureId")
        // Mark as failed so we don't retry
        missingTextures.add(textureId)
        updateStats { it.copy(failedCount = it.failedCount + 1) }
    }
    
    // Track textures that are known to be missing
    private val missingTextures = java.util.concurrent.ConcurrentHashMap.newKeySet<UUID>()
    
    // Track in-progress UDP texture transfers
    private val udpTextureTransfers = java.util.concurrent.ConcurrentHashMap<UUID, ByteArrayOutputStream>()
    
    /**
     * Handle ImageData message - first packet of UDP texture transfer.
     */
    fun handleImageData(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // ImageID block
            val textureId = com.linkpoint.protocol.types.getUUID(buffer)
            val codec = buffer.get().toInt() and 0xFF // 0 = raw, 2 = JPEG2000
            val size = buffer.int
            val packets = buffer.short.toInt() and 0xFFFF
            
            Log.d(TAG, "🖼️ ImageData: $textureId, codec=$codec, size=$size, packets=$packets")
            
            // Read image data
            if (buffer.remaining() > 0) {
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                
                if (packets == 1) {
                    // Single packet - decode immediately
                    processTextureData(textureId, data)
                } else {
                    // Multi-packet - start accumulating
                    val stream = ByteArrayOutputStream(size)
                    stream.write(data)
                    udpTextureTransfers[textureId] = stream
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling ImageData", e)
        }
    }
    
    /**
     * Handle ImagePacket message - subsequent packets of UDP texture transfer.
     */
    fun handleImagePacket(payload: ByteArray) {
        try {
            val buffer = java.nio.ByteBuffer.wrap(payload).order(java.nio.ByteOrder.LITTLE_ENDIAN)
            
            // ImageID block
            val textureId = com.linkpoint.protocol.types.getUUID(buffer)
            val packet = buffer.short.toInt() and 0xFFFF
            
            // Read image data
            if (buffer.remaining() > 0) {
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                
                val stream = udpTextureTransfers[textureId]
                if (stream != null) {
                    synchronized(stream) {
                        stream.write(data)
                    }
                    Log.d(TAG, "🖼️ ImagePacket: $textureId, packet=$packet, cumulative=${stream.size()} bytes")
                } else {
                    Log.w(TAG, "🖼️ ImagePacket for unknown transfer: $textureId")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling ImagePacket", e)
        }
    }
    
    /**
     * Complete a multi-packet texture transfer.
     */
    fun completeTextureTransfer(textureId: UUID) {
        val stream = udpTextureTransfers.remove(textureId) ?: return
        val data = stream.toByteArray()
        processTextureData(textureId, data)
    }
    
    /**
     * Process received texture data and cache it.
     */
    private fun processTextureData(textureId: UUID, data: ByteArray) {
        scope.launch(Dispatchers.IO) {
            try {
                val bitmap = decodeTexture(textureId, data)
                if (bitmap != null) {
                    cache(textureId.toString(), bitmap)
                    updateStats { it.copy(downloadedCount = it.downloadedCount + 1) }
                    Log.i(TAG, "🖼️ UDP texture completed: $textureId")
                } else {
                    updateStats { it.copy(failedCount = it.failedCount + 1) }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error processing texture data: $textureId", e)
            }
        }
    }
    
    private fun buildTextureUrl(textureId: UUID, discard: Int): String? {
        // Use capability URL if available
        // Per official SL viewer (lltexturefetch.cpp), the URL format is:
        // http_url + "/?texture_id=" + uuid
        // Note: discard_level is NOT passed as a query parameter - the SL protocol
        // uses HTTP Range headers for progressive/partial image loading instead.
        //
        // IMPORTANT: The asset CDN (asset-cdn.glb.agni.lindenlab.com) requires authentication
        // and does not support unauthenticated texture fetching. If no capability URL is
        // available, texture downloads will fail with HTTP 403 Forbidden.
        // In this case, we return null to indicate that texture fetching is not possible.
        val baseUrl = capabilityUrl ?: return null
        
        // Ensure HTTPS for Linden Lab servers (required for authentication)
        val secureUrl = if (baseUrl.startsWith("http://") && 
            (baseUrl.contains(".lindenlab.com") || baseUrl.contains(".secondlife.com"))) {
            baseUrl.replaceFirst("http://", "https://")
        } else {
            baseUrl
        }
        
        return "$secureUrl?texture_id=$textureId"
    }
    
    private fun decodeTexture(textureId: UUID, data: ByteArray): Bitmap? {
        val startTime = System.currentTimeMillis()
        
        return try {
            // Determine format
            val isJ2k = isJPEG2000(data)
            val format = if (isJ2k) "JPEG2000" else "Standard (PNG/JPEG)"
            
            Log.d(TAG, "🖼️ Decoding texture: $textureId (${data.size} bytes, $format)")
            
            // Check if it's JPEG2000 (J2K/JP2)
            val bitmap = if (isJ2k) {
                j2kDecodeAttempts.incrementAndGet()
                val result = decodeJPEG2000(data)
                if (result != null) {
                    j2kDecodeSuccesses.incrementAndGet()
                }
                result
            } else {
                // Try standard formats (PNG, JPEG)
                BitmapFactory.decodeByteArray(data, 0, data.size)
            }
            
            val durationMs = System.currentTimeMillis() - startTime
            
            bitmap?.let {
                Log.d(TAG, "🖼️ Texture decoded: $textureId (${it.width}x${it.height}, ${durationMs}ms)")
                NetworkLogger.logTextureDecode(textureId.toString(), true, format, durationMs)
                
                textureCache[textureId] = it
                updateStats { s -> s.copy(decodedCount = s.decodedCount + 1) }
            }
            
            if (bitmap == null) {
                Log.w(TAG, "🖼️ Texture decode returned null: $textureId")
                NetworkLogger.logTextureDecode(textureId.toString(), false, format, durationMs, "Decoder returned null")
            }
            
            bitmap
        } catch (e: Exception) {
            val durationMs = System.currentTimeMillis() - startTime
            Log.e(TAG, "🖼️ Texture decode error: $textureId - ${e.javaClass.simpleName}: ${e.message}")
            NetworkLogger.logTextureDecode(
                textureId.toString(), 
                false, 
                if (isJPEG2000(data)) "JPEG2000" else "Standard",
                durationMs,
                "${e.javaClass.simpleName}: ${e.message}"
            )
            
            lastError = "Decode: ${e.javaClass.simpleName}: ${e.message}"
            lastErrorTime = System.currentTimeMillis()
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
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    // Additional tracking for diagnostics (volatile for thread safety)
    @Volatile private var lastError: String? = null
    @Volatile private var lastErrorTime: Long = 0
    private var j2kDecodeAttempts = java.util.concurrent.atomic.AtomicInteger(0)
    private var j2kDecodeSuccesses = java.util.concurrent.atomic.AtomicInteger(0)
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): TextureManagerDiagnostics {
        val currentStats = _stats.value
        return TextureManagerDiagnostics(
            pendingDownloads = currentStats.pendingDownloads,
            downloadedCount = currentStats.downloadedCount,
            downloadedBytes = currentStats.downloadedBytes,
            failedCount = currentStats.failedCount,
            decodedCount = currentStats.decodedCount,
            decodeFailedCount = currentStats.decodeFailedCount,
            cachedTextureCount = textureCache.size,
            pendingRequestCount = pendingTextures.size,
            downloadQueueSize = downloadQueue.size,
            activeDownloads = activeDownloads.get(),
            hasTextureCapability = capabilityUrl != null,
            j2kDecodeAttempts = j2kDecodeAttempts.get(),
            j2kDecodeSuccesses = j2kDecodeSuccesses.get(),
            lastError = lastError,
            lastErrorTimeAgo = if (lastErrorTime > 0) System.currentTimeMillis() - lastErrorTime else null
        )
    }
    
    /**
     * Diagnostic data class for texture manager state
     */
    data class TextureManagerDiagnostics(
        val pendingDownloads: Int,
        val downloadedCount: Int,
        val downloadedBytes: Long,
        val failedCount: Int,
        val decodedCount: Int,
        val decodeFailedCount: Int,
        val cachedTextureCount: Int,
        val pendingRequestCount: Int,
        val downloadQueueSize: Int,
        val activeDownloads: Int,
        val hasTextureCapability: Boolean,
        val j2kDecodeAttempts: Int,
        val j2kDecodeSuccesses: Int,
        val lastError: String?,
        val lastErrorTimeAgo: Long?
    )
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
