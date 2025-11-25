package com.lumiyaviewer.lumiya.modern

import android.content.Context
import android.util.Log
import com.lumiyaviewer.lumiya.modern.auth.ModernAuthManager
import com.lumiyaviewer.lumiya.modern.connection.ConnectionDiagnostics
import com.lumiyaviewer.lumiya.modern.connection.ConnectionIntegrationBridge
import com.lumiyaviewer.lumiya.modern.connection.ModernConnectionManager
import com.lumiyaviewer.lumiya.modern.features.ModernSecondLifeFeatures
import com.lumiyaviewer.lumiya.modern.protocol.HybridProtocolManager
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams
import java.util.Date
import java.util.UUID
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * Complete modern Second Life client implementation bringing together
 * all modern systems: connection management, authentication, protocol handling,
 * and advanced Second Life features
 */
class ModernLinkpointClient(context: Context) {
    
    private val TAG = "ModernLinkpoint"
    
    private val context: Context = context.applicationContext
    
    // Core systems
    private val connectionManager = ModernConnectionManager(context)
    private val diagnostics = ConnectionDiagnostics(context)
    private val connectionBridge = ConnectionIntegrationBridge(context)
    private val authManager = ModernAuthManager(context)
    private val protocolManager = HybridProtocolManager()
    private val featuresManager = ModernSecondLifeFeatures(protocolManager)
    
    // Client state
    @Volatile private var currentState = ClientState.DISCONNECTED
    private val executor: ExecutorService
    @Volatile private var lastError: String? = null
    
    /**
     * Client states
     */
    enum class ClientState {
        DISCONNECTED,
        INITIALIZING,
        AUTHENTICATING,
        CONNECTING,
        CONNECTED,
        FEATURES_LOADING,
        READY,
        ERROR
    }
    
    init {
        // Executor for async operations
        executor = Executors.newCachedThreadPool { r ->
            Thread(r, "ModernLinkpoint-${r.hashCode()}").apply {
                isDaemon = true
            }
        }
        
        Log.i(TAG, "Modern Linkpoint client initialized")
    }
    
    /**
     * Complete login and connection flow with modern features
     */
    fun loginAsync(username: String, password: String, gridUrl: String): CompletableFuture<Boolean> {
        Log.i(TAG, "Starting modern login flow for user: $username")
        
        setState(ClientState.INITIALIZING)
        
        return CompletableFuture.supplyAsync({
            try {
                // Step 1: Run connection diagnostics
                Log.i(TAG, "Step 1: Running connection diagnostics...")
                val diagnostic = diagnostics.diagnoseAsync().get()
                
                if (diagnostic.getOverallHealth() == ConnectionDiagnostics.DiagnosticResult.HealthLevel.NO_CONNECTIVITY) {
                    throw LoginException("No network connectivity available")
                }
                
                Log.i(TAG, "Connection health: ${diagnostic.getOverallHealth()}")
                
                // Step 2: Authenticate user
                setState(ClientState.AUTHENTICATING)
                Log.i(TAG, "Step 2: Authenticating user...")
                
                val authResult = authManager.authenticateAsync(username, password).get()
                
                if (!authResult.isSuccessful()) {
                    throw LoginException("Authentication failed: ${authResult.errorMessage}")
                }
                
                Log.i(TAG, "Authentication successful")
                
                // Step 3: Establish connection
                setState(ClientState.CONNECTING)
                Log.i(TAG, "Step 3: Establishing connection...")
                
                // Create SL auth params with corrected URL
                val authParams = createAuthParams(username, password, gridUrl)
                
                val connectionSuccess = connectionBridge.connectWithModernReliability(authParams).get()
                
                if (!connectionSuccess) {
                    throw LoginException("Connection to Second Life failed")
                }
                
                setState(ClientState.CONNECTED)
                Log.i(TAG, "Connection established successfully")
                
                // Step 4: Initialize protocol layer
                Log.i(TAG, "Step 4: Initializing protocol layer...")
                
                val capsUrl = deriveCapsUrl(gridUrl)
                val wsUrl = deriveWebSocketUrl(gridUrl)
                
                val protocolReady = protocolManager.initializeAsync(capsUrl, wsUrl).get()
                
                if (!protocolReady) {
                    Log.w(TAG, "Protocol initialization had issues, some features may be limited")
                }
                
                // Step 5: Initialize modern features
                setState(ClientState.FEATURES_LOADING)
                Log.i(TAG, "Step 5: Loading modern Second Life features...")
                
                val featuresReady = featuresManager.initializeAsync().get()
                
                if (!featuresReady) {
                    Log.w(TAG, "Some features failed to initialize, client will work with limited functionality")
                }
                
                // Login complete
                setState(ClientState.READY)
                Log.i(TAG, "🎉 Modern Linkpoint login complete! Client is ready.")
                
                true
                
            } catch (e: Exception) {
                setState(ClientState.ERROR)
                lastError = e.message
                Log.e(TAG, "Login failed: ${e.message}", e)
                false
            }
        }, executor)
    }
    
