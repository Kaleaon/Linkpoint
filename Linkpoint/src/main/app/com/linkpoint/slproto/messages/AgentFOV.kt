package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentFOV : SLMessage {
    AgentData AgentData_Field = AgentData()
    FOVBlock FOVBlock_Field = FOVBlock()

    class AgentData {
        UUID AgentID
        Int CircuitCode
        UUID SessionID
    }

    class FOVBlock {
        Int GenCounter
        float VerticalAngle
    }

    AgentFOV() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 48
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentFOV(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 82)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.CircuitCode)
        packInt(byteBuffer, this.FOVBlock_Field.GenCounter)
        packFloat(byteBuffer, this.FOVBlock_Field.VerticalAngle)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer)
        this.FOVBlock_Field.GenCounter = unpackInt(byteBuffer)
        this.FOVBlock_Field.VerticalAngle = unpackFloat(byteBuffer)
    }
}
