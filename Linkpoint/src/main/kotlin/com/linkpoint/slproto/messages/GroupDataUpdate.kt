package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupDataUpdate : SLMessage() {
    public ArrayList<AgentGroupData> AgentGroupData_Fields = ArrayList<>()

    @JvmStatic
    class AgentGroupData {
        public UUID AgentID
        public Long AgentPowers
        public UUID GroupID
        public ByteArray GroupTitle
    }

    public GroupDataUpdate() {
        this.zeroCoded = true
    }

    public fun CalcPayloadSize(): Int {
        val i: Int = 5
        val it: Iterator<T> = this.AgentGroupData_Fields.iterator()
        while (true) {
            val i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            i = ((AgentGroupData) it.next()).GroupTitle.length + 41 + i2
        }
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGroupDataUpdate(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) -124)
        byteBuffer.put((Byte) this.AgentGroupData_Fields.size())
        for (AgentGroupData agentGroupData : this.AgentGroupData_Fields) {
            packUUID(byteBuffer, agentGroupData.AgentID)
            packUUID(byteBuffer, agentGroupData.GroupID)
            packLong(byteBuffer, agentGroupData.AgentPowers)
            packVariable(byteBuffer, agentGroupData.GroupTitle, 1)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val agentGroupData: AgentGroupData = AgentGroupData()
            agentGroupData.AgentID = unpackUUID(byteBuffer)
            agentGroupData.GroupID = unpackUUID(byteBuffer)
            agentGroupData.AgentPowers = unpackLong(byteBuffer)
            agentGroupData.GroupTitle = unpackVariable(byteBuffer, 1)
            this.AgentGroupData_Fields.add(agentGroupData)
        }
    }
}
