package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CompleteAgentMovement : SLMessage {
    AgentData AgentData_Field = AgentData()

    class AgentData {
        UUID AgentID
        Int CircuitCode
        UUID SessionID
    }

    CompleteAgentMovement() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 40
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleCompleteAgentMovement(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -7)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packInt(byteBuffer, this.AgentData_Field.CircuitCode)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.CircuitCode = unpackInt(byteBuffer)
    }
}
