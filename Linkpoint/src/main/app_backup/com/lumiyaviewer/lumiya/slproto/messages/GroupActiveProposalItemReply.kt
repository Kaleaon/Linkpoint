package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupActiveProposalItemReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<ProposalData> ProposalData_Fields = ArrayList<>()
    TransactionData TransactionData_Field

    class AgentData {
        UUID AgentID
        UUID GroupID
    }

    class ProposalData {
        Boolean AlreadyVoted
        byte[] EndDateTime
        float Majority
        byte[] ProposalText
        Int Quorum
        byte[] StartDateTime
        byte[] TerseDateID
        byte[] VoteCast
        UUID VoteID
        UUID VoteInitiator
    }

    class TransactionData {
        Int TotalNumItems
        UUID TransactionID
    }

    GroupActiveProposalItemReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.TransactionData_Field = TransactionData()
    }

    Int CalcPayloadSize() {
        Int i = 57
        Iterator<T> it = this.ProposalData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            ProposalData proposalData = (ProposalData) it.next()
            i = proposalData.ProposalText.length + proposalData.TerseDateID.length + 33 + 1 + proposalData.StartDateTime.length + 1 + proposalData.EndDateTime.length + 1 + 1 + proposalData.VoteCast.length + 4 + 4 + 1 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupActiveProposalItemReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 104)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packInt(byteBuffer, this.TransactionData_Field.TotalNumItems)
        byteBuffer.put((byte) this.ProposalData_Fields.size())
        for (ProposalData proposalData : this.ProposalData_Fields) {
            packUUID(byteBuffer, proposalData.VoteID)
            packUUID(byteBuffer, proposalData.VoteInitiator)
            packVariable(byteBuffer, proposalData.TerseDateID, 1)
            packVariable(byteBuffer, proposalData.StartDateTime, 1)
            packVariable(byteBuffer, proposalData.EndDateTime, 1)
            packBoolean(byteBuffer, proposalData.AlreadyVoted)
            packVariable(byteBuffer, proposalData.VoteCast, 1)
            packFloat(byteBuffer, proposalData.Majority)
            packInt(byteBuffer, proposalData.Quorum)
            packVariable(byteBuffer, proposalData.ProposalText, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TotalNumItems = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            ProposalData proposalData = ProposalData()
            proposalData.VoteID = unpackUUID(byteBuffer)
            proposalData.VoteInitiator = unpackUUID(byteBuffer)
            proposalData.TerseDateID = unpackVariable(byteBuffer, 1)
            proposalData.StartDateTime = unpackVariable(byteBuffer, 1)
            proposalData.EndDateTime = unpackVariable(byteBuffer, 1)
            proposalData.AlreadyVoted = unpackBoolean(byteBuffer)
            proposalData.VoteCast = unpackVariable(byteBuffer, 1)
            proposalData.Majority = unpackFloat(byteBuffer)
            proposalData.Quorum = unpackInt(byteBuffer)
            proposalData.ProposalText = unpackVariable(byteBuffer, 1)
            this.ProposalData_Fields.add(proposalData)
        }
    }
}
