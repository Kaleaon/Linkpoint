// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import android.os.SystemClock;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.eventbus.EventBus;
import com.lumiyaviewer.lumiya.slproto.auth.SLAuthReply;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageRouter;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDNode;
import com.lumiyaviewer.lumiya.slproto.messages.CompletePingCheck;
import com.lumiyaviewer.lumiya.slproto.messages.PacketAck;
import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.StartPingCheck;
import com.lumiyaviewer.lumiya.slproto.modules.SLIdleHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
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

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLGridConnection, SLCircuitInfo, SLMessage

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
    protected final EventBus eventBus = EventBus.getInstance();
    protected SLGridConnection gridConn;
    private Queue handledPackets;
    private List idleHandlers;
    private byte lastPingID;
    private long lastPingSent;
    private long lastReceivedPacketMillis;
    private int lastReceivedSeqnum;
    private AtomicInteger lastSeqNum;
    private SLMessageRouter messageRouter;
    private ConcurrentLinkedQueue outgoingQueue;
    private List pendingAcks;
    private int pingSentCount;
    private List receivedAcks;
    private ByteBuffer rxBuffer;
    private SelectionKey selectionKey;
    Selector selector;
    private ByteBuffer tempBuffer;
    private boolean timedOut;
    private ByteBuffer txBuffer;
    private ConcurrentLinkedQueue unackedQueue;

    SLCircuit(SLGridConnection slgridconnection, SLCircuitInfo slcircuitinfo, SLAuthReply slauthreply, SLCircuit slcircuit)
        throws IOException
    {
        lastReceivedPacketMillis = 0L;
        lastPingSent = 0L;
        pingSentCount = 0;
        lastPingID = 0;
        timedOut = false;
        messageRouter = new SLMessageRouter();
        idleHandlers = new LinkedList();
        gridConn = slgridconnection;
        circuitInfo = slcircuitinfo;
        authReply = slauthreply;
        selector = slgridconnection.getSelector();
        if (slcircuit != null)
        {
            lastReceivedPacketMillis = slcircuit.lastReceivedPacketMillis;
            lastSeqNum = slcircuit.lastSeqNum;
            outgoingQueue = slcircuit.outgoingQueue;
            unackedQueue = slcircuit.unackedQueue;
            pendingAcks = slcircuit.pendingAcks;
            handledPackets = slcircuit.handledPackets;
            lastReceivedSeqnum = slcircuit.lastReceivedSeqnum;
            receivedAcks = slcircuit.receivedAcks;
            txBuffer = slcircuit.txBuffer;
            tempBuffer = slcircuit.tempBuffer;
            rxBuffer = slcircuit.rxBuffer;
            datagramChannel = slcircuit.datagramChannel;
            selectionKey = slcircuit.selectionKey;
            selectionKey.attach(this);
            return;
        } else
        {
            lastReceivedPacketMillis = SystemClock.elapsedRealtime();
            lastSeqNum = new AtomicInteger(0);
            outgoingQueue = new ConcurrentLinkedQueue();
            unackedQueue = new ConcurrentLinkedQueue();
            pendingAcks = Collections.synchronizedList(new LinkedList());
            handledPackets = new LinkedList();
            lastReceivedSeqnum = 0;
            receivedAcks = new ArrayList();
            txBuffer = ByteBuffer.allocate(0x10000);
            tempBuffer = ByteBuffer.allocate(0x10000);
            rxBuffer = ByteBuffer.allocate(0x10000);
            datagramChannel = DatagramChannel.open();
            datagramChannel.configureBlocking(false);
            datagramChannel.connect(slcircuitinfo.socketAddress);
            selectionKey = datagramChannel.register(selector, 1);
            selectionKey.attach(this);
            return;
        }
    }

    private void DumpDebugBuffer(String s, ByteBuffer bytebuffer)
    {
        StringBuilder stringbuilder = new StringBuilder();
        stringbuilder.append(s).append(": ");
        for (int i = 0; i < bytebuffer.limit(); i++)
        {
            stringbuilder.append(Integer.toHexString(bytebuffer.get(i) & 0xff));
        }

        Debug.Log(stringbuilder.toString());
    }

    private void ProcessResends()
    {
        Iterator iterator = unackedQueue.iterator();
        boolean flag = false;
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            SLMessage slmessage = (SLMessage)iterator.next();
            if (System.currentTimeMillis() >= slmessage.sentTimeMillis + 5000L)
            {
                iterator.remove();
                slmessage.retries = slmessage.retries + 1;
                if (slmessage.retries > 3)
                {
                    slmessage.handleMessageTimeout();
                } else
                {
                    slmessage.isResent = true;
                    slmessage.sentTimeMillis = System.currentTimeMillis();
                    outgoingQueue.add(slmessage);
                    flag = true;
                }
            }
        } while (true);
        if (flag)
        {
            UpdateSelectorOps();
            selector.wakeup();
        }
        TryProcessIdle();
    }

    public void CloseCircuit()
    {
        selectionKey.cancel();
        try
        {
            datagramChannel.close();
        }
        catch (IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        selector.wakeup();
        ProcessCloseCircuit();
    }

    public void DefaultEventQueueHandler(com.lumiyaviewer.lumiya.slproto.caps.SLCapEventQueue.CapsEventType capseventtype, LLSDNode llsdnode)
    {
        if (!messageRouter.handleEventQueueMessage(capseventtype, llsdnode))
        {
            Debug.Log((new StringBuilder()).append("Unhandled event queue msg: type = ").append(capseventtype).toString());
        }
    }

    public void DefaultMessageHandler(SLMessage slmessage)
    {
        messageRouter.handleMessage(slmessage);
    }

    public void HandleMessage(SLMessage slmessage)
    {
        slmessage.Handle(this);
    }

    public void HandlePacketAck(PacketAck packetack)
    {
        for (packetack = packetack.Packets_Fields.iterator(); packetack.hasNext(); ProcessReceivedAck(((com.lumiyaviewer.lumiya.slproto.messages.PacketAck.Packets)packetack.next()).ID)) { }
    }

    public void HandleStartPingCheck(StartPingCheck startpingcheck)
    {
        CompletePingCheck completepingcheck = new CompletePingCheck();
        completepingcheck.PingID_Field.PingID = startpingcheck.PingID_Field.PingID;
        SendMessage(completepingcheck);
    }

    protected void InvokeProcessIdle()
    {
        ProcessIdle();
        for (Iterator iterator = idleHandlers.iterator(); iterator.hasNext(); ((SLIdleHandler)iterator.next()).ProcessIdle()) { }
    }

    public void ProcessCloseCircuit()
    {
    }

    public void ProcessIdle()
    {
    }

    public void ProcessNetworkError()
    {
    }

    public boolean ProcessReceive()
        throws IOException
    {
        rxBuffer.clear();
        rxBuffer.order(ByteOrder.BIG_ENDIAN);
        if (datagramChannel.read(rxBuffer) == 0) goto _L2; else goto _L1
_L1:
        SLMessage slmessage1;
        rxBuffer.flip();
        receivedAcks.clear();
        slmessage1 = SLMessage.Unpack(rxBuffer, tempBuffer, receivedAcks);
        if (slmessage1 == null) goto _L4; else goto _L3
_L3:
        lastReceivedPacketMillis = SystemClock.elapsedRealtime();
        pingSentCount = 0;
        if (slmessage1.seqNum - lastReceivedSeqnum > 0) goto _L6; else goto _L5
_L5:
        Debug.Printf("Detected incoming out of order: seqNum = %d", new Object[] {
            Integer.valueOf(slmessage1.seqNum)
        });
        if ((slmessage1 instanceof PacketAck) || !((slmessage1 instanceof StartPingCheck) ^ true)) goto _L8; else goto _L7
_L7:
        if (!((slmessage1 instanceof CompletePingCheck) ^ true) || !handledPackets.contains(Integer.valueOf(slmessage1.seqNum))) goto _L6; else goto _L9
_L9:
        boolean flag;
        Debug.Printf("Detected incoming duplicate: seqNum = %d", new Object[] {
            Integer.valueOf(slmessage1.seqNum)
        });
        flag = true;
_L20:
        SLMessage slmessage;
        Iterator iterator1;
        if (!flag)
        {
            while (handledPackets.size() >= 1024 && handledPackets.poll() != null) ;
            handledPackets.add(Integer.valueOf(slmessage1.seqNum));
            lastReceivedSeqnum = slmessage1.seqNum;
            Iterator iterator;
            if ((slmessage1 instanceof PacketAck) || (slmessage1 instanceof StartPingCheck))
            {
                slmessage1.Handle(this);
                slmessage = null;
            } else
            {
                slmessage = slmessage1;
            }
        } else
        {
            slmessage = null;
        }
_L11:
        for (iterator = receivedAcks.iterator(); iterator.hasNext(); ProcessReceivedAck(((Integer)iterator.next()).intValue())) { }
        break; /* Loop/switch isn't completed */
_L8:
        flag = false;
        continue; /* Loop/switch isn't completed */
_L4:
        Debug.Log("message discarded!");
        slmessage = null;
        if (true) goto _L11; else goto _L10
_L10:
        if (slmessage1 == null || !slmessage1.isReliable) goto _L13; else goto _L12
_L12:
        iterator1 = pendingAcks.iterator();
_L17:
        if (!iterator1.hasNext()) goto _L15; else goto _L14
_L14:
        if (((Integer)iterator1.next()).intValue() != slmessage1.seqNum) goto _L17; else goto _L16
_L16:
        flag = true;
_L18:
        if (!flag)
        {
            pendingAcks.add(Integer.valueOf(slmessage1.seqNum));
        }
_L13:
        if (slmessage != null)
        {
            HandleMessage(slmessage);
        }
        return true;
_L2:
        return false;
_L15:
        flag = false;
        if (true) goto _L18; else goto _L6
_L6:
        flag = false;
        if (true) goto _L20; else goto _L19
_L19:
    }

    public void ProcessReceivedAck(int i)
    {
        Iterator iterator = unackedQueue.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            SLMessage slmessage = (SLMessage)iterator.next();
            if (slmessage.seqNum == i)
            {
                iterator.remove();
                slmessage.handleMessageAcknowledged();
            }
        } while (true);
        iterator = outgoingQueue.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            SLMessage slmessage1 = (SLMessage)iterator.next();
            if (slmessage1.seqNum == i)
            {
                iterator.remove();
                slmessage1.handleMessageAcknowledged();
            }
        } while (true);
    }

    public void ProcessTimeout()
    {
    }

    public boolean ProcessTransmit()
        throws IOException
    {
        SLMessage slmessage = (SLMessage)outgoingQueue.peek();
        if (slmessage != null)
        {
            slmessage.Pack(txBuffer, tempBuffer);
            int k = slmessage.AppendPendingAcks(txBuffer, pendingAcks);
            txBuffer.flip();
            if (datagramChannel.write(txBuffer) != 0)
            {
                outgoingQueue.remove(slmessage);
                if (k >= pendingAcks.size())
                {
                    pendingAcks.clear();
                } else
                {
                    int i = 0;
                    while (i < k) 
                    {
                        pendingAcks.remove(0);
                        i++;
                    }
                }
                if (slmessage.isReliable)
                {
                    unackedQueue.add(slmessage);
                }
                return true;
            }
        } else
        if (!pendingAcks.isEmpty())
        {
            PacketAck packetack = new PacketAck();
            packetack.seqNum = lastSeqNum.incrementAndGet();
            Iterator iterator = pendingAcks.iterator();
            int j;
            for (j = 0; iterator.hasNext() && packetack.CalcPayloadSize() < 1018; j++)
            {
                com.lumiyaviewer.lumiya.slproto.messages.PacketAck.Packets packets = new com.lumiyaviewer.lumiya.slproto.messages.PacketAck.Packets();
                packets.ID = ((Integer)iterator.next()).intValue();
                packetack.Packets_Fields.add(packets);
            }

            packetack.Pack(txBuffer, tempBuffer);
            txBuffer.flip();
            if (datagramChannel.write(txBuffer) != 0)
            {
                if (j >= pendingAcks.size())
                {
                    pendingAcks.clear();
                } else
                {
                    int l = 0;
                    while (l < j) 
                    {
                        pendingAcks.remove(0);
                        l++;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public void ProcessWakeup()
    {
        ProcessResends();
        TryProcessIdle();
    }

    public void RegisterMessageHandler(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        messageRouter.registerHandler(obj);
        if (obj instanceof SLIdleHandler)
        {
            idleHandlers.add((SLIdleHandler)obj);
        }
        this;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    public void SendMessage(SLMessage slmessage)
    {
        slmessage.seqNum = lastSeqNum.incrementAndGet();
        slmessage.sentTimeMillis = System.currentTimeMillis();
        slmessage.retries = 0;
        outgoingQueue.add(slmessage);
        UpdateSelectorOps();
        selector.wakeup();
    }

    public void TryProcessIdle()
    {
        long l;
label0:
        {
            l = SystemClock.elapsedRealtime();
            if (l >= lastReceivedPacketMillis + 10000L && l >= lastPingSent + 5000L)
            {
                if (pingSentCount < 3)
                {
                    break label0;
                }
                if (!timedOut)
                {
                    timedOut = true;
                    Debug.Log("SLCircuit: Total timeout.");
                    ProcessTimeout();
                }
            }
            return;
        }
        Debug.Log((new StringBuilder()).append("SLCircuit: Sending ping ID ").append(lastPingID).toString());
        StartPingCheck startpingcheck = new StartPingCheck();
        int i = lastSeqNum.get();
        Object obj = (SLMessage)unackedQueue.peek();
        if (obj != null)
        {
            i = ((SLMessage) (obj)).seqNum;
        }
        obj = startpingcheck.PingID_Field;
        int j = lastPingID;
        lastPingID = (byte)(j + 1);
        obj.PingID = j;
        startpingcheck.PingID_Field.OldestUnacked = i;
        SendMessage(startpingcheck);
        pingSentCount = pingSentCount + 1;
        lastPingSent = l;
    }

    public void UnregisterMessageHandler(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        messageRouter.unregisterHandler(obj);
        if (obj instanceof SLIdleHandler)
        {
            idleHandlers.remove((SLIdleHandler)obj);
        }
        this;
        JVM INSTR monitorexit ;
        return;
        obj;
        throw obj;
    }

    public void UpdateSelectorOps()
    {
        if (!selectionKey.isValid())
        {
            break MISSING_BLOCK_LABEL_57;
        }
        if (outgoingQueue.isEmpty() && pendingAcks.isEmpty())
        {
            selectionKey.interestOps(1);
            return;
        }
        try
        {
            selectionKey.interestOps(5);
            return;
        }
        catch (CancelledKeyException cancelledkeyexception)
        {
            Debug.Warning(cancelledkeyexception);
        }
    }

    public SLAuthReply getAuthReply()
    {
        return authReply;
    }

    public EventBus getEventBus()
    {
        return eventBus;
    }

    public SLGridConnection getGridConnection()
    {
        return gridConn;
    }

    public int getIdleInterval()
    {
        this;
        JVM INSTR monitorenter ;
        return 1000;
    }
}
