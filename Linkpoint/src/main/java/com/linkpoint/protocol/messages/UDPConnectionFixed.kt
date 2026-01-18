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
    
    // Statistics
    private val packetsReceived = AtomicInteger(0)
    private val bytesReceived = AtomicLong(0)
    private val messagesRouted = AtomicInteger(0)
    private var lastReceiveTime = 0L
    
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
     * Connect to the simulator
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            NetworkLogger.log(TAG, "=== INITIATING FIXED UDP CONNECTION ===")
            
            val address = InetSocketAddress(simIP, simPort)
            
            // Create and configure DatagramChannel
            datagramChannel = DatagramChannel.open().apply {
                configureBlocking(false)
                connect(address)
            }
            
            NetworkLogger.log(TAG, "✓ DatagramChannel connected to $simIP:$simPort")
            NetworkLogger.log(TAG, "  Channel connected: ${datagramChannel?.isConnected}")
            NetworkLogger.log(TAG, "  Channel open: ${datagramChannel?.isOpen}")
            
            // Create selector
            selector = Selector.open()
            
            // Register channel for read operations
            selectionKey = datagramChannel?.register(selector, SelectionKey.OP_READ)
            
            if (selectionKey?.isValid != true) {
                throw IllegalStateException("Selection key is not valid")
            }
            
            NetworkLogger.log(TAG, "✓ Selector registered for OP_READ")
            NetworkLogger.log(TAG, "  Selection key valid: ${selectionKey?.isValid}")
            NetworkLogger.log(TAG, "  Selector open: ${selector?.isOpen}")
            
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
            
            NetworkLogger.log(TAG, "✓ Receive loop started")
            NetworkLogger.log(TAG, "=== UDP CONNECTION ESTABLISHED ===")
            
            // Send initial messages
            sendUseCircuitCode()
            sendCompleteAgentMovement()
            
            // Publish circuit established event
            EventBus.publish(CircuitEstablishedEvent(circuitCode))
            
            true
            
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "✗ Connection failed: ${e.message}", Log.ERROR)
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
        
        NetworkLogger.log(TAG, "=== RECEIVE LOOP STARTED ===")
        
        while (_isConnected.value) {
            try {
                val localSelector = selector
                val localChannel = datagramChannel
                
                if (localSelector == null || localChannel == null) {
                    NetworkLogger.log(TAG, "Selector or channel is null, exiting loop", Log.ERROR)
                    break
                }
                
                if (!localSelector.isOpen || !localChannel.isOpen) {
                    NetworkLogger.log(TAG, "Selector or channel closed, exiting loop", Log.ERROR)
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
                                
                                NetworkLogger.log(TAG, "📦 PACKET RECEIVED #${packetsReceived.get()}: $bytesRead bytes")
                                
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
                    NetworkLogger.log(TAG, "✗ Receive error: ${e.message}", Log.ERROR)
                }
            }
        }
        
        NetworkLogger.log(TAG, "=== RECEIVE LOOP STOPPED ===")
        NetworkLogger.log(TAG, "Total packets: ${packetsReceived.get()}")
        NetworkLogger.log(TAG, "Total bytes: ${bytesReceived.get()}")
        NetworkLogger.log(TAG, "Messages routed: ${messagesRouted.get()}")
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
     * Register a message handler
     */
    suspend fun registerHandler(messageId: Int, handler: MessageRouter.Handler) {
        messageRouter.registerHandler(messageId, handler)
    }
    
    /**
     * Send UseCircuitCode message
     * Uses mobile-optimized packet construction
     */
    private suspend fun sendUseCircuitCode() {
        NetworkLogger.log(TAG, "→ Sending UseCircuitCode")
        
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
        NetworkLogger.log(TAG, "→ Sending CompleteAgentMovement")
        
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
            NetworkLogger.log(TAG, "Cannot send: not connected", Log.WARN)
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
            
            NetworkLogger.log(TAG, "→ Sent packet: ${finalPacket.size} bytes (ID: $messageId, reliable: $reliable)")
            
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "✗ Send error: ${e.message}", Log.ERROR)
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
        NetworkLogger.log(TAG, "=== DISCONNECTING ===")
        
        _isConnected.value = false
        
        receiveJob?.cancel()
        
        try {
            selectionKey?.cancel()
            selector?.close()
            datagramChannel?.close()
        } catch (e: Exception) {
            NetworkLogger.log(TAG, "Error during disconnect: ${e.message}", Log.ERROR)
        }
        
        // Publish connection state event
        EventBus.publish(ConnectionStateChangedEvent(
            ConnectionState.CONNECTED,
            ConnectionState.DISCONNECTED
        ))
        
        NetworkLogger.log(TAG, "=== DISCONNECTED ===")
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
}