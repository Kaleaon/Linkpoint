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
        byte[] EndDateTime
        float Majority
        byte[] ProposalText
        Int Quorum
        byte[] StartDateTime
        byte[] TerseDateID
        UUID VoteID
        UUID VoteInitiator
        byte[] VoteResult
        byte[] VoteType
    }

    class TransactionData {
        Int TotalNumItems
        UUID TransactionID
    }

    class VoteItem {
        UUID CandidateID
        Int NumVotes
        byte[] VoteCast
    }

    GroupVoteHistoryItemReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.TransactionData_Field = TransactionData()
        this.HistoryItemData_Field = HistoryItemData()
    }

    Int CalcPayloadSize() {
        Int length = this.HistoryItemData_Field.TerseDateID.length + 17 + 1 + this.HistoryItemData_Field.StartDateTime.length + 1 + this.HistoryItemData_Field.EndDateTime.length + 16 + 1 + this.HistoryItemData_Field.VoteType.length + 1 + this.HistoryItemData_Field.VoteResult.length + 4 + 4 + 2 + this.HistoryItemData_Field.ProposalText.length + 56 + 1
        Iterator<T> it = this.VoteItem_Fields.iterator()
        while (true) {
            Int i = length
            if (!it.hasNext()) {
                return i
            }
            length = ((VoteItem) it.next()).VoteCast.length + 17 + 4 + i
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupVoteHistoryItemReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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
        byteBuffer.put((byte) this.VoteItem_Fields.size())
        for (VoteItem voteItem : this.VoteItem_Fields) {
            packUUID(byteBuffer, voteItem.CandidateID)
            packVariable(byteBuffer, voteItem.VoteCast, 1)
            packInt(byteBuffer, voteItem.NumVotes)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
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
        for (Int i = 0; i < b; i++) {
            VoteItem voteItem = VoteItem()
            voteItem.CandidateID = unpackUUID(byteBuffer)
            voteItem.VoteCast = unpackVariable(byteBuffer, 1)
            voteItem.NumVotes = unpackInt(byteBuffer)
            this.VoteItem_Fields.add(voteItem)
        }
    }
}
