package com.linkpoint.slproto

import com.linkpoint.slproto.auth.SLAuth
import com.linkpoint.slproto.auth.SLAuthParams
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.messages.*
import kotlinx.coroutines.*
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

/**
 * Main Second Life connection manager
 * Handles authentication and circuit management
 */
class SLConnection {
    
    private var circuit: SLCircuit? = null
    private val auth = SLAuth()
    private var authReply: SLAuthReply? = null
    
    // Connection info
    private var remoteAddress: InetAddress? = null
    private var remotePort: Int = 0
    
    // Connection state
    enum class State {
        DISCONNECTED,
        AUTHENTICATING,
        CONNECTING,
        CONNECTED,
        DISCONNECTING
    }
    
    private var state = State.DISCONNECTED
    
    // Callbacks
    var onStateChanged: ((State) -> Unit)? = null
    var onMessageReceived: ((SLMessage) -> Unit)? = null
    var onError: ((Exception) -> Unit)? = null
    
    /**
     * Connect to Second Life
     */
    suspend fun connect(params: SLAuthParams): Boolean = withContext(Dispatchers.IO) {
        try {
            // Authenticate
            setState(State.AUTHENTICATING)
            authReply = auth.sendLoginRequest(params)
            
            val reply = authReply ?: throw Exception("Authentication failed: No reply")
            
            if (!reply.success) {
                throw Exception("Authentication failed: ${reply.reason ?: reply.message}")
            }
            
            // Extract connection info
            val simIp = reply.simIp ?: throw Exception("No sim IP in auth reply")
            val simPort = reply.simPort ?: throw Exception("No sim port in auth reply")
            val circuitCode = reply.circuitCode ?: throw Exception("No circuit code in auth reply")
            val agentId = UUID.fromString(reply.agentId ?: throw Exception("No agent ID"))
            val sessionId = UUID.fromString(reply.sessionId ?: throw Exception("No session ID"))
            
            // Create circuit
            setState(State.CONNECTING)
            val address = InetAddress.getByName(simIp)
            remoteAddress = address
            remotePort = simPort
            circuit = SLCircuit(address, simPort).apply {
                this.circuitCode = circuitCode
                this.agentId = agentId
                this.sessionId = sessionId
                
                this.onMessageReceived = { message ->
                    handleMessage(message)
                }
                
                this.onCircuitClosed = {
                    setState(State.DISCONNECTED)
                }
                
                start()
            }
            
            // Send UseCircuitCode to establish connection
            sendUseCircuitCode(circuitCode, sessionId, agentId)
            
            // Send CompleteAgentMovement
            sendCompleteAgentMovement(agentId, sessionId)
            
            // Send RegionHandshakeReply
            sendRegionHandshakeReply(agentId, sessionId)
            
            setState(State.CONNECTED)
            true
            
        } catch (e: Exception) {
            onError?.invoke(e)
            setState(State.DISCONNECTED)
            false
        }
    }
    
    /**
     * Disconnect from Second Life
     */
    fun disconnect() {
        setState(State.DISCONNECTING)
        circuit?.stop()
        circuit = null
        setState(State.DISCONNECTED)
    }
    
    /**
     * Send a message
     */
    fun sendMessage(message: SLMessage) {
        circuit?.sendMessage(message)
    }
    
    /**
     * Handle received message
     */
    private fun handleMessage(message: SLMessage) {
        when (message) {
            is RegionHandshakeMessage -> handleRegionHandshake(message)
            is ChatFromSimulatorMessage -> handleChatMessage(message)
            is ObjectUpdateMessage -> handleObjectUpdate(message)
            is ObjectUpdateCompressedMessage -> handleObjectUpdateCompressed(message)
            is KillObjectMessage -> handleKillObject(message)
            is ImprovedIMMessage -> handleIM(message)
            else -> {
                // Forward to application
                onMessageReceived?.invoke(message)
            }
        }
    }
    
    private fun handleRegionHandshake(message: RegionHandshakeMessage) {
        // Send reply
        val reply = RegionHandshakeReplyMessage()
        reply.agentId = circuit?.agentId ?: UUID.randomUUID()
        reply.sessionId = circuit?.sessionId ?: UUID.randomUUID()
        reply.flags = 0
        sendMessage(reply)
        
        onMessageReceived?.invoke(message)
    }
    
