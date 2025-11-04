// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate;
import java.util.UUID;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;

public class AvatarGroupList implements Serializable
{
    public final ImmutableMap<UUID, AvatarGroupEntry> Groups;
    public final UUID avatarID;
    public final boolean newGroupDataValid;
    
    public AvatarGroupList(final AgentGroupDataUpdate agentGroupDataUpdate) {
        this.avatarID = agentGroupDataUpdate.AgentData_Field.AgentID;
        Debug.Printf("AvatarGroupList: created from AgentGroupDataUpdate (%s)", this.avatarID);
        final ImmutableMap.Builder<UUID, AvatarGroupEntry> builder = new ImmutableMap.Builder<UUID, AvatarGroupEntry>();
        for (final AgentGroupDataUpdate.GroupData groupData : agentGroupDataUpdate.GroupData_Fields) {
            if (!UUIDPool.ZeroUUID.equals(groupData.GroupID)) {
                builder.put(groupData.GroupID, new AvatarGroupEntry(groupData));
            }
        }
        this.Groups = builder.build();
        this.newGroupDataValid = true;
    }
    
    public AvatarGroupList(final AvatarGroupsReply avatarGroupsReply) {
        this.avatarID = avatarGroupsReply.AgentData_Field.AvatarID;
        Debug.Printf("AvatarGroupList: created from AvatarGroupsReply (%s)", this.avatarID);
        final ImmutableMap.Builder<UUID, AvatarGroupEntry> builder = new ImmutableMap.Builder<UUID, AvatarGroupEntry>();
        for (final AvatarGroupsReply.GroupData groupData : avatarGroupsReply.GroupData_Fields) {
            if (!UUIDPool.ZeroUUID.equals(groupData.GroupID)) {
                builder.put(groupData.GroupID, new AvatarGroupEntry(groupData, avatarGroupsReply.NewGroupData_Field));
            }
        }
        this.Groups = builder.build();
        this.newGroupDataValid = true;
    }
    
    public AvatarGroupList(final AgentGroupDataInfo agentGroupDataInfo) {
        boolean newGroupDataValid = false;
        UUID avatarID;
        if (((AgentGroupDataInfo.AgentDataEntry)agentGroupDataInfo.AgentData.get(0)).AvatarID != null) {
            avatarID = ((AgentGroupDataInfo.AgentDataEntry)agentGroupDataInfo.AgentData.get(0)).AvatarID;
        }
        else {
            avatarID = ((AgentGroupDataInfo.AgentDataEntry)agentGroupDataInfo.AgentData.get(0)).AgentID;
        }
        this.avatarID = avatarID;
        Debug.Printf("AvatarGroupList: created from AgentGroupDataInfo (%s)", this.avatarID);
        final ImmutableMap.Builder<UUID, AvatarGroupEntry> builder = new ImmutableMap.Builder<UUID, AvatarGroupEntry>();
        for (int i = 0; i < agentGroupDataInfo.GroupData.size(); ++i) {
            AgentGroupDataInfo.NewGroupDataEntry newGroupDataEntry;
            if (agentGroupDataInfo.NewGroupData != null && i < agentGroupDataInfo.NewGroupData.size()) {
                newGroupDataEntry = (AgentGroupDataInfo.NewGroupDataEntry)agentGroupDataInfo.NewGroupData.get(i);
            }
            else {
                newGroupDataEntry = null;
            }
            final UUID groupID = ((AgentGroupDataInfo.GroupDataEntry)agentGroupDataInfo.GroupData.get(i)).GroupID;
            if (!UUIDPool.ZeroUUID.equals(groupID)) {
                builder.put(groupID, new AvatarGroupEntry(agentGroupDataInfo.GroupData.get(i), newGroupDataEntry));
            }
        }
        this.Groups = builder.build();
        if (agentGroupDataInfo.NewGroupData != null) {
            newGroupDataValid = true;
        }
        this.newGroupDataValid = newGroupDataValid;
    }
    
    public static class AvatarGroupEntry implements Serializable
    {
        public final boolean AcceptNotices;
        public final int Contribution;
        public final UUID GroupID;
        public final UUID GroupInsigniaID;
        public final String GroupName;
        public final long GroupPowers;
        @Nullable
        public final String GroupTitle;
        public final boolean ListInProfile;
        
        public AvatarGroupEntry(final AgentGroupDataUpdate.GroupData groupData) {
            this.GroupName = SLMessage.stringFromVariableOEM(groupData.GroupName);
            this.GroupTitle = null;
            this.AcceptNotices = groupData.AcceptNotices;
            this.GroupPowers = groupData.GroupPowers;
            this.GroupInsigniaID = groupData.GroupInsigniaID;
            this.ListInProfile = true;
            this.GroupID = groupData.GroupID;
            this.Contribution = groupData.Contribution;
        }
        
        public AvatarGroupEntry(final AvatarGroupsReply.GroupData groupData, @Nullable final AvatarGroupsReply.NewGroupData newGroupData) {
            this.GroupName = SLMessage.stringFromVariableOEM(groupData.GroupName);
            this.GroupTitle = SLMessage.stringFromVariableOEM(groupData.GroupTitle);
            this.AcceptNotices = groupData.AcceptNotices;
            this.GroupPowers = groupData.GroupPowers;
            this.GroupInsigniaID = groupData.GroupInsigniaID;
            this.ListInProfile = (newGroupData == null || newGroupData.ListInProfile);
            this.GroupID = groupData.GroupID;
            this.Contribution = 0;
        }
        
        public AvatarGroupEntry(final AgentGroupDataInfo.GroupDataEntry groupDataEntry, final AgentGroupDataInfo.NewGroupDataEntry newGroupDataEntry) {
            this.GroupName = groupDataEntry.GroupName;
            this.GroupTitle = groupDataEntry.GroupTitle;
            this.AcceptNotices = groupDataEntry.AcceptNotices;
            this.GroupPowers = groupDataEntry.GroupPowers;
            this.GroupInsigniaID = groupDataEntry.GroupInsigniaID;
            boolean listInProfile;
            if (newGroupDataEntry != null) {
                listInProfile = newGroupDataEntry.ListInProfile;
            }
            else {
                listInProfile = groupDataEntry.ListInProfile;
            }
            this.ListInProfile = listInProfile;
            this.GroupID = groupDataEntry.GroupID;
            this.Contribution = groupDataEntry.Contribution;
        }
    }
}
