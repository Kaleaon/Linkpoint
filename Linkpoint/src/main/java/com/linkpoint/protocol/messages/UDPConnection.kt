package com.linkpoint.protocol.messages

import android.util.Log
import com.linkpoint.protocol.types.putUUID
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * UDP Connection handler for Second Life protocol.
 *
 * Packet headers (flags/sequence/message number) are big-endian network order, while message bodies
 * are little-endian per SL message templates; UUIDs remain raw big-endian bytes.
 */
class UDPConnection {
    
    private var simIP: String = ""
    private var simPort: Int = 0
    private var circuitCode: Int = 0
    private var sessionId: UUID = UUID(0, 0)
    private var agentId: UUID = UUID(0, 0)
    
    constructor()
    
    constructor(simIP: String, simPort: Int, circuitCode: Int) {
        this.simIP = simIP
        this.simPort = simPort
        this.circuitCode = circuitCode
    }
    
    fun configure(simIP: String, simPort: Int, circuitCode: Int) {
        this.simIP = simIP
        this.simPort = simPort
        this.circuitCode = circuitCode
    }
    
    /**
     * Set session information for circuit establishment
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
    
    companion object {
        private const val TAG = "UDPConnection"
        private const val BUFFER_SIZE = 65535
        private const val ACK_TIMEOUT_MS = 1000L
        private const val MAX_RETRIES = 5
        private const val PACKET_HEADER_SIZE = 6
        private val HEADER_BYTE_ORDER = ByteOrder.BIG_ENDIAN
        private val BODY_BYTE_ORDER = ByteOrder.LITTLE_ENDIAN
        
        // Packet flags
        const val FLAG_ZEROCODED = 0x80
        const val FLAG_RELIABLE = 0x40
        const val FLAG_RESENT = 0x20
        const val FLAG_ACK = 0x10
        
        // Agent update interval (matches official viewers - 10 updates/sec)
        private const val AGENT_UPDATE_INTERVAL_MS = 100L
        
        // ACK batching configuration
        private const val MAX_ACKS_PER_PACKET = 10
        private const val ACK_FLUSH_INTERVAL_MS = 100L
    }
    
    private var socket: DatagramSocket? = null
    private var receiveJob: Job? = null
    private var agentUpdateJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val sequenceNumber = AtomicInteger(0)
    private val pendingAcks = ConcurrentHashMap<Int, PendingPacket>()
    private val messageHandlers = ConcurrentHashMap<Int, MessageHandler>()
    
    // ACK batching for efficiency (matches official viewer behavior)
    private val pendingAckIds = mutableListOf<Int>()
    @Volatile private var lastAckFlush = System.currentTimeMillis()
    
    // Current agent state for AgentUpdate messages
    @Volatile private var currentPosition = floatArrayOf(128f, 128f, 25f)
    @Volatile private var currentRotation = floatArrayOf(0f, 0f, 0f, 1f) // quaternion
    @Volatile private var currentLookAt = floatArrayOf(1f, 0f, 0f)
    @Volatile private var agentState: Int = 0
    @Volatile private var controlFlags: Int = 0
    
    private var isConnected = false
    
    /**
     * Connect to the simulator
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ INITIATING UDP CONNECTION                                          ║")
            Log.i(TAG, "║ Target: $simIP:$simPort                                               ║")
            Log.i(TAG, "║ Circuit Code: $circuitCode                                                ║")
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            
            socket = DatagramSocket()
            socket?.soTimeout = 5000
            
            Log.i(TAG, "✓ Datagram socket created")
            
            // CRITICAL: Set isConnected BEFORE starting receive loop
            // The receive loop checks this flag in its while condition,
            // so it must be true before the loop starts or it exits immediately
            isConnected = true
            
            Log.i(TAG, "✓ isConnected flag set to true")
            
            // Start receive loop
            receiveJob = scope.launch {
                receiveLoop()
            }
            
            Log.i(TAG, "✓ Receive loop started")
            
            // Send UseCircuitCode message
            Log.i(TAG, "→ Sending UseCircuitCode...")
            sendUseCircuitCode()
            Log.i(TAG, "✓ UseCircuitCode sent")
            
            // Wait a moment for the circuit to be established
            Log.d(TAG, "  Waiting 500ms for circuit establishment...")
            delay(500)
            
            // Send AgentThrottle to set bandwidth allocation
            // This is CRITICAL - must be sent before CompleteAgentMovement
            Log.i(TAG, "→ Sending AgentThrottle (bandwidth configuration)...")
            sendAgentThrottle()
            Log.i(TAG, "✓ AgentThrottle sent")
            
            // Wait a moment
            Log.d(TAG, "  Waiting 200ms after AgentThrottle...")
            delay(200)
            
            // Send CompleteAgentMovement to tell the simulator we're ready
            Log.i(TAG, "→ Sending CompleteAgentMovement...")
            sendCompleteAgentMovement()
            Log.i(TAG, "✓ CompleteAgentMovement sent")
            
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            Log.i(TAG, "║ UDP CONNECTION ESTABLISHED                                         ║")
            Log.i(TAG, "║ Waiting for simulator to send RegionHandshake...                    ║")
            Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
            
            true
        } catch (e: Exception) {
            Log.e(TAG, "❌ Connection failed", e)
            isConnected = false
            receiveJob?.cancel()
            socket?.close()
            socket = null
            false
        }
    }
    
    /**
     * Send CompleteAgentMovement message.
     * This tells the simulator we're ready to receive world data.
     * 
     * NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
     */
    suspend fun sendCompleteAgentMovement() {
        // CompleteAgentMovement message format:
        // - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - CircuitCode (4 bytes, U32) - little-endian
        val payload = ByteBuffer.allocate(36).order(BODY_BYTE_ORDER)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Circuit code
        payload.putInt(circuitCode)
        
        Log.d(TAG, "Sending CompleteAgentMovement")
        sendPacket(MessageIds.COMPLETE_AGENT_MOVEMENT, payload.array(), reliable = true)
    }
    
