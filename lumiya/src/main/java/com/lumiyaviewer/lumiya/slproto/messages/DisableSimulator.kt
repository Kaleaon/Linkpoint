package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer

class DisableSimulator : SLMessage() {
    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int {
        return 4
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleDisableSimulator(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-104).toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        // No payload to unpack
    }
}
