package com.linkpoint.network.core

import android.content.Context
import com.linkpoint.auth.CrashTracker
import com.linkpoint.auth.DeviceIdentifier
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.auth.AuthParams
import com.linkpoint.protocol.auth.AuthReply
import com.linkpoint.protocol.caps.CapEventQueue
import com.linkpoint.protocol.capabilities.CapabilityManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

/**
 * Grid Connection
 * 
 * Represents a connection to a Second Life grid.
 * Based on the reference viewer's SLGridConnection implementation with mobile-first optimizations.
 * 
 * Features:
 * - Connection state management
 * - Authentication handling
 * - Circuit management (agent and temp circuits)
 * - Event-driven architecture
 * - Mobile-optimized resource management
 * 
 * Mobile-First Considerations:
 * - Efficient state management with StateFlow
 * - Automatic cleanup on disconnection
 * - Battery-aware connection handling
 * - Memory-efficient circuit management
 */
class GridConnection(
    private val context: Context,
    private val connectionId: UUID = UUID.randomUUID(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    companion object {
        private const val TAG = "GridConnection"
        private const val DEFAULT_SYSTEM_ACCOUNT = "Second Life"
        private const val MAX_RECONNECT_ATTEMPTS = 5

        // Viewer identification
        private const val VIEWER_NAME = "Linkpoint"
        private const val VIEWER_VERSION = "1.0.0"
    }
    
    /**
     * Connection states based on the reference viewer's ConnectionState enum
     */
    enum class ConnectionState {
        IDLE,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        ERROR,
        MFA_REQUIRED
    }
    
    /**
     * Connection state flow for reactive updates
     */
    private val _connectionState = MutableStateFlow(ConnectionState.IDLE)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    /**
     * Message explaining why MFA is required
     */
    var mfaMessage: String? = null
        private set
    
    /**
     * Authentication parameters
     */
    var authParams: AuthParams? = null
        private set
    
    /**
     * Authentication reply from grid
     */
    var authReply: AuthReply? = null
        private set
    
    /**
     * Agent circuit for primary communication
     */
    var agentCircuit: AgentCircuit? = null
        private set
    
    /**
     * Temporary circuits for temporary operations
     */
    private val tempCircuits: MutableMap<AuthReply, TempCircuit> = mutableMapOf()
    
    /**
     * Capability manager
     */
    var capabilityManager: CapabilityManager? = null
        private set

    /**
     * Capability event queue (Legacy/Wrapper support)
     */
    var capEventQueue: CapEventQueue? = null
        private set
    
    /**
     * Active agent UUID
     */
    var activeAgentUUID: UUID? = null
        private set
    
    /**
     * Reconnection state
     */
    private var isReconnecting: Boolean = false
    private var reconnectAttempts: Int = 0
    private var firstConnect: Boolean = true
    private var hadConnected: Boolean = false
    
    /**
     * User preference for connection
     */
    private var userWantsConnected: Boolean = false
    
    // Core services
    private val networkingService = CoreNetworkingService(context)
    private val deviceIdentifier = DeviceIdentifier(context)
    private val crashTracker = CrashTracker(context)
    
    // ==================== CONNECTION MANAGEMENT ====================
    
    /**
     * Connect to the grid
     * 
     * @param authParams Authentication parameters
     */
    suspend fun connect(authParams: AuthParams) {
        this.authParams = authParams
        userWantsConnected = true
        
        when (connectionState.value) {
            ConnectionState.IDLE -> performConnection()
            ConnectionState.CONNECTING -> {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Already connecting, ignoring duplicate request")
            }
            ConnectionState.CONNECTED -> {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Already connected, ignoring duplicate request")
            }
            else -> {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Cannot connect from state: ${connectionState.value}")
            }
        }
    }
    
    /**
     * Submit MFA code to proceed with login
     */
    suspend fun submitMfaCode(code: String) {
        if (connectionState.value != ConnectionState.MFA_REQUIRED) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Submit MFA code called when not in MFA_REQUIRED state")
            return
        }
        performConnection(mfaToken = code)
    }

    /**
     * Perform the actual connection
     * 
     * Handles:
     * 1. HTTP login (if authReply not already set)
     * 2. UDP circuit establishment
     * 3. Capability initialization
     */
    private suspend fun performConnection(mfaToken: String = "") {
        _connectionState.value = ConnectionState.CONNECTING
        mfaMessage = null
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Starting connection process")
        
        try {
            val params = authParams
            
            if (params == null) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "No auth params provided")
                _connectionState.value = ConnectionState.ERROR
                return
            }
            
            // Step 1: Authentication (if needed)
            if (authReply == null) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Performing HTTP login to ${params.gridUrl}")
                
                // Build login XML
                val passwordHash = createPasswordHash(params.password)
                val loginXml = buildLoginXml(
                    firstName = params.firstName,
                    lastName = params.lastName,
                    passwordHash = passwordHash,
                    startLocation = params.startLocation,
                    mfaToken = mfaToken
                )

                // Execute login
                val loginResult = networkingService.login(params.gridUrl, loginXml)

                when (loginResult) {
                    is CoreNetworkingService.LoginResult.Success -> {
                        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "Login successful: ${loginResult.agentId}")

                        // Populate AuthReply
                        authReply = AuthReply(
                            sessionId = UUID.fromString(loginResult.sessionId),
                            agentId = UUID.fromString(loginResult.agentId),
                            circuitCode = loginResult.circuitCode ?: 0,
                            simIP = loginResult.simIp,
                            simPort = loginResult.simPort,
                            seedCapability = loginResult.seedCapability ?: "",
                            mfaHash = loginResult.mfaHash
                        )
                    }
                    is CoreNetworkingService.LoginResult.MFARequired -> {
                        NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "MFA Required: ${loginResult.message}")
                        mfaMessage = loginResult.message
                        _connectionState.value = ConnectionState.MFA_REQUIRED
                        return
                    }
                    is CoreNetworkingService.LoginResult.Failure -> {
                        NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Login failed: ${loginResult.message}")
                        _connectionState.value = ConnectionState.ERROR
                        return
                    }
                }
            }
            
            // Validate auth reply was created/set successfully
            val reply = authReply ?: throw IllegalStateException("Auth reply was not created")
            
            activeAgentUUID = reply.agentId

            // Step 2: Establish UDP Circuit
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "Establishing agent circuit to ${reply.simIP}:${reply.simPort} (Circuit: ${reply.circuitCode})")

            agentCircuit = AgentCircuit(reply, scope = scope)

            // Wait for circuit to be ready (optional, but good for robust startup)
            // AgentCircuit initiates connection in its init block

            // Step 3: Initialize Capabilities
            if (reply.seedCapability.isNotBlank()) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Initializing capabilities...")
                val capManager = CapabilityManager()
                capabilityManager = capManager

                // Initialize capabilities asynchronously, passing loginUrl to enable
                // LinkpointTranslationLayer URL repair (critical for Agni grid)
                val loginUrl = params.gridUrl
                scope.launch {
                    val capsReady = capManager.initialize(reply.seedCapability, loginUrl)
                    if (capsReady) {
                        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "Capabilities initialized")
                    } else {
                        NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Capability initialization failed, retrying...")
                        // Retry once after a delay - capabilities are critical for textures/meshes
                        delay(5000)
                        val retryReady = capManager.initialize(reply.seedCapability, loginUrl)
                        if (retryReady) {
                            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP, "Capabilities initialized on retry")
                        } else {
                            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Capability initialization failed after retry")
                        }
                    }
                }
            } else {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "No seed capability provided, skipping caps init")
            }
            
            _connectionState.value = ConnectionState.CONNECTED
            hadConnected = true
            firstConnect = false
            reconnectAttempts = 0
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, 
                "Connection established. SimIP: ${reply.simIP}, SimPort: ${reply.simPort}")
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Connection failed: ${e.message}")
            e.printStackTrace()
            _connectionState.value = ConnectionState.ERROR
            
            // Attempt reconnection if appropriate
            if (shouldReconnect()) {
                attemptReconnection()
            }
        }
    }
    
    /**
     * Set the authentication reply from an external source (e.g., SecondLifeProtocol.login()).
     * Call this before connect() when you have pre-authenticated data.
     */
    fun setAuthReply(reply: AuthReply) {
        this.authReply = reply
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, 
            "Auth reply set: simIP=${reply.simIP}, simPort=${reply.simPort}")
    }
    
    /**
     * Disconnect from the grid
     */
    suspend fun disconnect() {
        userWantsConnected = false
        
        if (connectionState.value == ConnectionState.CONNECTED || connectionState.value == ConnectionState.CONNECTING) {
            _connectionState.value = ConnectionState.DISCONNECTING
            
            try {
                // Clean up agent circuit
                agentCircuit?.close()
                agentCircuit = null
                
                // Clean up temp circuits
                tempCircuits.values.forEach { it.close() }
                tempCircuits.clear()
                
                // Clean up capabilities
                capabilityManager?.shutdown()
                capabilityManager = null

                capEventQueue?.close()
                capEventQueue = null
                
                // Clean up networking service
                networkingService.shutdown()

                _connectionState.value = ConnectionState.IDLE
                
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Disconnected successfully")
                
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Error during disconnect: ${e.message}")
                _connectionState.value = ConnectionState.ERROR
            }
        }
    }
    
    /**
     * Check if should attempt reconnection
     */
    private fun shouldReconnect(): Boolean {
        return userWantsConnected && 
               reconnectAttempts < MAX_RECONNECT_ATTEMPTS && 
               !isReconnecting
    }
    
    /**
     * Attempt reconnection
     */
    private suspend fun attemptReconnection() {
        isReconnecting = true
        reconnectAttempts++
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Attempting reconnection ($reconnectAttempts/$MAX_RECONNECT_ATTEMPTS)")
        
        // Exponential backoff: 1s, 2s, 4s, 8s, 16s
        val backoffDelay = (1L shl reconnectAttempts) * 1000L
        delay(backoffDelay)
        
        performConnection()
        
        isReconnecting = false
    }
    
    // ==================== CIRCUIT MANAGEMENT ====================
    
    /**
     * Create a temporary circuit
     * 
     * @param authReply Authentication reply for the temp circuit
     * @return The created temp circuit
     */
    fun createTempCircuit(authReply: AuthReply): TempCircuit {
        val tempCircuit = TempCircuit(authReply)
        tempCircuits[authReply] = tempCircuit
        return tempCircuit
    }
    
    /**
     * Remove a temporary circuit
     * 
     * @param authReply The auth reply associated with the temp circuit
     */
    fun removeTempCircuit(authReply: AuthReply) {
        tempCircuits.remove(authReply)?.close()
    }
    
    /**
     * Get connection statistics
     * 
     * @return Map containing connection statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "connectionId" to connectionId.toString(),
            "state" to connectionState.value.name,
            "agentId" to (activeAgentUUID?.toString() ?: "none"),
            "hasAgentCircuit" to (agentCircuit != null),
            "tempCircuitCount" to tempCircuits.size,
            "reconnectAttempts" to reconnectAttempts,
            "firstConnect" to firstConnect,
            "hadConnected" to hadConnected,
            "userWantsConnected" to userWantsConnected
        )
    }
    
    /**
     * Clean up resources
     */
    fun close() {
        scope.cancel()
        runBlocking {
            disconnect()
        }
    }

    // ==================== HELPER METHODS ====================

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

        // Get last execution status for crash reporting
        val lastExecEvent = crashTracker.getLastExecStatus()

        // Build XML-RPC request
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

            // MFA fields
            append("<member><name>token</name><value><string>$safeToken</string></value></member>")
            append("<member><name>mfa_hash</name><value><string>$safeMfaHash</string></value></member>")

            // Viewer identification
            append("<member><name>channel</name><value><string>$VIEWER_NAME</string></value></member>")
            append("<member><name>version</name><value><string>$VIEWER_NAME $VIEWER_VERSION</string></value></member>")
            append("<member><name>platform</name><value><string>Android</string></value></member>")
            append("<member><name>platform_version</name><value><string>${android.os.Build.VERSION.RELEASE}</string></value></member>")

            // Device identification
            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")
            append("<member><name>viewer_digest</name><value><string>$viewerDigest</string></value></member>")

            // Agreements and status
            append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
            append("<member><name>read_critical</name><value><string>true</string></value></member>")
            append("<member><name>last_exec_event</name><value><i4>$lastExecEvent</i4></value></member>")

            // Options array
            append("<member><name>options</name><value><array><data>")
            append("<value><string>inventory-root</string></value>")
            append("<value><string>inventory-skeleton</string></value>")
            append("<value><string>inventory-lib-root</string></value>")
            append("<value><string>inventory-lib-owner</string></value>")
            append("<value><string>inventory-skel-lib</string></value>")
            append("<value><string>initial-outfit</string></value>")
            append("<value><string>gestures</string></value>")
            append("<value><string>display_names</string></value>")
            append("<value><string>adult_compliant</string></value>")
            append("<value><string>buddy-list</string></value>")
            append("<value><string>newuser-config</string></value>")
            append("<value><string>ui-config</string></value>")
            append("<value><string>advanced-mode</string></value>")
            append("<value><string>event_categories</string></value>")
            append("<value><string>event_notifications</string></value>")
            append("<value><string>classified_categories</string></value>")
            append("<value><string>max-agent-groups</string></value>")
            append("<value><string>map-server-url</string></value>")
            append("<value><string>voice-config</string></value>")
            append("<value><string>tutorial_settings</string></value>")
            append("<value><string>login-flags</string></value>")
            append("<value><string>global-textures</string></value>")
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
     * Create Second Life password hash.
     */
    private fun createPasswordHash(password: String): String {
        if (password.length == 35 && password.startsWith("\$1\$")) {
            return password
        }
        val truncatedPassword = password.trim().take(16)
        return "\$1\$${md5Hash(truncatedPassword)}"
    }
}