    /**
     * Send RegionHandshakeReply message.
     * This acknowledges the RegionHandshake and is REQUIRED for the simulator
     * to start sending world data (objects, textures, etc.).
     * 
     * Based on Lumiya's SLAgentCircuit.sendRegionHandshakeReply()
     * 
     * NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
     */
    suspend fun sendRegionHandshakeReply(flags: Int = 0) {
        // RegionHandshakeReply message format:
        // AgentData block:
        // - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // RegionInfo block:
        // - Flags (4 bytes, U32) - little-endian
        val payload = ByteBuffer.allocate(36).order(BODY_BYTE_ORDER)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Flags (typically 0)
        payload.putInt(flags)
        
        Log.d(TAG, "Sending RegionHandshakeReply")
        sendPacket(MessageIds.REGION_HANDSHAKE_REPLY, payload.array(), reliable = true)
    }
    
    /**
     * Send AgentThrottle message to set bandwidth allocations.
     * This tells the simulator how much bandwidth we want for different data types.
     * 
     * Based on Lumiya's SLAgentCircuit.sendAgentThrottle()
     * 
     * NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
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
        // AgentThrottle message format:
        // AgentData block:
        // - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - CircuitCode (4 bytes, U32) - little-endian
        // Throttle block:
        // - GenCounter (4 bytes, U32) - little-endian
        // - Throttles (28 bytes, 7 floats) - little-endian
        val payload = ByteBuffer.allocate(36 + 4 + 28).order(BODY_BYTE_ORDER)
        
        // Agent ID
        payload.putUUID(agentId)
        
        // Session ID
        payload.putUUID(sessionId)
        
        // Circuit code
        payload.putInt(circuitCode)
        
        // GenCounter (increment each time we send)
        payload.putInt(1)
        
        // Throttles - 7 float values for bandwidth allocation
        payload.putFloat(resend)    // Resend packets
        payload.putFloat(land)      // Land/terrain data
        payload.putFloat(wind)      // Wind data
        payload.putFloat(cloud)     // Cloud data
        payload.putFloat(task)      // Object updates (tasks)
        payload.putFloat(texture)   // Texture data
        payload.putFloat(asset)     // Asset data
        
        Log.d(TAG, "Sending AgentThrottle")
        sendPacket(MessageIds.AGENT_THROTTLE, payload.array(), reliable = true)
    }
    
    /**
     * Send AgentUpdate message.
     * 
     * This is CRITICAL for movement and interaction in Second Life.
     * Official viewers send this approximately 10 times per second.
     * The simulator uses this to update the agent's position and camera.
     * 
     * Based on LibreMetaverse AgentManager.SendAgentUpdate() and
     * Firestorm LLAgent::sendAgentUpdate()
     */
    suspend fun sendAgentUpdate(
        bodyRotation: FloatArray = currentRotation,
        headRotation: FloatArray = currentRotation,
        state: Int = agentState,
        cameraCenter: FloatArray = currentPosition,
        cameraAtAxis: FloatArray = currentLookAt,
        cameraLeftAxis: FloatArray = floatArrayOf(-currentLookAt[1], currentLookAt[0], 0f),
        cameraUpAxis: FloatArray = floatArrayOf(0f, 0f, 1f),
        far: Float = 128f,
        controlFlags: Int = this.controlFlags,
        flags: Int = 0
    ) {
        // AgentUpdate message format (High frequency, ID 0x04):
        // AgentData block:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // - BodyRotation (12 bytes, packed quaternion - 3 floats, w computed)
        // - HeadRotation (12 bytes, packed quaternion)
        // - State (1 byte)
        // - CameraCenter (12 bytes, Vector3)
        // - CameraAtAxis (12 bytes, Vector3)
        // - CameraLeftAxis (12 bytes, Vector3)
        // - CameraUpAxis (12 bytes, Vector3)
        // - Far (4 bytes, F32)
        // - ControlFlags (4 bytes, U32)
        // - Flags (1 byte)
        
        val payload = ByteBuffer.allocate(114).order(BODY_BYTE_ORDER)
        
        // AgentID
        payload.putUUID(agentId)
        
        // SessionID
        payload.putUUID(sessionId)
        
        // Body rotation (packed quaternion - x, y, z, w computed on server)
        payload.putFloat(bodyRotation[0])
        payload.putFloat(bodyRotation[1])
        payload.putFloat(bodyRotation[2])
        
        // Head rotation
        payload.putFloat(headRotation[0])
        payload.putFloat(headRotation[1])
        payload.putFloat(headRotation[2])
        
        // State (0 = standing, 1 = sitting, etc.)
        payload.put(state.toByte())
        
        // Camera center
        payload.putFloat(cameraCenter[0])
        payload.putFloat(cameraCenter[1])
        payload.putFloat(cameraCenter[2])
        
        // Camera at axis (look direction)
        payload.putFloat(cameraAtAxis[0])
        payload.putFloat(cameraAtAxis[1])
        payload.putFloat(cameraAtAxis[2])
        
        // Camera left axis
        payload.putFloat(cameraLeftAxis[0])
        payload.putFloat(cameraLeftAxis[1])
        payload.putFloat(cameraLeftAxis[2])
        
        // Camera up axis
        payload.putFloat(cameraUpAxis[0])
        payload.putFloat(cameraUpAxis[1])
        payload.putFloat(cameraUpAxis[2])
        
        // Far distance
        payload.putFloat(far)
        
        // Control flags (movement, etc.)
        payload.putInt(controlFlags)
        
        // Flags
        payload.put(flags.toByte())
        
        sendPacket(MessageIds.AGENT_UPDATE, payload.array(), reliable = false)
    }
    
