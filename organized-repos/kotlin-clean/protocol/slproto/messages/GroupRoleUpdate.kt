package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupRoleUpdate : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<RoleData> RoleData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID SessionID
    }

    @JvmStatic
    class RoleData {
        public ByteArray Description
        public ByteArray Name
        public Long Powers
        public UUID RoleID
        public ByteArray Title
        public Int UpdateType
    }

    public GroupRoleUpdate() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int i = 53
        Iterator<T> it = this.RoleData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            RoleData roleData = (RoleData) it.next()
            i = roleData.Title.length + roleData.Name.length + 17 + 1 + roleData.Description.length + 1 + 8 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 122)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        byteBuffer.put((Byte) this.RoleData_Fields.size())
        for (RoleData roleData : this.RoleData_Fields) {
            packUUID(byteBuffer, roleData.RoleID)
            packVariable(byteBuffer, roleData.Name, 1)
            packVariable(byteBuffer, roleData.Description, 1)
            packVariable(byteBuffer, roleData.Title, 1)
            packLong(byteBuffer, roleData.Powers)
            packByte(byteBuffer, (Byte) roleData.UpdateType)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            RoleData roleData = RoleData()
            roleData.RoleID = unpackUUID(byteBuffer)
            roleData.Name = unpackVariable(byteBuffer, 1)
            roleData.Description = unpackVariable(byteBuffer, 1)
            roleData.Title = unpackVariable(byteBuffer, 1)
            roleData.Powers = unpackLong(byteBuffer)
            roleData.UpdateType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            this.RoleData_Fields.add(roleData)
        }
    }
}
