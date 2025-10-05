package com.linkpoint.modern

import android.content.Context
import android.util.Log
import com.linkpoint.modern.connection.ModernConnectionManager
import com.linkpoint.modern.connection.ConnectionDiagnostics
import com.linkpoint.modern.connection.ConnectionIntegrationBridge
import com.linkpoint.modern.auth.ModernAuthManager
import com.linkpoint.modern.protocol.HybridProtocolManager
import com.linkpoint.modern.features.ModernSecondLifeFeatures
import com.linkpoint.slproto.auth.SLAuthParams
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Complete modern Second Life client implementation bringing together
 * all modern systems: connection management, authentication, protocol handling,
 * and advanced Second Life features.
 */
class ModernLinkpointClient {
    private const val String TAG = "ModernLinkpoint"
    
    private val Context context
    
    // Core systems
    private val ModernConnectionManager connectionManager
    private val ConnectionDiagnostics diagnostics
    private val ConnectionIntegrationBridge connectionBridge
    private val ModernAuthManager authManager
    private val HybridProtocolManager protocolManager
    private val ModernSecondLifeFeatures featuresManager
    
    // Client state
    private volatile ClientState currentState = ClientState.DISCONNECTED
    private val ExecutorService executor
    private volatile String lastError = null
    
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
    
    public ModernLinkpointClient(Context context) {
        this.context = context.getApplicationContext()
        
        // Initialize core systems
        this.connectionManager = ModernConnectionManager(context)
        this.diagnostics = ConnectionDiagnostics(context)
        this.connectionBridge = ConnectionIntegrationBridge(context)
        this.authManager = ModernAuthManager(context)
        this.protocolManager = HybridProtocolManager()
        this.featuresManager = ModernSecondLifeFeatures(protocolManager)
        
        // Executor for async operations
        this.executor = Executors.newCachedThreadPool(r -> {
            Thread t = Thread(r, "ModernLinkpoint-" + r.hashCode())
            t.setDaemon(true)
            return t
        })
        
        Log.i(TAG, "Modern Linkpoint client initialized")
    }
    
