// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto;

import java.nio.channels.CancelledKeyException;
import java.nio.ByteOrder;
import com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck;
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck;
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.Debug;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import android.os.SystemClock;
import java.util.LinkedList;
import java.nio.channels.Selector;
import java.nio.channels.SelectionKey;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageRouter;
import java.util.concurrent.atomic.AtomicInteger;
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler;
import java.util.List;
import java.util.Queue;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import java.nio.channels.DatagramChannel;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;

public class SLCircuit extends SLMessageHandler
{
    private static final int DEFAULT_IDLE_INTERVAL = 1000;
    private static final int FAST_IDLE_INTERVAL = 100;
    private static final int MESSAGE_MAX_RETRIES = 3;
    private static final int MESSAGE_TIMEOUT_MILLIS = 5000;
    private static final long NEED_PING_TIMEOUT = 10000L;
    private static final long PING_INTERVAL = 5000L;
    private static final int TRACK_HANDLED_PACKETS = 1024;
    private static final int UNANSWERED_PINGS = 3;
    SLAuthReply authReply;
    public SLCircuitInfo circuitInfo;
    private DatagramChannel datagramChannel;
    protected final EventBus eventBus;
    protected SLGridConnection gridConn;
    private Queue<Integer> handledPackets;
    private List<SLIdleHandler> idleHandlers;
    private byte lastPingID;
    private long lastPingSent;
    private long lastReceivedPacketMillis;
    private int lastReceivedSeqnum;
    private AtomicInteger lastSeqNum;
    private SLMessageRouter messageRouter;
    private ConcurrentLinkedQueue<SLMessage> outgoingQueue;
    private List<Integer> pendingAcks;
    private int pingSentCount;
    private List<Integer> receivedAcks;
    private ByteBuffer rxBuffer;
    private SelectionKey selectionKey;
    Selector selector;
    private ByteBuffer tempBuffer;
    private boolean timedOut;
    private ByteBuffer txBuffer;
    private ConcurrentLinkedQueue<SLMessage> unackedQueue;
    
    SLCircuit(final SLGridConnection gridConn, final SLCircuitInfo circuitInfo, final SLAuthReply authReply, final SLCircuit slCircuit) throws IOException {
        this.eventBus = EventBus.getInstance();
        this.lastReceivedPacketMillis = 0L;
        this.lastPingSent = 0L;
        this.pingSentCount = 0;
        this.lastPingID = 0;
        this.timedOut = false;
        this.messageRouter = new SLMessageRouter();
        this.idleHandlers = new LinkedList<SLIdleHandler>();
        this.gridConn = gridConn;
        this.circuitInfo = circuitInfo;
        this.authReply = authReply;
        this.selector = gridConn.getSelector();
        if (slCircuit != null) {
            this.lastReceivedPacketMillis = slCircuit.lastReceivedPacketMillis;
            this.lastSeqNum = slCircuit.lastSeqNum;
            this.outgoingQueue = slCircuit.outgoingQueue;
            this.unackedQueue = slCircuit.unackedQueue;
            this.pendingAcks = slCircuit.pendingAcks;
            this.handledPackets = slCircuit.handledPackets;
            this.lastReceivedSeqnum = slCircuit.lastReceivedSeqnum;
            this.receivedAcks = slCircuit.receivedAcks;
            this.txBuffer = slCircuit.txBuffer;
            this.tempBuffer = slCircuit.tempBuffer;
            this.rxBuffer = slCircuit.rxBuffer;
            this.datagramChannel = slCircuit.datagramChannel;
            (this.selectionKey = slCircuit.selectionKey).attach(this);
        }
        else {
            this.lastReceivedPacketMillis = SystemClock.elapsedRealtime();
            this.lastSeqNum = new AtomicInteger(0);
            this.outgoingQueue = new ConcurrentLinkedQueue<SLMessage>();
            this.unackedQueue = new ConcurrentLinkedQueue<SLMessage>();
            this.pendingAcks = Collections.synchronizedList(new LinkedList<Integer>());
            this.handledPackets = new LinkedList<Integer>();
            this.lastReceivedSeqnum = 0;
            this.receivedAcks = new ArrayList<Integer>();
            this.txBuffer = ByteBuffer.allocate(65536);
            this.tempBuffer = ByteBuffer.allocate(65536);
            this.rxBuffer = ByteBuffer.allocate(65536);
            (this.datagramChannel = DatagramChannel.open()).configureBlocking(false);
            this.datagramChannel.connect(circuitInfo.socketAddress);
            (this.selectionKey = this.datagramChannel.register(this.selector, 1)).attach(this);
        }
    }
    
