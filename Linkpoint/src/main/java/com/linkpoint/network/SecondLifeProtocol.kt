package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.core.ConnectionState
import com.linkpoint.core.RegionInfo
import com.linkpoint.LinkpointApp
import com.linkpoint.network.core.CoreNetworkingService
import com.linkpoint.network.core.NetworkStateManager
import com.linkpoint.network.NetworkLogger
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
        // NOTE: Currently identifying as "Lumiya" because Linkpoint is based on Lumiya
        // and "Linkpoint" is not yet registered with Linden Lab's Third-Party Viewer Directory.
        // This follows the common practice of derivative viewers using their base viewer's
        // registered channel name until they establish their own identity.
        // 
        // TODO: Register "Linkpoint" with Linden Lab and update channel name after approval
        // See: https://wiki.secondlife.com/wiki/Third_Party_Viewer_Directory
        private const val VIEWER_NAME = "Lumiya"
        private const val VIEWER_VERSION = "1.0.0"
        
        /**
         * Use simple Lumiya-style login instead of complex CoreNetworkingService
         * Set to true for fast, instant login like Lumiya
         * Set to false for full retry logic and diagnostics (slower, more complex)
         */
        private const val USE_SIMPLE_LOGIN = true
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
     * 
     * @param firstName User's first name
     * @param lastName User's last name
     * @param password User's password
     * @param loginUri Grid login URI
     * @param startLocation Start location ("last", "home", or specific)
     * @param mfaToken TOTP code from authenticator app (required after MFARequired result)
     * @param mfaHash Cached MFA hash from previous successful login (allows skipping MFA)
     * @return LoginResult (Success, MFARequired, or Failure)
     */
    suspend fun login(
        firstName: String,
        lastName: String,
        password: String,
        loginUri: String,
        startLocation: String = "last",
        mfaToken: String = "",
        mfaHash: String = ""
    ): LoginResult = withContext(Dispatchers.IO) {
        val app = LinkpointApp.getInstance()
        app.sessionManager.setConnectionState(ConnectionState.CONNECTING)
        
        Log.d(TAG, "Attempting login for $firstName $lastName (simple=${USE_SIMPLE_LOGIN})")
        NetworkLogger.logProtocol(
            "Second Life Login",
            "Grid: $loginUri, User: $firstName $lastName, Start: $startLocation, Mode: ${if (USE_SIMPLE_LOGIN) "SIMPLE" else "COMPLEX"}"
        )
        
        if (USE_SIMPLE_LOGIN) {
            // Use simple Lumiya-style login for instant connection
            Log.d(TAG, "Using SIMPLE login (Lumiya-style)")
            val simpleResult = SimpleSLLogin.login(
                firstName = firstName, 
                lastName = lastName, 
                password = password, 
                loginUri = loginUri, 
                startLocation = startLocation,
                mfaToken = mfaToken,
                mfaHash = mfaHash
            )
            
            when (simpleResult) {
                is SimpleSLLogin.SimpleLoginResult.Success -> {
                    val agentId = try {
                        UUID.fromString(simpleResult.agentId)
                    } catch (e: Exception) {
                        UUID.randomUUID()
                    }
                    
                    Log.i(TAG, "SIMPLE login successful! Agent: ${simpleResult.agentId}")
                    
                    val regionInfo = RegionInfo(
                        name = simpleResult.regionName ?: "Unknown",
                        handle = 0,
                        x = 128,
                        y = 128,
                        simIP = simpleResult.simIp,
                        simPort = simpleResult.simPort,
                        seedCapability = simpleResult.seedCapability
                    )
                    
                    app.sessionManager.onLoginSuccess(
                        sessionId = simpleResult.sessionId,
                        agentId = agentId,
                        secureSessionId = "",
                        firstName = firstName,
                        lastName = lastName,
                        regionInfo = regionInfo
                    )
                    
                    // Initialize agent-specific managers (sets app.agentId)
                    app.initializeAgentManagers(agentId)
                    
                    // Initialize capabilities from seed capability (for textures, meshes, etc.)
                    // This is critical for rendering - like Lumiya's SLCaps.GetCapabilities()
                    simpleResult.seedCapability?.let { seedCap ->
                        Log.i(TAG, "Initializing capabilities from seed...")
                        app.applicationScope.launch {
                            try {
                                val capsInitialized = app.capabilityManager.initialize(seedCap)
                                if (capsInitialized) {
                                    Log.i(TAG, "Capabilities initialized - textures and assets ready")
                                    // Connect texture manager to capability-based fetching
                                    app.textureManager.onCapabilitiesReady()
                                } else {
                                    Log.w(TAG, "Failed to initialize capabilities - textures may not load")
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error initializing capabilities", e)
                            }
                        }
                    } ?: Log.w(TAG, "No seed capability in login response - textures may not load")
                    
                    return@withContext LoginResult.Success(
                        agentId = agentId, 
                        sessionId = simpleResult.sessionId,
                        mfaHash = simpleResult.mfaHash
                    )
                }
                is SimpleSLLogin.SimpleLoginResult.MFARequired -> {
                    app.sessionManager.setConnectionState(ConnectionState.DISCONNECTED)
                    Log.i(TAG, "MFA required for login: ${simpleResult.message}")
                    
                    return@withContext LoginResult.MFARequired(
                        message = simpleResult.message,
                        agentId = simpleResult.agentId
                    )
                }
                is SimpleSLLogin.SimpleLoginResult.Failure -> {
                    app.sessionManager.setConnectionState(ConnectionState.ERROR)
                    Log.w(TAG, "SIMPLE login failed: ${simpleResult.message}")
                    
                    return@withContext LoginResult.Failure(
                        message = simpleResult.message,
                        errorCode = simpleResult.errorCode,
                        technicalDetails = simpleResult.details,
                        category = simpleResult.category,
                        rootCauseType = simpleResult.rootCauseType,
                        rootCauseMessage = simpleResult.rootCauseMessage,
                        exceptionChain = simpleResult.exceptionChain,
                        recommendations = simpleResult.recommendations,
                        isTransient = simpleResult.isTransient,
                        elapsedTimeMs = simpleResult.elapsedTimeMs,
                        attemptsMade = simpleResult.attemptsMade
                    )
                }
            }
        } else {
            // Use complex CoreNetworkingService login with full retry logic
            Log.d(TAG, "Using COMPLEX login (CoreNetworkingService)")
            
            // Log network diagnostics before login
            networkingService.logNetworkDiagnostics()
            
            // Create password hash - IMPORTANT: Must truncate to 16 chars like Lumiya does
            // This is a Second Life protocol requirement
            val truncatedPassword = password.trim().take(16)
            val passwordHash = createPasswordHash(password)
            
            Log.d(TAG, "Login details - URI: $loginUri, firstName: $firstName, lastName: $lastName, " +
                "passwordLen: ${password.length}, truncatedLen: ${truncatedPassword.length}, startLoc: $startLocation")
            
            // Log detailed authentication parameters (without sensitive data)
            NetworkLogger.logAuth("Password Hash Generation", mapOf(
                "originalLength" to password.length.toString(),
                "truncatedLength" to truncatedPassword.length.toString(),
                "hashFormat" to "\$1\$MD5"
            ))
            
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
                    
                    NetworkLogger.logAuth("Login Success", mapOf(
                        "agentId" to result.agentId,
                        "sessionId" to "***REDACTED***",
                        "simIp" to result.simIp,
                        "simPort" to result.simPort.toString()
                    ))
                    
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
                    
                    // Initialize agent-specific managers (sets app.agentId)
                    app.initializeAgentManagers(agentId)
                    
                    Log.i(TAG, "Login successful!")
                    NetworkLogger.logProtocol("Login Complete", "Successfully connected to ${result.simIp}:${result.simPort}")
                    LoginResult.Success(agentId, result.sessionId)
                }
                is CoreNetworkingService.LoginResult.Failure -> {
                    app.sessionManager.setConnectionState(ConnectionState.ERROR)
                    Log.w(TAG, "Login failed: ${result.message} [${result.errorCode}]")
                    NetworkLogger.log(
                        NetworkLogger.Level.ERROR,
                        NetworkLogger.Category.AUTHENTICATION,
                        "Login failed: ${result.message} [${result.errorCode}]"
                    )
                    LoginResult.Failure(
                        message = result.message,
                        errorCode = result.errorCode,
                        technicalDetails = result.technicalDetails
                    )
                }
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
            append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
            append("<member><name>read_critical</name><value><string>true</string></value></member>")
            
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
    
    /**
     * Create MD5 hash of input string
     */
    private fun md5Hash(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(input.toByteArray())
        return digest.joinToString("") { "%02x".format(it) }
    }
    
    /**
     * Create Second Life password hash.
     * 
     * IMPORTANT: Second Life protocol requires passwords to be truncated to 16 characters
     * before MD5 hashing. This matches the official Lumiya implementation and is required
     * for compatibility with Second Life login servers.
     * 
     * @param password The plain text password (will be trimmed and truncated to 16 chars)
     * @return Password hash in format "$1$<md5_hash>"
     */
    fun createPasswordHash(password: String): String {
        val truncatedPassword = password.trim().take(16)
        return "\$1\$${md5Hash(truncatedPassword)}"
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
    data class Success(
        val agentId: UUID, 
        val sessionId: String,
        /** MFA hash returned by server for future logins (to skip MFA prompt) */
        val mfaHash: String? = null
    ) : LoginResult()
    
    /**
     * Multi-Factor Authentication is required.
     * The user must provide a TOTP code from their authenticator app.
     * Call login() again with the mfaToken parameter after obtaining the code.
     */
    data class MFARequired(
        val message: String,
        val agentId: String? = null
    ) : LoginResult()
    
    data class Failure(
        val message: String,
        val errorCode: String? = null,
        val technicalDetails: String? = null,
        /** Error category for classification */
        val category: NetworkExceptionUtils.ErrorCategory = NetworkExceptionUtils.ErrorCategory.UNKNOWN,
        /** Root cause exception type name */
        val rootCauseType: String? = null,
        /** Root cause message */
        val rootCauseMessage: String? = null,
        /** Full exception chain for debugging */
        val exceptionChain: String? = null,
        /** Recommended actions for the user */
        val recommendations: List<String> = emptyList(),
        /** Whether this error is likely transient */
        val isTransient: Boolean = false,
        /** Time elapsed during the failed request(s) */
        val elapsedTimeMs: Long = 0,
        /** Number of attempts made */
        val attemptsMade: Int = 1
    ) : LoginResult() {
        /**
         * Get a comprehensive error report for debugging.
         */
        fun getFullReport(): String = buildString {
            appendLine("=== Login Error Report ===")
            appendLine()
            appendLine("Error Code: ${errorCode ?: "UNKNOWN"}")
            appendLine("Category: $category")
            appendLine("Message: $message")
            appendLine()
            appendLine("Attempts Made: $attemptsMade")
            appendLine("Total Time: ${elapsedTimeMs}ms")
            appendLine("Is Transient: $isTransient")
            appendLine()
            if (rootCauseType != null) {
                appendLine("=== Root Cause ===")
                appendLine("Type: $rootCauseType")
                appendLine("Message: ${rootCauseMessage ?: "(none)"}")
                appendLine()
            }
            if (!exceptionChain.isNullOrBlank()) {
                appendLine("=== Exception Chain ===")
                appendLine(exceptionChain)
                appendLine()
            }
            if (!technicalDetails.isNullOrBlank()) {
                appendLine("=== Technical Details ===")
                appendLine(technicalDetails)
                appendLine()
            }
            if (recommendations.isNotEmpty()) {
                appendLine("=== Recommended Actions ===")
                recommendations.forEachIndexed { idx, rec ->
                    appendLine("${idx + 1}. $rec")
                }
            }
        }
    }
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
