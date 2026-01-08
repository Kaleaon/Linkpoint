package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
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

    Int CalcPayloadSize() {
        return 37
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleLeaveGroupReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 92)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packBoolean(byteBuffer, this.GroupData_Field.Success)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.Success = unpackBoolean(byteBuffer)
    }
}
