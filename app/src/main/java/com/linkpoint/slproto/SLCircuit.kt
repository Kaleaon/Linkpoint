package com.linkpoint.slproto

import com.linkpoint.slproto.messages.PacketAckMessage
import kotlinx.coroutines.*
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Second Life UDP circuit implementation
 * Handles reliable message transmission and acknowledgment
 */
class SLCircuit(
    private val remoteAddress: InetAddress,
    private val remotePort: Int,
) {
    private val socket = DatagramSocket()
    private val sequenceNumber = AtomicInteger(1)
    private val pendingAcks = ConcurrentHashMap<Int, SLMessage>()
    private val receivedPackets = Collections.synchronizedSet(mutableSetOf<Int>())
    private val ackQueue = Collections.synchronizedList(mutableListOf<Int>())

    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Circuit state
    var circuitCode: Int = 0
    var agentId: UUID = UUID.randomUUID()
    var sessionId: UUID = UUID.randomUUID()

    // Callbacks
    var onMessageReceived: ((SLMessage) -> Unit)? = null
    var onCircuitClosed: (() -> Unit)? = null

    companion object {
        private const val ACK_TIMEOUT_MS = 3000L
        private const val MAX_RETRIES = 3
        private const val RECEIVE_BUFFER_SIZE = 65536

        // Message flags
        private const val FLAG_ZERO_CODE: Byte = 0x80.toByte()
        private const val FLAG_RELIABLE: Byte = 0x40
        private const val FLAG_RESENT: Byte = 0x20
        private const val FLAG_ACK: Byte = 0x10
    }

    /**
     * Start the circuit
     */
    fun start() {
        if (isRunning) return
        isRunning = true

        // Start receive loop
        scope.launch {
            receiveLoop()
        }

        // Start ACK sender
        scope.launch {
            ackSenderLoop()
        }

        // Start retry loop
        scope.launch {
            retryLoop()
        }
    }

    /**
     * Stop the circuit
     */
    fun stop() {
        isRunning = false
        scope.cancel()
        socket.close()
    }

    /**
     * Send a message
     */
    fun sendMessage(message: SLMessage) {
        val seqNum = sequenceNumber.getAndIncrement()
        message.seqNum = seqNum

        val buffer = ByteBuffer.allocate(SLMessage.MAX_TRANSMIT_SIZE)
        buffer.order(ByteOrder.BIG_ENDIAN)

        // Build header
        var flags: Byte = 0
        if (message.isReliable) {
            flags = (flags.toInt() or FLAG_RELIABLE.toInt()).toByte()
            pendingAcks[seqNum] = message
            message.sentTimeMillis = System.currentTimeMillis()
        }
        if (message.zeroCoded) {
            flags = (flags.toInt() or FLAG_ZERO_CODE.toInt()).toByte()
        }

        // Add pending ACKs to this packet if available
        val acksToSend =
            synchronized(ackQueue) {
                val acks = ackQueue.take(255.coerceAtMost(ackQueue.size))
                ackQueue.removeAll(acks)
                acks
            }

        if (acksToSend.isNotEmpty()) {
            flags = (flags.toInt() or FLAG_ACK.toInt()).toByte()
        }

        buffer.put(flags)
        buffer.putInt(seqNum)
        buffer.put(0) // Extra header bytes

        // Pack message payload
        val payloadStart = buffer.position()
        message.packPayload(buffer)
        val payloadEnd = buffer.position()

        // Add ACKs at the end if present
        if (acksToSend.isNotEmpty()) {
            for (ack in acksToSend) {
                buffer.putInt(ack)
            }
            buffer.put(acksToSend.size.toByte())
        }

        // Send packet
        buffer.flip()
        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        val packet = DatagramPacket(data, data.size, remoteAddress, remotePort)
        socket.send(packet)
    }

    /**
     * Receive loop
     */
    private suspend fun receiveLoop() {
        val buffer = ByteArray(RECEIVE_BUFFER_SIZE)
        val packet = DatagramPacket(buffer, buffer.size)

        while (isRunning) {
            try {
                socket.receive(packet)

                val byteBuffer = ByteBuffer.wrap(buffer, 0, packet.length)
                byteBuffer.order(ByteOrder.BIG_ENDIAN)

                val decodedBuffer = ByteBuffer.allocate(RECEIVE_BUFFER_SIZE)
                val ackList = mutableListOf<Int>()

                val message = SLMessage.unpack(byteBuffer, decodedBuffer, ackList)

                if (message != null) {
                    // Handle ACKs
                    for (ackSeq in ackList) {
                        pendingAcks.remove(ackSeq)
                    }

                    // Add to ACK queue if reliable
                    if (message.isReliable && !receivedPackets.contains(message.seqNum)) {
                        receivedPackets.add(message.seqNum)
                        synchronized(ackQueue) {
                            ackQueue.add(message.seqNum)
                        }
                    }

                    // Notify listener
                    onMessageReceived?.invoke(message)
                }
            } catch (e: Exception) {
                if (isRunning) {
                    e.printStackTrace()
                }
            }
        }
    }

    /**
     * ACK sender loop - sends accumulated ACKs periodically
     */
    private suspend fun ackSenderLoop() {
        while (isRunning) {
            delay(100) // Send ACKs every 100ms

            val acksToSend =
                synchronized(ackQueue) {
                    if (ackQueue.isEmpty()) return@synchronized emptyList()
                    val acks = ackQueue.toList()
                    ackQueue.clear()
                    acks
                }

            if (acksToSend.isNotEmpty()) {
                val ackMessage = PacketAckMessage()
                ackMessage.packets.addAll(acksToSend)
                sendMessage(ackMessage)
            }
        }
    }

    /**
     * Retry loop - resends unacknowledged reliable messages
     */
    private suspend fun retryLoop() {
        while (isRunning) {
            delay(ACK_TIMEOUT_MS)

            val now = System.currentTimeMillis()
            val toRetry = mutableListOf<SLMessage>()
            val toRemove = mutableListOf<Int>()

            for ((seqNum, message) in pendingAcks) {
                if (now - message.sentTimeMillis > ACK_TIMEOUT_MS) {
                    if (message.retries < MAX_RETRIES) {
                        message.retries++
                        message.isResent = true
                        toRetry.add(message)
                    } else {
                        // Max retries exceeded
                        toRemove.add(seqNum)
                    }
                }
            }

            // Remove failed messages
            for (seqNum in toRemove) {
                pendingAcks.remove(seqNum)
            }

            // Resend messages
            for (message in toRetry) {
                sendMessage(message)
            }
        }
    }

    /**
     * Send an ACK immediately
     */
    fun sendAck(seqNum: Int) {
        val ackMessage = PacketAckMessage()
        ackMessage.packets.add(seqNum)
        sendMessage(ackMessage)
    }
}

/**
 * Circuit information
 */
data class SLCircuitInfo(
    val address: InetAddress,
    val port: Int,
    val circuitCode: Int,
    val agentId: UUID,
    val sessionId: UUID,
)
