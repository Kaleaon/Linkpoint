package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GodKickUserMessage : SLMessage() {
    var godId: UUID = UUID(0L, 0L)
    var godSessionId: UUID = UUID(0L, 0L)
    var agentId: UUID = UUID(0L, 0L)
    var kickFlags: Int = 0
    var reason: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, godId)
        packUUID(buffer, godSessionId)
        packUUID(buffer, agentId)
        packInt(buffer, kickFlags)
        packVariable(buffer, reason, 2)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        godId = unpackUUID(buffer)
        godSessionId = unpackUUID(buffer)
        agentId = unpackUUID(buffer)
        kickFlags = unpackInt(buffer)
        reason = unpackVariable(buffer, 2)
    }

    override fun getMessageID(): Int = 0xFFFF00A5.toInt()

    override fun getMessageName(): String = "GodKickUser"
}