    private fun handleChatMessage(message: ChatFromSimulatorMessage) {
        onMessageReceived?.invoke(message)
    }
    
    private fun handleObjectUpdate(message: ObjectUpdateMessage) {
        onMessageReceived?.invoke(message)
    }
    
    private fun handleObjectUpdateCompressed(message: ObjectUpdateCompressedMessage) {
        onMessageReceived?.invoke(message)
    }
    
    private fun handleKillObject(message: KillObjectMessage) {
        onMessageReceived?.invoke(message)
    }
    
    private fun handleIM(message: ImprovedIMMessage) {
        onMessageReceived?.invoke(message)
    }
    
    /**
     * Send UseCircuitCode message
     */
    private fun sendUseCircuitCode(code: Int, sessionId: UUID, agentId: UUID) {
        // Create and send UseCircuitCode message
        // This is a system message to establish the circuit
        val buffer = ByteBuffer.allocate(256)
        buffer.order(ByteOrder.BIG_ENDIAN)
        
        // Message ID for UseCircuitCode (0xFFFFFFFF)
        buffer.put(0xFF.toByte())
        buffer.put(0xFF.toByte())
        buffer.put(0xFF.toByte())
        buffer.put(0xFF.toByte())
        
        // Circuit code
        buffer.putInt(code)
        
        // Session ID
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        
        // Agent ID
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        
        buffer.flip()
        val data = ByteArray(buffer.remaining())
        buffer.get(data)
        
        // Send raw packet through circuit
        remoteAddress?.let { addr ->
            val packet = java.net.DatagramPacket(data, data.size, addr, remotePort)
            // TODO: Add sendRawPacket method to SLCircuit or use proper message
            // For now, skip sending as this is a stub implementation
        }
    }
    
    /**
     * Send CompleteAgentMovement message
     */
    private fun sendCompleteAgentMovement(agentId: UUID, sessionId: UUID) {
        // This message tells the simulator we're ready to receive updates
        val buffer = ByteBuffer.allocate(256)
        buffer.order(ByteOrder.BIG_ENDIAN)
        
        // Message ID for CompleteAgentMovement
        buffer.put(0xF9.toByte())
        
        // Agent ID
        buffer.putLong(agentId.mostSignificantBits)
        buffer.putLong(agentId.leastSignificantBits)
        
        // Session ID
        buffer.putLong(sessionId.mostSignificantBits)
        buffer.putLong(sessionId.leastSignificantBits)
        
        // Circuit code
        buffer.putInt(circuit?.circuitCode ?: 0)
        
        buffer.flip()
        val data = ByteArray(buffer.remaining())
        buffer.get(data)
        
        // Send raw packet through circuit
        remoteAddress?.let { addr ->
            val packet = java.net.DatagramPacket(data, data.size, addr, remotePort)
            // TODO: Add sendRawPacket method to SLCircuit or use proper message
            // For now, skip sending as this is a stub implementation
        }
    }
    
    /**
     * Send RegionHandshakeReply
     */
    private fun sendRegionHandshakeReply(agentId: UUID, sessionId: UUID) {
        val reply = RegionHandshakeReplyMessage()
        reply.agentId = agentId
        reply.sessionId = sessionId
        reply.flags = 0
        reply.isReliable = true
        sendMessage(reply)
    }
    
    /**
     * Set connection state
     */
    private fun setState(newState: State) {
        if (state != newState) {
            state = newState
            onStateChanged?.invoke(newState)
        }
    }
    
    /**
     * Get current state
     */
    fun getState(): State = state
    
    /**
     * Get authentication reply
     */
    fun getAuthReply(): SLAuthReply? = authReply
}

/**
 * Extension to take elements while condition is true
 */
private fun <T> List<T>.take(maxCount: Int, predicate: (Int) -> Boolean): List<T> {
    val result = mutableListOf<T>()
    for (item in this) {
        if (result.size >= maxCount || !predicate(result.size)) break
        result.add(item)
    }
    return result
}