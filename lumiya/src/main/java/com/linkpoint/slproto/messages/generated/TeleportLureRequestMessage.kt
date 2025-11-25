package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class TeleportLureRequestMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var lureId: UUID = UUID(0L, 0L)
    var teleportFlags: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, lureId)
        packInt(buffer, teleportFlags)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        lureId = unpackUUID(buffer)
        teleportFlags = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0047.toInt()

    override fun getMessageName(): String = "TeleportLureRequest"
}
