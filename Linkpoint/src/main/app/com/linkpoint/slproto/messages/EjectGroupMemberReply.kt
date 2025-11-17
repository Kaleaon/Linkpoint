package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class EjectGroupMemberReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    EjectData EjectData_Field = EjectData()
    GroupData GroupData_Field = GroupData()

    class AgentData {
        UUID AgentID
    }

    class EjectData {
        Boolean Success
    }

    class GroupData {
        UUID GroupID
    }

    EjectGroupMemberReply() {
        this.zeroCoded = false
    }

    Int CalcPayloadSize() {
        return 37
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleEjectGroupMemberReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 90)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packBoolean(byteBuffer, this.EjectData_Field.Success)
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.EjectData_Field.Success = unpackBoolean(byteBuffer)
    }
}
