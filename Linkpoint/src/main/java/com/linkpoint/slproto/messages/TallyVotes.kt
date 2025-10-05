package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TallyVotes : SLMessage() {
    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 4

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleTallyVotes(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(109.toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {}
}