package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.core.ConnectionState
import com.linkpoint.core.RegionInfo
import com.linkpoint.LinkpointApp
import com.linkpoint.network.core.CoreNetworkingService
import com.linkpoint.network.core.NetworkStateManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

/**
 * Second Life protocol implementation
 * Handles login, message sending, and grid communication
 * 
 * Now integrated with CoreNetworkingService for:
 * - gRPC-based networking (where applicable)
 * - Comprehensive retry logic with exponential backoff
 * - Connection quality monitoring
 * - Error count tracking and thresholds
 * - Automatic reconnection
 * - Network diagnostics
 * 
 * Based on patterns from the official Second Life app.
 */
class SecondLifeProtocol(private val context: Context) {
    
    companion object {
        private const val TAG = "SLProtocol"
        private const val VIEWER_NAME = "Linkpoint"
        private const val VIEWER_VERSION = "1.0.0"
    }
    
    // Core networking service with all connection management features
    private val networkingService = CoreNetworkingService(context)
    
    // Expose connection quality and state for UI
    val qualityManager get() = networkingService.qualityManager
    val stateManager get() = networkingService.stateManager
    val connectionEvents get() = networkingService.connectionEvents
    
    init {
        // Observe connection events and update app state
        LinkpointApp.getInstance().applicationScope.launch {
            networkingService.connectionEvents.collectLatest { event ->
                when (event) {
                    is CoreNetworkingService.ConnectionEvent.Connected -> {
                        Log.d(TAG, "Connection established")
                    }
                    is CoreNetworkingService.ConnectionEvent.Disconnected -> {
                        Log.d(TAG, "Connection lost")
                        LinkpointApp.getInstance().sessionManager.setConnectionState(ConnectionState.DISCONNECTED)
                    }
                    is CoreNetworkingService.ConnectionEvent.Reconnecting -> {
                        Log.d(TAG, "Reconnecting (attempt ${event.attempt}, delay ${event.delayMs}ms)")
                        LinkpointApp.getInstance().sessionManager.setConnectionState(ConnectionState.CONNECTING)
                    }
                    is CoreNetworkingService.ConnectionEvent.Error -> {
                        Log.e(TAG, "Connection error: ${event.message} [${event.code}]")
                        if (!event.recoverable) {
                            LinkpointApp.getInstance().sessionManager.setConnectionState(ConnectionState.ERROR)
                        }
                    }
                    is CoreNetworkingService.ConnectionEvent.ConnectionReset -> {
                        Log.w(TAG, "Connection reset triggered")
                    }
                    else -> {}
                }
            }
        }
    }
    
    /**
     * Perform login to the grid
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last"
    ): LoginResult = withContext(Dispatchers.IO) {
        val app = LinkpointApp.getInstance()
        app.sessionManager.setConnectionState(ConnectionState.CONNECTING)
        
        Log.d(TAG, "Attempting login for $firstName $lastName")
        
        // Log network diagnostics before login
        networkingService.logNetworkDiagnostics()
        
        // Create password hash
        val passwordHash = "\$1\$${md5Hash(password)}"
        
        // Build XMLRPC request
        val xmlRequest = buildLoginXml(
            firstName = firstName,
            lastName = lastName,
            passwordHash = passwordHash,
            startLocation = startLocation
        )
        
        // Use CoreNetworkingService for login with comprehensive retry handling
        val result = networkingService.login(loginUri, xmlRequest)
        
        when (result) {
            is CoreNetworkingService.LoginResult.Success -> {
                val agentId = try { 
                    UUID.fromString(result.agentId) 
                } catch (e: Exception) { 
                    UUID.randomUUID() 
                }
                
                val regionInfo = RegionInfo(
                    name = "Unknown",
                    handle = 0,
                    x = 128,
                    y = 128,
                    simIP = result.simIp,
                    simPort = result.simPort
                )
                
                app.sessionManager.onLoginSuccess(
                    sessionId = result.sessionId,
                    agentId = agentId,
                    secureSessionId = "",
                    firstName = firstName,
                    lastName = lastName,
                    regionInfo = regionInfo
                )
                
                Log.i(TAG, "Login successful!")
                LoginResult.Success(agentId, result.sessionId)
            }
            is CoreNetworkingService.LoginResult.Failure -> {
                app.sessionManager.setConnectionState(ConnectionState.ERROR)
                Log.w(TAG, "Login failed: ${result.message} [${result.errorCode}]")
                LoginResult.Failure(
                    message = result.message,
                    errorCode = result.errorCode,
                    technicalDetails = result.technicalDetails
                )
            }
        }
    }
    
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        passwordHash: String,
        startLocation: String
    ): String {
        val safeFirstName = escapeXml(firstName)
        val safeLastName = escapeXml(lastName)
        val safePassword = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        
        // Generate viewer_digest - required by Second Life login server
        val viewerDigest = UUID.randomUUID().toString()
        
        // Generate unique MAC and ID0 for this device session
        val macAddress = generateMacAddress()
        val id0 = UUID.randomUUID().toString()
        
        // Build XML-RPC request with minimal whitespace for maximum compatibility
        return buildString {
            append("<?xml version=\"1.0\"?>")
            append("<methodCall>")
            append("<methodName>login_to_simulator</methodName>")
            append("<params>")
            append("<param>")
            append("<value><struct>")
            
            // Core login fields
            append("<member><name>first</name><value><string>$safeFirstName</string></value></member>")
            append("<member><name>last</name><value><string>$safeLastName</string></value></member>")
            append("<member><name>passwd</name><value><string>$safePassword</string></value></member>")
            append("<member><name>start</name><value><string>$safeStart</string></value></member>")
            
            // Viewer identification
            append("<member><name>channel</name><value><string>$VIEWER_NAME</string></value></member>")
            append("<member><name>version</name><value><string>$VIEWER_NAME $VIEWER_VERSION</string></value></member>")
            append("<member><name>platform</name><value><string>Android</string></value></member>")
            append("<member><name>platform_version</name><value><string>${android.os.Build.VERSION.RELEASE}</string></value></member>")
            
            // Device identification (required by SL login server)
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            append("<member><name>viewer_digest</name><value><string>$viewerDigest</string></value></member>")
            
            // Agreements
            append("<member><name>agree_to_tos</name><value><boolean>1</boolean></value></member>")
            append("<member><name>read_critical</name><value><boolean>1</boolean></value></member>")
            
            // Options array - comprehensive list for full functionality
            append("<member><name>options</name><value><array><data>")
            append("<value><string>inventory-root</string></value>")
            append("<value><string>inventory-skeleton</string></value>")
            append("<value><string>inventory-lib-root</string></value>")
            append("<value><string>inventory-lib-owner</string></value>")
            append("<value><string>inventory-skel-lib</string></value>")
            append("<value><string>initial-outfit</string></value>")
            append("<value><string>gestures</string></value>")
            append("<value><string>display_names</string></value>")
            append("<value><string>event_categories</string></value>")
            append("<value><string>event_notifications</string></value>")
            append("<value><string>classified_categories</string></value>")
            append("<value><string>adult_compliant</string></value>")
            append("<value><string>buddy-list</string></value>")
            append("<value><string>newuser-config</string></value>")
            append("<value><string>ui-config</string></value>")
            append("<value><string>advanced-mode</string></value>")
            append("<value><string>max-agent-groups</string></value>")
            append("<value><string>map-server-url</string></value>")
            append("<value><string>voice-config</string></value>")
            append("<value><string>tutorial_setting</string></value>")
            append("<value><string>login-flags</string></value>")
            append("<value><string>global-textures</string></value>")
            append("</data></array></value></member>")
            
            append("</struct></value>")
            append("</param>")
            append("</params>")
            append("</methodCall>")
        }
    }
    
    /**
     * Generate a pseudo-MAC address for device identification
     */
    private fun generateMacAddress(): String {
        val random = java.util.Random()
        return (0..5).joinToString(":") { 
            String.format("%02X", random.nextInt(256)) 
        }
    }
    
