package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupRoleDataReply : SLMessage {
    AgentData AgentData_Field
    GroupData GroupData_Field
    ArrayList<RoleData> RoleData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class GroupData {
        UUID GroupID
        UUID RequestID
        Int RoleCount
    }

    class RoleData {
        byte[] Description
        Int Members
        byte[] Name
        Long Powers
        UUID RoleID
        byte[] Title
    }

    GroupRoleDataReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    Int CalcPayloadSize() {
        Int i = 57
        Iterator<T> it = this.RoleData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            RoleData roleData = (RoleData) it.next()
            i = roleData.Description.length + roleData.Name.length + 17 + 1 + roleData.Title.length + 1 + 8 + 4 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleDataReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 116)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packUUID(byteBuffer, this.GroupData_Field.RequestID)
        packInt(byteBuffer, this.GroupData_Field.RoleCount)
        byteBuffer.put((byte) this.RoleData_Fields.size())
        for (RoleData roleData : this.RoleData_Fields) {
            packUUID(byteBuffer, roleData.RoleID)
            packVariable(byteBuffer, roleData.Name, 1)
            packVariable(byteBuffer, roleData.Title, 1)
            packVariable(byteBuffer, roleData.Description, 1)
            packLong(byteBuffer, roleData.Powers)
            packInt(byteBuffer, roleData.Members)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer)
        this.GroupData_Field.RoleCount = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RoleData roleData = RoleData()
            roleData.RoleID = unpackUUID(byteBuffer)
            roleData.Name = unpackVariable(byteBuffer, 1)
            roleData.Title = unpackVariable(byteBuffer, 1)
            roleData.Description = unpackVariable(byteBuffer, 1)
            roleData.Powers = unpackLong(byteBuffer)
            roleData.Members = unpackInt(byteBuffer)
            this.RoleData_Fields.add(roleData)
        }
    }
}
