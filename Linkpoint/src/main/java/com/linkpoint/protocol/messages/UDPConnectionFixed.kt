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
 * Fixed UDP Connection Handler for Second Life Protocol
 * 
 * This is the primary UDP connection implementation used throughout Linkpoint.
 * It provides comprehensive packet handling with full diagnostic capabilities
 * for debugging connection issues.
 * 
 * ## Architecture Overview
 * 
 * This class implements the Second Life UDP protocol with proper message routing
 * and event bus integration, following Lumiya-style architecture patterns.
 * 
 * ## Key Features
 * 
 * ### Message Routing
 * - Integrated MessageRouter for proper message handling
 * - EventBus integration for reactive updates across the app
 * - Support for all SL message frequencies (high, medium, low)
 * 
 * ### Diagnostic Capabilities (Critical for Debugging)
 * - **Packet History Tracking**: Records all sent/received packets with raw hex data
 *   - Enables diagnosis of protocol issues by capturing actual packet contents
 *   - Stored in circular buffer (last 50 packets) for memory efficiency
 * - **EnhancedPacketLogger Integration**: Comprehensive statistics and logging
 *   - Tracks packet counts, byte volumes, message types
 *   - Records malformed packets for protocol debugging
 * - **Socket Details**: Connection timing and state information
 *   - Local/remote addresses and ports
 *   - Connection attempt and last activity timestamps
 *   - Last error information for troubleshooting
 * 
 * ### Mobile-First Design
 * - Efficient resource usage with non-blocking NIO
 * - Battery-conscious operations with selective logging
 * - Memory-efficient buffering with bounded queues
 * 
 * ## Usage
 * 
 * This class is instantiated by LinkpointApp and shared across managers.
 * Packet history and diagnostics are accessed via:
 * - `getPacketHistory()` - Recent packet events with hex dumps
 * - `getSocketDetails()` - Connection state and timing
 * - `getDiagnostics()` - Overall connection diagnostics
 * - `getMessageStatistics()` - Message type counts and timing
 * 
 * ## Packet Data Logging
 * 
 * Raw packet data is logged for diagnostic purposes:
 * - Each sent packet: message ID, sequence number, size, hex preview
 * - Each received packet: message ID, sequence number, size, hex preview
 * - Failed operations include error messages
 * 
 * This addresses the debugging need identified in issue reports:
 * "Not enough data is being gathered, we need raw input and output to understand"
 * 
 * @see EnhancedPacketLogger for detailed packet statistics
 * @see MessageRouter for message dispatch logic
 * @see DebugReportService for how diagnostics are displayed
 */
class UDPConnectionFixed {
    
