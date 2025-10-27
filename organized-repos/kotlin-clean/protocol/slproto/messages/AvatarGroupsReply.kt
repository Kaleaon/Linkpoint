package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarGroupsReply : SLMessage() {
    public AgentData AgentData_Field
    public ArrayList<GroupData> GroupData_Fields = ArrayList<>()
    public NewGroupData NewGroupData_Field

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID AvatarID
    }

    @JvmStatic
    class GroupData {
        public Boolean AcceptNotices
        public UUID GroupID
        public UUID GroupInsigniaID
        public ByteArray GroupName
        public Long GroupPowers
        public ByteArray GroupTitle
    }

    @JvmStatic
    class NewGroupData {
        public Boolean ListInProfile
    }

    public AvatarGroupsReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.NewGroupData_Field = NewGroupData()
    }

    public Int CalcPayloadSize() {
        Int i = 37
        Iterator<T> it = this.GroupData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2 + 1
            }
            GroupData groupData = (GroupData) it.next()
            i = groupData.GroupName.length + groupData.GroupTitle.length + 10 + 16 + 1 + 16 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarGroupsReply(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) -83)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        byteBuffer.put((Byte) this.GroupData_Fields.size())
        for (GroupData groupData : this.GroupData_Fields) {
            packLong(byteBuffer, groupData.GroupPowers)
            packBoolean(byteBuffer, groupData.AcceptNotices)
            packVariable(byteBuffer, groupData.GroupTitle, 1)
            packUUID(byteBuffer, groupData.GroupID)
            packVariable(byteBuffer, groupData.GroupName, 1)
            packUUID(byteBuffer, groupData.GroupInsigniaID)
        }
        packBoolean(byteBuffer, this.NewGroupData_Field.ListInProfile)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
            GroupData groupData = GroupData()
            groupData.GroupPowers = unpackLong(byteBuffer)
            groupData.AcceptNotices = unpackBoolean(byteBuffer)
            groupData.GroupTitle = unpackVariable(byteBuffer, 1)
            groupData.GroupID = unpackUUID(byteBuffer)
            groupData.GroupName = unpackVariable(byteBuffer, 1)
            groupData.GroupInsigniaID = unpackUUID(byteBuffer)
            this.GroupData_Fields.add(groupData)
        }
        this.NewGroupData_Field.ListInProfile = unpackBoolean(byteBuffer)
    }
}
