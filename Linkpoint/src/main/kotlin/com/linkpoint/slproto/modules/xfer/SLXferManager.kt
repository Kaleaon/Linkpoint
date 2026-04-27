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

class SLXferManager : SLModule() {
    private Map<String, Long> activeTransferIDs = Collections.synchronizedMap(HashMap())
    private Map<Long, SLXfer> activeTransfers = Collections.synchronizedMap(HashMap())
    private AtomicLong nextID = AtomicLong(1)

    public SLXferManager(SLAgentCircuit sLAgentCircuit) {
        super(sLAgentCircuit)
    }

    @SLMessageHandler
    public synchronized Unit HandleSendXferPacket(SendXferPacket sendXferPacket) {
        val valueOf: Long = Long.valueOf(sendXferPacket.XferID_Field.ID)
        val sLXfer: SLXfer = this.activeTransfers.get(valueOf)
        if (sLXfer != null) {
            sLXfer.HandleDataPacket(this, sendXferPacket)
            if (sLXfer.isCompleted()) {
                this.activeTransfers.remove(valueOf)
                this.activeTransferIDs.remove(sLXfer.getFilename())
                sLXfer.invokeListeners()
            }
        }
    }

    public synchronized Unit RequestXfer(String str, ELLPath eLLPath, Boolean z, SLXfer.SLXferCompletionListener sLXferCompletionListener, Object obj) {
        SLXfer sLXfer
        val l: Long = this.activeTransferIDs.get(str)
        if (l == null || (sLXfer = this.activeTransfers.get(l)) == null) {
            val valueOf: Long = Long.valueOf(this.nextID.incrementAndGet())
            this.activeTransferIDs.put(str, valueOf)
            val sLXfer2: SLXfer = SLXfer(valueOf.longValue(), str, eLLPath, z)
            sLXfer2.addListener(sLXferCompletionListener, obj)
            this.activeTransfers.put(valueOf, sLXfer2)
            sLXfer2.StartTransfer(this)
            return
        }
        sLXfer.addListener(sLXferCompletionListener, obj)
    }
}