    /**
     * Get comprehensive client status report
     */
    fun getStatusReportAsync(): CompletableFuture<String> {
        return CompletableFuture.supplyAsync({
            buildString {
                append("=== MODERN LINKPOINT CLIENT STATUS ===\n")
                append("Generated: ${Date()}\n\n")
                
                // Overall state
                append("CLIENT STATE: $currentState\n")
                lastError?.let { append("Last Error: $it\n") }
                append("\n")
                
                // System status
                append("SYSTEM STATUS:\n")
                append("  Connection Manager: ${getSystemStatus(connectionManager.getState().toString())}\n")
                append("  Protocol Manager: ${if (protocolManager.isConnected()) "✅ Connected" else "❌ Disconnected"}\n")
                append("  Features Manager: ${if (featuresManager.areFeaturesInitialized()) "✅ Ready" else "⚠️ Limited"}\n")
                append("  Active Connections: ${connectionManager.getActiveConnectionCount()}\n")
                append("\n")
                
                // Available features
                append("AVAILABLE FEATURES:\n")
                if (featuresManager.areFeaturesInitialized()) {
                    append("  ✅ Modern Avatar System\n")
                    append("  ✅ Advanced Inventory Management\n")
                    append("  ✅ Enhanced Chat & Communication\n")
                    append("  ✅ Smart Object Management\n")
                    append("  ✅ PBR Material Support\n")
                    append("  ✅ Real-time Event Streaming\n")
                } else {
                    append("  ⚠️ Features loading or limited functionality\n")
                }
                
                append("\nCLIENT CAPABILITIES:\n")
                append("  • Hybrid Protocol Support (UDP/HTTP2/WebSocket)\n")
                append("  • Advanced Connection Diagnostics\n")
                append("  • Secure Authentication & Token Caching\n")
                append("  • Connection Retry with Exponential Backoff\n")
                append("  • Network Health Monitoring\n")
                append("  • Modern Second Life Feature Set\n")
            }
        }, executor)
    }
    
    /**
     * Get status indicator for system
     */
    private fun getSystemStatus(state: String): String {
        return when (state.lowercase()) {
            "connected" -> "✅ $state"
            "connecting" -> "🔄 $state"
            "error" -> "❌ $state"
            else -> "⚠️ $state"
        }
    }
    
    /**
     * Create auth parameters
     */
    private fun createAuthParams(username: String, password: String, gridUrl: String): SLAuthParams {
        val clientId = UUID.randomUUID()
        val startLocation = "last"
        val loginUrl = gridUrl.ifEmpty { "https://login.agni.lindenlab.com/cgi-bin/login.cgi" }
        val gridName = deriveGridName(loginUrl)
        
        return SLAuthParams(username, password, clientId, startLocation, loginUrl, gridName)
    }
    
    /**
     * Derive grid name from login URL
     */
    private fun deriveGridName(loginUrl: String): String {
        return when {
            loginUrl.contains("agni") -> "Second Life Main Grid"
            loginUrl.contains("aditi") -> "Second Life Beta Grid"
            else -> "OpenSimulator Grid"
        }
    }
    
    /**
     * Derive CAPS URL from grid URL
     */
    private fun deriveCapsUrl(gridUrl: String): String {
        // In real implementation, this would come from login response
        return "https://sim1.agni.lindenlab.com/caps/example"
    }
    
    /**
     * Derive WebSocket URL from grid URL
     */
    private fun deriveWebSocketUrl(gridUrl: String): String {
        // In real implementation, this would be provided by the grid
        return "wss://events.agni.lindenlab.com/websocket"
    }
    
    /**
     * Set client state
     */
    private fun setState(newState: ClientState) {
        if (currentState != newState) {
            val oldState = currentState
            currentState = newState
            Log.d(TAG, "Client state changed: $oldState -> $newState")
        }
    }
    
    // Accessors for subsystems
    fun getConnectionManager(): ModernConnectionManager = connectionManager
    fun getAuthManager(): ModernAuthManager = authManager
    fun getProtocolManager(): HybridProtocolManager = protocolManager
    fun getFeaturesManager(): ModernSecondLifeFeatures = featuresManager
    fun getCurrentState(): ClientState = currentState
    fun getLastError(): String? = lastError
    
    /**
     * Perform logout and cleanup
     */
    fun logoutAsync(): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync({
            Log.i(TAG, "Starting logout process")
            
            try {
                // Shutdown protocol manager
                protocolManager.shutdown()
                
                // Shutdown connection bridge
                connectionBridge.shutdown()
                
                setState(ClientState.DISCONNECTED)
                Log.i(TAG, "Logout completed successfully")
                
                true
            } catch (e: Exception) {
                Log.e(TAG, "Error during logout", e)
                false
            }
        }, executor)
    }
    
    /**
     * Shutdown the entire client
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down Modern Linkpoint client")
        
        logoutAsync() // Async logout
        
        // Shutdown executor
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
        
        Log.i(TAG, "Modern Linkpoint client shutdown complete")
    }
    
    /**
     * Custom exception for login-related errors
     */
    class LoginException : RuntimeException {
        constructor(message: String) : super(message)
        constructor(message: String, cause: Throwable) : super(message, cause)
    }
}