    private void DumpDebugBuffer(final String str, final ByteBuffer byteBuffer) {
        final StringBuilder sb = new StringBuilder();
        sb.append(str).append(": ");
        for (int i = 0; i < byteBuffer.limit(); ++i) {
            sb.append(Integer.toHexString(byteBuffer.get(i) & 0xFF));
        }
        Debug.Log(sb.toString());
    }
    
    private void ProcessResends() {
        final Iterator<SLMessage> iterator = this.unackedQueue.iterator();
        boolean b = false;
        while (iterator.hasNext()) {
            final SLMessage e = iterator.next();
            if (System.currentTimeMillis() >= e.sentTimeMillis + 5000L) {
                iterator.remove();
                ++e.retries;
                if (e.retries > 3) {
                    e.handleMessageTimeout();
                }
                else {
                    e.isResent = true;
                    e.sentTimeMillis = System.currentTimeMillis();
                    this.outgoingQueue.add(e);
                    b = true;
                }
            }
        }
        if (b) {
            this.UpdateSelectorOps();
            this.selector.wakeup();
        }
        this.TryProcessIdle();
    }
    
    public void CloseCircuit() {
        this.selectionKey.cancel();
        while (true) {
            try {
                this.datagramChannel.close();
                this.selector.wakeup();
                this.ProcessCloseCircuit();
            }
            catch (final IOException ex) {
                ex.printStackTrace();
                continue;
            }
            break;
        }
    }
    
    public void DefaultEventQueueHandler(final SLCapEventQueue.CapsEventType obj, final LLSDNode llsdNode) {
        if (!this.messageRouter.handleEventQueueMessage(obj, llsdNode)) {
            Debug.Log("Unhandled event queue msg: type = " + obj);
        }
    }
    
    @Override
    public void DefaultMessageHandler(final SLMessage slMessage) {
        this.messageRouter.handleMessage(slMessage);
    }
    
    public void HandleMessage(final SLMessage slMessage) {
        slMessage.Handle(this);
    }
    
    @Override
    public void HandlePacketAck(final PacketAck packetAck) {
        final Iterator<Object> iterator = packetAck.Packets_Fields.iterator();
        while (iterator.hasNext()) {
            this.ProcessReceivedAck(iterator.next().ID);
        }
    }
    
    @Override
    public void HandleStartPingCheck(final StartPingCheck startPingCheck) {
        final CompletePingCheck completePingCheck = new CompletePingCheck();
        completePingCheck.PingID_Field.PingID = startPingCheck.PingID_Field.PingID;
        this.SendMessage(completePingCheck);
    }
    
    protected void InvokeProcessIdle() {
        this.ProcessIdle();
        final Iterator<Object> iterator = this.idleHandlers.iterator();
        while (iterator.hasNext()) {
            iterator.next().ProcessIdle();
        }
    }
    
    public void ProcessCloseCircuit() {
    }
    
    public void ProcessIdle() {
    }
    
    public void ProcessNetworkError() {
    }
    
