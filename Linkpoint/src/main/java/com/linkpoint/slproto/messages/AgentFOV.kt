package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class AgentFOV : SLMessage() {
    val AgentData_Field = AgentData()
    val FOVBlock_Field = FOVBlock()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
        var CircuitCode: Int = 0
    }

    class FOVBlock {
        var GenCounter: Int = 0
        var VerticalAngle: Float = 0f
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 48

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleAgentFOV(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(82.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packInt(buffer, AgentData_Field.CircuitCode)
        packInt(buffer, FOVBlock_Field.GenCounter)
        packFloat(buffer, FOVBlock_Field.VerticalAngle)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        AgentData_Field.CircuitCode = unpackInt(buffer)
        FOVBlock_Field.GenCounter = unpackInt(buffer)
        FOVBlock_Field.VerticalAngle = unpackFloat(buffer)
    }
}