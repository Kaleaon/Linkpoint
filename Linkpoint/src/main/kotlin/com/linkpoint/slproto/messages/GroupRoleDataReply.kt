package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupRoleDataReply : SLMessage() {
    public AgentData AgentData_Field
    public GroupData GroupData_Field
    public ArrayList<RoleData> RoleData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class GroupData {
        public UUID GroupID
        public UUID RequestID
        public Int RoleCount
    }

    @JvmStatic
    class RoleData {
        public ByteArray Description
        public Int Members
        public ByteArray Name
        public Long Powers
        public UUID RoleID
        public ByteArray Title
    }

    public GroupRoleDataReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 57
        val it: Iterator<T> = this.RoleData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            val roleData: RoleData = (RoleData) it.next()
            i = roleData.Description.length + roleData.Name.length + 17 + 1 + roleData.Title.length + 1 + 8 + 4 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGroupRoleDataReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 116)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packUUID(byteBuffer, this.GroupData_Field.RequestID)
        packInt(byteBuffer, this.GroupData_Field.RoleCount)
        byteBuffer.put((Byte) this.RoleData_Fields.size())
        for (RoleData roleData : this.RoleData_Fields) {
            packUUID(byteBuffer, roleData.RoleID)
            packVariable(byteBuffer, roleData.Name, 1)
            packVariable(byteBuffer, roleData.Title, 1)
            packVariable(byteBuffer, roleData.Description, 1)
            packLong(byteBuffer, roleData.Powers)
            packInt(byteBuffer, roleData.Members)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer)
        this.GroupData_Field.RoleCount = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val roleData: RoleData = RoleData()
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
