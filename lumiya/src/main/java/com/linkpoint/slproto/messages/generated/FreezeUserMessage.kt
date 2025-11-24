package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class FreezeUserMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var targetId: UUID = UUID(0L, 0L)
    var flags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, targetId)
        packInt(buffer, flags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        targetId = unpackUUID(buffer)
        flags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00A8.toInt()

    override fun getMessageName(): String = "FreezeUser"
}
