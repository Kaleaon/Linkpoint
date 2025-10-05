package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class UnsubscribeLoad : SLMessage() {
    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 4

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleUnsubscribeLoad(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(8.toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {}
}