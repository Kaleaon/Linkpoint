package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupActiveProposalItemReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var totalNumItems: Int = 0
    val proposalData: MutableList<ProposalDataBlock> = mutableListOf()

    data class ProposalDataBlock(
        var voteId: UUID = UUID(0L, 0L),
        var voteInitiator: UUID = UUID(0L, 0L),
        var terseDateId: ByteArray = ByteArray(0),
        var startDateTime: ByteArray = ByteArray(0),
        var endDateTime: ByteArray = ByteArray(0),
        var alreadyVoted: Boolean = false,
        var voteCast: ByteArray = ByteArray(0),
        var majority: Float = 0f,
        var quorum: Int = 0,
        var proposalText: ByteArray = ByteArray(0)
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packUUID(buffer, transactionId)
        packInt(buffer, totalNumItems)
        require(proposalData.size <= 0xFF) { "ProposalData size exceeds 255 (" + proposalData.size + ")" }
        packByte(buffer, proposalData.size)
        proposalData.forEach { entry ->
            packUUID(buffer, entry.voteId)
            packUUID(buffer, entry.voteInitiator)
            packVariable(buffer, entry.terseDateId, 1)
            packVariable(buffer, entry.startDateTime, 1)
            packVariable(buffer, entry.endDateTime, 1)
            packBoolean(buffer, entry.alreadyVoted)
            packVariable(buffer, entry.voteCast, 1)
            packFloat(buffer, entry.majority)
            packInt(buffer, entry.quorum)
            packVariable(buffer, entry.proposalText, 1)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        totalNumItems = unpackInt(buffer)
        run {
            val count = unpackByte(buffer)
            proposalData.clear()
            repeat(count) {
                val entry = ProposalDataBlock()
                entry.voteId = unpackUUID(buffer)
                entry.voteInitiator = unpackUUID(buffer)
                entry.terseDateId = unpackVariable(buffer, 1)
                entry.startDateTime = unpackVariable(buffer, 1)
                entry.endDateTime = unpackVariable(buffer, 1)
                entry.alreadyVoted = unpackBoolean(buffer)
                entry.voteCast = unpackVariable(buffer, 1)
                entry.majority = unpackFloat(buffer)
                entry.quorum = unpackInt(buffer)
                entry.proposalText = unpackVariable(buffer, 1)
                proposalData += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF0168

    override fun getMessageName(): String = "GroupActiveProposalItemReply"
}
