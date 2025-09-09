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
        boolean z;
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
        sLMessage.lambda$-com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833(this);
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

    /* DevToolsApp WARNING: Removed duplicated region for block: B:48:0x0109  */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:16:0x007f A:{LOOP_START, LOOP:0: B:16:0x007f->B:19:0x008f} */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:16:0x007f A:{LOOP_START, LOOP:0: B:16:0x007f->B:19:0x008f} */
    /* DevToolsApp WARNING: Removed duplicated region for block: B:48:0x0109  */
    public boolean ProcessReceive() throws java.io.IOException {
        /*
        r8 = this;
        r0 = 0;
        r3 = 1;
        r4 = 0;
        r1 = r8.rxBuffer;
        r1.clear();
        r1 = r8.rxBuffer;
        r2 = java.nio.ByteOrder.BIG_ENDIAN;
        r1.order(r2);
        r1 = r8.datagramChannel;
        r2 = r8.rxBuffer;
        r1 = r1.read(r2);
        if (r1 == 0) goto L_0x0106;
    L_0x0019:
        r1 = r8.rxBuffer;
        r1.flip();
        r1 = r8.receivedAcks;
        r1.clear();
        r1 = r8.rxBuffer;
        r2 = r8.tempBuffer;
        r5 = r8.receivedAcks;
        r2 = com.lumiyaviewer.lumiya.slproto.SLMessage.Unpack(r1, r2, r5);
        if (r2 == 0) goto L_0x00ca;
    L_0x002f:
        r6 = android.os.SystemClock.elapsedRealtime();
        r8.lastReceivedPacketMillis = r6;
        r8.pingSentCount = r4;
        r1 = r2.seqNum;
        r5 = r8.lastReceivedSeqnum;
        r1 = r1 - r5;
        if (r1 > 0) goto L_0x010b;
    L_0x003e:
        r1 = "Detected incoming out of order: seqNum = %d";
        r5 = new java.lang.Object[r3];
        r6 = r2.seqNum;
        r6 = java.lang.Integer.valueOf(r6);
        r5[r4] = r6;
        com.lumiyaviewer.lumiya.Debug.Printf(r1, r5);
        r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck;
        if (r1 != 0) goto L_0x00c6;
    L_0x0052:
        r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck;
        r1 = r1 ^ 1;
        if (r1 == 0) goto L_0x00c6;
    L_0x0058:
        r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck;
        r1 = r1 ^ 1;
        if (r1 == 0) goto L_0x010b;
    L_0x005e:
        r1 = r8.handledPackets;
        r5 = r2.seqNum;
        r5 = java.lang.Integer.valueOf(r5);
        r1 = r1.contains(r5);
        if (r1 == 0) goto L_0x010b;
    L_0x006c:
        r1 = "Detected incoming duplicate: seqNum = %d";
        r5 = new java.lang.Object[r3];
        r6 = r2.seqNum;
        r6 = java.lang.Integer.valueOf(r6);
        r5[r4] = r6;
        com.lumiyaviewer.lumiya.Debug.Printf(r1, r5);
        r1 = r3;
    L_0x007d:
        if (r1 != 0) goto L_0x0109;
    L_0x007f:
        r1 = r8.handledPackets;
        r1 = r1.size();
        r5 = 1024; // 0x400 float:1.435E-42 double:5.06E-321;
        if (r1 < r5) goto L_0x0091;
    L_0x0089:
        r1 = r8.handledPackets;
        r1 = r1.poll();
        if (r1 != 0) goto L_0x007f;
    L_0x0091:
        r1 = r8.handledPackets;
        r5 = r2.seqNum;
        r5 = java.lang.Integer.valueOf(r5);
        r1.add(r5);
        r1 = r2.seqNum;
        r8.lastReceivedSeqnum = r1;
        r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.PacketAck;
        if (r1 != 0) goto L_0x00a8;
    L_0x00a4:
        r1 = r2 instanceof com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck;
        if (r1 == 0) goto L_0x00c8;
    L_0x00a8:
        r2.lambda$-com_lumiyaviewer_lumiya_slproto_SLThreadingCircuit_1833(r8);
        r1 = r0;
    L_0x00ac:
        r0 = r8.receivedAcks;
        r5 = r0.iterator();
    L_0x00b2:
        r0 = r5.hasNext();
        if (r0 == 0) goto L_0x00d2;
    L_0x00b8:
        r0 = r5.next();
        r0 = (java.lang.Integer) r0;
        r0 = r0.intValue();
        r8.ProcessReceivedAck(r0);
        goto L_0x00b2;
    L_0x00c6:
        r1 = r4;
        goto L_0x007d;
    L_0x00c8:
        r1 = r2;
        goto L_0x00ac;
    L_0x00ca:
        r1 = "message discarded!";
        com.lumiyaviewer.lumiya.Debug.Log(r1);
        r1 = r0;
        goto L_0x00ac;
    L_0x00d2:
        if (r2 == 0) goto L_0x0100;
    L_0x00d4:
        r0 = r2.isReliable;
        if (r0 == 0) goto L_0x0100;
    L_0x00d8:
        r0 = r8.pendingAcks;
        r5 = r0.iterator();
    L_0x00de:
        r0 = r5.hasNext();
        if (r0 == 0) goto L_0x0107;
    L_0x00e4:
        r0 = r5.next();
        r0 = (java.lang.Integer) r0;
        r0 = r0.intValue();
        r6 = r2.seqNum;
        if (r0 != r6) goto L_0x00de;
    L_0x00f2:
        r0 = r3;
    L_0x00f3:
        if (r0 != 0) goto L_0x0100;
    L_0x00f5:
        r0 = r8.pendingAcks;
        r2 = r2.seqNum;
        r2 = java.lang.Integer.valueOf(r2);
        r0.add(r2);
    L_0x0100:
        if (r1 == 0) goto L_0x0105;
    L_0x0102:
        r8.HandleMessage(r1);
    L_0x0105:
        return r3;
    L_0x0106:
        return r4;
    L_0x0107:
        r0 = r4;
        goto L_0x00f3;
    L_0x0109:
        r1 = r0;
        goto L_0x00ac;
    L_0x010b:
        r1 = r4;
        goto L_0x007d;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.slproto.SLCircuit.ProcessReceive():boolean");
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
        int i;
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
