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
    private byte lastPingID = 0;
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
        if (!this.messageRouter.handleEventQueueMessage(capsEventType, lLSDNode)) {
            Debug.Log("Unhandled event queue msg: type = " + capsEventType);
        }
    }

    public void DefaultMessageHandler(SLMessage sLMessage) {
        boolean handleMessage = this.messageRouter.handleMessage(sLMessage);
    }

    public void HandleMessage(SLMessage sLMessage) {
        sLMessage.Handle(this);
    }

    public void HandlePacketAck(PacketAck packetAck) {
        for (PacketAck.Packets packets : packetAck.Packets_Fields) {
            ProcessReceivedAck(packets.ID);
        }
    }

    public void HandleStartPingCheck(StartPingCheck startPingCheck) {
        CompletePingCheck completePingCheck = new CompletePingCheck();
        completePingCheck.PingID_Field.PingID = startPingCheck.PingID_Field.PingID;
        SendMessage(completePingCheck);
    }

    /* access modifiers changed from: protected */
    public void InvokeProcessIdle() {
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

    /* JADX WARNING: Removed duplicated region for block: B:16:0x007f A[LOOP:0: B:16:0x007f->B:19:0x008f, LOOP_START] */
    /* JADX WARNING: Removed duplicated region for block: B:48:0x0109  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean ProcessReceive() throws java.io.IOException {
        /*
            r8 = this;
            r0 = 0
            r3 = 1
            r4 = 0
            java.nio.ByteBuffer r1 = r8.rxBuffer
            r1.clear()
            java.nio.ByteBuffer r1 = r8.rxBuffer
            java.nio.ByteOrder r2 = java.nio.ByteOrder.BIG_ENDIAN
            r1.order(r2)
            java.nio.channels.DatagramChannel r1 = r8.datagramChannel
            java.nio.ByteBuffer r2 = r8.rxBuffer
            int r1 = r1.read(r2)
            if (r1 == 0) goto L_0x0106
            java.nio.ByteBuffer r1 = r8.rxBuffer
            r1.flip()
            java.util.List<java.lang.Integer> r1 = r8.receivedAcks
            r1.clear()
            java.nio.ByteBuffer r1 = r8.rxBuffer
            java.nio.ByteBuffer r2 = r8.tempBuffer
            java.util.List<java.lang.Integer> r5 = r8.receivedAcks
            com.lumiyaviewer.lumiya.slproto.SLMessage r2 = com.lumiyaviewer.lumiya.slproto.SLMessage.Unpack(r1, r2, r5)
            if (r2 == 0) goto L_0x00ca
            long r6 = android.os.SystemClock.elapsedRealtime()
            r8.lastReceivedPacketMillis = r6
            r8.pingSentCount = r4
            int r1 = r2.seqNum
            int r5 = r8.lastReceivedSeqnum
            int r1 = r1 - r5
            if (r1 > 0) goto L_0x010b
            java.lang.String r1 = "Detected incoming out of order: seqNum = %d"
            java.lang.Object[] r5 = new java.lang.Object[r3]
            int r6 = r2.seqNum
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r4] = r6
            com.lumiyaviewer.lumiya.Debug.Printf(r1, r5)
            boolean r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck
            if (r1 != 0) goto L_0x00c6
            boolean r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck
            r1 = r1 ^ 1
            if (r1 == 0) goto L_0x00c6
            boolean r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck
            r1 = r1 ^ 1
            if (r1 == 0) goto L_0x010b
            java.util.Queue<java.lang.Integer> r1 = r8.handledPackets
            int r5 = r2.seqNum
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            boolean r1 = r1.contains(r5)
            if (r1 == 0) goto L_0x010b
            java.lang.String r1 = "Detected incoming duplicate: seqNum = %d"
            java.lang.Object[] r5 = new java.lang.Object[r3]
            int r6 = r2.seqNum
            java.lang.Integer r6 = java.lang.Integer.valueOf(r6)
            r5[r4] = r6
            com.lumiyaviewer.lumiya.Debug.Printf(r1, r5)
            r1 = r3
        L_0x007d:
            if (r1 != 0) goto L_0x0109
        L_0x007f:
            java.util.Queue<java.lang.Integer> r1 = r8.handledPackets
            int r1 = r1.size()
            r5 = 1024(0x400, float:1.435E-42)
            if (r1 < r5) goto L_0x0091
            java.util.Queue<java.lang.Integer> r1 = r8.handledPackets
            java.lang.Object r1 = r1.poll()
            if (r1 != 0) goto L_0x007f
        L_0x0091:
            java.util.Queue<java.lang.Integer> r1 = r8.handledPackets
            int r5 = r2.seqNum
            java.lang.Integer r5 = java.lang.Integer.valueOf(r5)
            r1.add(r5)
            int r1 = r2.seqNum
            r8.lastReceivedSeqnum = r1
            boolean r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck
            if (r1 != 0) goto L_0x00a8
            boolean r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck
            if (r1 == 0) goto L_0x00c8
        L_0x00a8:
            r2.Handle(r8)
            r1 = r0
        L_0x00ac:
            java.util.List<java.lang.Integer> r0 = r8.receivedAcks
            java.util.Iterator r5 = r0.iterator()
        L_0x00b2:
            boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x00d2
            java.lang.Object r0 = r5.next()
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            r8.ProcessReceivedAck(r0)
            goto L_0x00b2
        L_0x00c6:
            r1 = r4
            goto L_0x007d
        L_0x00c8:
            r1 = r2
            goto L_0x00ac
        L_0x00ca:
            java.lang.String r1 = "message discarded!"
            com.lumiyaviewer.lumiya.Debug.Log(r1)
            r1 = r0
            goto L_0x00ac
        L_0x00d2:
            if (r2 == 0) goto L_0x0100
            boolean r0 = r2.isReliable
            if (r0 == 0) goto L_0x0100
            java.util.List<java.lang.Integer> r0 = r8.pendingAcks
            java.util.Iterator r5 = r0.iterator()
        L_0x00de:
            boolean r0 = r5.hasNext()
            if (r0 == 0) goto L_0x0107
            java.lang.Object r0 = r5.next()
            java.lang.Integer r0 = (java.lang.Integer) r0
            int r0 = r0.intValue()
            int r6 = r2.seqNum
            if (r0 != r6) goto L_0x00de
            r0 = r3
        L_0x00f3:
            if (r0 != 0) goto L_0x0100
            java.util.List<java.lang.Integer> r0 = r8.pendingAcks
            int r2 = r2.seqNum
            java.lang.Integer r2 = java.lang.Integer.valueOf(r2)
            r0.add(r2)
        L_0x0100:
            if (r1 == 0) goto L_0x0105
            r8.HandleMessage(r1)
        L_0x0105:
            return r3
        L_0x0106:
            return r4
        L_0x0107:
            r0 = r4
            goto L_0x00f3
        L_0x0109:
            r1 = r0
            goto L_0x00ac
        L_0x010b:
            r1 = r4
            goto L_0x007d
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
        if (elapsedRealtime >= this.lastReceivedPacketMillis + NEED_PING_TIMEOUT && elapsedRealtime >= this.lastPingSent + PING_INTERVAL) {
            if (this.pingSentCount < 3) {
                Debug.Log("SLCircuit: Sending ping ID " + this.lastPingID);
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
                if (!this.outgoingQueue.isEmpty() || !this.pendingAcks.isEmpty()) {
                    this.selectionKey.interestOps(5);
                } else {
                    this.selectionKey.interestOps(1);
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
