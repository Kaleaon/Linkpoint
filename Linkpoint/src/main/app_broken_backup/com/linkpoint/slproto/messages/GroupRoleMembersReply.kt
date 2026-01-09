package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class GroupRoleMembersReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<MemberData> MemberData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID RequestID
        Int TotalPairs
    }

    class MemberData {
        UUID MemberID
        UUID RoleID
    }

    GroupRoleMembersReply() {
        this.zeroCoded = false
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.MemberData_Fields.size() * 32) + 57
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupRoleMembersReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 118)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packUUID(byteBuffer, this.AgentData_Field.RequestID)
        packInt(byteBuffer, this.AgentData_Field.TotalPairs)
        byteBuffer.put((this as byte).MemberData_Fields.size())
        for (MemberData memberData : this.MemberData_Fields) {
            packUUID(byteBuffer, memberData.RoleID)
            packUUID(byteBuffer, memberData.MemberID)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.AgentData_Field.RequestID = unpackUUID(byteBuffer)
        this.AgentData_Field.TotalPairs = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            MemberData memberData = MemberData()
            memberData.RoleID = unpackUUID(byteBuffer)
            memberData.MemberID = unpackUUID(byteBuffer)
            this.MemberData_Fields.add(memberData)
        }
    }
}