    /**
     * Update agent position for AgentUpdate messages.
     * Call this when the avatar moves.
     */
    fun updateAgentPosition(x: Float, y: Float, z: Float) {
        currentPosition = floatArrayOf(x, y, z)
    }
    
    /**
     * Update agent rotation for AgentUpdate messages.
     * @param rotation Quaternion as [x, y, z, w]
     */
    fun updateAgentRotation(rotation: FloatArray) {
        if (rotation.size >= 4) {
            currentRotation = rotation.copyOf()
        }
    }
    
    /**
     * Update camera look-at direction.
     */
    fun updateLookAt(x: Float, y: Float, z: Float) {
        currentLookAt = floatArrayOf(x, y, z)
    }
    
    /**
     * Set control flags (for movement).
     */
    fun setControlFlags(flags: Int) {
        controlFlags = flags
    }
    
    /**
     * Start sending periodic AgentUpdate messages.
     * This is required for proper operation in Second Life.
     */
    fun startAgentUpdates() {
        agentUpdateJob?.cancel()
        agentUpdateJob = scope.launch {
            Log.d(TAG, "Starting periodic AgentUpdate messages")
            while (isConnected) {
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
     * Handle StartPingCheck message from simulator.
     * We must respond with CompletePingCheck to maintain the connection.
     * 
     * Based on LibreMetaverse NetworkManager.HandleStartPingCheck()
     */
    suspend fun handleStartPingCheck(pingId: Byte, oldestUnacked: Int) {
        val payload = ByteBuffer.allocate(5).order(BODY_BYTE_ORDER)
        payload.put(pingId)
        payload.putInt(getOldestUnackedSequence())
        
        Log.d(TAG, "Responding to ping check $pingId")
        sendPacket(MessageIds.COMPLETE_PING_CHECK, payload.array(), reliable = false)
    }
    
    /**
     * Get oldest unacknowledged sequence number for ping response.
     */
    private fun getOldestUnackedSequence(): Int {
        return pendingAcks.keys.minOrNull() ?: 0
    }
    
    /**
     * Queue an ACK for batching (more efficient than individual ACKs).
     * This matches official viewer behavior.
     */
    fun queueAck(sequenceNumber: Int) {
        synchronized(pendingAckIds) {
            pendingAckIds.add(sequenceNumber)
            
            // Flush if we have enough ACKs or enough time has passed
            if (pendingAckIds.size >= MAX_ACKS_PER_PACKET || 
                System.currentTimeMillis() - lastAckFlush > ACK_FLUSH_INTERVAL_MS) {
                scope.launch { flushAcks() }
            }
        }
    }
    
    /**
     * Send batched ACKs.
     */
    private suspend fun flushAcks() {
        val acksToSend: List<Int>
        synchronized(pendingAckIds) {
            if (pendingAckIds.isEmpty()) return
            acksToSend = pendingAckIds.toList()
            pendingAckIds.clear()
            lastAckFlush = System.currentTimeMillis()
        }
        
        val payload = ByteBuffer.allocate(1 + acksToSend.size * 4).order(BODY_BYTE_ORDER)
        payload.put(acksToSend.size.toByte())
        acksToSend.forEach { payload.putInt(it) }
        
        Log.d(TAG, "Sending ${acksToSend.size} batched ACKs")
        sendPacket(MessageIds.PACKET_ACK, payload.array(), reliable = false)
    }
    
    /**
     * Disconnect from the simulator
     */
    fun disconnect() {
        isConnected = false
        agentUpdateJob?.cancel()
        receiveJob?.cancel()
        
        // Flush any remaining ACKs
        scope.launch { flushAcks() }
        
        socket?.close()
        socket = null
        pendingAcks.clear()
        synchronized(pendingAckIds) { pendingAckIds.clear() }
        Log.i(TAG, "Disconnected")
    }
    
    /**
     * Register a handler for a message type
     */
    fun registerHandler(messageId: Int, handler: MessageHandler) {
        messageHandlers[messageId] = handler
    }
    
    /**
     * Send a packet.
     *
     * The payload must already be encoded in little-endian message order; the header and message
     * number are written in network (big-endian) order.
     */
    suspend fun sendPacket(
        messageId: Int,
        payload: ByteArray,
        reliable: Boolean = false,
        zerocoded: Boolean = false
    ) = withContext(Dispatchers.IO) {
        val socket = this@UDPConnection.socket ?: return@withContext
        
        val seqNum = sequenceNumber.getAndIncrement()
        
        // Build packet header
        var flags = 0
        if (reliable) flags = flags or FLAG_RELIABLE
        if (zerocoded) flags = flags or FLAG_ZEROCODED
        
        val header = ByteBuffer.allocate(PACKET_HEADER_SIZE).order(HEADER_BYTE_ORDER)
        header.put(flags.toByte())
        header.putInt(seqNum)
        header.put(0.toByte()) // Extra header byte
        
        // Determine message ID size (network order, per SL/PyOGP packet layout).
        val messageBytes = when {
            messageId <= 0xFF -> byteArrayOf(messageId.toByte())
            messageId in 0xFF00..0xFFFF -> {
                byteArrayOf(0xFF.toByte(), (messageId and 0xFF).toByte())
            }
            messageId ushr 16 == 0xFFFF -> {
                byteArrayOf(
                    0xFF.toByte(),
                    0xFF.toByte(),
                    ((messageId shr 8) and 0xFF).toByte(),
                    (messageId and 0xFF).toByte()
                )
            }
            else -> byteArrayOf(messageId.toByte())
        }
        
        val packet = header.array() + messageBytes + payload
        val finalPacket = if (zerocoded) zeroencode(packet) else packet
        
        try {
            val address = InetAddress.getByName(simIP)
            val datagram = DatagramPacket(finalPacket, finalPacket.size, address, simPort)
            socket.send(datagram)
            
            if (reliable) {
                pendingAcks[seqNum] = PendingPacket(seqNum, finalPacket, System.currentTimeMillis())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to send packet", e)
        }
    }
    
    private suspend fun receiveLoop() {
        val buffer = ByteArray(BUFFER_SIZE)
        var packetsReceived = 0
        var totalBytesReceived = 0L
        
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ UDP RECEIVE LOOP STARTED                                            ║")
        Log.i(TAG, "║ Will log all incoming packets for debugging                          ║")
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        
        while (isConnected) {
            try {
                val datagram = DatagramPacket(buffer, buffer.size)
                socket?.receive(datagram)
                
                if (datagram.length > 0) {
                    packetsReceived++
                    totalBytesReceived += datagram.length
                    
                    val data = buffer.copyOf(datagram.length)
                    
                    // Log packet reception
                    Log.i(TAG, "📦 PACKET RECEIVED #${packetsReceived}: ${datagram.length} bytes from ${datagram.address}:${datagram.port}")
                    
                    // Log raw hex for first few packets (for debugging)
                    if (packetsReceived <= 10) {
                        val hexPreview = data.take(32).joinToString(" ") { "%02X".format(it) }
                        Log.d(TAG, "   Raw preview: $hexPreview")
                    }
                    
                    processPacket(data)
                }
            } catch (e: java.net.SocketTimeoutException) {
                // Normal timeout, continue
                if (packetsReceived == 0) {
                    Log.w(TAG, "⚠️ No packets received yet, waiting...")
                }
                resendPendingPackets()
            } catch (e: Exception) {
                if (isConnected) {
                    Log.e(TAG, "❌ Receive error", e)
                }
            }
        }
        
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
        Log.i(TAG, "║ UDP RECEIVE LOOP STOPPED                                             ║")
        Log.i(TAG, "║ Total packets received: $packetsReceived                                 ║")
        Log.i(TAG, "║ Total bytes received: $totalBytesReceived                                   ║")
        Log.i(TAG, "═══════════════════════════════════════════════════════════════════")
    }
    
    private fun processPacket(data: ByteArray) {
        if (data.size < PACKET_HEADER_SIZE) {
            Log.w(TAG, "⚠️ Packet too small: ${data.size} bytes (minimum $PACKET_HEADER_SIZE)")
            return
        }
        
        val flags = data[0].toInt() and 0xFF
        val seqNum = ByteBuffer.wrap(data, 1, 4).order(HEADER_BYTE_ORDER).int
        
        val isZerocoded = (flags and FLAG_ZEROCODED) != 0
        val isReliable = (flags and FLAG_RELIABLE) != 0
        val hasAcks = (flags and FLAG_ACK) != 0
        
        // Log packet details
        val flagsStr = buildString {
            if (isZerocoded) append("[ZERO]")
            if (isReliable) append("[RELIABLE]")
            if (hasAcks) append("[ACK]")
        }
        Log.d(TAG, "   Packet #$seqNum $flagsStr - ${data.size} bytes")
        
        var decoded = data
        if (isZerocoded) {
            decoded = zerodecode(data)
            Log.d(TAG, "   Zero-decoded: ${decoded.size} bytes")
        }
        
        // Handle acks at end of packet
        // Appended ACKs are little-endian per SL protocol (unlike header which is big-endian)
        if (hasAcks && decoded.size > 1) {
            val numAcks = decoded[decoded.size - 1].toInt() and 0xFF
            if (numAcks > 0) {
                Log.d(TAG, "   Contains $numAcks ACK(s)")
                val ackStart = decoded.size - 1 - numAcks * 4
                if (ackStart > PACKET_HEADER_SIZE) {
                    for (i in 0 until numAcks) {
                        val offset = ackStart + i * 4
                        // Appended ACKs are little-endian (body byte order)
                        val ackSeq = ByteBuffer.wrap(decoded, offset, 4).order(BODY_BYTE_ORDER).int
                        pendingAcks.remove(ackSeq)
                        Log.d(TAG, "   ACK for packet #$ackSeq")
                    }
                }
            }
        }
        
        // Send ack if reliable
        if (isReliable) {
            scope.launch {
                sendAck(seqNum)
            }
        }
        
        // Parse message ID
        var offset = PACKET_HEADER_SIZE
        val messageId = when {
            decoded[offset] != 0xFF.toByte() -> {
                val id = decoded[offset].toInt() and 0xFF
                offset++
                id
            }
            decoded.size > offset + 1 && decoded[offset + 1] != 0xFF.toByte() -> {
                val id = (0xFF00 or (decoded[offset + 1].toInt() and 0xFF))
                offset += 2
                id
            }
            decoded.size > offset + 3 -> {
                val id = ((decoded[offset + 2].toInt() and 0xFF) shl 8) or
                    (decoded[offset + 3].toInt() and 0xFF)
                offset += 4
                (0xFFFF shl 16) or id
            }
            else -> {
                Log.w(TAG, "   ⚠️ Could not parse message ID")
                return
            }
        }
        
        // Get message name for logging
        val messageName = getMessageName(messageId)
        Log.i(TAG, "   📨 Message: $messageName (0x${messageId.toString(16).uppercase()})")
        
        // Dispatch to handler
        val payload = decoded.copyOfRange(offset, decoded.size - if (hasAcks) 1 + (decoded[decoded.size - 1].toInt() and 0xFF) * 4 else 0)
        
        val handler = messageHandlers[messageId]
        if (handler != null) {
            Log.d(TAG, "   → Dispatching to handler (${payload.size} bytes payload)")
            try {
                handler.onMessage(messageId, payload)
                Log.d(TAG, "   ✓ Handler executed successfully")
            } catch (e: Exception) {
                Log.e(TAG, "   ✗ Handler failed", e)
            }
        } else {
            Log.w(TAG, "   ⚠️ No handler registered for message: $messageName")
        }
    }
    
    private fun getMessageName(messageId: Int): String {
        return when (messageId) {
            MessageIds.USE_CIRCUIT_CODE -> "UseCircuitCode"
            MessageIds.COMPLETE_AGENT_MOVEMENT -> "CompleteAgentMovement"
            MessageIds.REGION_HANDSHAKE -> "⭐ RegionHandshake"
            MessageIds.REGION_HANDSHAKE_REPLY -> "RegionHandshakeReply"
            MessageIds.AGENT_THROTTLE -> "AgentThrottle"
            MessageIds.AGENT_MOVEMENT_COMPLETE -> "AgentMovementComplete"
            MessageIds.CHAT_FROM_SIMULATOR -> "ChatFromSimulator"
            MessageIds.OBJECT_UPDATE -> "ObjectUpdate"
            MessageIds.OBJECT_UPDATE_COMPRESSED -> "ObjectUpdateCompressed"
            MessageIds.IMPROVED_TERSE_OBJECT_UPDATE -> "ImprovedTerseObjectUpdate"
            MessageIds.AVATAR_ANIMATION -> "AvatarAnimation"
            MessageIds.COARSE_LOCATION_UPDATE -> "CoarseLocationUpdate"
            MessageIds.KILL_OBJECT -> "KillObject"
            MessageIds.START_PING_CHECK -> "StartPingCheck"
            MessageIds.COMPLETE_PING_CHECK -> "CompletePingCheck"
            else -> "Unknown(0x${messageId.toString(16).uppercase()})"
        }
    }
    
    private suspend fun sendAck(seqNum: Int) {
        // PacketAck message format per SL protocol:
        // - Count (U8): Number of ACKs (1 in this case)
        // - ID (U32): Sequence number, little-endian per message template
        val payload = ByteBuffer.allocate(5).order(BODY_BYTE_ORDER)
        payload.put(1.toByte())  // Count: 1 ACK
        payload.putInt(seqNum)
        sendPacket(MessageIds.PACKET_ACK, payload.array(), reliable = false)
    }
    
    private suspend fun sendUseCircuitCode() {
        // UseCircuitCode message format:
        // - CircuitCode (4 bytes, U32) - little-endian
        // - SessionID (16 bytes, UUID) - raw bytes (big-endian UUID)
        // - AgentID (16 bytes, UUID) - raw bytes (big-endian UUID)
        //
        // NOTE: Second Life message blocks use little-endian encoding; UUID bytes remain big-endian.
        val payload = ByteBuffer.allocate(36).order(BODY_BYTE_ORDER)
        payload.putInt(circuitCode)
        
        // Session ID (UUID)
        payload.putUUID(sessionId)
        
        // Agent ID (UUID)
        payload.putUUID(agentId)
        
        Log.d(TAG, "Sending UseCircuitCode: circuit=$circuitCode, agent=${agentId.toString().take(8)}...")
        sendPacket(MessageIds.USE_CIRCUIT_CODE, payload.array(), reliable = true)
    }
    
    private suspend fun resendPendingPackets() {
        val now = System.currentTimeMillis()
        pendingAcks.values.filter { now - it.timestamp > ACK_TIMEOUT_MS }.forEach { pending ->
            if (pending.retries < MAX_RETRIES) {
                // Resend with resent flag
                val resendPacket = pending.data.copyOf()
                resendPacket[0] = (resendPacket[0].toInt() or FLAG_RESENT).toByte()
                
                try {
                    val address = InetAddress.getByName(simIP)
                    val datagram = DatagramPacket(resendPacket, resendPacket.size, address, simPort)
                    socket?.send(datagram)
                    pending.retries++
                    pending.timestamp = now
                } catch (e: Exception) {
                    Log.w(TAG, "Resend failed for seq ${pending.seqNum}")
                }
            } else {
                pendingAcks.remove(pending.seqNum)
                Log.w(TAG, "Packet ${pending.seqNum} dropped after max retries")
            }
        }
    }
    
    private fun zeroencode(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        var i = 0
        // Skip header (packet header is always network order)
        while (i < PACKET_HEADER_SIZE && i < data.size) {
            result.add(data[i])
            i++
        }
        while (i < data.size) {
            if (data[i] == 0.toByte()) {
                var count = 0
                while (i < data.size && data[i] == 0.toByte() && count < 255) {
                    count++
                    i++
                }
                result.add(0)
                result.add(count.toByte())
            } else {
                result.add(data[i])
                i++
            }
        }
        return result.toByteArray()
    }
    
    private fun zerodecode(data: ByteArray): ByteArray {
        val result = mutableListOf<Byte>()
        var i = 0
        // Skip header (packet header is always network order)
        while (i < PACKET_HEADER_SIZE && i < data.size) {
            result.add(data[i])
            i++
        }
        while (i < data.size) {
            if (data[i] == 0.toByte() && i + 1 < data.size) {
                val count = data[i + 1].toInt() and 0xFF
                repeat(count) { result.add(0) }
                i += 2
            } else {
                result.add(data[i])
                i++
            }
        }
        return result.toByteArray()
    }
    
    data class PendingPacket(
        val seqNum: Int,
        val data: ByteArray,
        var timestamp: Long,
        var retries: Int = 0
    )
    
    // ==================== DIAGNOSTIC METHODS ====================
    
    /**
     * Check if the UDP connection is currently connected
     */
    fun isCurrentlyConnected(): Boolean = isConnected
    
    /**
     * Get the current sequence number (packets sent)
     */
    fun getCurrentSequenceNumber(): Int = sequenceNumber.get()
    
    /**
     * Get the number of pending acknowledgments (packets awaiting ACK)
     */
    fun getPendingAckCount(): Int = pendingAcks.size
    
    /**
     * Get the number of registered message handlers
     */
    fun getRegisteredHandlerCount(): Int = messageHandlers.size
    
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
                else -> "0x${id.toString(16).uppercase()}"
            }
        }
    }
    
    /**
     * Get comprehensive diagnostic data for debug reports
     */
    fun getDiagnostics(): UDPDiagnostics {
        val pendingPacketInfo = pendingAcks.values.map { packet ->
            PendingPacketInfo(
                seqNum = packet.seqNum,
                retries = packet.retries,
                ageMs = System.currentTimeMillis() - packet.timestamp
            )
        }
        
        return UDPDiagnostics(
            isConnected = isConnected,
            simIP = simIP,
            simPort = simPort,
            circuitCode = circuitCode,
            agentId = agentId,
            sessionId = sessionId,
            sequenceNumber = sequenceNumber.get(),
            pendingAckCount = pendingAcks.size,
            registeredHandlerCount = messageHandlers.size,
            registeredHandlers = getRegisteredHandlerIds(),
            pendingPackets = pendingPacketInfo,
            socketOpen = socket?.let { !it.isClosed } ?: false,
            receiveLoopActive = receiveJob?.isActive == true
        )
    }
    
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
}

fun interface MessageHandler {
    fun onMessage(messageId: Int, payload: ByteArray)
}

/**
 * Common SL message IDs
 */
object MessageIds {
    // Low frequency (0xFFFFxxxx)
    const val USE_CIRCUIT_CODE: Int = 0xFFFF0003.toInt()
    const val COMPLETE_AGENT_MOVEMENT: Int = 0xFFFF00F9.toInt()
    const val LOGOUT_REQUEST: Int = 0xFFFF00FC.toInt()
    const val CHAT_FROM_SIMULATOR: Int = 0xFFFF00A3.toInt()
    const val IMPROVED_INSTANT_MESSAGE: Int = 0xFFFF00FE.toInt()
    const val REGION_HANDSHAKE: Int = 0xFFFF0094.toInt()
    const val AGENT_MOVEMENT_COMPLETE: Int = 0xFFFF00FA.toInt()
    const val OBJECT_LINK: Int = 0xFFFF0066.toInt()
    const val OBJECT_DELINK: Int = 0xFFFF0067.toInt()
    const val REZ_OBJECT: Int = 0xFFFF0068.toInt()
    const val DEREZ_OBJECT: Int = 0xFFFF0069.toInt()
    const val OBJECT_DELETE: Int = 0xFFFF0089.toInt()
    const val OBJECT_NAME: Int = 0xFFFF008C.toInt()
    const val OBJECT_DESCRIPTION: Int = 0xFFFF008D.toInt()
    const val OBJECT_GRAB: Int = 0xFFFF0117.toInt()
    const val OBJECT_DEGRAB: Int = 0xFFFF0118.toInt()
    const val AGENT_REQUEST_SIT: Int = 0xFFFF00F5.toInt()
    const val AGENT_SIT: Int = 0xFFFF00F7.toInt()
    const val OFFER_FRIENDSHIP: Int = 0xFFFF0039.toInt()
    const val TERMINATE_FRIENDSHIP: Int = 0xFFFF003A.toInt()
    const val GRANT_USER_RIGHTS: Int = 0xFFFF003B.toInt()
    const val FIND_AGENT: Int = 0xFFFF001F.toInt()
    const val START_LURE: Int = 0xFFFF0040.toInt()
    const val TELEPORT_LURE_REQUEST: Int = 0xFFFF0041.toInt()
    const val AVATAR_PROPERTIES_UPDATE: Int = 0xFFFF00A6.toInt()
    const val GROUP_ROLE_DATA_REQUEST: Int = 0xFFFF0176.toInt()
    const val ACTIVATE_GROUP: Int = 0xFFFF0177.toInt()
    const val JOIN_GROUP_REQUEST: Int = 0xFFFF0178.toInt()
    const val LEAVE_GROUP_REQUEST: Int = 0xFFFF0179.toInt()
    const val GROUP_PROFILE_REQUEST: Int = 0xFFFF017A.toInt()
    const val GROUP_MEMBERS_REQUEST: Int = 0xFFFF017B.toInt()
    const val ACCEPT_FRIENDSHIP: Int = 0xFFFF003C.toInt()
    const val DECLINE_FRIENDSHIP: Int = 0xFFFF003D.toInt()
    const val IM_VIA_EMAIL: Int = 0xFFFF00FF.toInt()
    
