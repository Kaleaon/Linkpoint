package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AgentGroupDataInfo;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.Serializable;
import java.util.UUID;
import javax.annotation.Nullable;

public class AvatarGroupList implements Serializable {
    public final ImmutableMap<UUID, AvatarGroupEntry> Groups;
    public final UUID avatarID;
    public final boolean newGroupDataValid;

    public static class AvatarGroupEntry implements Serializable {
        public final boolean AcceptNotices;
        public final int Contribution;
        public final UUID GroupID;
        public final UUID GroupInsigniaID;
        public final String GroupName;
        public final long GroupPowers;
        @Nullable
        public final String GroupTitle;
        public final boolean ListInProfile;

        public AvatarGroupEntry(AgentGroupDataUpdate.GroupData groupData) {
            this.GroupName = SLMessage.stringFromVariableOEM(groupData.GroupName);
            this.GroupTitle = null;
            this.AcceptNotices = groupData.AcceptNotices;
            this.GroupPowers = groupData.GroupPowers;
            this.GroupInsigniaID = groupData.GroupInsigniaID;
            this.ListInProfile = true;
            this.GroupID = groupData.GroupID;
            this.Contribution = groupData.Contribution;
        }

        public AvatarGroupEntry(AvatarGroupsReply.GroupData groupData, @Nullable AvatarGroupsReply.NewGroupData newGroupData) {
            this.GroupName = SLMessage.stringFromVariableOEM(groupData.GroupName);
            this.GroupTitle = SLMessage.stringFromVariableOEM(groupData.GroupTitle);
            this.AcceptNotices = groupData.AcceptNotices;
            this.GroupPowers = groupData.GroupPowers;
            this.GroupInsigniaID = groupData.GroupInsigniaID;
            this.ListInProfile = newGroupData != null ? newGroupData.ListInProfile : true;
            this.GroupID = groupData.GroupID;
            this.Contribution = 0;
        }

        public AvatarGroupEntry(AgentGroupDataInfo.GroupDataEntry groupDataEntry, AgentGroupDataInfo.NewGroupDataEntry newGroupDataEntry) {
            this.GroupName = groupDataEntry.GroupName;
            this.GroupTitle = groupDataEntry.GroupTitle;
            this.AcceptNotices = groupDataEntry.AcceptNotices;
            this.GroupPowers = groupDataEntry.GroupPowers;
            this.GroupInsigniaID = groupDataEntry.GroupInsigniaID;
            this.ListInProfile = newGroupDataEntry != null ? newGroupDataEntry.ListInProfile : groupDataEntry.ListInProfile;
            this.GroupID = groupDataEntry.GroupID;
            this.Contribution = groupDataEntry.Contribution;
        }
    }

    public AvatarGroupList(AgentGroupDataUpdate agentGroupDataUpdate) {
        this.avatarID = agentGroupDataUpdate.AgentData_Field.AgentID;
        Debug.Printf("AvatarGroupList: created from AgentGroupDataUpdate (%s)", this.avatarID);
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (AgentGroupDataUpdate.GroupData groupData : agentGroupDataUpdate.GroupData_Fields) {
            if (!UUIDPool.ZeroUUID.equals(groupData.GroupID)) {
                builder.put(groupData.GroupID, new AvatarGroupEntry(groupData));
            }
        }
        this.Groups = builder.build();
        this.newGroupDataValid = true;
    }

    public AvatarGroupList(AvatarGroupsReply avatarGroupsReply) {
        this.avatarID = avatarGroupsReply.AgentData_Field.AvatarID;
        Debug.Printf("AvatarGroupList: created from AvatarGroupsReply (%s)", this.avatarID);
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        for (AvatarGroupsReply.GroupData groupData : avatarGroupsReply.GroupData_Fields) {
            if (!UUIDPool.ZeroUUID.equals(groupData.GroupID)) {
                builder.put(groupData.GroupID, new AvatarGroupEntry(groupData, avatarGroupsReply.NewGroupData_Field));
            }
        }
        this.Groups = builder.build();
        this.newGroupDataValid = true;
    }

    public AvatarGroupList(AgentGroupDataInfo agentGroupDataInfo) {
        boolean z = false;
        this.avatarID = agentGroupDataInfo.AgentData.get(0).AvatarID != null ? agentGroupDataInfo.AgentData.get(0).AvatarID : agentGroupDataInfo.AgentData.get(0).AgentID;
        Debug.Printf("AvatarGroupList: created from AgentGroupDataInfo (%s)", this.avatarID);
        ImmutableMap.Builder builder = new ImmutableMap.Builder();
        int i = 0;
        while (i < agentGroupDataInfo.GroupData.size()) {
            AgentGroupDataInfo.NewGroupDataEntry newGroupDataEntry = (agentGroupDataInfo.NewGroupData == null || i >= agentGroupDataInfo.NewGroupData.size()) ? null : agentGroupDataInfo.NewGroupData.get(i);
            UUID uuid = agentGroupDataInfo.GroupData.get(i).GroupID;
            if (!UUIDPool.ZeroUUID.equals(uuid)) {
                builder.put(uuid, new AvatarGroupEntry(agentGroupDataInfo.GroupData.get(i), newGroupDataEntry));
            }
            i++;
        }
        this.Groups = builder.build();
        this.newGroupDataValid = agentGroupDataInfo.NewGroupData != null ? true : z;
    }
}
