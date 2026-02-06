package com.linkpoint.protocol.translation

import android.util.Log
import com.linkpoint.protocol.capabilities.CapabilityManager
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Lumiya Protocol Bridge - Coordinates all translation between Linkpoint and Lumiya protocols.
 * 
 * This is the main entry point for Lumiya compatibility. It coordinates:
 * 
 * 1. **Capability URL Management**: Applies URL repair logic from LumiyaTranslationLayer
 * 2. **Message Translation**: Uses MessageTranslation for protocol message handling
 * 3. **Request/Response Formatting**: Ensures LLSD requests match Lumiya's format
 * 
 * Usage:
 * ```kotlin
 * val bridge = LumiyaProtocolBridge(loginUrl)
 * 
 * // Fetch capabilities using Lumiya-compatible logic
 * val capabilities = bridge.fetchCapabilities(seedCapUrl)
 * 
 * // Make capability requests
 * val response = bridge.makeCapabilityRequest(capName, requestBody)
 * ```
 * 
 * This bridge ensures that Linkpoint can communicate with Second Life servers
 * using the same patterns that worked in Lumiya, solving issues where
 * "straight Kotlin is unworkable" due to subtle protocol differences.
 * 
 * @param loginUrl The login URL used for authentication (determines grid-specific behavior)
 * @see LumiyaTranslationLayer
 * @see MessageTranslation
 */
