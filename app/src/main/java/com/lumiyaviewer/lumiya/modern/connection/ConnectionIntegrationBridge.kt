package com.lumiyaviewer.lumiya.modern.connection

import android.content.Context
import android.util.Log
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.slproto.SLGridConnection
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthParams
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.events.SLConnectionStateChangedEvent
import com.lumiyaviewer.lumiya.slproto.events.SLDisconnectEvent
import com.lumiyaviewer.lumiya.slproto.events.SLLoginResultEvent
import java.util.concurrent.CompletableFuture

/**
 * Integration bridge between the modern connection system and legacy SLGridConnection.
 * This class provides backwards compatibility while adding modern reliability features.
 */
class ConnectionIntegrationBridge {
    private String TAG = "ConnectionBridge"
    
    private Context context
    private ModernConnectionManager modernManager
    private EventBus eventBus
    
    ConnectionIntegrationBridge(Context context) {
        this.context = context.getApplicationContext()
        this.modernManager = ModernConnectionManager(context)
        this.eventBus = EventBus.getInstance()
    }
    
    /**
     * Enhanced connection method that uses modern connection manager
     * but integrates with existing event system.
     */
    fun connectWithModernReliability(authParams: SLAuthParams): CompletableFuture<Boolean> {
        Log.i(TAG, "Starting enhanced connection with modern reliability features")
        
        // Emit connection state change
        eventBus.publish(SLConnectionStateChangedEvent(
            SLGridConnection.ConnectionState.Connecting))
        
        return modernManager.connectAsync(authParams)
            .thenApply(authReply -> {
                if (authReply != null && authReply.success) {
                    Log.i(TAG, "Modern connection successful, integrating with legacy system")
                    
                    // Emit successful login event
                    eventBus.publish(SLLoginResultEvent(
                        true, 
                        "Connection established successfully", 
                        authReply.agentID))
                    
                    // Emit connection state change
                    eventBus.publish(SLConnectionStateChangedEvent(
                        SLGridConnection.ConnectionState.Connected))
                    
                    return true
                } else {
                    String errorMessage = authReply != null ? authReply.message : 
                        "Unknown authentication error"
                    Log.e(TAG, "Modern connection failed: " + errorMessage)
                    
                    // Emit failure events
                    eventBus.publish(SLLoginResultEvent(
                        false, 
                        errorMessage, 
                        null))
                    
                    eventBus.publish(SLConnectionStateChangedEvent(
                        SLGridConnection.ConnectionState.Idle))
                    
                    return false
                }
            })
            .exceptionally(throwable -> {
                Log.e(TAG, "Connection failed with exception", throwable)
                
                // Emit disconnect event
                eventBus.publish(SLDisconnectEvent(
                    false, 
                    "Connection failed: " + throwable.getMessage()))
                
                eventBus.publish(SLConnectionStateChangedEvent(
                    SLGridConnection.ConnectionState.Idle))
                
                return false
            })
    }
    
    /**
     * Run diagnostics and return detailed connection health information
     */
    fun getDiagnosticReport(): CompletableFuture<String> {
        ConnectionDiagnostics diagnostics = ConnectionDiagnostics(context)
        
        return diagnostics.diagnoseAsync().thenApply(result -> {
            StringBuilder report = StringBuilder()
            report.append("=== Second Life Connection Diagnostic Report ===\n")
            report.append("Network Available: ").append(result.networkAvailable ? "✅" : "❌").append("\n")
            report.append("DNS Resolution: ").append(result.dnsWorking ? "✅" : "❌").append("\n")
            report.append("HTTPS Connectivity: ").append(result.httpsWorking ? "✅" : "❌").append("\n")
            report.append("Login Server Access: ").append(result.loginServerWorking ? "✅" : "❌").append("\n")
            report.append("Proxy/Firewall Detected: ").append(result.proxyDetected ? "⚠️" : "✅").append("\n")
            report.append("Overall Health: ").append(result.getOverallHealth()).append("\n")
            
            if (!result.getIssues().isEmpty()) {
                report.append("Issues Found: ").append(result.getIssues()).append("\n")
            }
            
            // Add recommendations based on health level
            switch (result.getOverallHealth()) {
                case EXCELLENT:
                    report.append("Recommendation: Connection should work perfectly!\n")
                    break
                case GOOD:
                    report.append("Recommendation: Connection should work. Login servers may be temporarily unavailable.\n")
                    break
                case POOR:
                    report.append("Recommendation: Limited connectivity. Check firewall/proxy settings.\n")
                    break
                case CRITICAL:
                    report.append("Recommendation: Network issues detected. Check internet connection.\n")
                    break
                case NO_CONNECTIVITY:
                    report.append("Recommendation: No network available. Enable WiFi or mobile data.\n")
                    break
            }
            
            return report.toString()
        })
    }
    
    /**
     * Get current connection manager state
     */
    ModernConnectionManager.ConnectionState getCurrentState() {
        return modernManager.getState()
    }
    
    /**
     * Get the last error message if any
     */
    fun getLastError(): String {
        return modernManager.getLastError()
    }
    
    /**
     * Get number of active connections
     */
    fun getActiveConnectionCount(): Int {
        return modernManager.getActiveConnectionCount()
    }
    
    /**
     * Shutdown the connection manager
     */
    fun shutdown(): Unit {
        Log.i(TAG, "Shutting down connection integration bridge")
        modernManager.shutdown()
    }
}
