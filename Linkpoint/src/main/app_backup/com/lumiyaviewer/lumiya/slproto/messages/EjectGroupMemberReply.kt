package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class EjectGroupMemberReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var GroupData_Field: GroupData = GroupData()
    var EjectData_Field: EjectData = EjectData()

    class AgentData {
        var AgentID: UUID = UUID(0, 0)
    }

    class GroupData {
        var GroupID: UUID = UUID(0, 0)
    }

    class EjectData {
        var Success: Boolean = false
    }

    init {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        return 33
    }

    override fun handleMessage(sLMessageHandler: SLMessageHandler) {
        // sLMessageHandler.HandleEjectGroupMemberReply(this)
    }

    override fun PackPayload(buffer: ByteBuffer) {
        buffer.putShort(-1)
        buffer.put(0.toByte())
        buffer.put(72.toByte())
        packUUID(buffer, this.AgentData_Field.AgentID)
        packUUID(buffer, this.GroupData_Field.GroupID)
        packBoolean(buffer, this.EjectData_Field.Success)
    }

    override fun UnpackPayload(buffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(buffer)
        this.GroupData_Field.GroupID = unpackUUID(buffer)
        this.EjectData_Field.Success = unpackBoolean(buffer)
    }
}
