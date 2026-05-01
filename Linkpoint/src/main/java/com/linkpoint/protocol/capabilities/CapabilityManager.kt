package com.linkpoint.protocol.capabilities

import android.util.Log
import com.linkpoint.network.core.HttpRequestOptions
import com.linkpoint.network.core.PolicyClass
import com.linkpoint.network.core.RequestThrottler
import com.linkpoint.protocol.llsd.*
import com.linkpoint.protocol.translation.LinkpointTranslationLayer
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

interface CapabilityRequester {
    suspend fun request(capName: String, body: LLSDValue? = null): LLSDValue?
    fun getCapability(name: String): String?
    fun hasCapability(name: String): Boolean

    /**
     * Issue an HTTP GET against [capName] with the given repeated query
     * parameters. Required by capabilities that follow the
     * `<base_url>?key=v1&key=v2&...` shape rather than a POST LLSD body —
     * notably `GetDisplayNames` (`?ids=<uuid>&ids=<uuid>...`). Default
     * implementation falls back to the body-less `request()` for fakes.
     */
    suspend fun requestWithQuery(
        capName: String,
        queryParams: List<Pair<String, String>>
    ): LLSDValue? = request(capName, null)
}

/**
 * Manages Second Life Capabilities (Caps)
 * Caps are HTTP endpoints that provide various services.
 * 
 * Now enhanced with:
 * - Firestorm-style retry logic with exponential backoff
 * - Request throttling for inventory operations
 * - Retry-After header support
 * - Per-capability request options
 * - Linkpoint-compatible URL repair for Agni grid
 */
class CapabilityManager : CapabilityRequester {
    
    companion object {
        private const val TAG = "CapabilityManager"
        
        // Diagnostic truncation length for URLs in debug reports
        private const val DIAGNOSTIC_URL_TRUNCATE_LENGTH = 50
        private const val SEED_CAP_HEADER_PREVIEW_BYTES = 64
        private const val SEED_CAP_ASCII_CHECK_BYTES = 16
        private const val SEED_CAP_RESPONSE_PREVIEW_CHARS = 200
        private const val UTF8_BOM = '\uFEFF'
        
        // Common capability names
        const val CAP_EVENT_QUEUE = "EventQueueGet"
        const val CAP_FETCH_INVENTORY = "FetchInventory2"
        const val CAP_FETCH_LIB_INVENTORY = "FetchLib2"
        const val CAP_FETCH_INVENTORY_DESCENDENTS = "FetchInventoryDescendents2"
        const val CAP_GET_TEXTURE = "GetTexture"
        const val CAP_GET_MESH = "GetMesh"
        const val CAP_GET_MESH2 = "GetMesh2"
        const val CAP_VIEW_STATS = "ViewerStats"
        const val CAP_AGENT_STATE = "AgentState"
        const val CAP_AGENT_PROFILE = "AgentProfile"
        const val CAP_UPDATE_AGENT_INFO = "UpdateAgentInformation"
        const val CAP_UPLOAD_BAKED_TEXTURE = "UploadBakedTexture"
        const val CAP_OBJECT_MEDIA = "ObjectMedia"
        const val CAP_OBJECT_MEDIA_NAVIGATE = "ObjectMediaNavigate"
        const val CAP_PARCEL_VOICE = "ParcelVoiceInfoRequest"
        const val CAP_PROVISION_VOICE = "ProvisionVoiceAccountRequest"

        /**
         * VoiceSignalingRequest — paired with [CAP_PROVISION_VOICE] for the
         * new WebRTC spatial voice path. Used solely for trickling ICE
         * candidates after the SDP answer has been received. POST takes
         * an LLSD body; see [com.linkpoint.voice.WebRtcVoiceSession] for
         * payload shapes. Per the LL "WebRTC on Second Life, a
         * Developer's View" document the cap is region-scoped (the
         * simulator forwards the candidates to its bundled
         * Second Life Voice Server).
         */
        const val CAP_VOICE_SIGNALING_REQUEST = "VoiceSignalingRequest"
        const val CAP_CHAT_PASS = "ChatSessionRequest"
        const val CAP_COPY_INVENTORY_FROM_NOTECARD = "CopyInventoryFromNotecard"
        const val CAP_UPDATE_NOTECARD_AGENT = "UpdateNotecardAgentInventory"
        const val CAP_UPDATE_NOTECARD_TASK = "UpdateNotecardTaskInventory"
        const val CAP_UPDATE_SCRIPT_AGENT = "UpdateScriptAgent"
        const val CAP_UPDATE_SCRIPT_TASK = "UpdateScriptTask"
        const val CAP_GROUP_PROFILE = "GroupProfile"
        const val CAP_ENVIRONMENT = "EnvironmentSettings"
        const val CAP_EXT_ENVIRONMENT = "ExtEnvironment"
        const val CAP_REGION_EXPERIENCE = "RegionExperiences"
        const val CAP_SIMULATE_LURE = "SimulatorLure"
        const val CAP_AVATAR_PICKER = "AvatarPickerSearch"
        const val CAP_SEARCH_STATIC = "SearchStatRequest"
        
        // Voice moderation capability (Project Voice Moderation)
        const val CAP_VOICE_MODERATION = "VoiceModeration"
        
        // Inventory operations (added for full viewer compliance)
        const val CAP_CREATE_INVENTORY_CATEGORY = "CreateInventoryCategory"
        const val CAP_MOVE_INVENTORY_ITEM = "MoveItemsToTrash"  // Uses AISv3
        const val CAP_UPDATE_INVENTORY_ITEM = "UpdateInventoryItem"
        const val CAP_INVENTORY_API = "InventoryAPIv3"  // AISv3 endpoint
        
        // Display names
        const val CAP_GET_DISPLAY_NAMES = "GetDisplayNames"
        const val CAP_SET_DISPLAY_NAME = "SetDisplayName"
        
        // Simulator features
        const val CAP_SIMULATOR_FEATURES = "SimulatorFeatures"
        const val CAP_AGENT_PREFERENCES = "AgentPreferences"
        const val CAP_UPDATE_AGENT_LANGUAGE = "UpdateAgentLanguage"
        
        // Render materials (PBR)
        const val CAP_RENDER_MATERIALS = "RenderMaterials"
        const val CAP_GROUP_MEMBER_DATA = "GroupMemberData"
        const val CAP_CHAT_SEND = "ChatSend"
        const val CAP_FRIENDSHIP_TERMINATE = "FriendshipTerminate"

        const val CAP_TELEPORT_LOCATION = "TeleportLocation"
        const val CAP_SEARCH_LAND = "SearchLand"
        const val CAP_GET_SCRIPT_RUNNING = "GetScriptRunning"
        const val CAP_SET_SCRIPT_RUNNING = "SetScriptRunning"
        const val CAP_GET_SCRIPT_TASK_INFO = "GetScriptTaskInfo"
        const val CAP_CREATE_INVENTORY_ITEM = "CreateInventoryItem"
        const val CAP_NEW_FILE_AGENT_INVENTORY = "NewFileAgentInventory"
        const val CAP_UPLOAD_AGENT_PROFILE_IMAGE = "UploadAgentProfileImage"
        const val CAP_UPLOAD_AGENT_BAKED_TEXTURE = "UploadAgentBakedTexture"
        
        // Capabilities that are inventory-related (for throttling)
        private val INVENTORY_CAPS = setOf(
            CAP_FETCH_INVENTORY,
            CAP_FETCH_LIB_INVENTORY,
            CAP_FETCH_INVENTORY_DESCENDENTS,
            CAP_CREATE_INVENTORY_CATEGORY,
            CAP_INVENTORY_API
        )
        
        // Capabilities that are asset-related
        private val ASSET_CAPS = setOf(
            CAP_GET_TEXTURE,
            CAP_GET_MESH,
            CAP_GET_MESH2
        )
        
        // Retryable HTTP status codes - defined once for efficiency
        private val RETRYABLE_HTTP_CODES = setOf(503, 429, 500, 502, 504)
        
        // Retryable message patterns for IOException detection
        private val RETRYABLE_MESSAGE_PATTERNS = listOf("EOF", "reset", "closed", "timeout", "ECONNRESET")
        
        // Background retry constants
        private const val BACKGROUND_RETRY_INITIAL_DELAY_MS = 10_000L  // 10 seconds
        private const val BACKGROUND_RETRY_MAX_DELAY_MS = 60_000L     // 60 seconds
        private const val BACKGROUND_RETRY_MAX_ATTEMPTS = 10
    }
    
