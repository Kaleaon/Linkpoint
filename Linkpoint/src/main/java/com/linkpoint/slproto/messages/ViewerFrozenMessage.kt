package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class ViewerFrozenMessage : SLMessage() {
    val FrozenData_Field = FrozenData()

    class FrozenData {
        var Data: Boolean = false
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 5

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleViewerFrozenMessage(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-119).toByte())
        packBoolean(buffer, FrozenData_Field.Data)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        FrozenData_Field.Data = unpackBoolean(buffer)
    }
}