package com.linkpoint.network.core

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.auth.AuthParams
import com.linkpoint.protocol.auth.AuthReply
import com.linkpoint.protocol.caps.CapEventQueue
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.security.MessageDigest
import java.util.UUID

/**
 * Grid Connection
 * 
 * Represents a connection to a Second Life grid.
 * Based on Lumiya's SLGridConnection implementation with mobile-first optimizations.
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
    private val connectionId: UUID = UUID.randomUUID(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    companion object {
        private const val TAG = "GridConnection"
        private const val DEFAULT_SYSTEM_ACCOUNT = "Second Life"
        private const val MAX_RECONNECT_ATTEMPTS = 5
    }
    
    /**
     * Connection states based on Lumiya's ConnectionState enum
     */
    enum class ConnectionState {
        IDLE,
        CONNECTING,
        CONNECTED,
        DISCONNECTING,
        ERROR
    }
    
    /**
     * Connection state flow for reactive updates
     */
    private val _connectionState = MutableStateFlow(ConnectionState.IDLE)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()
    
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
     * Capability event queue
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
    
    /**
     * Login thread
     */
    private var loginThread: Thread? = null

    /**
     * HTTP client for login requests
     */
    private val httpClient by lazy { OkHttpClient() }
    
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
     * Perform the actual connection
     * 
     * Note: In the current architecture, actual login/authentication is handled by
     * SecondLifeProtocol.login() which uses CoreNetworkingService. This GridConnection
     * class is used for internal connection state management and testing scenarios.
     * 
     * For real connection:
     * 1. HTTP login via SecondLifeProtocol.login() returns sim IP/port
     * 2. UDP connection established via UDPConnectionFixed
     * 3. Capabilities initialized via CapabilityManager
     * 
     * This method currently supports two modes:
     * - Testing: Creates simulated connection with placeholder data
     * - Real: Receives pre-authenticated data when connected through SecondLifeProtocol
     */
    private suspend fun performConnection() {
        _connectionState.value = ConnectionState.CONNECTING
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Starting connection process")
        
        try {
            // If authParams contains pre-authenticated data (from SecondLifeProtocol.login()),
            // use that information. Otherwise, this is a test/development scenario.
            val params = authParams
            
            if (params == null) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "No auth params provided")
                _connectionState.value = ConnectionState.ERROR
                return
            }
            
            // In a full implementation, this would:
            // 1. Make HTTP login request to params.gridUrl
            // 2. Parse XMLRPC response to get simIP, simPort, circuitCode, etc.
            // 3. The result would come from the server response
            //
            // Currently, the actual HTTP login is handled by SecondLifeProtocol.login()
            // which provides the sim IP/port. This class can receive that info via
            // the setAuthReply() method or by extending the connect() signature.
            
            if (authReply == null) {
                // Try to perform actual login if authReply is not set but we have params
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Performing direct login request...")
                val reply = performLogin(params)
                
                if (reply != null) {
                    authReply = reply
                    NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                        "Login successful: ${reply.simIP}:${reply.simPort}")
                } else {
                    NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                        "Login failed or not implemented fully - operating in stub mode.")

                    authReply = AuthReply(
                        sessionId = UUID.randomUUID(),
                        agentId = UUID.randomUUID(),
                        circuitCode = 123456789,
                        simIP = "0.0.0.0",
                        simPort = 0
                    )
                }
            }
            
            // Validate auth reply was created successfully
            val reply = authReply ?: throw IllegalStateException("Auth reply was not created")
            
            activeAgentUUID = reply.agentId
            agentCircuit = AgentCircuit(reply)
            
            _connectionState.value = ConnectionState.CONNECTED
            hadConnected = true
            firstConnect = false
            reconnectAttempts = 0
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, 
                "Connection state updated. SimIP: ${reply.simIP}, SimPort: ${reply.simPort}")
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Connection failed: ${e.message}")
            _connectionState.value = ConnectionState.ERROR
            
            // Attempt reconnection if appropriate
            if (shouldReconnect()) {
                attemptReconnection()
            }
        }
    }
    
    /**
     * Perform HTTP login request and parse response
     */
    private suspend fun performLogin(params: AuthParams): AuthReply? = withContext(Dispatchers.IO) {
        try {
            val requestBody = buildLoginXml(params).toRequestBody("text/xml".toMediaType())

            val request = Request.Builder()
                .url(params.gridUrl)
                .post(requestBody)
                .header("Content-Type", "text/xml")
                .header("Accept", "text/xml, application/xml")
                .header("User-Agent", "Linkpoint/1.0.0 (Android)")
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            response.close()

            if (!response.isSuccessful) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                    "Login request failed: ${response.code}")
                return@withContext null
            }

            // Parse response
            val sessionIdStr = extractXmlValue(responseBody, "session_id")
            val agentIdStr = extractXmlValue(responseBody, "agent_id")
            val circuitCodeStr = extractXmlValue(responseBody, "circuit_code")
            val simIpStr = extractXmlValue(responseBody, "sim_ip")
            val simPortStr = extractXmlValue(responseBody, "sim_port")

            if (sessionIdStr != null && agentIdStr != null && simIpStr != null && simPortStr != null) {
                return@withContext AuthReply(
                    sessionId = try { UUID.fromString(sessionIdStr) } catch (e: Exception) { UUID.randomUUID() },
                    agentId = try { UUID.fromString(agentIdStr) } catch (e: Exception) { UUID.randomUUID() },
                    circuitCode = circuitCodeStr?.toIntOrNull() ?: 0,
                    simIP = simIpStr,
                    simPort = simPortStr.toIntOrNull() ?: 0
                )
            }

            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Login response missing required fields")
            return@withContext null

        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Login exception: ${e.message}")
            return@withContext null
        }
    }

    private fun extractXmlValue(xml: String, name: String): String? {
        // Try string type
        val stringPattern = "<name>$name</name>\\s*<value>\\s*<string>([^<]*)</string>".toRegex()
        stringPattern.find(xml)?.groupValues?.get(1)?.let { return it }

        // Try uuid type
        val uuidPattern = "<name>$name</name>\\s*<value>\\s*<uuid>([^<]*)</uuid>".toRegex()
        uuidPattern.find(xml)?.groupValues?.get(1)?.let { return it }

        // Try integer types
        val i4Pattern = "<name>$name</name>\\s*<value>\\s*<i4>([^<]*)</i4>".toRegex()
        i4Pattern.find(xml)?.groupValues?.get(1)?.let { return it }

        val intPattern = "<name>$name</name>\\s*<value>\\s*<int>([^<]*)</int>".toRegex()
        intPattern.find(xml)?.groupValues?.get(1)?.let { return it }

        return null
    }

    private fun buildLoginXml(params: AuthParams): String {
        val safeFirstName = params.firstName
        val safeLastName = params.lastName
        val safePassword = createPasswordHash(params.password)
        val safeStart = params.startLocation

        // Basic Viewer ID info
        val viewerDigest = "" // Not calculating in this simplified version
        val macAddress = params.mac ?: ""
        val id0 = params.id0 ?: ""

        return buildString {
            append("<?xml version=\"1.0\"?>")
            append("<methodCall>")
            append("<methodName>login_to_simulator</methodName>")
            append("<params>")
            append("<param>")
            append("<value><struct>")

            append("<member><name>first</name><value><string>$safeFirstName</string></value></member>")
            append("<member><name>last</name><value><string>$safeLastName</string></value></member>")
            append("<member><name>passwd</name><value><string>$safePassword</string></value></member>")
            append("<member><name>start</name><value><string>$safeStart</string></value></member>")

            append("<member><name>channel</name><value><string>${params.viewerChannel}</string></value></member>")
            append("<member><name>version</name><value><string>${params.viewerVersion}</string></value></member>")
            append("<member><name>platform</name><value><string>Android</string></value></member>")

            append("<member><name>mac</name><value><string>$macAddress</string></value></member>")
            append("<member><name>id0</name><value><string>$id0</string></value></member>")

            append("<member><name>agree_to_tos</name><value><string>true</string></value></member>")
            append("<member><name>read_critical</name><value><string>true</string></value></member>")

            append("</struct></value>")
            append("</param>")
            append("</params>")
            append("</methodCall>")
        }
    }

    private fun createPasswordHash(password: String): String {
        if (password.length == 35 && password.startsWith("\$1\$")) {
            return password
        }
        val truncatedPassword = password.trim().take(16)
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(truncatedPassword.toByteArray())
        val hash = digest.joinToString("") { "%02x".format(it) }
        return "\$1\$${hash}"
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
        
        if (connectionState.value == ConnectionState.CONNECTED) {
            _connectionState.value = ConnectionState.DISCONNECTING
            
            try {
                // Clean up agent circuit
                agentCircuit?.close()
                agentCircuit = null
                
                // Clean up temp circuits
                tempCircuits.values.forEach { it.close() }
                tempCircuits.values.forEach { it.close() }
                tempCircuits.clear()
                
                // Clean up capabilities
                capEventQueue?.close()
                capEventQueue = null
                
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
}
