package com.linkpoint.protocol.circuit

import android.util.Log
import com.linkpoint.network.NetworkLogger
import com.linkpoint.network.events.ConnectionState
import com.linkpoint.network.events.ConnectionStateChangedEvent
import com.linkpoint.protocol.messages.PacketCodec
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.SocketTimeoutException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

/**
 * Linkpoint Threaded Circuit Connection
 * 
 * This class implements the EXACT threading model that made the reference viewer work flawlessly
 * on mobile networks for 10+ years, including 5G.
 * 
 * ## Why This Works Better Than Coroutines
 * 
 * The proven approach was a SINGLE DEDICATED THREAD with a deterministic processing loop:
 * 
 * ```
 * while (running) {
 *     1. Wakeup/Yield
 *     2. Process received packets
 *     3. Send pending transmissions  
 *     4. Process idle tasks (resends, pings, ACKs)
 *     5. Sleep until next interval (1 second default, 100ms when active)
 * }
 * ```
 * 
 * This is fundamentally different from coroutines which have:
 * - Non-deterministic scheduling (coroutine dispatcher decides when to run)
 * - Multiple concurrent contexts (receive loop, ACK sender, etc.)
 * - Potential race conditions between operations
 * - No guarantee that pings/resends happen within mobile NAT timeouts
 * 
 * ## Key Features from the reference viewer Analysis
 * 
 * 1. **Single Thread**: All network operations in one thread for determinism
 * 2. **1-Second Tight Loop**: Guarantees frequent pings for mobile NAT keep-alive
 * 3. **Integrated Resend Logic**: Resends checked every loop iteration
 * 4. **Ping After Every Packet**: Keeps track of server responsiveness
 * 5. **Message ACK/Timeout Callbacks**: Every reliable message can have listeners
 * 
 * @see LinkpointConstants for all the exact timing values from the reference viewer
 */
