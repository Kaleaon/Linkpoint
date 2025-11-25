package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ParcelGodForceOwnerMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var ownerId: UUID = UUID(0L, 0L)
    var localId: Int = 0


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, ownerId)
        packInt(buffer, localId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        ownerId = unpackUUID(buffer)
        localId = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF00D6.toInt()

    override fun getMessageName(): String = "ParcelGodForceOwner"
}
