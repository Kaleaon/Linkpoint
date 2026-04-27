package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupTitlesReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<GroupData> GroupData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID RequestID
    }

    @JvmStatic
    class GroupData {
        public UUID RoleID
        public Boolean Selected
        public ByteArray Title
    }

    public GroupTitlesReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
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

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupTitlesReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 120)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.AgentData_Field.RequestID)
        byteBuffer.put((Byte) this.GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packVariable(byteBuffer, groupData.Title, 1)
            packUUID(byteBuffer, groupData.RoleID)
            packBoolean(byteBuffer, groupData.Selected)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            GroupData groupData = GroupData()
            groupData.Title = unpackVariable(byteBuffer, 1)
            groupData.RoleID = unpackUUID(byteBuffer)
            groupData.Selected = unpackBoolean(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
    }
}
