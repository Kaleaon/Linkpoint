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
        ByteArray Description
        Int Members
        ByteArray Name
        Long Powers
        UUID RoleID
        ByteArray Title
    }

    GroupRoleDataReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 57
        Iterator<T> it = this.RoleData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            RoleData roleData = (it as RoleData).next()
            i = roleData.Description.size + roleData.Name.size + 17 + 1 + roleData.Title.size + 1 + 8 + 4 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGroupRoleDataReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 116)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packUUID(byteBuffer, this.GroupData_Field.RequestID)
        packInt(byteBuffer, this.GroupData_Field.RoleCount)
        byteBuffer.put((this as byte).RoleData_Fields.size())
        for (RoleData roleData : this.RoleData_Fields) {
            packUUID(byteBuffer, roleData.RoleID)
            packVariable(byteBuffer, roleData.Name, 1)
            packVariable(byteBuffer, roleData.Title, 1)
            packVariable(byteBuffer, roleData.Description, 1)
            packLong(byteBuffer, roleData.Powers)
            packInt(byteBuffer, roleData.Members)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer)
        this.GroupData_Field.RoleCount = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
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
