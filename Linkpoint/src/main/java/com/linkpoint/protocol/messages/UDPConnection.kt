package com.linkpoint.protocol.messages

import android.util.Log
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
 * UDP Connection handler for Second Life protocol
 * Handles reliable and unreliable message transmission
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
        
        // Packet flags
        const val FLAG_ZEROCODED = 0x80
        const val FLAG_RELIABLE = 0x40
        const val FLAG_RESENT = 0x20
        const val FLAG_ACK = 0x10
    }
    
    private var socket: DatagramSocket? = null
    private var receiveJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    
    private val sequenceNumber = AtomicInteger(0)
    private val pendingAcks = ConcurrentHashMap<Int, PendingPacket>()
    private val messageHandlers = ConcurrentHashMap<Int, MessageHandler>()
    
    private var isConnected = false
    
    /**
     * Connect to the simulator
     */
    suspend fun connect(): Boolean = withContext(Dispatchers.IO) {
        try {
            socket = DatagramSocket()
            socket?.soTimeout = 5000
            
            // CRITICAL: Set isConnected BEFORE starting receive loop
            // The receive loop checks this flag in its while condition,
            // so it must be true before the loop starts or it exits immediately
            isConnected = true
            
            // Start receive loop
            receiveJob = scope.launch {
                receiveLoop()
            }
            
            // Send UseCircuitCode message
            sendUseCircuitCode()
            
            // Wait a moment for the circuit to be established
            delay(500)
            
            // Send CompleteAgentMovement to tell the simulator we're ready
            sendCompleteAgentMovement()
            
            Log.i(TAG, "Connected to $simIP:$simPort, circuit established")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Connection failed", e)
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
     * NOTE: Second Life protocol uses network byte order (big-endian) for all fields.
     */
    suspend fun sendCompleteAgentMovement() {
        // CompleteAgentMovement message format:
        // - AgentID (16 bytes, UUID) - big-endian
        // - SessionID (16 bytes, UUID) - big-endian
        // - CircuitCode (4 bytes, U32) - big-endian
        val payload = ByteBuffer.allocate(36).order(ByteOrder.BIG_ENDIAN)
        
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
     * NOTE: Second Life protocol uses network byte order (big-endian) for all fields.
     */
    suspend fun sendRegionHandshakeReply(flags: Int = 0) {
        // RegionHandshakeReply message format:
        // AgentData block:
        // - AgentID (16 bytes, UUID) - big-endian
        // - SessionID (16 bytes, UUID) - big-endian
        // RegionInfo block:
        // - Flags (4 bytes, U32) - big-endian
        val payload = ByteBuffer.allocate(36).order(ByteOrder.BIG_ENDIAN)
        
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
     * NOTE: Second Life protocol uses network byte order (big-endian) for all fields.
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
        // - AgentID (16 bytes, UUID) - big-endian
        // - SessionID (16 bytes, UUID) - big-endian
        // - CircuitCode (4 bytes, U32) - big-endian
        // Throttle block:
        // - GenCounter (4 bytes, U32) - big-endian
        // - Throttles (28 bytes, 7 floats) - big-endian
        val payload = ByteBuffer.allocate(36 + 4 + 28).order(ByteOrder.BIG_ENDIAN)
        
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
     * Disconnect from the simulator
     */
    fun disconnect() {
        isConnected = false
        receiveJob?.cancel()
        socket?.close()
        socket = null
        pendingAcks.clear()
        Log.i(TAG, "Disconnected")
    }
    
    /**
     * Register a handler for a message type
     */
    fun registerHandler(messageId: Int, handler: MessageHandler) {
        messageHandlers[messageId] = handler
    }
    
    /**
     * Send a packet
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
        
        val header = ByteBuffer.allocate(6).order(ByteOrder.BIG_ENDIAN)
        header.put(flags.toByte())
        header.putInt(seqNum)
        header.put(0.toByte()) // Extra header byte
        
        // Determine message ID size
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
        var packetCount = 0
        val maxVerbosePackets = 10000  // Stop verbose logging after this many packets
        
        while (isConnected) {
            try {
                val datagram = DatagramPacket(buffer, buffer.size)
                socket?.receive(datagram)
                
                if (datagram.length > 0) {
                    packetCount++
                    val data = buffer.copyOf(datagram.length)
                    // Log first 10 packets, then every 100th up to 10000
                    if (packetCount <= 10 || (packetCount % 100 == 0 && packetCount <= maxVerbosePackets)) {
                        Log.d(TAG, "Received packet #$packetCount: ${data.size} bytes from ${datagram.address}:${datagram.port}")
                    }
                    processPacket(data)
                }
            } catch (e: java.net.SocketTimeoutException) {
                // Normal timeout, continue
                resendPendingPackets()
            } catch (e: Exception) {
                if (isConnected) {
                    Log.e(TAG, "Receive error", e)
                }
            }
        }
    }
    
    private fun processPacket(data: ByteArray) {
        if (data.size < 6) return
        
        val flags = data[0].toInt() and 0xFF
        val seqNum = ByteBuffer.wrap(data, 1, 4).order(ByteOrder.BIG_ENDIAN).int
        
        val isZerocoded = (flags and FLAG_ZEROCODED) != 0
        val isReliable = (flags and FLAG_RELIABLE) != 0
        val hasAcks = (flags and FLAG_ACK) != 0
        
        var decoded = data
        if (isZerocoded) {
            decoded = zerodecode(data)
        }
        
        // Handle acks at end of packet
        if (hasAcks && decoded.size > 1) {
            val numAcks = decoded[decoded.size - 1].toInt() and 0xFF
            val ackStart = decoded.size - 1 - numAcks * 4
            if (ackStart > 6) {
                for (i in 0 until numAcks) {
                    val offset = ackStart + i * 4
                    val ackSeq = ByteBuffer.wrap(decoded, offset, 4).order(ByteOrder.BIG_ENDIAN).int
                    pendingAcks.remove(ackSeq)
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
        var offset = 6
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
            else -> return
        }
        
        // Dispatch to handler
        val payload = decoded.copyOfRange(offset, decoded.size - if (hasAcks) 1 + (decoded[decoded.size - 1].toInt() and 0xFF) * 4 else 0)
        val handler = messageHandlers[messageId]
        if (handler != null) {
            handler.onMessage(messageId, payload)
        } else {
            // Log unhandled messages for diagnostics
            val msgIdHex = "0x${Integer.toHexString(messageId).uppercase()}"
            Log.v(TAG, "Unhandled message: $msgIdHex (${payload.size} bytes)")
        }
    }
    
    private suspend fun sendAck(seqNum: Int) {
        val payload = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(seqNum).array()
        sendPacket(MessageIds.PACKET_ACK, payload, reliable = false)
    }
    
    /**
     * Write UUID to ByteBuffer in Second Life format.
     * UUIDs are always stored as 16 raw bytes in big-endian order per SL protocol.
     * This method temporarily switches to BIG_ENDIAN (if not already) and restores after.
     */
    private fun ByteBuffer.putUUID(uuid: UUID): ByteBuffer {
        // Ensure UUID bytes are in big-endian order (standard for SL protocol)
        val originalOrder = order()
        order(ByteOrder.BIG_ENDIAN)
        putLong(uuid.mostSignificantBits)
        putLong(uuid.leastSignificantBits)
        order(originalOrder)
        return this
    }
    
    private suspend fun sendUseCircuitCode() {
        // UseCircuitCode message format:
        // - CircuitCode (4 bytes, U32) - big-endian per SL protocol
        // - SessionID (16 bytes, UUID) - big-endian per SL protocol
        // - AgentID (16 bytes, UUID) - big-endian per SL protocol
        //
        // NOTE: Second Life protocol uses network byte order (big-endian) for all fields
        val payload = ByteBuffer.allocate(36).order(ByteOrder.BIG_ENDIAN)
        payload.putInt(circuitCode)
        
        // Session ID (UUID) - already big-endian in putUUID
        payload.putUUID(sessionId)
        
        // Agent ID (UUID) - already big-endian in putUUID
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
        // Skip header (first 6 bytes)
        while (i < 6 && i < data.size) {
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
        // Skip header (first 6 bytes)
        while (i < 6 && i < data.size) {
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
    const val ACCEPT_FRIENDSHIP: Int = 0xFFFF003C.toInt()
    const val DECLINE_FRIENDSHIP: Int = 0xFFFF003D.toInt()
    const val IM_VIA_EMAIL: Int = 0xFFFF00FF.toInt()
    
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
    const val CHAT_FROM_VIEWER: Int = 0xFF50
    const val INSTANT_MESSAGE: Int = 0xFF4B
    const val KILL_OBJECT: Int = 0xFF0C
    const val OBJECT_SELECT: Int = 0xFF09
    const val OBJECT_DESELECT: Int = 0xFF0A
    const val MULTIPLE_OBJECT_UPDATE: Int = 0xFF0B
    const val AGENT_ANIMATION: Int = 0xFF05
    const val SOUND_TRIGGER: Int = 0xFF1D
    const val TYPING_START: Int = 0xFF4C
    const val TYPING_STOP: Int = 0xFF4D
    
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
    const val PACKET_ACK: Int = 0xFFFF00FB.toInt()
}