    // Request throttler for rate limiting
    private val throttler = RequestThrottler.getInstance()

    /**
     * Optional Android Context, set by the application after construction
     * (LinkpointApp wires this in initializeManagers). When present, the
     * request path tries [com.linkpoint.network.CronetHttpClient] first
     * for HTTP/3 (QUIC) — which gives capability traffic the same
     * connection-migration win that asset traffic got in commit 163af1df.
     * When absent (no-arg unit-test constructor, GridConnection, etc.),
     * Cronet is skipped and OkHttp is the only path. Either way the
     * fallback chain is H3 → H2 → H1.1.
     */
    @Volatile var androidContext: android.content.Context? = null

    // Default HTTP client for general requests.
    //
    // We explicitly request `[HTTP_2, HTTP_1_1]` even though that's OkHttp's
    // documented default, because (a) future OkHttp version bumps could
    // change the default and (b) being explicit lets the build flag a
    // problem if HTTP_2 is ever removed from `okhttp3.Protocol`. ALPN
    // negotiation happens at the TLS layer; on Android 10+ the platform
    // SSL engine handles this directly. Conscrypt (installed as the
    // primary JCA provider in `LinkpointApp.onCreate`) is the fallback
    // for older devices where the platform ALPN is unreliable.
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .build()

    // Optimized client for inventory requests
    private val inventoryClient = createClientForOptions(HttpRequestOptions.forInventory())

    // Optimized client for event queue (long polling)
    private val eventQueueClient = createClientForOptions(HttpRequestOptions.forEventQueue())

