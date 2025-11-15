package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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
        byte[] VoteCast
    }

    GroupProposalBallot() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return this.ProposalData_Field.VoteCast.length + 33 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupProposalBallot(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 108)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.ProposalData_Field.ProposalID)
        packUUID(byteBuffer, this.ProposalData_Field.GroupID)
        packVariable(byteBuffer, this.ProposalData_Field.VoteCast, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ProposalData_Field.ProposalID = unpackUUID(byteBuffer)
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer)
        this.ProposalData_Field.VoteCast = unpackVariable(byteBuffer, 1)
    }
}
