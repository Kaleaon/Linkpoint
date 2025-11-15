package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupRoleUpdate : SLMessage {
    AgentData AgentData_Field
    ArrayList<RoleData> RoleData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID SessionID
    }

    class RoleData {
        byte[] Description
        byte[] Name
        Long Powers
        UUID RoleID
        byte[] Title
        Int UpdateType
    }

    GroupRoleUpdate() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
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

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupRoleUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 122)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        byteBuffer.put((byte) this.RoleData_Fields.size())
        for (RoleData roleData : this.RoleData_Fields) {
            packUUID(byteBuffer, roleData.RoleID)
            packVariable(byteBuffer, roleData.Name, 1)
            packVariable(byteBuffer, roleData.Description, 1)
            packVariable(byteBuffer, roleData.Title, 1)
            packLong(byteBuffer, roleData.Powers)
            packByte(byteBuffer, (byte) roleData.UpdateType)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
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
