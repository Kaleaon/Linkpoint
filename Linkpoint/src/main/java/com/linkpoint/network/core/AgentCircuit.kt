package com.linkpoint.network.core

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.events.EventBus
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.network.events.MessageReceivedEvent
import com.linkpoint.protocol.auth.AuthReply
import com.linkpoint.protocol.messages.UDPConnectionFixedFixed
import com.linkpoint.protocol.messages.MessageRouter
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Agent Circuit
 * 
 * Primary circuit for agent communication with the Second Life grid.
 * Based on Lumiya's SLAgentCircuit implementation with mobile-first optimizations.
 * 
 * Features:
 * - UDP connection management with UDPConnectionFixed
 * - Message routing and processing via MessageRouter
 * - Event bus integration for reactive updates
 * - Circuit state management
 * - Mobile-optimized packet handling
 * 
 * Mobile-First Considerations:
 * - Efficient packet buffering
 * - Battery-aware update intervals (10 updates/sec)
 * - Memory-efficient message queues
 * - Adaptive reliability settings
 * 
 * Integration Changes:
 * - Uses UDPConnectionFixed instead of UDPConnection
 * - Integrates MessageRouter for proper packet routing
 * - Uses EventBus for reactive event distribution
 * - Maintains mobile-optimized settings (update intervals, buffer sizes)
 */