    // Teleport messages
    const val TELEPORT_LOCATION_REQUEST: Int = 0xFFFF0042.toInt()
    const val TELEPORT_LANDMARK_REQUEST: Int = 0xFFFF0043.toInt()
    const val TELEPORT_HOME_REQUEST: Int = 0xFFFF0044.toInt()
    const val TELEPORT_CANCEL: Int = 0xFFFF0045.toInt()
    const val TELEPORT_LOCAL: Int = 0xFFFF0046.toInt()
    const val TELEPORT_REQUEST: Int = 0xFFFF0047.toInt()
    const val TELEPORT_FINISH: Int = 0xFFFF0048.toInt()
    const val TELEPORT_FAILED: Int = 0xFFFF0049.toInt()
    const val TELEPORT_PROGRESS: Int = 0xFFFF004A.toInt()
    
    // Parcel messages
    const val PARCEL_BUY: Int = 0xFFFF00D1.toInt()
    const val PARCEL_DEED_TO_GROUP: Int = 0xFFFF00D2.toInt()
    const val PARCEL_RELEASE: Int = 0xFFFF00D3.toInt()
    const val PARCEL_PROPERTIES_UPDATE: Int = 0xFFFF00D4.toInt()
    const val PARCEL_RETURN_OBJECTS: Int = 0xFFFF00C9.toInt()
    const val PARCEL_ACCESS_LIST_UPDATE: Int = 0xFFFF00CF.toInt()
    const val PARCEL_DISABLE_OBJECTS: Int = 0xFFFF00C9.toInt()
    
