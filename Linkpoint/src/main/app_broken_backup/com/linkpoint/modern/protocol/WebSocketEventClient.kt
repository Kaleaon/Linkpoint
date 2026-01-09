package com.linkpoint.modern.protocol

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern
import kotlin.math.min
import kotlin.math.pow

/**
 * WebSocket-based event client for real-time Second Life events
 * Implements modern event streaming with automatic reconnection
 */
class WebSocketEventClient : WebSocketListener() {
    
    private val TAG = "WebSocketEventClient"
    
    private val client: OkHttpClient
    private val eventListeners = ConcurrentHashMap<String, CopyOnWriteArrayList<EventListener>>()
    private var webSocket: WebSocket? = null
    
    @Volatile private var connected = false
    private var authToken: String? = null
    
    // Reconnection management
    private val reconnectAttempts = AtomicInteger(0)
    private val MAX_RECONNECT_ATTEMPTS = 5
    private var lastConnectionUrl: String? = null
    private val reconnectHandler = Handler(Looper.getMainLooper())
    
    init {
        client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    /**
     * Set authentication token
     */
    fun setAuthToken(token: String) {
        authToken = token
    }
    
    /**
     * Connect to Second Life event queue via WebSocket
     */
    fun connect(eventQueueUrl: String) {
        lastConnectionUrl = eventQueueUrl
        reconnectAttempts.set(0) // Reset reconnect attempts on manual connect
        
        webSocket?.close(1000, "Reconnecting")
        
        val requestBuilder = Request.Builder().url(eventQueueUrl)
        
        authToken?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
        }
        
        val request = requestBuilder.build()
        webSocket = client.newWebSocket(request, this)
        
        Log.i(TAG, "Connecting to event queue: $eventQueueUrl")
    }
    
    /**
     * Disconnect from event queue
     */
    fun disconnect() {
        webSocket?.close(1000, "Normal closure")
        webSocket = null
        connected = false
    }
    
    /**
     * Subscribe to a specific event type
     */
    fun subscribe(eventType: String, listener: EventListener) {
        eventListeners.computeIfAbsent(eventType) { CopyOnWriteArrayList() }.add(listener)
        
        // Send subscription message if connected
        if (connected && webSocket != null) {
            val subscriptionMessage = """{"action":"subscribe","eventType":"$eventType"}"""
            webSocket?.send(subscriptionMessage)
            Log.d(TAG, "Subscribed to event type: $eventType")
        }
    }
    
    /**
     * Unsubscribe from an event type
     */
    fun unsubscribe(eventType: String, listener: EventListener) {
        eventListeners[eventType]?.let { listeners ->
            listeners.remove(listener)
            if (listeners.isEmpty()) {
                eventListeners.remove(eventType)
                
                // Send unsubscription message if connected
                if (connected && webSocket != null) {
                    val unsubscribeMessage = """{"action":"unsubscribe","eventType":"$eventType"}"""
                    webSocket?.send(unsubscribeMessage)
                    Log.d(TAG, "Unsubscribed from event type: $eventType")
                }
            }
        }
    }
    
    /**
     * Send a formatted message through the WebSocket connection
     */
    fun sendMessage(messageType: String, payload: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync {
            if (connected && webSocket != null) {
                try {
                    val message = """{"type":"$messageType","payload":$payload}"""
                    val success = webSocket?.send(message) ?: false
                    Log.d(TAG, "Sent message: $messageType (success: $success)")
                    success
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to send message: $messageType", e)
                    false
                }
            } else {
                Log.w(TAG, "Cannot send message - WebSocket not connected")
                false
            }
        }
    }
    
    /**
     * Send raw message through WebSocket connection
     */
    fun sendRawMessage(message: String): Boolean {
        if (webSocket == null || !connected) {
            Log.w(TAG, "Cannot send message: WebSocket not connected")
            return false
        }
        
        return try {
            webSocket?.send(message) ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send message", e)
            false
        }
    }
    
    /**
     * Send binary message through WebSocket connection
     */
    fun sendBinaryMessage(data: ByteArray): Boolean {
        if (webSocket == null || !connected) {
            Log.w(TAG, "Cannot send binary message: WebSocket not connected")
            return false
        }
        
        return try {
            webSocket?.send(ByteString.of(*data)) ?: false
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send binary message", e)
            false
        }
    }
    
    // WebSocketListener implementation
    
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.i(TAG, "WebSocket connected: ${response.message}")
        connected = true
        
        // Reset reconnection attempts on successful connection
        reconnectAttempts.set(0)
        
