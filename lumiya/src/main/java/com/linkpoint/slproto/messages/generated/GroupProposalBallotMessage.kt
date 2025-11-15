package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupProposalBallotMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var proposalId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var voteCast: ByteArray = ByteArray(0)


    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, proposalId)
        packUUID(buffer, groupId)
        packVariable(buffer, voteCast, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        proposalId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        voteCast = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF016C

    override fun getMessageName(): String = "GroupProposalBallot"
}
