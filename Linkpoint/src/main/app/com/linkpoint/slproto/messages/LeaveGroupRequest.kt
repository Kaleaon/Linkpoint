package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class LeaveGroupRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    GroupData GroupData_Field = GroupData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class GroupData {
        UUID GroupID
    }

    LeaveGroupRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 52
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleLeaveGroupRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 91)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
    }
}
