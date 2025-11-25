package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer

class DisableSimulator : SLMessage {
    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 0
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleDisableSimulator(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(6.toByte())
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
    }
}
