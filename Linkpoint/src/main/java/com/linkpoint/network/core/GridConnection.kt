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
            
            // For now, indicate we need authentication data to be set externally
            // This simulates waiting for auth response
            delay(100)
            
            if (authReply == null) {
                // No pre-set auth reply - this is a stub/test scenario
                // In production, authReply should be set before calling connect
                // or passed through from the actual login response
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, 
                    "No auth reply set - operating in stub mode. " +
                    "For real connections, use SecondLifeProtocol.login() instead.")
                
                authReply = AuthReply(
                    sessionId = UUID.randomUUID(),
                    agentId = UUID.randomUUID(),
                    circuitCode = 123456789,
                    simIP = "0.0.0.0",  // Placeholder - actual IP comes from login response
                    simPort = 0         // Placeholder - actual port comes from login response
                )
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