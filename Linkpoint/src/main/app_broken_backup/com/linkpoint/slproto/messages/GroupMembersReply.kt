package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupMembersReply : SLMessage {
    AgentData AgentData_Field
    GroupData GroupData_Field
    ArrayList<MemberData> MemberData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
    }

    class GroupData {
        UUID GroupID
        Int MemberCount
        UUID RequestID
    }

    class MemberData {
        UUID AgentID
        Long AgentPowers
        Int Contribution
        Boolean IsOwner
        ByteArray OnlineStatus
        ByteArray Title
    }

    GroupMembersReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 57
        Iterator<T> it = this.MemberData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            MemberData memberData = (it as MemberData).next()
            i = memberData.Title.size + memberData.OnlineStatus.size + 21 + 8 + 1 + 1 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleGroupMembersReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 1)
        byteBuffer.put((byte) 111)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packUUID(byteBuffer, this.GroupData_Field.RequestID)
        packInt(byteBuffer, this.GroupData_Field.MemberCount)
        byteBuffer.put((this as byte).MemberData_Fields.size())
        for (MemberData memberData : this.MemberData_Fields) {
            packUUID(byteBuffer, memberData.AgentID)
            packInt(byteBuffer, memberData.Contribution)
            packVariable(byteBuffer, memberData.OnlineStatus, 1)
            packLong(byteBuffer, memberData.AgentPowers)
            packVariable(byteBuffer, memberData.Title, 1)
            packBoolean(byteBuffer, memberData.IsOwner)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer)
        this.GroupData_Field.MemberCount = unpackInt(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            MemberData memberData = MemberData()
            memberData.AgentID = unpackUUID(byteBuffer)
            memberData.Contribution = unpackInt(byteBuffer)
            memberData.OnlineStatus = unpackVariable(byteBuffer, 1)
            memberData.AgentPowers = unpackLong(byteBuffer)
            memberData.Title = unpackVariable(byteBuffer, 1)
            memberData.IsOwner = unpackBoolean(byteBuffer)
            this.MemberData_Fields.add(memberData)
        }
    }
}
