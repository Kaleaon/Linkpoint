package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupProposalBallot : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ProposalData ProposalData_Field = ProposalData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class ProposalData {
        public UUID GroupID
        public UUID ProposalID
        public Byte[] VoteCast
    }

    public GroupProposalBallot() {
        this.zeroCoded = false
    }

    public Int CalcPayloadSize() {
        return this.ProposalData_Field.VoteCast.length + 33 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupProposalBallot(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 108)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ProposalData_Field.ProposalID)
        packUUID(byteBuffer, this.ProposalData_Field.GroupID)
        packVariable(byteBuffer, this.ProposalData_Field.VoteCast, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ProposalData_Field.ProposalID = unpackUUID(byteBuffer)
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer)
        this.ProposalData_Field.VoteCast = unpackVariable(byteBuffer, 1)
    }
}
