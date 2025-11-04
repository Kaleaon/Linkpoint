package com.linkpoint.slproto

import android.os.SystemClock
import com.linkpoint.Debug
import com.linkpoint.eventbus.EventBus
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.handler.SLMessageRouter
import com.linkpoint.slproto.messages.CompletePingCheck
import com.linkpoint.slproto.messages.PacketAck
import com.linkpoint.slproto.messages.StartPingCheck
import com.linkpoint.slproto.modules.SLIdleHandler
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.min

/**
 * SLCircuit - UDP circuit for Second Life protocol
 * 
 * Handles:
 * - Reliable message delivery with ACKs
 * - Unreliable messages (fire and forget)
 * - Packet retransmission
 * - Ping/pong for connection health
 * - Message routing
 * 
 * Based on Firestorm's LLCircuit
 */
open class SLCircuitNew(
    protected val gridConn: SLGridConnection,
    val circuitInfo: SLCircuitInfo,
    val authReply: SLAuthReply,
    previousCircuit: SLCircuitNew? = null
) {
    
    companion object {
        const val DEFAULT_IDLE_INTERVAL = 1000
        const val FAST_IDLE_INTERVAL = 100
        const val MESSAGE_MAX_RETRIES = 3
        const val MESSAGE_TIMEOUT_MILLIS = 5000
        const val NEED_PING_TIMEOUT = 10000L
        const val PING_INTERVAL = 5000L
        const val TRACK_HANDLED_PACKETS = 1024
        const val UNANSWERED_PINGS = 3
        const val MAX_ACKS_PER_PACKET = 255
    }
    
    // Network components
    protected val eventBus = EventBus.getInstance()
    val selector: Selector = gridConn.getSelector()
    private val datagramChannel: DatagramChannel
    private val selectionKey: SelectionKey
    
    // Buffers
    private val txBuffer = ByteBuffer.allocate(65536)
    private val rxBuffer = ByteBuffer.allocate(65536)
    
    // Message queues
    private val outgoingQueue = ConcurrentLinkedQueue<SLMessage>()
    private val unackedQueue = ConcurrentLinkedQueue<SLMessage>()
    
    // Packet tracking
    private val lastSeqNum = AtomicInteger(0)
    private var lastReceivedSeqnum = 0
    private val pendingAcks = Collections.synchronizedList(LinkedList<Int>())
    private val handledPackets: Queue<Int> = LinkedList()
    
    // Ping tracking
    private var lastPingID: Byte = 0
    private var lastPingSent = 0L
    private var lastReceivedPacketMillis = 0L
    private var pingSentCount = 0
    private var timedOut = false
    
    // Message routing
    private val messageRouter = SLMessageRouter()
    private val idleHandlers = LinkedList<SLIdleHandler>()
    
    init {
        if (previousCircuit != null) {
            // Transfer state from previous circuit (for region handoff)
            lastReceivedPacketMillis = previousCircuit.lastReceivedPacketMillis
            // Copy other state as needed
        } else {
            lastReceivedPacketMillis = SystemClock.elapsedRealtime()
        }
        
        // Initialize UDP channel
        datagramChannel = DatagramChannel.open()
        datagramChannel.configureBlocking(false)
        datagramChannel.connect(circuitInfo.socketAddress)
        selectionKey = datagramChannel.register(selector, SelectionKey.OP_READ)
        selectionKey.attach(this)
    }
    
    /**
     * Send a message
     */
    fun sendMessage(message: SLMessage) {
        if (message.isReliable) {
            // Assign sequence number
            message.sequenceNumber = lastSeqNum.incrementAndGet()
            message.sentTimeMillis = System.currentTimeMillis()
            
            // Add to unacked queue for retransmission if needed
            unackedQueue.add(message)
        }
        
        // Add to outgoing queue
        outgoingQueue.add(message)
        
        // Wake up selector to send immediately
        updateSelectorOps()
        selector.wakeup()
    }
    
    /**
     * Process incoming packets
     */
    fun processReceive(): Boolean {
        try {
            rxBuffer.clear()
            val address = datagramChannel.receive(rxBuffer)

            if (address == null) {
                return false
            }

            rxBuffer.flip()
            lastReceivedPacketMillis = SystemClock.elapsedRealtime()

            val decodedPacket = SLPacket.decode(rxBuffer)
            if (decodedPacket == null) {
                Debug.Log("Failed to decode incoming packet")
                return false
            }

            // Process ACKs bundled with this packet before handling the message
            if (decodedPacket.ackIds.isNotEmpty()) {
                for (ack in decodedPacket.ackIds) {
                    processReceivedAck(ack)
                }
            }

            val sequenceNum = decodedPacket.header.sequenceNumber

            // Duplicate detection
            if (handledPackets.contains(sequenceNum)) {
                Debug.Log("Duplicate packet: $sequenceNum")
                return true
            }

            handledPackets.add(sequenceNum)
            if (handledPackets.size > TRACK_HANDLED_PACKETS) {
                handledPackets.poll()
            }

            if (decodedPacket.header.isReliable) {
                pendingAcks.add(sequenceNum)
            }

            val message = decodedPacket.message
            if (message != null) {
                handleMessage(message)
            } else {
                Debug.Log("Unhandled or unknown message in packet $sequenceNum")
            }

            lastReceivedSeqnum = sequenceNum
            resetPingTimer()

            return true

        } catch (e: IOException) {
            Debug.Log("Error receiving packet: ${e.message}")
            return false
        }
    }
    
    /**
     * Process outgoing packets
     */
    fun processSend(): Boolean {
        try {
            val message = outgoingQueue.poll()
            val pendingAckSnapshot = snapshotPendingAcks(MAX_ACKS_PER_PACKET)

            if (message == null) {
                return sendAckPacket(pendingAckSnapshot)
            }

            txBuffer.clear()

            message.sentTimeMillis = System.currentTimeMillis()

            val packet = SLPacket.outgoing(
                message = message,
                sequenceNumber = message.sequenceNumber,
                isResent = message.isResent,
                ackIds = pendingAckSnapshot
            )

            packet.writeTo(txBuffer)

            txBuffer.flip()
            val bytesWritten = datagramChannel.write(txBuffer)
            if (bytesWritten > 0) {
                if (pendingAckSnapshot.isNotEmpty()) {
                    removePendingAcks(pendingAckSnapshot.size)
                }
                return true
            }

            // Write failed; requeue message for later attempt
            outgoingQueue.add(message)
            return false

        } catch (e: IOException) {
            Debug.Log("Error sending packet: ${e.message}")
            return false
        }
    }
    
    /**
     * Process message retransmissions
     */
    fun processResends() {
        val currentTime = System.currentTimeMillis()
        val iterator = unackedQueue.iterator()
        var needsWakeup = false
        
        while (iterator.hasNext()) {
            val message = iterator.next()
            
            if (currentTime >= message.sentTimeMillis + MESSAGE_TIMEOUT_MILLIS) {
                iterator.remove()
                message.retries++
                
                if (message.retries > MESSAGE_MAX_RETRIES) {
                    // Message timed out
                    message.handleMessageTimeout()
                } else {
                    // Resend message
                    message.isResent = true
                    message.sentTimeMillis = currentTime
                    outgoingQueue.add(message)
                    needsWakeup = true
                }
            }
        }
        
        if (needsWakeup) {
            updateSelectorOps()
            selector.wakeup()
        }
        
        tryProcessIdle()
    }
    
    /**
     * Handle received ACK
     */
    private fun processReceivedAck(sequenceNum: Int) {
        // Remove acknowledged message from unacked queue
        val iterator = unackedQueue.iterator()
        while (iterator.hasNext()) {
            val message = iterator.next()
            if (message.sequenceNumber == sequenceNum) {
                iterator.remove()
                break
            }
        }
    }

    private fun snapshotPendingAcks(maxCount: Int): List<Int> {
        if (maxCount <= 0) {
            return emptyList()
        }

        return synchronized(pendingAcks) {
            if (pendingAcks.isEmpty()) {
                emptyList()
            } else {
                pendingAcks.take(maxCount).toList()
            }
        }
    }

    private fun removePendingAcks(count: Int) {
        if (count <= 0) {
            return
        }

        synchronized(pendingAcks) {
            val removeCount = min(count, pendingAcks.size)
            repeat(removeCount) {
                pendingAcks.removeAt(0)
            }
        }
    }

    private fun sendAckPacket(ackIds: List<Int>): Boolean {
        if (ackIds.isEmpty()) {
            return false
        }

        val ackMessage = PacketAck(ackIds.map { PacketAck.Packet(it) })
        ackMessage.isReliable = false
        ackMessage.sequenceNumber = lastSeqNum.incrementAndGet()
        ackMessage.sentTimeMillis = System.currentTimeMillis()
        ackMessage.retries = 0

        txBuffer.clear()
        val packet = SLPacket.outgoing(
            message = ackMessage,
            sequenceNumber = ackMessage.sequenceNumber,
            isResent = false,
            ackIds = emptyList()
        )
        packet.writeTo(txBuffer)
        txBuffer.flip()

        return try {
            val bytesWritten = datagramChannel.write(txBuffer)
            if (bytesWritten > 0) {
                removePendingAcks(ackIds.size)
                true
            } else {
                false
            }
        } catch (e: IOException) {
            Debug.Log("Error sending ACK packet: ${e.message}")
            false
        }
    }
    
    /**
     * Handle received message
     */
    private fun handleMessage(message: SLMessage) {
        message.handleMessage(this)
    }
    
    /**
     * Default message handler (routes to message router)
     */
    fun DefaultMessageHandler(message: SLMessage) {
        val handled = messageRouter.handleMessage(message)
        if (!handled) {
            Debug.Log("Unhandled message: ${message.getMessageName()}")
        }
    }
    
    /**
     * Handle PacketAck message
     */
    fun handlePacketAck(packetAck: PacketAck) {
        // Process each acknowledged packet
        for (packet in packetAck.packets) {
            processReceivedAck(packet.id)
        }
    }
    
    /**
     * Handle StartPingCheck message
     */
    fun handleStartPingCheck(startPingCheck: StartPingCheck) {
        // Respond with CompletePingCheck
        val response = CompletePingCheck()
        response.pingID = startPingCheck.pingID
        sendMessage(response)
    }
    
    /**
     * Send ping to server
     */
    private fun sendPing() {
        lastPingID = ((lastPingID + 1) % 256).toByte()
        val ping = StartPingCheck()
        ping.pingID = lastPingID
        sendMessage(ping)
        lastPingSent = System.currentTimeMillis()
        pingSentCount++
    }
    
    /**
     * Reset ping timer (called when we receive data)
     */
    private fun resetPingTimer() {
        pingSentCount = 0
    }
    
    /**
     * Check if connection has timed out
     */
    fun isTimedOut(): Boolean {
        if (timedOut) return true
        
        val timeSinceLastPacket = SystemClock.elapsedRealtime() - lastReceivedPacketMillis
        if (timeSinceLastPacket > NEED_PING_TIMEOUT) {
            if (pingSentCount >= UNANSWERED_PINGS) {
                timedOut = true
                processNetworkError()
                return true
            }
            sendPing()
        }
        
        return false
    }
    
    /**
     * Update selector operations
     */
    private fun updateSelectorOps() {
        var ops = SelectionKey.OP_READ
        val hasPendingAcks = synchronized(pendingAcks) { pendingAcks.isNotEmpty() }
        if (outgoingQueue.isNotEmpty() || hasPendingAcks) {
            ops = ops or SelectionKey.OP_WRITE
        }
        selectionKey.interestOps(ops)
    }
    
    /**
     * Try to process idle handlers
     */
    private fun tryProcessIdle() {
        invokeProcessIdle()
    }
    
    /**
     * Invoke all idle handlers
     */
    protected fun invokeProcessIdle() {
        processIdle()
        for (handler in idleHandlers) {
            handler.ProcessIdle()
        }
    }
    
    /**
     * Process idle (override in subclass)
     */
    open fun processIdle() {
        // Override in subclass
    }
    
    /**
     * Process network error (override in subclass)
     */
    open fun processNetworkError() {
        Debug.Log("Network error on circuit ${circuitInfo.socketAddress}")
    }
    
    /**
     * Process close circuit (override in subclass)
     */
    open fun processCloseCircuit() {
        // Override in subclass
    }
    
    /**
     * Close the circuit
     */
    fun closeCircuit() {
        selectionKey.cancel()
        try {
            datagramChannel.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        selector.wakeup()
        processCloseCircuit()
    }
    
    /**
     * Add idle handler
     */
    fun addIdleHandler(handler: SLIdleHandler) {
        idleHandlers.add(handler)
    }
    
    /**
     * Remove idle handler
     */
    fun removeIdleHandler(handler: SLIdleHandler) {
        idleHandlers.remove(handler)
    }
}
