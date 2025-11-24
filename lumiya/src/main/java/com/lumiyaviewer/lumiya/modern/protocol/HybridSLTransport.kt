package com.lumiyaviewer.lumiya.modern.protocol

import android.util.Log
import java.util.concurrent.CompletableFuture
import java.util.regex.Pattern

/**
 * Hybrid transport layer combining HTTP/2, WebSocket, and UDP
 * Based on Second Life Integration Guide modernization plans
 */
class HybridSLTransport {
    private val TAG = "HybridSLTransport"
    
    private var capsClient: HTTP2CapsClient? = null
    private var eventClient: WebSocketEventClient? = null
    private val router: MessageRouter = MessageRouter()
    
    init {
        try {
            capsClient = HTTP2CapsClient()
            eventClient = WebSocketEventClient()
            Log.i(TAG, "Hybrid transport layer initialized")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing clients", e)
        }
    }
    
    fun setAuthToken(token: String) {
        capsClient?.setAuthToken(token)
        eventClient?.setAuthToken(token)
        Log.d(TAG, "Auth token configured for all transports")
    }
    
    fun initialize(eventQueueUrl: String?, seedCapability: String?) {
        try {
            if (seedCapability != null) {
                Log.i(TAG, "Configuring CAPS client with seed capability")
                val capsUrls = parseSeedCapability(seedCapability)
                capsClient?.configureCapabilities(capsUrls)
            }
            
            if (eventQueueUrl != null) {
                Log.i(TAG, "Connecting to event queue: $eventQueueUrl")
                eventClient?.connect(eventQueueUrl)
            }
            
            Log.i(TAG, "Hybrid transport initialized successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize hybrid transport", e)
        }
    }
    
    fun sendMessage(message: ModernMessage): CompletableFuture<SLResponse> {
        val route = router.selectOptimalRoute(message)
        
        Log.d(TAG, "Routing message via ${route.transport}: ${message.javaClass.simpleName}")
        
        return when (route.transport) {
            TransportType.HTTP2_CAPS -> {
                val client = capsClient
                if (client != null && route.url != null) {
                    client.sendAsync(route.url, message.toLLSDXML())
                        .thenApply { responseData -> parseHTTP2Response(responseData) }
                } else {
                    CompletableFuture.completedFuture(SLResponse("error", "CAPS client not ready"))
                }
            }
            TransportType.WEBSOCKET_REALTIME -> sendViaWebSocket(message)
            TransportType.UDP_LEGACY -> CompletableFuture.completedFuture(SLResponse("error", "UDP not supported"))
        }
    }
    