    /**
     * Create an HTTP client with specific options.
     */
    private fun createClientForOptions(options: HttpRequestOptions): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(options.timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(
                if (options.transferTimeoutSeconds > 0) options.transferTimeoutSeconds
                else options.timeoutSeconds * 2,
                TimeUnit.SECONDS
            )
            .writeTimeout(options.timeoutSeconds, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .followRedirects(options.followRedirects)
            // Same explicit-protocols rationale as `httpClient` above —
            // every cap client should attempt H2 ALPN.
            .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
            .build()
    }
    
    /**
     * Get the appropriate HTTP client for a capability.
     */
    private fun getClientForCapability(capName: String): OkHttpClient {
        return when {
            INVENTORY_CAPS.contains(capName) -> inventoryClient
            capName == CAP_EVENT_QUEUE -> eventQueueClient
            else -> httpClient
        }
    }
    
    /**
     * Get the request options for a capability.
     */
    private fun getOptionsForCapability(capName: String): HttpRequestOptions {
        return when {
            INVENTORY_CAPS.contains(capName) -> HttpRequestOptions.forInventory()
            ASSET_CAPS.contains(capName) -> HttpRequestOptions.forTextures()
            capName == CAP_EVENT_QUEUE -> HttpRequestOptions.forEventQueue()
            else -> HttpRequestOptions()
        }
    }
    
    private val capabilities = ConcurrentHashMap<String, String>()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private var seedCapability: String? = null
    private var eventQueueJob: Job? = null
    
    private val _isReady = MutableStateFlow(false)
    val isReady: StateFlow<Boolean> = _isReady

    private data class EventHandlerRegistration(
        val handler: EventHandler,
        val dispatcher: CoroutineDispatcher
    )

    // Event handlers
    private val eventHandlers = ConcurrentHashMap<String, MutableList<EventHandlerRegistration>>()

    // Initialization tracking for diagnostics (volatile for thread safety)
    @Volatile private var initializationStartTime: Long = 0
    @Volatile private var initializationEndTime: Long = 0
    @Volatile private var lastInitializationError: String? = null
    @Volatile private var lastInitializationAttempts: Int = 0
    @Volatile private var lastSeedCapabilityUsed: String? = null

    // Linkpoint translation layer support
    @Volatile private var loginUrl: String? = null

    // Background retry for capability initialization
    private var backgroundRetryJob: Job? = null
    @Volatile private var backgroundRetryCount: Int = 0

    
    /**
     * Initialize capabilities from seed with Linkpoint translation layer support.
     * 
     * This overload accepts the login URL to enable Linkpoint-compatible capability URL
     * repair and grid-specific handling.
     * 
     * @param seedCap The seed capability URL
     * @param loginUrlParam The login URL used for authentication (enables URL repair)
     * @return true if initialization succeeded
     */
    suspend fun initialize(seedCap: String, loginUrlParam: String): Boolean {
        this.loginUrl = loginUrlParam
        
        // Apply URL repair to seed capability
        val repairedSeedCap = LinkpointTranslationLayer.prepareSeedCapability(loginUrlParam, seedCap)
        
        if (repairedSeedCap != seedCap) {
            Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ LUMIYA TRANSLATION: Seed capability URL repaired")
            Log.i(TAG, "║ Grid Type: ${LinkpointTranslationLayer.detectGridType(loginUrlParam)}")
            Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        }
        
        return initialize(repairedSeedCap)
    }
    
    /**
     * Initialize capabilities from seed
     */
    suspend fun initialize(seedCap: String): Boolean = withContext(Dispatchers.IO) {
        seedCapability = seedCap
        lastSeedCapabilityUsed = seedCap
        initializationStartTime = System.currentTimeMillis()
        lastInitializationError = null
        lastInitializationAttempts = 0
        
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ CAPABILITY INITIALIZATION STARTING")
        Log.i(TAG, "╠══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ Seed URL: ${seedCap.take(80)}...")
        val currentLoginUrl = loginUrl
        Log.i(TAG, "║ Translation Mode: ${if (currentLoginUrl != null) "ENABLED" else "DISABLED"}")
        if (currentLoginUrl != null) {
            Log.i(TAG, "║ Grid Type: ${LinkpointTranslationLayer.detectGridType(currentLoginUrl)}")
        }
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        
        // Use reference capability list when enabled for better compatibility
        val capNames = if (currentLoginUrl != null && LinkpointTranslationLayer.config.useReferenceCapabilityList) {
            Log.d(TAG, "Using Linkpoint-compatible capability list")
            LinkpointTranslationLayer.getReferenceCapabilityNames()
        } else {
            listOf(
                CAP_EVENT_QUEUE,
                CAP_FETCH_INVENTORY,
                CAP_FETCH_LIB_INVENTORY,
                CAP_FETCH_INVENTORY_DESCENDENTS,
                CAP_GET_TEXTURE,
                CAP_GET_MESH,
                CAP_GET_MESH2,
                CAP_VIEW_STATS,
                CAP_AGENT_STATE,
                CAP_UPDATE_AGENT_INFO,
                CAP_UPLOAD_BAKED_TEXTURE,
                CAP_OBJECT_MEDIA,
                CAP_PARCEL_VOICE,
                CAP_PROVISION_VOICE,
                CAP_VOICE_SIGNALING_REQUEST,
                CAP_CHAT_PASS,
                CAP_ENVIRONMENT,
                CAP_EXT_ENVIRONMENT,
                CAP_AVATAR_PICKER,
                CAP_SEARCH_STATIC,
                CAP_GROUP_PROFILE
            )
        }
        
        Log.d(TAG, "Requesting ${capNames.size} capabilities from seed...")
        
        val options = HttpRequestOptions.forLogin()
        var seedCaps: Map<String, String>? = null
        var lastException: Exception? = null
        
        // Retry seed caps request (initial attempt + retries)
        for (attempt in 0..options.retries) {
            lastInitializationAttempts = attempt + 1
            try {
                if (attempt > 0) {
                    val delayMs = options.calculateRetryDelay(attempt - 1)
                    Log.w(TAG, "Retrying seed capability request (attempt ${attempt + 1}/${options.retries + 1}) after ${delayMs}ms")
                    delay(delayMs)
                }
                
                Log.d(TAG, "Seed capability request attempt ${attempt + 1}...")
                seedCaps = requestCapabilities(seedCap, capNames)
                
                if (!seedCaps.isNullOrEmpty()) {
                    Log.i(TAG, "Seed capability request succeeded with ${seedCaps.size} capabilities")
                    break
                }
                
                lastException = Exception("Seed capability returned no entries")
                lastInitializationError = "Seed capability returned empty response"
                Log.w(TAG, "Seed capability response was empty on attempt ${attempt + 1}")
            } catch (e: Exception) {
                lastException = e
                lastInitializationError = "Exception: ${e.javaClass.simpleName}: ${e.message}"
                Log.e(TAG, "Seed capability request failed on attempt ${attempt + 1}", e)
            }
        }
        
        val resolvedCaps = seedCaps
        if (resolvedCaps.isNullOrEmpty()) {
            _isReady.value = false
            initializationEndTime = System.currentTimeMillis()
            val duration = initializationEndTime - initializationStartTime
            Log.e(TAG, "╔══════════════════════════════════════════════════════════════════")
            Log.e(TAG, "║ CAPABILITY INITIALIZATION FAILED")
            Log.e(TAG, "╠══════════════════════════════════════════════════════════════════")
            Log.e(TAG, "║ Duration: ${duration}ms")
            Log.e(TAG, "║ Attempts: $lastInitializationAttempts")
            Log.e(TAG, "║ Last Error: $lastInitializationError")
            Log.e(TAG, "║ Seed URL: ${seedCap.take(80)}...")
            Log.e(TAG, "╚══════════════════════════════════════════════════════════════════")
            return@withContext false
        }
        
        // Apply URL repair to each capability URL if enabled
        val shouldRepairUrls = loginUrl != null && LinkpointTranslationLayer.config.repairCapabilityUrls
        var repairedCount = 0
        
        resolvedCaps.forEach { (name, url) ->
            val finalUrl = if (shouldRepairUrls) {
                val repairedUrl = LinkpointTranslationLayer.repairUrl(loginUrl ?: "", url)
                if (repairedUrl != url) {
                    repairedCount++
                    Log.d(TAG, "Repaired URL for $name")
                }
                repairedUrl
            } else {
                url
            }
            capabilities[name] = finalUrl
            Log.d(TAG, "Capability: $name -> ${finalUrl.take(60)}...")
        }
        
        if (repairedCount > 0) {
            Log.i(TAG, "Linkpoint translation: Repaired $repairedCount capability URLs")
        }
        
        // Start event queue
        val eqUrl = getCapability(CAP_EVENT_QUEUE)
        if (eqUrl != null) {
            Log.i(TAG, "Starting EventQueue from capability...")
            startEventQueue(eqUrl)
        } else {
            Log.w(TAG, "⚠️ EventQueueGet capability not available - events won't work!")
        }
        
        initializationEndTime = System.currentTimeMillis()
        val duration = initializationEndTime - initializationStartTime
        
        _isReady.value = true
        Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ CAPABILITY INITIALIZATION SUCCESS")
        Log.i(TAG, "╠══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ Duration: ${duration}ms")
        Log.i(TAG, "║ Capabilities loaded: ${capabilities.size}")
        Log.i(TAG, "║ EventQueue: ${if (eqUrl != null) "AVAILABLE" else "MISSING"}")
        Log.i(TAG, "║ GetTexture: ${if (hasCapability(CAP_GET_TEXTURE)) "✓" else "✗"}")
        Log.i(TAG, "║ GetMesh: ${if (hasCapability(CAP_GET_MESH) || hasCapability(CAP_GET_MESH2)) "✓" else "✗"}")
        Log.i(TAG, "║ FetchInventory: ${if (hasCapability(CAP_FETCH_INVENTORY)) "✓" else "✗"}")
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        true
    }

    /**
     * Start a background retry loop for capability initialization.
     *
     * When the initial seed capability request fails (e.g. empty response on mobile networks),
     * this launches a coroutine that periodically retries with exponential backoff.
     * The loop stops as soon as capabilities are successfully loaded or the manager is shut down.
     *
     * @param seedCap The seed capability URL to retry
     * @param loginUrlParam The login URL for URL repair
     */
    fun startBackgroundRetry(seedCap: String, loginUrlParam: String) {
        // Don't start if already ready or already retrying
        if (_isReady.value) return
        backgroundRetryJob?.cancel()
        backgroundRetryCount = 0

        backgroundRetryJob = scope.launch {
            while (isActive && !_isReady.value && backgroundRetryCount < BACKGROUND_RETRY_MAX_ATTEMPTS) {
                backgroundRetryCount++
                val delayMs = minOf(
                    BACKGROUND_RETRY_INITIAL_DELAY_MS * (1L shl minOf(backgroundRetryCount - 1, 4)),
                    BACKGROUND_RETRY_MAX_DELAY_MS
                )
                Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                Log.i(TAG, "║ BACKGROUND CAPABILITY RETRY $backgroundRetryCount/$BACKGROUND_RETRY_MAX_ATTEMPTS")
                Log.i(TAG, "║ Waiting ${delayMs}ms before retry...")
                Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                delay(delayMs)

                if (_isReady.value) break

                val success = try {
                    initialize(seedCap, loginUrlParam)
                } catch (e: Exception) {
                    Log.e(TAG, "Background capability retry $backgroundRetryCount failed: ${e.message}")
                    false
                }

                if (success) {
                    Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                    Log.i(TAG, "║ BACKGROUND RETRY SUCCEEDED on attempt $backgroundRetryCount")
                    Log.i(TAG, "║ Capabilities now available: ${capabilities.size}")
                    Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                    break
                }
            }

            if (!_isReady.value) {
                Log.e(TAG, "Background capability retry exhausted all $BACKGROUND_RETRY_MAX_ATTEMPTS attempts")
            }
        }
    }

    /**
     * Request capabilities from seed
     * 
     * Per Second Life protocol specification:
     * - POST an LLSD array of strings (capability names) to the seed capability URL
     * - Receive an LLSD map of capability names to URLs
     * 
     * See: https://wiki.secondlife.com/wiki/SeedCapability
     */
    private suspend fun requestCapabilities(
        seedUrl: String,
        capNames: List<String>
    ): Map<String, String>? = withContext(Dispatchers.IO) {
        // IMPORTANT: Seed capability expects an LLSD array of capability names, NOT a map
        // This was the root cause of "Seed capability returned empty response" errors
        val requestBody = LLSDArray().apply {
            capNames.forEach { name ->
                add(LLSDString(name))
            }
        }
        
        val xml = LLSDXmlUtils.wrap(requestBody)
        
        Log.d(TAG, "Requesting capabilities from: ${seedUrl.take(80)}...")
        Log.d(TAG, "Request body length: ${xml.length} bytes")
        
        try {
            val request = Request.Builder()
                .url(seedUrl)
                .header("Accept", "application/llsd+xml, application/llsd+binary")
                .post(xml.toRequestBody("application/llsd+xml".toMediaType()))
                .build()
            
            val startTime = System.currentTimeMillis()
            val response = httpClient.newCall(request).execute()
            val elapsed = System.currentTimeMillis() - startTime
            val negotiatedProtocol = response.protocol.toString()

            Log.d(TAG, "Seed capability response: HTTP ${response.code} (${negotiatedProtocol}) in ${elapsed}ms")
            // Feed the protocol stat tracker so the Settings → Debug Report
            // "HTTP/2 Requests" counter actually reflects capability traffic.
            // Previously this was 0/0 because nothing called the tracker for
            // cap requests, making the report look like H2 was broken even
            // when it wasn't.
            com.linkpoint.network.NetworkLogger.logCapabilityResponse(
                capName = "SeedCapability",
                success = response.isSuccessful,
                durationMs = elapsed,
                protocol = negotiatedProtocol
            )

            if (!response.isSuccessful) {
                Log.e(TAG, "Seed capability request failed with HTTP ${response.code}: ${response.message}")
                lastInitializationError = "HTTP ${response.code}: ${response.message}"
                response.close()
                return@withContext null
            }
            
            // Use try-finally to ensure response is always closed
            val contentType = response.header("Content-Type")
            val bodyBytes: ByteArray?
            try {
                bodyBytes = response.body?.bytes()
            } finally {
                response.close()
            }
            
            if (bodyBytes == null || bodyBytes.isEmpty()) {
                Log.e(TAG, "Seed capability response body is empty")
                lastInitializationError = "Empty response body"
                return@withContext null
            }
            
            Log.d(TAG, "Response body length: ${bodyBytes.size} bytes")
            
            // Log first few chars for debugging (don't log full body for security)
            val isXmlContentType = contentType?.contains("xml", ignoreCase = true) == true
            val previewSize = minOf(bodyBytes.size, SEED_CAP_HEADER_PREVIEW_BYTES)
            val headerBytes = bodyBytes.copyOfRange(0, previewSize)
            val asciiCheckSize = minOf(headerBytes.size, SEED_CAP_ASCII_CHECK_BYTES)
            val isAscii = headerBytes.copyOfRange(0, asciiCheckSize).all { byte ->
                val code = byte.toInt() and 0xFF
                code == '\t'.code || code == '\n'.code || code == '\r'.code || code in ' '.code..'~'.code
            }
            val shouldInspectText = isXmlContentType || isAscii
            val headerText = if (shouldInspectText) {
                runCatching { headerBytes.toString(Charsets.UTF_8) }.getOrNull()
            } else {
                null
            }
            val trimmedHeader = headerText?.trimStart(UTF8_BOM, ' ', '\n', '\r', '\t')
            val looksLikeXml = trimmedHeader?.startsWith("<?xml", ignoreCase = true) == true ||
                trimmedHeader?.startsWith("<llsd", ignoreCase = true) == true
            val bodyText = if (looksLikeXml || isXmlContentType) {
                runCatching { bodyBytes.toString(Charsets.UTF_8) }.getOrNull()
            } else {
                null
            }
            if (bodyText != null) {
                val preview = bodyText.take(SEED_CAP_RESPONSE_PREVIEW_CHARS)
                    .replace("\n", " ")
                    .replace("\r", "")
                Log.d(TAG, "Response preview: $preview...")
            } else {
                Log.d(TAG, "Response preview: <binary LLSD omitted>")
            }
            
            val llsd = try {
                LLSDParser.parseAuto(bodyBytes, contentType)
            } catch (e: Exception) {
                Log.e(TAG, "Failed to parse LLSD from seed capability response", e)
                lastInitializationError = "LLSD parse error: ${e.message}"
                return@withContext null
            }
            
            if (llsd == LLSDUndefined) {
                Log.e(TAG, "Seed capability response is not valid XML or binary LLSD")
                lastInitializationError = "LLSD parse error: invalid format"
                return@withContext null
            }
            
            if (llsd is LLSDMap) {
                val result = llsd.value.keys.mapNotNull { key ->
                    llsd.getString(key)?.takeIf { it.isNotEmpty() }?.let { key to it }
                }.toMap()
                
                Log.d(TAG, "Parsed ${result.size} capabilities from response")
                
                if (result.isEmpty()) {
                    Log.w(TAG, "LLSD response parsed successfully but contained no capability URLs")
                    lastInitializationError = "LLSD contained no capability URLs"
                }
                
                result
            } else {
                Log.e(TAG, "LLSD response is not a map, got: ${llsd?.javaClass?.simpleName}")
                lastInitializationError = "LLSD response is not a map"
                null
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "Seed capability request timed out", e)
            lastInitializationError = "Socket timeout: ${e.message}"
            null
        } catch (e: java.net.UnknownHostException) {
            Log.e(TAG, "Cannot resolve seed capability host", e)
            lastInitializationError = "DNS resolution failed: ${e.message}"
            null
        } catch (e: java.io.IOException) {
            Log.e(TAG, "IO error requesting capabilities", e)
            lastInitializationError = "IO error: ${e.message}"
            null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request capabilities", e)
            lastInitializationError = "Exception: ${e.javaClass.simpleName}: ${e.message}"
            null
        }
    }
    
    /**
     * Get a capability URL
     */
    override fun getCapability(name: String): String? = capabilities[name]
    
    /**
     * Check if a capability is available
     */
    override fun hasCapability(name: String): Boolean = capabilities.containsKey(name)
    
    /**
     * Make a capability request with Firestorm-style retry logic.
     * 
     * Features:
     * - Automatic retries with exponential backoff
     * - Retry-After header support
     * - Request throttling for inventory caps
     * - Appropriate timeouts per capability type
     */
    override suspend fun request(
        capName: String,
        body: LLSDValue?
    ): LLSDValue? = withContext(Dispatchers.IO) {
        val url = getCapability(capName) ?: return@withContext null
        val options = getOptionsForCapability(capName)
        val client = getClientForCapability(capName)
        
        // Apply throttling for inventory requests
        if (INVENTORY_CAPS.contains(capName)) {
            val waitMs = throttler.acquire(PolicyClass.INVENTORY)
            if (waitMs > 0) {
                Log.d(TAG, "Throttled $capName request for ${waitMs}ms")
            }
        }
        
        var lastException: Exception? = null
        var retryAfterSeconds: Int? = null
        
        repeat(options.retries + 1) { attempt ->
            try {
                if (attempt > 0) {
                    // Calculate retry delay using Firestorm-style backoff
                    val delayMs = options.calculateRetryDelay(attempt - 1, retryAfterSeconds)
                    Log.d(TAG, "Retrying $capName request (attempt ${attempt + 1}/${options.retries + 1}) after ${delayMs}ms")
                    delay(delayMs)
                }
                
                // Cronet primary path (H3/QUIC when the cap host advertises
                // it; otherwise H2 over Cronet's TLS stack). Falls through
                // to OkHttp+Conscrypt (H2/H1.1) on any non-2xx, network
                // failure, or when the engine isn't available (no Android
                // context wired, native lib missing, etc.). Keeps the
                // exact same retry / Retry-After / throttle behaviour.
                val xmlBody: ByteArray? = body?.let { LLSDXmlUtils.wrap(it).toByteArray(Charsets.UTF_8) }
                val ctx = androidContext
                if (ctx != null) {
                    val cronet = com.linkpoint.network.CronetHttpClient.getOrCreate(ctx)
                    if (cronet.isAvailable) {
                        val cronetStart = System.currentTimeMillis()
                        val cronetResult = if (xmlBody != null) {
                            cronet.post(url, xmlBody, "application/llsd+xml", timeoutMs = options.timeoutSeconds * 1000L)
                        } else {
                            cronet.get(url, timeoutMs = options.timeoutSeconds * 1000L)
                        }
                        if (cronetResult is com.linkpoint.network.CronetResult.Success && cronetResult.code in 200..299) {
                            Log.d(TAG, "Cap $capName via Cronet/${cronetResult.protocol} (${cronetResult.body.size} bytes)")
                            com.linkpoint.network.NetworkLogger.logCapabilityResponse(
                                capName = capName,
                                success = true,
                                durationMs = System.currentTimeMillis() - cronetStart,
                                protocol = cronetResult.protocol
                            )
                            return@withContext LLSDParser.parseAuto(cronetResult.body, "application/llsd+xml")
                        }
                        if (cronetResult is com.linkpoint.network.CronetResult.Success && cronetResult.code in RETRYABLE_HTTP_CODES) {
                            // Honour Retry-After parsing path by routing
                            // through OkHttp on retryable codes — Cronet's
                            // negotiatedProtocol is enough info for one
                            // attempt; OkHttp's response object exposes
                            // the headers our retry policy reads.
                            Log.d(TAG, "Cap $capName Cronet got ${cronetResult.code}; falling through to OkHttp for retry semantics")
                        } else if (cronetResult is com.linkpoint.network.CronetResult.Failure) {
                            Log.d(TAG, "Cap $capName Cronet failed (${cronetResult.message}); falling through to OkHttp")
                        }
                        // Fall through to OkHttp on any other Cronet outcome
                    }
                }

                val requestBuilder = Request.Builder().url(url)

                if (xmlBody != null) {
                    requestBuilder.post(
                        String(xmlBody, Charsets.UTF_8).toRequestBody("application/llsd+xml".toMediaType())
                    )
                } else {
                    requestBuilder.get()
                }

                val okhttpStart = System.currentTimeMillis()
                val response = client.newCall(requestBuilder.build()).execute()
                val okhttpProto = response.protocol.toString()

                // Check for retryable HTTP errors
                if (response.code in RETRYABLE_HTTP_CODES) {
                    retryAfterSeconds = parseRetryAfterHeader(response)

                    if (attempt < options.retries) {
                        Log.w(TAG, "HTTP ${response.code} for $capName ($okhttpProto), will retry")
                        com.linkpoint.network.NetworkLogger.logCapabilityResponse(
                            capName = capName,
                            success = false,
                            durationMs = System.currentTimeMillis() - okhttpStart,
                            protocol = okhttpProto
                        )
                        response.close()
                        throw RetryableException("HTTP ${response.code}")
                    }
                }

                val contentType = response.header("Content-Type")
                val responseBytes = response.body?.bytes()
                com.linkpoint.network.NetworkLogger.logCapabilityResponse(
                    capName = capName,
                    success = response.isSuccessful,
                    durationMs = System.currentTimeMillis() - okhttpStart,
                    protocol = okhttpProto
                )
                response.close()
                
                if (responseBytes == null || responseBytes.isEmpty()) {
                    if (attempt < options.retries) {
                        throw RetryableException("Empty response body")
                    }
                    return@withContext null
                }

                return@withContext LLSDParser.parseAuto(responseBytes, contentType)
                
            } catch (e: RetryableException) {
                lastException = e
                // Continue to next attempt
            } catch (e: SocketTimeoutException) {
                Log.w(TAG, "Timeout for $capName (attempt ${attempt + 1})")
                lastException = e
                // Timeout is retryable
            } catch (e: EOFException) {
                Log.w(TAG, "EOF for $capName (attempt ${attempt + 1})")
                lastException = e
                // EOF is retryable
            } catch (e: IOException) {
                if (isRetryableIOException(e)) {
                    Log.w(TAG, "Retryable IO error for $capName: ${e.message}")
                    lastException = e
                } else {
                    Log.e(TAG, "Non-retryable IO error for $capName", e)
                    throw e
                }
            } catch (e: Exception) {
                Log.e(TAG, "Capability request failed: $capName", e)
                throw e
            }
        }
        
        Log.e(TAG, "All retries exhausted for $capName", lastException)
        null
    }

    /**
     * GET against [capName] with repeated query parameters appended to the
     * cap URL. Used by capabilities whose wire shape is HTTP GET rather
     * than POST-LLSD — primarily `GetDisplayNames`. Reuses the same retry
     * / parse pipeline as [request].
     *
     * Previously callers (`ProfileManager.getDisplayNames`,
     * `DisplayNameManager.fetchBatch`) POSTed an LLSD body to
     * GetDisplayNames; the simulator returned nothing useful and friend
     * names stayed on the `Resident (xxxx)` placeholder forever — the
     * symptom in the 2026-04-25 Athanasia debug capture.
     */
    override suspend fun requestWithQuery(
        capName: String,
        queryParams: List<Pair<String, String>>
    ): LLSDValue? = withContext(Dispatchers.IO) {
        val baseUrl = getCapability(capName) ?: return@withContext null
        val httpUrl = baseUrl.toHttpUrlOrNull() ?: run {
            Log.w(TAG, "requestWithQuery: invalid URL for $capName")
            return@withContext null
        }
        val urlBuilder = httpUrl.newBuilder()
        queryParams.forEach { (k, v) -> urlBuilder.addQueryParameter(k, v) }
        val url = urlBuilder.build()

        val options = getOptionsForCapability(capName)
        val client = getClientForCapability(capName)

        var lastException: Exception? = null
        var retryAfterSeconds: Int? = null

        repeat(options.retries + 1) { attempt ->
            try {
                if (attempt > 0) {
                    val delayMs = options.calculateRetryDelay(attempt - 1, retryAfterSeconds)
                    Log.d(TAG, "Retrying GET $capName (attempt ${attempt + 1}/${options.retries + 1}) after ${delayMs}ms")
                    delay(delayMs)
                }
                val getStart = System.currentTimeMillis()
                val response = client.newCall(Request.Builder().url(url).get().build()).execute()
                val getProto = response.protocol.toString()
                if (response.code in RETRYABLE_HTTP_CODES) {
                    retryAfterSeconds = parseRetryAfterHeader(response)
                    if (attempt < options.retries) {
                        com.linkpoint.network.NetworkLogger.logCapabilityResponse(
                            capName = capName,
                            success = false,
                            durationMs = System.currentTimeMillis() - getStart,
                            protocol = getProto
                        )
                        response.close()
                        throw RetryableException("HTTP ${response.code}")
                    }
                }
                val contentType = response.header("Content-Type")
                val responseBytes = response.body?.bytes()
                com.linkpoint.network.NetworkLogger.logCapabilityResponse(
                    capName = capName,
                    success = response.isSuccessful,
                    durationMs = System.currentTimeMillis() - getStart,
                    protocol = getProto
                )
                response.close()
                if (responseBytes == null || responseBytes.isEmpty()) {
                    if (attempt < options.retries) throw RetryableException("Empty body")
                    return@withContext null
                }
                return@withContext LLSDParser.parseAuto(responseBytes, contentType)
            } catch (e: RetryableException) {
                lastException = e
            } catch (e: SocketTimeoutException) {
                Log.w(TAG, "Timeout for GET $capName (attempt ${attempt + 1})")
                lastException = e
            } catch (e: EOFException) {
                Log.w(TAG, "EOF for GET $capName (attempt ${attempt + 1})")
                lastException = e
            } catch (e: IOException) {
                if (isRetryableIOException(e)) {
                    Log.w(TAG, "Retryable IO for GET $capName: ${e.message}")
                    lastException = e
                } else {
                    Log.e(TAG, "Non-retryable IO for GET $capName", e)
                    throw e
                }
            } catch (e: Exception) {
                Log.e(TAG, "GET capability request failed: $capName", e)
                throw e
            }
        }
        Log.e(TAG, "All retries exhausted for GET $capName", lastException)
        null
    }

    /**
     * Exception to signal that a request should be retried.
     */
    private class RetryableException(message: String) : Exception(message)
    
    /**
     * Check if an IOException is retryable.
     */
    private fun isRetryableIOException(e: IOException): Boolean {
        val message = e.message ?: return false
        return RETRYABLE_MESSAGE_PATTERNS.any { message.contains(it, ignoreCase = true) }
    }
    
    /**
     * Parse Retry-After header value.
     */
    private fun parseRetryAfterHeader(response: Response): Int? {
        val retryAfter = response.header("Retry-After") ?: return null
        return retryAfter.toIntOrNull()?.let { minOf(it, 30) }
    }
    
    /**
     * Register an event handler
     */
    fun registerEventHandler(eventName: String, handler: EventHandler) {
        registerEventHandler(eventName, handler, Dispatchers.Default)
    }
    
    /**
     * Register an event handler with a specific dispatcher.
     */
    fun registerEventHandler(
        eventName: String,
        handler: EventHandler,
        dispatcher: CoroutineDispatcher
    ) {
        eventHandlers.getOrPut(eventName) { mutableListOf() }
            .add(EventHandlerRegistration(handler, dispatcher))
    }
    
    /**
     * Start the event queue with Firestorm-style retry handling.
     * 
     * The event queue uses long-polling, so timeouts are expected and normal.
     * Uses exponential backoff for actual errors, but immediate retry for
     * expected 502 responses (long-poll timeout).
     */
    private fun startEventQueue(url: String) {
        eventQueueJob?.cancel()
        eventQueueJob = scope.launch {
            var ack: Int? = null
            var done = false
            var consecutiveErrors = 0
            val options = HttpRequestOptions.forEventQueue()
            
            while (isActive && !done) {
                try {
                    val requestBody = LLSDMap().apply {
                        this["ack"] = ack?.let { LLSDInteger(it) } ?: LLSDBoolean(true)
                        this["done"] = LLSDBoolean(false)
                    }
                    
                    val xml = LLSDXmlUtils.wrap(requestBody)
                    
                    val request = Request.Builder()
                        .url(url)
                        .post(xml.toRequestBody("application/llsd+xml".toMediaType()))
                        .build()
                    
                    eventQueueClient.newCall(request).execute().use { response ->
                        val contentType = response.header("Content-Type")
                        val code = response.code

                        // 502 is expected for long-poll timeout - retry immediately
                        if (code == 502) {
                            consecutiveErrors = 0  // Not an error
                            return@use
                        }

                        // Handle other HTTP errors with backoff
                        if (code in RETRYABLE_HTTP_CODES) {
                            consecutiveErrors++
                            val retryAfter = parseRetryAfterHeader(response)
                            val delayMs = options.calculateRetryDelay(consecutiveErrors - 1, retryAfter)
                            Log.w(TAG, "Event queue HTTP $code, retrying in ${delayMs}ms")
                            delay(delayMs)
                            return@use
                        }

                        // Success - reset error count
                        consecutiveErrors = 0

                        response.body?.byteStream()?.use { stream ->
                            val llsd = LLSDStreamingParser.parseAnyToValue(stream, contentType)
                            if (llsd is LLSDMap) {
                                ack = llsd.getInt("id")

                                val events = llsd.getArray("events")
                                events?.value?.forEach { event ->
                                    if (event is LLSDMap) {
                                        processEvent(event)
                                    }
                                }
                            }
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    // Timeout is expected for long-polling, retry immediately
                    Log.v(TAG, "Event queue poll timeout, continuing...")
                    consecutiveErrors = 0
                    continue
                } catch (e: Exception) {
                    if (isActive) {
                        consecutiveErrors++
                        val delayMs = options.calculateRetryDelay(consecutiveErrors - 1, null)
                        Log.w(TAG, "Event queue error (${e.javaClass.simpleName}), " +
                            "retrying in ${delayMs}ms (errors: $consecutiveErrors)", e)
                        
                        // Cap consecutive errors to prevent excessive backoff
                        if (consecutiveErrors >= options.retries) {
                            Log.e(TAG, "Too many consecutive event queue errors, longer backoff")
                            delay(30_000)  // 30 second pause before resetting
                            consecutiveErrors = 0
                        } else {
                            delay(delayMs)
                        }
                    }
                }
            }
        }
    }
    
    private fun processEvent(event: LLSDMap) {
        val message = event.getString("message") ?: return
        val body = event.getMap("body")
        
        Log.d(TAG, "Event: $message")
        
        eventHandlers[message]?.forEach { registration ->
            scope.launch(registration.dispatcher) {
                try {
                    registration.handler.onEvent(message, body ?: LLSDMap())
                } catch (e: Exception) {
                    Log.e(TAG, "Event handler error", e)
                }
            }
        }
    }
    
    /**
     * Stop the capability manager
     */
    fun shutdown() {
        backgroundRetryJob?.cancel()
        eventQueueJob?.cancel()
        scope.cancel()
        capabilities.clear()
        _isReady.value = false
    }
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    /**
     * Get list of all available capability names
     */
    fun getAvailableCapabilities(): List<String> = capabilities.keys.toList()
    
    /**
     * Get the total number of capabilities
     */
    fun getCapabilityCount(): Int = capabilities.size
    
    /**
     * Check if event queue is active
     */
    fun isEventQueueActive(): Boolean = eventQueueJob?.isActive == true
    
    /**
     * Get number of registered event handlers
     */
    fun getEventHandlerCount(): Int = eventHandlers.values.sumOf { it.size }
    
    /**
     * Get list of events with registered handlers
     */
    fun getRegisteredEventTypes(): List<String> = eventHandlers.keys.toList()
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): CapabilityDiagnostics {
        val initDurationMs = if (initializationEndTime > 0 && initializationStartTime > 0) {
            initializationEndTime - initializationStartTime
        } else if (initializationStartTime > 0) {
            System.currentTimeMillis() - initializationStartTime // Still in progress
        } else {
            0L
        }
        
        return CapabilityDiagnostics(
            isReady = _isReady.value,
            seedCapability = seedCapability?.let { 
                if (it.length > DIAGNOSTIC_URL_TRUNCATE_LENGTH) it.take(DIAGNOSTIC_URL_TRUNCATE_LENGTH) + "..." else it 
            },
            capabilityCount = capabilities.size,
            availableCapabilities = capabilities.keys.toList().sorted(),
            eventQueueActive = eventQueueJob?.isActive == true,
            eventHandlerCount = eventHandlers.values.sumOf { it.size },
            registeredEventTypes = eventHandlers.keys.toList().sorted(),
            hasGetTexture = hasCapability(CAP_GET_TEXTURE),
            hasGetMesh = hasCapability(CAP_GET_MESH) || hasCapability(CAP_GET_MESH2),
            hasFetchInventory = hasCapability(CAP_FETCH_INVENTORY),
            hasEventQueue = hasCapability(CAP_EVENT_QUEUE),
            // New initialization tracking fields
            initializationAttempts = lastInitializationAttempts,
            lastInitializationError = lastInitializationError,
            initializationDurationMs = initDurationMs,
            initializationComplete = initializationEndTime > 0
        )
    }
    
    /**
     * Diagnostic data class for capability manager state
     */
    data class CapabilityDiagnostics(
        val isReady: Boolean,
        val seedCapability: String?,
        val capabilityCount: Int,
        val availableCapabilities: List<String>,
        val eventQueueActive: Boolean,
        val eventHandlerCount: Int,
        val registeredEventTypes: List<String>,
        val hasGetTexture: Boolean,
        val hasGetMesh: Boolean,
        val hasFetchInventory: Boolean,
        val hasEventQueue: Boolean,
        // New initialization tracking fields
        val initializationAttempts: Int = 0,
        val lastInitializationError: String? = null,
        val initializationDurationMs: Long = 0,
        val initializationComplete: Boolean = false
    )
}

fun interface EventHandler {
    fun onEvent(message: String, body: LLSDMap)
}
