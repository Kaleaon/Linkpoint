package com.linkpoint.network.core

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.events.EventBus
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.network.events.ConnectionState
import com.linkpoint.protocol.auth.AuthReply
import com.linkpoint.protocol.messages.UDPConnectionFixed
import com.linkpoint.protocol.messages.MessageRouter
import com.linkpoint.protocol.messages.CircuitDispatcher
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

/**
 * Temporary Circuit
 * 
 * Temporary circuit for specific operations in the Second Life grid.
 * Used for tasks like teleportation, region crossing, and temporary asset operations.
 * Based on the reference viewer's SLTempCircuit implementation with mobile-first optimizations.
 * 
 * Features:
 * - Short-lived circuit for temporary operations
 * - Lightweight UDP connection with UDPConnectionFixed
 * - Message routing via MessageRouter
 * - EventBus integration for reactive updates
 * - Automatic cleanup after operation completion
 * - Mobile-optimized resource usage
 * 
 * Mobile-First Considerations:
 * - Minimal memory footprint
 * - Fast creation and destruction
 * - Efficient for one-time operations
 * - Battery-conscious design
 * - 30-second timeout to prevent resource leaks
 * 
 * Integration Changes:
 * - Uses UDPConnectionFixed instead of UDPConnection
 * - Integrates MessageRouter for proper packet routing
 * - Uses EventBus for reactive event distribution
 */
class TempCircuit(
    private val authReply: AuthReply,
    private val scope: CoroutineScope = CoroutineScope(CircuitDispatcher.dispatcher + SupervisorJob())
) {
    
    companion object {
        private const val TAG = "TempCircuit"
        private const val DEFAULT_TIMEOUT_MS = 30000L // 30 seconds
        private const val CLEANUP_DELAY_MS = 5000L // 5 seconds after completion
    }
    
    /**
     * Circuit states
     */
    enum class CircuitState {
        INITIALIZING,
        ACTIVE,
        COMPLETED,
        TIMEOUT,
        ERROR,
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
     */
    private val messageRouter = udpConnection.getMessageRouter()
    
    /**
     * Circuit code
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
     * Timeout job
     */
    private var timeoutJob: Job? = null
    
    /**
     * Active flag
     */
    private var isActive: Boolean = false
    
    // ==================== CIRCUIT LIFECYCLE ====================
    
    init {
        scope.launch {
            initializeCircuit()
        }
    }
    
    /**
     * Initialize the temp circuit
     */
    private suspend fun initializeCircuit() {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Initializing temp circuit")
        
        try {
            // Start UDP connection with fixed implementation
            startUDPConnectionFixed()
            
            // Start timeout timer (mobile-optimized: 30s max)
            startTimeoutTimer()
            
            _circuitState.value = CircuitState.ACTIVE
            isActive = true
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Temp circuit initialized successfully")
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Failed to initialize temp circuit: ${e.message}")
            _circuitState.value = CircuitState.ERROR
            close()
            throw e
        }
    }
    
    /**
     * Start UDP connection
     * Uses UDPConnectionFixed which properly handles receive operations
     */
    private suspend fun startUDPConnectionFixed() {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Starting temp circuit UDP connection (Fixed implementation)")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Sim IP: ${authReply.simIP}, Port: ${authReply.simPort}")
        
        val connected = udpConnection.connect()
        
        if (connected) {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ Temp circuit UDP connection established")
            isActive = true
            
            // Publish connection state event via EventBus
            EventBus.publish(ConnectionStateChangedEvent(
                ConnectionState.DISCONNECTED,
                ConnectionState.CONNECTED
            ))
        } else {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "✗ Failed to establish temp circuit UDP connection")
            isActive = false
            throw IllegalStateException("Failed to connect temp circuit UDP")
        }
    }
    
    /**
     * Start timeout timer
     * Mobile-optimized: prevents resource leaks from abandoned circuits
     */
    private fun startTimeoutTimer() {
        timeoutJob = scope.launch {
            delay(DEFAULT_TIMEOUT_MS)
            
            if (isActive) {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Temp circuit timed out")
                _circuitState.value = CircuitState.TIMEOUT
                close()
            }
        }
    }
    
    // ==================== MESSAGE HANDLING ====================
    
    /**
     * Register a message handler for a specific message type
     * Uses MessageRouter for efficient message routing
     */
    suspend fun registerHandler(messageId: Int, handler: MessageRouter.Handler) {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Registering temp circuit handler for message ID: $messageId")
        messageRouter.registerHandler(messageId, handler)
    }
    
    /**
     * Send a message through this circuit
     */
    suspend fun sendMessage(messageId: Int, payload: ByteArray, reliable: Boolean = false) {
        if (!isActive) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Cannot send message: temp circuit not active")
            return
        }
        
        try {
            // UDPConnectionFixed handles the actual sending with proper protocol
            udpConnection.sendPacket(messageId, payload, reliable)
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Sent temp circuit message ID: $messageId (${payload.size} bytes)")
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Error sending message: ${e.message}")
        }
    }
    
    /**
     * Mark operation as completed
     * Triggers cleanup after a short delay
     */
    fun markCompleted() {
        if (isActive) {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Marking temp circuit as completed")
            isActive = false
            _circuitState.value = CircuitState.COMPLETED
            
            // Schedule cleanup
            scope.launch {
                delay(CLEANUP_DELAY_MS)
                close()
            }
        }
    }
    
    /**
     * Mark operation as failed
     */
    fun markFailed(error: String) {
        if (isActive) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Marking temp circuit as failed: $error")
            isActive = false
            _circuitState.value = CircuitState.ERROR
            
            // Schedule cleanup
            scope.launch {
                delay(CLEANUP_DELAY_MS)
                close()
            }
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
            "isActive" to isActive,
            "udpConnected" to udpConnection.isConnected.value,
            "simIP" to authReply.simIP,
            "simPort" to authReply.simPort,
            "messageRouterStats" to messageRouter.getStatistics()
        )
    }
    
    /**
     * Close the temp circuit
     */
    fun close() {
        if (_circuitState.value == CircuitState.CLOSED) {
            return // Already closed
        }
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Closing temp circuit")
        
        isActive = false
        
        // Cancel timeout job
        timeoutJob?.cancel()
        
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
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Error closing UDP connection: ${e.message}")
        }
        
        _circuitState.value = CircuitState.CLOSED
        
        // Cancel scope
        scope.cancel()
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Temp circuit closed")
    }
}
