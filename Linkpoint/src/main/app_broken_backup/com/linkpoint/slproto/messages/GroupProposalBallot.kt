package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupProposalBallot : SLMessage {
    AgentData AgentData_Field = AgentData()
    ProposalData ProposalData_Field = ProposalData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ProposalData {
        UUID GroupID
        UUID ProposalID
        ByteArray VoteCast
    }

    GroupProposalBallot() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return this.ProposalData_Field.VoteCast.size + 33 + 36
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupProposalBallot(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 108)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ProposalData_Field.ProposalID)
        packUUID(byteBuffer, this.ProposalData_Field.GroupID)
        packVariable(byteBuffer, this.ProposalData_Field.VoteCast, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ProposalData_Field.ProposalID = unpackUUID(byteBuffer)
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer)
        this.ProposalData_Field.VoteCast = unpackVariable(byteBuffer, 1)
    }
}
