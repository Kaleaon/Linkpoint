package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupDataUpdate : SLMessage {
    ArrayList<AgentGroupData> AgentGroupData_Fields = ArrayList<>()

    class AgentGroupData {
        UUID AgentID
        Long AgentPowers
        UUID GroupID
        ByteArray GroupTitle
    }

    GroupDataUpdate() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        Int i = 5
        Iterator<T> it = this.AgentGroupData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((it as AgentGroupData).next()).GroupTitle.size + 41 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleGroupDataUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) -124)
        byteBuffer.put((this as byte).AgentGroupData_Fields.size())
        for (AgentGroupData agentGroupData : this.AgentGroupData_Fields) {
            packUUID(byteBuffer, agentGroupData.AgentID)
            packUUID(byteBuffer, agentGroupData.GroupID)
            packLong(byteBuffer, agentGroupData.AgentPowers)
            packVariable(byteBuffer, agentGroupData.GroupTitle, 1)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            AgentGroupData agentGroupData = AgentGroupData()
            agentGroupData.AgentID = unpackUUID(byteBuffer)
            agentGroupData.GroupID = unpackUUID(byteBuffer)
            agentGroupData.AgentPowers = unpackLong(byteBuffer)
            agentGroupData.GroupTitle = unpackVariable(byteBuffer, 1)
            this.AgentGroupData_Fields.add(agentGroupData)
        }
    }
}
