package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class JoinGroupRequestExtendedMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupLimit: Int = 0
    var groupId: UUID = UUID(0L, 0L)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packInt(buffer, groupLimit)
        packUUID(buffer, groupId)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupLimit = unpackInt(buffer)
        groupId = unpackUUID(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF01AC.toInt()

    override fun getMessageName(): String = "JoinGroupRequestExtended"
}
