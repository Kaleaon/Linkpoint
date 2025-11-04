// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.xfer;

import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.SendXferPacket;
import java.util.Collections;
import java.util.HashMap;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Map;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;

public class SLXferManager extends SLModule
{
    private Map<String, Long> activeTransferIDs;
    private Map<Long, SLXfer> activeTransfers;
    private AtomicLong nextID;
    
    public SLXferManager(final SLAgentCircuit slAgentCircuit) {
        super(slAgentCircuit);
        this.activeTransfers = Collections.synchronizedMap(new HashMap<Long, SLXfer>());
        this.activeTransferIDs = Collections.synchronizedMap(new HashMap<String, Long>());
        this.nextID = new AtomicLong(1L);
    }
    
    @SLMessageHandler
    public void HandleSendXferPacket(final SendXferPacket sendXferPacket) {
        synchronized (this) {
            final Long value = sendXferPacket.XferID_Field.ID;
            final SLXfer slXfer = this.activeTransfers.get(value);
            if (slXfer != null) {
                slXfer.HandleDataPacket(this, sendXferPacket);
                if (slXfer.isCompleted()) {
                    this.activeTransfers.remove(value);
                    this.activeTransferIDs.remove(slXfer.getFilename());
                    slXfer.invokeListeners();
                }
            }
        }
    }
    
    public void RequestXfer(final String s, final ELLPath ellPath, final boolean b, final SLXfer.SLXferCompletionListener slXferCompletionListener, final Object o) {
        synchronized (this) {
            final Long n = this.activeTransferIDs.get(s);
            if (n != null) {
                final SLXfer slXfer = this.activeTransfers.get(n);
                if (slXfer != null) {
                    slXfer.addListener(slXferCompletionListener, o);
                    return;
                }
            }
            final Long value = this.nextID.incrementAndGet();
            this.activeTransferIDs.put(s, value);
            final SLXfer slXfer2 = new SLXfer(value, s, ellPath, b);
            slXfer2.addListener(slXferCompletionListener, o);
            this.activeTransfers.put(value, slXfer2);
            slXfer2.StartTransfer(this);
        }
    }
}