class LumiyaProtocolBridge(
    private val loginUrl: String
) {
    companion object {
        private const val TAG = "LumiyaProtocolBridge"
        
        // Timeouts matching Lumiya's HTTP client settings
        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 60L
        private const val WRITE_TIMEOUT_SECONDS = 30L
        
        // Retry configuration
        private const val MAX_RETRIES = 3
        private const val INITIAL_RETRY_DELAY_MS = 1000L
        private const val MAX_RETRY_DELAY_MS = 15000L
        
        // User-Agent for Lumiya-compatible requests
        private const val USER_AGENT = "Linkpoint/1.0 (Lumiya-compatible)"
        
    }
    
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Grid-specific configuration
    private val gridType = LumiyaTranslationLayer.detectGridType(loginUrl)
    private val isAgniGrid = LumiyaTranslationLayer.isAgniGrid(loginUrl)
    
    // HTTP client configured for Lumiya-style requests
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()
    
    // Cached capabilities
    private val capabilities = mutableMapOf<String, String>()
    
    init {
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ LUMIYA PROTOCOL BRIDGE INITIALIZED")
        Log.i(TAG, "╠══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ Login URL: ${loginUrl.take(60)}...")
        Log.i(TAG, "║ Grid Type: $gridType")
        Log.i(TAG, "║ Is Agni Grid: $isAgniGrid")
        Log.i(TAG, "║ URL Repair: ${LumiyaTranslationLayer.config.repairCapabilityUrls}")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
    }
    
    /**
     * Prepare a seed capability URL for use.
     * 
     * Applies all necessary repairs and validations.
     * 
     * @param seedCapUrl The raw seed capability URL from login response
     * @return The prepared URL ready for use
     */
    fun prepareSeedCapability(seedCapUrl: String): String {
        return LumiyaTranslationLayer.prepareSeedCapability(loginUrl, seedCapUrl)
    }
    
    /**
     * Fetch capabilities from the seed capability URL using Lumiya-compatible logic.
     * 
     * This method:
     * 1. Repairs the seed capability URL if needed
     * 2. Builds an LLSD array request with capability names (matching Lumiya's format)
     * 3. Parses the response and repairs returned capability URLs
     * 
     * @param seedCapUrl The seed capability URL from login response
     * @return Map of capability name to URL, or null on failure
     */
    suspend fun fetchCapabilities(seedCapUrl: String): Map<String, String>? = withContext(Dispatchers.IO) {
        val preparedUrl = prepareSeedCapability(seedCapUrl)
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ FETCHING CAPABILITIES (Lumiya-compatible)")
        Log.i(TAG, "║ Seed URL: ${preparedUrl.take(60)}...")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        
        // Build capability request using Lumiya's capability list
        val capabilityNames = if (LumiyaTranslationLayer.config.useLumiyaCapabilityList) {
            LumiyaTranslationLayer.getLumiyaCapabilityNames()
        } else {
            getStandardCapabilityNames()
        }
        
        val requestBody = buildCapabilityRequestBody(capabilityNames)
        
        var lastError: Exception? = null
        var retryDelay = INITIAL_RETRY_DELAY_MS
        
        repeat(MAX_RETRIES) { attempt ->
            try {
                if (attempt > 0) {
                    Log.d(TAG, "Capability fetch retry ${attempt + 1}/$MAX_RETRIES after ${retryDelay}ms")
                    delay(retryDelay)
                    retryDelay = minOf(retryDelay * 2, MAX_RETRY_DELAY_MS)
                }
                
                val request = Request.Builder()
                    .url(preparedUrl)
                    .header("Accept", "application/llsd+xml, application/xml, text/xml")
                    .header("Content-Type", "application/llsd+xml")
                    .header("User-Agent", USER_AGENT)
                    .post(requestBody.toRequestBody("application/llsd+xml".toMediaType()))
                    .build()
                
                val response = httpClient.newCall(request).execute()
                val responseCode = response.code
                val responseBody = response.body?.string()
                response.close()
                
                Log.d(TAG, "Capability response: HTTP $responseCode")
                
                if (!response.isSuccessful) {
                    Log.w(TAG, "Capability fetch failed with HTTP $responseCode")
                    lastError = Exception("HTTP $responseCode")
                    return@repeat
                }
                
                if (responseBody.isNullOrBlank()) {
                    Log.w(TAG, "Empty response body from capability fetch")
                    lastError = Exception("Empty response body")
                    return@repeat
                }
                
                // Parse and process response
                val result = parseCapabilityResponse(responseBody)
                
                if (result.isNullOrEmpty()) {
                    Log.w(TAG, "No capabilities parsed from response")
                    LumiyaTranslationLayer.logCapabilityDiagnostics(
                        "PARSE_EMPTY",
                        loginUrl,
                        preparedUrl,
                        responseBody.take(500),
                        "No capabilities in response"
                    )
                    lastError = Exception("No capabilities in response")
                    return@repeat
                }
                
                // Cache capabilities
                capabilities.clear()
                capabilities.putAll(result)
                
                Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                Log.i(TAG, "║ CAPABILITIES FETCHED SUCCESSFULLY")
                Log.i(TAG, "║ Count: ${result.size}")
                Log.i(TAG, "║ EventQueueGet: ${if (result.containsKey("EventQueueGet")) "✓" else "✗"}")
                Log.i(TAG, "║ GetTexture: ${if (result.containsKey("GetTexture")) "✓" else "✗"}")
                Log.i(TAG, "║ GetMesh: ${if (result.containsKey("GetMesh")) "✓" else "✗"}")
                Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                
                return@withContext result
                
            } catch (e: SocketTimeoutException) {
                Log.w(TAG, "Capability fetch timeout on attempt ${attempt + 1}")
                lastError = e
            } catch (e: Exception) {
                Log.e(TAG, "Capability fetch error on attempt ${attempt + 1}", e)
                lastError = e
            }
        }
        
        Log.e(TAG, "All capability fetch attempts failed", lastError)
        LumiyaTranslationLayer.logCapabilityDiagnostics(
            "FETCH_FAILED",
            loginUrl,
            preparedUrl,
            null,
            lastError?.message
        )
        
        null
    }
    
    /**
     * Build the LLSD request body for capability fetching.
     * 
     * Lumiya sends an LLSD array of capability names to the seed capability URL.
     */
    private fun buildCapabilityRequestBody(capabilityNames: List<String>): String {
        val llsdArray = LLSDArray().apply {
            capabilityNames.forEach { name ->
                add(LLSDString(name))
            }
        }
        
        return LLSDXmlUtils.wrap(llsdArray)
    }
    
    /**
     * Parse the capability response and repair URLs as needed.
     */
    private fun parseCapabilityResponse(responseBody: String): Map<String, String>? {
        return try {
            val llsd = LLSDParser.parseAuto(responseBody.toByteArray(Charsets.UTF_8), "application/llsd+xml")
            
            if (llsd !is LLSDMap) {
                Log.w(TAG, "Capability response is not a map: ${llsd.javaClass.simpleName}")
                return null
            }
            
            val result = mutableMapOf<String, String>()
            
            for (key in llsd.value.keys) {
                val url = llsd.getString(key) ?: continue
                if (url.isBlank()) continue
                
                // Apply URL repair if configured
                val repairedUrl = if (LumiyaTranslationLayer.config.repairCapabilityUrls) {
                    LumiyaTranslationLayer.repairUrl(loginUrl, url)
                } else {
                    url
                }
                
                result[key] = repairedUrl
                
                if (repairedUrl != url) {
                    Log.d(TAG, "Repaired capability URL for $key")
                }
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse capability response", e)
            null
        }
    }
    
    /**
     * Get standard capability names (non-Lumiya list).
     */
    private fun getStandardCapabilityNames(): List<String> {
        return listOf(
            CapabilityManager.CAP_EVENT_QUEUE,
            CapabilityManager.CAP_FETCH_INVENTORY,
            CapabilityManager.CAP_FETCH_LIB_INVENTORY,
            CapabilityManager.CAP_FETCH_INVENTORY_DESCENDENTS,
            CapabilityManager.CAP_GET_TEXTURE,
            CapabilityManager.CAP_GET_MESH,
            CapabilityManager.CAP_GET_MESH2,
            CapabilityManager.CAP_VIEW_STATS,
            CapabilityManager.CAP_AGENT_STATE,
            CapabilityManager.CAP_UPDATE_AGENT_INFO,
            CapabilityManager.CAP_UPLOAD_BAKED_TEXTURE,
            CapabilityManager.CAP_OBJECT_MEDIA,
            CapabilityManager.CAP_PARCEL_VOICE,
            CapabilityManager.CAP_PROVISION_VOICE,
            CapabilityManager.CAP_CHAT_PASS,
            CapabilityManager.CAP_ENVIRONMENT,
            CapabilityManager.CAP_EXT_ENVIRONMENT,
            CapabilityManager.CAP_AVATAR_PICKER,
            CapabilityManager.CAP_SEARCH_STATIC
        )
    }
    
    /**
     * Get a cached capability URL.
     */
    fun getCapability(name: String): String? = capabilities[name]
    
    /**
     * Check if a capability is available.
     */
    fun hasCapability(name: String): Boolean = capabilities.containsKey(name)
    
    /**
     * Make a capability request with Lumiya-compatible formatting.
     * 
     * @param capabilityName The capability name
     * @param body Optional LLSD body for POST requests
     * @return The parsed LLSD response or null on failure
     */
    suspend fun makeCapabilityRequest(
        capabilityName: String,
        body: LLSDValue? = null
    ): LLSDValue? = withContext(Dispatchers.IO) {
        val url = getCapability(capabilityName)
        if (url == null) {
            Log.w(TAG, "Capability not available: $capabilityName")
            return@withContext null
        }
        
        try {
            val requestBuilder = Request.Builder()
                .url(url)
                .header("Accept", "application/llsd+xml")
                .header("User-Agent", USER_AGENT)
            
            if (body != null) {
                val xml = LLSDXmlUtils.wrap(body)
                requestBuilder.post(xml.toRequestBody("application/llsd+xml".toMediaType()))
            } else {
                requestBuilder.get()
            }
            
            val response = httpClient.newCall(requestBuilder.build()).execute()
            val responseBody = response.body?.string()
            response.close()
            
            if (!response.isSuccessful) {
                Log.w(TAG, "Capability request failed: $capabilityName -> HTTP ${response.code}")
                return@withContext null
            }
            
            if (responseBody.isNullOrBlank()) {
                return@withContext null
            }
            
            LLSDParser.parseAuto(responseBody.toByteArray(Charsets.UTF_8), "application/llsd+xml")
        } catch (e: Exception) {
            Log.e(TAG, "Capability request error: $capabilityName", e)
            null
        }
    }
    
    /**
     * Get diagnostic information about the bridge state.
     */
    fun getDiagnostics(): BridgeDiagnostics {
        return BridgeDiagnostics(
            gridType = gridType,
            isAgniGrid = isAgniGrid,
            loginUrl = loginUrl,
            capabilityCount = capabilities.size,
            capabilityNames = capabilities.keys.toList().sorted(),
            config = LumiyaTranslationLayer.config
        )
    }
    
    /**
     * Diagnostic data for the bridge.
     */
    data class BridgeDiagnostics(
        val gridType: LumiyaTranslationLayer.GridType,
        val isAgniGrid: Boolean,
        val loginUrl: String,
        val capabilityCount: Int,
        val capabilityNames: List<String>,
        val config: LumiyaTranslationLayer.CompatibilityConfig
    )
    
    // ==================================================================================
    // ASSET TRANSFER METHODS
    // ==================================================================================
    
    /**
     * Fetch a texture using Lumiya-compatible URL handling.
     * 
     * This method:
     * 1. Repairs the GetTexture capability URL if needed
     * 2. Builds the proper texture request URL
     * 3. Fetches the texture with appropriate headers
     * 4. Validates the returned data
     * 
     * @param textureId The texture UUID to fetch
     * @return The raw texture data (JPEG2000 format) or null on failure
     */
    suspend fun fetchTexture(textureId: String): ByteArray? = withContext(Dispatchers.IO) {
        val textureCapUrl = getCapability("GetTexture")
        if (textureCapUrl == null) {
            Log.w(TAG, "GetTexture capability not available")
            return@withContext null
        }
        
        val url = LumiyaTranslationLayer.buildTextureUrl(loginUrl, textureCapUrl, textureId)
        val headers = LumiyaTranslationLayer.getAssetFetchHeaders(LumiyaTranslationLayer.AssetTransferType.TEXTURE)
        
        fetchAsset(url, headers, LumiyaTranslationLayer.AssetTransferType.TEXTURE, textureId)
    }
    
    /**
     * Fetch a mesh using Lumiya-compatible URL handling.
     * 
     * This method:
     * 1. Repairs the GetMesh/GetMesh2 capability URL if needed
     * 2. Builds the proper mesh request URL
     * 3. Fetches the mesh with appropriate headers
     * 4. Validates the returned data
     * 
     * @param meshId The mesh UUID to fetch
     * @return The raw mesh data (LLSD format) or null on failure
     */
    suspend fun fetchMesh(meshId: String): ByteArray? = withContext(Dispatchers.IO) {
        // Prefer GetMesh2 over GetMesh
        val meshCapUrl = getCapability("GetMesh2") ?: getCapability("GetMesh")
        if (meshCapUrl == null) {
            Log.w(TAG, "GetMesh/GetMesh2 capability not available")
            return@withContext null
        }
        
        val url = LumiyaTranslationLayer.buildMeshUrl(loginUrl, meshCapUrl, meshId)
        val headers = LumiyaTranslationLayer.getAssetFetchHeaders(LumiyaTranslationLayer.AssetTransferType.MESH)
        
        fetchAsset(url, headers, LumiyaTranslationLayer.AssetTransferType.MESH, meshId)
    }
    
    /**
     * Generic asset fetching with Lumiya-compatible handling.
     * 
     * @param url The prepared asset URL
     * @param headers HTTP headers for the request
     * @param assetType The type of asset being fetched
     * @param assetId The asset ID (for logging)
     * @return The raw asset data or null on failure
     */
    private suspend fun fetchAsset(
        url: String,
        headers: Map<String, String>,
        assetType: LumiyaTranslationLayer.AssetTransferType,
        assetId: String
    ): ByteArray? = withContext(Dispatchers.IO) {
        try {
            val requestBuilder = Request.Builder().url(url)
            headers.forEach { (key, value) ->
                requestBuilder.header(key, value)
            }
            
            val response = httpClient.newCall(requestBuilder.build()).execute()
            
            if (!response.isSuccessful) {
                LumiyaTranslationLayer.logAssetTransferDiagnostics(
                    "FETCH_FAILED",
                    assetType,
                    assetId,
                    url,
                    null,
                    "HTTP ${response.code}"
                )
                return@withContext null
            }
            
            val data = response.body?.bytes()
            response.close()
            
            if (data == null || data.isEmpty()) {
                LumiyaTranslationLayer.logAssetTransferDiagnostics(
                    "EMPTY_RESPONSE",
                    assetType,
                    assetId,
                    url,
                    0,
                    "Empty response body"
                )
                return@withContext null
            }
            
            // Validate the received data
            if (!LumiyaTranslationLayer.validateAssetData(data, assetType)) {
                LumiyaTranslationLayer.logAssetTransferDiagnostics(
                    "INVALID_DATA",
                    assetType,
                    assetId,
                    url,
                    data.size,
                    "Data validation failed"
                )
                return@withContext null
            }
            
            LumiyaTranslationLayer.logAssetTransferDiagnostics(
                "SUCCESS",
                assetType,
                assetId,
                url,
                data.size,
                null
            )
            
            data
        } catch (e: Exception) {
            LumiyaTranslationLayer.logAssetTransferDiagnostics(
                "ERROR",
                assetType,
                assetId,
                url,
                null,
                "${e.javaClass.simpleName}: ${e.message}"
            )
            null
        }
    }
    
    /**
     * Prepare an asset URL for external use.
     * 
     * Use this when you need a repaired asset URL for use outside of this bridge.
     * 
     * @param capabilityName The capability name (e.g., "GetTexture", "GetMesh")
     * @param assetId The asset UUID
     * @param assetType The type of asset
     * @return The prepared asset URL or null if capability unavailable
     */
    fun prepareAssetUrl(
        capabilityName: String,
        assetId: String,
        assetType: LumiyaTranslationLayer.AssetTransferType
    ): String? {
        val capUrl = getCapability(capabilityName) ?: return null
        
        val preparedCapUrl = LumiyaTranslationLayer.prepareAssetUrl(loginUrl, capUrl, assetType)
        
        return when (assetType) {
            LumiyaTranslationLayer.AssetTransferType.TEXTURE -> "$preparedCapUrl?texture_id=$assetId"
            LumiyaTranslationLayer.AssetTransferType.MESH -> "$preparedCapUrl?mesh_id=$assetId"
            LumiyaTranslationLayer.AssetTransferType.SOUND -> "$preparedCapUrl?asset_id=$assetId"
            LumiyaTranslationLayer.AssetTransferType.ANIMATION -> "$preparedCapUrl?asset_id=$assetId"
            else -> "$preparedCapUrl?asset_id=$assetId"
        }
    }
    
    /**
     * Check if the bridge has texture fetch capability.
     */
    fun hasTextureCapability(): Boolean = hasCapability("GetTexture")
    
    /**
     * Check if the bridge has mesh fetch capability.
     */
    fun hasMeshCapability(): Boolean = hasCapability("GetMesh2") || hasCapability("GetMesh")
    
    /**
     * Clean up resources.
     */
    fun shutdown() {
        scope.cancel()
        capabilities.clear()
        Log.i(TAG, "Lumiya Protocol Bridge shutdown")
    }
}