    public boolean ProcessReceive() throws IOException {
        this.rxBuffer.clear();
        this.rxBuffer.order(ByteOrder.BIG_ENDIAN);
        if (this.datagramChannel.read(this.rxBuffer) != 0) {
            this.rxBuffer.flip();
            this.receivedAcks.clear();
            final SLMessage unpack = SLMessage.Unpack(this.rxBuffer, this.tempBuffer, this.receivedAcks);
            SLMessage slMessage = null;
            Label_0257: {
                if (unpack != null) {
                    this.lastReceivedPacketMillis = SystemClock.elapsedRealtime();
                    this.pingSentCount = 0;
                    while (true) {
                        Label_0417: {
                            if (unpack.seqNum - this.lastReceivedSeqnum > 0) {
                                break Label_0417;
                            }
                            Debug.Printf("Detected incoming out of order: seqNum = %d", unpack.seqNum);
                            int n;
                            if (!(unpack instanceof PacketAck) && (unpack instanceof StartPingCheck ^ true)) {
                                if (!(unpack instanceof CompletePingCheck ^ true) || !this.handledPackets.contains(unpack.seqNum)) {
                                    break Label_0417;
                                }
                                Debug.Printf("Detected incoming duplicate: seqNum = %d", unpack.seqNum);
                                n = 1;
                            }
                            else {
                                n = 0;
                            }
                            if (n != 0) {
                                slMessage = null;
                                break Label_0257;
                            }
                            while (this.handledPackets.size() >= 1024 && this.handledPackets.poll() != null) {}
                            this.handledPackets.add(unpack.seqNum);
                            this.lastReceivedSeqnum = unpack.seqNum;
                            if (unpack instanceof PacketAck || unpack instanceof StartPingCheck) {
                                unpack.Handle(this);
                                slMessage = null;
                                break Label_0257;
                            }
                            slMessage = unpack;
                            break Label_0257;
                        }
                        int n = 0;
                        continue;
                    }
                }
                Debug.Log("message discarded!");
                slMessage = null;
            }
            final Iterator<Object> iterator = this.receivedAcks.iterator();
            while (iterator.hasNext()) {
                this.ProcessReceivedAck(iterator.next());
            }
            Label_0394: {
                if (unpack != null && unpack.isReliable) {
                    final Iterator<Object> iterator2 = this.pendingAcks.iterator();
                    while (true) {
                        while (iterator2.hasNext()) {
                            if (iterator2.next() == unpack.seqNum) {
                                final int n2 = 1;
                                if (n2 == 0) {
                                    this.pendingAcks.add(unpack.seqNum);
                                }
                                break Label_0394;
                            }
                        }
                        final int n2 = 0;
                        continue;
                    }
                }
            }
            if (slMessage != null) {
                this.HandleMessage(slMessage);
            }
            return true;
        }
        return false;
    }
    
    public void ProcessReceivedAck(final int n) {
        final Iterator<SLMessage> iterator = this.unackedQueue.iterator();
        while (iterator.hasNext()) {
            final SLMessage slMessage = iterator.next();
            if (slMessage.seqNum == n) {
                iterator.remove();
                slMessage.handleMessageAcknowledged();
            }
        }
        final Iterator<SLMessage> iterator2 = this.outgoingQueue.iterator();
        while (iterator2.hasNext()) {
            final SLMessage slMessage2 = iterator2.next();
            if (slMessage2.seqNum == n) {
                iterator2.remove();
                slMessage2.handleMessageAcknowledged();
            }
        }
    }
    
    public void ProcessTimeout() {
    }
    