    private fun escapeXml(input: String): String {
        return input
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
    }
    
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Force a reconnection
     */
    suspend fun forceReconnect() {
        Log.d(TAG, "Force reconnect requested")
        networkingService.forceReconnect()
    }
    
    /**
     * Get network diagnostics report
     */
    fun getNetworkDiagnosticsReport(): String {
        val qualityReport = qualityManager.getQualityReport()
        val connectionDetails = stateManager.getConnectionDetails()
        
        return buildString {
            appendLine("=== Linkpoint Network Diagnostics ===")
            appendLine()
            appendLine("Device: ${Build.MANUFACTURER} ${Build.MODEL}")
            appendLine("Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
            appendLine()
            appendLine("=== Connection Status ===")
            appendLine("Status: ${connectionDetails.status}")
            appendLine("Connected: ${qualityReport.isConnected}")
            appendLine("Quality: ${qualityReport.quality}")
            appendLine("Is Reconnecting: ${connectionDetails.isReconnecting}")
            appendLine("Is Faulted: ${connectionDetails.isFaulted}")
            appendLine()
            appendLine("=== Network Quality ===")
            appendLine("Type: ${qualityReport.networkType}")
            appendLine("Average Latency: ${qualityReport.averageLatencyMs}ms")
            appendLine("Est. Bandwidth: ${qualityReport.estimatedBandwidthKbps}kbps")
            appendLine("Error Rate: ${(qualityReport.errorRate * 100).toInt()}%")
            appendLine("Timeout Multiplier: ${qualityReport.timeoutMultiplier}x")
            appendLine()
            appendLine("=== Connection Details ===")
            appendLine("Connection ID: ${connectionDetails.connectionInstanceId}")
            appendLine("Reconnect Count: ${connectionDetails.reconnectCount}")
            appendLine("Force Reconnect: ${connectionDetails.forceReconnect}")
            appendLine("Always Reconnect: ${connectionDetails.alwaysReconnect}")
        }
    }
    
    /**
     * Send a chat message
     */
    suspend fun sendChat(message: String, channel: Int = 0, type: ChatType = ChatType.NORMAL) {
        Log.d(TAG, "Sending chat: $message on channel $channel")
        // TODO: Implement via gRPC or UDP
    }
    
    /**
     * Request teleport to location
     */
    suspend fun teleport(regionName: String, x: Float, y: Float, z: Float): TeleportResult {
        Log.d(TAG, "Requesting teleport to $regionName ($x, $y, $z)")
        // TODO: Implement teleport request
        return TeleportResult.Success(regionName)
    }
    
    /**
     * Disconnect from grid
     */
    fun disconnect() {
        Log.i(TAG, "Disconnecting from grid")
        networkingService.disconnect()
        LinkpointApp.getInstance().sessionManager.disconnect()
    }
    
    /**
     * Clean up resources
     */
    fun shutdown() {
        Log.d(TAG, "Shutting down SecondLifeProtocol")
        networkingService.shutdown()
    }
}

sealed class LoginResult {
    data class Success(val agentId: UUID, val sessionId: String) : LoginResult()
    data class Failure(
        val message: String,
        val errorCode: String? = null,
        val technicalDetails: String? = null
    ) : LoginResult()
}

sealed class TeleportResult {
    data class Success(val regionName: String) : TeleportResult()
    data class Failure(val message: String) : TeleportResult()
}

enum class ChatType(val value: Int) {
    WHISPER(0),
    NORMAL(1),
    SHOUT(2)
}
