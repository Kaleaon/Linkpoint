package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class EjectGroupMemberRequest : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<EjectData> EjectData_Fields = ArrayList<>()
    public GroupData GroupData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID SessionID
    }

    @JvmStatic
    class EjectData {
        public UUID EjecteeID
    }

    @JvmStatic
    class GroupData {
        public UUID GroupID
    }

    public EjectGroupMemberRequest() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.EjectData_Fields.size() * 16) + 53
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleEjectGroupMemberRequest(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 89)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        byteBuffer.put((Byte) this.EjectData_Fields.size())
        for (EjectData ejectData : this.EjectData_Fields) {
            packUUID(byteBuffer, ejectData.EjecteeID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val ejectData: EjectData = EjectData()
            ejectData.EjecteeID = unpackUUID(byteBuffer)
            this.EjectData_Fields.add(ejectData)
        }
    }
}
