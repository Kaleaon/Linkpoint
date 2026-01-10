package com.linkpoint.slproto.messages

import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.UUID

class GroupRoleMembersRequest : SLMessage {
    AgentData AgentData_Field = AgentData()
    GroupData GroupData_Field = GroupData()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class GroupData {
        UUID GroupID
        UUID RequestID
    }

    GroupRoleMembersRequest() {
        this.zeroCoded = false
    }

    fun CalcPayloadSize(): Int {
        return 68
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupRoleMembersRequest(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 117)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packUUID(byteBuffer, this.GroupData_Field.RequestID)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer)
    }
}
