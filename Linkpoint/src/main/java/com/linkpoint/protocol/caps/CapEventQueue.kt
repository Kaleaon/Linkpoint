package com.linkpoint.protocol.caps

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.llsd.*
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

/**
 * Capability Event Queue
 *
 * Handles capability-based event queuing for Second Life protocol.
 * Based on the reference viewer's SLCapEventQueue implementation.
 *
 * Implements the EventQueueGet capability using HTTP long-polling
 * to receive events from the simulator.
 *
 * Features:
 * - Persistent sequence tracking (`highestAckSequenceId`) across network handoffs
 * - Rapid network callback recovery on network interface changes (<500ms)
 * - Sliding-window event buffering and deduplication with 5 MB memory cap
 * - Exponential backoff (max 30s) on socket/HTTP errors without sequence ID resets
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
        private const val MAX_BACKOFF_DELAY_MS = 30_000L
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
     * Event queue for local storage
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
     * Adaptive background mode flag
     */
    @Volatile
    var isAdaptiveBackgroundMode: Boolean = false
        private set

    /**
     * Set adaptive background mode for Doze / background optimization.
     */
    fun setAdaptiveBackgroundMode(enabled: Boolean, intervalMs: Long = 15_000L) {
        isAdaptiveBackgroundMode = enabled
        if (enabled) {
            pollIntervalMs = intervalMs
        } else {
            pollIntervalMs = DEFAULT_POLL_INTERVAL_MS
        }
        NetworkLogger.log(
            NetworkLogger.Level.DEBUG,
            NetworkLogger.Category.UDP,
            "Adaptive background polling mode=${if (enabled) "ENABLED (${pollIntervalMs}ms)" else "DISABLED"}"
        )
    }

    /**
     * Persistent record of the highest acknowledged sequence ID.
     * MUST NOT be reset during socket errors (ECONNRESET, ETIMEDOUT).
     */
    @Volatile
    private var highestAckSequenceId: Int = 0

    /**
     * Sliding-window queue for out-of-order event buffering & deduplication
     */
    val slidingWindow = SlidingWindowEventQueue()

    /**
     * Current capability URL
     */
    @Volatile
    private var currentCapabilityUrl: String? = null

    /**
     * Network recovery trigger signal
     */
    private var pollTriggerJob: Job? = null

    /**
     * Register an event listener
     */
    fun registerListener(eventType: String, listener: EventListener) {
        eventListeners.getOrPut(eventType) { mutableListOf() }.add(listener)
    }

    /**
     * Gets highest acknowledged sequence ID.
     */
    fun getHighestAckSequenceId(): Int = highestAckSequenceId

    /**
     * Directly sets/overrides sequence ID if needed.
     */
    fun setHighestAckSequenceId(seqId: Int) {
        if (seqId > highestAckSequenceId) {
            highestAckSequenceId = seqId
            slidingWindow.setBaselineSequenceId(seqId)
        }
    }

    /**
     * Start the event queue
     */
    fun start(capabilityUrl: String, pollInterval: Long = DEFAULT_POLL_INTERVAL_MS) {
        if (isActive) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Event queue already active")
            return
        }

        this.currentCapabilityUrl = capabilityUrl
        this.pollIntervalMs = pollInterval
        isActive = true

        pollingJob = scope.launch {
            NetworkLogger.log(
                NetworkLogger.Level.DEBUG,
                NetworkLogger.Category.UDP,
                "Starting event queue polling: $capabilityUrl (ack=$highestAckSequenceId)"
            )

            var consecutiveErrors = 0

            while (isActive && isCoroutineActive()) {
                try {
                    pollEvents(capabilityUrl)
                    consecutiveErrors = 0  // Reset on success
                    if (isAdaptiveBackgroundMode && pollIntervalMs > 0) {
                        delay(pollIntervalMs)
                    }
                } catch (e: SocketTimeoutException) {
                    // Timeout is expected for long-polling, retry immediately
                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Event queue poll timeout (expected)")
                    consecutiveErrors = 0
                    continue
                } catch (e: Exception) {
                    if (isActive) {
                        consecutiveErrors++
                        NetworkLogger.log(
                            NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                            "Error polling events (attempt $consecutiveErrors, seq=$highestAckSequenceId): ${e.message}"
                        )

                        // Exponential backoff capped at 30 seconds
                        val backoff = pollIntervalMs * (1L shl minOf(consecutiveErrors, 5))
                        val delayMs = minOf(backoff, MAX_BACKOFF_DELAY_MS)

                        NetworkLogger.log(
                            NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                            "Backing off event queue poll for ${delayMs}ms without resetting sequence counter ($highestAckSequenceId)"
                        )

                        delay(delayMs)
                        if (consecutiveErrors >= MAX_CONSECUTIVE_ERRORS) {
                            consecutiveErrors = MAX_CONSECUTIVE_ERRORS - 1
                        }
                    }
                }
            }
        }
    }

    /**
     * Force immediate reconnection / restart of EventQueue polling (e.g. after network interface switch).
     */
    fun forceReconnect(newCapabilityUrl: String? = null) {
        val targetUrl = newCapabilityUrl ?: currentCapabilityUrl
        if (targetUrl.isNullOrEmpty()) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Cannot forceReconnect: No capability URL available")
            return
        }
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "Force reconnecting EventQueue to $targetUrl")
        pollingJob?.cancel()
        pollTriggerJob?.cancel()
        isActive = false
        start(targetUrl, pollIntervalMs)
    }

    /**
     * Triggered by Android network callbacks when network interface changes or IP mutates.
     * Forces immediate reconnection request with highest acknowledged sequence ID.
     */
    fun onNetworkInterfaceChanged() {
        if (!isActive) return
        val capUrl = currentCapabilityUrl ?: return

        NetworkLogger.log(
            NetworkLogger.Level.INFO,
            NetworkLogger.Category.CONNECTIVITY,
            "⚡ Network interface changed: triggering immediate EventQueue reconnect (seq=$highestAckSequenceId)"
        )

        // Cancel existing delay / waiting poll job and launch immediate poll
        pollTriggerJob?.cancel()
        pollTriggerJob = scope.launch {
            try {
                pollEvents(capUrl)
            } catch (e: Exception) {
                NetworkLogger.log(
                    NetworkLogger.Level.WARN,
                    NetworkLogger.Category.UDP,
                    "Immediate reconnect poll failed: ${e.message}"
                )
            }
        }
    }

    private fun isCoroutineActive(): Boolean {
        return scope.coroutineContext[Job]?.isCancelled != true
    }

    /**
     * Poll for new events using HTTP long-polling.
     * Implements the EventQueueGet capability protocol.
     */
    private suspend fun pollEvents(capabilityUrl: String) {
        // Build LLSD request payload including last acknowledged sequence ID
        val ackSeq = highestAckSequenceId
        val requestBody = LLSDMap().apply {
            this["ack"] = if (ackSeq > 0) LLSDInteger(ackSeq) else LLSDBoolean(true)
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

            // Handle HTTP errors (4xx / 5xx)
            if (code !in 200..299) {
                throw IOException("HTTP $code from event queue")
            }

            response.body?.byteStream()?.use { stream ->
                val llsd = LLSDStreamingParser.parseAnyToValue(stream, contentType)
                if (llsd is LLSDMap) {
                    // Update persistent highest acknowledged sequence ID
                    val responseId = llsd.getInt("id") ?: 0
                    if (responseId > highestAckSequenceId) {
                        highestAckSequenceId = responseId
                        NetworkLogger.log(
                            NetworkLogger.Level.DEBUG,
                            NetworkLogger.Category.UDP,
                            "CapEventQueue: updated highestAckSequenceId = $highestAckSequenceId"
                        )
                    }

                    // Process events
                    val eventsArray = llsd.getArray("events")
                    val rawEvents = mutableListOf<Event>()
                    eventsArray?.value?.forEach { event ->
                        if (event is LLSDMap) {
                            val msg = event.getString("message")
                            val body = event.getMap("body") ?: LLSDMap()
                            if (msg != null) {
                                val localEvent = Event(
                                    eventType = msg,
                                    eventData = convertLLSDMapToMap(body),
                                    timestamp = System.currentTimeMillis()
                                )
                                rawEvents.add(localEvent)
                                addEvent(localEvent)
                            }
                        }
                    }

                    // Buffer through sliding window queue to guarantee chronological ordering
                    val readyEvents = slidingWindow.offerBatch(responseId, rawEvents)
                    for (readyEvent in readyEvents) {
                        dispatchLocalEvent(readyEvent, llsd)
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

        val eventData = convertLLSDMapToMap(body)
        val localEvent = Event(
            eventType = message,
            eventData = eventData,
            timestamp = System.currentTimeMillis()
        )
        addEvent(localEvent)

        dispatchLocalEvent(localEvent, body)
    }

    private fun dispatchLocalEvent(localEvent: Event, body: LLSDMap) {
        eventListeners[localEvent.eventType]?.forEach { listener ->
            try {
                listener.onEvent(localEvent.eventType, body)
            } catch (e: Exception) {
                Log.e(TAG, "Event listener error for ${localEvent.eventType}", e)
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
            slidingWindow.clear()
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
                "highestAckSequenceId" to highestAckSequenceId,
                "slidingWindowPending" to slidingWindow.getPendingCount(),
                "slidingWindowMemoryBytes" to slidingWindow.getMemoryUsageBytes()
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
        pollTriggerJob?.cancel()

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
