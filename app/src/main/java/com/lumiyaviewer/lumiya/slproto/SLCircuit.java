package com.lumiyaviewer.lumiya.slproto;

import android.os.SystemClock;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageRouter;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck;
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck;
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck.Packets;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck;
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck.PingID;
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class SLCircuit extends SLMessageHandler {
    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private static final int FAST_IDLE_INTERVAL = 100;
    private static final int MESSAGE_MAX_RETRIES = 3;
    private static final int MESSAGE_TIMEOUT_MILLIS = 5000;
    private static final long NEED_PING_TIMEOUT = 10000;
    private static final long PING_INTERVAL = 5000;
    private static final int TRACK_HANDLED_PACKETS = 1024;
    private static final int UNANSWERED_PINGS = 3;
    SLAuthReply authReply;
    public SLCircuitInfo circuitInfo;
    private DatagramChannel datagramChannel;
    protected final EventBus eventBus = EventBus.getInstance();
    protected SLGridConnection gridConn;
    private Queue<Integer> handledPackets;
    private List<SLIdleHandler> idleHandlers = new LinkedList();
    private byte lastPingID = (byte) 0;
    private long lastPingSent = 0;
    private long lastReceivedPacketMillis = 0;
    private int lastReceivedSeqnum;
    private AtomicInteger lastSeqNum;
    private SLMessageRouter messageRouter = new SLMessageRouter();
    private ConcurrentLinkedQueue<SLMessage> outgoingQueue;
    private List<Integer> pendingAcks;
    private int pingSentCount = 0;
    private List<Integer> receivedAcks;
    private ByteBuffer rxBuffer;
    private SelectionKey selectionKey;
    Selector selector;
    private ByteBuffer tempBuffer;
    private boolean timedOut = false;
    private ByteBuffer txBuffer;
    private ConcurrentLinkedQueue<SLMessage> unackedQueue;

    SLCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCircuit sLCircuit) throws IOException {
        this.gridConn = sLGridConnection;
        this.circuitInfo = sLCircuitInfo;
        this.authReply = sLAuthReply;
        this.selector = sLGridConnection.getSelector();
        if (sLCircuit != null) {
            this.lastReceivedPacketMillis = sLCircuit.lastReceivedPacketMillis;
            this.lastSeqNum = sLCircuit.lastSeqNum;
            this.outgoingQueue = sLCircuit.outgoingQueue;
            this.unackedQueue = sLCircuit.unackedQueue;
            this.pendingAcks = sLCircuit.pendingAcks;
            this.handledPackets = sLCircuit.handledPackets;
            this.lastReceivedSeqnum = sLCircuit.lastReceivedSeqnum;
            this.receivedAcks = sLCircuit.receivedAcks;
            this.txBuffer = sLCircuit.txBuffer;
            this.tempBuffer = sLCircuit.tempBuffer;
            this.rxBuffer = sLCircuit.rxBuffer;
            this.datagramChannel = sLCircuit.datagramChannel;
            this.selectionKey = sLCircuit.selectionKey;
            this.selectionKey.attach(this);
            return;
        }
        this.lastReceivedPacketMillis = SystemClock.elapsedRealtime();
        this.lastSeqNum = new AtomicInteger(0);
        this.outgoingQueue = new ConcurrentLinkedQueue();
        this.unackedQueue = new ConcurrentLinkedQueue();
        this.pendingAcks = Collections.synchronizedList(new LinkedList());
        this.handledPackets = new LinkedList();
        this.lastReceivedSeqnum = 0;
        this.receivedAcks = new ArrayList();
        this.txBuffer = ByteBuffer.allocate(65536);
        this.tempBuffer = ByteBuffer.allocate(65536);
        this.rxBuffer = ByteBuffer.allocate(65536);
        this.datagramChannel = DatagramChannel.open();
        this.datagramChannel.configureBlocking(false);
        this.datagramChannel.connect(sLCircuitInfo.socketAddress);
        this.selectionKey = this.datagramChannel.register(this.selector, 1);
        this.selectionKey.attach(this);
    }

    private void DumpDebugBuffer(String str, ByteBuffer byteBuffer) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(str).append(": ");
        for (int i = 0; i < byteBuffer.limit(); i++) {
            stringBuilder.append(Integer.toHexString(byteBuffer.get(i) & 255));
        }
        Debug.Log(stringBuilder.toString());
    }

    private void ProcessResends() {
        boolean z2 = false;
        Iterator it = this.unackedQueue.iterator();
        while (true) {
            z = z2;
            if (!it.hasNext()) {
                break;
            }
            SLMessage sLMessage = (SLMessage) it.next();
            if (System.currentTimeMillis() >= sLMessage.sentTimeMillis + PING_INTERVAL) {
                it.remove();
                sLMessage.retries++;
                if (sLMessage.retries > 3) {
                    sLMessage.handleMessageTimeout();
                    z2 = z;
                } else {
                    sLMessage.isResent = true;
                    sLMessage.sentTimeMillis = System.currentTimeMillis();
                    this.outgoingQueue.add(sLMessage);
                    z2 = true;
                }
            } else {
                z2 = z;
            }
        }
        if (z) {
            UpdateSelectorOps();
            this.selector.wakeup();
        }
        TryProcessIdle();
    }

    public void CloseCircuit() {
        this.selectionKey.cancel();
        try {
            this.datagramChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.selector.wakeup();
        ProcessCloseCircuit();
    }

    public void DefaultEventQueueHandler(CapsEventType capsEventType, LLSDNode lLSDNode) {
        if (!this.messageRouter.handleEventQueueMessage(capsEventType, lLSDNode)) {
            Debug.Log("Unhandled event queue msg: type = " + capsEventType);
        }
    }

    public void DefaultMessageHandler(SLMessage sLMessage) {
        boolean handleMessage = this.messageRouter.handleMessage(sLMessage);
    }

    public void HandleMessage(SLMessage sLMessage) {
        sLMessage.handleMessage(this);
    }

    public void HandlePacketAck(PacketAck packetAck) {
        for (Packets packets : packetAck.Packets_Fields) {
            ProcessReceivedAck(packets.ID);
        }
    }

    public void HandleStartPingCheck(StartPingCheck startPingCheck) {
        SLMessage completePingCheck = new CompletePingCheck();
        completePingCheck.PingID_Field.PingID = startPingCheck.PingID_Field.PingID;
        SendMessage(completePingCheck);
    }

    protected void InvokeProcessIdle() {
        ProcessIdle();
        for (SLIdleHandler ProcessIdle : this.idleHandlers) {
            ProcessIdle.ProcessIdle();
        }
    }

    public void ProcessCloseCircuit() {
    }

    public void ProcessIdle() {
    }

    public void ProcessNetworkError() {
    }

    public boolean ProcessReceive() throws java.io.IOException {
        // Clear and prepare buffer for reading
        this.rxBuffer.clear();
        this.rxBuffer.order(ByteOrder.BIG_ENDIAN);
        
        // Try to read from the datagram channel
        int bytesRead = this.datagramChannel.read(this.rxBuffer);
        if (bytesRead == 0) {
            return false;
        }
        
        // Prepare buffer for parsing
        this.rxBuffer.flip();
        this.receivedAcks.clear();
        
        // Unpack the message
        SLMessage message = SLMessage.Unpack(this.rxBuffer, this.tempBuffer, this.receivedAcks);
        if (message == null) {
            Debug.Log("message discarded!");
            return true;
        }
        
        // Update timing and reset ping count on successful receive
        this.lastReceivedPacketMillis = SystemClock.elapsedRealtime();
        this.pingSentCount = 0;
        
        // Check for out-of-order packets
        boolean isDuplicate = false;
        int seqDiff = message.seqNum - this.lastReceivedSeqnum;
        if (seqDiff <= 0) {
            Debug.Printf("Detected incoming out of order: seqNum = %d", message.seqNum);
            
            // Check if this is a special message type that we should process anyway
            if (!(message instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck) &&
                !(message instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck) &&
                !(message instanceof com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck)) {
                
                // Check if we've already handled this packet
                if (this.handledPackets.contains(message.seqNum)) {
                    Debug.Printf("Detected incoming duplicate: seqNum = %d", message.seqNum);
                    isDuplicate = true;
                }
            }
        }
        
        SLMessage messageToHandle = null;
        if (!isDuplicate) {
            // Maintain the handled packets queue (LRU with max size)
            while (this.handledPackets.size() >= TRACK_HANDLED_PACKETS) {
                this.handledPackets.poll();
            }
            this.handledPackets.add(message.seqNum);
            this.lastReceivedSeqnum = message.seqNum;
            
            // Handle special message types immediately
            if (message instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck ||
                message instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck) {
                message.handleMessage(this);
            } else {
                messageToHandle = message;
            }
        }
        
        // Process any received acknowledgments
        for (Integer ackSeqNum : this.receivedAcks) {
            ProcessReceivedAck(ackSeqNum);
        }
        
        // Add reliable messages to pending acks (unless duplicate)
        if (message != null && message.isReliable && !isDuplicate) {
            // Check if we already have this sequence number in pending acks
            boolean alreadyPending = false;
            for (Integer pendingSeq : this.pendingAcks) {
                if (pendingSeq == message.seqNum) {
                    alreadyPending = true;
                    break;
                }
            }
            if (!alreadyPending) {
                this.pendingAcks.add(message.seqNum);
            }
        }
        
        // Handle the message if it's not a special type
        if (messageToHandle != null) {
            HandleMessage(messageToHandle);
        }
        
        return true;
    }

    public void ProcessReceivedAck(int i) {
        SLMessage sLMessage;
        Iterator it = this.unackedQueue.iterator();
        while (it.hasNext()) {
            sLMessage = (SLMessage) it.next();
            if (sLMessage.seqNum == i) {
                it.remove();
                sLMessage.handleMessageAcknowledged();
            }
        }
        it = this.outgoingQueue.iterator();
        while (it.hasNext()) {
            sLMessage = (SLMessage) it.next();
            if (sLMessage.seqNum == i) {
                it.remove();
                sLMessage.handleMessageAcknowledged();
            }
        }
    }

    public void ProcessTimeout() {
    }

    public boolean ProcessTransmit() throws IOException {
        SLMessage sLMessage = (SLMessage) this.outgoingQueue.peek();
        if (sLMessage != null) {
            sLMessage.Pack(this.txBuffer, this.tempBuffer);
            int AppendPendingAcks = sLMessage.AppendPendingAcks(this.txBuffer, this.pendingAcks);
            this.txBuffer.flip();
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                this.outgoingQueue.remove(sLMessage);
                if (AppendPendingAcks >= this.pendingAcks.size()) {
                    this.pendingAcks.clear();
                } else {
                    for (i = 0; i < AppendPendingAcks; i++) {
                        this.pendingAcks.remove(0);
                    }
                }
                if (sLMessage.isReliable) {
                    this.unackedQueue.add(sLMessage);
                }
                return true;
            }
        } else if (!this.pendingAcks.isEmpty()) {
            PacketAck packetAck = new PacketAck();
            packetAck.seqNum = this.lastSeqNum.incrementAndGet();
            Iterator it = this.pendingAcks.iterator();
            i = 0;
            while (it.hasNext() && packetAck.CalcPayloadSize() < 1018) {
                Packets packets = new Packets();
                packets.ID = ((Integer) it.next()).intValue();
                packetAck.Packets_Fields.add(packets);
                i++;
            }
            packetAck.Pack(this.txBuffer, this.tempBuffer);
            this.txBuffer.flip();
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                if (i >= this.pendingAcks.size()) {
                    this.pendingAcks.clear();
                } else {
                    for (int i2 = 0; i2 < i; i2++) {
                        this.pendingAcks.remove(0);
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void ProcessWakeup() {
        ProcessResends();
        TryProcessIdle();
    }

    public synchronized void RegisterMessageHandler(Object obj) {
        this.messageRouter.registerHandler(obj);
        if (obj instanceof SLIdleHandler) {
            this.idleHandlers.add((SLIdleHandler) obj);
        }
    }

    public void SendMessage(SLMessage sLMessage) {
        sLMessage.seqNum = this.lastSeqNum.incrementAndGet();
        sLMessage.sentTimeMillis = System.currentTimeMillis();
        sLMessage.retries = 0;
        this.outgoingQueue.add(sLMessage);
        UpdateSelectorOps();
        this.selector.wakeup();
    }

    public void TryProcessIdle() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime >= this.lastReceivedPacketMillis + NEED_PING_TIMEOUT && elapsedRealtime >= this.lastPingSent + PING_INTERVAL) {
            if (this.pingSentCount < 3) {
                Debug.Log("SLCircuit: Sending ping ID " + this.lastPingID);
                SLMessage startPingCheck = new StartPingCheck();
                SLMessage sLMessage = (SLMessage) this.unackedQueue.peek();
                int i = sLMessage != null ? sLMessage.seqNum : this.lastSeqNum.get();
                PingID pingID = startPingCheck.PingID_Field;
                byte b = this.lastPingID;
                this.lastPingID = (byte) (b + 1);
                pingID.PingID = b;
                startPingCheck.PingID_Field.OldestUnacked = i;
                SendMessage(startPingCheck);
                this.pingSentCount++;
                this.lastPingSent = elapsedRealtime;
            } else if (!this.timedOut) {
                this.timedOut = true;
                Debug.Log("SLCircuit: Total timeout.");
                ProcessTimeout();
            }
        }
    }

    public synchronized void UnregisterMessageHandler(Object obj) {
        this.messageRouter.unregisterHandler(obj);
        if (obj instanceof SLIdleHandler) {
            this.idleHandlers.remove((SLIdleHandler) obj);
        }
    }

    public void UpdateSelectorOps() {
        if (this.selectionKey.isValid()) {
            try {
                if (this.outgoingQueue.isEmpty() && this.pendingAcks.isEmpty()) {
                    this.selectionKey.interestOps(1);
                } else {
                    this.selectionKey.interestOps(5);
                }
            } catch (Throwable e) {
                Debug.Warning(e);
            }
        }
    }

    public SLAuthReply getAuthReply() {
        return this.authReply;
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public SLGridConnection getGridConnection() {
        return this.gridConn;
    }

    public synchronized int getIdleInterval() {
        return 1000;
    }
}
