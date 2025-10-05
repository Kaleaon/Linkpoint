package com.linkpoint.slproto

import com.linkpoint.slproto.messages.SLMessageHandler
import java.nio.ByteBuffer

class SLDefaultMessage : SLMessage() {
    override fun CalcPayloadSize(): Int {
        return 0
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.DefaultMessageHandler(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {}

    override fun UnpackPayload(buffer: ByteBuffer) {}
}