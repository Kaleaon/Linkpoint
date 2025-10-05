package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupTitlesRequest : SLMessage() {
    val AgentData_Field = AgentData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
        var GroupID: UUID? = null
        var RequestID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 68

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleGroupTitlesRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(119.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, AgentData_Field.GroupID)
        packUUID(buffer, AgentData_Field.RequestID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        AgentData_Field.GroupID = unpackUUID(buffer)
        AgentData_Field.RequestID = unpackUUID(buffer)
    }
}