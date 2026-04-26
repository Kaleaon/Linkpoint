package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.network.events.EventBus
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.network.events.ConnectionState
import com.linkpoint.network.events.CircuitEstablishedEvent
import com.linkpoint.network.events.MessageReceivedEvent
import com.linkpoint.network.NetworkLogger
import com.linkpoint.protocol.core.AgentIdentity
import com.linkpoint.protocol.circuit.LinkpointConstants
import com.linkpoint.protocol.types.putUUID
import com.linkpoint.utils.SessionLogRecorder
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.net.InetSocketAddress
import java.net.StandardSocketOptions
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.UUID
import java.util.concurrent.atomic.AtomicReference
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
 * and event bus integration, following Linkpoint architecture patterns.
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
        
        /** Maximum UDP datagram size - from LinkpointConstants */
        private val BUFFER_SIZE = LinkpointConstants.MAX_MESSAGE_SIZE
        
        /** Timeout for NIO selector operations - from LinkpointConstants (1 second idle interval) */
        private val SELECTOR_TIMEOUT_MS = LinkpointConstants.DEFAULT_IDLE_INTERVAL_MS
        
        /** 
         * Packet header size: flags (1) + sequence (4) + extra (1) = 6 bytes
         * This is constant across all SL UDP packets - from LinkpointConstants
         */
        private val PACKET_HEADER_SIZE = LinkpointConstants.PACKET_HEADER_SIZE
        
        /**
         * Frequency bases for message ID encoding (matching SL protocol)
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
        // These critical values come from the reference viewer's proven mobile implementation
        
        /** Message timeout from the reference viewer (5 seconds) */
        private val MESSAGE_TIMEOUT_MS = LinkpointConstants.MESSAGE_TIMEOUT_MS
        
        /** Maximum retries from the reference viewer (3 retries) */
        private val MESSAGE_MAX_RETRIES = LinkpointConstants.MESSAGE_MAX_RETRIES
        
        /** Time before sending ping from the reference viewer (10 seconds) */
        private val NEED_PING_TIMEOUT_MS = LinkpointConstants.NEED_PING_TIMEOUT_MS
        
        /** Unanswered pings before disconnect from the reference viewer (3) */
        private val UNANSWERED_PINGS_DISCONNECT = LinkpointConstants.UNANSWERED_PINGS_DISCONNECT

        /**
         * Suppress identical RequestMultipleObjects bursts caused by duplicate
         * ObjectUpdateCached notifications arriving in the same render tick.
         */
        private const val REQUEST_MULTIPLE_OBJECTS_DEDUP_WINDOW_MS = 250L
        
        /**
         * Threshold for triggering reconnection due to consecutive send errors.
         * Android socket errors like "Operation not permitted" indicate socket invalidation.
         */
        const val CONSECUTIVE_ERROR_THRESHOLD = 5

        /**
         * Maximum time without ANY new received packet before we treat the
         * inbound side as dead and force a socket rebind. Set above the
         * unanswered-ping window (3 × 5s + slack) so the cheaper ping-based
         * watchdog gets first shot, but well below typical user-visible
         * timeouts so a stuck cellular NAT clears within ~45s.
         */
        const val INBOUND_DATA_STALL_MS = 45_000L

        /**
         * Cooldown after a socket rebind. Prevents tight reconnect loops if
         * the rebind itself doesn't restore inbound flow (e.g. carrier-side
         * outage rather than NAT eviction).
         */
        const val SOCKET_REBIND_COOLDOWN_MS = 30_000L

        /**
         * After an in-place socket reconnect, we expect the simulator to
         * answer our re-sent UseCircuitCode within this window — either with
         * a PacketAck or with any object/animation/coarse-location traffic
         * resumed from the live circuit.
         *
         * If `postReconnectPacketsReceived` stays at 0 past this window, the
         * simulator-side circuit is gone (typical 60s server-side timeout
         * already fired, or a cellular NAT mapping flap killed both
         * directions). Re-sending UseCircuitCode on yet another fresh source
         * port won't resurrect a circuit the simulator has already torn
         * down, so the inbound-stall watchdog would just thrash. Escalate to
         * the higher-level reconnect path (full re-login) instead.
         *
         * Sized just above the typical UseCircuitCode round-trip on poor
         * cellular (~5–8s seen in capture) so legitimate slow paths still
         * recover, but well under the 30s rebind cooldown that would
         * otherwise mask a dead circuit.
         */
        const val POST_RECONNECT_VERIFY_MS = 12_000L
    }
    
    // Connection parameters
    private var simIP: String = ""
    private var simPort: Int = 0
    private var circuitCode: Int = 0
    private var sessionId: UUID = UUID(0, 0)
    private var agentId: UUID = UUID(0, 0)
    
    // NIO components.
    //
    // @Volatile is required so the I/O thread (which re-reads these fields
    // every iteration) sees writes from reconnect()/disconnect() running on
    // CircuitDispatcher without us having to take a lock on every iteration.
    // Without @Volatile the JIT can hoist the load out of the loop and the
    // I/O thread would never observe a swapped selector/channel.
    @Volatile private var datagramChannel: DatagramChannel? = null
    @Volatile private var selector: Selector? = null
    @Volatile private var selectionKey: SelectionKey? = null
    
    // State
    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    // Ping tracking for connection health
    private val lastPingTime = AtomicLong(0)
    private val unansweredPings = AtomicInteger(0)
    private val nextPingId = AtomicInteger(0)

    // Message routing
    private val messageRouter = MessageRouter()
    
    // Coroutine scope. Used for off-thread suspending work (reconnect retry,
    // periodic AgentUpdate scheduler, EventBus publishes). The I/O thread owns
    // the DatagramChannel; nothing in this scope writes to the channel directly.
    private val scope = CoroutineScope(CircuitDispatcher.dispatcher + SupervisorJob())


    // Dedicated I/O thread for blocking receive operations.
    // Following Lumiya's SLConnection pattern: a single thread owns the
    // selector and the DatagramChannel. All reads, writes, ACK flushes,
    // pings, and timeout checks happen on this thread.
    @Volatile private var ioThread: Thread? = null

    // Agent update job
    private var agentUpdateJob: Job? = null
    private val movementLifecycleOwner = AtomicReference<String?>(null)
    
    // AgentUpdate cadence (docs/lumiya-port/README.md item 5).
    // Active cadence matches the SL viewer reference (~10 Hz). When the avatar
    // is idle (no movement controlFlags and no look-at change), back off to a
    // lower cadence — the simulator only needs occasional updates to keep the
    // circuit alive, and the saved packets are pure win on mobile data.
    private val AGENT_UPDATE_INTERVAL_MS = 100L
    private val AGENT_UPDATE_IDLE_INTERVAL_MS = 500L
    private val AGENT_UPDATE_IDLE_THRESHOLD_MS = 2_000L
    @Volatile private var lastAgentInputAtMs: Long = 0L

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

    /**
     * Rolling per-second buckets of inbound packet count and byte volume.
     * Cumulative counters can't tell you whether `bytesReceived = 30 MB` came
     * in steadily or as a burst followed by silence — these can.
     */
    private val inboundRateTracker = InboundRateTracker()
    
    /** Count of messages successfully routed to handlers */
    private val messagesRouted = AtomicInteger(0)
    
    /** Timestamp of last packet received (for timing diagnostics) */
    @Volatile private var lastReceiveTime = 0L

    /** Timestamp of last packet sent (for timing diagnostics) */
    @Volatile private var lastSendTime = 0L

    /**
     * Inbound-flow watchdog state. Catches "send-only zombie circuits" — the
     * 2026-04-25 Athanasia capture showed 4+ minutes of pings + AgentUpdate
     * going outbound while no application data ever arrived. Pings can keep
     * `lastReceiveTime` fresh on cellular even when the data path is dead
     * (NAT idle eviction often kills only large flows), so the existing
     * unanswered-ping watchdog never triggered. We track packets-received
     * growth instead and force a true socket rebind if it stalls.
     */
    private var lastInboundFlowSnapshot = 0
    private var lastInboundFlowGrowthTime = 0L
    private var lastSocketRebindTime = 0L

    /**
     * Total number of in-place socket reconnects performed during this circuit's
     * lifetime (UseCircuitCode reissued on a fresh source port). Distinct from
     * `connectAttemptCount`, which counts full circuit (re)establishments.
     */
    private val socketReconnectCount = AtomicInteger(0)

    /** Timestamp of the most recent socket reconnect (0 = never). */
    @Volatile private var lastSocketReconnectTime: Long = 0L

    /** Whether the most recent socket reconnect succeeded. */
    @Volatile private var lastSocketReconnectSucceeded: Boolean = true

    /**
     * Number of received packets observed AFTER the most recent socket
     * reconnect started. Used by the debug report and inbound watchdog to
     * tell "the new socket is healthy" from "the new socket is silent".
     */
    private val postReconnectPacketsReceived = AtomicInteger(0)
    
    /** Timestamp when connection was attempted (for connection duration) */
    private var connectionAttemptTime = 0L
    private val connectAttemptCount = AtomicInteger(0)
    
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
     * Outbound packet awaiting transmission on the I/O thread.
     * The Lumiya pattern: producers build the packet, drop it here, and call
     * `selector.wakeup()`. The single I/O thread owns all DatagramChannel writes.
     */
    private data class OutboundPacket(
        val data: ByteArray,
        val messageId: Int,
        val seqNum: Int,
        val reliable: Boolean,
        val zerocoded: Boolean
    )

    private val outgoingQueue = java.util.concurrent.ConcurrentLinkedQueue<OutboundPacket>()

    /**
     * Track callbacks for reliable messages by sequence number.
     * This allows callers to receive notifications when their messages are acknowledged.
     * Critical for implementing circuit establishment state machine.
     */
    private val pendingCallbacks = java.util.concurrent.ConcurrentHashMap<Int, MessageCallbackInfo>()

    /**
     * Sequence number used for the most recent UseCircuitCode send, or -1 if not sent yet.
     * The login handshake's PacketAck handler uses this to know which ACK signals that
     * the circuit is established so CompleteAgentMovement can be sent.
     */
    @Volatile
    var useCircuitCodeSequence: Int = -1
        private set
    
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
     * Based on SL protocol limits .
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

    /**
     * Network-state transitions emitted by the watchdog and the in-line socket
     * reconnect path. Drives [NetworkStateManager] bookkeeping so the live
     * diagnostic counter (`Reconnect Count`) reflects reality.
     *
     * Without this, the only signal a higher layer got was the hard
     * [reconnectionCallback], which only fires AFTER socket reconnect has
     * already failed and we've torn the circuit down — way too late to
     * surface "Reconnecting…" to the user or to bump the diagnostic.
     */
    enum class NetworkStateTransition { RECONNECTING, CONNECTED, FAULTED }

    private var networkStateListener: ((NetworkStateTransition) -> Unit)? = null

    /**
     * Set a listener for fine-grained network-state transitions emitted by the
     * watchdog. Single-listener; last writer wins. LinkpointApp wires this to
     * `protocol.stateManager.setStatus(...)`.
     */
    fun setNetworkStateListener(listener: (NetworkStateTransition) -> Unit) {
        networkStateListener = listener
    }

    private fun emitNetworkState(transition: NetworkStateTransition) {
        try {
            networkStateListener?.invoke(transition)
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "networkStateListener threw on $transition: ${e.message}")
        }
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

    // Keep a small LRU-ish set of recent RequestMultipleObjects signatures so we
    // can suppress immediate duplicates and avoid flooding the simulator.
    private val recentRequestMultipleObjects = object : LinkedHashMap<String, Long>(64, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Long>?): Boolean = size > 64
    }
    private val requestMultipleObjectsLock = Any()
    
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
        // Register PacketAck handler - CRITICAL for reliable messaging.
        // Without this, ACK callbacks are never invoked, breaking circuit establishment.
        messageRouter.registerHandler(MessageIds.PACKET_ACK, object : MessageRouter.Handler {
            override fun handleMessage(messageId: Int, data: ByteArray): Boolean {
                return handlePacketAck(data)
            }
            override fun getPriority(): Int = Int.MAX_VALUE // Highest priority - process ACKs first
        })
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
            val decodeResult = decodeMessageIdSLProtocol(data, PACKET_HEADER_SIZE)
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

    private fun outboundIdentity(context: String): AgentIdentity =
        AgentIdentity(
            agentId = agentId,
            sessionId = sessionId,
            circuitCode = circuitCode
        ).requireValid(context)

    /**
     * Guards ownership of movement/reliable-send lifecycle for shared circuits.
     * Only one owner should drive send loops at a time.
     */
    fun tryAcquireMovementLifecycle(ownerId: String): Boolean {
        require(ownerId.isNotBlank()) { "ownerId must not be blank" }
        val acquired = movementLifecycleOwner.compareAndSet(null, ownerId) || movementLifecycleOwner.get() == ownerId
        if (!acquired) {
            NetworkLogger.log(
                NetworkLogger.Level.WARN,
                NetworkLogger.Category.UDP,
                "Movement lifecycle acquire denied for $ownerId; owner=${movementLifecycleOwner.get()}"
            )
        } else {
            NetworkLogger.log(
                NetworkLogger.Level.DEBUG,
                NetworkLogger.Category.UDP,
                "Movement lifecycle owned by ${movementLifecycleOwner.get()}"
            )
        }
        return acquired
    }

    fun getMovementLifecycleOwner(): String? = movementLifecycleOwner.get()

    fun releaseMovementLifecycle(ownerId: String) {
        if (!movementLifecycleOwner.compareAndSet(ownerId, null)) {
            NetworkLogger.log(
                NetworkLogger.Level.WARN,
                NetworkLogger.Category.UDP,
                "Movement lifecycle release ignored for $ownerId; owner=${movementLifecycleOwner.get()}"
            )
        } else {
            NetworkLogger.log(
                NetworkLogger.Level.DEBUG,
                NetworkLogger.Category.UDP,
                "Movement lifecycle released by $ownerId"
            )
        }
    }

    fun getConnectAttemptCount(): Int = connectAttemptCount.get()
    
    /**
     * Connect to the simulator
     */
    suspend fun connect(): Boolean = withContext(CircuitDispatcher.dispatcher) {
        try {
            connectAttemptCount.incrementAndGet()
            // Record connection attempt time and reset ALL statistics for new session
            connectionAttemptTime = System.currentTimeMillis()
            lastConnectionError = null
            recentPacketHistory.clear()
            
            // Reset the sequence counter for a new circuit. The first packet
            // sent (UseCircuitCode) will then go out at seq=1 via incrementAndGet,
            // matching Lumiya's SLCircuit pattern.
            sequenceNumber.set(0)
            
            // Reset inbound-flow watchdog state so the first observation
            // after handshake establishes the baseline (rather than tripping
            // off an old snapshot from a previous circuit).
            lastInboundFlowSnapshot = 0
            lastInboundFlowGrowthTime = 0L
            lastSocketRebindTime = 0L

            // Reset per-circuit reconnect tracking — a fresh connect() is a
            // new circuit, so the in-place reconnect history doesn't apply.
            socketReconnectCount.set(0)
            lastSocketReconnectTime = 0L
            lastSocketReconnectSucceeded = true
            postReconnectPacketsReceived.set(0)

            // Reset packet statistics for accurate per-session tracking
            packetsReceived.set(0)
            packetsSent.set(0)
            bytesReceived.set(0)
            bytesSent.set(0)
            messagesRouted.set(0)
            packetsResentCount.set(0)
            inboundRateTracker.reset()
            
            // Reset timing information
            val now = System.currentTimeMillis()
            lastReceiveTime = now
            lastSendTime = 0L
            lastAckSendTime = 0L
            lastPingTime.set(now)
            unansweredPings.set(0)
            
            // Clear pending ACKs from previous session (they're no longer valid)
            pendingAcksToSend.clear()

            // Clear queued outbound packets — old session's bytes are stale.
            outgoingQueue.clear()

            // Clear pending callbacks (they won't be satisfied by new circuit)
            pendingCallbacks.clear()
            
            // Clear message statistics for accurate per-session tracking
            messageTypeCounts.clear()
            lastMessageTimes.clear()
            
            // Start EnhancedPacketLogger session for comprehensive tracking
            EnhancedPacketLogger.startSession()

            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== INITIATING FIXED UDP CONNECTION ===")

            // CRITICAL: Force IPv4 stack, matching Lumiya's SLConnection() constructor.
            // Second Life simulators only listen on IPv4. On cellular networks, Android may
            // create a dual-stack (IPv6) socket by default. When connecting to an IPv4
            // simulator address from a dual-stack socket over cellular CGNAT, outgoing packets
            // are sent as IPv4-mapped IPv6 but return packets may not be routed back correctly
            // through the carrier's NAT. This causes "packets sent but none received."
            // Lumiya proves this works on cellular by forcing IPv4 before any socket creation.
            System.setProperty("java.net.preferIPv4Stack", "true")
            System.setProperty("java.net.preferIPv6Addresses", "false")

            val address = InetSocketAddress(simIP, simPort)

            // Create and configure DatagramChannel — force IPv4 (StandardProtocolFamily.INET)
            // to match Lumiya's behavior. Falls back to default open() on older Android APIs.
            datagramChannel = try {
                DatagramChannel.open(java.net.StandardProtocolFamily.INET)
            } catch (e: Exception) {
                // Fallback for API < 26 — system properties above still ensure IPv4
                DatagramChannel.open()
            }
            datagramChannel!!.apply {
                configureBlocking(false)
                setOption(StandardSocketOptions.SO_RCVBUF, 65536)
                setOption(StandardSocketOptions.SO_SNDBUF, 65536)
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
            
            // Start receive loop on a DEDICATED I/O thread.
            // CRITICAL FIX: Previously this ran as a coroutine that did
            // withContext(CircuitDispatcher.dispatcher) { select() }, which BLOCKED
            // CircuitDispatcher's single thread. This starved the ACK sender, ping sender,
            // and AgentUpdate sender since they also run on CircuitDispatcher.
            // Now the I/O thread is independent, leaving CircuitDispatcher free.
            ioThread = Thread({
                receiveLoopBlocking()
            }, "SLCircuitIO").apply {
                isDaemon = true
                start()
            }

            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "✓ Receive loop started on dedicated I/O thread")
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
     * Blocking receive loop that runs on a dedicated thread (Lumiya pattern).
     *
     * This replaces the coroutine-based receiveLoop that used withContext(CircuitDispatcher)
     * for NIO select(). The old approach blocked CircuitDispatcher's single thread,
     * permanently starving ACK sender, ping sender, and AgentUpdate sender.
     *
     * Like Lumiya's SLConnection.run(), this is a simple blocking loop:
     * 1. select() with timeout (blocks THIS thread, not CircuitDispatcher)
     * 2. Read packet if available
     * 3. Process packet: decode, handle ACKs, dispatch to handlers
     * 4. Periodically send pending ACKs and check ping health
     */
    private fun receiveLoopBlocking() {
        val buffer = ByteBuffer.allocate(BUFFER_SIZE)

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== BLOCKING RECEIVE LOOP STARTED (dedicated I/O thread) ===")
        var disconnectReason: String? = null
        var lastAckCheckTime = System.currentTimeMillis()
        var lastTimeoutCheckTime = System.currentTimeMillis()

        // Tracks consecutive iterations where selector/channel/key looked
        // bad. A reconnect() running on CircuitDispatcher swaps these fields
        // sequentially with @Volatile writes, so this thread can briefly see
        // an inconsistent state mid-swap. Exiting the loop and triggering a
        // second reconnect for that transient was the dead-socket-after-
        // reconnect bug.
        var consecutiveBadStateIters = 0
        // ~20 iterations × tiny sleep ≈ 1 s, well over reconnect's typical
        // tens-of-ms duration. If the state is still bad after that we treat
        // it as real and break for recovery.
        val badStateBreakThreshold = 20

        while (_isConnected.value && !Thread.currentThread().isInterrupted) {
            try {
                val localSelector = selector
                val localChannel = datagramChannel

                // Selector being null/closed is the only true "this loop
                // can't run any more" condition — Selector lifetime is owned
                // by connect()/disconnect(). reconnect() never closes the
                // selector, so we treat null/closed selector as a hard exit.
                if (localSelector == null) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP, "Selector is null, exiting loop")
                    disconnectReason = "Selector missing"
                    break
                }
                if (!localSelector.isOpen) {
                    NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Selector closed, exiting loop")
                    disconnectReason = "Selector closed"
                    break
                }

                // Channel and selectionKey, in contrast, are owned by the
                // circuit lifetime and are swapped on every reconnect. A
                // brief null/closed/cancelled view here just means reconnect
                // is mid-swap; sleep a moment and re-read.
                val key = selectionKey
                if (localChannel == null || !localChannel.isOpen ||
                    key == null || !key.isValid) {
                    consecutiveBadStateIters++
                    if (consecutiveBadStateIters >= badStateBreakThreshold) {
                        NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                            "Channel/key still invalid after $consecutiveBadStateIters iterations — exiting loop for recovery")
                        disconnectReason = "Channel/key persistently invalid"
                        break
                    }
                    // Short backoff so reconnect (running on a different
                    // thread) gets CPU time to publish the new state. We
                    // don't call select() because the key is invalid; that
                    // would either no-op or throw.
                    try {
                        Thread.sleep(50)
                    } catch (ie: InterruptedException) {
                        // disconnect() interrupts us; the while-condition
                        // re-check on the next iteration handles teardown.
                        Thread.currentThread().interrupt()
                    }
                    continue
                }
                consecutiveBadStateIters = 0

                // DatagramChannel.isConnected is intentionally NOT checked here: it only
                // reflects whether connect() was called on the channel, not real network
                // liveness. On mobile (LTE tower hand-off, ICMP port-unreachable on some
                // Linux stacks) it can transiently report false while the socket is still
                // usable. Real disconnects manifest as channel close (guarded above) or
                // as read()/select() exceptions (caught below).

                // Drain any packets queued by producers since the last cycle.
                // sendPacket() calls selector.wakeup() after enqueueing, so this
                // runs without waiting for the select timeout when traffic exists.
                drainOutgoingQueueOnIOThread()

                // BLOCKING select on THIS thread (not on CircuitDispatcher!)
                // This is the key fix: the select() call blocks only the I/O thread,
                // leaving the rest of the app free.
                val readyKeys = localSelector.select(SELECTOR_TIMEOUT_MS)

                if (readyKeys > 0) {
                    val iterator = localSelector.selectedKeys().iterator()
                    while (iterator.hasNext()) {
                        val selKey = iterator.next()
                        iterator.remove()

                        if (selKey.isReadable) {
                            buffer.clear()

                            // BLOCKING read on THIS thread
                            val bytesRead = localChannel.read(buffer)

                            if (bytesRead > 0) {
                                packetsReceived.incrementAndGet()
                                postReconnectPacketsReceived.incrementAndGet()
                                bytesReceived.addAndGet(bytesRead.toLong())
                                inboundRateTracker.record(bytesRead)
                                lastReceiveTime = System.currentTimeMillis()
                                unansweredPings.set(0)

                                buffer.flip()
                                val rawData = ByteArray(bytesRead)
                                buffer.get(rawData)

                                // Zero-decode if needed
                                val isZerocoded = (rawData[0].toInt() and 0x80) != 0
                                val data = if (isZerocoded) zeroDecode(rawData) else rawData

                                // Extract message info
                                val messageId = extractMessageId(data)
                                val messageName = getMessageName(messageId)
                                val seqNum = extractSequenceNumber(data)

                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                                    "📦 PACKET RECEIVED #${packetsReceived.get()}: $bytesRead bytes${if (isZerocoded) " (decoded to ${data.size})" else ""}")
                                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                                    "   Message: $messageName (ID: 0x${messageId.toString(16).uppercase()}, seq: $seqNum)")

                                // Record in packet history
                                recordPacketEvent(
                                    type = PacketHistoryEntry.PacketEventType.RECEIVE,
                                    messageId = messageId,
                                    data = data,
                                    sequenceNumber = seqNum,
                                    success = true
                                )

                                val packetFlags = extractPacketFlags(data)

                                // Log to enhanced logger
                                EnhancedPacketLogger.logPacketReceived(
                                    messageId = messageId,
                                    messageName = messageName,
                                    sequenceNumber = seqNum,
                                    data = data,
                                    flags = packetFlags,
                                    handlerFound = messageHandlers.containsKey(messageId)
                                )

                                // Log to session recorder
                                SessionLogRecorder.logPacketReceived(
                                    messageId = messageId,
                                    messageName = messageName,
                                    sequenceNumber = seqNum,
                                    data = data,
                                    handlerFound = messageHandlers.containsKey(messageId)
                                )

                                // Queue ACK for reliable packets
                                if (packetFlags.reliable && seqNum >= 0) {
                                    pendingAcksToSend.offer(seqNum)
                                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                                        "🔔 Queued ACK for reliable packet seq=$seqNum, pending ACKs: ${pendingAcksToSend.size}")
                                }

                                // Track message stats
                                trackMessageReceived(messageName)

                                // Process appended ACKs ONLY when the flag is set (0x10)
                                // CRITICAL FIX: Previously this was called unconditionally,
                                // which read garbage from the end of normal packets as ACK data
                                if (packetFlags.hasAcks) {
                                    processAppendedAcks(data)
                                }

                                // SYNCHRONOUS message dispatch from I/O thread.
                                // This follows Lumiya's pattern where HandleMessage() is called
                                // directly from the circuit's receive processing, not queued.
                                dispatchMessageDirect(messageId, data)
                            }
                        }
                    }
                }

                // Idle processing — Lumiya's ProcessIdle() pattern. Runs between
                // select() calls on the same thread that owns the channel, so
                // there is no contention with the read path.
                val now = System.currentTimeMillis()
                if (now - lastAckCheckTime >= ACK_SEND_INTERVAL_MS) {
                    if (pendingAcksToSend.isNotEmpty()) {
                        sendPendingAcksFromIOThread()
                    }
                    lastAckCheckTime = now
                }
                if (now - lastTimeoutCheckTime >= 1000L) {
                    checkPingHealth()
                    checkInboundFlow()
                    checkPostReconnectSilence()
                    checkMessageTimeouts()
                    lastTimeoutCheckTime = now
                }

            } catch (e: java.nio.channels.ClosedSelectorException) {
                // The selector was closed under us. With the Lumiya-style
                // reconnect (which keeps the selector open), this should
                // only happen on a real disconnect. If we're still nominally
                // connected, set disconnectReason so the post-loop recovery
                // path triggers a reconnect — previously we just logged and
                // broke, leaving _isConnected=true with no I/O thread, which
                // is the dead-socket-after-reconnect bug.
                if (_isConnected.value) {
                    NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                        "Selector closed while connected — recovering")
                    disconnectReason = "Selector closed unexpectedly"
                }
                break
            } catch (e: java.nio.channels.AsynchronousCloseException) {
                // The DatagramChannel was closed (typically reconnect()).
                // The next iteration will read the new channel via the
                // @Volatile field and resume normally.
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "Channel closed asynchronously — picking up replacement next iteration")
            } catch (e: java.nio.channels.ClosedByInterruptException) {
                // Same idea — happens if a blocking I/O operation is
                // interrupted. Treat as transient; loop will re-check state.
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "I/O interrupted — re-checking selector/channel")
            } catch (e: java.nio.channels.ClosedChannelException) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "Channel closed — re-checking selector/channel")
            } catch (e: java.nio.channels.CancelledKeyException) {
                // Stale key — reconnect cancelled it. The next iteration
                // re-reads selectionKey and sees the new one.
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "Selection key cancelled — re-checking next iteration")
            } catch (e: Exception) {
                if (_isConnected.value) {
                    val msg = e.message ?: e.javaClass.simpleName
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                        "✗ Receive error: $msg")
                }
            }
        }

        if (_isConnected.value && disconnectReason != null) {
            NetworkLogger.log(
                NetworkLogger.Level.WARN,
                NetworkLogger.Category.UDP,
                "Receive loop ended unexpectedly: $disconnectReason - attempting socket reconnect"
            )
            emitNetworkState(NetworkStateTransition.RECONNECTING)
            // Attempt socket reconnect from a coroutine (can't call suspend from plain thread)
            scope.launch {
                val reconnected = try { reconnect() } catch (e: Exception) { false }
                if (reconnected) {
                    emitNetworkState(NetworkStateTransition.CONNECTED)
                } else {
                    NetworkLogger.log(
                        NetworkLogger.Level.ERROR,
                        NetworkLogger.Category.UDP,
                        "Socket reconnect failed after receive loop exit, triggering full reconnection"
                    )
                    emitNetworkState(NetworkStateTransition.FAULTED)
                    reconnectionCallback?.invoke()
                    disconnect()
                }
            }
        }

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "=== BLOCKING RECEIVE LOOP STOPPED ===")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Total packets: ${packetsReceived.get()}")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Total bytes: ${bytesReceived.get()}")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "Messages routed: ${messagesRouted.get()}")
    }

    /**
     * Synchronous message dispatch called directly from the I/O thread.
     * Follows Lumiya's pattern where messages are handled immediately on the
     * circuit thread.
     */
    private fun dispatchMessageDirect(messageId: Int, data: ByteArray) {
        try {
            // Internal PacketAck handling (processes ACK callbacks for reliable messaging)
            if (messageId == MessageIds.PACKET_ACK) {
                handlePacketAck(data)
            }

            // Dispatch to all registered handlers (from LinkpointApp and managers)
            val handler = messageHandlers[messageId]
            if (handler != null) {
                try {
                    handler.handle(messageId, data)
                } catch (e: Exception) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                        "Handler error for ${getMessageName(messageId)}: ${e.message}")
                }
                messagesRouted.incrementAndGet()
            } else if (messageId != MessageIds.PACKET_ACK) {
                // Only log unhandled messages if not PacketAck (which is handled internally)
                NetworkLogger.log(NetworkLogger.Level.VERBOSE, NetworkLogger.Category.UDP,
                    "No handler for ${getMessageName(messageId)} (ID: $messageId)")
            }

            // Also try to route through messageRouter for any handlers only registered there
            try {
                messageRouter.routeMessageSync(messageId, data)
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                    "Router error for ${getMessageName(messageId)}: ${e.message}")
            }
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Fatal dispatch error for ${getMessageName(messageId)}: ${e.message}")
        }
    }

    /**
     * Drain queued ACKs into a single PacketAck and write it directly to the
     * DatagramChannel. Runs on the I/O thread from the receive loop's idle pass.
     */
    private fun sendPendingAcksFromIOThread() {
        val acksToSend = mutableListOf<Int>()

        while (acksToSend.size < MAX_ACKS_PER_PACKET) {
            val ack = pendingAcksToSend.poll() ?: break
            acksToSend.add(ack)
        }

        if (acksToSend.isEmpty()) return

        try {
            val packetSize = PACKET_HEADER_SIZE + 1 + 1 + (acksToSend.size * 4)
            val packet = ByteBuffer.allocate(packetSize).order(ByteOrder.BIG_ENDIAN)

            val seqNum = sequenceNumber.incrementAndGet()
            packet.put(0x00.toByte()) // Flags: not reliable, not zerocoded
            packet.putInt(seqNum)
            packet.put(0x00.toByte()) // Extra byte

            // Message ID: PacketAck (0xFB)
            packet.put(0xFB.toByte())

            // Block count
            packet.put((acksToSend.size and 0xFF).toByte())

            // ACKed sequence numbers (little-endian)
            packet.order(ByteOrder.LITTLE_ENDIAN)
            for (ackSeq in acksToSend) {
                packet.putInt(ackSeq)
            }

            packet.flip()
            val bytes = ByteArray(packet.remaining())
            packet.get(bytes)

            val channel = datagramChannel ?: return
            val buffer = ByteBuffer.wrap(bytes)
            channel.write(buffer)

            packetsSent.incrementAndGet()
            bytesSent.addAndGet(bytes.size.toLong())
            lastSendTime = System.currentTimeMillis()
            lastAckSendTime = lastSendTime

            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "✅ Sent PacketAck for ${acksToSend.size} packets: $acksToSend (remaining: ${pendingAcksToSend.size})")

            acksToSend.forEach { ackSeq ->
                EnhancedPacketLogger.logAckSent(ackSeq)
            }

        } catch (e: java.nio.channels.AsynchronousCloseException) {
            // Channel was swapped under us by reconnect(). Re-queue the ACKs;
            // the new channel will pick them up next idle pass.
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "PacketAck deferred — channel swapped during reconnect")
            acksToSend.forEach { pendingAcksToSend.offer(it) }
        } catch (e: java.nio.channels.ClosedChannelException) {
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "PacketAck deferred — channel closed during reconnect")
            acksToSend.forEach { pendingAcksToSend.offer(it) }
        } catch (e: Exception) {
            // Surface the exception class when message is null so the log
            // doesn't read "Failed to send PacketAck: null".
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Failed to send PacketAck from I/O thread: ${e.message ?: e.javaClass.simpleName}")
            acksToSend.forEach { pendingAcksToSend.offer(it) }
        }
    }

    /**
     * Check whether the connection needs a ping or should disconnect due to inactivity.
     *
     * IMPORTANT: The disconnect check runs BEFORE sending a new ping to avoid a race
     * condition where sendStartPingCheck() increments unansweredPings and the disconnect
     * threshold is immediately hit in the same call, giving the server zero time to respond.
     */
    private fun checkPingHealth() {
        // Check for connection death FIRST, based on pings already sent
        if (unansweredPings.get() >= UNANSWERED_PINGS_DISCONNECT) {
            NetworkLogger.log(
                NetworkLogger.Level.WARN,
                NetworkLogger.Category.UDP,
                "No response from server (${unansweredPings.get()} unanswered pings) — circuit is dead, escalating to full re-login"
            )
            // Escalate directly to the higher-level reconnection callback
            // (auto re-login). Previously we tried socket-rebind first, but
            // the 2026-04-25 Athanasia capture shows that pattern produces
            // a silent loop on cellular: every rebind "succeeds" at the
            // socket layer but no packets ever come back because the
            // simulator-side circuit has already timed us out. The LLUDP
            // protocol has no in-band recovery for that — only a fresh
            // login can resurrect the session. This matches Lumiya's
            // `ProcessTimeout()` (3 unanswered pings → tear down circuit
            // → full re-login via `SLGridConnection.Reconnect()`).
            //
            // The inbound-stall watchdog (`checkInboundDataStall`) and
            // post-reconnect-silence watchdog (`checkPostReconnectSilence`)
            // still call `reconnect()` for transient socket failures — we
            // only short-circuit the unanswered-ping path here, since that
            // signal specifically means the simulator is no longer hearing
            // us (or we're no longer hearing it).
            emitNetworkState(NetworkStateTransition.FAULTED)
            scope.launch {
                try {
                    reconnectionCallback?.invoke()
                } catch (e: Exception) {
                    NetworkLogger.log(
                        NetworkLogger.Level.ERROR,
                        NetworkLogger.Category.UDP,
                        "reconnectionCallback threw during ping-watchdog escalation: ${e.message}"
                    )
                }
                disconnect()
            }
            return
        }

        // Then check if we need to send a new ping
        val now = System.currentTimeMillis()
        val timeSinceReceive = now - lastReceiveTime
        val timeSincePing = now - lastPingTime.get()

        if (timeSinceReceive > NEED_PING_TIMEOUT_MS &&
            timeSincePing > LinkpointConstants.PING_INTERVAL_MS) {
            sendStartPingCheck()
        }
    }

    /**
     * Inbound-flow watchdog (independent of the unanswered-ping check).
     *
     * The unanswered-ping watchdog only catches "no response of any kind".
     * On cellular networks the simulator's small ping-replies sometimes
     * survive while the larger data flow (ObjectUpdate, AgentGroupDataUpdate,
     * IM, friend status) is silently dropped — typically because the carrier
     * NAT timed out the high-volume path or the per-flow rate-limiter capped
     * the sim → client direction. The 2026-04-25 Athanasia capture showed
     * exactly that: 4.5 minutes with `Unanswered Pings: 0` and 89 received
     * packets total, all in the first 2 seconds.
     *
     * If [packetsReceived] hasn't grown in [INBOUND_DATA_STALL_MS] AND we're
     * still actively sending, force a socket rebind to clear the stuck NAT
     * mapping. A new `connect()` on the DatagramChannel picks a fresh
     * ephemeral source port, which forces the carrier NAT to allocate a new
     * mapping and the sim re-targets us via the follow-up `UseCircuitCode`.
     *
     * Cooled down by [SOCKET_REBIND_COOLDOWN_MS] so a carrier-side outage
     * doesn't cause us to thrash.
     */
    private fun checkInboundFlow() {
        if (!_isConnected.value) return

        val now = System.currentTimeMillis()
        val received = packetsReceived.get()

        // Initialise snapshot the first time we observe any traffic.
        if (lastInboundFlowGrowthTime == 0L) {
            lastInboundFlowSnapshot = received
            lastInboundFlowGrowthTime = now
            return
        }

        // Refresh snapshot whenever inbound is healthy.
        if (received > lastInboundFlowSnapshot) {
            lastInboundFlowSnapshot = received
            lastInboundFlowGrowthTime = now
            return
        }

        // Don't trip the watchdog while we're still in the cooldown after a
        // previous rebind (the new circuit needs time to receive its first
        // packet through the freshly-allocated NAT mapping).
        if (now - lastSocketRebindTime < SOCKET_REBIND_COOLDOWN_MS) return

        val stallMs = now - lastInboundFlowGrowthTime
        if (stallMs < INBOUND_DATA_STALL_MS) return

        // Require evidence we're actually sending. If `packetsSent` isn't
        // growing either, the unanswered-ping path will handle it; firing a
        // rebind here would just race with that.
        if (packetsSent.get() <= lastInboundFlowSnapshot) return

        NetworkLogger.log(
            NetworkLogger.Level.WARN,
            NetworkLogger.Category.UDP,
            "Inbound stalled for ${stallMs}ms (received=$received, sent=${packetsSent.get()}) - forcing socket rebind"
        )
        lastSocketRebindTime = now
        emitNetworkState(NetworkStateTransition.RECONNECTING)
        scope.launch {
            val ok = try { reconnect() } catch (e: Exception) { false }
            if (ok) {
                emitNetworkState(NetworkStateTransition.CONNECTED)
            } else {
                NetworkLogger.log(
                    NetworkLogger.Level.ERROR,
                    NetworkLogger.Category.UDP,
                    "Inbound-stall socket rebind failed; deferring to higher-level reconnect"
                )
                emitNetworkState(NetworkStateTransition.FAULTED)
                reconnectionCallback?.invoke()
            }
        }
    }
    

    /**
     * Detect the "silent socket after reconnect" pathology described in
     * docs/2026-04-25 Athanasia capture: socket reconnect succeeds (new
     * source port bound, UseCircuitCode resent), but no packet ever arrives
     * because the simulator-side circuit has already timed out. Without
     * this check, the inbound-stall watchdog defers for [SOCKET_REBIND_COOLDOWN_MS]
     * + [INBOUND_DATA_STALL_MS] = 75s before reacting, and even then it
     * would just rebind again on a new source port — also pointless against
     * a dead server-side circuit.
     *
     * Escalation path: trip [reconnectionCallback] so [LinkpointApp] can
     * tear the circuit down and either re-login or hand the user back to
     * the login screen with a meaningful error. [lastSocketReconnectTime]
     * is cleared so we don't fire again on the same reconnect.
     */
    private fun checkPostReconnectSilence() {
        if (!_isConnected.value) return
        if (lastSocketReconnectTime == 0L) return
        if (!lastSocketReconnectSucceeded) return
        if (postReconnectPacketsReceived.get() > 0) return

        val now = System.currentTimeMillis()
        if (now - lastSocketReconnectTime < POST_RECONNECT_VERIFY_MS) return

        NetworkLogger.log(
            NetworkLogger.Level.WARN,
            NetworkLogger.Category.UDP,
            "Post-reconnect silence: no inbound packets ${now - lastSocketReconnectTime}ms after socket rebind " +
                "(sent=${packetsSent.get()}). Simulator-side circuit likely dead — escalating to full reconnect."
        )

        // Clear so this only fires once per reconnect attempt; the next
        // successful reconnect() will set it again.
        lastSocketReconnectTime = 0L

        emitNetworkState(NetworkStateTransition.FAULTED)
        try {
            reconnectionCallback?.invoke()
        } catch (e: Exception) {
            NetworkLogger.log(
                NetworkLogger.Level.ERROR,
                NetworkLogger.Category.UDP,
                "reconnectionCallback threw during post-reconnect escalation: ${e.message}"
            )
        }
    }

    /**
     * Send an explicit StartPingCheck packet so unanswered-ping tracking maps to real ping requests.
     */
    private fun sendStartPingCheck() {
        val pingId = (nextPingId.incrementAndGet() and 0xFF)
        lastPingTime.set(System.currentTimeMillis())
        val unanswered = unansweredPings.incrementAndGet()

        val payload = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN)
            .put(pingId.toByte())
            .putInt(0)
            .array()

        sendPacket(MessageIds.START_PING_CHECK, payload, reliable = false)

        NetworkLogger.log(
            NetworkLogger.Level.DEBUG,
            NetworkLogger.Category.UDP,
            "Ping check sent (pingId=$pingId, unanswered: $unanswered)"
        )
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
        val ackedMessageName = callbackInfo?.let { getMessageName(it.messageId) }

        // Always log the inbound ACK so the ACKs Received counter and
        // packet history reflect every ACK the simulator sends, including
        // those for which we no longer have a pending callback (e.g. ACKs
        // for already-completed reliable sends or duplicate ACKs).
        EnhancedPacketLogger.logAckReceived(sequenceNumber, ackedMessageName)
        recordAckReceived(sequenceNumber, callbackInfo?.messageId ?: -1)

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
     * Record an inbound ACK in the bounded packet history so it shows up
     * in the debug report's per-packet event log alongside sends/receives.
     */
    private fun recordAckReceived(sequenceNumber: Int, messageId: Int) {
        val entry = PacketHistoryEntry(
            timestamp = System.currentTimeMillis(),
            type = PacketHistoryEntry.PacketEventType.ACK_RECEIVED,
            messageId = messageId,
            messageName = if (messageId >= 0) getMessageName(messageId) else "ACK",
            size = 4,
            sequenceNumber = sequenceNumber,
            fullHexDump = "",
            success = true,
            errorMessage = null
        )
        recentPacketHistory.offer(entry)
        while (recentPacketHistory.size > DEFAULT_PACKET_HISTORY_SIZE) {
            recentPacketHistory.poll()
        }
    }
    
    /**
     * Check for timeouts on pending reliable messages.
     * This should be called periodically to handle message timeouts.
     * Based on the reference viewer's timeout and retry logic.
     */
    private fun checkMessageTimeouts() {
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
     * Route a message by ID to the registered app-level handlers.
     * Called by LinkpointThreadedCircuit to forward received packets.
     */
    fun routeMessage(messageId: Int, data: ByteArray) {
        // Dispatch to the handler registered via registerHandler()
        val handler = messageHandlers[messageId]
        if (handler != null) {
            handler.handle(messageId, data)
            messagesRouted.incrementAndGet()
        }
    }
    
    /**
     * Extract message ID from packet using Linkpoint decoding.
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
        
        val result = decodeMessageIdSLProtocol(data, PACKET_HEADER_SIZE)
        return result?.first ?: INVALID_MESSAGE_ID
    }
    
    /**
     * Decode message ID using Linkpoint-compatible encoding.
     * Returns Pair of (messageId, nextOffset) or null if invalid.
     */
    private fun decodeMessageIdSLProtocol(data: ByteArray, startOffset: Int): Pair<Int, Int>? {
        if (data.size <= startOffset) return null
        
        var offset = startOffset
        
        // First byte - check if it's 0xFF (which is -1 as signed byte)
        val b1 = data[offset].toInt() // Signed byte, -128 to 127
        offset++
        
        if (b1 != -1) {
            // High frequency message - return the signed byte value directly
            // This matches the reference viewer: if (b != -1) return b;
            // e.g., 0x0C (12) = ObjectUpdate, 0xFB (-5) = PacketAck
            return Pair(b1, offset)
        }
        
        // Second byte
        if (data.size <= offset) return null
        val b2 = data[offset].toInt() // Signed byte
        offset++
        
        if (b2 != -1) {
            // Medium frequency message - byte OR MEDIUM_FREQUENCY_BASE
            // This matches the reference viewer: b2 | 65280
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
        
        // This matches the reference viewer: byteBuffer.getShort() | (-65536)
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
     * Uses synchronous registration to avoid main-thread deadlocks.
     * Handler is ready immediately when this method returns.
     */
    fun registerHandler(messageId: Int, handler: (Int, ByteArray) -> Unit) {
        // Register in messageHandlers for diagnostics (using SAM conversion for functional interface)
        messageHandlers[messageId] = MessageHandler { msgId, data ->
            handler(msgId, data)
        }

        val messageName = MessageIds.getMessageName(messageId)
        EnhancedPacketLogger.logHandlerRegistered(messageId, messageName)

        messageRouter.registerHandler(messageId, object : MessageRouter.Handler {
            override fun handleMessage(messageId: Int, data: ByteArray): Boolean {
                handler(messageId, data)
                return true
            }
        })

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
            "Registered handler for message $messageId (total: ${messageHandlers.size})")
    }



    fun registerParsedHandler(messageId: Int, handler: (Int, Any?) -> Unit) {
        registerHandler(messageId) { msgId, rawPacket ->
            val payload = MessageParser.extractPayload(rawPacket) ?: return@registerHandler
            handler(msgId, MessageParser.parseByMessageId(msgId, payload))
        }
    }

    /**
     * Send UseCircuitCode message
     * Uses mobile-optimized packet construction
     */
    private fun sendUseCircuitCode() {
        val identity = outboundIdentity("UDPConnectionFixed.sendUseCircuitCode")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sending UseCircuitCode")

        // UseCircuitCode message format:
        // - CircuitCode (4 bytes, little-endian)
        // - SessionID (16 bytes, UUID)
        // - AgentID (16 bytes, UUID)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        payload.putInt(identity.circuitCode ?: 0)
        payload.put(identity.sessionId.asBytes())
        payload.put(identity.agentId.asBytes())

        val seq = sendPacket(MessageIds.USE_CIRCUIT_CODE, payload.array(), reliable = true)
        if (seq >= 0) {
            useCircuitCodeSequence = seq
        }
    }
    
    /**
     * Send CompleteAgentMovement message
     * Uses mobile-optimized packet construction
     */
    fun sendCompleteAgentMovement() {
        val identity = outboundIdentity("UDPConnectionFixed.sendCompleteAgentMovement")
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, "→ Sending CompleteAgentMovement")
        
        // CompleteAgentMovement message format:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // - CircuitCode (4 bytes, little-endian)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        payload.put(identity.agentId.asBytes())
        payload.put(identity.sessionId.asBytes())
        payload.putInt(identity.circuitCode ?: 0)
        
        // Message ID for CompleteAgentMovement (low frequency message)
        val messageId = MessageIds.COMPLETE_AGENT_MOVEMENT
        
        // Build packet with header
        sendPacket(messageId, payload.array(), reliable = true)
    }
    
    /**
     * Send AgentUpdate message
     * Mobile-optimized: 10 updates/sec to balance responsiveness and battery
     */
    fun sendAgentUpdate() {
        val identity = outboundIdentity("UDPConnectionFixed.sendAgentUpdate")
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
        payload.put(identity.agentId.asBytes())
        payload.put(identity.sessionId.asBytes())
        
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
    fun sendRegionHandshakeReply(flags: Int = 0) {
        val identity = outboundIdentity("UDPConnectionFixed.sendRegionHandshakeReply")
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        
        // Agent ID
        payload.putUUID(identity.agentId)
        
        // Session ID
        payload.putUUID(identity.sessionId)
        
        // Flags (typically 0)
        payload.putInt(flags)
        
        Log.d(TAG, "Sending RegionHandshakeReply")
        sendPacket(MessageIds.REGION_HANDSHAKE_REPLY, payload.array(), reliable = true, zerocoded = true)
    }
    
    /**
     * Send AgentThrottle message to set bandwidth allocations.
     * Tells the simulator how much bandwidth we want for different data types.
     *
     * Wire format (LL message_template `AgentThrottle`, low-freq 81):
     *   AgentData: AgentID (LLUUID) + SessionID (LLUUID) + CircuitCode (U32)
     *   Throttle:  GenCounter (U32) + Throttles (Variable 1: u8 length + bytes)
     *
     * The Throttles field is a `Variable 1` block, so its 28 bytes of seven
     * little-endian floats must be preceded by a 1-byte length prefix. Lumiya
     * encodes this via `packVariable(buf, throttles, 1)` in
     * `slproto/messages/AgentThrottle.java`. Without the prefix, the simulator
     * parses the first throttle byte as the length, the message is structurally
     * wrong, and the agent ends up on default throttle settings (or worse, a
     * silent reject), which contributed to the post-login data stall in the
     * 2026-04-26 Athanasia capture.
     */
    fun sendAgentThrottle(
        resend: Float = 150_000f,
        land: Float = 170_000f,
        wind: Float = 12_500f,
        cloud: Float = 12_500f,
        task: Float = 446_000f,
        texture: Float = 446_000f,
        asset: Float = 220_000f
    ) {
        val identity = outboundIdentity("UDPConnectionFixed.sendAgentThrottle")
        // 36 (AgentData) + 4 (GenCounter) + 1 (Throttles length prefix) + 28 (7 floats)
        val payload = ByteBuffer.allocate(36 + 4 + 1 + 28).order(ByteOrder.LITTLE_ENDIAN)

        // AgentData block
        payload.putUUID(identity.agentId)
        payload.putUUID(identity.sessionId)
        payload.putInt(identity.circuitCode ?: 0)

        // Throttle block
        payload.putInt(1) // GenCounter
        payload.put(28.toByte()) // Variable 1 length prefix for Throttles
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
    fun sendRequestMultipleObjects(objectIds: List<Int>, cacheMissType: Int = 0) {
        if (objectIds.isEmpty()) return
        val identity = outboundIdentity("UDPConnectionFixed.sendRequestMultipleObjects")

        // Wire format stores object count in one byte; enforce protocol-safe chunking.
        val normalizedIds = objectIds.distinct()
        val now = System.currentTimeMillis()
        val requestKey = buildString(normalizedIds.size * 10 + 16) {
            append(cacheMissType)
            append(':')
            normalizedIds.forEach {
                append(it)
                append(',')
            }
        }
        val isDuplicateBurst = synchronized(requestMultipleObjectsLock) {
            val previousTs = recentRequestMultipleObjects[requestKey]
            val duplicate = previousTs != null && now - previousTs < REQUEST_MULTIPLE_OBJECTS_DEDUP_WINDOW_MS
            if (!duplicate) {
                recentRequestMultipleObjects[requestKey] = now
            }
            duplicate
        }
        if (isDuplicateBurst) {
            NetworkLogger.log(
                NetworkLogger.Level.DEBUG,
                NetworkLogger.Category.UDP,
                "↺ Suppressed duplicate RequestMultipleObjects burst (${normalizedIds.size} objects)"
            )
            return
        }

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP, 
            "→ Sending RequestMultipleObjects for ${normalizedIds.size} objects")
        
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
        
        for (chunk in normalizedIds.chunked(255)) {
            val payloadSize = agentDataSize + objectCountSize + (chunk.size * objectEntrySize)
            val payload = ByteBuffer.allocate(payloadSize).order(ByteOrder.LITTLE_ENDIAN)

            // AgentData block
            payload.put(identity.agentId.asBytes())
            payload.put(identity.sessionId.asBytes())

            // ObjectData count (u8)
            payload.put(chunk.size.toByte())

            // ObjectData blocks
            for (objectId in chunk) {
                payload.put(cacheMissType.toByte())
                payload.putInt(objectId)
            }

            sendPacket(MessageIds.REQUEST_MULTIPLE_OBJECTS, payload.array(), reliable = true)
        }
    }

    /**
     * UDP fallback for resolving avatar UUIDs to legacy first/last name pairs.
     * Used when the GetDisplayNames capability is unavailable, times out, or
     * returns no entries for a subset of IDs (the 2026-04-25 Athanasia capture
     * showed 851 friends stuck on `Resident (xxxx)` because the cap path
     * silently failed). Reply arrives as `UUIDNameReply` and is handled by
     * `LinkpointApp` to feed `FriendsManager` / display-name caches.
     *
     * Wire format (LL message template `UUIDNameRequest`, low-freq 235):
     *   UUIDNameBlock (Variable u8 count): { ID LLUUID }
     * No AgentData block — this message is `NotTrusted Unencoded`.
     */
    fun sendUUIDNameRequest(ids: List<UUID>) {
        if (ids.isEmpty()) return
        // Cap at 60 per packet to stay well under the ~1200B UDP MTU
        // (1 + count*16). Sender batches if larger.
        for (batch in ids.chunked(60)) {
            val payload = ByteBuffer.allocate(1 + batch.size * 16).order(ByteOrder.LITTLE_ENDIAN)
            payload.put(batch.size.toByte())
            for (id in batch) {
                payload.put(id.asBytes())
            }
            sendPacket(MessageIds.UUID_NAME_REQUEST, payload.array(), reliable = true)
        }
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
            "→ Sent UUIDNameRequest for ${ids.size} ids")
    }

    /**
     * Send a packet with proper SL protocol encoding
     * 
     * @param messageId The message ID
     * @param payload The message payload (already encoded)
     * @param reliable Whether this packet is reliable
     * @param zerocoded Whether to use zero-coding
     */
    /**
     * Build a packet from this circuit's outbound message and queue it for the
     * I/O thread to write. Lumiya pattern (`SLCircuit.SendMessage`): producers
     * never touch the DatagramChannel directly; they enqueue and wake the
     * selector. The single I/O thread drains the queue between select cycles,
     * so wire access is always serialized to one thread.
     *
     * Safe to call from any thread. Returns the assigned sequence number
     * (≥ 1) or -1 if the connection isn't up.
     */
    fun sendPacket(
        messageId: Int,
        payload: ByteArray,
        reliable: Boolean = false,
        zerocoded: Boolean = false,
        listener: MessageEventListener? = null
    ): Int {
        if (!_isConnected.value) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP, "Cannot send: not connected")
            return -1
        }

        val seqNum: Int
        val finalPacket: ByteArray
        // Synchronize seq assignment with enqueue so wire order matches seq order.
        // Without this, two producers can interleave incrementAndGet vs offer().
        synchronized(outgoingQueue) {
            seqNum = sequenceNumber.incrementAndGet()
            val flags = (if (reliable) 0x40 else 0) or (if (zerocoded) 0x80 else 0)

            val header = ByteBuffer.allocate(6).order(ByteOrder.BIG_ENDIAN)
            header.put(flags.toByte())
            header.putInt(seqNum)
            header.put(0.toByte()) // Extra header byte

            val packet = header.array() + encodeMessageId(messageId) + payload
            finalPacket = if (zerocoded) zeroEncode(packet) else packet

            outgoingQueue.offer(OutboundPacket(
                data = finalPacket,
                messageId = messageId,
                seqNum = seqNum,
                reliable = reliable,
                zerocoded = zerocoded
            ))
        }

        if (reliable && listener != null) {
            pendingCallbacks[seqNum] = MessageCallbackInfo(
                sequenceNumber = seqNum,
                messageId = messageId,
                listener = listener,
                sentTime = System.currentTimeMillis(),
                retryCount = 0
            )
        }

        // Wake the I/O thread so it drains the queue immediately rather than
        // waiting for the next select() timeout.
        selector?.wakeup()

        return seqNum
    }

    /**
     * Drain queued outbound packets to the DatagramChannel. Runs on the I/O
     * thread (Lumiya `ProcessTransmit`). All write-path bookkeeping —
     * counters, packet history, EnhancedPacketLogger, SessionLogRecorder, and
     * socket-invalidation detection — is performed here so it stays on the
     * thread that actually owns the channel.
     */
    private fun drainOutgoingQueueOnIOThread() {
        val channel = datagramChannel ?: return

        while (true) {
            val pkt = outgoingQueue.poll() ?: break
            val messageName = getMessageName(pkt.messageId)

            try {
                val written = channel.write(ByteBuffer.wrap(pkt.data))
                if (written > 0) {
                    packetsSent.incrementAndGet()
                    bytesSent.addAndGet(written.toLong())
                    lastSendTime = System.currentTimeMillis()
                    consecutiveSendErrors.set(0)

                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                        "→ Sent packet: ${pkt.data.size} bytes (ID: ${pkt.messageId}, reliable: ${pkt.reliable})")
                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                        "   Message: $messageName (seq: ${pkt.seqNum})")
                    NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                        "   Full packet data: ${pkt.data.joinToString(" ") { "%02X".format(it) }}")

                    recordPacketEvent(
                        type = PacketHistoryEntry.PacketEventType.SEND_SUCCESS,
                        messageId = pkt.messageId,
                        data = pkt.data,
                        sequenceNumber = pkt.seqNum,
                        success = true
                    )

                    EnhancedPacketLogger.logPacketSent(
                        messageId = pkt.messageId,
                        messageName = messageName,
                        sequenceNumber = pkt.seqNum,
                        data = pkt.data,
                        flags = EnhancedPacketLogger.PacketFlags(
                            reliable = pkt.reliable,
                            resent = false,
                            zerocoded = pkt.zerocoded,
                            hasAcks = false
                        )
                    )

                    SessionLogRecorder.logPacketSent(
                        messageId = pkt.messageId,
                        messageName = messageName,
                        sequenceNumber = pkt.seqNum,
                        data = pkt.data,
                        reliable = pkt.reliable
                    )
                } else {
                    recordPacketEvent(
                        type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                        messageId = pkt.messageId,
                        data = pkt.data,
                        sequenceNumber = pkt.seqNum,
                        success = false,
                        errorMessage = "DatagramChannel.write() returned 0 bytes"
                    )
                    NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                        "→ Send may have failed: 0 bytes written (ID: ${pkt.messageId})")
                }
            } catch (e: java.nio.channels.AsynchronousCloseException) {
                // The channel was closed mid-write — almost always reconnect()
                // swapping our DatagramChannel out from under us. The packet
                // we just popped is lost, but if it was reliable it's still
                // in pendingCallbacks and will be resent. Don't count this
                // as a send error and don't escalate; just bail out of the
                // drain so the next iteration picks up the new channel.
                NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                    "Channel closed during send (${getMessageName(pkt.messageId)} seq=${pkt.seqNum}) — will resume on new channel")
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                    messageId = pkt.messageId,
                    data = pkt.data,
                    sequenceNumber = pkt.seqNum,
                    success = false,
                    errorMessage = "AsynchronousCloseException (channel swap)"
                )
                break
            } catch (e: java.nio.channels.ClosedChannelException) {
                NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                    "Channel closed during send (${getMessageName(pkt.messageId)} seq=${pkt.seqNum}) — will resume on new channel")
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                    messageId = pkt.messageId,
                    data = pkt.data,
                    sequenceNumber = pkt.seqNum,
                    success = false,
                    errorMessage = "ClosedChannelException (channel swap)"
                )
                break
            } catch (e: Exception) {
                // Use the exception class name when the message is null —
                // otherwise the log just says "Send error: null", which
                // hides AsynchronousCloseException etc. behind a useless
                // string and leaves consecutiveSendErrors silently growing.
                val errorDetail = e.message ?: e.javaClass.simpleName
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                    "✗ Send error: $errorDetail")
                recordPacketEvent(
                    type = PacketHistoryEntry.PacketEventType.SEND_FAILED,
                    messageId = pkt.messageId,
                    data = pkt.data,
                    sequenceNumber = pkt.seqNum,
                    success = false,
                    errorMessage = errorDetail
                )

                val errorCount = consecutiveSendErrors.incrementAndGet()
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                    "Consecutive send errors: $errorCount (threshold: $CONSECUTIVE_ERROR_THRESHOLD)")

                val errorMessage = errorDetail.lowercase()
                val isSocketInvalidationError = SOCKET_INVALIDATION_ERRORS.any { errorMessage.contains(it) }

                if (isSocketInvalidationError && errorCount >= CONSECUTIVE_ERROR_THRESHOLD) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                        "🔄 Too many consecutive send errors ($errorCount), attempting socket reconnect")
                    consecutiveSendErrors.set(0)
                    emitNetworkState(NetworkStateTransition.RECONNECTING)
                    scope.launch {
                        val reconnected = try { reconnect() } catch (ex: Exception) { false }
                        if (reconnected) {
                            emitNetworkState(NetworkStateTransition.CONNECTED)
                        } else {
                            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                                "Socket reconnect failed after send errors, triggering full reconnection")
                            emitNetworkState(NetworkStateTransition.FAULTED)
                            reconnectionCallback?.invoke()
                        }
                    }
                }
                // Stop draining on error — the I/O thread will retry on the next
                // select cycle once the socket is healthy again.
                break
            }
        }
    }

    /**
     * Encode message ID for transmission (Linkpoint)
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

        // Stop the dedicated I/O thread first (it checks _isConnected in its loop)
        ioThread?.interrupt()
        ioThread = null

        agentUpdateJob?.cancel()

        // Clear pending ACKs since we're disconnecting
        pendingAcksToSend.clear()

        // Drop any outbound packets that didn't get out before disconnect
        outgoingQueue.clear()

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
     * Reconnect the UDP socket without re-sending UseCircuitCode.
     *
     * This recreates the NIO DatagramChannel and restarts the receive loop when the
     * existing socket has been invalidated (e.g. by mobile network changes, NAT timeout,
     * or ICMP port-unreachable). The circuit code and session credentials are preserved.
     *
     * Unlike a full disconnect+connect cycle, this does NOT reset the sequence number
     * or re-send UseCircuitCode/CompleteAgentMovement because the server-side circuit
     * may still be alive. It re-sends UseCircuitCode to ensure the server maps our
     * new source port to the existing circuit.
     *
     * @return true if reconnection succeeded
     */
    suspend fun reconnect(): Boolean = withContext(CircuitDispatcher.dispatcher) {
        if (simIP.isBlank() || simPort == 0) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "Cannot reconnect: no sim address configured")
            return@withContext false
        }

        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "=== UDP SOCKET RECONNECT ===")
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "Recreating socket to $simIP:$simPort (circuit=$circuitCode)")

        val reconnectAttempt = socketReconnectCount.incrementAndGet()
        val now = System.currentTimeMillis()
        lastSocketReconnectTime = now
        lastSocketReconnectSucceeded = false
        postReconnectPacketsReceived.set(0)

        // Lumiya pattern (SLConnection / SLCircuit): keep the I/O thread and
        // the Selector alive across reconnects — only swap the DatagramChannel
        // and SelectionKey. This avoids the dead-socket race where:
        //   1. We close the old selector here on CircuitDispatcher.
        //   2. Meanwhile the I/O thread is mid-iteration with the OLD selector
        //      captured in a local variable, plus its own write that's about
        //      to fire. The write throws AsynchronousCloseException (no msg);
        //      its select() throws ClosedSelectorException; the receive loop
        //      exits while _isConnected has just been set back to true.
        //   3. The new I/O thread we'd just spawned then races the dying old
        //      one, sometimes succeeding and sometimes ending up with a closed
        //      selector before it can do anything useful.
        // By preserving the same selector + thread, the swap is observed
        // atomically by the I/O thread on its next iteration.
        //
        // Also: we deliberately do NOT clear `outgoingQueue` here. Reliable
        // packets that haven't been acked are still meaningful and the
        // simulator will accept them on the new source port once
        // UseCircuitCode lands. Pending ACKs are likewise preserved — they
        // refer to received seqnums, not sockets.
        //
        // _isConnected stays true throughout. emitNetworkState() handles UI
        // signalling separately.

        val oldChannel = datagramChannel
        val oldKey = selectionKey

        try {
            oldKey?.cancel()
        } catch (e: Exception) {
            // Cancelling an already-cancelled key is harmless; keep going.
        }
        try {
            oldChannel?.close()
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                "Error closing old socket during reconnect: ${e.message}")
        }

        // Reset ping state so we don't immediately disconnect again.
        lastReceiveTime = now
        lastPingTime.set(now)
        unansweredPings.set(0)
        consecutiveSendErrors.set(0)

        // Re-baseline the inbound-flow watchdog. lastSocketRebindTime is
        // set to `now` so the cooldown protects this attempt from being
        // immediately retripped if the new socket also can't receive.
        lastInboundFlowSnapshot = packetsReceived.get()
        lastInboundFlowGrowthTime = now
        lastSocketRebindTime = now

        try {
            val address = InetSocketAddress(simIP, simPort)
            val newChannel = try {
                DatagramChannel.open(java.net.StandardProtocolFamily.INET)
            } catch (e: Exception) {
                DatagramChannel.open()
            }
            newChannel.apply {
                configureBlocking(false)
                setOption(StandardSocketOptions.SO_RCVBUF, 65536)
                setOption(StandardSocketOptions.SO_SNDBUF, 65536)
                connect(address)
            }

            try {
                val localAddr = newChannel.localAddress as? InetSocketAddress
                localBindAddress = localAddr?.address?.hostAddress
                localBindPort = localAddr?.port ?: 0
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "✓ Reconnect local bind: $localBindAddress:$localBindPort (attempt #$reconnectAttempt)")
            } catch (e: Exception) {
                // Non-fatal
            }

            // Reuse the existing Selector if it's still open; otherwise open
            // a new one. The first branch is the hot path for in-place
            // reconnects; the second covers the cold case where a previous
            // disconnect closed the selector.
            val activeSelector = selector?.takeIf { it.isOpen } ?: Selector.open()
            val newKey = newChannel.register(activeSelector, SelectionKey.OP_READ)

            // Publish the new state before waking the I/O thread so it picks
            // up consistent fields on its next iteration.
            datagramChannel = newChannel
            selector = activeSelector
            selectionKey = newKey

            // Make sure there's an I/O thread to drive the new state. The
            // thread that was running before reconnect should still be alive
            // and will see the new selector/channel via @Volatile reads, but
            // if it died (e.g. an earlier ClosedSelectorException), restart it.
            val existing = ioThread
            if (existing == null || !existing.isAlive) {
                NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                    "I/O thread not alive at reconnect — starting a fresh one")
                ioThread = Thread({
                    receiveLoopBlocking()
                }, "SLCircuitIO-reconnect-$reconnectAttempt").apply {
                    isDaemon = true
                    start()
                }
            } else {
                // Wake the existing I/O thread out of select() so it observes
                // the new selector/channel right away rather than waiting for
                // SELECTOR_TIMEOUT_MS.
                activeSelector.wakeup()
            }

            // Re-send UseCircuitCode so the server maps our new source port
            // to this circuit. The I/O thread will drain it from outgoingQueue.
            sendUseCircuitCode()

            lastSocketReconnectSucceeded = true
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "=== UDP SOCKET RECONNECT SUCCEEDED (attempt #$reconnectAttempt) ===")
            true
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "UDP socket reconnect failed: ${e.message}")
            lastConnectionError = "Reconnect failed: ${e.message}"
            lastSocketReconnectSucceeded = false
            // We deliberately do NOT flip _isConnected to false here — the
            // higher-level reconnectionCallback or disconnect() will handle
            // session-level recovery if this socket reconnect can't be
            // retried. Flipping it would also wedge LinkpointApp's UI into a
            // disconnected state when the next attempt may succeed.
            false
        }
    }

    fun getSocketReconnectCount(): Int = socketReconnectCount.get()
    fun getLastSocketReconnectTime(): Long = lastSocketReconnectTime
    fun getLastSocketReconnectSucceeded(): Boolean = lastSocketReconnectSucceeded
    fun getPostReconnectPacketsReceived(): Int = postReconnectPacketsReceived.get()
    fun getIoThreadName(): String? = ioThread?.name

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
        // Treat the connection moment as fresh input so the first second of
        // updates runs at the active cadence (helps the simulator pick up the
        // initial avatar pose quickly).
        lastAgentInputAtMs = System.currentTimeMillis()
        agentUpdateJob = scope.launch {
            Log.d(TAG, "Starting periodic AgentUpdate messages")
            try {
                while (_isConnected.value) {
                    sendAgentUpdate()
                    delay(currentAgentUpdateIntervalMs())
                }
            } catch (e: CancellationException) {
                // Expected during disconnect/reconnect — not an error
                NetworkLogger.log(
                    NetworkLogger.Level.DEBUG,
                    NetworkLogger.Category.UDP,
                    "AgentUpdate sender cancelled"
                )
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
        if (flags != controlFlags) noteAgentInput()
        controlFlags = flags
    }

    /**
     * Update look-at direction for camera/avatar orientation
     */
    fun updateLookAt(x: Float, y: Float, z: Float) {
        val prev = currentLookAt
        if (prev[0] == x && prev[1] == y && prev[2] == z) return
        noteAgentInput()
        currentLookAt = floatArrayOf(x, y, z)
    }

    /**
     * Pick the AgentUpdate send interval based on whether the avatar is
     * actively moving or idle (no movement flags, no look-at change for
     * [AGENT_UPDATE_IDLE_THRESHOLD_MS]). See docs/lumiya-port/README.md
     * item 5.
     */
    private fun currentAgentUpdateIntervalMs(): Long {
        val now = System.currentTimeMillis()
        val movementActive = controlFlags != 0
        val recentInput = (now - lastAgentInputAtMs) < AGENT_UPDATE_IDLE_THRESHOLD_MS
        return if (movementActive || recentInput) AGENT_UPDATE_INTERVAL_MS
               else AGENT_UPDATE_IDLE_INTERVAL_MS
    }

    private fun noteAgentInput() {
        lastAgentInputAtMs = System.currentTimeMillis()
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
    fun handleStartPingCheck(pingId: Byte, oldestUnacked: Int) {
        val payload = byteArrayOf(pingId)

        Log.d(TAG, "Responding to ping check $pingId")
        sendPacket(MessageIds.COMPLETE_PING_CHECK, payload, reliable = false)
    }

    /**
     * Process ACKs appended to a received packet.
     *
     * IMPORTANT: This should ONLY be called when the packet's flags byte has
     * bit 0x10 set (hasAcks flag). Calling this on packets without appended ACKs
     * will read garbage from the end of the payload as ACK sequence numbers,
     * potentially corrupting the ACK state.
     *
     * SL Protocol appended ACK format:
     * - The last byte of the packet is the count of appended ACKs
     * - Before that are count * 4 bytes of ACKed sequence numbers (little-endian)
     * - These are piggybacked on the server's outgoing packets as an optimization
     */
    private fun processAppendedAcks(data: ByteArray) {
        if (data.size < PACKET_HEADER_SIZE + 2) return

        // Verify the appended ACK flag is set (0x10) before processing
        val flags = data[0].toInt() and 0xFF
        if ((flags and 0x10) == 0) return

        val count = data[data.size - 1].toInt() and 0xFF
        if (count == 0) return

        val acksStart = data.size - 1 - (count * 4)
        if (acksStart < PACKET_HEADER_SIZE) return

        var pos = acksStart
        for (i in 0 until count) {
            if (pos + 4 > data.size - 1) break
            val ackedSeq = ByteBuffer.wrap(data, pos, 4).order(ByteOrder.LITTLE_ENDIAN).int
            pos += 4
            processReceivedAck(ackedSeq)
        }

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
            "Processed $count appended ACKs from packet")
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
            lastMessageTimes = lastMessageTimes.toMap(),
            inboundRateBuckets = inboundRateTracker.snapshot()
        )
    }
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    /**
     * Debug-report formatter for a registered message ID.
     *
     * SL UDP message IDs span three frequency classes that all get stored as
     * plain Kotlin Int:
     *   - High   (1 byte):  0x01..0xFE        -> positive 1..254
     *   - Medium (2 bytes): 0xFF01..0xFFFE    -> positive 65281..65534
     *   - Low    (4 bytes): 0xFFFF0001..      -> negative (0xFFFFxxxx is <0 as signed Int)
     *   - Fixed  (4 bytes): 0xFFFFFFFA..0xFFFFFFFF -> -6..-1
     *
     * The previous formatter just did Int.toString(), which is what produced
     * the forest of confusing negatives in the debug report. Format here with
     * the message name (when known) plus a stable hex form.
     */
    private fun formatHandlerIdForDiag(id: Int): String {
        val name = MessageIds.getMessageName(id)
        val hex = when {
            id in 0x01..0xFE -> "0x%02X".format(id)
            id in 0xFF01..0xFFFE -> "0x%04X".format(id)
            else -> "0x%08X".format(id)
        }
        return if (name.isNotBlank() && !name.startsWith("Unknown")) {
            "$name ($hex)"
        } else {
            hex
        }
    }

    private fun messageFrequencyOrder(id: Int): Int = when {
        id in 0x01..0xFE -> 0        // High
        id in 0xFF01..0xFFFE -> 1    // Medium
        id in -6..-1 -> 3            // Fixed (0xFFFFFFFA..0xFFFFFFFF)
        else -> 2                    // Low (other negatives)
    }

    fun getDiagnostics(): UDPDiagnostics {
        return UDPDiagnostics(
            isConnected = _isConnected.value,
            simIP = simIP,
            simPort = simPort,
            circuitCode = circuitCode,
            agentId = agentId,
            sessionId = sessionId,
            sequenceNumber = sequenceNumber.get(),
            pendingAckCount = pendingAcksToSend.size,
            registeredHandlerCount = messageHandlers.size,
            registeredHandlers = messageHandlers.keys
                .sortedWith(compareBy({ messageFrequencyOrder(it) }, { it }))
                .map(::formatHandlerIdForDiag),
            pendingPackets = emptyList(),
            socketOpen = datagramChannel?.isOpen ?: false,
            receiveLoopActive = ioThread?.isAlive == true,
            lastPingTime = lastPingTime.get(),
            unansweredPings = unansweredPings.get()
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
        val lastMessageTimes: Map<String, Long>,
        val inboundRateBuckets: List<InboundRateTracker.RateBucket> = emptyList()
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
        val receiveLoopActive: Boolean,
        val lastPingTime: Long,
        val unansweredPings: Int
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
        val lastConnectionError: String?,
        val lastPingTime: Long,
        val unansweredPings: Int
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
            lastConnectionError = lastConnectionError,
            lastPingTime = lastPingTime.get(),
            unansweredPings = unansweredPings.get()
        )
    }
}
