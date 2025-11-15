package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentQuitCopy : SLMessage {
    AgentData AgentData_Field = AgentData()
    FuseBlock FuseBlock_Field = FuseBlock()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class FuseBlock {
        Int ViewerCircuitCode
    }

    AgentQuitCopy() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 40
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentQuitCopy(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 85)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.FuseBlock_Field.ViewerCircuitCode)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.FuseBlock_Field.ViewerCircuitCode = unpackInt(byteBuffer)
    }
}
