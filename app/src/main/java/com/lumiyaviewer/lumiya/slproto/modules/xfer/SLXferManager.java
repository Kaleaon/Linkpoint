package com.lumiyaviewer.lumiya.slproto.modules.xfer;

import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler;
import com.lumiyaviewer.lumiya.slproto.messages.SendXferPacket;
import com.lumiyaviewer.lumiya.slproto.modules.SLModule;
import com.lumiyaviewer.lumiya.slproto.modules.xfer.SLXfer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class SLXferManager extends SLModule {
    private Map<String, Long> activeTransferIDs = Collections.synchronizedMap(new HashMap());
    private Map<Long, SLXfer> activeTransfers = Collections.synchronizedMap(new HashMap());
    private AtomicLong nextID = new AtomicLong(1);

    public SLXferManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit);
    }

    @SLMessageHandler
    public synchronized void HandleSendXferPacket(SendXferPacket sendXferPacket) {
        Long valueOf = Long.valueOf(sendXferPacket.XferID_Field.ID);
        SLXfer sLXfer = this.activeTransfers.get(valueOf);
        if (sLXfer != null) {
            sLXfer.HandleDataPacket(this, sendXferPacket);
            if (sLXfer.isCompleted()) {
                this.activeTransfers.remove(valueOf);
                this.activeTransferIDs.remove(sLXfer.getFilename());
                sLXfer.invokeListeners();
            }
        }
    }

    public synchronized void RequestXfer(String str, ELLPath eLLPath, boolean z, SLXfer.SLXferCompletionListener sLXferCompletionListener, Object obj) {
        SLXfer sLXfer;
        Long l = this.activeTransferIDs.get(str);
        if (l == null || (sLXfer = this.activeTransfers.get(l)) == null) {
            Long valueOf = Long.valueOf(this.nextID.incrementAndGet());
            this.activeTransferIDs.put(str, valueOf);
            SLXfer sLXfer2 = new SLXfer(valueOf.longValue(), str, eLLPath, z);
            sLXfer2.addListener(sLXferCompletionListener, obj);
            this.activeTransfers.put(valueOf, sLXfer2);
            sLXfer2.StartTransfer(this);
            return;
        }
        sLXfer.addListener(sLXferCompletionListener, obj);
    }
}
