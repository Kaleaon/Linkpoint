package com.linkpoint.slproto

import android.os.SystemClock
import com.linkpoint.Debug
import com.linkpoint.eventbus.EventBus
import com.linkpoint.slproto.auth.SLAuthReply
import com.linkpoint.slproto.caps.SLCapEventQueue.CapsEventType
import com.linkpoint.slproto.handler.SLMessageRouter
import com.linkpoint.slproto.llsd.LLSDNode
import com.linkpoint.slproto.messages.CompletePingCheck
import com.linkpoint.slproto.messages.PacketAck
import com.linkpoint.slproto.messages.PacketAck.Packets
import com.linkpoint.slproto.messages.SLMessageHandler
import com.linkpoint.slproto.messages.StartPingCheck
import com.linkpoint.slproto.messages.StartPingCheck.PingID
import com.linkpoint.slproto.modules.SLIdleHandler
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.DatagramChannel
import java.nio.channels.SelectionKey
import java.nio.channels.Selector
import java.util.ArrayList
import java.util.Collections
import java.util.Iterator
import java.util.LinkedList
import java.util.List
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.atomic.AtomicInteger

class SLCircuit : SLMessageHandler() {
    private const val DEFAULT_IDLE_INTERVAL: Int = 1000
    private const val FAST_IDLE_INTERVAL: Int = 100
    private const val MESSAGE_MAX_RETRIES: Int = 3
    private const val MESSAGE_TIMEOUT_MILLIS: Int = 5000
    private const val NEED_PING_TIMEOUT: Long = 10000
    private const val PING_INTERVAL: Long = 5000
    private const val TRACK_HANDLED_PACKETS: Int = 1024
    private const val UNANSWERED_PINGS: Int = 3
    SLAuthReply authReply
    public SLCircuitInfo circuitInfo
    private DatagramChannel datagramChannel
    protected val EventBus eventBus = EventBus.getInstance()
    protected SLGridConnection gridConn
    private Queue<Integer> handledPackets
    private List<SLIdleHandler> idleHandlers = LinkedList()
    private Byte lastPingID = (Byte) 0
    private Long lastPingSent = 0
    private Long lastReceivedPacketMillis = 0
    private Int lastReceivedSeqnum
    private AtomicInteger lastSeqNum
    private SLMessageRouter messageRouter = SLMessageRouter()
    private ConcurrentLinkedQueue<SLMessage> outgoingQueue
    private List<Integer> pendingAcks
    private Int pingSentCount = 0
    private List<Integer> receivedAcks
    private ByteBuffer rxBuffer
    private SelectionKey selectionKey
    Selector selector
    private ByteBuffer tempBuffer
    private Boolean timedOut = false
    private ByteBuffer txBuffer
    private ConcurrentLinkedQueue<SLMessage> unackedQueue

    SLCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCircuit sLCircuit) throws IOException {
        this.gridConn = sLGridConnection
        this.circuitInfo = sLCircuitInfo
        this.authReply = sLAuthReply
        this.selector = sLGridConnection.getSelector()
        if (sLCircuit != null) {
            this.lastReceivedPacketMillis = sLCircuit.lastReceivedPacketMillis
            this.lastSeqNum = sLCircuit.lastSeqNum
            this.outgoingQueue = sLCircuit.outgoingQueue
            this.unackedQueue = sLCircuit.unackedQueue
            this.pendingAcks = sLCircuit.pendingAcks
            this.handledPackets = sLCircuit.handledPackets
            this.lastReceivedSeqnum = sLCircuit.lastReceivedSeqnum
            this.receivedAcks = sLCircuit.receivedAcks
            this.txBuffer = sLCircuit.txBuffer
            this.tempBuffer = sLCircuit.tempBuffer
            this.rxBuffer = sLCircuit.rxBuffer
            this.datagramChannel = sLCircuit.datagramChannel
            this.selectionKey = sLCircuit.selectionKey
            this.selectionKey.attach(this)
            return
        }
        this.lastReceivedPacketMillis = SystemClock.elapsedRealtime()
        this.lastSeqNum = AtomicInteger(0)
        this.outgoingQueue = ConcurrentLinkedQueue()
        this.unackedQueue = ConcurrentLinkedQueue()
        this.pendingAcks = Collections.synchronizedList(LinkedList())
        this.handledPackets = LinkedList()
        this.lastReceivedSeqnum = 0
        this.receivedAcks = ArrayList()
        this.txBuffer = ByteBuffer.allocate(65536)
        this.tempBuffer = ByteBuffer.allocate(65536)
        this.rxBuffer = ByteBuffer.allocate(65536)
        this.datagramChannel = DatagramChannel.open()
        this.datagramChannel.configureBlocking(false)
        this.datagramChannel.connect(sLCircuitInfo.socketAddress)
        this.selectionKey = this.datagramChannel.register(this.selector, 1)
        this.selectionKey.attach(this)
    }

    private Unit DumpDebugBuffer(String str, ByteBuffer byteBuffer) {
        StringBuilder stringBuilder = StringBuilder()
        stringBuilder.append(str).append(": ")
        for (Int i = 0; i < byteBuffer.limit(); i++) {
            stringBuilder.append(Integer.toHexString(byteBuffer.get(i) & 255))
        }
        Debug.Log(stringBuilder.toString())
    }

    private Unit ProcessResends() {
        Boolean z2 = false
        Iterator it = this.unackedQueue.iterator()
        while (true) {
            z = z2
            if (!it.hasNext()) {
                break
            }
            SLMessage sLMessage = (SLMessage) it.next()
            if (System.currentTimeMillis() >= sLMessage.sentTimeMillis + PING_INTERVAL) {
                it.remove()
                sLMessage.retries++
                if (sLMessage.retries > 3) {
                    sLMessage.handleMessageTimeout()
                    z2 = z
                } else {
                    sLMessage.isResent = true
                    sLMessage.sentTimeMillis = System.currentTimeMillis()
                    this.outgoingQueue.add(sLMessage)
                    z2 = true
                }
            } else {
                z2 = z
            }
        }
        if (z) {
            UpdateSelectorOps()
            this.selector.wakeup()
        }
        TryProcessIdle()
    }

    fun CloseCircuit() {
        this.selectionKey.cancel()
        try {
            this.datagramChannel.close()
        } catch (IOException e) {
            e.printStackTrace()
        }
        this.selector.wakeup()
        ProcessCloseCircuit()
    }

    fun DefaultEventQueueHandler(CapsEventType capsEventType, LLSDNode lLSDNode) {
        if (!this.messageRouter.handleEventQueueMessage(capsEventType, lLSDNode)) {
            Debug.Log("Unhandled event queue msg: type = " + capsEventType)
        }
    }

    fun DefaultMessageHandler(SLMessage sLMessage) {
        Boolean handleMessage = this.messageRouter.handleMessage(sLMessage)
    }

    fun HandleMessage(SLMessage sLMessage) {
        sLMessage.handleMessage(this)
    }

    fun HandlePacketAck(PacketAck packetAck) {
        for (Packets packets : packetAck.Packets_Fields) {
            ProcessReceivedAck(packets.ID)
        }
    }

    fun HandleStartPingCheck(StartPingCheck startPingCheck) {
        SLMessage completePingCheck = CompletePingCheck()
        completePingCheck.PingID_Field.PingID = startPingCheck.PingID_Field.PingID
        SendMessage(completePingCheck)
    }

    protected Unit InvokeProcessIdle() {
        ProcessIdle()
        for (SLIdleHandler ProcessIdle : this.idleHandlers) {
            ProcessIdle.ProcessIdle()
        }
    }

    fun ProcessCloseCircuit() {
    }

    fun ProcessIdle() {
    }

    fun ProcessNetworkError() {
    }

    public Boolean ProcessReceive() throws java.io.IOException {
        // Clear and prepare buffer for reading
        this.rxBuffer.clear()
        this.rxBuffer.order(ByteOrder.BIG_ENDIAN)
        
        // Try to read from the datagram channel
        Int bytesRead = this.datagramChannel.read(this.rxBuffer)
        if (bytesRead == 0) {
            return false
        }
        
        // Prepare buffer for parsing
        this.rxBuffer.flip()
        this.receivedAcks.clear()
        
        // Unpack the message
        SLMessage message = SLMessage.Unpack(this.rxBuffer, this.tempBuffer, this.receivedAcks)
        if (message == null) {
            Debug.Log("message discarded!")
            return true
        }
        
        // Update timing and reset ping count on successful receive
        this.lastReceivedPacketMillis = SystemClock.elapsedRealtime()
        this.pingSentCount = 0
        
        // Check for out-of-order packets
        Boolean isDuplicate = false
        Int seqDiff = message.seqNum - this.lastReceivedSeqnum
        if (seqDiff <= 0) {
            Debug.Printf("Detected incoming out of order: seqNum = %d", message.seqNum)
            
            // Check if this is a special message type that we should process anyway
            if (!(message instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck) &&
                !(message instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck) &&
                !(message instanceof com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck)) {
                
                // Check if we've already handled this packet
                if (this.handledPackets.contains(message.seqNum)) {
                    Debug.Printf("Detected incoming duplicate: seqNum = %d", message.seqNum)
                    isDuplicate = true
                }
            }
        }
        
        SLMessage messageToHandle = null
        if (!isDuplicate) {
            // Maintain the handled packets queue (LRU with max size)
            while (this.handledPackets.size() >= TRACK_HANDLED_PACKETS) {
                this.handledPackets.poll()
            }
            this.handledPackets.add(message.seqNum)
            this.lastReceivedSeqnum = message.seqNum
            
            // Handle special message types immediately
            if (message instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck ||
                message instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck) {
                message.handleMessage(this)
            } else {
                messageToHandle = message
            }
        }
        
        // Process any received acknowledgments
        for (Integer ackSeqNum : this.receivedAcks) {
            ProcessReceivedAck(ackSeqNum)
        }
        
        // Add reliable messages to pending acks (unless duplicate)
        if (message != null && message.isReliable && !isDuplicate) {
            // Check if we already have this sequence number in pending acks
            Boolean alreadyPending = false
            for (Integer pendingSeq : this.pendingAcks) {
                if (pendingSeq == message.seqNum) {
                    alreadyPending = true
                    break
                }
            }
            if (!alreadyPending) {
                this.pendingAcks.add(message.seqNum)
            }
        }
        
        // Handle the message if it's not a special type
        if (messageToHandle != null) {
            HandleMessage(messageToHandle)
        }
        
        return true
    }

    fun ProcessReceivedAck(Int i) {
        SLMessage sLMessage
        Iterator it = this.unackedQueue.iterator()
        while (it.hasNext()) {
            sLMessage = (SLMessage) it.next()
            if (sLMessage.seqNum == i) {
                it.remove()
                sLMessage.handleMessageAcknowledged()
            }
        }
        it = this.outgoingQueue.iterator()
        while (it.hasNext()) {
            sLMessage = (SLMessage) it.next()
            if (sLMessage.seqNum == i) {
                it.remove()
                sLMessage.handleMessageAcknowledged()
            }
        }
    }

    fun ProcessTimeout() {
    }

    public Boolean ProcessTransmit() throws IOException {
        SLMessage sLMessage = (SLMessage) this.outgoingQueue.peek()
        if (sLMessage != null) {
            sLMessage.Pack(this.txBuffer, this.tempBuffer)
            Int AppendPendingAcks = sLMessage.AppendPendingAcks(this.txBuffer, this.pendingAcks)
            this.txBuffer.flip()
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                this.outgoingQueue.remove(sLMessage)
                if (AppendPendingAcks >= this.pendingAcks.size()) {
                    this.pendingAcks.clear()
                } else {
                    for (i = 0; i < AppendPendingAcks; i++) {
                        this.pendingAcks.remove(0)
                    }
                }
                if (sLMessage.isReliable) {
                    this.unackedQueue.add(sLMessage)
                }
                return true
            }
        } else if (!this.pendingAcks.isEmpty()) {
            PacketAck packetAck = PacketAck()
            packetAck.seqNum = this.lastSeqNum.incrementAndGet()
            Iterator it = this.pendingAcks.iterator()
            i = 0
            while (it.hasNext() && packetAck.CalcPayloadSize() < 1018) {
                Packets packets = Packets()
                packets.ID = ((Integer) it.next()).intValue()
                packetAck.Packets_Fields.add(packets)
                i++
            }
            packetAck.Pack(this.txBuffer, this.tempBuffer)
            this.txBuffer.flip()
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                if (i >= this.pendingAcks.size()) {
                    this.pendingAcks.clear()
                } else {
                    for (Int i2 = 0; i2 < i; i2++) {
                        this.pendingAcks.remove(0)
                    }
                }
                return true
            }
        }
        return false
    }

    fun ProcessWakeup() {
        ProcessResends()
        TryProcessIdle()
    }

    public synchronized Unit RegisterMessageHandler(Object obj) {
        this.messageRouter.registerHandler(obj)
        if (obj instanceof SLIdleHandler) {
            this.idleHandlers.add((SLIdleHandler) obj)
        }
    }

    fun SendMessage(SLMessage sLMessage) {
        sLMessage.seqNum = this.lastSeqNum.incrementAndGet()
        sLMessage.sentTimeMillis = System.currentTimeMillis()
        sLMessage.retries = 0
        this.outgoingQueue.add(sLMessage)
        UpdateSelectorOps()
        this.selector.wakeup()
    }

    fun TryProcessIdle() {
        Long elapsedRealtime = SystemClock.elapsedRealtime()
        if (elapsedRealtime >= this.lastReceivedPacketMillis + NEED_PING_TIMEOUT && elapsedRealtime >= this.lastPingSent + PING_INTERVAL) {
            if (this.pingSentCount < 3) {
                Debug.Log("SLCircuit: Sending ping ID " + this.lastPingID)
                SLMessage startPingCheck = StartPingCheck()
                SLMessage sLMessage = (SLMessage) this.unackedQueue.peek()
                Int i = sLMessage != null ? sLMessage.seqNum : this.lastSeqNum.get()
                PingID pingID = startPingCheck.PingID_Field
                Byte b = this.lastPingID
                this.lastPingID = (Byte) (b + 1)
                pingID.PingID = b
                startPingCheck.PingID_Field.OldestUnacked = i
                SendMessage(startPingCheck)
                this.pingSentCount++
                this.lastPingSent = elapsedRealtime
            } else if (!this.timedOut) {
                this.timedOut = true
                Debug.Log("SLCircuit: Total timeout.")
                ProcessTimeout()
            }
        }
    }

    public synchronized Unit UnregisterMessageHandler(Object obj) {
        this.messageRouter.unregisterHandler(obj)
        if (obj instanceof SLIdleHandler) {
            this.idleHandlers.remove((SLIdleHandler) obj)
        }
    }

    fun UpdateSelectorOps() {
        if (this.selectionKey.isValid()) {
            try {
                if (this.outgoingQueue.isEmpty() && this.pendingAcks.isEmpty()) {
                    this.selectionKey.interestOps(1)
                } else {
                    this.selectionKey.interestOps(5)
                }
            } catch (Throwable e) {
                Debug.Warning(e)
            }
        }
    }

    public SLAuthReply getAuthReply() {
        return this.authReply
    }

    public EventBus getEventBus() {
        return this.eventBus
    }

    public SLGridConnection getGridConnection() {
        return this.gridConn
    }

    public synchronized Int getIdleInterval() {
        return 1000
    }
}
