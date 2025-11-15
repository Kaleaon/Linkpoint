package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentThrottle : SLMessage {
    AgentData AgentData_Field = AgentData()
    Throttle Throttle_Field = Throttle()

    class AgentData {
        UUID AgentID
        Int CircuitCode
        UUID SessionID
    }

    class Throttle {
        Int GenCounter
        byte[] Throttles
    }

    AgentThrottle() {
        this.zeroCoded = true
    }

    Int CalcPayloadSize() {
        return this.Throttle_Field.Throttles.length + 5 + 40
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentThrottle(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 81)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.CircuitCode)
        packInt(byteBuffer, this.Throttle_Field.GenCounter)
        packVariable(byteBuffer, this.Throttle_Field.Throttles, 1)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer)
        this.Throttle_Field.GenCounter = unpackInt(byteBuffer)
        this.Throttle_Field.Throttles = unpackVariable(byteBuffer, 1)
    }
}
