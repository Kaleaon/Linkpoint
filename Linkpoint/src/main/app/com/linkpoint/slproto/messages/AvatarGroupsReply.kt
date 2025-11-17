package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class AvatarGroupsReply : SLMessage {
    AgentData AgentData_Field
    ArrayList<GroupData> GroupData_Fields = ArrayList<>()
    NewGroupData NewGroupData_Field

    class AgentData {
        UUID AgentID
        UUID AvatarID
    }

    class GroupData {
        Boolean AcceptNotices
        UUID GroupID
        UUID GroupInsigniaID
        byte[] GroupName
        Long GroupPowers
        byte[] GroupTitle
    }

    class NewGroupData {
        Boolean ListInProfile
    }

    AvatarGroupsReply() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
        this.NewGroupData_Field = NewGroupData()
    }

    Int CalcPayloadSize() {
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

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleAvatarGroupsReply(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put((byte) 0)
        byteBuffer.put((byte) -83)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.AvatarID)
        byteBuffer.put((byte) this.GroupData_Fields.size())
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

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
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
