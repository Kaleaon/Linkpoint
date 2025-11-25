package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartGroupProposalMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var sessionId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var quorum: Int = 0
    var majority: Float = 0f
    var duration: Int = 0
    var proposalText: ByteArray = ByteArray(0)


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, sessionId)
        packUUID(buffer, groupId)
        packInt(buffer, quorum)
        packFloat(buffer, majority)
        packInt(buffer, duration)
        packVariable(buffer, proposalText, 1)
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        sessionId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        quorum = unpackInt(buffer)
        majority = unpackFloat(buffer)
        duration = unpackInt(buffer)
        proposalText = unpackVariable(buffer, 1)
    }

    override fun getMessageID(): Int = 0xFFFF016B.toInt()

    override fun getMessageName(): String = "StartGroupProposal"
}
