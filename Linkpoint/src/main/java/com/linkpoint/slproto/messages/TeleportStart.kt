package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer

class TeleportStart : SLMessage() {
    val Info_Field = Info()

    class Info {
        var TeleportFlags: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 8

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleTeleportStart(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(73.toByte())
        packInt(buffer, Info_Field.TeleportFlags)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        Info_Field.TeleportFlags = unpackInt(buffer)
    }
}