package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class GroupRoleChanges : SLMessage {
    AgentData AgentData_Field
    ArrayList<RoleChange> RoleChange_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID SessionID
    }

    class RoleChange {
        Int Change
        UUID MemberID
        UUID RoleID
    }

    GroupRoleChanges() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.RoleChange_Fields.size() * 36) + 53
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupRoleChanges(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 86)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        byteBuffer.put((this as byte).RoleChange_Fields.size())
        for (RoleChange roleChange : this.RoleChange_Fields) {
            packUUID(byteBuffer, roleChange.RoleID)
            packUUID(byteBuffer, roleChange.MemberID)
            packInt(byteBuffer, roleChange.Change)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            RoleChange roleChange = RoleChange()
            roleChange.RoleID = unpackUUID(byteBuffer)
            roleChange.MemberID = unpackUUID(byteBuffer)
            roleChange.Change = unpackInt(byteBuffer)
            this.RoleChange_Fields.add(roleChange)
        }
    }
}
