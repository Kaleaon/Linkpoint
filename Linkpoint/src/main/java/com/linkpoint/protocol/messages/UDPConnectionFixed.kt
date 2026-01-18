package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.network.events.EventBus
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.network.events.ConnectionState
import com.linkpoint.network.events.CircuitEstablishedEvent
import com.linkpoint.network.events.MessageReceivedEvent
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Extension function to convert UUID to byte array
 * Used in packet construction
 */
private fun UUID.asBytes(): ByteArray {
    val bytes = ByteArray(16)
    val mostSignificantBits = this.mostSignificantBits
    val leastSignificantBits = this.leastSignificantBits
    
    // Write MSB and LSB in big-endian order
    for (i in 7 downTo 0) {
        bytes[7 - i] = (mostSignificantBits shr (i * 8)).toByte()
    }
    for (i in 7 downTo 0) {
        bytes[15 - i] = (leastSignificantBits shr (i * 8)).toByte()
    }
    
    return bytes
}

/**
 * Fixed UDP Connection Handler
 * 
 * Enhanced UDP connection with proper message routing and event bus integration.
 * Fixes the receive issue by implementing Lumiya-style architecture.
 * 
 * Key Fixes:
 * - Integrated MessageRouter for proper message handling
 * - EventBus integration for reactive updates
 * - Improved selector registration and validation
 * - Enhanced buffer management
 * - Better error handling and diagnostics
 * 
 * Mobile-First Considerations:
 * - Efficient resource usage
 * - Battery-conscious operations
 * - Memory-efficient buffering
 * - Comprehensive logging
 */
class UDPConnectionFixed {
    
    companion object {
        private const val TAG = "UDPConnectionFixed"
        private const val BUFFER_SIZE = 65535
        private const val SELECTOR_TIMEOUT_MS = 1000L
    }
    
    // Connection parameters
    private var simIP: String = ""
    private var simPort: Int = 0
    private var circuitCode: Int = 0
    private var sessionId: UUID = UUID(0, 0)
    private var agentId: UUID = UUID(0, 0)
    
    // NIO components
    private var datagramChannel: DatagramChannel? = null
    private var selector: Selector? = null
    private var selectionKey: SelectionKey? = null
    
    // State
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()
    
    // Message routing
    private val messageRouter = MessageRouter()
    
    // Coroutine scope
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    // Receive job
    private var receiveJob: Job? = null
    
    // Agent update job
    private var agentUpdateJob: Job? = null
    
    // Mobile optimized: 10 updates/sec = 100ms interval
    private val AGENT_UPDATE_INTERVAL_MS = 100L
    
    // Statistics
    private val packetsReceived = AtomicInteger(0)
    private val bytesReceived = AtomicLong(0)
    private val messagesRouted = AtomicInteger(0)
    private var lastReceiveTime = 0L
    private val messageTypeCounts = java.util.concurrent.ConcurrentHashMap<String, AtomicInteger>()
    private val lastMessageTimes = java.util.concurrent.ConcurrentHashMap<String, Long>()
    private val packetsResentCount = AtomicInteger(0)
    
    // Control flags for movement
    private var controlFlags: Int = 0
    
    // Current look-at direction
    private var currentLookAt: FloatArray = floatArrayOf(128f, 128f, 25f)
    
    // Registered message handlers
    private val messageHandlers = java.util.concurrent.ConcurrentHashMap<Int, MessageHandler>()
    
    /**
     * Default constructor
     */
    constructor()
    
    /**
     * Constructor with connection parameters
     */
    constructor(simIP: String, simPort: Int, circuitCode: Int) {
        this.simIP = simIP
        this.simPort = simPort
        this.circuitCode = circuitCode
    }
    
    /**
     * Configure connection parameters
     */
    fun configure(simIP: String, simPort: Int, circuitCode: Int) {
        this.simIP = simIP
        this.simPort = simPort
        this.circuitCode = circuitCode
    }
    
