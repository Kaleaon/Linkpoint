package com.linkpoint.slproto

import com.linkpoint.slproto.messages.SLMessageHandler
import java.nio.ByteBuffer

class SLDefaultMessage : SLMessage() {
    override fun CalcPayloadSize(): Int = 0

    override fun handleMessage(handler: SLMessageHandler) {
        handler.DefaultMessageHandler(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        // No payload to pack
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        // No payload to unpack
    }
}
