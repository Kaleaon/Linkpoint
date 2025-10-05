package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LeaveGroupRequest : SLMessage() {
    val AgentData_Field = AgentData()
    val GroupData_Field = GroupData()

    class AgentData {
        var AgentID: UUID? = null
        var SessionID: UUID? = null
    }

    class GroupData {
        var GroupID: UUID? = null
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 52

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleLeaveGroupRequest(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(91.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, AgentData_Field.SessionID)
        packUUID(buffer, GroupData_Field.GroupID)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        AgentData_Field.SessionID = unpackUUID(buffer)
        GroupData_Field.GroupID = unpackUUID(buffer)
    }
}