package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AgentGroupDataUpdate : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<GroupData> GroupData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class GroupData {
        public Boolean AcceptNotices
        public Int Contribution
        public UUID GroupID
        public UUID GroupInsigniaID
        public ByteArray GroupName
        public Long GroupPowers
    }

    public AgentGroupDataUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    public Int CalcPayloadSize() {
        Int i = 21
        Iterator<T> it = this.GroupData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((GroupData) it.next()).GroupName.length + 46 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentGroupDataUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -123)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((Byte) this.GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packUUID(byteBuffer, groupData.GroupID)
            packLong(byteBuffer, groupData.GroupPowers)
            packBoolean(byteBuffer, groupData.AcceptNotices)
            packUUID(byteBuffer, groupData.GroupInsigniaID)
            packInt(byteBuffer, groupData.Contribution)
            packVariable(byteBuffer, groupData.GroupName, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            GroupData groupData = GroupData()
            groupData.GroupID = unpackUUID(byteBuffer)
            groupData.GroupPowers = unpackLong(byteBuffer)
            groupData.AcceptNotices = unpackBoolean(byteBuffer)
            groupData.GroupInsigniaID = unpackUUID(byteBuffer)
            groupData.Contribution = unpackInt(byteBuffer)
            groupData.GroupName = unpackVariable(byteBuffer, 1)
            this.GroupData_Fields.add(groupData)
        }
    }
}