    // Medium frequency (0xFFxx)
    const val AGENT_UPDATE: Int = 0xFF04
    const val INSTANT_MESSAGE: Int = 0xFF4B
    const val KILL_OBJECT: Int = 0xFF0C
    const val OBJECT_SELECT: Int = 0xFF09
    const val OBJECT_DESELECT: Int = 0xFF0A
    const val MULTIPLE_OBJECT_UPDATE: Int = 0xFF0B
    const val AGENT_ANIMATION: Int = 0xFF05
    const val SOUND_TRIGGER: Int = 0xFF1D
    
    // Low frequency chat message (0xFFFFxxxx)
    // ChatFromViewer is Low 80 = 0xFFFF0050 per message_template.msg
    const val CHAT_FROM_VIEWER: Int = 0xFFFF0050.toInt()
    
    // High frequency (0x00 - 0xFE)
    const val START_PING_CHECK: Int = 0x01
    const val COMPLETE_PING_CHECK: Int = 0x02
    const val OBJECT_UPDATE: Int = 0x0C
    const val OBJECT_UPDATE_COMPRESSED: Int = 0x0D
    const val OBJECT_UPDATE_CACHED: Int = 0x0E
    const val IMPROVED_TERSE_OBJECT_UPDATE: Int = 0x0F
    const val AVATAR_ANIMATION: Int = 0x14
    const val COARSE_LOCATION_UPDATE: Int = 0x06
    