    private fun sendViaWebSocket(message: ModernMessage): CompletableFuture<SLResponse> {
        val future = CompletableFuture<SLResponse>()
        val client = eventClient
        
        try {
            if (client == null || !client.isConnected()) {
                future.completeExceptionally(IllegalStateException("WebSocket not connected"))
                return future
            }
            
            val jsonMessage = message.toJSON()
            Log.d(TAG, "Sending WebSocket message: ${jsonMessage.take(100)}")
            
            val sent = client.sendRawMessage(jsonMessage)
            
            if (sent) {
                future.complete(SLResponse("websocket_ack", "Message sent successfully"))
            } else {
                future.completeExceptionally(RuntimeException("Failed to send WebSocket message"))
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending WebSocket message", e)
            future.completeExceptionally(e)
        }
        
        return future
    }
    
    private fun parseSeedCapability(seedCapability: String): Map<String, String> {
        val capabilities = HashMap<String, String>()
        
        try {
            val capabilityNames = listOf(
                "EventQueueGet", "ChatSessionRequest", "SendChatMessage",
                "UploadBakedTexture", "FetchInventory", "GetMesh", 
                "GetTexture", "AgentPreferences", "UpdateAgentInformation"
            )
            
            for (capName in capabilityNames) {
                val capPattern = Pattern.compile(
                    "<key>" + Pattern.quote(capName) + "</key>\\s*<string>([^<]+)</string>",
                    Pattern.CASE_INSENSITIVE
                )
                
                val matcher = capPattern.matcher(seedCapability)
                if (matcher.find()) {
                    val capUrl = matcher.group(1).trim()
                    capabilities[capName] = capUrl
                    Log.d(TAG, "Parsed capability: $capName -> $capUrl")
                }
            }
            
            if (capabilities.isEmpty() && seedCapability.contains(":")) {
                val jsonPattern = Pattern.compile("\"([^\"]+)\"\\s*:\\s*\"([^\"]+)\"")
                val jsonMatcher = jsonPattern.matcher(seedCapability)
                
                while (jsonMatcher.find()) {
                    val key = jsonMatcher.group(1)
                    val value = jsonMatcher.group(2)
                    
                    for (capName in capabilityNames) {
                        if (key.equals(capName, ignoreCase = true)) {
                            capabilities[capName] = value
                            Log.d(TAG, "Parsed JSON capability: $key -> $value")
                            break
                        }
                    }
                }
            }
            
            Log.i(TAG, "Parsed ${capabilities.size} capabilities from seed")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse seed capability", e)
        }
        
        return capabilities
    }
    
    private fun parseHTTP2Response(responseData: String?): SLResponse {
        try {
            var responseType = "caps_response"
            var parsedData = responseData ?: ""
            
            if (responseData != null) {
                if (responseData.contains("<map>")) {
                    responseType = "llsd_map"
                    
                    val mapPattern = Pattern.compile("<key>([^<]+)</key>\\s*<(string|integer|real|boolean)>([^<]*)</\\2>")
                    val matcher = mapPattern.matcher(responseData)
                    
                    val parsed = StringBuilder("LLSD Map: ")
                    while (matcher.find()) {
                        val key = matcher.group(1)
                        val value = matcher.group(3)
                        parsed.append(key).append("=").append(value).append(" ")
                    }
                    
                    if (parsed.length > "LLSD Map: ".length) {
                        parsedData = parsed.toString()
                    }
                    
                } else if (responseData.contains("<array>")) {
                    responseType = "llsd_array"
                    parsedData = "LLSD Array with " + responseData.split("<").size + " elements"
                    
                } else if (responseData.contains("<string>")) {
                    val stringPattern = Pattern.compile("<string>([^<]*)</string>")
                    val stringMatcher = stringPattern.matcher(responseData)
                    if (stringMatcher.find()) {
                        parsedData = stringMatcher.group(1)
                        responseType = "llsd_string"
                    }
                }
                
                if (responseData.lowercase().contains("error") || 
                    responseData.lowercase().contains("fail")) {
                    responseType = "error_response"
                }
            }
            
            Log.d(TAG, "Parsed CAPS response type: $responseType")
            return SLResponse(responseType, parsedData)
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse HTTP/2 response", e)
            return SLResponse("parse_error", "Failed to parse response: ${e.message}")
        }
    }
    
    fun subscribeToEvents(eventType: String, listener: WebSocketEventClient.EventListener) {
        eventClient?.subscribe(eventType, listener)
        Log.d(TAG, "Subscribed to event type: $eventType")
    }
    
    fun uploadAsset(assetData: ByteArray, contentType: String, progressListener: Any?): CompletableFuture<String> {
        return CompletableFuture.completedFuture("mock_upload_id")
    }
    
    fun isConnected(): Boolean {
        return eventClient?.isConnected() ?: false
    }
    
    fun shutdown() {
        Log.i(TAG, "Shutting down hybrid transport")
        capsClient?.shutdown()
        eventClient?.shutdown()
    }
    
    abstract class ModernMessage(protected val type: String) {
        protected val timestamp: Long = System.currentTimeMillis()
        
        fun getType(): String = type
        
        abstract fun toLLSDXML(): String
        
        fun toJSON(): String {
            return String.format("{\"type\":\"%s\",\"timestamp\":%d,\"data\":%s}", 
                               type, timestamp, getMessageDataJSON())
        }
        
        protected open fun getMessageDataJSON(): String = "{}"
    }
    
    private class MessageRouter {
        fun selectOptimalRoute(message: ModernMessage): TransportRoute {
            val messageType = message.javaClass.simpleName
            
            if (messageType.contains("Asset") || messageType.contains("Upload") || 
                messageType.contains("Texture") || messageType.contains("Inventory")) {
                return TransportRoute(TransportType.HTTP2_CAPS, "https://example.com/caps")
            }
            
            if (messageType.contains("Chat") || messageType.contains("ObjectUpdate") || 
                messageType.contains("Avatar") || messageType.contains("Position")) {
                return TransportRoute(TransportType.WEBSOCKET_REALTIME, null)
            }
            
            return TransportRoute(TransportType.HTTP2_CAPS, "https://example.com/caps")
        }
    }
    
    private class TransportRoute(val transport: TransportType, val url: String?)
    
    private enum class TransportType {
        HTTP2_CAPS,
        WEBSOCKET_REALTIME,
        UDP_LEGACY
    }
    
    class SLResponse(val type: String, val data: String) {
        fun getType(): String = type
        fun getData(): String = data
    }
}
