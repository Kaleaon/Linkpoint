package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class KickUserAckMessage : SLMessage() {
    var sessionId: UUID = UUID(0L, 0L)
    var flags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, sessionId)
        packInt(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        sessionId = unpackUUID(buffer)
        flags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00A4.toInt()

    override fun getMessageName(): String = "KickUserAck"
}
