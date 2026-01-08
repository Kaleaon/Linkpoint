package com.lumiyaviewer.lumiya.slproto.messages

import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.handler.SLMessageHandler
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class AvatarGroupsReply : SLMessage {
    var AgentData_Field: AgentData = AgentData()
    var GroupData_Fields: ArrayList<GroupData> = ArrayList()
    var NewGroupData_Field: NewGroupData = NewGroupData()

    class AgentData {
        var AgentID: UUID? = null
        var AvatarID: UUID? = null
    }

    class GroupData {
        var AcceptNotices: Boolean = false
        var GroupID: UUID? = null
        var GroupInsigniaID: UUID? = null
        var GroupName: ByteArray = ByteArray(0)
        var GroupPowers: Long = 0
        var GroupTitle: ByteArray = ByteArray(0)
    }

    class NewGroupData {
        var ListInProfile: Boolean = false
    }

    constructor() {
        this.zeroCoded = true
    }

    override fun CalcPayloadSize(): Int {
        var length = 37
        for (groupData in GroupData_Fields) {
            length += groupData.GroupName.size + groupData.GroupTitle.size + 10 + 16 + 1 + 16
        }
        return length + 1
    }

    override fun Handle(sLMessageHandler: SLMessageHandler) {
        sLMessageHandler.HandleAvatarGroupsReply(this)
    }

    override fun PackPayload(byteBuffer: ByteBuffer) {
        byteBuffer.putShort(-1)
        byteBuffer.put(0.toByte())
        byteBuffer.put((-83).toByte())
        packUUID(byteBuffer, AgentData_Field.AgentID!!)
        packUUID(byteBuffer, AgentData_Field.AvatarID!!)
        byteBuffer.put(GroupData_Fields.size.toByte())
        for (groupData in GroupData_Fields) {
            packLong(byteBuffer, groupData.GroupPowers)
            packBoolean(byteBuffer, groupData.AcceptNotices)
            packVariable(byteBuffer, groupData.GroupTitle, 1)
            packUUID(byteBuffer, groupData.GroupID!!)
            packVariable(byteBuffer, groupData.GroupName, 1)
            packUUID(byteBuffer, groupData.GroupInsigniaID!!)
        }
        packBoolean(byteBuffer, NewGroupData_Field.ListInProfile)
    }

    override fun UnpackPayload(byteBuffer: ByteBuffer) {
        AgentData_Field.AgentID = unpackUUID(byteBuffer)
        AgentData_Field.AvatarID = unpackUUID(byteBuffer)
        val b = (byteBuffer.get().toInt() and 0xFF)
        for (i in 0 until b) {
            val groupData = GroupData()
            groupData.GroupPowers = unpackLong(byteBuffer)
            groupData.AcceptNotices = unpackBoolean(byteBuffer)
            groupData.GroupTitle = unpackVariable(byteBuffer, 1)
            groupData.GroupID = unpackUUID(byteBuffer)
            groupData.GroupName = unpackVariable(byteBuffer, 1)
            groupData.GroupInsigniaID = unpackUUID(byteBuffer)
            GroupData_Fields.add(groupData)
        }
        NewGroupData_Field.ListInProfile = unpackBoolean(byteBuffer)
    }
}
