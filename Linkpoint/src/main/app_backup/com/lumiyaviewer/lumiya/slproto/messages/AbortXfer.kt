package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class AbortXfer : SLMessage {
    val XferID_Field = XferID()

    class XferID {
        var ID: Long = 0
        var Result: Int = 0
    }

    constructor() {
        this.zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 16
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAbortXfer(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-99).toByte())
        packLong(buffer, this.XferID_Field.ID)
        packInt(buffer, this.XferID_Field.Result)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.XferID_Field.ID = unpackLong(buffer)
        this.XferID_Field.Result = unpackInt(buffer)
    }
}
