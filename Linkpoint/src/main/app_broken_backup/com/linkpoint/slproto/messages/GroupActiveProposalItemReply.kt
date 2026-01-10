package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
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
        ByteArray EndDateTime
        float Majority
        ByteArray ProposalText
        Int Quorum
        ByteArray StartDateTime
        ByteArray TerseDateID
        ByteArray VoteCast
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

    fun CalcPayloadSize(): Int {
        var i: Int = 57
        Iterator<T> it = this.ProposalData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            ProposalData proposalData = (it as ProposalData).next()
            i = proposalData.ProposalText.size + proposalData.TerseDateID.size + 33 + 1 + proposalData.StartDateTime.size + 1 + proposalData.EndDateTime.size + 1 + 1 + proposalData.VoteCast.size + 4 + 4 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupActiveProposalItemReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 104)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packInt(byteBuffer, this.TransactionData_Field.TotalNumItems)
        byteBuffer.put((this as byte).ProposalData_Fields.size())
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

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TotalNumItems = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
