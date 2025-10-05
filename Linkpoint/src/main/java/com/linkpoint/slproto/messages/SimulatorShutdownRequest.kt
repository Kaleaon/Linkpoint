package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class SimulatorShutdownRequest : SLMessage() {
    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleSimulatorShutdownRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(Ascii.CR)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {}
}