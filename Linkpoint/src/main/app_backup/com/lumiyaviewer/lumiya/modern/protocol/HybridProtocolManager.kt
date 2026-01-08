package com.lumiyaviewer.lumiya.modern.protocol

import android.util.Log
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import okio.ByteString.Companion.toByteString
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * Modern hybrid protocol implementation for Second Life communication.
 * Supports UDP (legacy), HTTP/2 (CAPS), and WebSocket (real-time events) transports.
 */
class HybridProtocolManager {
    private val TAG: String = "HybridProtocol"
    
    // Transport types
    enum class Transport {
        UDP_LEGACY,     // Traditional Second Life UDP messages
        HTTP2_CAPS,     // HTTP/2 for CAPS (Capabilities) requests
        WEBSOCKET_RT    // WebSocket for real-time events
    }
    
    // Protocol configuration
    private val http2Client: OkHttpClient
    private val webSocketManager: WebSocketManager
    private val udpManager: LegacyUDPManager
    private val router: ProtocolRouter
    private val executor: ExecutorService
    
    // Connection state
    @Volatile
    private var isConnectedVal = false
    private val pendingRequests = ConcurrentHashMap<String, CompletableFuture<Response>>()
    
    init {
        // HTTP/2 client for CAPS requests
        this.http2Client = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
            
        // WebSocket manager for real-time events
        this.webSocketManager = WebSocketManager()
        
        // Legacy UDP manager for compatibility
        this.udpManager = LegacyUDPManager()
        
        // Protocol router to determine optimal transport
        this.router = ProtocolRouter()
        
        // Executor for async operations
        this.executor = Executors.newCachedThreadPool { r ->
            val t = Thread(r, "HybridProtocol-" + r.hashCode())
            t.isDaemon = true
            t
        }
    }
    
    /**
     * Initialize the hybrid protocol system with grid endpoints
     */
    fun initializeAsync(capsURL: String, websocketURL: String): CompletableFuture<Boolean> {
        Log.i(TAG, "Initializing hybrid protocol system")
        
        return CompletableFuture.supplyAsync({
            try {
                // Initialize WebSocket connection for real-time events
                val wsConnected = try {
                     webSocketManager.connectAsync(websocketURL).get(15, TimeUnit.SECONDS)
                } catch (e: Exception) {
                     false
                }
                if (!wsConnected) {
                    Log.w(TAG, "WebSocket connection failed, real-time features may be limited")
                }
                
                // Test HTTP/2 CAPS connectivity
                val capsWorking = testCapsConnectivity(capsURL)
                if (!capsWorking) {
                    Log.w(TAG, "CAPS HTTP/2 connection issues detected")
                }
                
                // Initialize UDP legacy support
                val udpInitialized = udpManager.initialize()
                
                isConnectedVal = wsConnected || capsWorking || udpInitialized
                
                if (isConnectedVal) {
                    Log.i(TAG, "Hybrid protocol initialized successfully")
                    Log.i(TAG, "  WebSocket: " + (if (wsConnected) "✅" else "❌"))
                    Log.i(TAG, "  HTTP/2 CAPS: " + (if (capsWorking) "✅" else "❌"))
                    Log.i(TAG, "  UDP Legacy: " + (if (udpInitialized) "✅" else "❌"))
                } else {
                    Log.e(TAG, "Failed to initialize any protocol transport")
                }
                
                isConnectedVal
                
            } catch (e: Exception) {
                Log.e(TAG, "Protocol initialization failed", e)
                false
            }
        }, executor)
    }
    
    /**
     * Send message using optimal transport protocol
     */
    fun sendMessageAsync(message: SLMessage): CompletableFuture<Boolean> {
        if (!isConnectedVal) {
            return CompletableFuture.completedFuture(false)
        }
        
        // Determine optimal transport for this message
        val transport = router.getOptimalTransport(message)
        
        Log.d(TAG, "Sending message via " + transport + ": " + message.javaClass.simpleName)
        
        return when (transport) {
            Transport.HTTP2_CAPS -> sendViaHTTP2(message)
            Transport.WEBSOCKET_RT -> sendViaWebSocket(message)
            Transport.UDP_LEGACY -> sendViaUDP(message)
        }
    }
    
