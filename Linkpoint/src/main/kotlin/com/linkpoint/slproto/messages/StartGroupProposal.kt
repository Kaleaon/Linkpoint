package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartGroupProposal : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ProposalData ProposalData_Field = ProposalData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ProposalData {
        public Int Duration
        public UUID GroupID
        public Float Majority
        public ByteArray ProposalText
        public Int Quorum
    }

    public StartGroupProposal() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        return this.ProposalData_Field.ProposalText.length + 29 + 36
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleStartGroupProposal(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 107)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ProposalData_Field.GroupID)
        packInt(byteBuffer, this.ProposalData_Field.Quorum)
        packFloat(byteBuffer, this.ProposalData_Field.Majority)
        packInt(byteBuffer, this.ProposalData_Field.Duration)
        packVariable(byteBuffer, this.ProposalData_Field.ProposalText, 1)
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer)
        this.ProposalData_Field.Quorum = unpackInt(byteBuffer)
        this.ProposalData_Field.Majority = unpackFloat(byteBuffer)
        this.ProposalData_Field.Duration = unpackInt(byteBuffer)
        this.ProposalData_Field.ProposalText = unpackVariable(byteBuffer, 1)
    }
}
