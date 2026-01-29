package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.network.events.EventBus
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.network.events.ConnectionState
import com.linkpoint.network.events.CircuitEstablishedEvent
import com.linkpoint.network.events.MessageReceivedEvent
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.lumiya.LumiyaConstants
import com.linkpoint.protocol.types.putUUID
import com.linkpoint.utils.SessionLogRecorder
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
 * ## History & Design Decision
 * 
 * This class supersedes the original `UDPConnection.kt` implementation.
 * The "Fixed" suffix reflects the architectural improvements made:
 * - Better mobile network compatibility using NIO DatagramChannel
 * - Integrated MessageRouter for proper message dispatch
 * - EventBus integration for reactive updates
 * - Comprehensive diagnostic capabilities for debugging
 * 
 * The original UDPConnection.kt was removed from the codebase as it was
 * no longer used and this implementation covers all its functionality
 * with improved reliability and diagnostics.
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
 * - **Full Packet Logging**: Records COMPLETE packet data (all bytes) for every packet
 *   - Enables full protocol diagnosis by capturing entire packet contents
 *   - Both sent and received packets are logged with complete hex dumps
 *   - Stored in circular buffer (configurable via [DEFAULT_PACKET_HISTORY_SIZE])
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
 * - Configurable logging overhead via companion object constants
 * 
 * ## Usage
 * 
 * This class is instantiated by LinkpointApp and shared across managers.
 * Packet history and diagnostics are accessed via:
 * - `getPacketHistory()` - Recent packet events with FULL hex dumps
 * - `getSocketDetails()` - Connection state and timing
 * - `getDiagnostics()` - Overall connection diagnostics
 * - `getMessageStatistics()` - Message type counts and timing
 * 
 * ## Packet Data Logging
 * 
 * FULL raw packet data is logged for diagnostic purposes:
 * - Each sent packet: message ID, sequence number, size, COMPLETE hex dump
 * - Each received packet: message ID, sequence number, size, COMPLETE hex dump
 * - Failed operations include error messages
 * 
 * Full packet data is now captured by default for complete protocol diagnosis.
 * The number of packets kept in history is configurable via [DEFAULT_PACKET_HISTORY_SIZE].
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
        
        /** Maximum UDP datagram size - from LumiyaConstants */
        private val BUFFER_SIZE = LumiyaConstants.MAX_MESSAGE_SIZE
        
        /** Timeout for NIO selector operations - from LumiyaConstants (1 second idle interval) */
        private val SELECTOR_TIMEOUT_MS = LumiyaConstants.DEFAULT_IDLE_INTERVAL_MS
        
        /** 
         * Packet header size: flags (1) + sequence (4) + extra (1) = 6 bytes
         * This is constant across all SL UDP packets - from LumiyaConstants
         */
        private val PACKET_HEADER_SIZE = LumiyaConstants.PACKET_HEADER_SIZE
        
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
        
        // ==================== CONFIGURABLE PACKET LOGGING CONSTANTS ====================
        // These control memory usage and overhead for packet diagnostic features.
        // Full packet data is captured to enable complete protocol diagnosis.
        
        /**
         * Maximum number of packet events to keep in history.
         * Higher values provide more diagnostic data but use more memory.
         * Default of 50 provides sufficient history for debugging connection issues.
         */
        const val DEFAULT_PACKET_HISTORY_SIZE = 50
        
        /**
         * Whether to log full packet data or just a preview.
         * When true (default), all packet bytes are logged for complete diagnosis.
         * Set to false to reduce log verbosity in production.
         */
        const val LOG_FULL_PACKET_DATA = true
        
        // ==================== LUMIYA TIMING CONSTANTS ====================
        // These critical values come from Lumiya's proven mobile implementation
        
        /** Message timeout from Lumiya (5 seconds) */
        private val MESSAGE_TIMEOUT_MS = LumiyaConstants.MESSAGE_TIMEOUT_MS
        
        /** Maximum retries from Lumiya (3 retries) */
        private val MESSAGE_MAX_RETRIES = LumiyaConstants.MESSAGE_MAX_RETRIES
        
        /** Time before sending ping from Lumiya (10 seconds) */
        private val NEED_PING_TIMEOUT_MS = LumiyaConstants.NEED_PING_TIMEOUT_MS
        
        /** Unanswered pings before disconnect from Lumiya (3) */
        private val UNANSWERED_PINGS_DISCONNECT = LumiyaConstants.UNANSWERED_PINGS_DISCONNECT
        
        /**
         * Threshold for triggering reconnection due to consecutive send errors.
         * Android socket errors like "Operation not permitted" indicate socket invalidation.
         */
        const val CONSECUTIVE_ERROR_THRESHOLD = 5
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
    
    // ==================== RELIABLE MESSAGING SUPPORT ====================
    // Critical for SL protocol: ACKs must be sent for reliable packets or server will resend/drop connection
    
    /**
     * Queue of sequence numbers from reliable packets that need to be ACKed.
     * When we receive a packet with the reliable flag (0x40), we must acknowledge it.
     * ACKs can be piggy-backed on outgoing packets or sent as standalone PacketAck messages.
     */
    private val pendingAcksToSend = java.util.concurrent.ConcurrentLinkedQueue<Int>()
    
    /**
     * Track callbacks for reliable messages by sequence number.
     * This allows callers to receive notifications when their messages are acknowledged.
     * Critical for implementing circuit establishment state machine.
     */
    private val pendingCallbacks = java.util.concurrent.ConcurrentHashMap<Int, MessageCallbackInfo>()
    
    /**
     * Internal callback info with timeout handling.
     * Tracks message details for ACK/timeout callbacks.
     */
    private data class MessageCallbackInfo(
        val sequenceNumber: Int,
        val messageId: Int,
        val listener: MessageEventListener,
        var sentTime: Long,
        var retryCount: Int = 0
    )
    
    /**
     * Maximum number of ACKs to include in a single PacketAck message.
     * Based on SL protocol limits and Lumiya's implementation.
     */
    private val MAX_ACKS_PER_PACKET = 255
    
    /**
     * Interval for sending pending ACKs (milliseconds).
     * ACKs are typically sent every 100ms or piggy-backed on outgoing packets.
     */
    private val ACK_SEND_INTERVAL_MS = 100L
    
    /**
     * Last time ACKs were sent (for throttling standalone ACK packets).
     */
    private var lastAckSendTime = 0L
    
    /**
     * Job for the ACK sender coroutine.
     */
    private var ackSenderJob: Job? = null
    
    /**
     * Job for the timeout checker coroutine.
     */
    private var timeoutCheckerJob: Job? = null
    
    // ==================== SEND ERROR TRACKING FOR RECONNECTION ====================
    // Track consecutive send failures to detect socket invalidation (e.g., network change)
    
    /**
     * Number of consecutive send errors encountered.
     * When this exceeds [CONSECUTIVE_ERROR_THRESHOLD], we should trigger reconnection.
     */
    private val consecutiveSendErrors = AtomicInteger(0)
    
    /**
     * Error messages that indicate socket invalidation requiring reconnection.
     */
    private val SOCKET_INVALIDATION_ERRORS = listOf(
        "operation not permitted",
        "network is unreachable", 
        "connection refused",
        "broken pipe",
        "socket closed"
    )
    
    /**
     * Callback for notifying when reconnection is needed due to send failures.
     * Set by LinkpointApp to trigger the reconnection flow.
     */
    private var reconnectionCallback: (() -> Unit)? = null
    
    /**
     * Set a callback to be invoked when reconnection is needed.
     * This is called when consecutive send errors exceed the threshold.
     */
    fun setReconnectionCallback(callback: () -> Unit) {
        reconnectionCallback = callback
    }
    
    // ==================== PACKET HISTORY FOR RAW DATA LOGGING ====================
    // Critical for debugging: captures raw packet data with hex dumps.
    // This addresses the requirement: "we need raw input and output to understand"
    
    /** 
     * Circular buffer of recent packet events including raw hex data.
     * Used by DebugReportService to show packet history in debug reports.
     * Each entry contains: timestamp, type, message ID, size, sequence number, hex preview
     * Size is controlled by [DEFAULT_PACKET_HISTORY_SIZE]
     */
    private val recentPacketHistory = java.util.concurrent.ConcurrentLinkedQueue<PacketHistoryEntry>()
    
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
    
    // ==================== INTERNAL HANDLER REGISTRATION ====================
    // Register internal handlers that must be processed by UDPConnectionFixed itself.
    // This is called from init block to ensure handlers are registered before any packets arrive.
    
    init {
        registerInternalHandlers()
    }
    
    /**
     * Register internal message handlers for protocol messages that UDPConnectionFixed
     * must handle itself, such as PacketAck for reliable messaging.
     */
    private fun registerInternalHandlers() {
        // Register PacketAck handler - CRITICAL for reliable messaging
        // Without this, ACK callbacks are never invoked, breaking circuit establishment
        kotlinx.coroutines.runBlocking {
            messageRouter.registerHandler(MessageIds.PACKET_ACK, object : MessageRouter.Handler {
                override fun handleMessage(messageId: Int, data: ByteArray): Boolean {
                    return handlePacketAck(data)
                }
                override fun getPriority(): Int = Int.MAX_VALUE // Highest priority - process ACKs first
            })
        }
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, 
            "✓ Internal handlers registered (PacketAck)")
    }
    
    /**
     * Handle incoming PacketAck messages from the server.
     * 
     * PacketAck message format (High Frequency, ID = -5 / 0xFB):
     * - Header: flags (1) + seq (4) + extra (1) = 6 bytes
     * - Message ID: 1 byte (0xFB = -5)
     * - Packets block count: 1 byte
     * - For each packet: sequence number (4 bytes, little-endian)
     * 
     * @param data The raw packet data including header
     * @return true if handled successfully, false on error
     */
    private fun handlePacketAck(data: ByteArray): Boolean {
        try {
            // Use the same message ID decoder to find where the payload starts
            val decodeResult = decodeMessageIdLumiyaStyle(data, PACKET_HEADER_SIZE)
            if (decodeResult == null) {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                    "PacketAck: Failed to decode message ID")
                return false
            }
            
            val (messageId, payloadOffset) = decodeResult
            
            // Minimum size: payloadOffset + count byte (1)
            if (data.size < payloadOffset + 1) {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                    "PacketAck too short: ${data.size} bytes (payload at $payloadOffset)")
                return false
            }
            
            // Extract packets block count (first byte after message ID)
            val packetsBlockCount = data[payloadOffset].toInt() and 0xFF
            
            // Calculate expected size: payloadOffset + count (1) + (4 bytes per ACK)
            val expectedSize = payloadOffset + 1 + (packetsBlockCount * 4)
            if (data.size < expectedSize) {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                    "PacketAck truncated: ${data.size} bytes (expected $expectedSize for $packetsBlockCount ACKs)")
                return false
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "📎 Received PacketAck with $packetsBlockCount ACKed packets (messageId=$messageId)")
            
            // Parse each ACKed sequence number and invoke callbacks
            var offset = payloadOffset + 1 // Start after message ID + count byte
            for (i in 0 until packetsBlockCount) {
                if (offset + 4 > data.size) {
                    NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                        "PacketAck truncated at ACK $i (need 4 bytes, have ${data.size - offset})")
                    break
                }
                
                // Read 4-byte little-endian unsigned integer as sequence number
                val seqNum = ((data[offset].toInt() and 0xFF)) or
                            ((data[offset + 1].toInt() and 0xFF) shl 8) or
                            ((data[offset + 2].toInt() and 0xFF) shl 16) or
                            ((data[offset + 3].toInt() and 0xFF) shl 24)
                offset += 4
                
                // Process the ACK - this invokes any pending callbacks
                processReceivedAck(seqNum)
            }
            
            return true
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Error handling PacketAck: ${e.message}")
            e.printStackTrace()
            return false
        }
    }
    
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
        // Note: registerInternalHandlers() is called automatically by init block
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
            // Record connection attempt time and reset ALL statistics for new session
            connectionAttemptTime = System.currentTimeMillis()
            lastConnectionError = null
            recentPacketHistory.clear()
            
            // ==================== CRITICAL: RESET SEQUENCE NUMBER ====================
            // The Second Life protocol requires UseCircuitCode to be sent with sequence 0
            // to establish a new circuit. Without resetting, the server will ignore our
            // packets because the sequence numbers are from a previous session.
            // This was the root cause of "packets sent but none received" bugs.
            sequenceNumber.set(0)
            
            // Reset packet statistics for accurate per-session tracking
            packetsReceived.set(0)
            packetsSent.set(0)
            bytesReceived.set(0)
            bytesSent.set(0)
            messagesRouted.set(0)
            packetsResentCount.set(0)
            
            // Reset timing information
            lastReceiveTime = 0L
            lastSendTime = 0L
            lastAckSendTime = 0L
            
            // Clear pending ACKs from previous session (they're no longer valid)
            pendingAcksToSend.clear()
            
            // Clear pending callbacks (they won't be satisfied by new circuit)
            pendingCallbacks.clear()
            
            // Clear message statistics for accurate per-session tracking
            messageTypeCounts.clear()
            lastMessageTimes.clear()
            
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
            
            // Start ACK sender loop for reliable packet acknowledgments
            ackSenderJob = scope.launch {
                ackSenderLoop()
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ ACK sender loop started")
            
            // Start timeout checker for message timeouts
            timeoutCheckerJob = scope.launch {
                timeoutCheckerLoop()
            }
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ Timeout checker loop started")
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== UDP CONNECTION ESTABLISHED ===")
            
            // Send initial messages
            sendUseCircuitCode()
            // Note: CompleteAgentMovement is now sent after RegionHandshake to ensure correct sequence
            
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
                
                // Check if the selection key is still valid - it can become invalid after
                // network changes on mobile devices without the channel/selector being closed
                val key = selectionKey
                if (key == null || !key.isValid) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Selection key invalid (network may have changed), exiting loop")
                    break
                }
                
                // Check if the DatagramChannel is still connected.
                // For UDP, isConnected() reflects the state set by connect() and can become
                // false due to:
                // - Explicit disconnect() calls
                // - ICMP port unreachable messages (on some platforms)
                // - Other network error conditions
                // While UDP is connectionless at the protocol level, the channel connection
                // state helps detect when communication is no longer possible.
                if (!localChannel.isConnected) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "DatagramChannel.isConnected returned false, exiting loop")
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
                                val rawData = ByteArray(bytesRead)
                                buffer.get(rawData)
                                
                                // Check if packet is zero-coded and decode if needed
                                val isZerocoded = (rawData[0].toInt() and 0x80) != 0
                                val data = if (isZerocoded) zeroDecode(rawData) else rawData
                                
                                // Extract message info for logging
                                val messageId = extractMessageId(data)
                                val messageName = getMessageName(messageId)
                                val seqNum = extractSequenceNumber(data)
                                // Generate full hex dump of all packet bytes
                                val fullHexDump = data.joinToString(" ") { "%02X".format(it) }
                                
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "📦 PACKET RECEIVED #${packetsReceived.get()}: $bytesRead bytes${if (isZerocoded) " (decoded to ${data.size})" else ""}")
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Message: $messageName (ID: 0x${messageId.toString(16).uppercase()}, seq: $seqNum)")
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Full packet data: $fullHexDump")
                                
                                // Record in packet history for debug reports
                                recordPacketEvent(
                                    type = PacketHistoryEntry.PacketEventType.RECEIVE,
                                    messageId = messageId,
                                    data = data,
                                    sequenceNumber = seqNum,
                                    success = true
                                )
                                
                                // Extract packet flags once for reuse
                                val packetFlags = extractPacketFlags(data)
                                
                                // Log to EnhancedPacketLogger for comprehensive tracking
                                EnhancedPacketLogger.logPacketReceived(
                                    messageId = messageId,
                                    messageName = messageName,
                                    sequenceNumber = seqNum,
                                    data = data,
                                    flags = packetFlags,
                                    handlerFound = messageHandlers.containsKey(messageId)
                                )
                                
                                // Log to SessionLogRecorder for full session recording
                                SessionLogRecorder.logPacketReceived(
                                    messageId = messageId,
                                    messageName = messageName,
                                    sequenceNumber = seqNum,
                                    data = data,
                                    handlerFound = messageHandlers.containsKey(messageId)
                                )
                                
                                // ==================== RELIABLE PACKET ACK HANDLING ====================
                                // CRITICAL: If this packet has the reliable flag (0x40), we MUST ACK it.
                                // Failure to ACK reliable packets causes the server to resend, timeout, or drop connection.
                                if (packetFlags.reliable && seqNum >= 0) {
                                    pendingAcksToSend.offer(seqNum)
                                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "🔔 Queued ACK for reliable packet seq=$seqNum, pending ACKs: ${pendingAcksToSend.size}")
                                }
                                
                                // Publish message received event
                                EventBus.publish(MessageReceivedEvent(messageId, data))
                                
                                // Track message for statistics (fixes "RegionHandshake never received" bug)
                                trackMessageReceived(messageName)
                                
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
     * ACK sender loop - periodically sends pending ACKs to the simulator.
     * 
     * CRITICAL FOR RELIABLE MESSAGING:
     * The Second Life protocol requires clients to ACK reliable packets.
     * Without ACKs, the server will:
     * 1. Keep resending packets (wasting bandwidth)
     * 2. Eventually timeout the connection
     * 3. Not send subsequent reliable data (like chat, objects, terrain)
     * 
     * This follows Lumiya's implementation where ACKs are sent either:
     * - Piggy-backed on outgoing packets (optimization)
     * - As standalone PacketAck messages (when no other traffic)
     */
    private suspend fun ackSenderLoop() {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== ACK SENDER LOOP STARTED ===")
        
        while (_isConnected.value) {
            try {
                // Wait for ACK interval before checking for pending ACKs
                delay(ACK_SEND_INTERVAL_MS)
                
                // Send any pending ACKs
                if (pendingAcksToSend.isNotEmpty()) {
                    sendPendingAcks()
                }
            } catch (e: Exception) {
                if (_isConnected.value) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "ACK sender error: ${e.message}")
                }
            }
        }
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== ACK SENDER LOOP STOPPED ===")
    }
    
    /**
     * Timeout checker loop - periodically checks for message timeouts.
     * Runs every 1 second to check if any reliable messages have timed out.
     */
    private suspend fun timeoutCheckerLoop() {
        while (_isConnected.value && timeoutCheckerJob?.isActive == true) {
            try {
                checkMessageTimeouts()
                delay(1000L) // Check every second
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                    "Error in timeout checker: ${e.message}")
            }
        }
    }
    
    /**
     * Send pending ACKs as a PacketAck message.
     * 
     * PacketAck message format (High Frequency, ID = -5 / 0xFB):
     * - Header: flags (1) + seq (4) + extra (1)
     * - Message ID: 1 byte (0xFB = -5)
     * - Packets block count: 1 byte
     * - For each packet:
     *   - ID: 4 bytes (unsigned int, little-endian) - the sequence number being ACKed
     */
    private suspend fun sendPendingAcks() = withContext(Dispatchers.IO) {
        val acksToSend = mutableListOf<Int>()
        
        // Drain up to MAX_ACKS_PER_PACKET from the queue
        while (acksToSend.size < MAX_ACKS_PER_PACKET) {
            val ack = pendingAcksToSend.poll() ?: break
            acksToSend.add(ack)
        }
        
        if (acksToSend.isEmpty()) return@withContext
        
        try {
            // Build PacketAck message
            // Header (6 bytes) + MessageID (1 byte) + Count (1 byte) + (4 bytes per ACK)
            val packetSize = PACKET_HEADER_SIZE + 1 + 1 + (acksToSend.size * 4)
            val packet = ByteBuffer.allocate(packetSize).order(ByteOrder.BIG_ENDIAN)
            
            // Packet header
            val seqNum = sequenceNumber.incrementAndGet()
            packet.put(0x00.toByte()) // Flags: not reliable, not zerocoded
            packet.putInt(seqNum)
            packet.put(0x00.toByte()) // Extra byte
            
            // Message ID: PacketAck is high frequency -5 (0xFB as signed byte)
            packet.put(0xFB.toByte())
            
            // Block count - use bitwise AND to ensure unsigned byte representation
            packet.put((acksToSend.size and 0xFF).toByte())
            
            // Each ACKed sequence number (4 bytes, little-endian for the data)
            packet.order(ByteOrder.LITTLE_ENDIAN)
            for (ackSeq in acksToSend) {
                packet.putInt(ackSeq)
            }
            
            // Send the packet
            packet.flip()
            val bytes = ByteArray(packet.remaining())
            packet.get(bytes)
            
            val channel = datagramChannel ?: return@withContext
            val buffer = ByteBuffer.wrap(bytes)
            channel.write(buffer)
            
            packetsSent.incrementAndGet()
            bytesSent.addAndGet(bytes.size.toLong())
            lastSendTime = System.currentTimeMillis()
            lastAckSendTime = lastSendTime
            
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✅ Sent PacketAck for ${acksToSend.size} packets: $acksToSend (remaining pending: ${pendingAcksToSend.size})")
            
            // Record in packet history
            recordPacketEvent(
                type = PacketHistoryEntry.PacketEventType.SEND_SUCCESS,
                messageId = MessageIds.PACKET_ACK,
                data = bytes,
                sequenceNumber = seqNum,
                success = true
            )
            
            // Log ACKs sent to EnhancedPacketLogger for statistics
            acksToSend.forEach { ackSeq ->
                EnhancedPacketLogger.logAckSent(ackSeq)
            }
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Failed to send PacketAck: ${e.message}")
            // Re-queue the ACKs we couldn't send
            acksToSend.forEach { pendingAcksToSend.offer(it) }
        }
    }
    
    /**
     * Handle PacketAck message from server
     * 
     * PacketAck message format (High Frequency, ID = -5 / 0xFB):
     * - Header: flags (1) + seq (4) + extra (1) = 6 bytes
     * - Message ID: 1 byte (0xFB = -5)
     * - Packets block count: 1 byte
     * - For each packet being ACKed:
     *   - Sequence number: 4 bytes (unsigned int, little-endian)
     * 
     * @param data The complete packet data including header
     * @return true if handled successfully
     */
    
    /**
     * Process received ACK from server and invoke callbacks.
     * This is called when we receive a PacketAck message from the server.
     * 
     * @param sequenceNumber The sequence number being acknowledged
     */
    private fun processReceivedAck(sequenceNumber: Int) {
        // Check if we have a callback for this sequence number
        val callbackInfo = pendingCallbacks.remove(sequenceNumber)
        
        if (callbackInfo != null) {
            try {
                // Invoke the ACK callback
                callbackInfo.listener.onMessageAcknowledged(
                    sequenceNumber,
                    callbackInfo.messageId
                )
                
                NetworkLogger.log(
                    NetworkLogger.Level.DEBUG,
                    NetworkLogger.Category.UDP,
                    "✓ ACK callback invoked for seqNum=$sequenceNumber, messageId=${callbackInfo.messageId}"
                )
            } catch (e: Exception) {
                NetworkLogger.log(
                    NetworkLogger.Level.ERROR,
                    NetworkLogger.Category.UDP,
                    "Error in ACK callback for seqNum=$sequenceNumber: ${e.message}"
                )
            }
        } else {
            NetworkLogger.log(
                NetworkLogger.Level.VERBOSE,
                NetworkLogger.Category.UDP,
                "No callback registered for ACK seqNum=$sequenceNumber"
            )
        }
    }
    
    /**
     * Check for timeouts on pending reliable messages.
     * This should be called periodically to handle message timeouts.
     * Based on Lumiya's timeout and retry logic.
     */
    private suspend fun checkMessageTimeouts() {
        val now = System.currentTimeMillis()
        val timeout = MESSAGE_TIMEOUT_MS
        val maxRetries = MESSAGE_MAX_RETRIES
        
        // Process all pending callbacks
        pendingCallbacks.entries.removeIf { (seqNum, callbackInfo) ->
            val age = now - callbackInfo.sentTime
            
            if (age > timeout) {
                callbackInfo.retryCount++
                
                if (callbackInfo.retryCount > maxRetries) {
                    // Max retries exceeded - invoke timeout callback
                    try {
                        callbackInfo.listener.onMessageTimeout(
                            seqNum,
                            callbackInfo.messageId
                        )
                        
                        NetworkLogger.log(
                            NetworkLogger.Level.WARN,
                            NetworkLogger.Category.UDP,
                            "✗ Message timeout: seqNum=$seqNum, messageId=${callbackInfo.messageId}, retries=${callbackInfo.retryCount}"
                        )
                    } catch (e: Exception) {
                        NetworkLogger.log(
                            NetworkLogger.Level.ERROR,
                            NetworkLogger.Category.UDP,
                            "Error in timeout callback for seqNum=$seqNum: ${e.message}"
                        )
                    }
                    
                    return@removeIf true // Remove from pending callbacks
                } else {
                    // Resend the packet (Note: This would require tracking the packet data)
                    NetworkLogger.log(
                        NetworkLogger.Level.WARN,
                        NetworkLogger.Category.UDP,
                        "⚠ Message timeout, would resend: seqNum=$seqNum, retry ${callbackInfo.retryCount}"
                    )
                    
                    // Update sent time to avoid immediate timeout
                    callbackInfo.sentTime = now
                }
            }
            
            false // Keep in pending callbacks
        }
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

        // Log handler registration with EnhancedPacketLogger for debug reports
        val messageName = MessageIds.getMessageName(messageId)
        EnhancedPacketLogger.logHandlerRegistered(messageId, messageName)

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
        val messageId = MessageIds.USE_CIRCUIT_CODE
        
        // Build packet with header
        sendPacket(messageId, payload.array(), reliable = true)
    }
    
    /**
     * Send CompleteAgentMovement message
     * Uses mobile-optimized packet construction
     */
    suspend fun sendCompleteAgentMovement() {
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sending CompleteAgentMovement")
        
        // CompleteAgentMovement message format:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // - CircuitCode (4 bytes, little-endian)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        payload.put(agentId.asBytes())
        payload.put(sessionId.asBytes())
        payload.putInt(circuitCode)
        
        // Message ID for CompleteAgentMovement (low frequency message)
        val messageId = MessageIds.COMPLETE_AGENT_MOVEMENT
        
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
     * Send RequestMultipleObjects message to request full object data
     * 
     * This is used as a response to ObjectUpdateCached messages when the client
     * doesn't have the cached object data and needs the full update.
     * 
     * @param objectIds List of local object IDs to request
     * @param cacheMissType 0 = CRC mismatch/not cached, 1 = full request
     */
    suspend fun sendRequestMultipleObjects(objectIds: List<Int>, cacheMissType: Int = 0) {
        if (objectIds.isEmpty()) return
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, 
            "→ Sending RequestMultipleObjects for ${objectIds.size} objects")
        
        // RequestMultipleObjects message format:
        // AgentData:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // ObjectData (variable, one per object):
        // - CacheMissType (1 byte)
        // - ID (4 bytes, U32)
        
        // Message structure sizes
        val agentDataSize = 32    // AgentID (16) + SessionID (16)
        val objectCountSize = 1   // Object count byte
        val objectEntrySize = 5   // CacheMissType (1) + ID (4)
        
        val payloadSize = agentDataSize + objectCountSize + (objectIds.size * objectEntrySize)
        val payload = ByteBuffer.allocate(payloadSize).order(ByteOrder.LITTLE_ENDIAN)
        
        // AgentData block
        payload.put(agentId.asBytes())
        payload.put(sessionId.asBytes())
        
        // ObjectData count
        payload.put(objectIds.size.toByte())
        
        // ObjectData blocks
        for (objectId in objectIds) {
            payload.put(cacheMissType.toByte())
            payload.putInt(objectId)
        }
        
        sendPacket(MessageIds.REQUEST_MULTIPLE_OBJECTS, payload.array(), reliable = true)
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
        zerocoded: Boolean = false,
        listener: MessageEventListener? = null
    ) {
        if (!_isConnected.value) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Cannot send: not connected")
            return
        }
        
        try {
            // Build packet header (big-endian per SL protocol)
            val flags = (if (reliable) 0x40 else 0) or (if (zerocoded) 0x80 else 0)
            val seqNum = sequenceNumber.getAndIncrement()
            
            // Track callback if this is a reliable message with listener
            if (reliable && listener != null) {
                val callbackInfo = MessageCallbackInfo(
                    sequenceNumber = seqNum,
                    messageId = messageId,
                    listener = listener,
                    sentTime = System.currentTimeMillis(),
                    retryCount = 0
                )
                pendingCallbacks[seqNum] = callbackInfo
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "Registered callback for seqNum=$seqNum, messageId=$messageId")
            }
            
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
            
            // Get message name and full hex dump for logging
            val messageName = getMessageName(messageId)
            // Generate full hex dump of all packet bytes
            val fullHexDump = finalPacket.joinToString(" ") { "%02X".format(it) }
            
            // Send via DatagramChannel
            val buffer = ByteBuffer.wrap(finalPacket)
            val bytesWritten = datagramChannel?.write(buffer) ?: 0
            
            if (bytesWritten > 0) {
                packetsSent.incrementAndGet()
                bytesSent.addAndGet(bytesWritten.toLong())
                lastSendTime = System.currentTimeMillis()
                
                // Reset consecutive error counter on successful send
                consecutiveSendErrors.set(0)
                
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sent packet: ${finalPacket.size} bytes (ID: $messageId, reliable: $reliable)")
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Message: $messageName (seq: $seqNum)")
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "   Full packet data: $fullHexDump")
                
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
                
                // Log to SessionLogRecorder for full session recording
                SessionLogRecorder.logPacketSent(
                    messageId = messageId,
                    messageName = messageName,
                    sequenceNumber = seqNum,
                    data = finalPacket,
                    reliable = reliable
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
            
            // Track consecutive send errors for reconnection detection
            val errorCount = consecutiveSendErrors.incrementAndGet()
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, 
                "Consecutive send errors: $errorCount (threshold: $CONSECUTIVE_ERROR_THRESHOLD)")
            
            // Check for critical errors that indicate socket invalidation
            val errorMessage = e.message?.lowercase() ?: ""
            val isSocketInvalidationError = SOCKET_INVALIDATION_ERRORS.any { errorMessage.contains(it) }
            
            if (isSocketInvalidationError) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                    "⚠️ Critical socket error detected: ${e.message}")
                
                if (errorCount >= CONSECUTIVE_ERROR_THRESHOLD) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                        "🔄 Too many consecutive send errors ($errorCount), triggering reconnection")
                    
                    // Trigger reconnection via callback
                    reconnectionCallback?.invoke()
                    
                    // Reset counter after triggering reconnection
                    consecutiveSendErrors.set(0)
                }
            }
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
     * Zero-decode packet data.
     * 
     * Zero-coding is a run-length encoding for zeros used in SL protocol.
     * Format: 0x00 followed by count byte means that many zeros.
     * The first PACKET_HEADER_SIZE bytes (header) are not zero-coded.
     */
    private fun zeroDecode(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        var i = 0
        
        // Copy header unchanged (first PACKET_HEADER_SIZE bytes are not zero-coded)
        while (i < PACKET_HEADER_SIZE && i < data.size) {
            result.add(data[i])
            i++
        }
        
        // Decode body
        while (i < data.size) {
            if (data[i] == 0.toByte()) {
                // Zero run: next byte is count of zeros
                i++
                if (i < data.size) {
                    val count = data[i].toInt() and 0xFF
                    repeat(count) {
                        result.add(0.toByte())
                    }
                    i++
                }
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
        ackSenderJob?.cancel()
        timeoutCheckerJob?.cancel()
        
        // Clear pending ACKs since we're disconnecting
        pendingAcksToSend.clear()
        
        // Clear pending callbacks since we're disconnecting
        pendingCallbacks.clear()
        
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
                MessageIds.SOUND_TRIGGER -> "SOUND_TRIGGER"
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
     * Contains complete packet data for full protocol diagnosis.
     */
    data class PacketHistoryEntry(
        val timestamp: Long,
        val type: PacketEventType,
        val messageId: Int,
        val messageName: String,
        val size: Int,
        val sequenceNumber: Int,
        /** Complete hex dump of all packet bytes for full diagnosis */
        val fullHexDump: String,
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
     * Captures complete raw packet data as hex dump for full protocol diagnosis.
     */
    private fun recordPacketEvent(
        type: PacketHistoryEntry.PacketEventType,
        messageId: Int,
        data: ByteArray,
        sequenceNumber: Int,
        success: Boolean = true,
        errorMessage: String? = null
    ) {
        // Generate complete hex dump of all packet bytes for full diagnosis
        val fullHexDump = data.joinToString(" ") { "%02X".format(it) }
        
        val entry = PacketHistoryEntry(
            timestamp = System.currentTimeMillis(),
            type = type,
            messageId = messageId,
            messageName = getMessageName(messageId),
            size = data.size,
            sequenceNumber = sequenceNumber,
            fullHexDump = fullHexDump,
            success = success,
            errorMessage = errorMessage
        )
        
        recentPacketHistory.offer(entry)
        
        // Keep bounded size using configurable constant
        while (recentPacketHistory.size > DEFAULT_PACKET_HISTORY_SIZE) {
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
     * Uses the centralized MessageIds.getMessageName() for comprehensive coverage.
     */
    private fun getMessageName(messageId: Int): String {
        return MessageIds.getMessageName(messageId)
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