package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class JoinGroupReply : SLMessage {
    AgentData AgentData_Field = AgentData()
    GroupData GroupData_Field = GroupData()

    class AgentData {
        UUID AgentID
    }

    class GroupData {
        UUID GroupID
        Boolean Success
    }

    JoinGroupReply() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 37
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleJoinGroupReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 88)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packBoolean(byteBuffer, this.GroupData_Field.Success)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.Success = unpackBoolean(byteBuffer)
    }
}
