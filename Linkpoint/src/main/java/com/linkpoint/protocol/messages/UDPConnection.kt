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
            
            isConnected = true
            Log.i(TAG, "Connected to $simIP:$simPort, circuit established")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Connection failed", e)
            false
        }
    }
    
    /**
     * Send CompleteAgentMovement message.
     * This tells the simulator we're ready to receive world data.
     */
    suspend fun sendCompleteAgentMovement() {
        // CompleteAgentMovement message format:
        // - AgentID (16 bytes, UUID)
        // - SessionID (16 bytes, UUID)
        // - CircuitCode (4 bytes, U32)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
        
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
            messageId < 0xFF -> byteArrayOf(messageId.toByte())
            messageId < 0xFFFF -> {
                byteArrayOf(0xFF.toByte(), (messageId shr 8).toByte(), messageId.toByte())
            }
            else -> {
                byteArrayOf(0xFF.toByte(), 0xFF.toByte(), 
                    (messageId shr 24).toByte(), 
                    (messageId shr 16).toByte(),
                    (messageId shr 8).toByte(), 
                    messageId.toByte())
            }
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
        
        while (isConnected) {
            try {
                val datagram = DatagramPacket(buffer, buffer.size)
                socket?.receive(datagram)
                
                if (datagram.length > 0) {
                    val data = buffer.copyOf(datagram.length)
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
            decoded.size > offset + 2 && decoded[offset + 1] != 0xFF.toByte() -> {
                val id = ((decoded[offset + 1].toInt() and 0xFF) shl 8) or 
                         (decoded[offset + 2].toInt() and 0xFF)
                offset += 3
                id
            }
            decoded.size > offset + 5 -> {
                val id = ByteBuffer.wrap(decoded, offset + 2, 4).order(ByteOrder.BIG_ENDIAN).int
                offset += 6
                id
            }
            else -> return
        }
        
        // Dispatch to handler
        val payload = decoded.copyOfRange(offset, decoded.size - if (hasAcks) 1 + (decoded[decoded.size - 1].toInt() and 0xFF) * 4 else 0)
        messageHandlers[messageId]?.onMessage(messageId, payload)
    }
    
    private suspend fun sendAck(seqNum: Int) {
        val payload = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN).putInt(seqNum).array()
        sendPacket(0xFFFFFFFF.toInt(), payload, reliable = false)
    }
    
    /**
     * Write UUID to ByteBuffer in Second Life format.
     * SL stores UUIDs as 16 raw bytes in little-endian order.
     */
    private fun ByteBuffer.putUUID(uuid: UUID): ByteBuffer {
        // Second Life UUID format: raw 16 bytes
        // The UUID is stored as two 64-bit values in big-endian within the little-endian buffer
        // This matches how they're received/parsed
        putLong(uuid.mostSignificantBits)
        putLong(uuid.leastSignificantBits)
        return this
    }
    
    private suspend fun sendUseCircuitCode() {
        // UseCircuitCode message format:
        // - CircuitCode (4 bytes, U32)
        // - SessionID (16 bytes, UUID)
        // - AgentID (16 bytes, UUID)
        val payload = ByteBuffer.allocate(36).order(ByteOrder.LITTLE_ENDIAN)
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
    
    // Medium frequency (0xFFxx)
    const val AGENT_UPDATE: Int = 0xFF04
    const val CHAT_FROM_VIEWER: Int = 0xFF50
    const val INSTANT_MESSAGE: Int = 0xFF4B
    const val KILL_OBJECT: Int = 0xFF0C
    
    // High frequency (0x00 - 0xFE)
    const val START_PING_CHECK: Int = 0x01
    const val COMPLETE_PING_CHECK: Int = 0x02
    const val OBJECT_UPDATE: Int = 0x0C
    const val OBJECT_UPDATE_COMPRESSED: Int = 0x0D
    const val OBJECT_UPDATE_CACHED: Int = 0x0E
    const val IMPROVED_TERSE_OBJECT_UPDATE: Int = 0x0F
    const val AVATAR_ANIMATION: Int = 0x14
    const val COARSE_LOCATION_UPDATE: Int = 0x06
}