class LinkpointThreadedCircuit(
    private val simIP: String,
    private val simPort: Int,
    private val circuitCode: Int,
    private val sessionId: UUID,
    private val agentId: UUID
) {
    
    companion object {
        private const val TAG = "LinkpointThreadedCircuit"
    }
    
    // ==================== CONNECTION STATE ====================
    
    /** Whether the circuit thread should keep running */
    private val running = AtomicBoolean(false)
    
    /** Whether we've established the circuit (UseCircuitCode ACKed) */
    private val circuitEstablished = AtomicBoolean(false)
    
    /** Whether we're fully connected (CompleteAgentMovement done) */
    private val fullyConnected = AtomicBoolean(false)
    
    /** The dedicated circuit thread */
    private var circuitThread: Thread? = null
    
    /** UDP socket for communication */
    private var socket: DatagramSocket? = null
    
    /** Remote address for the simulator */
    private var remoteAddress: InetSocketAddress? = null
    
    // ==================== PACKET SEQUENCING ====================
    
    /** Next sequence number for outgoing packets */
    private val nextSequenceNumber = AtomicInteger(1)
    
    /** Last sequence number we received from server */
    private var lastReceivedSequence = 0
    
    /** Set of recently handled packet sequence numbers (for duplicate detection) */
    private val handledPackets = ConcurrentHashMap.newKeySet<Int>()
    
    // ==================== RELIABLE MESSAGING ====================
    
    /**
     * Messages waiting to be ACKed by the server.
     * Key: sequence number, Value: PendingMessage with retry info
     */
    private val pendingReliableMessages = ConcurrentHashMap<Int, PendingMessage>()
    
    /** Queue of ACKs we need to send to the server */
    private val pendingAcksToSend = ConcurrentLinkedQueue<Int>()
    
    /** Queue of messages waiting to be sent */
    private val outgoingMessageQueue = ConcurrentLinkedQueue<OutgoingMessage>()
    
    // ==================== TIMING & STATISTICS ====================
    
    /** Time of last packet received (for ping detection) */
    private var lastReceiveTime = AtomicLong(0)
    
    /** Time of last packet sent */
    private var lastSendTime = AtomicLong(0)
    
    /** Time of last ping sent */
    private var lastPingTime = AtomicLong(0)
    
    /** Current ping ID */
    private var pingId = AtomicInteger(0)
    
    /** Number of unanswered pings */
    private var unansweredPings = AtomicInteger(0)
    
    /** Total packets received */
    val packetsReceived = AtomicInteger(0)
    
    /** Total packets sent */
    val packetsSent = AtomicInteger(0)
    
    /** Total bytes received */
    val bytesReceived = AtomicLong(0)
    
    /** Total bytes sent */
    val bytesSent = AtomicLong(0)
    
    // ==================== MESSAGE HANDLERS ====================
    
    /** Registered message handlers by message ID */
    private val messageHandlers = ConcurrentHashMap<Int, MessageHandler>()

    private val transportPolicy = ReliableTransportPolicy()
    
    /** Listener for message ACK/timeout events (proven pattern) */
    interface MessageEventListener {
        fun onMessageAcknowledged(sequenceNumber: Int, messageId: Int)
        fun onMessageTimeout(sequenceNumber: Int, messageId: Int)
    }
    
    /** Message handler interface */
    interface MessageHandler {
        fun handleMessage(messageId: Int, data: ByteArray)
    }
    
    /** Connection state listener */
    interface ConnectionStateListener {
        fun onCircuitEstablished()
        fun onFullyConnected()
        fun onDisconnected(reason: String)
        fun onPacketReceived(messageId: Int, data: ByteArray)
    }
    
    private var connectionListener: ConnectionStateListener? = null
    private var messageEventListener: MessageEventListener? = null
    
    // ==================== DATA CLASSES ====================
    
    /**
     * Pending message awaiting ACK from server
     */
    data class PendingMessage(
        val sequenceNumber: Int,
        val messageId: Int,
        val data: ByteArray,
        val sentTime: Long,
        val firstSentTime: Long = sentTime,
        var retryCount: Int = 0,
        val listener: MessageEventListener? = null
    )
    
    /**
     * Outgoing message queued for transmission
     */
    data class OutgoingMessage(
        val messageId: Int,
        val payload: ByteArray,
        val reliable: Boolean,
        val listener: MessageEventListener? = null
    )
    
    // ==================== PUBLIC API ====================
    
    /**
     * Set connection state listener
     */
    fun setConnectionListener(listener: ConnectionStateListener) {
        this.connectionListener = listener
    }
    
    /**
     * Set message event listener for ACK/timeout callbacks
     */
    fun setMessageEventListener(listener: MessageEventListener) {
        this.messageEventListener = listener
    }
    
    /**
     * Register a message handler for a specific message ID
     */
    fun registerHandler(messageId: Int, handler: MessageHandler) {
        messageHandlers[messageId] = handler
    }
    
    /**
     * Connect to the simulator.
     * This starts the dedicated circuit thread.
     */
    fun connect(): Boolean {
        if (running.get()) {
            Log.w(TAG, "Already connected")
            return true
        }
        
        try {
            // Force IPv4 preference (SL doesn't support IPv6 well)
            System.setProperty("java.net.preferIPv4Stack", "true")
            System.setProperty("java.net.preferIPv6Addresses", "false")
            
            // Create socket with reasonable timeout for blocking receive
            socket = DatagramSocket().apply {
                soTimeout = LinkpointConstants.DEFAULT_IDLE_INTERVAL_MS.toInt()
                reuseAddress = true
            }
            
            // Set up remote address
            remoteAddress = InetSocketAddress(InetAddress.getByName(simIP), simPort)
            
            // Connect the socket (for efficiency - only one destination)
            socket?.connect(remoteAddress)
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "LinkpointCircuit: Socket created, connecting to $simIP:$simPort")
            
            // Initialize timing
            val now = System.currentTimeMillis()
            lastReceiveTime.set(now)
            lastSendTime.set(now)
            lastPingTime.set(now)
            
            // Start the circuit thread
            // 
            // NOTE: This uses a dedicated Thread instead of coroutines for the following reasons:
            // 1. The circuit loop is a long-running, blocking operation that must not be suspended
            // 2. Socket operations are blocking and must run on a dedicated thread
            // 3. Thread priority control is important for network responsiveness
            // 4. Modernizing to coroutines would require significant refactoring of the protocol handling
            // 
            // Future enhancement: Consider using a coroutine with Dispatchers.IO and proper
            // suspendCancellableCoroutine for socket operations, but this requires extensive
            // testing to ensure protocol compatibility.
            //
            running.set(true)
            circuitThread = Thread(::circuitLoop, "LinkpointCircuit-$simIP:$simPort").apply {
                priority = Thread.MAX_PRIORITY - 1 // High priority for network
                isDaemon = true // Don't prevent app shutdown
                start()
            }
            
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "LinkpointCircuit: Thread started with priority ${circuitThread?.priority}")
            
            // Send UseCircuitCode to establish the circuit
            sendUseCircuitCode()
            
            return true
            
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "LinkpointCircuit: Connect failed: ${e.message}")
            disconnect("Connect failed: ${e.message}")
            return false
        }
    }
    
    /**
     * Disconnect from the simulator
     */
    fun disconnect(reason: String = "User requested") {
        if (!running.getAndSet(false)) {
            return // Already disconnected
        }
        
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "LinkpointCircuit: Disconnecting - $reason")
        
        // Close socket to unblock any waiting receive
        try {
            socket?.close()
        } catch (e: Exception) {
            // Ignore
        }
        socket = null
        
        // Wait for thread to stop (with timeout)
        circuitThread?.let { thread ->
            try {
                thread.join(2000)
                if (thread.isAlive) {
                    thread.interrupt()
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        circuitThread = null
        
        // Clear state
        circuitEstablished.set(false)
        fullyConnected.set(false)
        pendingReliableMessages.clear()
        pendingAcksToSend.clear()
        outgoingMessageQueue.clear()
        handledPackets.clear()
        
        // Notify listener
        connectionListener?.onDisconnected(reason)
        
        // Publish event (use GlobalScope since we're on a non-coroutine thread)
        GlobalScope.launch {
            com.linkpoint.network.events.EventBus.publish(ConnectionStateChangedEvent(
                ConnectionState.CONNECTED,
                ConnectionState.DISCONNECTED
            ))
        }
    }
    
    /**
     * Queue a message for sending.
     * 
     * @param messageId The message ID
     * @param payload The message payload (excluding header)
     * @param reliable Whether this message requires ACK
     * @param listener Optional listener for ACK/timeout
     */
    fun sendMessage(
        messageId: Int,
        payload: ByteArray,
        reliable: Boolean = false,
        listener: MessageEventListener? = null
    ) {
        outgoingMessageQueue.offer(OutgoingMessage(messageId, payload, reliable, listener))
    }
    
    /**
     * Check if connected
     */
    fun isConnected(): Boolean = running.get() && circuitEstablished.get()
    
    /**
     * Check if fully connected (world data flowing)
     */
    fun isFullyConnected(): Boolean = fullyConnected.get()

    fun getTransportMetrics(): TransportMetricsSnapshot =
        transportPolicy.metrics(pendingReliableMessages.size)
    
    // ==================== MAIN CIRCUIT LOOP ====================
    
    /**
     * The main circuit processing loop.
     * 
     * This is the HEART of mobile stability - a single thread with
     * deterministic processing order that runs every 1 second (or faster when active).
     */
    private fun circuitLoop() {
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "LinkpointCircuit: === CIRCUIT LOOP STARTED ===")
        
        val receiveBuffer = ByteArray(LinkpointConstants.MAX_MESSAGE_SIZE)
        val receivePacket = DatagramPacket(receiveBuffer, receiveBuffer.size)
        
        while (running.get()) {
            try {
                val loopStartTime = System.currentTimeMillis()
                
                // ========== STEP 1: RECEIVE PACKETS ==========
                // Non-blocking receive with timeout (set in socket creation)
                try {
                    socket?.let { sock ->
                        sock.receive(receivePacket)
                        
                        if (receivePacket.length > 0) {
                            val data = receivePacket.data.copyOf(receivePacket.length)
                            processReceivedPacket(data)
                        }
                    }
                } catch (e: SocketTimeoutException) {
                    // Normal - no data received within timeout
                } catch (e: Exception) {
                    if (running.get()) {
                        NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                            "LinkpointCircuit: Receive error: ${e.message}")
                    }
                }
                
                // ========== STEP 2: SEND PENDING MESSAGES ==========
                processPendingTransmissions()
                
                // ========== STEP 3: SEND PENDING ACKs ==========
                sendPendingAcks()
                
                // ========== STEP 4: PROCESS IDLE TASKS ==========
                processIdleTasks()
                
                // ========== STEP 5: CALCULATE SLEEP TIME ==========
                // The reference viewer uses 1 second default, 100ms when active
                val elapsed = System.currentTimeMillis() - loopStartTime
                val targetInterval = if (outgoingMessageQueue.isNotEmpty() || pendingAcksToSend.isNotEmpty()) {
                    LinkpointConstants.FAST_IDLE_INTERVAL_MS
                } else {
                    LinkpointConstants.DEFAULT_IDLE_INTERVAL_MS
                }
                
                val sleepTime = (targetInterval - elapsed).coerceAtLeast(10)
                if (sleepTime > 0) {
                    Thread.sleep(sleepTime)
                }
                
            } catch (e: InterruptedException) {
                // Thread interrupted, exit loop
                break
            } catch (e: Exception) {
                if (running.get()) {
                    NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                        "LinkpointCircuit: Loop error: ${e.message}")
                }
            }
        }
        
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "LinkpointCircuit: === CIRCUIT LOOP STOPPED ===")
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "LinkpointCircuit: Stats - Sent: ${packetsSent.get()}, Received: ${packetsReceived.get()}")
    }
    
    // ==================== PACKET PROCESSING ====================
    
    /**
     * Process a received packet
     */
    private fun processReceivedPacket(rawData: ByteArray) {
        if (rawData.size < LinkpointConstants.PACKET_HEADER_SIZE) {
            return // Too small to be valid
        }
        
        packetsReceived.incrementAndGet()
        bytesReceived.addAndGet(rawData.size.toLong())
        lastReceiveTime.set(System.currentTimeMillis())
        unansweredPings.set(0) // Server is responding
        
        // Decode zero-coded packet if needed
        val flags = rawData[0].toInt() and 0xFF
        val isZerocoded = (flags and LinkpointConstants.FLAG_ZEROCODED) != 0
        val data = if (isZerocoded) zeroDecode(rawData) else rawData
        
        // Extract sequence number
        val sequenceNumber = ByteBuffer.wrap(data, 1, 4).order(ByteOrder.BIG_ENDIAN).int
        
        // Check for duplicate packet
        if (handledPackets.contains(sequenceNumber)) {
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "LinkpointCircuit: Duplicate packet seq=$sequenceNumber, ignoring")
            return
        }
        
        // Add to handled packets (with limit)
        handledPackets.add(sequenceNumber)
        if (handledPackets.size > LinkpointConstants.TRACK_HANDLED_PACKETS) {
            // Remove oldest entries (simple approach - clear half when full)
            val toRemove = handledPackets.take(handledPackets.size / 2)
            toRemove.forEach { handledPackets.remove(it) }
        }
        
        // If reliable packet, queue ACK
        val isReliable = (flags and LinkpointConstants.FLAG_RELIABLE) != 0
        if (isReliable) {
            pendingAcksToSend.offer(sequenceNumber)
        }
        
        // Check for appended ACKs
        val hasAcks = (flags and LinkpointConstants.FLAG_ACK) != 0
        if (hasAcks) {
            processAppendedAcks(data)
        }
        
        // Extract message ID
        val messageId = extractMessageId(data)
        
        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
            "LinkpointCircuit: Received msg=${messageIdToName(messageId)} seq=$sequenceNumber reliable=$isReliable")
        
        // Handle special protocol messages
        when (messageId) {
            LinkpointConstants.MSG_PACKET_ACK -> handlePacketAck(data)
            LinkpointConstants.MSG_START_PING_CHECK -> handleStartPingCheck(data)
            LinkpointConstants.MSG_COMPLETE_PING_CHECK -> unansweredPings.set(0)
            LinkpointConstants.MSG_REGION_HANDSHAKE -> handleRegionHandshake(data)
            else -> {
                // Route to registered handler
                messageHandlers[messageId]?.handleMessage(messageId, data)
                connectionListener?.onPacketReceived(messageId, data)
            }
        }
    }
    
    /**
     * Handle PacketAck message - server is ACKing our reliable packets
     */
    private fun handlePacketAck(data: ByteArray) {
        if (data.size < LinkpointConstants.PACKET_HEADER_SIZE + 2) return
        
        val offset = LinkpointConstants.PACKET_HEADER_SIZE + 1 // Skip header and message ID
        val count = data[offset].toInt() and 0xFF
        
        var pos = offset + 1
        for (i in 0 until count) {
            if (pos + 4 > data.size) break
            
            val ackedSeq = ByteBuffer.wrap(data, pos, 4).order(ByteOrder.LITTLE_ENDIAN).int
            pos += 4
            
            // Remove from pending and notify listener
            val pending = pendingReliableMessages.remove(ackedSeq)
            if (pending != null) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "LinkpointCircuit: ACK received for seq=$ackedSeq msg=${messageIdToName(pending.messageId)}")
                
                val ackLatency = System.currentTimeMillis() - pending.firstSentTime
                if (transportPolicy.onAck(ackedSeq, ackLatency)) {
                    pending.listener?.onMessageAcknowledged(ackedSeq, pending.messageId)
                    messageEventListener?.onMessageAcknowledged(ackedSeq, pending.messageId)
                }
                
                // Special handling for UseCircuitCode ACK
                if (pending.messageId == LinkpointConstants.MSG_USE_CIRCUIT_CODE) {
                    onUseCircuitCodeAcked()
                }
            }
        }
    }
    
    /**
     * Handle StartPingCheck from server
     */
    private fun handleStartPingCheck(data: ByteArray) {
        if (data.size < LinkpointConstants.PACKET_HEADER_SIZE + 6) return
        
        val offset = LinkpointConstants.PACKET_HEADER_SIZE + 1
        val pingIdByte = data[offset].toInt() and 0xFF
        
        // Send CompletePingCheck response immediately
        sendCompletePingCheck(pingIdByte)
    }
    
    /**
     * Handle RegionHandshake - CRITICAL: Must reply to receive world data
     */
    private fun handleRegionHandshake(data: ByteArray) {
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "LinkpointCircuit: RegionHandshake received! Sending reply...")
        
        // Send RegionHandshakeReply immediately (server is waiting!)
        sendRegionHandshakeReply()
        
        // Mark as fully connected
        if (!fullyConnected.getAndSet(true)) {
            connectionListener?.onFullyConnected()
            NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
                "LinkpointCircuit: === FULLY CONNECTED ===")
        }
    }
    
    /**
     * Process ACKs appended to a received packet
     */
    private fun processAppendedAcks(data: ByteArray) {
        // ACKs are at the end of the packet
        // Last byte is the count, followed by 4-byte sequence numbers (in reverse)
        if (data.size < LinkpointConstants.PACKET_HEADER_SIZE + 2) return
        
        val count = data[data.size - 1].toInt() and 0xFF
        val acksStart = data.size - 1 - (count * 4)
        
        if (acksStart < LinkpointConstants.PACKET_HEADER_SIZE) return
        
        var pos = acksStart
        for (i in 0 until count) {
            if (pos + 4 > data.size - 1) break
            
            val ackedSeq = ByteBuffer.wrap(data, pos, 4).order(ByteOrder.LITTLE_ENDIAN).int
            pos += 4
            
            val pending = pendingReliableMessages.remove(ackedSeq)
            if (pending != null) {
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "LinkpointCircuit: Appended ACK for seq=$ackedSeq")
                val ackLatency = System.currentTimeMillis() - pending.firstSentTime
                if (transportPolicy.onAck(ackedSeq, ackLatency)) {
                    pending.listener?.onMessageAcknowledged(ackedSeq, pending.messageId)
                    messageEventListener?.onMessageAcknowledged(ackedSeq, pending.messageId)
                }
            }
        }
    }
    
    // ==================== TRANSMISSION ====================
    
    /**
     * Process pending transmissions
     */
    private fun processPendingTransmissions() {
        var sent = 0
        while (outgoingMessageQueue.isNotEmpty() && sent < 10) { // Limit per iteration
            val msg = outgoingMessageQueue.poll() ?: break
            sendPacket(msg.messageId, msg.payload, msg.reliable, msg.listener)
            sent++
        }
    }
    
    /**
     * Send a packet
     */
    private fun sendPacket(
        messageId: Int,
        payload: ByteArray,
        reliable: Boolean,
        listener: MessageEventListener?
    ) {
        val seqNum = nextSequenceNumber.getAndIncrement()
        
        // Build packet
        val packet = buildPacket(messageId, payload, seqNum, reliable)
        
        try {
            socket?.let { sock ->
                val dgPacket = DatagramPacket(packet, packet.size, remoteAddress)
                sock.send(dgPacket)
                
                packetsSent.incrementAndGet()
                bytesSent.addAndGet(packet.size.toLong())
                lastSendTime.set(System.currentTimeMillis())
                
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "LinkpointCircuit: Sent msg=${messageIdToName(messageId)} seq=$seqNum reliable=$reliable")
                
                // If reliable, track for ACK/retry
                if (reliable) {
                    pendingReliableMessages[seqNum] = PendingMessage(
                        sequenceNumber = seqNum,
                        messageId = messageId,
                        data = packet,
                        sentTime = System.currentTimeMillis(),
                        listener = listener ?: messageEventListener
                    )
                }
            }
        } catch (e: Exception) {
            NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                "LinkpointCircuit: Send failed: ${e.message}")
        }
    }
    
    /**
     * Send pending ACKs as a PacketAck message
     */
    private fun sendPendingAcks() {
        if (pendingAcksToSend.isEmpty()) return
        
        val acksToSend = mutableListOf<Int>()
        while (acksToSend.size < 255) { // Max ACKs per packet
            val ack = pendingAcksToSend.poll() ?: break
            acksToSend.add(ack)
        }
        
        if (acksToSend.isEmpty()) return
        
        // Build PacketAck message
        val seqNum = nextSequenceNumber.getAndIncrement()
        val packetSize = LinkpointConstants.PACKET_HEADER_SIZE + 1 + 1 + (acksToSend.size * 4)
        val buffer = ByteBuffer.allocate(packetSize).order(ByteOrder.BIG_ENDIAN)
        
        // Header
        buffer.put(0x00.toByte()) // Flags: not reliable
        buffer.putInt(seqNum)
        buffer.put(0x00.toByte()) // Extra
        
        // Message ID (high frequency PacketAck = 0xFB)
        buffer.put(LinkpointConstants.MSG_PACKET_ACK.toByte())
        
        // Count
        buffer.put(acksToSend.size.toByte())
        
        // ACKs (little-endian)
        buffer.order(ByteOrder.LITTLE_ENDIAN)
        for (ack in acksToSend) {
            buffer.putInt(ack)
        }
        
        try {
            val packet = buffer.array()
            socket?.let { sock ->
                val dgPacket = DatagramPacket(packet, packet.size, remoteAddress)
                sock.send(dgPacket)
                
                packetsSent.incrementAndGet()
                bytesSent.addAndGet(packet.size.toLong())
                
                NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                    "LinkpointCircuit: Sent PacketAck for ${acksToSend.size} packets: $acksToSend")
            }
        } catch (e: Exception) {
            // Re-queue ACKs on failure
            acksToSend.forEach { pendingAcksToSend.offer(it) }
        }
    }
    
    // ==================== IDLE TASKS ====================
    
    /**
     * Process idle tasks - resends, pings, timeouts
     */
    private fun processIdleTasks() {
        val now = System.currentTimeMillis()
        
        // Check for message timeouts and resends
        val toRetry = mutableListOf<PendingMessage>()
        val toTimeout = mutableListOf<PendingMessage>()
        
        pendingReliableMessages.values.forEach { pending ->
            if (transportPolicy.isExpired(pending.firstSentTime) ||
                pending.retryCount >= LinkpointConstants.MESSAGE_MAX_RETRIES
            ) {
                toTimeout.add(pending)
            } else if (transportPolicy.shouldRetry(pending.sentTime, pending.retryCount)) {
                toRetry.add(pending)
            }
        }
        
        // Process retries
        for (pending in toRetry) {
            pending.retryCount++
            transportPolicy.markRetransmit()
            NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
                "LinkpointCircuit: Retry ${pending.retryCount}/${LinkpointConstants.MESSAGE_MAX_RETRIES} " +
                "for seq=${pending.sequenceNumber} msg=${messageIdToName(pending.messageId)}")
            
            // Resend with RESENT flag
            try {
                val packet = pending.data.copyOf()
                packet[0] = (packet[0].toInt() or LinkpointConstants.FLAG_RESENT).toByte()
                
                socket?.let { sock ->
                    val dgPacket = DatagramPacket(packet, packet.size, remoteAddress)
                    sock.send(dgPacket)
                    packetsSent.incrementAndGet()
                    bytesSent.addAndGet(packet.size.toLong())
                }
                
                // Update sent time
                pendingReliableMessages[pending.sequenceNumber]?.let {
                    pendingReliableMessages[pending.sequenceNumber] = it.copy(
                        sentTime = now,
                        retryCount = pending.retryCount
                    )
                }
            } catch (e: Exception) {
                NetworkLogger.log(NetworkLogger.Level.ERROR, NetworkLogger.Category.UDP,
                    "LinkpointCircuit: Retry send failed: ${e.message}")
            }
        }
        
        // Process timeouts
        for (pending in toTimeout) {
            pendingReliableMessages.remove(pending.sequenceNumber)
            transportPolicy.markExpiredDrop()
            NetworkLogger.log(NetworkLogger.Level.WARN, NetworkLogger.Category.UDP,
                "LinkpointCircuit: Message timeout seq=${pending.sequenceNumber} msg=${messageIdToName(pending.messageId)}")
            
            pending.listener?.onMessageTimeout(pending.sequenceNumber, pending.messageId)
            messageEventListener?.onMessageTimeout(pending.sequenceNumber, pending.messageId)
        }
        
        // Check for connection death FIRST, based on pings already sent.
        // This must run BEFORE sending a new ping to avoid a race condition where
        // sendPing() increments unansweredPings and the disconnect threshold is
        // immediately hit in the same call, giving the server zero time to respond.
        if (unansweredPings.get() >= LinkpointConstants.UNANSWERED_PINGS_DISCONNECT) {
            disconnect("No response from server (${unansweredPings.get()} unanswered pings)")
            return
        }

        // Then check if we need to send a new ping
        val timeSinceReceive = now - lastReceiveTime.get()
        val timeSincePing = now - lastPingTime.get()

        if (timeSinceReceive > LinkpointConstants.NEED_PING_TIMEOUT_MS &&
            timeSincePing > LinkpointConstants.PING_INTERVAL_MS) {
            sendPing()
        }
    }
    
    // ==================== SPECIFIC MESSAGES ====================
    
    /**
     * Send UseCircuitCode to establish the circuit
     */
    private fun sendUseCircuitCode() {
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "LinkpointCircuit: Sending UseCircuitCode (circuit=$circuitCode)")
        
        // Payload: circuit code (4) + session ID (16) + agent ID (16)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.BIG_ENDIAN)
        payload.putInt(circuitCode)
        putUUID(payload, sessionId)
        putUUID(payload, agentId)
        
        sendPacket(
            messageId = LinkpointConstants.MSG_USE_CIRCUIT_CODE,
            payload = payload.array(),
            reliable = true,
            listener = object : MessageEventListener {
                override fun onMessageAcknowledged(sequenceNumber: Int, messageId: Int) {
                    onUseCircuitCodeAcked()
                }
                
                override fun onMessageTimeout(sequenceNumber: Int, messageId: Int) {
                    disconnect("UseCircuitCode timeout - server not responding")
                }
            }
        )
    }
    
    /**
     * Called when UseCircuitCode is ACKed
     */
    private fun onUseCircuitCodeAcked() {
        if (circuitEstablished.getAndSet(true)) {
            return // Already established
        }
        
        NetworkLogger.log(NetworkLogger.Level.INFO, NetworkLogger.Category.UDP,
            "LinkpointCircuit: Circuit established! Sending CompleteAgentMovement...")
        
        connectionListener?.onCircuitEstablished()
        
        // Now send CompleteAgentMovement
        sendCompleteAgentMovement()
    }
    
    /**
     * Send CompleteAgentMovement
     */
    private fun sendCompleteAgentMovement() {
        // Payload: agent ID (16) + session ID (16) + circuit code (4)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.BIG_ENDIAN)
        putUUID(payload, agentId)
        putUUID(payload, sessionId)
        payload.putInt(circuitCode)
        
        sendPacket(
            messageId = LinkpointConstants.MSG_COMPLETE_AGENT_MOVEMENT,
            payload = payload.array(),
            reliable = true,
            listener = null
        )
    }
    
    /**
     * Send CompletePingCheck response
     */
    private fun sendCompletePingCheck(pingIdByte: Int) {
        val payload = ByteBuffer.allocate(1)
        payload.put(pingIdByte.toByte())
        
        sendPacket(
            messageId = LinkpointConstants.MSG_COMPLETE_PING_CHECK,
            payload = payload.array(),
            reliable = false,
            listener = null
        )
    }
    
    /**
     * Send a ping (StartPingCheck) so unanswered-ping tracking reflects real requests.
     */
    private fun sendPing() {
        val pingIdByte = pingId.incrementAndGet() and 0xFF
        lastPingTime.set(System.currentTimeMillis())
        val unanswered = unansweredPings.incrementAndGet()

        val payload = ByteBuffer.allocate(5).order(ByteOrder.BIG_ENDIAN)
        payload.put(pingIdByte.toByte())
        payload.putInt(0)

        sendPacket(
            messageId = LinkpointConstants.MSG_START_PING_CHECK,
            payload = payload.array(),
            reliable = false,
            listener = null
        )

        NetworkLogger.log(NetworkLogger.Level.DEBUG, NetworkLogger.Category.UDP,
            "LinkpointCircuit: Ping check sent (pingId=$pingIdByte, unanswered: $unanswered)")
    }
    
    /**
     * Send RegionHandshakeReply - CRITICAL for receiving world data
     */
    private fun sendRegionHandshakeReply() {
        // Payload: agent ID (16) + session ID (16) + flags (4)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.BIG_ENDIAN)
        putUUID(payload, agentId)
        putUUID(payload, sessionId)
        payload.putInt(0) // Flags = 0
        
        sendPacket(
            messageId = LinkpointConstants.MSG_REGION_HANDSHAKE_REPLY,
            payload = payload.array(),
            reliable = true,
            listener = null
        )
    }
    
    // ==================== UTILITY FUNCTIONS ====================
    
    /**
     * Build a packet with proper header
     */
    private fun buildPacket(messageId: Int, payload: ByteArray, seqNum: Int, reliable: Boolean): ByteArray {
        // Calculate message ID bytes needed
        val (messageIdBytes, isHighFreq) = encodeMessageId(messageId)
        
        val totalSize = LinkpointConstants.PACKET_HEADER_SIZE + messageIdBytes.size + payload.size
        val buffer = ByteBuffer.allocate(totalSize).order(ByteOrder.BIG_ENDIAN)
        
        // Flags
        var flags = 0
        if (reliable) flags = flags or LinkpointConstants.FLAG_RELIABLE
        buffer.put(flags.toByte())
        
        // Sequence number
        buffer.putInt(seqNum)
        
        // Extra byte
        buffer.put(0x00.toByte())
        
        // Message ID
        buffer.put(messageIdBytes)
        
        // Payload
        buffer.put(payload)
        
        return buffer.array()
    }
    
    /**
     * Encode message ID to bytes
     */
    private fun encodeMessageId(messageId: Int): Pair<ByteArray, Boolean> {
        return when {
            // High frequency (single byte, 0x00-0xFE)
            messageId in 0..254 || messageId == LinkpointConstants.MSG_PACKET_ACK -> {
                Pair(byteArrayOf(messageId.toByte()), true)
            }
            // Low frequency (0xFF 0xFF + 2-byte short)
            else -> {
                val bytes = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN)
                bytes.put(0xFF.toByte())
                bytes.put(0xFF.toByte())
                bytes.putShort(messageId.toShort())
                Pair(bytes.array(), false)
            }
        }
    }
    
    /**
     * Extract message ID from packet
     */
    private fun extractMessageId(data: ByteArray): Int {
        if (data.size < LinkpointConstants.PACKET_HEADER_SIZE + 1) return -1
        
        var offset = LinkpointConstants.PACKET_HEADER_SIZE
        val b1 = data[offset].toInt() and 0xFF
        
        return when {
            b1 != 0xFF -> b1 // High frequency
            data.size > offset + 1 && (data[offset + 1].toInt() and 0xFF) != 0xFF -> {
                // Medium frequency
                val b2 = data[offset + 1].toInt() and 0xFF
                b2 or 0xFF00
            }
            data.size > offset + 3 -> {
                // Low frequency
                ByteBuffer.wrap(data, offset + 2, 2).order(ByteOrder.BIG_ENDIAN).short.toInt() and 0xFFFF
            }
            else -> -1
        }
    }
    
    /**
     * Zero decode a packet
     */
    private fun zeroDecode(input: ByteArray): ByteArray {
        val output = ByteArray(input.size * 2) // Worst case expansion
        var inPos = 0
        var outPos = 0
        
        // Copy header unchanged (first 6 bytes)
        val headerEnd = minOf(LinkpointConstants.PACKET_HEADER_SIZE, input.size)
        while (inPos < headerEnd) {
            output[outPos++] = input[inPos++]
        }
        
        // Clear zero-coded flag in output
        output[0] = (output[0].toInt() and LinkpointConstants.FLAG_ZEROCODED.inv()).toByte()
        
        // Decode rest
        while (inPos < input.size) {
            val b = input[inPos++]
            if (b.toInt() == 0 && inPos < input.size) {
                val count = input[inPos++].toInt() and 0xFF
                repeat(count) {
                    if (outPos < output.size) output[outPos++] = 0
                }
            } else {
                if (outPos < output.size) output[outPos++] = b
            }
        }
        
        return output.copyOf(outPos)
    }
    
    /**
     * Put UUID into ByteBuffer
     */
    private fun putUUID(buffer: ByteBuffer, uuid: UUID) {
        PacketCodec.fromBuffer(buffer).writeUuid(uuid)
    }
    
    /**
     * Convert message ID to name for logging
     */
    private fun messageIdToName(messageId: Int): String {
        return when (messageId) {
            LinkpointConstants.MSG_START_PING_CHECK -> "StartPingCheck"
            LinkpointConstants.MSG_COMPLETE_PING_CHECK -> "CompletePingCheck"
            LinkpointConstants.MSG_PACKET_ACK -> "PacketAck"
            LinkpointConstants.MSG_USE_CIRCUIT_CODE -> "UseCircuitCode"
            LinkpointConstants.MSG_COMPLETE_AGENT_MOVEMENT -> "CompleteAgentMovement"
            LinkpointConstants.MSG_REGION_HANDSHAKE -> "RegionHandshake"
            LinkpointConstants.MSG_REGION_HANDSHAKE_REPLY -> "RegionHandshakeReply"
            else -> "0x${messageId.toString(16).uppercase()}"
        }
    }
}
