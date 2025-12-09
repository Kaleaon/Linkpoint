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
        ByteArray Title
    }

    GroupTitlesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        Int i = 53
        Iterator<T> it = this.GroupData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as GroupData).next()).Title.size + 1 + 16 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGroupTitlesReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 120)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.AgentData_Field.RequestID)
        byteBuffer.put((this as byte).GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packVariable(byteBuffer, groupData.Title, 1)
            packUUID(byteBuffer, groupData.RoleID)
            packBoolean(byteBuffer, groupData.Selected)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            GroupData groupData = GroupData()
            groupData.Title = unpackVariable(byteBuffer, 1)
            groupData.RoleID = unpackUUID(byteBuffer)
            groupData.Selected = unpackBoolean(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
    }
}
