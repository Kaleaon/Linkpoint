package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupVoteHistoryItemReply : SLMessage {
    AgentData AgentData_Field
    HistoryItemData HistoryItemData_Field
    TransactionData TransactionData_Field
    ArrayList<VoteItem> VoteItem_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID GroupID
    }

    class HistoryItemData {
        ByteArray EndDateTime
        float Majority
        ByteArray ProposalText
        Int Quorum
        ByteArray StartDateTime
        ByteArray TerseDateID
        UUID VoteID
        UUID VoteInitiator
        ByteArray VoteResult
        ByteArray VoteType
    }

    class TransactionData {
        Int TotalNumItems
        UUID TransactionID
    }

    class VoteItem {
        UUID CandidateID
        Int NumVotes
        ByteArray VoteCast
    }

    GroupVoteHistoryItemReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.TransactionData_Field = TransactionData()
        this.HistoryItemData_Field = HistoryItemData()
    }

    fun CalcPayloadSize(): Int {
        Int length = this.HistoryItemData_Field.TerseDateID.size + 17 + 1 + this.HistoryItemData_Field.StartDateTime.size + 1 + this.HistoryItemData_Field.EndDateTime.size + 16 + 1 + this.HistoryItemData_Field.VoteType.size + 1 + this.HistoryItemData_Field.VoteResult.size + 4 + 4 + 2 + this.HistoryItemData_Field.ProposalText.size + 56 + 1
        Iterator<T> it = this.VoteItem_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i
            }
            length = ((it as VoteItem).next()).VoteCast.size + 17 + 4 + i
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGroupVoteHistoryItemReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 106)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packInt(byteBuffer, this.TransactionData_Field.TotalNumItems)
        packUUID(byteBuffer, this.HistoryItemData_Field.VoteID)
        packVariable(byteBuffer, this.HistoryItemData_Field.TerseDateID, 1)
        packVariable(byteBuffer, this.HistoryItemData_Field.StartDateTime, 1)
        packVariable(byteBuffer, this.HistoryItemData_Field.EndDateTime, 1)
        packUUID(byteBuffer, this.HistoryItemData_Field.VoteInitiator)
        packVariable(byteBuffer, this.HistoryItemData_Field.VoteType, 1)
        packVariable(byteBuffer, this.HistoryItemData_Field.VoteResult, 1)
        packFloat(byteBuffer, this.HistoryItemData_Field.Majority)
        packInt(byteBuffer, this.HistoryItemData_Field.Quorum)
        packVariable(byteBuffer, this.HistoryItemData_Field.ProposalText, 2)
        byteBuffer.put((this as byte).VoteItem_Fields.size())
        for (VoteItem voteItem : this.VoteItem_Fields) {
            packUUID(byteBuffer, voteItem.CandidateID)
            packVariable(byteBuffer, voteItem.VoteCast, 1)
            packInt(byteBuffer, voteItem.NumVotes)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TotalNumItems = unpackInt(byteBuffer)
        this.HistoryItemData_Field.VoteID = unpackUUID(byteBuffer)
        this.HistoryItemData_Field.TerseDateID = unpackVariable(byteBuffer, 1)
        this.HistoryItemData_Field.StartDateTime = unpackVariable(byteBuffer, 1)
        this.HistoryItemData_Field.EndDateTime = unpackVariable(byteBuffer, 1)
        this.HistoryItemData_Field.VoteInitiator = unpackUUID(byteBuffer)
        this.HistoryItemData_Field.VoteType = unpackVariable(byteBuffer, 1)
        this.HistoryItemData_Field.VoteResult = unpackVariable(byteBuffer, 1)
        this.HistoryItemData_Field.Majority = unpackFloat(byteBuffer)
        this.HistoryItemData_Field.Quorum = unpackInt(byteBuffer)
        this.HistoryItemData_Field.ProposalText = unpackVariable(byteBuffer, 2)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            VoteItem voteItem = VoteItem()
            voteItem.CandidateID = unpackUUID(byteBuffer)
            voteItem.VoteCast = unpackVariable(byteBuffer, 1)
            voteItem.NumVotes = unpackInt(byteBuffer)
            this.VoteItem_Fields.add(voteItem)
        }
    }
}