        // Re-subscribe to all event types
        eventListeners.keys.forEach { eventType ->
            val subscriptionMessage = """{"action":"subscribe","eventType":"$eventType"}"""
            webSocket.send(subscriptionMessage)
        }
    }
    
    override fun onMessage(webSocket: WebSocket, text: String) {
        val preview = text.substring(0, min(100, text.length))
        Log.d(TAG, "Received text message: $preview")
        
        try {
            val event = EventMessage.parseFromJson(text)
            dispatchEvent(event)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse text event message", e)
        }
    }
    
    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d(TAG, "Received binary message: ${bytes.size()} bytes")
        
        try {
            val event = EventMessage.parseFromBytes(bytes.toByteArray())
            dispatchEvent(event)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse binary event message", e)
        }
    }
    
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG, "WebSocket closing: $reason")
        connected = false
    }
    
    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        Log.i(TAG, "WebSocket closed: $reason")
        connected = false
    }
    
    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e(TAG, "WebSocket failure", t)
        connected = false
        
        // Attempt reconnection with delay
        scheduleReconnect()
    }
    
    /**
     * Dispatch event to registered listeners
     */
    private fun dispatchEvent(event: EventMessage) {
        eventListeners[event.type]?.forEach { listener ->
            try {
                listener.onEvent(event)
            } catch (e: Exception) {
                Log.e(TAG, "Error in event listener", e)
            }
        }
    }
    
    /**
     * Schedule reconnection with exponential backoff
     */
    private fun scheduleReconnect() {
        val lastUrl = lastConnectionUrl
        if (lastUrl == null) {
            Log.w(TAG, "No last connection URL available for reconnection")
            return
        }
        
        val currentAttempt = reconnectAttempts.incrementAndGet()
        if (currentAttempt > MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Maximum reconnection attempts reached ($MAX_RECONNECT_ATTEMPTS)")
            return
        }
        
        // Exponential backoff: 1s, 2s, 4s, 8s, 16s
        val delaySeconds = 2.0.pow(currentAttempt - 1).toLong()
        
        Log.i(TAG, "Scheduling reconnection attempt $currentAttempt in $delaySeconds seconds")
        
        reconnectHandler.postDelayed({
            Log.i(TAG, "Attempting reconnection $currentAttempt/$MAX_RECONNECT_ATTEMPTS")
            connect(lastUrl)
        }, delaySeconds * 1000)
    }
    
    /**
     * Check if connected
     */
    fun isConnected(): Boolean = connected
    
    /**
     * Shutdown the client
     */
    fun shutdown() {
        disconnect()
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
    }
    
    /**
     * Event listener interface
     */
    fun interface EventListener {
        fun onEvent(event: EventMessage)
    }
    
    /**
     * Event message wrapper
     */
    data class EventMessage(
        val type: String,
        val data: String,
        val timestamp: Long
    ) {
        companion object {
            /**
             * Parse event message from JSON text
             */
            fun parseFromJson(json: String): EventMessage {
                try {
                    var type = "unknown"
                    var data = json
                    var timestamp = System.currentTimeMillis()
                    
                    // Extract message type from common SL event patterns
                    if (json.contains("\"message\"")) {
                        val messagePattern = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"")
                        val matcher = messagePattern.matcher(json)
                        if (matcher.find()) {
                            val messageContent = matcher.group(1)
                            if (messageContent.contains(":")) {
                                val parts = messageContent.split(":", limit = 2)
                                type = parts[0].trim()
                                data = parts[1].trim()
                            } else {
                                data = messageContent
                            }
                        }
                    }
                    
                    // Extract timestamp if present
                    val timestampPattern = Pattern.compile("\"timestamp\"\\s*:\\s*(\\d+)")
                    val timestampMatcher = timestampPattern.matcher(json)
                    if (timestampMatcher.find()) {
                        timestamp = timestampMatcher.group(1).toLong()
                    }
                    
                    // Identify common Second Life event types
                    val lowerJson = json.lowercase()
                    type = when {
                        lowerJson.contains("chat") || lowerJson.contains("im") -> "chat"
                        lowerJson.contains("objectupdate") -> "objectUpdate"
                        lowerJson.contains("agent") -> "agentUpdate"
                        else -> type
                    }
                    
                    return EventMessage(type, data, timestamp)
                    
                } catch (e: Exception) {
                    Log.w("EventMessage", "Failed to parse JSON event message: $json", e)
                    return EventMessage("parse_error", json, System.currentTimeMillis())
                }
            }
            
            /**
             * Parse event message from binary data
             */
            fun parseFromBytes(bytes: ByteArray): EventMessage {
                try {
                    if (bytes.isEmpty()) {
                        return EventMessage("empty", "", System.currentTimeMillis())
                    }
                    
                    var type = "binary"
                    
                    // Check for common Second Life binary message patterns
                    if (bytes.size > 4) {
                        // First 4 bytes often contain message type information
                        val messageType = ((bytes[0].toInt() and 0xFF) shl 24) or
                                        ((bytes[1].toInt() and 0xFF) shl 16) or
                                        ((bytes[2].toInt() and 0xFF) shl 8) or
                                        (bytes[3].toInt() and 0xFF)
                        
                        // Map common Second Life message types
                        type = when (messageType and 0xFF) {
                            0x01 -> "objectUpdate"
                            0x02 -> "agentMovement"
                            0x03 -> "chatMessage"
                            else -> "binary_${Integer.toHexString(messageType and 0xFF)}"
                        }
                    }
                    
                    // Convert bytes to hex string for debugging (first 32 bytes)
                    val hexString = bytes.take(min(bytes.size, 32))
                        .joinToString(" ") { "%02X".format(it) }
                    
                    val data = "Binary data (${bytes.size} bytes): $hexString"
                    
                    return EventMessage(type, data, System.currentTimeMillis())
                    
                } catch (e: Exception) {
                    Log.w("EventMessage", "Failed to parse binary event message", e)
                    return EventMessage(
                        "binary_error",
                        "Failed to parse ${bytes.size} bytes",
                        System.currentTimeMillis()
                    )
                }
            }
        }
    }
}