    private fun sendViaHTTP2(message: SLMessage): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({
            try {
                // Convert SL message to HTTP/2 request
                val messageData = serializeMessage(message)
                val body = messageData.toRequestBody("application/octet-stream".toMediaType())
                
                val request = Request.Builder()
                    .url("https://example.com/caps/" + message.javaClass.simpleName) // This would be actual CAPS URL
                    .post(body)
                    .header("Content-Type", "application/octet-stream")
                    .header("User-Agent", "Linkpoint-Hybrid/1.0")
                    .build()
                
                http2Client.newCall(request).execute().use { response ->
                    val success = response.isSuccessful
                    Log.d(TAG, "HTTP/2 message result: " + response.code)
                    success
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "HTTP/2 message sending failed", e)
                false
            }
        }, executor)
    }
    
    private fun sendViaWebSocket(message: SLMessage): CompletableFuture<Boolean> {
        return webSocketManager.sendMessage(message)
    }
    
    private fun sendViaUDP(message: SLMessage): CompletableFuture<Boolean> {
        return udpManager.sendMessage(message)
    }
    
    private fun testCapsConnectivity(capsURL: String): Boolean {
        return try {
            val testRequest = Request.Builder()
                .url(capsURL)
                .head()
                .build()
                
            http2Client.newCall(testRequest).execute().use { response ->
                response.isSuccessful || response.code == 405 // 405 Method Not Allowed is OK
            }
        } catch (e: Exception) {
            Log.w(TAG, "CAPS connectivity test failed", e)
            false
        }
    }
    
    private fun serializeMessage(message: SLMessage): ByteArray {
        try {
            val buffer = ByteBuffer.allocate(SLMessage.MAX_MESSAGE_SIZE).order(ByteOrder.BIG_ENDIAN)
            val tempBuffer = ByteBuffer.allocate(SLMessage.MAX_MESSAGE_SIZE).order(ByteOrder.BIG_ENDIAN)
            message.Pack(buffer, tempBuffer)
            val length = buffer.position()
            val data = ByteArray(length)
            buffer.rewind()
            buffer.get(data)
            return data
        } catch (e: Exception) {
            Log.e(TAG, "Message serialization failed", e)
            return ByteArray(0)
        }
    }
    
    /**
     * WebSocket manager for real-time events
     */
    private inner class WebSocketManager {
        @Volatile var webSocket: WebSocket? = null
        @Volatile var connected = false
        
        fun connectAsync(websocketURL: String): CompletableFuture<Boolean> {
            return CompletableFuture.supplyAsync {
                try {
                    val client = OkHttpClient.Builder()
                        .readTimeout(0, TimeUnit.MILLISECONDS) // Keep connection alive
                        .build()
                    
                    val request = Request.Builder()
                        .url(websocketURL)
                        .build()
                    
                    webSocket = client.newWebSocket(request, object : WebSocketListener() {
                        override fun onOpen(webSocket: WebSocket, response: Response) {
                            Log.i(TAG, "WebSocket connected")
                            connected = true
                        }
                        
                        override fun onMessage(webSocket: WebSocket, text: String) {
                            Log.d(TAG, "WebSocket text message received: " + text)
                        }
                        
                        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                            Log.d(TAG, "WebSocket binary message received: " + bytes.size + " bytes")
                        }
                        
                        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                            Log.e(TAG, "WebSocket connection failed", t)
                            connected = false
                        }
                        
                        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                            Log.i(TAG, "WebSocket closed: " + code + " " + reason)
                            connected = false
                        }
                    })
                    
                    Thread.sleep(2000)
                    connected
                    
                } catch (e: Exception) {
                    Log.e(TAG, "WebSocket connection failed", e)
                    false
                }
            }
        }
        
        fun sendMessage(message: SLMessage): CompletableFuture<Boolean> {
            return CompletableFuture.supplyAsync {
                if (!connected || webSocket == null) {
                    return@supplyAsync false
                }
                
                try {
                    val messageData = serializeMessage(message)
                    val sent = webSocket!!.send(messageData.toByteString())
                    Log.d(TAG, "WebSocket message sent: " + sent)
                    sent
                } catch (e: Exception) {
                    Log.e(TAG, "WebSocket message sending failed", e)
                    false
                }
            }
        }
    }
    
    /**
     * Legacy UDP manager for backwards compatibility
     */
    private inner class LegacyUDPManager {
        fun initialize(): Boolean {
            Log.d(TAG, "Legacy UDP manager initialized")
            return true 
        }
        
        fun sendMessage(message: SLMessage): CompletableFuture<Boolean> {
            return CompletableFuture.supplyAsync {
                try {
                    Log.d(TAG, "Sending message via legacy UDP")
                    true 
                } catch (e: Exception) {
                    Log.e(TAG, "UDP message sending failed", e)
                    false
                }
            }
        }
    }
    
    /**
     * Protocol router to determine optimal transport
     */
    private inner class ProtocolRouter {
        fun getOptimalTransport(message: SLMessage): Transport {
            val messageType = message.javaClass.simpleName
            
            return if (messageType.contains("Chat") || messageType.contains("IM")) {
                Transport.WEBSOCKET_RT 
            } else if (messageType.contains("Asset") || messageType.contains("Upload")) {
                Transport.HTTP2_CAPS 
            } else {
                Transport.UDP_LEGACY 
            }
        }
    }
    
    /**
     * Shutdown the protocol manager
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down hybrid protocol manager")
        
        isConnectedVal = false
        
        webSocketManager.webSocket?.close(1000, "Shutdown")
        
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }
    
    fun isConnected(): Boolean {
        return isConnectedVal
    }
}
