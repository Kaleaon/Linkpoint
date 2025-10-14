package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class StartGroupProposal : SLMessage {
    AgentData AgentData_Field = AgentData()
    ProposalData ProposalData_Field = ProposalData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ProposalData {
        Int Duration
        UUID GroupID
        Float Majority
        Byte[] ProposalText
        Int Quorum
    }

    StartGroupProposal() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.ProposalData_Field.ProposalText.length + 29 + 36
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleStartGroupProposal(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.ProposalData_Field.GroupID = unpackUUID(byteBuffer)
        this.ProposalData_Field.Quorum = unpackInt(byteBuffer)
        this.ProposalData_Field.Majority = unpackFloat(byteBuffer)
        this.ProposalData_Field.Duration = unpackInt(byteBuffer)
        this.ProposalData_Field.ProposalText = unpackVariable(byteBuffer, 1)
    }
}
