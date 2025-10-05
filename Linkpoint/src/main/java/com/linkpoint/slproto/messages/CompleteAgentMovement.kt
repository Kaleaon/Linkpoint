package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class CompleteAgentMovement : SLMessage() {
    val AgentData_Field = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
        var CircuitCode: Int = 0
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 40

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleCompleteAgentMovement(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put((-7).toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, AgentData_Field.CircuitCode)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        AgentData_Field.CircuitCode = unpackInt(buffer)
    }
}