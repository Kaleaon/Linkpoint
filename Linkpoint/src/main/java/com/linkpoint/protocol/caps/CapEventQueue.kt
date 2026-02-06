package com.linkpoint.protocol.caps

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Capability Event Queue
 * 
 * Handles capability-based event queuing for Second Life protocol.
 * Based on the reference viewer's SLCapEventQueue implementation.
 * 
 * This implements the EventQueueGet capability which uses HTTP long-polling
 * to receive events from the simulator.
 * 
 * Features:
 * - Event queue management
 * - Capability-based communication
 * - Mobile-optimized polling
 * - Efficient event processing
 * 
 * Mobile-First Considerations:
 * - Battery-conscious polling intervals
 * - Efficient event batching
 * - Memory-conscious queue management
 * - Network-aware adaptive polling
 * 
 * Note: The main CapabilityManager also has event queue support. This class
 * provides a standalone implementation that can be used by GridConnection.
 */
class CapEventQueue(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    companion object {
        private const val TAG = "CapEventQueue"
        private const val DEFAULT_POLL_INTERVAL_MS = 1000L
        private const val MAX_QUEUE_SIZE = 100
        private const val LONG_POLL_TIMEOUT_SECONDS = 30L
        private const val MAX_CONSECUTIVE_ERRORS = 5
    }
    
    /**
     * Event data structure
     */
    data class Event(
        val eventType: String,
        val eventData: Map<String, Any>,
        val timestamp: Long = System.currentTimeMillis()
    )
    
    /**
     * Event listener interface
     */
    fun interface EventListener {
        fun onEvent(message: String, body: LLSDMap)
    }
    
    /**
     * HTTP client configured for long-polling
     */
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(LONG_POLL_TIMEOUT_SECONDS + 5, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
    
    /**
     * Polling job
     */
    private var pollingJob: Job? = null
    
    /**
     * Event queue
     */
    private val eventQueue: MutableList<Event> = mutableListOf()
    
    /**
     * Event listeners
     */
    private val eventListeners = mutableMapOf<String, MutableList<EventListener>>()
    
    /**
     * Polling interval
     */
    private var pollIntervalMs: Long = DEFAULT_POLL_INTERVAL_MS
    
    /**
     * Active flag
     */
    private var isActive: Boolean = false
    
    /**
     * Current acknowledgement ID
     */
    private var currentAck: Int? = null
    
    /**
     * Register an event listener
     */
    fun registerListener(eventType: String, listener: EventListener) {
        eventListeners.getOrPut(eventType) { mutableListOf() }.add(listener)
    }
    
    /**
     * Start the event queue
     */
    fun start(capabilityUrl: String, pollInterval: Long = DEFAULT_POLL_INTERVAL_MS) {
        if (isActive) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Event queue already active")
            return
        }
        
        this.pollIntervalMs = pollInterval
        isActive = true
        
        pollingJob = scope.launch {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Starting event queue polling: $capabilityUrl")
            
            var consecutiveErrors = 0
            
            while (isActive && isCoroutineActive()) {
                try {
                    pollEvents(capabilityUrl)
                    consecutiveErrors = 0  // Reset on success
                } catch (e: SocketTimeoutException) {
                    // Timeout is expected for long-polling, retry immediately
                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Event queue poll timeout (expected)")
                    consecutiveErrors = 0
                    continue
                } catch (e: Exception) {
                    if (isActive) {
                        consecutiveErrors++
                        NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, 
                            "Error polling events (attempt $consecutiveErrors): ${e.message}")
                        
                        // Exponential backoff
                        if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, 
                                "Too many errors, pausing event queue for 30s")
                            delay(30_000)
                            consecutiveErrors = 0
                        } else {
                            val delayMs = pollIntervalMs * (1L shl minOf(consecutiveErrors, 5))
                            delay(delayMs)
                        }
                    }
                }
            }
        }
    }
    
    private fun isCoroutineActive(): Boolean {
        return scope.coroutineContext[Job]?.isCancelled != true
    }
    
    /**
     * Poll for new events using HTTP long-polling.
     * This implements the EventQueueGet capability protocol.
     */
    private suspend fun pollEvents(capabilityUrl: String) {
        // Build LLSD request body
        val ack = currentAck
        val requestBody = LLSDMap().apply {
            this["ack"] = if (ack != null) LLSDInteger(ack) else LLSDBoolean(true)
            this["done"] = LLSDBoolean(false)
        }
        
        val xml = LLSDXmlUtils.wrap(requestBody)
        
        val request = Request.Builder()
            .url(capabilityUrl)
            .post(xml.toRequestBody("application/llsd+xml".toMediaType()))
            .build()
        
        httpClient.newCall(request).execute().use { response ->
            val code = response.code
            val contentType = response.header("Content-Type")

            // 502 is expected for long-poll timeout - retry immediately
            if (code == 502) {
                return
            }

            // Handle HTTP errors
            if (code !in 200..299) {
                throw Exception("HTTP $code from event queue")
            }

            response.body?.byteStream()?.use { stream ->
                val llsd = LLSDStreamingParser.parseAnyToValue(stream, contentType)
                if (llsd is LLSDMap) {
                    // Update acknowledgement ID
                    currentAck = llsd.getInt("id")

                    // Process events
                    val events = llsd.getArray("events")
                    events?.value?.forEach { event ->
                        if (event is LLSDMap) {
                            processLLSDEvent(event)
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Process an LLSD event from the server
     */
    private fun processLLSDEvent(event: LLSDMap) {
        val message = event.getString("message") ?: return
        val body = event.getMap("body") ?: LLSDMap()
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Event: $message")
        
        // Convert to local Event format for queue
        // Convert LLSDMap to Map<String, Any> for the Event data structure
        val eventData = convertLLSDMapToMap(body)
        val localEvent = Event(
            eventType = message,
            eventData = eventData,
            timestamp = System.currentTimeMillis()
        )
        addEvent(localEvent)
        
        // Notify listeners
        eventListeners[message]?.forEach { listener ->
            try {
                listener.onEvent(message, body)
            } catch (e: Exception) {
                Log.e(TAG, "Event listener error", e)
            }
        }
    }
    
    /**
     * Convert LLSDMap to Map<String, Any> for simpler event data storage
     */
    private fun convertLLSDMapToMap(llsdMap: LLSDMap): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        for ((key, value) in llsdMap.value) {
            when (value) {
                is LLSDString -> result[key] = value.value
                is LLSDInteger -> result[key] = value.value
                is LLSDReal -> result[key] = value.value
                is LLSDBoolean -> result[key] = value.value
                is LLSDUUID -> result[key] = value.value
                is LLSDMap -> result[key] = convertLLSDMapToMap(value)
                is LLSDArray -> result[key] = value.value.map { it.toString() }
                else -> result[key] = value.toString()
            }
        }
        return result
    }
    
    /**
     * Add an event to the queue
     */
    fun addEvent(event: Event) {
        synchronized(eventQueue) {
            if (eventQueue.size >= MAX_QUEUE_SIZE) {
                // Remove oldest event
                eventQueue.removeAt(0)
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Event queue full, removed oldest event")
            }
            
            eventQueue.add(event)
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Added event: ${event.eventType}")
        }
    }
    
    /**
     * Get the next event
     */
    fun getNextEvent(): Event? {
        synchronized(eventQueue) {
            return if (eventQueue.isNotEmpty()) {
                eventQueue.removeAt(0)
            } else {
                null
            }
        }
    }
    
    /**
     * Get all pending events
     */
    fun getAllEvents(): List<Event> {
        synchronized(eventQueue) {
            return eventQueue.toList()
        }
    }
    
    /**
     * Clear all events
     */
    fun clearEvents() {
        synchronized(eventQueue) {
            eventQueue.clear()
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Cleared all events")
        }
    }
    
    /**
     * Get queue statistics
     */
    fun getStatistics(): Map<String, Any> {
        synchronized(eventQueue) {
            return mapOf(
                "queueSize" to eventQueue.size,
                "isActive" to isActive,
                "pollIntervalMs" to pollIntervalMs,
                "maxQueueSize" to MAX_QUEUE_SIZE,
                "currentAck" to (currentAck ?: "none")
            )
        }
    }
    
    /**
     * Stop the event queue
     */
    fun stop() {
        if (!isActive) {
            return
        }
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Stopping event queue")
        
        isActive = false
        pollingJob?.cancel()
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Event queue stopped")
    }
    
    /**
     * Close the event queue
     */
    fun close() {
        stop()
        scope.cancel()
        clearEvents()
        eventListeners.clear()
    }
}