    companion object {
        private const val TAG = "UDPConnectionFixed"
        
        /** Maximum UDP datagram size (64KB - 1) */
        private const val BUFFER_SIZE = 65535
        
        /** Timeout for NIO selector operations (1 second) */
        private const val SELECTOR_TIMEOUT_MS = 1000L
        
        /** 
         * Packet header size: flags (1) + sequence (4) + extra (1) = 6 bytes
         * This is constant across all SL UDP packets
         */
        private const val PACKET_HEADER_SIZE = 6
        
        /**
         * Frequency bases for message ID encoding (matching Lumiya)
         * 
         * Second Life uses three message frequency ranges:
         * - High frequency: Single byte (0x00-0xFE), used for frequent messages like ObjectUpdate
         * - Medium frequency: 0xFF + byte, decoded as (byte | 65280)
         * - Low frequency: 0xFF 0xFF + short, decoded as (short | -65536)
         */
        private const val MEDIUM_FREQUENCY_BASE = 65280  // 0xFF00
        private const val LOW_FREQUENCY_BASE = -65536    // 0xFFFF0000 as signed Int32
        
        /** Sentinel value for invalid/unparseable message IDs */
        private const val INVALID_MESSAGE_ID = Int.MIN_VALUE
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
    
    // ==================== STATISTICS & DIAGNOSTICS ====================
    // These fields track packet activity for debug reports and diagnostic purposes.
    // Raw packet data including hex dumps are captured to enable protocol debugging.
    
    /** Total packets received from simulator */
    private val packetsReceived = AtomicInteger(0)
    
    /** Total packets sent to simulator */
    private val packetsSent = AtomicInteger(0)
    
    /** Total bytes received from simulator */
    private val bytesReceived = AtomicLong(0)
    
    /** Total bytes sent to simulator */
    private val bytesSent = AtomicLong(0)
    
    /** Count of messages successfully routed to handlers */
    private val messagesRouted = AtomicInteger(0)
    
    /** Timestamp of last packet received (for timing diagnostics) */
    private var lastReceiveTime = 0L
    
    /** Timestamp of last packet sent (for timing diagnostics) */
    private var lastSendTime = 0L
    
    /** Timestamp when connection was attempted (for connection duration) */
    private var connectionAttemptTime = 0L
    
    /** Last connection error message (for troubleshooting) */
    private var lastConnectionError: String? = null
    
    /** Local bind address for socket diagnostics */
    private var localBindAddress: String? = null
    
    /** Local bind port for socket diagnostics */
    private var localBindPort: Int = 0
    
    /** Count of each message type received (for protocol analysis) */
    private val messageTypeCounts = java.util.concurrent.ConcurrentHashMap<String, AtomicInteger>()
    
    /** Last time each message type was received (for protocol analysis) */
    private val lastMessageTimes = java.util.concurrent.ConcurrentHashMap<String, Long>()
    
    /** Count of packets that were resent due to ACK timeout */
    private val packetsResentCount = AtomicInteger(0)
    
    // ==================== PACKET HISTORY FOR RAW DATA LOGGING ====================
    // Critical for debugging: captures raw packet data with hex dumps.
    // This addresses the requirement: "we need raw input and output to understand"
    
    /** 
     * Circular buffer of recent packet events including raw hex data.
     * Used by DebugReportService to show packet history in debug reports.
     * Each entry contains: timestamp, type, message ID, size, sequence number, hex preview
     */
    private val recentPacketHistory = java.util.concurrent.ConcurrentLinkedQueue<PacketHistoryEntry>()
    
    /** Maximum number of packet events to keep in history (memory bounded) */
    private val maxPacketHistorySize = 50
    
    /** 
     * Sequence number for outgoing packets.
     * Incremented for each packet sent, used for reliable delivery ACK tracking.
     */
    private val sequenceNumber = AtomicInteger(0)
    
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
            // Record connection attempt time and reset statistics
            connectionAttemptTime = System.currentTimeMillis()
            lastConnectionError = null
            recentPacketHistory.clear()
            
            // Start EnhancedPacketLogger session for comprehensive tracking
            EnhancedPacketLogger.startSession()
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== INITIATING FIXED UDP CONNECTION ===")
            
            val address = InetSocketAddress(simIP, simPort)
            
            // Create and configure DatagramChannel
            datagramChannel = DatagramChannel.open().apply {
                configureBlocking(false)
                connect(address)
            }
            
            // Capture local bind information for diagnostics
            try {
                val localAddr = datagramChannel?.localAddress as? InetSocketAddress
                localBindAddress = localAddr?.address?.hostAddress
                localBindPort = localAddr?.port ?: 0
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ Local bind: $localBindAddress:$localBindPort")
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Could not determine local bind address: ${e.message}")
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
            lastConnectionError = e.message ?: e.javaClass.simpleName
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
                                
                                // Extract message info for logging
                                val messageId = extractMessageId(data)
                                val messageName = getMessageName(messageId)
                                val seqNum = extractSequenceNumber(data)
                                val hexPreview = data.take(32).joinToString(" ") { "%02X".format(it) }
                                
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "📦 PACKET RECEIVED #${packetsReceived.get()}: $bytesRead bytes")
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Message: $messageName (ID: 0x${messageId.toString(16).uppercase()}, seq: $seqNum)")
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Raw data: $hexPreview")
                                
                                // Record in packet history for debug reports
                                recordPacketEvent(
                                    type = PacketHistoryEntry.PacketEventType.RECEIVE,
                                    messageId = messageId,
                                    data = data,
                                    sequenceNumber = seqNum,
                                    success = true
                                )
                                
                                // Log to EnhancedPacketLogger for comprehensive tracking
                                EnhancedPacketLogger.logPacketReceived(
                                    messageId = messageId,
                                    messageName = messageName,
                                    sequenceNumber = seqNum,
                                    data = data,
                                    flags = extractPacketFlags(data),
                                    handlerFound = messageHandlers.containsKey(messageId)
                                )
                                
                                // Publish message received event
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
     * Extract message ID from packet using Lumiya-style decoding.
     * 
     * The packet format is:
     * - Bytes 0-5: Header (flags, sequence number, extra byte)
     * - Bytes 6+: Message ID and payload
     * 
     * Message ID encoding:
     * - High frequency: 1 byte (values 0-254, where 255/-1 means continue to medium)
     * - Medium frequency: 2 bytes (0xFF, then value) - value | 65280
     * - Low frequency: 4 bytes (0xFF, 0xFF, then 2-byte big-endian short) - short | -65536
     */
    private fun extractMessageId(data: ByteArray): Int {
        if (data.size < PACKET_HEADER_SIZE + 1) return INVALID_MESSAGE_ID
        
        val result = decodeMessageIdLumiyaStyle(data, PACKET_HEADER_SIZE)
        return result?.first ?: INVALID_MESSAGE_ID
    }
    
    /**
     * Decode message ID using Lumiya-compatible encoding.
     * Returns Pair of (messageId, nextOffset) or null if invalid.
     */
    private fun decodeMessageIdLumiyaStyle(data: ByteArray, startOffset: Int): Pair<Int, Int>? {
        if (data.size <= startOffset) return null
        
        var offset = startOffset
        
        // First byte - check if it's 0xFF (which is -1 as signed byte)
        val b1 = data[offset].toInt() // Signed byte, -128 to 127
        offset++
        
        if (b1 != -1) {
            // High frequency message - return the signed byte value directly
            // This matches Lumiya: if (b != -1) return b;
            // e.g., 0x0C (12) = ObjectUpdate, 0xFB (-5) = PacketAck
            return Pair(b1, offset)
        }
        
        // Second byte
        if (data.size <= offset) return null
        val b2 = data[offset].toInt() // Signed byte
        offset++
        
        if (b2 != -1) {
            // Medium frequency message - byte OR MEDIUM_FREQUENCY_BASE
            // This matches Lumiya: b2 | 65280
            // e.g., 0x06 (6) | 65280 = 65286 = CoarseLocationUpdate
            return Pair(b2 or MEDIUM_FREQUENCY_BASE, offset)
        }
        
        // Low frequency message - next two bytes as signed short OR LOW_FREQUENCY_BASE
        if (data.size <= offset + 1) return null
        
        // Big-endian to signed short conversion:
        // 1. Read two bytes as unsigned (byte3=high, byte4=low) in network/big-endian order
        // 2. Combine into a 16-bit value: (byte3 << 8) | byte4
        // 3. Convert to signed short via .toShort().toInt() to sign-extend
        val byte3 = data[offset].toInt() and 0xFF
        val byte4 = data[offset + 1].toInt() and 0xFF
        offset += 2
        
        val shortValue = ((byte3 shl 8) or byte4).toShort().toInt()
        
        // This matches Lumiya: byteBuffer.getShort() | (-65536)
        // e.g., 0x0094 (148) | -65536 = -65388 = RegionHandshake
        return Pair(shortValue or LOW_FREQUENCY_BASE, offset)
    }
    
    /**
     * Get the message router for external handler registration
     * This allows AgentCircuit and other components to register handlers
     */
    fun getMessageRouter(): MessageRouter = messageRouter
    
    /**
     * Register a message handler using a lambda
     * This is a convenience method that wraps the lambda in a MessageRouter.Handler
     * Note: Uses runBlocking to ensure handler is registered before returning
     * This is critical for ensuring handlers are ready when packets arrive
     */
    fun registerHandler(messageId: Int, handler: (Int, ByteArray) -> Unit) {
        // Register in messageHandlers for diagnostics (using SAM conversion for functional interface)
        messageHandlers[messageId] = MessageHandler { msgId, data ->
            handler(msgId, data)
        }
        
        // Register with messageRouter synchronously using runBlocking
        // This ensures handler is ready before we return
        kotlinx.coroutines.runBlocking {
            messageRouter.registerHandler(messageId, object : MessageRouter.Handler {
                override fun handleMessage(messageId: Int, data: ByteArray): Boolean {
                    handler(messageId, data)
                    return true
                }
            })
        }
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, 
            "Registered handler for message $messageId (total: ${messageHandlers.size})")
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
            val seqNum = sequenceNumber.getAndIncrement()
            
            val header = ByteBuffer.allocate(6).order(ByteOrder.BIG_ENDIAN)
            header.put(flags.toByte())
            header.putInt(seqNum)
            header.put(0.toByte()) // Extra header byte
            
            // Encode message ID (Lumiya-style)
            val messageIdBytes = encodeMessageId(messageId)
            
            // Combine header, message ID, and payload
            val packet = header.array() + messageIdBytes + payload
            
            // Zero-code if requested
            val finalPacket = if (zerocoded) zeroEncode(packet) else packet
            
            // Get message name and hex preview for logging
            val messageName = getMessageName(messageId)
            val hexPreview = finalPacket.take(32).joinToString(" ") { "%02X".format(it) }
            
            // Send via DatagramChannel
            val buffer = ByteBuffer.wrap(finalPacket)
            val bytesWritten = datagramChannel?.write(buffer) ?: 0
            
            if (bytesWritten > 0) {
                packetsSent.incrementAndGet()
                bytesSent.addAndGet(bytesWritten.toLong())
                lastSendTime = System.currentTimeMillis()
                
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sent packet: ${finalPacket.size} bytes (ID: $messageId, reliable: $reliable)")
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Message: $messageName (seq: $seqNum)")
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Raw data: $hexPreview")
                
                // Record in packet history for debug reports
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.SEND_SUCCESS,
                    messageId = messageId,
                    data = finalPacket,
                    sequenceNumber = seqNum,
                    success = true
                )
                
                // Log to EnhancedPacketLogger for comprehensive tracking
                EnhancedPacketLogger.logPacketSent(
                    messageId = messageId,
                    messageName = messageName,
                    sequenceNumber = seqNum,
                    data = finalPacket,
                    flags = EnhancedPacketLogger.PacketFlags(
                        reliable = reliable,
                        resent = false,
                        zerocoded = zerocoded,
                        hasAcks = false
                    )
                )
            } else {
                // Record failed send
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                    messageId = messageId,
                    data = finalPacket,
                    sequenceNumber = seqNum,
                    success = false,
                    errorMessage = "DatagramChannel.write() returned 0 bytes"
                )
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "→ Send may have failed: 0 bytes written (ID: $messageId)")
            }
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "✗ Send error: ${e.message}")
            // Record failed send with error
            recordPacketEvent(
                type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                messageId = messageId,
                data = payload,
                sequenceNumber = sequenceNumber.get(),
                success = false,
                errorMessage = e.message
            )
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
            sequenceNumber = sequenceNumber.get(),
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
     * Record a packet event in the history for debugging.
     * This captures raw packet data including hex dumps for diagnostic purposes.
     */
    private fun recordPacketEvent(
        type: PacketHistoryEntry.PacketEventType,
        messageId: Int,
        data: ByteArray,
        sequenceNumber: Int,
        success: Boolean = true,
        errorMessage: String? = null
    ) {
        val entry = PacketHistoryEntry(
            timestamp = System.currentTimeMillis(),
            type = type,
            messageId = messageId,
            messageName = getMessageName(messageId),
            size = data.size,
            sequenceNumber = sequenceNumber,
            hexPreview = data.take(24).joinToString(" ") { "%02X".format(it) },
            success = success,
            errorMessage = errorMessage
        )
        
        recentPacketHistory.offer(entry)
        
        // Keep bounded size
        while (recentPacketHistory.size > maxPacketHistorySize) {
            recentPacketHistory.poll()
        }
    }
    
    /**
     * Extract the sequence number from raw packet data.
     * Packet header format: flags (1 byte), sequence (4 bytes big-endian), extra (1 byte)
     */
    private fun extractSequenceNumber(data: ByteArray): Int {
        if (data.size < 5) return -1
        return ((data[1].toInt() and 0xFF) shl 24) or
               ((data[2].toInt() and 0xFF) shl 16) or
               ((data[3].toInt() and 0xFF) shl 8) or
               (data[4].toInt() and 0xFF)
    }
    
    /**
     * Extract packet flags from raw packet data for EnhancedPacketLogger.
     */
    private fun extractPacketFlags(data: ByteArray): EnhancedPacketLogger.PacketFlags {
        if (data.isEmpty()) {
            return EnhancedPacketLogger.PacketFlags(false, false, false, false)
        }
        val flags = data[0].toInt() and 0xFF
        return EnhancedPacketLogger.PacketFlags(
            reliable = (flags and 0x40) != 0,
            resent = (flags and 0x20) != 0,
            zerocoded = (flags and 0x80) != 0,
            hasAcks = (flags and 0x10) != 0
        )
    }
    
    /**
     * Get human-readable message name from ID for debugging.
     */
    private fun getMessageName(messageId: Int): String {
        return when (messageId) {
            MessageIds.USE_CIRCUIT_CODE -> "UseCircuitCode"
            MessageIds.COMPLETE_AGENT_MOVEMENT -> "CompleteAgentMovement"
            MessageIds.LOGOUT_REQUEST -> "LogoutRequest"
            MessageIds.REGION_HANDSHAKE -> "RegionHandshake"
            MessageIds.REGION_HANDSHAKE_REPLY -> "RegionHandshakeReply"
            MessageIds.AGENT_THROTTLE -> "AgentThrottle"
            MessageIds.AGENT_MOVEMENT_COMPLETE -> "AgentMovementComplete"
            MessageIds.CHAT_FROM_SIMULATOR -> "ChatFromSimulator"
            MessageIds.IMPROVED_INSTANT_MESSAGE -> "ImprovedInstantMessage"
            MessageIds.OBJECT_UPDATE -> "ObjectUpdate"
            MessageIds.OBJECT_UPDATE_COMPRESSED -> "ObjectUpdateCompressed"
            MessageIds.IMPROVED_TERSE_OBJECT_UPDATE -> "ImprovedTerseObjectUpdate"
            MessageIds.AVATAR_ANIMATION -> "AvatarAnimation"
            MessageIds.AGENT_ANIMATION -> "AgentAnimation"
            MessageIds.COARSE_LOCATION_UPDATE -> "CoarseLocationUpdate"
            MessageIds.KILL_OBJECT -> "KillObject"
            MessageIds.PACKET_ACK -> "PacketAck"
            MessageIds.START_PING_CHECK -> "StartPingCheck"
            MessageIds.COMPLETE_PING_CHECK -> "CompletePingCheck"
            MessageIds.AGENT_UPDATE -> "AgentUpdate"
            else -> "Unknown(0x${messageId.toString(16).uppercase()})"
        }
    }
    
    /**
     * Get packet history for debugging.
     * Returns the list of recent packet events including raw hex data.
     */
    fun getPacketHistory(): List<PacketHistoryEntry> {
        return recentPacketHistory.toList()
    }
    
    /**
     * Get socket details for diagnostics.
     */
    fun getSocketDetails(): SocketDetails {
        return SocketDetails(
            localBindAddress = localBindAddress,
            localBindPort = localBindPort,
            remoteAddress = simIP,
            remotePort = simPort,
            isConnected = datagramChannel?.isConnected == true,
            isOpen = datagramChannel?.isOpen == true,
            connectionAttemptTime = connectionAttemptTime,
            lastSendAttemptTime = lastSendTime,
            lastReceiveTime = lastReceiveTime,
            lastConnectionError = lastConnectionError
        )
    }
}