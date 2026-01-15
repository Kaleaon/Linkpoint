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
    
    /**
     * Initialize capabilities from seed
     */
    suspend fun initialize(seedCap: String): Boolean = withContext(Dispatchers.IO) {
        seedCapability = seedCap
        Log.i(TAG, "Initializing capabilities from seed...")
        
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
        val options = HttpRequestOptions.forLogin()
        var seedCaps: Map<String, String>? = null
        var lastException: Exception? = null
        
        // Retry seed caps request (initial attempt + retries)
        for (attempt in 0..options.retries) {
            try {
                if (attempt > 0) {
                    val delayMs = options.calculateRetryDelay(attempt - 1)
                    Log.w(TAG, "Retrying seed capability request (attempt ${attempt + 1}/${options.retries + 1}) after ${delayMs}ms")
                    delay(delayMs)
                }
                
                seedCaps = requestCapabilities(seedCap, capNames)
                if (!seedCaps.isNullOrEmpty()) {
                    break
                }
                
                lastException = Exception("Seed capability returned no entries for $seedCap")
            } catch (e: Exception) {
                lastException = e
            }
        }
        
        val resolvedCaps = seedCaps
        if (resolvedCaps.isNullOrEmpty()) {
            _isReady.value = false
            Log.e(TAG, "Failed to initialize capabilities", lastException)
            return@withContext false
        }
        
        resolvedCaps.forEach { (name, url) ->
            capabilities[name] = url
            Log.d(TAG, "Capability: $name -> $url")
        }
        
        // Start event queue
        getCapability(CAP_EVENT_QUEUE)?.let { eqUrl ->
            startEventQueue(eqUrl)
        }
        
        _isReady.value = true
        Log.i(TAG, "Capabilities initialized: ${capabilities.size} caps")
        true
    }
    
    /**
     * Request capabilities from seed
     */
    private suspend fun requestCapabilities(
        seedUrl: String,
        capNames: List<String>
    ): Map<String, String>? = withContext(Dispatchers.IO) {
        val requestBody = LLSDMap().apply {
            capNames.forEach { name ->
                this[name] = LLSDString("")
            }
        }
        
        val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd>${requestBody.toXML()}</llsd>"
        
        try {
            val request = Request.Builder()
                .url(seedUrl)
                .post(xml.toRequestBody("application/llsd+xml".toMediaType()))
                .build()
            
            val response = httpClient.newCall(request).execute()
            val body = response.body?.string() ?: return@withContext null
            
            val llsd = LLSDParser.parseXML(body)
            if (llsd is LLSDMap) {
                llsd.value.mapNotNull { (key, value) ->
                    if (value is LLSDString && value.value.isNotEmpty()) {
                        key to value.value
                    } else null
                }.toMap()
            } else null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to request capabilities", e)
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
            hasEventQueue = hasCapability(CAP_EVENT_QUEUE)
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
        val hasEventQueue: Boolean
    )
}

fun interface EventHandler {
    fun onEvent(message: String, body: LLSDMap)
}