    public boolean ProcessTransmit() throws IOException {
        final SLMessage slMessage = this.outgoingQueue.peek();
        if (slMessage != null) {
            slMessage.Pack(this.txBuffer, this.tempBuffer);
            final int appendPendingAcks = slMessage.AppendPendingAcks(this.txBuffer, this.pendingAcks);
            this.txBuffer.flip();
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                this.outgoingQueue.remove(slMessage);
                if (appendPendingAcks >= this.pendingAcks.size()) {
                    this.pendingAcks.clear();
                }
                else {
                    for (int i = 0; i < appendPendingAcks; ++i) {
                        this.pendingAcks.remove(0);
                    }
                }
                if (slMessage.isReliable) {
                    this.unackedQueue.add(slMessage);
                }
                return true;
            }
        }
        else if (!this.pendingAcks.isEmpty()) {
            final PacketAck packetAck = new PacketAck();
            packetAck.seqNum = this.lastSeqNum.incrementAndGet();
            final Iterator<Integer> iterator = this.pendingAcks.iterator();
            int n = 0;
            while (iterator.hasNext() && packetAck.CalcPayloadSize() < 1018) {
                final PacketAck.Packets e = new PacketAck.Packets();
                e.ID = iterator.next();
                packetAck.Packets_Fields.add(e);
                ++n;
            }
            packetAck.Pack(this.txBuffer, this.tempBuffer);
            this.txBuffer.flip();
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                if (n >= this.pendingAcks.size()) {
                    this.pendingAcks.clear();
                }
                else {
                    for (int j = 0; j < n; ++j) {
                        this.pendingAcks.remove(0);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public void ProcessWakeup() {
        this.ProcessResends();
        this.TryProcessIdle();
    }
    
    public void RegisterMessageHandler(final Object o) {
        synchronized (this) {
            this.messageRouter.registerHandler(o);
            if (o instanceof SLIdleHandler) {
                this.idleHandlers.add((SLIdleHandler)o);
            }
        }
    }
    
    public void SendMessage(final SLMessage e) {
        e.seqNum = this.lastSeqNum.incrementAndGet();
        e.sentTimeMillis = System.currentTimeMillis();
        e.retries = 0;
        this.outgoingQueue.add(e);
        this.UpdateSelectorOps();
        this.selector.wakeup();
    }
    
    public void TryProcessIdle() {
        final long elapsedRealtime = SystemClock.elapsedRealtime();
        if (elapsedRealtime >= this.lastReceivedPacketMillis + 10000L && elapsedRealtime >= this.lastPingSent + 5000L) {
            if (this.pingSentCount >= 3) {
                if (!this.timedOut) {
                    this.timedOut = true;
                    Debug.Log("SLCircuit: Total timeout.");
                    this.ProcessTimeout();
                }
            }
            else {
                Debug.Log("SLCircuit: Sending ping ID " + this.lastPingID);
                final StartPingCheck startPingCheck = new StartPingCheck();
                int oldestUnacked = this.lastSeqNum.get();
                final SLMessage slMessage = this.unackedQueue.peek();
                if (slMessage != null) {
                    oldestUnacked = slMessage.seqNum;
                }
                final StartPingCheck.PingID pingID_Field = startPingCheck.PingID_Field;
                final byte lastPingID = this.lastPingID;
                this.lastPingID = (byte)(lastPingID + 1);
                pingID_Field.PingID = lastPingID;
                startPingCheck.PingID_Field.OldestUnacked = oldestUnacked;
                this.SendMessage(startPingCheck);
                ++this.pingSentCount;
                this.lastPingSent = elapsedRealtime;
            }
        }
    }
    
    public void UnregisterMessageHandler(final Object o) {
        synchronized (this) {
            this.messageRouter.unregisterHandler(o);
            if (o instanceof SLIdleHandler) {
                this.idleHandlers.remove(o);
            }
        }
    }
    
    public void UpdateSelectorOps() {
        if (!this.selectionKey.isValid()) {
            return;
        }
        while (true) {
            try {
                if (this.outgoingQueue.isEmpty() && this.pendingAcks.isEmpty()) {
                    this.selectionKey.interestOps(1);
                }
                else {
                    this.selectionKey.interestOps(5);
                }
            }
            catch (final CancelledKeyException ex) {
                Debug.Warning(ex);
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
    
    public int getIdleInterval() {
        monitorenter(this);
        monitorexit(this);
        return 1000;
    }
}
