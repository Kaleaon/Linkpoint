package com.linkpoint.protocol.capabilities

import android.util.Log
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit

/**
 * Manages Second Life Capabilities (Caps)
 * Caps are HTTP endpoints that provide various services
 */
class CapabilityManager {
    
    companion object {
        private const val TAG = "CapabilityManager"
        
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
    }
    
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()
    
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
        
        try {
            val caps = requestCapabilities(seedCap, listOf(
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
            ))
            
            caps?.forEach { (name, url) ->
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
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize capabilities", e)
            false
        }
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
     * Make a capability request
     */
    suspend fun request(
        capName: String,
        body: LLSDValue? = null
    ): LLSDValue? = withContext(Dispatchers.IO) {
        val url = getCapability(capName) ?: return@withContext null
        
        try {
            val requestBuilder = Request.Builder().url(url)
            
            if (body != null) {
                val xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><llsd>${body.toXML()}</llsd>"
                requestBuilder.post(xml.toRequestBody("application/llsd+xml".toMediaType()))
            } else {
                requestBuilder.get()
            }
            
            val response = httpClient.newCall(requestBuilder.build()).execute()
            val responseBody = response.body?.string() ?: return@withContext null
            
            LLSDParser.parseXML(responseBody)
        } catch (e: Exception) {
            Log.e(TAG, "Capability request failed: $capName", e)
            null
        }
    }
    
    /**
     * Register an event handler
     */
    fun registerEventHandler(eventName: String, handler: EventHandler) {
        eventHandlers.getOrPut(eventName) { mutableListOf() }.add(handler)
    }
    
    /**
     * Start the event queue
     */
    private fun startEventQueue(url: String) {
        eventQueueJob?.cancel()
        eventQueueJob = scope.launch {
            var ack: Int? = null
            var done = false
            
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
                    
                    val response = httpClient.newCall(request).execute()
                    val body = response.body?.string()
                    
                    if (response.code == 502) {
                        // Normal timeout, retry
                        continue
                    }
                    
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
                } catch (e: Exception) {
                    if (isActive) {
                        Log.w(TAG, "Event queue error, retrying...", e)
                        delay(1000)
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
}

fun interface EventHandler {
    fun onEvent(message: String, body: LLSDMap)
}
