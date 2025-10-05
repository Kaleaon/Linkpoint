package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupActiveProposalItemReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<ProposalData> ProposalData_Fields = ArrayList<>()
    public TransactionData TransactionData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
    }

    @JvmStatic
    class ProposalData {
        public Boolean AlreadyVoted
        public Byte[] EndDateTime
        public Float Majority
        public Byte[] ProposalText
        public Int Quorum
        public Byte[] StartDateTime
        public Byte[] TerseDateID
        public Byte[] VoteCast
        public UUID VoteID
        public UUID VoteInitiator
    }

    @JvmStatic
    class TransactionData {
        public Int TotalNumItems
        public UUID TransactionID
    }

    public GroupActiveProposalItemReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.TransactionData_Field = TransactionData()
    }

    public Int CalcPayloadSize() {
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

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupActiveProposalItemReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 104)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.TransactionData_Field.TransactionID)
        packInt(byteBuffer, this.TransactionData_Field.TotalNumItems)
        byteBuffer.put((Byte) this.ProposalData_Fields.size())
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

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TransactionID = unpackUUID(byteBuffer)
        this.TransactionData_Field.TotalNumItems = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
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