    /**
     * Set session information
     */
    fun setSessionInfo(sessionId: UUID, agentId: UUID) {
        this.sessionId = sessionId
        this.agentId = agentId
    }
    
    /**
     * Get the agent ID for this connection
     */
    fun getAgentId(): UUID = agentId
    
    /**
     * Get the session ID for this connection
     */
    fun getSessionId(): UUID = sessionId
    
    /**
     * Get the circuit code for this connection
     */
    fun getCircuitCode(): Int = circuitCode
    
    /**
     * Connect to the simulator
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== INITIATING FIXED UDP CONNECTION ===")
            
            val address = InetSocketAddress(simIP, simPort)
            
            // Create and configure DatagramChannel
            datagramChannel = DatagramChannel.open().apply {
                configureBlocking(false)
                connect(address)
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ DatagramChannel connected to $simIP:$simPort")
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "  Channel connected: ${datagramChannel?.isConnected}")
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "  Channel open: ${datagramChannel?.isOpen}")
            
            // Create selector
            selector = Selector.open()
            
            // Register channel for read operations
            selectionKey = datagramChannel?.register(selector, SelectionKey.OP_READ)
            
            if (selectionKey?.isValid != true) {
                throw IllegalStateException("Selection key is not valid")
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ Selector registered for OP_READ")
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "  Selection key valid: ${selectionKey?.isValid}")
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "  Selector open: ${selector?.isOpen}")
            
            // Set connected state
            _isConnected.value = true
            
            // Publish connection state event
            EventBus.publish(ConnectionStateChangedEvent(
                ConnectionState.DISCONNECTED,
                ConnectionState.CONNECTED
            ))
            
            // Start receive loop
            receiveJob = scope.launch {
                receiveLoop()
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ Receive loop started")
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== UDP CONNECTION ESTABLISHED ===")
            
            // Send initial messages
            sendUseCircuitCode()
            sendCompleteAgentMovement()
            
            // Publish circuit established event
            EventBus.publish(CircuitEstablishedEvent(circuitCode))
            
            true
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "✗ Connection failed: ${e.message}")
            _isConnected.value = false
            disconnect()
            false
        }
    }
    
    /**
     * Receive loop with proper selector usage
     */
    private suspend fun receiveLoop() {
        val buffer = ByteBuffer.allocate(BUFFER_SIZE)
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== RECEIVE LOOP STARTED ===")
        
        while (_isConnected.value) {
            try {
                val localSelector = selector
                val localChannel = datagramChannel
                
                if (localSelector == null || localChannel == null) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Selector or channel is null, exiting loop")
                    break
                }
                
                if (!localSelector.isOpen || !localChannel.isOpen) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Selector or channel closed, exiting loop")
                    break
                }
                
                // Wait for packets with timeout
                val readyKeys = localSelector.select(SELECTOR_TIMEOUT_MS)
                
                if (readyKeys > 0) {
                    val iterator = localSelector.selectedKeys().iterator()
                    while (iterator.hasNext()) {
                        val key = iterator.next()
                        iterator.remove()
                        
                        if (key.isReadable) {
                            buffer.clear()
                            
                            val bytesRead = localChannel.read(buffer)
                            
                            if (bytesRead > 0) {
                                packetsReceived.incrementAndGet()
                                bytesReceived.addAndGet(bytesRead.toLong())
                                lastReceiveTime = System.currentTimeMillis()
                                
                                buffer.flip()
                                val data = ByteArray(bytesRead)
                                buffer.get(data)
                                
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "📦 PACKET RECEIVED #${packetsReceived.get()}: $bytesRead bytes")
                                
                                // Publish message received event
                                val messageId = extractMessageId(data)
                                EventBus.publish(MessageReceivedEvent(messageId, data))
                                
                                // Route message through router
                                routeMessage(data)
                            }
                        }
                    }
                }
                
            } catch (e: Exception) {
                if (_isConnected.value) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "✗ Receive error: ${e.message}")
                }
            }
        }
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== RECEIVE LOOP STOPPED ===")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Total packets: ${packetsReceived.get()}")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Total bytes: ${bytesReceived.get()}")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Messages routed: ${messagesRouted.get()}")
    }
    
    /**
     * Route message through message router
     */
    private suspend fun routeMessage(data: ByteArray) {
        val messageId = extractMessageId(data)
        val routed = messageRouter.routeMessage(messageId, data)
        
        if (routed) {
            messagesRouted.incrementAndGet()
        }
    }
    
    /**
     * Extract message ID from packet
     */
    private fun extractMessageId(data: ByteArray): Int {
        if (data.size < 6) return -1
        
        val flags = data[0].toInt() and 0xFF
        val frequency = data[1].toInt() and 0xFF
        
        return when {
            frequency < 128 -> frequency // High frequency
            frequency < 255 -> frequency or 0xFF00 // Medium frequency
            else -> (frequency shl 8) or (data[2].toInt() and 0xFF) // Low frequency
        }
    }
    
    /**
     * Get the message router for external handler registration
     * This allows AgentCircuit and other components to register handlers
     */
    fun getMessageRouter(): MessageRouter = messageRouter
    
    /**
     * Register a message handler using a lambda
     * This is a convenience method that wraps the lambda in a MessageRouter.Handler
     */
    fun registerHandler(messageId: Int, handler: (Int, ByteArray) -> Unit) {
        kotlinx.coroutines.runBlocking {
            messageRouter.registerHandler(messageId, object : MessageRouter.Handler {
                override fun handleMessage(messageId: Int, data: ByteArray): Boolean {
                    handler(messageId, data)
                    return true
                }
            })
        }
    }
    
    /**
     * Register a message handler with MessageRouter.Handler interface
     */
    suspend fun registerHandlerWithPriority(messageId: Int, handler: MessageRouter.Handler) {
        messageRouter.registerHandler(messageId, handler)
    }
    
    /**
     * Send UseCircuitCode message
     * Uses mobile-optimized packet construction
     */
    private suspend fun sendUseCircuitCode() {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sending UseCircuitCode")
        
        // UseCircuitCode message format:
        // - CircuitCode (4 bytes, little-endian)
        // - SessionID (16 bytes, UUID)
        // - AgentID (16 bytes, UUID)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        payload.putInt(circuitCode)
        payload.put(sessionId.asBytes())
        payload.put(agentId.asBytes())
        
        // Message ID for UseCircuitCode (low frequency: -65533)
        val messageId = -65533
        
        // Build packet with header
        sendPacket(messageId, payload.array(), reliable = true)
    }
    
    /**
     * Send CompleteAgentMovement message
     * Uses mobile-optimized packet construction
     */
    private suspend fun sendCompleteAgentMovement() {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sending CompleteAgentMovement")
        
        // CompleteAgentMovement message format:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // - CircuitCode (4 bytes, little-endian)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        payload.put(agentId.asBytes())
        payload.put(sessionId.asBytes())
        payload.putInt(circuitCode)
        
        // Message ID for CompleteAgentMovement (high frequency: 19)
        val messageId = 19
        
        // Build packet with header
        sendPacket(messageId, payload.array(), reliable = true)
    }
    
    /**
     * Send AgentUpdate message
     * Mobile-optimized: 10 updates/sec to balance responsiveness and battery
     */
    suspend fun sendAgentUpdate() {
        if (!_isConnected.value) {
            return
        }
        
        // AgentUpdate message format:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // - BodyRotation (12 bytes, quaternion)
        // - HeadRotation (12 bytes, quaternion)
        // - State (1 byte)
        // - CameraCenter (12 bytes, Vector3)
        // - CameraAtAxis (12 bytes, Vector3)
        // - CameraLeftAxis (12 bytes, Vector3)
        // - CameraUpAxis (12 bytes, Vector3)
        // - Far (4 bytes, F32)
        // - ControlFlags (4 bytes, U32)
        // - Flags (1 byte)
        val payload = ByteBuffer.allocate(114).order(ByteOrder.LITTLE_ENDIAN)
        
        // AgentID and SessionID
        payload.put(agentId.asBytes())
        payload.put(sessionId.asBytes())
        
        // Body rotation (identity quaternion: x=0, y=0, z=0, w computed by server)
        payload.putFloat(0f)
        payload.putFloat(0f)
        payload.putFloat(0f)
        
        // Head rotation (identity quaternion)
        payload.putFloat(0f)
        payload.putFloat(0f)
        payload.putFloat(0f)
        
        // State (0 = standing)
        payload.put(0.toByte())
        
        // Camera center (default position)
        payload.putFloat(128f)
        payload.putFloat(128f)
        payload.putFloat(25f)
        
        // Camera look-at direction (looking forward)
        payload.putFloat(1f)
        payload.putFloat(0f)
        payload.putFloat(0f)
        
        // Camera left axis
        payload.putFloat(0f)
        payload.putFloat(-1f)
        payload.putFloat(0f)
        
        // Camera up axis
        payload.putFloat(0f)
        payload.putFloat(0f)
        payload.putFloat(1f)
        
        // Far distance
        payload.putFloat(128f)
        
        // Control flags (0 = no movement)
        payload.putInt(0)
        
        // Flags
        payload.put(0.toByte())
        
        // Message ID for AgentUpdate (high frequency: 4)
        val messageId = 4
        
        // Build packet with header (not reliable, sent frequently)
        sendPacket(messageId, payload.array(), reliable = false)
    }
    
    /**
     * Send RegionHandshakeReply message.
     * Must be sent in response to RegionHandshake from simulator.
     */
    suspend fun sendRegionHandshakeReply(flags: Int = 0) {
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Flags (typically 0)
        payload.putInt(flags)
        
        Log.d(TAG, "Sending RegionHandshakeReply")
        sendPacket(MessageIds.REGION_HANDSHAKE_REPLY, payload.array(), reliable = true, zerocoded = true)
    }
    
    /**
     * Send AgentThrottle message to set bandwidth allocations.
     * Tells the simulator how much bandwidth we want for different data types.
     */
    suspend fun sendAgentThrottle(
        resend: Float = 50000f,
        land: Float = 100000f,
        wind: Float = 10000f,
        cloud: Float = 10000f,
        task: Float = 200000f,
        texture: Float = 200000f,
        asset: Float = 100000f
    ) {
        val payload = ByteBuffer.allocate(36 + 4 + 28).order(ByteOrder.LITTLE_ENDIAN)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Circuit code
        payload.putInt(circuitCode)
        
        // GenCounter
        payload.putInt(1)
        
        // Throttles - 7 float values for bandwidth allocation
        payload.putFloat(resend)
        payload.putFloat(land)
        payload.putFloat(wind)
        payload.putFloat(cloud)
        payload.putFloat(task)
        payload.putFloat(texture)
        payload.putFloat(asset)
        
        Log.d(TAG, "Sending AgentThrottle")
        sendPacket(MessageIds.AGENT_THROTTLE, payload.array(), reliable = true)
    }
    
    /**
     * Send a packet with proper SL protocol encoding
     * 
     * @param messageId The message ID
     * @param payload The message payload (already encoded)
     * @param reliable Whether this packet is reliable
     * @param zerocoded Whether to use zero-coding
     */
    suspend fun sendPacket(
        messageId: Int, 
        payload: ByteArray, 
        reliable: Boolean = false,
        zerocoded: Boolean = false
    ) {
        if (!_isConnected.value) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Cannot send: not connected")
            return
        }
        
        try {
            // Build packet header (big-endian per SL protocol)
            val flags = (if (reliable) 0x40 else 0) or (if (zerocoded) 0x80 else 0)
            val sequence = 0 // TODO: Track sequence numbers
            
            val header = ByteBuffer.allocate(6).order(ByteOrder.BIG_ENDIAN)
            header.put(flags.toByte())
            header.putInt(sequence)
            header.put(0.toByte()) // Extra header byte
            
            // Encode message ID (Lumiya-style)
            val messageIdBytes = encodeMessageId(messageId)
            
            // Combine header, message ID, and payload
            val packet = header.array() + messageIdBytes + payload
            
            // Zero-code if requested
            val finalPacket = if (zerocoded) zeroEncode(packet) else packet
            
            // Send via DatagramChannel
            val buffer = ByteBuffer.wrap(finalPacket)
            datagramChannel?.write(buffer)
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sent packet: ${finalPacket.size} bytes (ID: $messageId, reliable: $reliable)")
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "✗ Send error: ${e.message}")
        }
    }
    
    /**
     * Encode message ID for transmission (Lumiya-style)
     */
    private fun encodeMessageId(messageId: Int): ByteArray {
        return when {
            // Low frequency: negative values < -128
            messageId < -128 -> {
                val shortValue = messageId and 0xFFFF
                byteArrayOf(
                    0xFF.toByte(),
                    0xFF.toByte(),
                    ((shortValue shr 8) and 0xFF).toByte(),
                    (shortValue and 0xFF).toByte()
                )
            }
            // Medium frequency: 65280-65534
            messageId in 65280..65534 -> {
                byteArrayOf(0xFF.toByte(), (messageId and 0xFF).toByte())
            }
            // High frequency: signed byte
            else -> {
                byteArrayOf(messageId.toByte())
            }
        }
    }
    
    /**
     * Zero-encode packet (compress consecutive zeros)
     */
    private fun zeroEncode(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        var i = 0
        
        // Copy header (not zero-coded)
        while (i < 6 && i < data.size) {
            result.add(data[i])
            i++
        }
        
        // Zero-encode body
        while (i < data.size) {
            if (data[i] == 0.toByte()) {
                var count = 0
                while (i < data.size && data[i] == 0.toByte() && count < 255) {
                    count++
                    i++
                }
                result.add(0.toByte())
                result.add(count.toByte())
            } else {
                result.add(data[i])
                i++
            }
        }
        
        return result.toByteArray()
    }
    
    /**
     * Disconnect
     */
    fun disconnect() {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== DISCONNECTING ===")
        
        _isConnected.value = false
        
        receiveJob?.cancel()
        agentUpdateJob?.cancel()
        
        try {
            selectionKey?.cancel()
            selector?.close()
            datagramChannel?.close()
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Error during disconnect: ${e.message}")
        }
        
        // Publish connection state event (using scope.launch since publish is suspend)
        scope.launch {
            EventBus.publish(ConnectionStateChangedEvent(
                ConnectionState.CONNECTED,
                ConnectionState.DISCONNECTED
            ))
        }
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== DISCONNECTED ===")
    }
    
    /**
     * Get statistics
     */
    fun getStatistics(): Map<String, Any> {
        return mapOf(
            "connected" to _isConnected.value,
            "packetsReceived" to packetsReceived.get(),
            "bytesReceived" to bytesReceived.get(),
            "messagesRouted" to messagesRouted.get(),
            "lastReceiveTime" to lastReceiveTime,
            "routerStats" to messageRouter.getStatistics()
        )
    }
    
    /**
     * Start sending periodic AgentUpdate messages.
     * This is required for proper operation in Second Life.
     */
    fun startAgentUpdates() {
        agentUpdateJob?.cancel()
        agentUpdateJob = scope.launch {
            Log.d(TAG, "Starting periodic AgentUpdate messages")
            while (_isConnected.value) {
                sendAgentUpdate()
                delay(AGENT_UPDATE_INTERVAL_MS)
            }
        }
    }
    
    /**
     * Stop sending periodic AgentUpdate messages.
     */
    fun stopAgentUpdates() {
        agentUpdateJob?.cancel()
        agentUpdateJob = null
    }
    
    /**
     * Register a message handler for a specific message ID
     */
    fun registerMessageHandler(messageId: Int, handler: MessageHandler) {
        messageHandlers[messageId] = handler
    }
    
    /**
     * Unregister a message handler
    */
    fun unregisterMessageHandler(messageId: Int) {
        messageHandlers.remove(messageId)
    }
    
    /**
     * Set control flags (for movement).
     */
    fun setControlFlags(flags: Int) {
        controlFlags = flags
    }
    
    /**
     * Update look-at direction for camera/avatar orientation
     */
    fun updateLookAt(x: Float, y: Float, z: Float) {
        currentLookAt = floatArrayOf(x, y, z)
    }
    
    /**
     * Get list of registered message handler IDs for diagnostics
     */
    fun getRegisteredHandlerIds(): List<String> {
        return messageHandlers.keys.map { id ->
            when (id) {
                MessageIds.REGION_HANDSHAKE -> "REGION_HANDSHAKE"
                MessageIds.AGENT_MOVEMENT_COMPLETE -> "AGENT_MOVEMENT_COMPLETE"
                MessageIds.CHAT_FROM_SIMULATOR -> "CHAT_FROM_SIMULATOR"
                MessageIds.OBJECT_UPDATE -> "OBJECT_UPDATE"
                MessageIds.OBJECT_UPDATE_COMPRESSED -> "OBJECT_UPDATE_COMPRESSED"
                MessageIds.AVATAR_ANIMATION -> "AVATAR_ANIMATION"
                MessageIds.IMPROVED_TERSE_OBJECT_UPDATE -> "IMPROVED_TERSE_OBJECT_UPDATE"
                MessageIds.KILL_OBJECT -> "KILL_OBJECT"
                MessageIds.COARSE_LOCATION_UPDATE -> "COARSE_LOCATION_UPDATE"
                MessageIds.START_PING_CHECK -> "START_PING_CHECK"
                MessageIds.PACKET_ACK -> "PACKET_ACK"
                else -> "0x${id.toString(16).uppercase()}"
            }
        }
    }
    
    /**
     * Get the number of registered message handlers
     */
    fun getRegisteredHandlerCount(): Int = messageHandlers.size
    
    /**
     * Update agent position for AgentUpdate messages
     */
    fun updateAgentPosition(x: Float, y: Float, z: Float) {
        // Position is not currently used in sendAgentUpdate but can be added later
    }
    
    /**
     * Handle StartPingCheck message from simulator.
     * Responds with CompletePingCheck to maintain the connection.
     */
    suspend fun handleStartPingCheck(pingId: Byte, oldestUnacked: Int) {
        val payload = ByteBuffer.allocate(5).order(ByteOrder.LITTLE_ENDIAN)
        payload.put(pingId)
        payload.putInt(0) // Simplified - no pending ACKs tracking yet
        
        Log.d(TAG, "Responding to ping check $pingId")
        sendPacket(MessageIds.COMPLETE_PING_CHECK, payload.array(), reliable = false)
    }
    
    /**
     * Mark handlers as ready. 
     * In this simplified implementation, this is a no-op.
     */
    fun setHandlersReady() {
        Log.i(TAG, "Handlers marked ready")
    }
    
    /**
     * Track message reception for statistics
     */
    fun trackMessageReceived(messageType: String) {
        messageTypeCounts.computeIfAbsent(messageType) { AtomicInteger(0) }.incrementAndGet()
        lastMessageTimes[messageType] = System.currentTimeMillis()
    }
    
    /**
     * Get message statistics for diagnostics
     */
    fun getMessageStatistics(): MessageStatistics {
        return MessageStatistics(
            totalPacketsReceived = packetsReceived.get(),
            totalBytesReceived = bytesReceived.get(),
            packetsResent = packetsResentCount.get(),
            messageTypeCounts = messageTypeCounts.mapValues { it.value.get() },
            lastMessageTimes = lastMessageTimes.toMap()
        )
    }
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): UDPDiagnostics {
        return UDPDiagnostics(
            isConnected = _isConnected.value,
            simIP = simIP,
            simPort = simPort,
            circuitCode = circuitCode,
            agentId = agentId,
            sessionId = sessionId,
            sequenceNumber = 0, // TODO: Track sequence numbers
            pendingAckCount = 0,
            registeredHandlerCount = messageHandlers.size,
            registeredHandlers = messageHandlers.keys.map { it.toString() },
            pendingPackets = emptyList(),
            socketOpen = datagramChannel?.isOpen ?: false,
            receiveLoopActive = receiveJob?.isActive == true
        )
    }
    
    /**
     * Detailed message statistics for diagnostics
     */
    data class MessageStatistics(
        val totalPacketsReceived: Int,
        val totalBytesReceived: Long,
        val packetsResent: Int,
        val messageTypeCounts: Map<String, Int>,
        val lastMessageTimes: Map<String, Long>
    )
    
    /**
     * Diagnostic data class for UDP connection state
     */
    data class UDPDiagnostics(
        val isConnected: Boolean,
        val simIP: String,
        val simPort: Int,
        val circuitCode: Int,
        val agentId: UUID,
        val sessionId: UUID,
        val sequenceNumber: Int,
        val pendingAckCount: Int,
        val registeredHandlerCount: Int,
        val registeredHandlers: List<String>,
        val pendingPackets: List<PendingPacketInfo>,
        val socketOpen: Boolean,
        val receiveLoopActive: Boolean
    )
    
    /**
     * Info about a pending packet for diagnostics
     */
    data class PendingPacketInfo(
        val seqNum: Int,
        val retries: Int,
        val ageMs: Long
    )
    
    /**
     * Packet history entry for debugging.
     */
    data class PacketHistoryEntry(
        val timestamp: Long,
        val type: PacketEventType,
        val messageId: Int,
        val messageName: String,
        val size: Int,
        val sequenceNumber: Int,
        val hexPreview: String,
        val success: Boolean,
        val errorMessage: String? = null
    ) {
        enum class PacketEventType {
            SEND_SUCCESS,
            SEND_FAILED,
            RECEIVE,
            RESEND,
            ACK_RECEIVED,
            ACK_TIMEOUT
        }
    }
    
    /**
     * Socket details for diagnostics.
     */
    data class SocketDetails(
        val localBindAddress: String?,
        val localBindPort: Int,
        val remoteAddress: String,
        val remotePort: Int,
        val isConnected: Boolean,
        val isOpen: Boolean,
        val connectionAttemptTime: Long,
        val lastSendAttemptTime: Long,
        val lastReceiveTime: Long,
        val lastConnectionError: String?
    )
    
    /**
     * Get packet history for debugging (returns empty list - simplified implementation)
     */
    fun getPacketHistory(): List<PacketHistoryEntry> {
        return emptyList()
    }
    
    /**
     * Get socket details for diagnostics.
     */
    fun getSocketDetails(): SocketDetails {
        return SocketDetails(
            localBindAddress = null,
            localBindPort = 0,
            remoteAddress = simIP,
            remotePort = simPort,
            isConnected = datagramChannel?.isConnected == true,
            isOpen = datagramChannel?.isOpen == true,
            connectionAttemptTime = 0L,
            lastSendAttemptTime = 0L,
            lastReceiveTime = lastReceiveTime,
            lastConnectionError = null
        )
    }
}