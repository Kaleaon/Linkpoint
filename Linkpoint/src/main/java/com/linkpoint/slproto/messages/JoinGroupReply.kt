package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class JoinGroupReply : SLMessage() {
    val AgentData_Field = AgentData()
    val GroupData_Field = GroupData()

    class AgentData {
        var AgentID: UUID? = null
    }

    class GroupData {
        var GroupID: UUID? = null
        var Success: Boolean = false
    }

    init {
        zeroCoded = false
    }

    override fun CalcPayloadSize(): Int = 37

    override fun Handle(handler: SLMessageHandler) {
        handler.HandleJoinGroupReply(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(1.toByte())
        buffer.put(88.toByte())
        packUUID(buffer, AgentData_Field.AgentID)
        packUUID(buffer, GroupData_Field.GroupID)
        packBoolean(buffer, GroupData_Field.Success)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(buffer)
        GroupData_Field.GroupID = unpackUUID(buffer)
        GroupData_Field.Success = unpackBoolean(buffer)
    }
}