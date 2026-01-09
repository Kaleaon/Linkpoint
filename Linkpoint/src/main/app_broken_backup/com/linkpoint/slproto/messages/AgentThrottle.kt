package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
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
        ByteArray Throttles
    }

    AgentThrottle() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return this.Throttle_Field.Throttles.size + 5 + 40
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleAgentThrottle(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) 81)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.CircuitCode)
        packInt(byteBuffer, this.Throttle_Field.GenCounter)
        packVariable(byteBuffer, this.Throttle_Field.Throttles, 1)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer)
        this.Throttle_Field.GenCounter = unpackInt(byteBuffer)
        this.Throttle_Field.Throttles = unpackVariable(byteBuffer, 1)
    }
}
