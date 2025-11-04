package com.lumiyaviewer.lumiya.slproto;

import android.os.SystemClock;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageRouter;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck;
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck;
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
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

/* loaded from: classes.dex */
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
    protected SLGridConnection gridConn;
    private Queue<Integer> handledPackets;
    private long lastReceivedPacketMillis;
    private int lastReceivedSeqnum;
    private AtomicInteger lastSeqNum;
    private ConcurrentLinkedQueue<SLMessage> outgoingQueue;
    private List<Integer> pendingAcks;
    private List<Integer> receivedAcks;
    private ByteBuffer rxBuffer;
    private SelectionKey selectionKey;
    Selector selector;
    private ByteBuffer tempBuffer;
    private ByteBuffer txBuffer;
    private ConcurrentLinkedQueue<SLMessage> unackedQueue;
    protected final EventBus eventBus = EventBus.getInstance();
    private long lastPingSent = 0;
    private int pingSentCount = 0;
    private byte lastPingID = 0;
    private boolean timedOut = false;
    private SLMessageRouter messageRouter = new SLMessageRouter();
    private List<SLIdleHandler> idleHandlers = new LinkedList();

    /* JADX INFO: Access modifiers changed from: package-private */
    public SLCircuit(SLGridConnection sLGridConnection, SLCircuitInfo sLCircuitInfo, SLAuthReply sLAuthReply, SLCircuit sLCircuit) throws IOException {
        this.lastReceivedPacketMillis = 0L;
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
        this.outgoingQueue = new ConcurrentLinkedQueue<>();
        this.unackedQueue = new ConcurrentLinkedQueue<>();
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
        StringBuilder sb = new StringBuilder();
        sb.append(str).append(": ");
        for (int i = 0; i < byteBuffer.limit(); i++) {
            sb.append(Integer.toHexString(byteBuffer.get(i) & UnsignedBytes.MAX_VALUE));
        }
        Debug.Log(sb.toString());
    }

    private void ProcessResends() {
        boolean z;
        boolean z2 = false;
        Iterator<SLMessage> it = this.unackedQueue.iterator();
        while (true) {
            z = z2;
            if (!it.hasNext()) {
                break;
            }
            SLMessage next = it.next();
            if (System.currentTimeMillis() >= next.sentTimeMillis + PING_INTERVAL) {
                it.remove();
                next.retries++;
                if (next.retries > 3) {
                    next.handleMessageTimeout();
                    z2 = z;
                } else {
                    next.isResent = true;
                    next.sentTimeMillis = System.currentTimeMillis();
                    this.outgoingQueue.add(next);
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

    public void DefaultEventQueueHandler(SLCapEventQueue.CapsEventType capsEventType, LLSDNode lLSDNode) {
        if (this.messageRouter.handleEventQueueMessage(capsEventType, lLSDNode)) {
            return;
        }
        Debug.Log("Unhandled event queue msg: type = " + capsEventType);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void DefaultMessageHandler(SLMessage sLMessage) {
        this.messageRouter.handleMessage(sLMessage);
    }

    public void HandleMessage(SLMessage sLMessage) {
        sLMessage.Handle(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandlePacketAck(PacketAck packetAck) {
        Iterator<T> it = packetAck.Packets_Fields.iterator();
        while (it.hasNext()) {
            ProcessReceivedAck(((PacketAck.Packets) it.next()).ID);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
    public void HandleStartPingCheck(StartPingCheck startPingCheck) {
        CompletePingCheck completePingCheck = new CompletePingCheck();
        completePingCheck.PingID_Field.PingID = startPingCheck.PingID_Field.PingID;
        SendMessage(completePingCheck);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void InvokeProcessIdle() {
        ProcessIdle();
        Iterator<T> it = this.idleHandlers.iterator();
        while (it.hasNext()) {
            ((SLIdleHandler) it.next()).ProcessIdle();
        }
    }

    public void ProcessCloseCircuit() {
    }

    public void ProcessIdle() {
    }

    public void ProcessNetworkError() {
    }

    /* JADX WARN: Removed duplicated region for block: B:54:0x0109  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean ProcessReceive() throws java.io.IOException {
        /*
            Method dump skipped, instructions count: 270
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.SLCircuit.ProcessReceive():boolean");
    }

    public void ProcessReceivedAck(int i) {
        Iterator<SLMessage> it = this.unackedQueue.iterator();
        while (it.hasNext()) {
            SLMessage next = it.next();
            if (next.seqNum == i) {
                it.remove();
                next.handleMessageAcknowledged();
            }
        }
        Iterator<SLMessage> it2 = this.outgoingQueue.iterator();
        while (it2.hasNext()) {
            SLMessage next2 = it2.next();
            if (next2.seqNum == i) {
                it2.remove();
                next2.handleMessageAcknowledged();
            }
        }
    }

    public void ProcessTimeout() {
    }

    public boolean ProcessTransmit() throws IOException {
        SLMessage peek = this.outgoingQueue.peek();
        if (peek != null) {
            peek.Pack(this.txBuffer, this.tempBuffer);
            int AppendPendingAcks = peek.AppendPendingAcks(this.txBuffer, this.pendingAcks);
            this.txBuffer.flip();
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                this.outgoingQueue.remove(peek);
                if (AppendPendingAcks >= this.pendingAcks.size()) {
                    this.pendingAcks.clear();
                } else {
                    for (int i = 0; i < AppendPendingAcks; i++) {
                        this.pendingAcks.remove(0);
                    }
                }
                if (peek.isReliable) {
                    this.unackedQueue.add(peek);
                }
                return true;
            }
        } else if (!this.pendingAcks.isEmpty()) {
            PacketAck packetAck = new PacketAck();
            packetAck.seqNum = this.lastSeqNum.incrementAndGet();
            Iterator<Integer> it = this.pendingAcks.iterator();
            int i2 = 0;
            while (it.hasNext() && packetAck.CalcPayloadSize() < 1018) {
                PacketAck.Packets packets = new PacketAck.Packets();
                packets.ID = it.next().intValue();
                packetAck.Packets_Fields.add(packets);
                i2++;
            }
            packetAck.Pack(this.txBuffer, this.tempBuffer);
            this.txBuffer.flip();
            if (this.datagramChannel.write(this.txBuffer) != 0) {
                if (i2 >= this.pendingAcks.size()) {
                    this.pendingAcks.clear();
                } else {
                    for (int i3 = 0; i3 < i2; i3++) {
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
        if (elapsedRealtime < this.lastReceivedPacketMillis + NEED_PING_TIMEOUT || elapsedRealtime < this.lastPingSent + PING_INTERVAL) {
            return;
        }
        if (this.pingSentCount >= 3) {
            if (this.timedOut) {
                return;
            }
            this.timedOut = true;
            Debug.Log("SLCircuit: Total timeout.");
            ProcessTimeout();
            return;
        }
        Debug.Log("SLCircuit: Sending ping ID " + ((int) this.lastPingID));
        StartPingCheck startPingCheck = new StartPingCheck();
        int i = this.lastSeqNum.get();
        SLMessage peek = this.unackedQueue.peek();
        int i2 = peek != null ? peek.seqNum : i;
        StartPingCheck.PingID pingID = startPingCheck.PingID_Field;
        byte b = this.lastPingID;
        this.lastPingID = (byte) (b + 1);
        pingID.PingID = b;
        startPingCheck.PingID_Field.OldestUnacked = i2;
        SendMessage(startPingCheck);
        this.pingSentCount++;
        this.lastPingSent = elapsedRealtime;
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
            } catch (CancelledKeyException e) {
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
