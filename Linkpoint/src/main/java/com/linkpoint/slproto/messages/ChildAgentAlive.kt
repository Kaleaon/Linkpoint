package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class ChildAgentAlive : SLMessage() {
    val AgentData_Field = AgentData()

    class AgentData {
        var RegionHandle: Long = 0
        var ViewerCircuitCode: Int = 0
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 45

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleChildAgentAlive(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.put(Ascii.SUB)
        packLong(buffer, AgentData_Field.RegionHandle)
        packInt(buffer, AgentData_Field.ViewerCircuitCode)
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.RegionHandle = unpackLong(buffer)
        AgentData_Field.ViewerCircuitCode = unpackInt(buffer)
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
    }
}