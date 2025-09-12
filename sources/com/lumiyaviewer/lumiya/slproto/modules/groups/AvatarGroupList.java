// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.groups:
//            AgentGroupDataInfo

public class AvatarGroupList
    implements Serializable
{
    public static class AvatarGroupEntry
        implements Serializable
    {

        public final boolean AcceptNotices;
        public final int Contribution;
        public final UUID GroupID;
        public final UUID GroupInsigniaID;
        public final String GroupName;
        public final long GroupPowers;
        public final String GroupTitle;
        public final boolean ListInProfile;

        public AvatarGroupEntry(com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate.GroupData groupdata)
        {
            GroupName = SLMessage.stringFromVariableOEM(groupdata.GroupName);
            GroupTitle = null;
            AcceptNotices = groupdata.AcceptNotices;
            GroupPowers = groupdata.GroupPowers;
            GroupInsigniaID = groupdata.GroupInsigniaID;
            ListInProfile = true;
            GroupID = groupdata.GroupID;
            Contribution = groupdata.Contribution;
        }

        public AvatarGroupEntry(com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply.GroupData groupdata, com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply.NewGroupData newgroupdata)
        {
            GroupName = SLMessage.stringFromVariableOEM(groupdata.GroupName);
            GroupTitle = SLMessage.stringFromVariableOEM(groupdata.GroupTitle);
            AcceptNotices = groupdata.AcceptNotices;
            GroupPowers = groupdata.GroupPowers;
            GroupInsigniaID = groupdata.GroupInsigniaID;
            boolean flag;
            if (newgroupdata != null)
            {
                flag = newgroupdata.ListInProfile;
            } else
            {
                flag = true;
            }
            ListInProfile = flag;
            GroupID = groupdata.GroupID;
            Contribution = 0;
        }

        public AvatarGroupEntry(AgentGroupDataInfo.GroupDataEntry groupdataentry, AgentGroupDataInfo.NewGroupDataEntry newgroupdataentry)
        {
            GroupName = groupdataentry.GroupName;
            GroupTitle = groupdataentry.GroupTitle;
            AcceptNotices = groupdataentry.AcceptNotices;
            GroupPowers = groupdataentry.GroupPowers;
            GroupInsigniaID = groupdataentry.GroupInsigniaID;
            boolean flag;
            if (newgroupdataentry != null)
            {
                flag = newgroupdataentry.ListInProfile;
            } else
            {
                flag = groupdataentry.ListInProfile;
            }
            ListInProfile = flag;
            GroupID = groupdataentry.GroupID;
            Contribution = groupdataentry.Contribution;
        }
    }


    public final ImmutableMap Groups;
    public final UUID avatarID;
    public final boolean newGroupDataValid;

    public AvatarGroupList(AgentGroupDataUpdate agentgroupdataupdate)
    {
        avatarID = agentgroupdataupdate.AgentData_Field.AgentID;
        Debug.Printf("AvatarGroupList: created from AgentGroupDataUpdate (%s)", new Object[] {
            avatarID
        });
        com.google.common.collect.ImmutableMap.Builder builder = new com.google.common.collect.ImmutableMap.Builder();
        agentgroupdataupdate = agentgroupdataupdate.GroupData_Fields.iterator();
        do
        {
            if (!agentgroupdataupdate.hasNext())
            {
                break;
            }
            com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate.GroupData groupdata = (com.lumiyaviewer.lumiya.slproto.messages.AgentGroupDataUpdate.GroupData)agentgroupdataupdate.next();
            if (!UUIDPool.ZeroUUID.equals(groupdata.GroupID))
            {
                builder.put(groupdata.GroupID, new AvatarGroupEntry(groupdata));
            }
        } while (true);
        Groups = builder.build();
        newGroupDataValid = true;
    }

    public AvatarGroupList(AvatarGroupsReply avatargroupsreply)
    {
        avatarID = avatargroupsreply.AgentData_Field.AvatarID;
        Debug.Printf("AvatarGroupList: created from AvatarGroupsReply (%s)", new Object[] {
            avatarID
        });
        com.google.common.collect.ImmutableMap.Builder builder = new com.google.common.collect.ImmutableMap.Builder();
        Iterator iterator = avatargroupsreply.GroupData_Fields.iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply.GroupData groupdata = (com.lumiyaviewer.lumiya.slproto.messages.AvatarGroupsReply.GroupData)iterator.next();
            if (!UUIDPool.ZeroUUID.equals(groupdata.GroupID))
            {
                builder.put(groupdata.GroupID, new AvatarGroupEntry(groupdata, avatargroupsreply.NewGroupData_Field));
            }
        } while (true);
        Groups = builder.build();
        newGroupDataValid = true;
    }

    public AvatarGroupList(AgentGroupDataInfo agentgroupdatainfo)
    {
        boolean flag = false;
        super();
        Object obj;
        com.google.common.collect.ImmutableMap.Builder builder;
        int i;
        if (((AgentGroupDataInfo.AgentDataEntry)agentgroupdatainfo.AgentData.get(0)).AvatarID != null)
        {
            obj = ((AgentGroupDataInfo.AgentDataEntry)agentgroupdatainfo.AgentData.get(0)).AvatarID;
        } else
        {
            obj = ((AgentGroupDataInfo.AgentDataEntry)agentgroupdatainfo.AgentData.get(0)).AgentID;
        }
        avatarID = ((UUID) (obj));
        Debug.Printf("AvatarGroupList: created from AgentGroupDataInfo (%s)", new Object[] {
            avatarID
        });
        builder = new com.google.common.collect.ImmutableMap.Builder();
        i = 0;
        while (i < agentgroupdatainfo.GroupData.size()) 
        {
            UUID uuid;
            if (agentgroupdatainfo.NewGroupData != null && i < agentgroupdatainfo.NewGroupData.size())
            {
                obj = (AgentGroupDataInfo.NewGroupDataEntry)agentgroupdatainfo.NewGroupData.get(i);
            } else
            {
                obj = null;
            }
            uuid = ((AgentGroupDataInfo.GroupDataEntry)agentgroupdatainfo.GroupData.get(i)).GroupID;
            if (!UUIDPool.ZeroUUID.equals(uuid))
            {
                builder.put(uuid, new AvatarGroupEntry((AgentGroupDataInfo.GroupDataEntry)agentgroupdatainfo.GroupData.get(i), ((AgentGroupDataInfo.NewGroupDataEntry) (obj))));
            }
            i++;
        }
        Groups = builder.build();
        if (agentgroupdatainfo.NewGroupData != null)
        {
            flag = true;
        }
        newGroupDataValid = flag;
    }
}
