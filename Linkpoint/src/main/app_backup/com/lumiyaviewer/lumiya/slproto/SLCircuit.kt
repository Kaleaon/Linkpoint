package com.lumiyaviewer.lumiya.slproto

import android.os.SystemClock
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.eventbus.EventBus
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageRouter
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode
import com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.CancelledKeyException
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.Collections
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

/**
 * UDP circuit implementation for the Second Life protocol.
 * Handles packet transmission, acknowledgements, resends and idle processing.
 */
open class SLCircuit @JvmOverloads constructor(
    protected val gridConn: SLGridConnection,
    val circuitInfo: SLCircuitInfo,
    val authReply: SLAuthReply,
    previousCircuit: SLCircuit? = null,
) : SLMessageHandler() {

    companion object {
        private const val DEFAULT_IDLE_INTERVAL_MS = 1000
        private const val FAST_IDLE_INTERVAL_MS = 100
        private const val MESSAGE_MAX_RETRIES = 3
        private const val MESSAGE_TIMEOUT_MS = 5_000
        private const val NEED_PING_TIMEOUT_MS = 10_000L
        private const val PING_INTERVAL_MS = 5_000L
        private const val TRACK_HANDLED_PACKETS = 1_024
        private const val MAX_ACKS_PER_PACKET = 255
        private const val MAX_PAYLOAD_SIZE = 1_018
        private const val BUFFER_CAPACITY = 65_536
        private const val UNANSWERED_PINGS = 3
    }

    private val eventBus: EventBus = EventBus.getInstance()
    private val messageRouter = SLMessageRouter()
    private val idleHandlers: MutableList<SLIdleHandler> = previousCircuit?.idleHandlers?.toMutableList() ?: mutableListOf()

    private val handledPackets: Queue<Int> = previousCircuit?.handledPackets ?: LinkedList()
    private val outgoingQueue: ConcurrentLinkedQueue<SLMessage> = previousCircuit?.outgoingQueue ?: ConcurrentLinkedQueue()
    private val unackedQueue: ConcurrentLinkedQueue<SLMessage> = previousCircuit?.unackedQueue ?: ConcurrentLinkedQueue()
    private val pendingAcks: MutableList<Int> =
        previousCircuit?.pendingAcks ?: Collections.synchronizedList(mutableListOf())
    private val receivedAcks: MutableList<Int> = previousCircuit?.receivedAcks ?: mutableListOf()
    protected val lastSeqNum: AtomicInteger = previousCircuit?.lastSeqNum ?: AtomicInteger(0)

    private val selector: Selector = gridConn.getSelector()
    private val datagramChannel: DatagramChannel = previousCircuit?.datagramChannel ?: DatagramChannel.open().apply {
        configureBlocking(false)
        connect(circuitInfo.socketAddress)
    }
    private val txBuffer: ByteBuffer = previousCircuit?.txBuffer ?: ByteBuffer.allocateDirect(BUFFER_CAPACITY)
    private val tempBuffer: ByteBuffer = previousCircuit?.tempBuffer ?: ByteBuffer.allocateDirect(BUFFER_CAPACITY)
    private val rxBuffer: ByteBuffer = previousCircuit?.rxBuffer ?: ByteBuffer.allocateDirect(BUFFER_CAPACITY)
    private val selectionKey: SelectionKey =
        previousCircuit?.selectionKey ?: datagramChannel.register(selector, SelectionKey.OP_READ).also {
            it.attach(this)
        }

    private var lastReceivedPacketMillis: Long = previousCircuit?.lastReceivedPacketMillis ?: SystemClock.elapsedRealtime()
    private var lastReceivedSeqnum: Int = previousCircuit?.lastReceivedSeqnum ?: 0
    private var lastPingSent: Long = previousCircuit?.lastPingSent ?: 0L
    private var pingSentCount: Int = previousCircuit?.pingSentCount ?: 0
    private var lastPingID: Byte = previousCircuit?.lastPingID ?: 0
    private var timedOut: Boolean = previousCircuit?.timedOut ?: false

    open fun closeCircuit() {
        try {
            selectionKey.cancel()
        } catch (_: Exception) {
        }

        try {
            datagramChannel.close()
        } catch (ioe: IOException) {
            Debug.Warning(ioe)
        }

        selector.wakeup()
        processCloseCircuit()
    }

    open fun defaultEventQueueHandler(eventType: CapsEventType, node: LLSDNode) {
        if (!messageRouter.handleEventQueueMessage(eventType, node)) {
            Debug.Log("Unhandled event queue message: type = $eventType")
        }
    }

    override fun DefaultMessageHandler(message: SLMessage) {
        messageRouter.handleMessage(message)
    }

    fun handleMessage(message: SLMessage) {
        message.handleMessage(this)
    }

    fun handlePacketAck(packetAck: PacketAck) {
        packetAck.packets.forEach { processReceivedAck(it.id) }
    }

    fun handleStartPingCheck(startPingCheck: StartPingCheck) {
        val reply = CompletePingCheck().apply {
            PingID_Field.PingID = startPingCheck.PingID_Field.PingID
        }
        sendMessage(reply)
    }

    protected open fun invokeProcessIdle() {
        processIdle()
        idleHandlers.forEach { handler ->
            handler.ProcessIdle()
        }
    }

    open fun processCloseCircuit() {}

    open fun processIdle() {}

    open fun processNetworkError() {}

    @Throws(IOException::class)
    open fun processReceive(): Boolean {
        rxBuffer.clear()
        rxBuffer.order(ByteOrder.BIG_ENDIAN)

        val bytesRead = datagramChannel.read(rxBuffer)
        if (bytesRead <= 0) {
            return false
        }

        rxBuffer.flip()
        receivedAcks.clear()

        val message = SLMessage.Unpack(rxBuffer, tempBuffer, receivedAcks)
        if (message == null) {
            Debug.Log("SLCircuit: message discarded (failed to unpack)")
            return true
        }

        lastReceivedPacketMillis = SystemClock.elapsedRealtime()
        pingSentCount = 0

        var isDuplicate = false
        val seqDiff = message.seqNum - lastReceivedSeqnum
        if (seqDiff <= 0) {
            Debug.Printf("Detected incoming out of order: seqNum = %d", message.seqNum)

            if (message !is PacketAck && message !is StartPingCheck && message !is CompletePingCheck) {
                if (handledPackets.contains(message.seqNum)) {
                    Debug.Printf("Detected incoming duplicate: seqNum = %d", message.seqNum)
                    isDuplicate = true
                }
            }
        }

        var messageToHandle: SLMessage? = null
        if (!isDuplicate) {
            while (handledPackets.size >= TRACK_HANDLED_PACKETS) {
                handledPackets.poll()
            }
            handledPackets.add(message.seqNum)
            lastReceivedSeqnum = message.seqNum

            if (message is PacketAck || message is StartPingCheck) {
                message.handleMessage(this)
            } else {
                messageToHandle = message
            }
        }

        if (receivedAcks.isNotEmpty()) {
            receivedAcks.forEach { ackSeq -> processReceivedAck(ackSeq) }
            receivedAcks.clear()
        }

        if (message.isReliable && !isDuplicate) {
            synchronized(pendingAcks) {
                if (!pendingAcks.contains(message.seqNum)) {
                    pendingAcks.add(message.seqNum)
                }
            }
        }

        messageToHandle?.let { handleMessage(it) }

        return true
    }

    fun processReceivedAck(sequence: Int) {
        val ackIterator = unackedQueue.iterator()
        while (ackIterator.hasNext()) {
            val queued = ackIterator.next()
            if (queued.seqNum == sequence) {
                ackIterator.remove()
                queued.handleMessageAcknowledged()
                break
            }
        }

        val outgoingIterator = outgoingQueue.iterator()
        while (outgoingIterator.hasNext()) {
            val queued = outgoingIterator.next()
            if (queued.seqNum == sequence) {
                outgoingIterator.remove()
                queued.handleMessageAcknowledged()
                break
            }
        }
    }

    open fun processTimeout() {}

    @Throws(IOException::class)
    open fun processTransmit(): Boolean {
        val nextMessage = outgoingQueue.peek()
        if (nextMessage != null) {
            nextMessage.Pack(txBuffer, tempBuffer)
            val appendedAcks = nextMessage.AppendPendingAcks(txBuffer, pendingAcks)
            txBuffer.flip()

            if (datagramChannel.write(txBuffer) > 0) {
                outgoingQueue.remove(nextMessage)
                removeSentAcks(appendedAcks)
                txBuffer.clear()

                if (nextMessage.isReliable) {
                    unackedQueue.add(nextMessage)
                }
                return true
            }
            txBuffer.clear()
        } else {
            val ackSnapshot: List<Int> = synchronized(pendingAcks) {
                if (pendingAcks.isEmpty()) {
                    emptyList()
                } else {
                    pendingAcks.take(MAX_ACKS_PER_PACKET.coerceAtMost(pendingAcks.size)).toList()
                }
            }

            if (ackSnapshot.isNotEmpty()) {
                val ackMessage = PacketAck(ackSnapshot.map { PacketAck.Packet(it) }.toMutableList()).apply {
                    isReliable = false
                    seqNum = lastSeqNum.incrementAndGet()
                }

                ackMessage.Pack(txBuffer, tempBuffer)
                txBuffer.flip()
                if (datagramChannel.write(txBuffer) > 0) {
                    txBuffer.clear()
                    removeSentAcks(ackSnapshot.size)
                    return true
                }
                txBuffer.clear()
            } else {
                return false
            }
        }

        return false
    }

    fun processWakeup() {
        processResends()
        tryProcessIdle()
    }

    @Synchronized
    open fun registerMessageHandler(handler: Any) {
        messageRouter.registerHandler(handler)
        if (handler is SLIdleHandler) {
            idleHandlers += handler
        }
    }

    open fun sendMessage(message: SLMessage) {
        message.seqNum = lastSeqNum.incrementAndGet()
        message.sentTimeMillis = System.currentTimeMillis()
        message.retries = 0
        outgoingQueue.add(message)
        updateSelectorOps()
        selector.wakeup()
    }

    fun tryProcessIdle() {
        val now = SystemClock.elapsedRealtime()
        if (now < lastReceivedPacketMillis + NEED_PING_TIMEOUT_MS || now < lastPingSent + PING_INTERVAL_MS) {
            return
        }

        if (pingSentCount < UNANSWERED_PINGS) {
            val startPing = StartPingCheck().apply {
                val oldestUnacked = unackedQueue.peek()?.seqNum ?: lastSeqNum.get()
                PingID_Field.PingID = lastPingID.toInt() // Fix Byte to Int conversion
                PingID_Field.OldestUnacked = oldestUnacked
            }

            lastPingID = (lastPingID + 1).toByte()
            sendMessage(startPing)
            pingSentCount++
            lastPingSent = now
            Debug.Log("SLCircuit: Sending ping ID $lastPingID")
        } else if (!timedOut) {
            timedOut = true
            Debug.Log("SLCircuit: Total timeout")
            processTimeout()
        }
    }

    @Synchronized
    open fun unregisterMessageHandler(handler: Any) {
        messageRouter.unregisterHandler(handler)
        if (handler is SLIdleHandler) {
            idleHandlers.remove(handler)
        }
    }

    open fun updateSelectorOps() {
        if (!selectionKey.isValid) return

        try {
            val ops = if (outgoingQueue.isEmpty() && pendingAcks.isEmpty()) {
                SelectionKey.OP_READ
            } else {
                SelectionKey.OP_READ or SelectionKey.OP_WRITE
            }
            selectionKey.interestOps(ops)
        } catch (ex: CancelledKeyException) {
            Debug.Warning(ex)
        }
    }

    open fun getAuthReply(): SLAuthReply = authReply

    open fun getEventBus(): EventBus = eventBus

    open fun getGridConnection(): SLGridConnection = gridConn

    @Synchronized
    open fun getIdleInterval(): Int = DEFAULT_IDLE_INTERVAL_MS

    private fun removeSentAcks(count: Int) {
        if (count <= 0) return

        synchronized(pendingAcks) {
            repeat(count.coerceAtMost(pendingAcks.size)) {
                pendingAcks.removeAt(0)
            }
        }
    }

    private fun processResends() {
        var resent = false
        val iterator = unackedQueue.iterator()
        val now = System.currentTimeMillis()

        while (iterator.hasNext()) {
            val message = iterator.next()
            if (now >= message.sentTimeMillis + PING_INTERVAL_MS) {
                iterator.remove()
                message.retries++
                if (message.retries > MESSAGE_MAX_RETRIES) {
                    message.handleMessageTimeout()
                } else {
                    message.isResent = true
                    message.sentTimeMillis = now
                    outgoingQueue.add(message)
                    resent = true
                }
            }
        }

        if (resent) {
            updateSelectorOps()
            selector.wakeup()
        }
    }

    fun DefaultEventQueueHandler(eventType: CapsEventType, node: LLSDNode) = defaultEventQueueHandler(eventType, node)

    // fun DefaultMessageHandler(message: SLMessage) = defaultMessageHandler(message) // Removed to avoid conflict with override

    fun HandleMessage(message: SLMessage) = handleMessage(message)

    fun HandlePacketAck(packetAck: PacketAck) = handlePacketAck(packetAck)

    fun HandleStartPingCheck(startPingCheck: StartPingCheck) = handleStartPingCheck(startPingCheck)

    fun InvokeProcessIdle() = invokeProcessIdle()

    fun ProcessCloseCircuit() = processCloseCircuit()

    fun ProcessIdle() = processIdle()

    fun ProcessNetworkError() = processNetworkError()

    fun ProcessReceive(): Boolean = processReceive()

    fun ProcessReceivedAck(sequence: Int) = processReceivedAck(sequence)

    fun ProcessTimeout() = processTimeout()

    fun ProcessTransmit(): Boolean = processTransmit()

    fun ProcessWakeup() = processWakeup()

    fun RegisterMessageHandler(handler: Any) = registerMessageHandler(handler)

    fun SendMessage(message: SLMessage) = sendMessage(message)

    fun TryProcessIdle() = tryProcessIdle()

    fun UnregisterMessageHandler(handler: Any) = unregisterMessageHandler(handler)

    fun UpdateSelectorOps() = updateSelectorOps()

    fun CloseCircuit() = closeCircuit()

    fun GetAuthReply(): SLAuthReply = getAuthReply()

    fun GetEventBus(): EventBus = getEventBus()

    fun GetGridConnection(): SLGridConnection = getGridConnection()

    fun GetIdleInterval(): Int = getIdleInterval()
}
