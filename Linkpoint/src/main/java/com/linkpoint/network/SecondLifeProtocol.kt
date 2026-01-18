package com.linkpoint.network

import android.content.Context
import android.os.Build
import android.util.Log
import com.linkpoint.auth.CrashTracker
import com.linkpoint.auth.DeviceIdentifier
import com.linkpoint.auth.MfaHashStorage
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
 * Enhanced with official viewer compliance:
 * - Persistent device identifiers (MAC, ID0)
 * - Crash tracking (last_exec_event)
 * - MFA hash storage
 * - Pre-hashed password support
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
    }
    
    // Core networking service with all connection management features
    private val networkingService = CoreNetworkingService(context)
    
    // Device identification (persistent across sessions) - matches official viewer behavior
    private val deviceIdentifier = DeviceIdentifier(context)
    
    // Crash tracking for last_exec_event parameter - matches official viewer behavior
    private val crashTracker = CrashTracker(context)
    
    // MFA hash storage for skipping MFA on trusted devices
    private val mfaHashStorage = MfaHashStorage(context)
    
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
     * Record that the app has started (for crash tracking).
     * Call this from Application.onCreate().
     */
    fun recordAppStart() {
        crashTracker.recordAppStart()
    }
    
    /**
     * Record a clean shutdown (for crash tracking).
     * Call this when the user logs out properly.
     */
    fun recordCleanShutdown() {
        crashTracker.recordCleanShutdown()
    }
    
    /**
     * Get stored MFA hash for a user (to skip MFA prompt).
     */
    fun getStoredMfaHash(username: String): String? {
        return mfaHashStorage.getMfaHash(username)
    }
    
    /**
     * Store MFA hash after successful MFA verification.
     */
    fun storeMfaHash(username: String, mfaHash: String) {
        mfaHashStorage.saveMfaHash(username, mfaHash)
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
        
        // Start initialization tracking
        com.linkpoint.utils.InitializationTracker.startSession()
        com.linkpoint.utils.InitializationTracker.startPhase(
            com.linkpoint.utils.InitializationTracker.Phase.LOGIN_STARTING,
            "Login for $firstName $lastName"
        )
        
        Log.d(TAG, "Attempting login for $firstName $lastName")
        NetworkLogger.logProtocol(
            "Second Life Login",
            "Grid: $loginUri, User: $firstName $lastName, Start: $startLocation"
        )
        
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
        
        // Build XMLRPC request with MFA support
        val xmlRequest = buildLoginXml(
            firstName = firstName,
            lastName = lastName,
            passwordHash = passwordHash,
            startLocation = startLocation,
            mfaToken = mfaToken,
            mfaHash = mfaHash
        )
        
        // Use CoreNetworkingService for login with comprehensive retry handling
        com.linkpoint.utils.InitializationTracker.startPhase(
            com.linkpoint.utils.InitializationTracker.Phase.LOGIN_HTTP_REQUEST,
            "Sending login request"
        )
        
        val result = networkingService.login(loginUri, xmlRequest)
        
        when (result) {
            is CoreNetworkingService.LoginResult.Success -> {
                com.linkpoint.utils.InitializationTracker.completePhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_HTTP_REQUEST,
                    "Login response received"
                )
                com.linkpoint.utils.InitializationTracker.startPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_SUCCESS,
                    "Processing login success"
                )
                
                val agentId = try { 
                    UUID.fromString(result.agentId) 
                } catch (e: Exception) { 
                    UUID.randomUUID() 
                }
                
                Log.i(TAG, "Login successful! Agent: ${result.agentId}")
                
                NetworkLogger.logAuth("Login Success", mapOf(
                    "agentId" to result.agentId,
                    "sessionId" to "***REDACTED***",
                    "simIp" to result.simIp,
                    "simPort" to result.simPort.toString()
                ))
                
                val regionInfo = RegionInfo(
                    name = result.regionName ?: "Unknown",
                    handle = 0,
                    x = 128,
                    y = 128,
                    simIP = result.simIp,
                    simPort = result.simPort,
                    seedCapability = result.seedCapability
                )
                
                com.linkpoint.utils.InitializationTracker.startPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.SESSION_SETUP,
                    "Setting up session"
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
                
                com.linkpoint.utils.InitializationTracker.completePhase(
                    com.linkpoint.utils.InitializationTracker.Phase.SESSION_SETUP,
                    "Session and managers initialized"
                )
                
                Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                Log.i(TAG, "║ POST-LOGIN INITIALIZATION SEQUENCE STARTING")
                Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                
                // Configure and connect UDP connection for simulator communication
                // This is critical for receiving object updates, chat, IMs, etc.
                val circuitCode = result.circuitCode ?: 0
                if (circuitCode != 0 && result.simIp.isNotEmpty() && result.simPort > 0) {
                    com.linkpoint.utils.InitializationTracker.startPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                        "Connecting to ${result.simIp}:${result.simPort}"
                    )
                    
                    Log.i(TAG, "[STEP 1/2] Establishing UDP connection to ${result.simIp}:${result.simPort} with circuit $circuitCode")
                    app.udpConnection.configure(result.simIp, result.simPort, circuitCode)
                    
                    // Set session info for circuit establishment
                    val sessionUUID = try {
                        UUID.fromString(result.sessionId)
                    } catch (e: Exception) {
                        Log.w(TAG, "Invalid session ID format, using random UUID")
                        UUID.randomUUID()
                    }
                    app.udpConnection.setSessionInfo(sessionUUID, agentId)
                    
                    app.applicationScope.launch {
                        try {
                            Log.d(TAG, "[STEP 1/2] UDP connect() starting...")
                            val udpConnected = app.udpConnection.connect()
                            if (udpConnected) {
                                com.linkpoint.utils.InitializationTracker.completePhase(
                                    com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                                    "UDP connected"
                                )
                                com.linkpoint.utils.InitializationTracker.startPhase(
                                    com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTED,
                                    "Waiting for simulator messages"
                                )
                                Log.i(TAG, "[STEP 1/2] ✓ UDP connection established - simulator packets active")
                                Log.i(TAG, "[STEP 1/2] Registered handlers: ${app.udpConnection.getRegisteredHandlerIds()}")
                            } else {
                                com.linkpoint.utils.InitializationTracker.failPhase(
                                    com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                                    "UDP connect() returned false"
                                )
                                Log.w(TAG, "[STEP 1/2] ✗ Failed to establish UDP connection - simulator features may not work")
                            }
                        } catch (e: Exception) {
                            com.linkpoint.utils.InitializationTracker.failPhase(
                                com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                                "Exception: ${e.message}"
                            )
                            Log.e(TAG, "[STEP 1/2] ✗ Error establishing UDP connection", e)
                        }
                    }
                } else {
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.UDP_CONNECTING,
                        "Missing circuit code or sim info"
                    )
                    Log.w(TAG, "[STEP 1/2] ✗ Missing circuit code or sim info - UDP connection not established")
                    Log.w(TAG, "  circuitCode=$circuitCode, simIp=${result.simIp}, simPort=${result.simPort}")
                }
                
                // Initialize capabilities from seed capability (for textures, meshes, etc.)
                // This is critical for rendering - like Lumiya's SLCaps.GetCapabilities()
                result.seedCapability?.let { seedCap ->
                    com.linkpoint.utils.InitializationTracker.startPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                        "Fetching capabilities from seed"
                    )
                    Log.i(TAG, "[STEP 2/2] Initializing capabilities from seed...")
                    Log.d(TAG, "[STEP 2/2] Seed URL: ${seedCap.take(80)}...")
                    Log.d(TAG, "[STEP 2/2] Using Lumiya translation layer with login URL: ${loginUri.take(60)}...")
                    app.applicationScope.launch {
                        try {
                            Log.d(TAG, "[STEP 2/2] capabilityManager.initialize() starting with Lumiya translation...")
                            // Use the overload that accepts loginUri for Lumiya-compatible URL repair
                            val capsInitialized = app.capabilityManager.initialize(seedCap, loginUri)
                            if (capsInitialized) {
                                com.linkpoint.utils.InitializationTracker.completePhase(
                                    com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                                    "${app.capabilityManager.getCapabilityCount()} capabilities loaded"
                                )
                                com.linkpoint.utils.InitializationTracker.startPhase(
                                    com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_READY,
                                    "Capabilities available for use"
                                )
                                Log.i(TAG, "[STEP 2/2] ✓ Capabilities initialized - textures and assets ready")
                                Log.i(TAG, "[STEP 2/2] Capabilities loaded: ${app.capabilityManager.getCapabilityCount()}")
                                // Connect texture manager to capability-based fetching
                                app.textureManager.onCapabilitiesReady()
                                Log.i(TAG, "╔══════════════════════════════════════════════════════════════════")
                                Log.i(TAG, "║ POST-LOGIN INITIALIZATION COMPLETE - WORLD SHOULD START LOADING")
                                Log.i(TAG, "╚══════════════════════════════════════════════════════════════════")
                            } else {
                                com.linkpoint.utils.InitializationTracker.failPhase(
                                    com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                                    "initialize() returned false - see CapabilityManager logs"
                                )
                                Log.w(TAG, "[STEP 2/2] ✗ Failed to initialize capabilities - textures may not load")
                                Log.w(TAG, "[STEP 2/2] Check CapabilityManager logs for detailed error information")
                            }
                        } catch (e: Exception) {
                            com.linkpoint.utils.InitializationTracker.failPhase(
                                com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                                "Exception: ${e.message}"
                            )
                            Log.e(TAG, "[STEP 2/2] ✗ Error initializing capabilities", e)
                        }
                    }
                } ?: run {
                    com.linkpoint.utils.InitializationTracker.failPhase(
                        com.linkpoint.utils.InitializationTracker.Phase.CAPABILITIES_FETCHING,
                        "No seed capability in login response"
                    )
                    Log.w(TAG, "[STEP 2/2] ✗ No seed capability in login response - textures may not load")
                    Log.w(TAG, "  seedCapability was null in login response")
                }
                
                com.linkpoint.utils.InitializationTracker.completePhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_SUCCESS,
                    "Login completed, waiting for world data"
                )
                
                NetworkLogger.logProtocol("Login Complete", "Successfully connected to ${result.simIp}:${result.simPort}")
                LoginResult.Success(agentId, result.sessionId, result.mfaHash)
            }
            is CoreNetworkingService.LoginResult.MFARequired -> {
                app.sessionManager.setConnectionState(ConnectionState.DISCONNECTED)
                Log.i(TAG, "MFA required for login: ${result.message}")
                
                LoginResult.MFARequired(
                    message = result.message,
                    agentId = result.agentId
                )
            }
            is CoreNetworkingService.LoginResult.Failure -> {
                com.linkpoint.utils.InitializationTracker.failPhase(
                    com.linkpoint.utils.InitializationTracker.Phase.LOGIN_HTTP_REQUEST,
                    "Login failed: ${result.message}"
                )
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
    
    private fun buildLoginXml(
        firstName: String,
        lastName: String,
        passwordHash: String,
        startLocation: String,
        mfaToken: String = "",
        mfaHash: String = ""
    ): String {
        val safeFirstName = escapeXml(firstName)
        val safeLastName = escapeXml(lastName)
        val safePassword = escapeXml(passwordHash)
        val safeStart = escapeXml(startLocation)
        val safeToken = escapeXml(mfaToken)
        val safeMfaHash = escapeXml(mfaHash)
        
        // Use persistent device identifiers (matches official viewer behavior)
        val viewerDigest = deviceIdentifier.getViewerDigest()
        val macAddress = deviceIdentifier.getMacAddress()
        val id0 = deviceIdentifier.getId0()
        
        // Get last execution status for crash reporting (matches official viewer behavior)
        val lastExecEvent = crashTracker.getLastExecStatus()
        
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
            
            // MFA fields - required by Second Life MFA login flow
            // See: https://wiki.secondlife.com/wiki/User:Brad_Linden/Login_MFA
            // - token: TOTP code from authenticator app (empty string if not responding to challenge)
            // - mfa_hash: Cached hash from previous successful MFA (allows skipping token entry)
            append("<member><name>token</name><value><string>$safeToken</string></value></member>")
            append("<member><name>mfa_hash</name><value><string>$safeMfaHash</string></value></member>")
            
            // Viewer identification
            append("<member><name>channel</name><value><string>$VIEWER_NAME</string></value></member>")
            append("<member><name>version</name><value><string>$VIEWER_NAME $VIEWER_VERSION</string></value></member>")
            append("<member><name>platform</name><value><string>Android</string></value></member>")
            append("<member><name>platform_version</name><value><string>${android.os.Build.VERSION.RELEASE}</string></value></member>")
            
            // Device identification (persistent, hashed - matches official viewer behavior)
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            append("<member><name>viewer_digest</name><value><string>$viewerDigest</string></value></member>")
            
            // Agreements and status
            append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
            append("<member><name>read_critical</name><value><string>true</string></value></member>")
            
            // Last execution event - tracks previous app exit status for crash reporting
            // Required by official protocol, all desktop viewers send this
            append("<member><name>last_exec_event</name><value><i4>$lastExecEvent</i4></value></member>")
            
            // Options array - comprehensive list matching official viewers
            append("<member><name>options</name><value><array><data>")
            // Core inventory options
            append("<value><string>inventory-root</string></value>")
            append("<value><string>inventory-skeleton</string></value>")
            append("<value><string>inventory-lib-root</string></value>")
            append("<value><string>inventory-lib-owner</string></value>")
            append("<value><string>inventory-skel-lib</string></value>")
            // Avatar and UI options
            append("<value><string>initial-outfit</string></value>")
            append("<value><string>gestures</string></value>")
            append("<value><string>display_names</string></value>")
            append("<value><string>adult_compliant</string></value>")
            append("<value><string>buddy-list</string></value>")
            append("<value><string>newuser-config</string></value>")
            append("<value><string>ui-config</string></value>")
            append("<value><string>advanced-mode</string></value>")
            // Events and classifieds
            append("<value><string>event_categories</string></value>")
            append("<value><string>event_notifications</string></value>")
            append("<value><string>classified_categories</string></value>")
            // Server configuration
            append("<value><string>max-agent-groups</string></value>")
            append("<value><string>map-server-url</string></value>")
            append("<value><string>voice-config</string></value>")
            append("<value><string>tutorial_settings</string></value>")  // Fixed: was tutorial_setting (singular)
            append("<value><string>login-flags</string></value>")
            append("<value><string>global-textures</string></value>")
            // OpenSim compatibility options
            append("<value><string>avatar_picker_url</string></value>")
            append("<value><string>classified_fee</string></value>")
            append("<value><string>currency</string></value>")
            append("<value><string>destination_guide_url</string></value>")
            append("<value><string>profile-server-url</string></value>")
            append("<value><string>search</string></value>")
            append("</data></array></value></member>")
            
            append("</struct></value>")
            append("</param>")
            append("</params>")
            append("</methodCall>")
        }
    }
    
    // Note: generateMacAddress() removed - now using DeviceIdentifier for persistent IDs
    
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
     * before MD5 hashing. This matches the official viewer implementation and is required
     * for compatibility with Second Life login servers.
     * 
     * Supports already-hashed passwords: If the input is already in $1$<md5> format
     * (35 characters starting with $1$), it is returned unchanged. This matches
     * LibreMetaverse, Firestorm, and other official viewer behavior.
     * 
     * @param password The plain text password (will be trimmed and truncated to 16 chars)
     *                 OR an already-hashed password in $1$<md5> format
     * @return Password hash in format "$1$<md5_hash>"
     */
    fun createPasswordHash(password: String): String {
        // Support already-hashed passwords (35 chars: "$1$" + 32 hex)
        // This matches LibreMetaverse/Firestorm behavior
        if (password.length == 35 && password.startsWith("\$1\$")) {
            return password
        }
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
