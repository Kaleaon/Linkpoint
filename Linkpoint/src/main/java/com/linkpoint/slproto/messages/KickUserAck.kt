package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class KickUserAck : SLMessage() {
    val UserInfo_Field = UserInfo()

    class UserInfo {
        var SessionID: UUID? = null
        var Flags: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 24

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleKickUserAck(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-92).toByte())
        packUUID(buffer, UserInfo_Field.SessionID)
        packInt(buffer, UserInfo_Field.Flags)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        UserInfo_Field.SessionID = unpackUUID(buffer)
        UserInfo_Field.Flags = unpackInt(buffer)
    }
}