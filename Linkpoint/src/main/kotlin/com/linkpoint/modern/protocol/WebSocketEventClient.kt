package com.linkpoint.modern.protocol

import android.util.Log
import android.os.Handler
import android.os.Looper
import okhttp3.*
import okio.ByteString

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * WebSocket-based event client for real-time Second Life events
 * Implements modern event streaming as described in the documentation
 */
class WebSocketEventClient : WebSocketListener() {
    private const val TAG: String = "WebSocketEventClient"
    
    private val OkHttpClient client
    private val ConcurrentHashMap<String, CopyOnWriteArrayList<EventListener>> eventListeners = ConcurrentHashMap<>()
    private WebSocket webSocket
    private volatile Boolean connected = false
    private String authToken
    
    // Reconnection management
    private val AtomicInteger reconnectAttempts = AtomicInteger(0)
    private const val MAX_RECONNECT_ATTEMPTS: Int = 5
    private String lastConnectionUrl
    private val Handler reconnectHandler = Handler(Looper.getMainLooper())
    
    public WebSocketEventClient() {
        this.client = OkHttpClient.Builder()
            .pingInterval(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }
    
    fun setAuthToken(token: String) {
        this.authToken = token
    }
    
    /**
     * Connect to Second Life event queue via WebSocket
     */
    fun connect(eventQueueUrl: String) {
        this.lastConnectionUrl = eventQueueUrl
        this.reconnectAttempts.set(0); // Reset reconnect attempts on manual connect
        
        if (webSocket != null) {
            webSocket.close(1000, "Reconnecting")
        }
        
        Request.Builder requestBuilder = Request.Builder()
            .url(eventQueueUrl)
            
        if (authToken != null) {
            requestBuilder.addHeader("Authorization", "Bearer " + authToken)
        }
        
        val request: Request = requestBuilder.build()
        webSocket = client.newWebSocket(request, this)
        
        Log.i(TAG, "Connecting to event queue: " + eventQueueUrl)
    }
    
    /**
     * Disconnect from event queue
     */
    fun disconnect() {
        if (webSocket != null) {
            webSocket.close(1000, "Normal closure")
            webSocket = null
        }
        connected = false
    }
    
    /**
     * Subscribe to a specific event type
     */
    fun subscribe(eventType: String, listener: EventListener) {
        eventListeners.computeIfAbsent(eventType, k -> CopyOnWriteArrayList<>()).add(listener)
        
        // Send subscription message if connected
        if (connected && webSocket != null) {
            val subscriptionMessage: String = String.format(
                "{\"action\":\"subscribe\",\"eventType\":\"%s\"}", 
                eventType
            )
            webSocket.send(subscriptionMessage)
            Log.d(TAG, "Subscribed to event type: " + eventType)
        }
    }
    
    /**
     * Unsubscribe from an event type
     */
    fun unsubscribe(eventType: String, listener: EventListener) {
        val listeners: CopyOnWriteArrayList<EventListener> = eventListeners.get(eventType)
        if (listeners != null) {
            listeners.remove(listener)
            if (listeners.isEmpty()) {
                eventListeners.remove(eventType)
                
                // Send unsubscription message if connected
                if (connected && webSocket != null) {
                    val unsubscribeMessage: String = String.format(
                        "{\"action\":\"unsubscribe\",\"eventType\":\"%s\"}", 
                        eventType
                    )
                    webSocket.send(unsubscribeMessage)
                    Log.d(TAG, "Unsubscribed from event type: " + eventType)
                }
            }
        }
    }
    
    /**
     * Send a formatted message through the WebSocket connection (from main branch)
     */
    public java.util.concurrent.CompletableFuture<Boolean> sendMessage(String messageType, String payload) {
        return java.util.concurrent.CompletableFuture.supplyAsync(() -> {
            if (connected && webSocket != null) {
                try {
                    val message: String = String.format(
                        "{\"type\":\"%s\",\"payload\":%s}", 
                        messageType, 
                        payload
                    )
                    val success: Boolean = webSocket.send(message)
                    Log.d(TAG, "Sent message: " + messageType + " (success: " + success + ")")
                    return success
                } catch (Exception e) {
                    Log.e(TAG, "Failed to send message: " + messageType, e)
                    return false
                }
            } else {
                Log.w(TAG, "Cannot send message - WebSocket not connected")
                return false
            }
    }
    
    /**
     * Send raw message through WebSocket connection (enhanced version)
     */
     public fun sendRawMessage(message: String): Boolean {
        if (webSocket == null || !connected) {
            Log.w(TAG, "Cannot send message: WebSocket not connected")
            return false
        }
        
        try {
            return webSocket.send(message)
        } catch (Exception e) {
            Log.e(TAG, "Failed to send message", e)
            return false
        }
    }
    
    /**
     * Send binary message through WebSocket connection (enhanced version)
     */
     public fun sendBinaryMessage(data: ByteArray): Boolean {
        if (webSocket == null || !connected) {
            Log.w(TAG, "Cannot send binary message: WebSocket not connected")
            return false
        }
        
        try {
            return webSocket.send(ByteString.of(data))
        } catch (Exception e) {
            Log.e(TAG, "Failed to send binary message", e)
            return false
        }
    }
    
    // WebSocketListener implementation
    
    override Unit onOpen(WebSocket webSocket, Response response) {
        Log.i(TAG, "WebSocket connected: " + response.message())
        connected = true
        
        // Reset reconnection attempts on successful connection
        reconnectAttempts.set(0)
        
        // Re-subscribe to all event types
        for (String eventType : eventListeners.keySet()) {
            val subscriptionMessage: String = String.format(
                "{\"action\":\"subscribe\",\"eventType\":\"%s\"}", 
                eventType
            )
            webSocket.send(subscriptionMessage)
        }
    }
    
    override Unit onMessage(WebSocket webSocket, String text) {
        Log.d(TAG, "Received text message: " + text.substring(0, Math.min(100, text.length())))
        
        try {
            val event: EventMessage = EventMessage.parseFromJson(text)
            dispatchEvent(event)
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse text event message", e)
        }
    }
    
    override Unit onMessage(WebSocket webSocket, ByteString bytes) {
        Log.d(TAG, "Received binary message: " + bytes.size() + " bytes")
        
        try {
            val event: EventMessage = EventMessage.parseFromBytes(bytes.toByteArray())
            dispatchEvent(event)
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse binary event message", e)
        }
    }
    
    override Unit onClosing(WebSocket webSocket, Int code, String reason) {
        Log.i(TAG, "WebSocket closing: " + reason)
        connected = false
    }
    
    override Unit onClosed(WebSocket webSocket, Int code, String reason) {
        Log.i(TAG, "WebSocket closed: " + reason)
        connected = false
    }
    
    override Unit onFailure(WebSocket webSocket, Throwable t, Response response) {
        Log.e(TAG, "WebSocket failure", t)
        connected = false
        
        // Attempt reconnection after delay
        scheduleReconnect()
    }
    
    /**
     * Dispatch event to registered listeners
     */
     private fun dispatchEvent(event: EventMessage) {
        val listeners: CopyOnWriteArrayList<EventListener> = eventListeners.get(event.getType())
        if (listeners != null) {
            for (EventListener listener : listeners) {
                try {
                    listener.onEvent(event)
                } catch (Exception e) {
                    Log.e(TAG, "Error in event listener", e)
                }
            }
        }
    }
    
    /**
     * Schedule reconnection with exponential backoff
     */
     private fun scheduleReconnect() {
        if (lastConnectionUrl == null) {
            Log.w(TAG, "No last connection URL available for reconnection")
            return
        }
        
        val currentAttempt: Int = reconnectAttempts.incrementAndGet()
        if (currentAttempt > MAX_RECONNECT_ATTEMPTS) {
            Log.e(TAG, "Maximum reconnection attempts reached (" + MAX_RECONNECT_ATTEMPTS + ")")
            return
        }
        
        // Exponential backoff: 1s, 2s, 4s, 8s, 16s
        val delaySeconds: Long = (Long) Math.pow(2, currentAttempt - 1)
        
        Log.i(TAG, "Scheduling reconnection attempt " + currentAttempt + " in " + delaySeconds + " seconds")
        
        // Use Handler to schedule the reconnection
        reconnectHandler.postDelayed(Runnable() {
            override Unit run() {
                Log.i(TAG, "Attempting reconnection " + currentAttempt + "/" + MAX_RECONNECT_ATTEMPTS)
                connect(lastConnectionUrl)
            }
        }, delaySeconds * 1000); // Convert to milliseconds
    }
    
     public fun isConnected(): Boolean {
        return connected
    }
    
    /**
     * Event listener interface
     */
    interface EventListener {
         fun onEvent(event: EventMessage)
    }
    
    /**
     * Event message wrapper
     */
    @JvmStatic
    class EventMessage {
        private val String type
        private val String data
        private val Long timestamp
        
        public EventMessage(String type, String data, Long timestamp) {
            this.type = type
            this.data = data
            this.timestamp = timestamp
        }
        
         public fun getType(): String {
            return type
        }
        
         public fun getData(): String {
            return data
        }
        
         public fun getTimestamp(): Long {
            return timestamp
        }
        
        /**
         * Parse event message from JSON text
         * Supports Second Life event queue format
         */
        @JvmStatic
     fun parseFromJson(json: String): EventMessage {
            try {
                // Simple JSON parsing for Second Life event format
                // Expected format: {"message": "type:data", "timestamp": 123456}
                val type: String = "unknown"
                val data: String = json
                val timestamp: Long = System.currentTimeMillis()
                
                // Extract message type from common SL event patterns
                if (json.contains("\"message\"")) {
                    val messagePattern: Pattern = Pattern.compile("\"message\"\\s*:\\s*\"([^\"]+)\"")
                    val matcher: Matcher = messagePattern.matcher(json)
                    if (matcher.find()) {
                        val messageContent: String = matcher.group(1)
                        if (messageContent.contains(":")) {
                            val parts: Array<String> = messageContent.split(":", 2)
                            type = parts[0].trim()
                            data = parts[1].trim()
                        } else {
                            data = messageContent
                        }
                    }
                }
                
                // Extract timestamp if present
                val timestampPattern: Pattern = Pattern.compile("\"timestamp\"\\s*:\\s*(\\d+)")
                val timestampMatcher: Matcher = timestampPattern.matcher(json)
                if (timestampMatcher.find()) {
                    timestamp = Long.parseLong(timestampMatcher.group(1))
                }
                
                // Identify common Second Life event types
                if (json.toLowerCase().contains("chat") || json.toLowerCase().contains("im")) {
                    type = "chat"
                } else if (json.toLowerCase().contains("objectupdate")) {
                    type = "objectUpdate"
                } else if (json.toLowerCase().contains("agent")) {
                    type = "agentUpdate"
                }
                
                return EventMessage(type, data, timestamp)
                
            } catch (Exception e) {
                Log.w(TAG, "Failed to parse JSON event message: " + json, e)
                return EventMessage("parse_error", json, System.currentTimeMillis())
            }
        }
        
        /**
         * Parse event message from binary data
         * Handles Second Life UDP-style binary messages
         */
        @JvmStatic
     fun parseFromBytes(bytes: ByteArray): EventMessage {
            try {
                if (bytes == null || bytes.length == 0) {
                    return EventMessage("empty", "", System.currentTimeMillis())
                }
                
                // Second Life binary messages often start with message type flags
                val type: String = "binary"
                val data: String = ""
                
                // Check for common Second Life binary message patterns
                if (bytes.length > 4) {
                    // First 4 bytes often contain message type information
                    val messageType: Int = ((bytes[0] & 0xFF) << 24) | 
                                    ((bytes[1] & 0xFF) << 16) |
                                    ((bytes[2] & 0xFF) << 8) | 
                                    (bytes[3] & 0xFF)
                    
                    // Map common Second Life message types
                    switch (messageType & 0xFF) {
                        case 0x01:
                            type = "objectUpdate"
                            break
                        case 0x02:
                            type = "agentMovement"
                            break
                        case 0x03:
                            type = "chatMessage"
                            break
                        default:
                            type = "binary_" + Integer.toHexString(messageType & 0xFF)
                    }
                }
                
                // Convert bytes to hex string for debugging
                val hexString: StringBuilder = StringBuilder()
                for (Int i = 0; i < Math.min(bytes.length, 32); i++) { // Limit to first 32 bytes
                    hexString.append(String.format("%02X ", bytes[i]))
                }
                data = "Binary data (" + bytes.length + " bytes): " + hexString.toString()
                
                return EventMessage(type, data, System.currentTimeMillis())
                
            } catch (Exception e) {
                Log.w(TAG, "Failed to parse binary event message", e)
                return EventMessage("binary_error", "Failed to parse " + bytes.length + " bytes", 
                                      System.currentTimeMillis())
            }
        }
    }
    
    fun shutdown() {
        disconnect()
        client.dispatcher().executorService().shutdown()
        client.connectionPool().evictAll()
    }
}