package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class GroupRoleMembersReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<MemberData> MemberData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID RequestID
        public Int TotalPairs
    }

    @JvmStatic
    class MemberData {
        public UUID MemberID
        public UUID RoleID
    }

    public GroupRoleMembersReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    public fun CalcPayloadSize(): Int {
        return (this.MemberData_Fields.size() * 32) + 57
    }

    fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleGroupRoleMembersReply(this)
    }

    fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 118)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.AgentData_Field.RequestID)
        packInt(byteBuffer, this.AgentData_Field.TotalPairs)
        byteBuffer.put((Byte) this.MemberData_Fields.size())
        for (MemberData memberData : this.MemberData_Fields) {
            packUUID(byteBuffer, memberData.RoleID)
            packUUID(byteBuffer, memberData.MemberID)
        }
    }

    fun UnpackPayload(byteBuffer: ByteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer)
        this.AgentData_Field.TotalPairs = unpackInt(byteBuffer)
        val b: Byte = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            val memberData: MemberData = MemberData()
            memberData.RoleID = unpackUUID(byteBuffer)
            memberData.MemberID = unpackUUID(byteBuffer)
            this.MemberData_Fields.add(memberData)
        }
    }
}
