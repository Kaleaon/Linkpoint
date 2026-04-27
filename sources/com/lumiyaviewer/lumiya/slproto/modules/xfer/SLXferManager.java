// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.xfer;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.messages.SendXferPacket;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.xfer:
//            SLXfer, ELLPath

public class SLXferManager extends SLModule
{

    private Map activeTransferIDs;
    private Map activeTransfers;
    private AtomicLong nextID;

    public SLXferManager(SLAgentCircuit slagentcircuit)
    {
        super(slagentcircuit);
        activeTransfers = Collections.synchronizedMap(new HashMap());
        activeTransferIDs = Collections.synchronizedMap(new HashMap());
        nextID = new AtomicLong(1L);
    }

    public void HandleSendXferPacket(SendXferPacket sendxferpacket)
    {
        this;
        JVM INSTR monitorenter ;
        Long long1;
        SLXfer slxfer;
        long1 = Long.valueOf(sendxferpacket.XferID_Field.ID);
        slxfer = (SLXfer)activeTransfers.get(long1);
        if (slxfer == null)
        {
            break MISSING_BLOCK_LABEL_73;
        }
        slxfer.HandleDataPacket(this, sendxferpacket);
        if (slxfer.isCompleted())
        {
            activeTransfers.remove(long1);
            activeTransferIDs.remove(slxfer.getFilename());
            slxfer.invokeListeners();
        }
        this;
        JVM INSTR monitorexit ;
        return;
        sendxferpacket;
        throw sendxferpacket;
    }

    public void RequestXfer(String s, ELLPath ellpath, boolean flag, SLXfer.SLXferCompletionListener slxfercompletionlistener, Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj1 = (Long)activeTransferIDs.get(s);
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_55;
        }
        obj1 = (SLXfer)activeTransfers.get(obj1);
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_55;
        }
        ((SLXfer) (obj1)).addListener(slxfercompletionlistener, obj);
        this;
        JVM INSTR monitorexit ;
        return;
        Long long1 = Long.valueOf(nextID.incrementAndGet());
        activeTransferIDs.put(s, long1);
        s = new SLXfer(long1.longValue(), s, ellpath, flag);
        s.addListener(slxfercompletionlistener, obj);
        activeTransfers.put(long1, s);
        s.StartTransfer(this);
        this;
        JVM INSTR monitorexit ;
        return;
        s;
        throw s;
    }
}
