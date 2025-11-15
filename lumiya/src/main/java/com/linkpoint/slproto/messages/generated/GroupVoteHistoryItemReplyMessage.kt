package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupVoteHistoryItemReplyMessage : SLMessage() {
    var agentId: UUID = UUID(0L, 0L)
    var groupId: UUID = UUID(0L, 0L)
    var transactionId: UUID = UUID(0L, 0L)
    var totalNumItems: Int = 0
    var voteId: UUID = UUID(0L, 0L)
    var terseDateId: ByteArray = ByteArray(0)
    var startDateTime: ByteArray = ByteArray(0)
    var endDateTime: ByteArray = ByteArray(0)
    var voteInitiator: UUID = UUID(0L, 0L)
    var voteType: ByteArray = ByteArray(0)
    var voteResult: ByteArray = ByteArray(0)
    var majority: Float = 0f
    var quorum: Int = 0
    var proposalText: ByteArray = ByteArray(0)
    val voteItem: MutableList<VoteItemBlock> = mutableListOf()

    data class VoteItemBlock(
        var candidateId: UUID = UUID(0L, 0L),
        var voteCast: ByteArray = ByteArray(0),
        var numVotes: Int = 0
    )


    init {
        zeroCoded = true
    }

    override fun packPayload(buffer: ByteBuffer) {
        packUUID(buffer, agentId)
        packUUID(buffer, groupId)
        packUUID(buffer, transactionId)
        packInt(buffer, totalNumItems)
        packUUID(buffer, voteId)
        packVariable(buffer, terseDateId, 1)
        packVariable(buffer, startDateTime, 1)
        packVariable(buffer, endDateTime, 1)
        packUUID(buffer, voteInitiator)
        packVariable(buffer, voteType, 1)
        packVariable(buffer, voteResult, 1)
        packFloat(buffer, majority)
        packInt(buffer, quorum)
        packVariable(buffer, proposalText, 2)
        require(voteItem.size <= 0xFF) { "VoteItem size exceeds 255 (" + voteItem.size + ")" }
        packByte(buffer, voteItem.size)
        voteItem.forEach { entry ->
            packUUID(buffer, entry.candidateId)
            packVariable(buffer, entry.voteCast, 1)
            packInt(buffer, entry.numVotes)
        }
    }

    override fun unpackPayload(buffer: ByteBuffer) {
        agentId = unpackUUID(buffer)
        groupId = unpackUUID(buffer)
        transactionId = unpackUUID(buffer)
        totalNumItems = unpackInt(buffer)
        voteId = unpackUUID(buffer)
        terseDateId = unpackVariable(buffer, 1)
        startDateTime = unpackVariable(buffer, 1)
        endDateTime = unpackVariable(buffer, 1)
        voteInitiator = unpackUUID(buffer)
        voteType = unpackVariable(buffer, 1)
        voteResult = unpackVariable(buffer, 1)
        majority = unpackFloat(buffer)
        quorum = unpackInt(buffer)
        proposalText = unpackVariable(buffer, 2)
        run {
            val count = unpackByte(buffer)
            voteItem.clear()
            repeat(count) {
                val entry = VoteItemBlock()
                entry.candidateId = unpackUUID(buffer)
                entry.voteCast = unpackVariable(buffer, 1)
                entry.numVotes = unpackInt(buffer)
                voteItem += entry
            }
        }
    }

    override fun getMessageID(): Int = 0xFFFF016A

    override fun getMessageName(): String = "GroupVoteHistoryItemReply"
}
