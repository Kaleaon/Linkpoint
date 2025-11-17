package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupTitlesReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<GroupData> GroupData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID RequestID
    }

    class GroupData {
        UUID RoleID
        Boolean Selected
        byte[] Title
    }

    GroupTitlesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
        Int i = 53
        Iterator<T> it = this.GroupData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((GroupData) it.next()).Title.length + 1 + 16 + 1 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupTitlesReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 120)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.AgentData_Field.RequestID)
        byteBuffer.put((byte) this.GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packVariable(byteBuffer, groupData.Title, 1)
            packUUID(byteBuffer, groupData.RoleID)
            packBoolean(byteBuffer, groupData.Selected)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            GroupData groupData = GroupData()
            groupData.Title = unpackVariable(byteBuffer, 1)
            groupData.RoleID = unpackUUID(byteBuffer)
            groupData.Selected = unpackBoolean(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
    }
}
