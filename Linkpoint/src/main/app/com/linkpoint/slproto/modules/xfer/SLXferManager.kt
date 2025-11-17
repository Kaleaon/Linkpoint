package com.linkpoint.slproto.modules.xfer

import com.linkpoint.slproto.SLAgentCircuit
import com.linkpoint.slproto.handler.SLMessageHandler
import com.linkpoint.slproto.messages.SendXferPacket
import com.linkpoint.slproto.modules.SLModule
import com.linkpoint.slproto.modules.xfer.SLXfer
import java.util.Collections
import java.util.HashMap
import java.util.Map
import java.util.concurrent.atomic.AtomicLong

class SLXferManager : SLModule {
    private Map<String, Long> activeTransferIDs = Collections.synchronizedMap(HashMap())
    private Map<Long, SLXfer> activeTransfers = Collections.synchronizedMap(HashMap())
    private AtomicLong nextID = AtomicLong(1)

    SLXferManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
    }

    @SLMessageHandler
    synchronized Unit HandleSendXferPacket(SendXferPacket sendXferPacket) {
        Long valueOf = Long.valueOf(sendXferPacket.XferID_Field.ID)
        SLXfer sLXfer = this.activeTransfers.get(valueOf)
        if (sLXfer != null) {
            sLXfer.HandleDataPacket(this, sendXferPacket)
            if (sLXfer.isCompleted()) {
                this.activeTransfers.remove(valueOf)
                this.activeTransferIDs.remove(sLXfer.getFilename())
                sLXfer.invokeListeners()
            }
        }
    }

    synchronized Unit RequestXfer(String str, ELLPath eLLPath, Boolean z, SLXfer.SLXferCompletionListener sLXferCompletionListener, Any obj) {
        SLXfer sLXfer
        Long l = this.activeTransferIDs.get(str)
        if (l == null || (sLXfer = this.activeTransfers.get(l)) == null) {
            Long valueOf = Long.valueOf(this.nextID.incrementAndGet())
            this.activeTransferIDs.put(str, valueOf)
            SLXfer sLXfer2 = SLXfer(valueOf.longValue(), str, eLLPath, z)
            sLXfer2.addListener(sLXferCompletionListener, obj)
            this.activeTransfers.put(valueOf, sLXfer2)
            sLXfer2.StartTransfer(this)
            return
        }
        sLXfer.addListener(sLXferCompletionListener, obj)
    }
}
