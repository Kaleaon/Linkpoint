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

    public fun CalcPayloadSize(): Int {
        val i: Int = 53
        val it: Iterator<T> = this.GroupData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((GroupData) it.next()).Title.length + 1 + 16 + 1 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGroupTitlesReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
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

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val groupData: GroupData = GroupData()
            groupData.Title = unpackVariable(byteBuffer, 1)
            groupData.RoleID = unpackUUID(byteBuffer)
            groupData.Selected = unpackBoolean(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
    }
}
