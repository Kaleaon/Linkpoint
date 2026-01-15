package com.linkpoint.protocol.capabilities

import android.util.Log
import com.linkpoint.network.core.HttpRequestOptions
import com.linkpoint.network.core.PolicyClass
import com.linkpoint.network.core.RequestThrottler
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.EOFException
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Manages Second Life Capabilities (Caps)
 * Caps are HTTP endpoints that provide various services.
 * 
 * Now enhanced with:
 * - Firestorm-style retry logic with exponential backoff
 * - Request throttling for inventory operations
 * - Retry-After header support
 * - Per-capability request options
 */
class CapabilityManager {
    
    companion object {
        private const val TAG = "CapabilityManager"
        
        // Diagnostic truncation length for URLs in debug reports
        private const val DIAGNOSTIC_URL_TRUNCATE_LENGTH = 50
        private const val SEED_CAP_HEADER_PREVIEW_BYTES = 64
        private const val SEED_CAP_ASCII_CHECK_BYTES = 16
        private const val SEED_CAP_RESPONSE_PREVIEW_CHARS = 200
        private const val UTF8_BOM = '\uFEFF'
        
        // Common capability names
        const val CAP_SEED = "Seed"
        const val CAP_EVENT_QUEUE = "EventQueueGet"
        const val CAP_FETCH_INVENTORY = "FetchInventory2"
        const val CAP_FETCH_LIB_INVENTORY = "FetchLib2"
        const val CAP_FETCH_INVENTORY_DESCENDENTS = "FetchInventoryDescendents2"
        const val CAP_GET_TEXTURE = "GetTexture"
        const val CAP_GET_MESH = "GetMesh"
        const val CAP_GET_MESH2 = "GetMesh2"
        const val CAP_VIEW_STATS = "ViewerStats"
        const val CAP_AGENT_STATE = "AgentState"
        const val CAP_UPDATE_AGENT_INFO = "UpdateAgentInformation"
        const val CAP_UPLOAD_BAKED_TEXTURE = "UploadBakedTexture"
        const val CAP_OBJECT_MEDIA = "ObjectMedia"
        const val CAP_OBJECT_MEDIA_NAVIGATE = "ObjectMediaNavigate"
        const val CAP_PARCEL_VOICE = "ParcelVoiceInfoRequest"
        const val CAP_PROVISION_VOICE = "ProvisionVoiceAccountRequest"
        const val CAP_CHAT_PASS = "ChatSessionRequest"
        const val CAP_COPY_INVENTORY_FROM_NOTECARD = "CopyInventoryFromNotecard"
        const val CAP_ENVIRONMENT = "EnvironmentSettings"
        const val CAP_EXT_ENVIRONMENT = "ExtEnvironment"
        const val CAP_REGION_EXPERIENCE = "RegionExperiences"
        const val CAP_SIMULATE_LURE = "SimulatorLure"
        const val CAP_AVATAR_PICKER = "AvatarPickerSearch"
        const val CAP_SEARCH_STATIC = "SearchStatRequest"
        
        // Voice moderation capability (Project Voice Moderation)
        const val CAP_VOICE_MODERATION = "VoiceModeration"
        
        // Capabilities that are inventory-related (for throttling)
        private val INVENTORY_CAPS = setOf(
            CAP_FETCH_INVENTORY,
            CAP_FETCH_LIB_INVENTORY,
            CAP_FETCH_INVENTORY_DESCENDENTS
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
    }
    
    // Request throttler for rate limiting
    private val throttler = RequestThrottler.getInstance()
    
    // Default HTTP client for general requests
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
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
    
    // Event handlers
    private val eventHandlers = ConcurrentHashMap<String, MutableList<EventHandler>>()
    
    // Initialization tracking for diagnostics (volatile for thread safety)
    @Volatile private var initializationStartTime: Long = 0
    @Volatile private var initializationEndTime: Long = 0
    @Volatile private var lastInitializationError: String? = null
    @Volatile private var lastInitializationAttempts: Int = 0
    @Volatile private var lastSeedCapabilityUsed: String? = null
    
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
        Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
        
        val capNames = listOf(
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
            CAP_CHAT_PASS,
            CAP_ENVIRONMENT,
            CAP_EXT_ENVIRONMENT,
            CAP_AVATAR_PICKER,
            CAP_SEARCH_STATIC
        )
        
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
        
        resolvedCaps.forEach { (name, url) ->
            capabilities[name] = url
            Log.d(TAG, "Capability: $name -> ${url.take(60)}...")
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
        
        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd>${requestBody.toXML()}</llsd>"
        
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
            
            Log.d(TAG, "Seed capability response: HTTP ${response.code} in ${elapsed}ms")
            
            if (!response.isSuccessful) {
                Log.e(TAG, "Seed capability request failed with HTTP ${response.code}: ${response.message}")
                lastInitializationError = "HTTP ${response.code}: ${response.message}"
                response.close()
                return@withContext null
            }
            
            // Use try-finally to ensure response is always closed
            val contentType = response.header("Content-Type") ?: ""
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
            val isXmlContentType = contentType.contains("xml", ignoreCase = true)
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
                if (looksLikeXml && bodyText != null) {
                    LLSDParser.parseXML(bodyText)
                } else {
                    LLSDParser.parse(bodyBytes)
                }
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
    fun getCapability(name: String): String? = capabilities[name]
    
    /**
     * Check if a capability is available
     */
    fun hasCapability(name: String): Boolean = capabilities.containsKey(name)
    
    /**
     * Make a capability request with Firestorm-style retry logic.
     * 
     * Features:
     * - Automatic retries with exponential backoff
     * - Retry-After header support
     * - Request throttling for inventory caps
     * - Appropriate timeouts per capability type
     */
    suspend fun request(
        capName: String,
        body: LLSDValue? = null
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
                
                val requestBuilder = Request.Builder().url(url)
                
                if (body != null) {
                    val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd>${body.toXML()}</llsd>"
                    requestBuilder.post(xml.toRequestBody("application/llsd+xml".toMediaType()))
                } else {
                    requestBuilder.get()
                }
                
                val response = client.newCall(requestBuilder.build()).execute()
                
                // Check for retryable HTTP errors
                if (response.code in RETRYABLE_HTTP_CODES) {
                    retryAfterSeconds = parseRetryAfterHeader(response)
                    
                    if (attempt < options.retries) {
                        Log.w(TAG, "HTTP ${response.code} for $capName, will retry")
                        response.close()
                        throw RetryableException("HTTP ${response.code}")
                    }
                }
                
                val responseBody = response.body?.string()
                response.close()
                
                if (responseBody == null) {
                    if (attempt < options.retries) {
                        throw RetryableException("Empty response body")
                    }
                    return@withContext null
                }
                
                return@withContext LLSDParser.parseXML(responseBody)
                
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
        eventHandlers.getOrPut(eventName) { mutableListOf() }.add(handler)
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
                        this["ack"] = if (ack != null) LLSDInteger(ack!!) else LLSDBoolean(true)
                        this["done"] = LLSDBoolean(false)
                    }
                    
                    val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd>${requestBody.toXML()}</llsd>"
                    
                    val request = Request.Builder()
                        .url(url)
                        .post(xml.toRequestBody("application/llsd+xml".toMediaType()))
                        .build()
                    
                    val response = eventQueueClient.newCall(request).execute()
                    val body = response.body?.string()
                    val code = response.code
                    response.close()
                    
                    // 502 is expected for long-poll timeout - retry immediately
                    if (code == 502) {
                        consecutiveErrors = 0  // Not an error
                        continue
                    }
                    
                    // Handle other HTTP errors with backoff
                    if (code in RETRYABLE_HTTP_CODES) {
                        consecutiveErrors++
                        val retryAfter = parseRetryAfterHeader(response)
                        val delayMs = options.calculateRetryDelay(consecutiveErrors - 1, retryAfter)
                        Log.w(TAG, "Event queue HTTP $code, retrying in ${delayMs}ms")
                        delay(delayMs)
                        continue
                    }
                    
                    // Success - reset error count
                    consecutiveErrors = 0
                    
                    if (body != null) {
                        val llsd = LLSDParser.parseXML(body)
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
        
        eventHandlers[message]?.forEach { handler ->
            try {
                handler.onEvent(message, body ?: LLSDMap())
            } catch (e: Exception) {
                Log.e(TAG, "Event handler error", e)
            }
        }
    }
    
    /**
     * Stop the capability manager
     */
    fun shutdown() {
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
