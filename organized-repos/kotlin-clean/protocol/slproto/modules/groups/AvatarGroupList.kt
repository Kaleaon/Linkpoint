package com.linkpoint.slproto.modules.groups

import com.google.common.collect.ImmutableMap
import com.linkpoint.Debug
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.messages.AgentGroupDataUpdate
import com.linkpoint.slproto.messages.AvatarGroupsReply
import com.linkpoint.slproto.modules.groups.AgentGroupDataInfo
import com.linkpoint.utils.UUIDPool
import java.io.Serializable
import java.util.UUID
import javax.annotation.Nullable

class AvatarGroupList : Serializable {
    val ImmutableMap<UUID, AvatarGroupEntry> Groups
    val UUID avatarID
    val Boolean newGroupDataValid

    @JvmStatic
    class AvatarGroupEntry : Serializable {
        val Boolean AcceptNotices
        val Int Contribution
        val UUID GroupID
        val UUID GroupInsigniaID
        val String GroupName
        val Long GroupPowers
        val String GroupTitle
        val Boolean ListInProfile

        public AvatarGroupEntry(AgentGroupDataUpdate.GroupData groupData) {
            this.GroupName = SLMessage.stringFromVariableOEM(groupData.GroupName)
            this.GroupTitle = null
            this.AcceptNotices = groupData.AcceptNotices
            this.GroupPowers = groupData.GroupPowers
            this.GroupInsigniaID = groupData.GroupInsigniaID
            this.ListInProfile = true
            this.GroupID = groupData.GroupID
            this.Contribution = groupData.Contribution
        }

        public AvatarGroupEntry(AvatarGroupsReply.GroupData groupData, AvatarGroupsReply.NewGroupData newGroupData) {
            this.GroupName = SLMessage.stringFromVariableOEM(groupData.GroupName)
            this.GroupTitle = SLMessage.stringFromVariableOEM(groupData.GroupTitle)
            this.AcceptNotices = groupData.AcceptNotices
            this.GroupPowers = groupData.GroupPowers
            this.GroupInsigniaID = groupData.GroupInsigniaID
            this.ListInProfile = newGroupData != null ? newGroupData.ListInProfile : true
            this.GroupID = groupData.GroupID
            this.Contribution = 0
        }

        public AvatarGroupEntry(AgentGroupDataInfo.GroupDataEntry groupDataEntry, AgentGroupDataInfo.NewGroupDataEntry newGroupDataEntry) {
            this.GroupName = groupDataEntry.GroupName
            this.GroupTitle = groupDataEntry.GroupTitle
            this.AcceptNotices = groupDataEntry.AcceptNotices
            this.GroupPowers = groupDataEntry.GroupPowers
            this.GroupInsigniaID = groupDataEntry.GroupInsigniaID
            this.ListInProfile = newGroupDataEntry != null ? newGroupDataEntry.ListInProfile : groupDataEntry.ListInProfile
            this.GroupID = groupDataEntry.GroupID
            this.Contribution = groupDataEntry.Contribution
        }
    }

    public AvatarGroupList(AgentGroupDataUpdate agentGroupDataUpdate) {
        this.avatarID = agentGroupDataUpdate.AgentData_Field.AgentID
        Debug.Printf("AvatarGroupList: created from AgentGroupDataUpdate (%s)", this.avatarID)
        ImmutableMap.Builder builder = ImmutableMap.Builder()
        for (AgentGroupDataUpdate.GroupData groupData : agentGroupDataUpdate.GroupData_Fields) {
            if (!UUIDPool.ZeroUUID.equals(groupData.GroupID)) {
                builder.put(groupData.GroupID, AvatarGroupEntry(groupData))
            }
        }
        this.Groups = builder.build()
        this.newGroupDataValid = true
    }

    public AvatarGroupList(AvatarGroupsReply avatarGroupsReply) {
        this.avatarID = avatarGroupsReply.AgentData_Field.AvatarID
        Debug.Printf("AvatarGroupList: created from AvatarGroupsReply (%s)", this.avatarID)
        ImmutableMap.Builder builder = ImmutableMap.Builder()
        for (AvatarGroupsReply.GroupData groupData : avatarGroupsReply.GroupData_Fields) {
            if (!UUIDPool.ZeroUUID.equals(groupData.GroupID)) {
                builder.put(groupData.GroupID, AvatarGroupEntry(groupData, avatarGroupsReply.NewGroupData_Field))
            }
        }
        this.Groups = builder.build()
        this.newGroupDataValid = true
    }

    public AvatarGroupList(AgentGroupDataInfo agentGroupDataInfo) {
        Boolean z = false
        this.avatarID = agentGroupDataInfo.AgentData.get(0).AvatarID != null ? agentGroupDataInfo.AgentData.get(0).AvatarID : agentGroupDataInfo.AgentData.get(0).AgentID
        Debug.Printf("AvatarGroupList: created from AgentGroupDataInfo (%s)", this.avatarID)
        ImmutableMap.Builder builder = ImmutableMap.Builder()
        Int i = 0
        while (i < agentGroupDataInfo.GroupData.size()) {
            AgentGroupDataInfo.NewGroupDataEntry newGroupDataEntry = (agentGroupDataInfo.NewGroupData == null || i >= agentGroupDataInfo.NewGroupData.size()) ? null : agentGroupDataInfo.NewGroupData.get(i)
            UUID uuid = agentGroupDataInfo.GroupData.get(i).GroupID
            if (!UUIDPool.ZeroUUID.equals(uuid)) {
                builder.put(uuid, AvatarGroupEntry(agentGroupDataInfo.GroupData.get(i), newGroupDataEntry))
            }
            i++
        }
        this.Groups = builder.build()
        this.newGroupDataValid = agentGroupDataInfo.NewGroupData != null ? true : z
    }
}
