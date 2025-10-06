package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class GroupRoleChanges : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<RoleChange> RoleChange_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID SessionID
    }

    @JvmStatic
    class RoleChange {
        public Int Change
        public UUID MemberID
        public UUID RoleID
    }

    public GroupRoleChanges() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        return (this.RoleChange_Fields.size() * 36) + 53
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleChanges(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 86)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        byteBuffer.put((Byte) this.RoleChange_Fields.size())
        for (RoleChange roleChange : this.RoleChange_Fields) {
            packUUID(byteBuffer, roleChange.RoleID)
            packUUID(byteBuffer, roleChange.MemberID)
            packInt(byteBuffer, roleChange.Change)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RoleChange roleChange = RoleChange()
            roleChange.RoleID = unpackUUID(byteBuffer)
            roleChange.MemberID = unpackUUID(byteBuffer)
            roleChange.Change = unpackInt(byteBuffer)
            this.RoleChange_Fields.add(roleChange)
        }
    }
}