    // Region/Connection messages
    const val REGION_HANDSHAKE_REPLY: Int = 0xFFFF0095.toInt()
    const val AGENT_THROTTLE: Int = 0xFFFF0099.toInt()
    
    // Packet ACK is high frequency (0xFB)
    const val PACKET_ACK: Int = 0xFB
    
    // Inventory messages
    const val MOVE_INVENTORY_ITEM: Int = 0xFFFF0109.toInt()
    const val COPY_INVENTORY_ITEM: Int = 0xFFFF010A.toInt()
    const val REMOVE_INVENTORY_ITEM: Int = 0xFFFF010B.toInt()
    const val CREATE_INVENTORY_FOLDER: Int = 0xFFFF010C.toInt()
    const val UPDATE_INVENTORY_FOLDER: Int = 0xFFFF010D.toInt()
    const val MOVE_INVENTORY_FOLDER: Int = 0xFFFF010E.toInt()
    const val REMOVE_INVENTORY_FOLDER: Int = 0xFFFF010F.toInt()
    const val UPDATE_INVENTORY_ITEM: Int = 0xFFFF0107.toInt()
    const val FETCH_INVENTORY_REPLY: Int = 0xFFFF0120.toInt()
    const val PURGE_INVENTORY_DESCENDENTS: Int = 0xFFFF0110.toInt()
    
    // Attachment messages
    const val REZ_SINGLE_ATTACHMENT_FROM_INV: Int = 0xFFFF0184.toInt()
    const val REZ_MULTIPLE_ATTACHMENTS_FROM_INV: Int = 0xFFFF0185.toInt()
    const val DETACH_ATTACHMENT_INTO_INV: Int = 0xFFFF0186.toInt()
    
    // Agent appearance
    const val AGENT_SET_APPEARANCE: Int = 0xFFFF0054.toInt()
    const val AGENT_WEARABLES_UPDATE: Int = 0xFFFF00A1.toInt()
    const val AGENT_IS_NOW_WEARING: Int = 0xFFFF00A2.toInt()
}
