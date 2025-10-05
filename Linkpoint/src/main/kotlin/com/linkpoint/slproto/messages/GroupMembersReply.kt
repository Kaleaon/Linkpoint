package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class GroupMembersReply : SLMessage() {
    public AgentData AgentData_Field
    public GroupData GroupData_Field
    public ArrayList<MemberData> MemberData_Fields = ArrayList<>()

    @JvmStatic
    class AgentData {
        public UUID AgentID
    }

    @JvmStatic
    class GroupData {
        public UUID GroupID
        public Int MemberCount
        public UUID RequestID
    }

    @JvmStatic
    class MemberData {
        public UUID AgentID
        public Long AgentPowers
        public Int Contribution
        public Boolean IsOwner
        public Byte[] OnlineStatus
        public Byte[] Title
    }

    public GroupMembersReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.GroupData_Field = GroupData()
    }

    public Int CalcPayloadSize() {
        Int i = 57
        Iterator<T> it = this.MemberData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            MemberData memberData = (MemberData) it.next()
            i = memberData.Title.length + memberData.OnlineStatus.length + 21 + 8 + 1 + 1 + i2
        }
    }

    public Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleGroupMembersReply(this)
    }

    public Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 1)
        byteBuffer.put((Byte) 111)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.GroupData_Field.GroupID)
        packUUID(byteBuffer, this.GroupData_Field.RequestID)
        packInt(byteBuffer, this.GroupData_Field.MemberCount)
        byteBuffer.put((Byte) this.MemberData_Fields.size())
        for (MemberData memberData : this.MemberData_Fields) {
            packUUID(byteBuffer, memberData.AgentID)
            packInt(byteBuffer, memberData.Contribution)
            packVariable(byteBuffer, memberData.OnlineStatus, 1)
            packLong(byteBuffer, memberData.AgentPowers)
            packVariable(byteBuffer, memberData.Title, 1)
            packBoolean(byteBuffer, memberData.IsOwner)
        }
    }

    public Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.GroupData_Field.GroupID = unpackUUID(byteBuffer)
        this.GroupData_Field.RequestID = unpackUUID(byteBuffer)
        this.GroupData_Field.MemberCount = unpackInt(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
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
