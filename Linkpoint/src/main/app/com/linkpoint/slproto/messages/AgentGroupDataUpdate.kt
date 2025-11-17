package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AgentGroupDataUpdate : SLMessage {
    AgentData AgentData_Field
    ArrayList<GroupData> GroupData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class GroupData {
        Boolean AcceptNotices
        Int Contribution
        UUID GroupID
        UUID GroupInsigniaID
        byte[] GroupName
        Long GroupPowers
    }

    AgentGroupDataUpdate() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    Int CalcPayloadSize() {
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

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAgentGroupDataUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -123)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        byteBuffer.put((byte) this.GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packUUID(byteBuffer, groupData.GroupID)
            packLong(byteBuffer, groupData.GroupPowers)
            packBoolean(byteBuffer, groupData.AcceptNotices)
            packUUID(byteBuffer, groupData.GroupInsigniaID)
            packInt(byteBuffer, groupData.Contribution)
            packVariable(byteBuffer, groupData.GroupName, 1)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
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
