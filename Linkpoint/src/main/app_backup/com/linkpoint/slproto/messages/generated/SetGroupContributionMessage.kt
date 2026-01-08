package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class SetGroupContributionMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var contribution: Int = 0


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packInt(buffer, contribution)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        contribution = unpackInt(buffer)
    }

    override fun getMessageID(): Int = 0xFFFF0171.toInt()

    override fun getMessageName(): String = "SetGroupContribution"
}
