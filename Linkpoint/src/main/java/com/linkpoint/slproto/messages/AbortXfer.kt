package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class AbortXfer : SLMessage() {
    val XferID_Field = XferID()

    class XferID {
        var ID: Long = 0
        var Result: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 16

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAbortXfer(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-99).toByte())
        packLong(buffer, XferID_Field.ID)
        packInt(buffer, XferID_Field.Result)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        XferID_Field.ID = unpackLong(buffer)
        XferID_Field.Result = unpackInt(buffer)
    }
}