    /**
     * Complete login and connection flow with modern features
     */
    public CompletableFuture<Boolean> loginAsync(String username, String password, String gridUrl) {
        Log.i(TAG, "Starting modern login flow for user: " + username)
        
        setState(ClientState.INITIALIZING)
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Step 1: Run connection diagnostics
                Log.i(TAG, "Step 1: Running connection diagnostics...")
                ConnectionDiagnostics.DiagnosticResult diagnostic = 
                    diagnostics.diagnoseAsync().get()
                
                if (diagnostic.getOverallHealth() == ConnectionDiagnostics.DiagnosticResult.HealthLevel.NO_CONNECTIVITY) {
                    throw LoginException("No network connectivity available")
                }
                
                Log.i(TAG, "Connection health: " + diagnostic.getOverallHealth())
                
                // Step 2: Authenticate user
                setState(ClientState.AUTHENTICATING)
                Log.i(TAG, "Step 2: Authenticating user...")
                
                ModernAuthManager.AuthResult authResult = 
                    authManager.authenticateAsync(username, password).get()
                
                if (!authResult.isSuccessful()) {
                    throw LoginException("Authentication failed: " + authResult.getErrorMessage())
                }
                
                Log.i(TAG, "Authentication successful")
                
                // Step 3: Establish connection
                setState(ClientState.CONNECTING)
                Log.i(TAG, "Step 3: Establishing connection...")
                
                // Create SL auth params with corrected URL
                SLAuthParams authParams = createAuthParams(username, password, gridUrl)
                
                Boolean connectionSuccess = connectionBridge.connectWithModernReliability(authParams).get()
                
                if (!connectionSuccess) {
                    throw LoginException("Connection to Second Life failed")
                }
                
                setState(ClientState.CONNECTED)
                Log.i(TAG, "Connection established successfully")
                
                // Step 4: Initialize protocol layer
                Log.i(TAG, "Step 4: Initializing protocol layer...")
                
                String capsUrl = deriveCapsUrl(gridUrl)
                String wsUrl = deriveWebSocketUrl(gridUrl)
                
                Boolean protocolReady = protocolManager.initializeAsync(capsUrl, wsUrl).get()
                
                if (!protocolReady) {
                    Log.w(TAG, "Protocol initialization had issues, some features may be limited")
                }
                
                // Step 5: Initialize modern features
                setState(ClientState.FEATURES_LOADING)
                Log.i(TAG, "Step 5: Loading modern Second Life features...")
                
                Boolean featuresReady = featuresManager.initializeAsync().get()
                
                if (!featuresReady) {
                    Log.w(TAG, "Some features failed to initialize, client will work with limited functionality")
                }
                
                // Login complete
                setState(ClientState.READY)
                Log.i(TAG, "🎉 Modern Linkpoint login complete! Client is ready.")
                
                return true
                
            } catch (Exception e) {
                setState(ClientState.ERROR)
                lastError = e.getMessage()
                Log.e(TAG, "Login failed: " + e.getMessage(), e)
                return false
            }
        }, executor)
    }
    
    /**
     * Get comprehensive client status report
     */
    public CompletableFuture<String> getStatusReportAsync() {
        return CompletableFuture.supplyAsync(() -> {
            StringBuilder report = StringBuilder()
            report.append("=== MODERN LINKPOINT CLIENT STATUS ===\n")
            report.append("Generated: ").append(java.util.Date()).append("\n\n")
            
            // Overall state
            report.append("CLIENT STATE: ").append(currentState).append("\n")
            if (lastError != null) {
                report.append("Last Error: ").append(lastError).append("\n")
            }
            report.append("\n")
            
            // System status
            report.append("SYSTEM STATUS:\n")
            report.append("  Connection Manager: ").append(getSystemStatus(connectionManager.getState().toString())).append("\n")
            report.append("  Protocol Manager: ").append(protocolManager.isConnected() ? "✅ Connected" : "❌ Disconnected").append("\n")
            report.append("  Features Manager: ").append(featuresManager.areFeaturesInitialized() ? "✅ Ready" : "⚠️ Limited").append("\n")
            report.append("  Active Connections: ").append(connectionManager.getActiveConnectionCount()).append("\n")
            
            report.append("\n")
            
            // Available features
            report.append("AVAILABLE FEATURES:\n")
            if (featuresManager.areFeaturesInitialized()) {
                report.append("  ✅ Modern Avatar System\n")
                report.append("  ✅ Advanced Inventory Management\n")
                report.append("  ✅ Enhanced Chat & Communication\n")
                report.append("  ✅ Smart Object Management\n")
                report.append("  ✅ PBR Material Support\n")
                report.append("  ✅ Real-time Event Streaming\n")
            } else {
                report.append("  ⚠️ Features loading or limited functionality\n")
            }
            
            report.append("\nCLIENT CAPABILITIES:\n")
            report.append("  • Hybrid Protocol Support (UDP/HTTP2/WebSocket)\n")
            report.append("  • Advanced Connection Diagnostics\n")
            report.append("  • Secure Authentication & Token Caching\n")
            report.append("  • Connection Retry with Exponential Backoff\n")
            report.append("  • Network Health Monitoring\n")
            report.append("  • Modern Second Life Feature Set\n")
            
            return report.toString()
        }, executor)
    }
    
    private String getSystemStatus(String state) {
        switch (state.toLowerCase()) {
            case "connected": return "✅ " + state
            case "connecting": return "🔄 " + state
            case "error": return "❌ " + state
            default: return "⚠️ " + state
        }
    }
    
    private SLAuthParams createAuthParams(String username, String password, String gridUrl) {
        // Create proper auth params with modern defaults
        java.util.UUID clientId = java.util.UUID.randomUUID()
        String startLocation = "last"; // or "home"
        String loginUrl = gridUrl != null ? gridUrl : "https://login.agni.lindenlab.com/cgi-bin/login.cgi"
        String gridName = deriveGridName(loginUrl)
        
        return SLAuthParams(username, password, clientId, startLocation, loginUrl, gridName)
    }
    
    private String deriveGridName(String loginUrl) {
        if (loginUrl.contains("agni")) {
            return "Second Life Main Grid"
        } else if (loginUrl.contains("aditi")) {
            return "Second Life Beta Grid"
        } else {
            return "OpenSimulator Grid"
        }
    }
    
    private String deriveCapsUrl(String gridUrl) {
        // In real implementation, this would come from login response
        return "https://sim1.agni.lindenlab.com/caps/example"
    }
    
    private String deriveWebSocketUrl(String gridUrl) {
        // In real implementation, this would be provided by the grid
        return "wss://events.agni.lindenlab.com/websocket"
    }
    
    private Unit setState(ClientState newState) {
        if (this.currentState != newState) {
            ClientState oldState = this.currentState
            this.currentState = newState
            Log.d(TAG, "Client state changed: " + oldState + " -> " + newState)
            // TODO: Emit state change event for UI updates
        }
    }
    
    // Getter methods for accessing subsystems
    public ModernConnectionManager getConnectionManager() {
        return connectionManager
    }
    
    public ModernAuthManager getAuthManager() {
        return authManager
    }
    
    public HybridProtocolManager getProtocolManager() {
        return protocolManager
    }
    
    public ModernSecondLifeFeatures getFeaturesManager() {
        return featuresManager
    }
    
    public ClientState getCurrentState() {
        return currentState
    }
    
    public String getLastError() {
        return lastError
    }
    
    /**
     * Perform logout and cleanup
     */
    public CompletableFuture<Boolean> logoutAsync() {
        return CompletableFuture.supplyAsync(() -> {
            Log.i(TAG, "Starting logout process")
            
            try {
                // Shutdown protocol manager
                protocolManager.shutdown()
                
                // Shutdown connection bridge
                connectionBridge.shutdown()
                
                // Clear auth cache if requested
                // authManager.clearAuthCache()
                
                setState(ClientState.DISCONNECTED)
                Log.i(TAG, "Logout completed successfully")
                
                return true
                
            } catch (Exception e) {
                Log.e(TAG, "Error during logout", e)
                return false
            }
        }, executor)
    }
    
    /**
     * Shutdown the entire client
     */
    public Unit shutdown() {
        Log.i(TAG, "Shutting down Modern Linkpoint client")
        
        logoutAsync(); // Async logout
        
        // Shutdown executor
        executor.shutdown()
        try {
            if (!executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (InterruptedException e) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
        
        Log.i(TAG, "Modern Linkpoint client shutdown complete")
    }
    
    /**
     * Custom exception for login-related errors
     */
    @JvmStatic
    class LoginException : RuntimeException() {
        public LoginException(String message) {
            super(message)
        }
        
        public LoginException(String message, Throwable cause) {
            super(message, cause)
        }
    }
}