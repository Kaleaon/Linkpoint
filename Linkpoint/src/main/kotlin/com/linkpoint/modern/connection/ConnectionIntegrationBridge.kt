package com.linkpoint.modern.connection

import android.content.Context
import android.util.Log
import com.linkpoint.eventbus.EventBus
import com.linkpoint.slproto.SLGridConnection
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.events.SLConnectionStateChangedEvent
import com.linkpoint.slproto.events.SLDisconnectEvent
import com.linkpoint.slproto.events.SLLoginResultEvent
import kotlinx.coroutines.*

/**
 * Integration bridge between the modern connection system and legacy SLGridConnection.
 * This class provides backwards compatibility while adding modern reliability features.
 * 
 * Fixed from original broken syntax to proper Kotlin.
 */
class ConnectionIntegrationBridge(context: Context) {
    
    companion object {
        private const val TAG = "ConnectionBridge"
    }
    
    private val appContext: Context = context.applicationContext
    private val modernManager: ModernConnectionManager = ModernConnectionManager(appContext)
    private val eventBus: EventBus = EventBus.getInstance()
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    
    /**
     * Enhanced connection method that uses modern connection manager
     * but integrates with existing event system.
     */
    fun connectWithModernReliability(authParams: SLAuthParams, callback: (Boolean) -> Unit) {
        Log.i(TAG, "Starting enhanced connection with modern reliability features")
        
        // Emit connection state change
        eventBus.publish(SLConnectionStateChangedEvent(SLGridConnection.ConnectionState.Connecting))
        
        scope.launch {
            try {
                val authReply = modernManager.connectAsync(authParams)
                
                if (authReply.success) {
                    Log.i(TAG, "Modern connection successful, integrating with legacy system")
                    
                    // Emit successful login event
                    eventBus.publish(SLLoginResultEvent(
                        true,
                        "Connection established successfully",
                        authReply.agentID
                    ))
                    
                    // Emit connection state change
                    eventBus.publish(SLConnectionStateChangedEvent(
                        SLGridConnection.ConnectionState.Connected
                    ))
                    
                    callback(true)
                } else {
                    val errorMessage = authReply.message ?: "Unknown authentication error"
                    Log.e(TAG, "Modern connection failed: $errorMessage")
                    
                    // Emit failure events
                    eventBus.publish(SLLoginResultEvent(
                        false,
                        errorMessage,
                        null
                    ))
                    
                    eventBus.publish(SLConnectionStateChangedEvent(
                        SLGridConnection.ConnectionState.Idle
                    ))
                    
                    callback(false)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Connection failed with exception", e)
                
                // Emit disconnect event
                eventBus.publish(SLDisconnectEvent(
                    false,
                    "Connection failed: ${e.message}"
                ))
                
                eventBus.publish(SLConnectionStateChangedEvent(
                    SLGridConnection.ConnectionState.Idle
                ))
                
                callback(false)
            }
        }
    }
    
    /**
     * Run diagnostics and return detailed connection health information
     */
    fun getDiagnosticReport(callback: (String) -> Unit) {
        val diagnostics = ConnectionDiagnostics(appContext)
        
        scope.launch {
            val result = diagnostics.diagnoseAsync()
            
            val report = buildString {
                appendLine("=== Second Life Connection Diagnostic Report ===")
                appendLine("Network Available: ${if (result.networkAvailable) "✓" else "✗"}")
                appendLine("DNS Resolution: ${if (result.dnsWorking) "✓" else "✗"}")
                appendLine("HTTPS Connectivity: ${if (result.httpsWorking) "✓" else "✗"}")
                appendLine("Login Server Access: ${if (result.loginServerWorking) "✓" else "✗"}")
                appendLine("Proxy/Firewall Detected: ${if (result.proxyDetected) "!" else "✓"}")
                appendLine("Overall Health: ${result.getOverallHealth()}")
                
                if (result.getIssues().isNotEmpty()) {
                    appendLine("Issues Found: ${result.getIssues()}")
                }
                
                // Add recommendations based on health level
                appendLine()
                append("Recommendation: ")
                appendLine(when (result.getOverallHealth()) {
                    ConnectionDiagnostics.DiagnosticResult.HealthLevel.EXCELLENT ->
                        "Connection should work perfectly!"
                    ConnectionDiagnostics.DiagnosticResult.HealthLevel.GOOD ->
                        "Connection should work. Login servers may be temporarily unavailable."
                    ConnectionDiagnostics.DiagnosticResult.HealthLevel.POOR ->
                        "Limited connectivity. Check firewall/proxy settings."
                    ConnectionDiagnostics.DiagnosticResult.HealthLevel.CRITICAL ->
                        "Network issues detected. Check internet connection."
                    ConnectionDiagnostics.DiagnosticResult.HealthLevel.NO_CONNECTIVITY ->
                        "No network available. Enable WiFi or mobile data."
                })
            }
            
            callback(report)
        }
    }
    
    /**
     * Get current connection manager state
     */
    fun getCurrentState(): ModernConnectionManager.ConnectionState = modernManager.getState()
    
    /**
     * Get the last error message if any
     */
    fun getLastError(): String? = modernManager.getLastError()
    
    /**
     * Get number of active connections
     */
    fun getActiveConnectionCount(): Int = modernManager.getActiveConnectionCount()
    
    /**
     * Shutdown the connection manager
     */
    fun shutdown() {
        Log.i(TAG, "Shutting down connection integration bridge")
        scope.cancel()
        modernManager.shutdown()
    }
}
