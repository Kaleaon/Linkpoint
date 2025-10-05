package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportCancel : SLMessage() {
    val Info_Field = Info()

    class Info {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 36

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleTeleportCancel(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(72.toByte())
        packUUID(buffer, Info_Field.AgentID)
        packUUID(buffer, Info_Field.SessionID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        Info_Field.AgentID = unpackUUID(buffer)
        Info_Field.SessionID = unpackUUID(buffer)
    }
}