class AgentCircuit(
    private val authReply: AuthReply,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    
    companion object {
        private const val TAG = "AgentCircuit"
        private const val DEFAULT_ACK_TIMEOUT_MS = 1000L
        private const val MAX_RETRIES = 5
        
        // Agent update interval (10 updates/sec matches official viewers)
        // Mobile-optimized: reduces battery drain while maintaining responsiveness
        private const val AGENT_UPDATE_INTERVAL_MS = 100L
    }
    
    /**
     * Circuit states
     */
    enum class CircuitState {
        INITIALIZING,
        CONNECTED,
        DISCONNECTING,
        CLOSED
    }
    
    /**
     * Circuit state flow
     */
    private val _circuitState = MutableStateFlow(CircuitState.INITIALIZING)
    val circuitState: StateFlow<CircuitState> = _circuitState.asStateFlow()
    
    /**
     * Fixed UDP connection for this circuit
     * Using UDPConnectionFixed which includes MessageRouter integration
     * This fixes the receive issue by properly routing messages to handlers
     */
    private val udpConnection = UDPConnectionFixed(
        simIP = authReply.simIP,
        simPort = authReply.simPort,
        circuitCode = authReply.circuitCode
    ).apply {
        setSessionInfo(authReply.sessionId, authReply.agentId)
    }
    
    /**
     * Message router for routing incoming messages to handlers
     * Integrated with UDPConnectionFixed for efficient message processing
     */
    private val messageRouter = udpConnection.getMessageRouter()
    
    /**
     * Circuit code from authentication
     */
    val circuitCode: Int = authReply.circuitCode
    
    /**
     * Agent ID
     */
    val agentId: UUID = authReply.agentId
    
    /**
     * Session ID
     */
    val sessionId: UUID = authReply.sessionId
    
    /**
     * Agent update job
     */
    private var agentUpdateJob: Job? = null
    
    /**
     * Connected flag
     */
    private var isConnected: Boolean = false
    
    // ==================== CIRCUIT LIFECYCLE ====================
    
    init {
        initializeCircuit()
    }
    
    /**
     * Initialize the circuit
     */
    private fun initializeCircuit() {
        NetworkLogger.log(TAG, "Initializing agent circuit")
        
        try {
            // Start UDP connection with fixed implementation
            startUDPConnectionFixed()
            
            // Start agent updates (mobile-optimized interval)
            startAgentUpdates()
            
            _circuitState.value = CircuitState.CONNECTED
            isConnected = true
            
            NetworkLogger.log(TAG, "Agent circuit initialized successfully")
            
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "Failed to initialize circuit: ${e.message}", Log.ERROR)
            _circuitState.value = CircuitState.CLOSED
            throw e
        }
    }
    
    /**
     * Start UDP connection
     * Uses UDPConnectionFixed which properly handles receive operations
     */
    private suspend fun startUDPConnectionFixed() {
        NetworkLogger.log(TAG, "Starting UDP connection (Fixed implementation)")
        NetworkLogger.log(TAG, "Sim IP: ${authReply.simIP}, Port: ${authReply.simPort}")
        
        val connected = udpConnection.connect()
        
        if (connected) {
            NetworkLogger.log(TAG, "✓ UDP connection established successfully")
            isConnected = true
            
            // Publish connection state event via EventBus
            EventBus.publish(ConnectionStateChangedEvent(
                ConnectionState.DISCONNECTED,
                ConnectionState.CONNECTED
            ))
        } else {
            NetworkLogger.log(TAG, "✗ Failed to establish UDP connection", Log.ERROR)
            isConnected = false
            throw IllegalStateException("Failed to connect UDP")
        }
    }
    
    /**
     * Start agent updates
     * Sends periodic agent update messages to the server
     * Mobile-optimized: 10 updates/sec to balance responsiveness and battery
     */
    private fun startAgentUpdates() {
        agentUpdateJob = scope.launch {
            while (isActive && isConnected) {
                try {
                    sendAgentUpdate()
                    delay(AGENT_UPDATE_INTERVAL_MS)
                } catch (e: Exception) {
                    NetworkLogger.log(TAG, "Error in agent update: ${e.message}", Log.ERROR)
                }
            }
        }
    }
    
    // ==================== MESSAGE HANDLING ====================
    
    /**
     * Send agent update message
     * Delegates to UDPConnectionFixed which handles the actual sending
     */
    private suspend fun sendAgentUpdate() {
        if (!isConnected) {
            return
        }
        
        try {
            // UDPConnectionFixed handles the actual message construction and sending
            // This maintains mobile-optimized update intervals
            udpConnection.sendAgentUpdate()
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "Error sending agent update: ${e.message}", Log.ERROR)
        }
    }
    
    /**
     * Register a message handler for a specific message type
     * Uses MessageRouter for efficient message routing
     */
    suspend fun registerHandler(messageId: Int, handler: MessageRouter.Handler) {
        NetworkLogger.log(TAG, "Registering handler for message ID: $messageId")
        messageRouter.registerHandler(messageId, handler)
    }
    
    /**
     * Send a message through this circuit
     */
    suspend fun sendMessage(messageId: Int, payload: ByteArray, reliable: Boolean = false) {
        if (!isConnected) {
            NetworkLogger.log(TAG, "Cannot send message: circuit not connected", Log.WARN)
            return
        }
        
        try {
            // UDPConnectionFixed handles the actual sending with proper protocol
            // This maintains mobile-optimized settings (reliability, zero-coding, etc.)
            udpConnection.sendPacket(messageId, payload, reliable)
            NetworkLogger.log(TAG, "Sent message ID: $messageId (${payload.size} bytes)")
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "Error sending message: ${e.message}", Log.ERROR)
        }
    }
    
    /**
     * Get circuit statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "circuitCode" to circuitCode,
            "agentId" to agentId.toString(),
            "sessionId" to sessionId.toString(),
            "state" to circuitState.value.name,
            "isConnected" to isConnected,
            "udpConnected" to udpConnection.isConnected.value,
            "simIP" to authReply.simIP,
            "simPort" to authReply.simPort,
            "messageRouterStats" to messageRouter.getStatistics()
        )
    }
    
    /**
     * Close the circuit
     */
    fun close() {
        NetworkLogger.log(TAG, "Closing agent circuit")
        
        isConnected = false
        
        // Cancel agent update job
        agentUpdateJob?.cancel()
        
        // Disconnect UDP connection
        try {
            udpConnection.disconnect()
            
            // Publish connection state event via EventBus
            scope.launch {
                EventBus.publish(ConnectionStateChangedEvent(
                    ConnectionState.CONNECTED,
                    ConnectionState.DISCONNECTED
                ))
            }
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "Error closing UDP connection: ${e.message}", Log.ERROR)
        }
        
        _circuitState.value = CircuitState.CLOSED
        
        // Cancel scope
        scope.cancel()
        
        NetworkLogger.log(TAG, "Agent circuit closed")
    }
}