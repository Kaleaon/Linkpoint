package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LeaveGroupReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    GroupData GroupData_Field = GroupData()

    class AgentData {
        UUID AgentID
    }

    class GroupData {
        UUID GroupID
        Boolean Success
    }

    LeaveGroupReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 37
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleLeaveGroupReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 92)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packBoolean(byteBuffer, this.GroupData_Field.Success)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.Success = unpackBoolean(byteBuffer)
    }
}
