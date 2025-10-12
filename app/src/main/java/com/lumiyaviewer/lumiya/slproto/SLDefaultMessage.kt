package com.lumiyaviewer.lumiya.slproto

import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler
import java.nio.ByteBuffer

class SLDefaultMessage : SLMessage() {
    
    override fun CalcPayloadSize(): Int {
        return 0
    }

    override fun Handle(handler: SLMessageHandler) {
        handler.DefaultMessageHandler(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        // No payload to pack
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        // No payload to unpack
    }
}