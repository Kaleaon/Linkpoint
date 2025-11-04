// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.llsd.LLSDSerialized;
import java.util.List;

public class AgentGroupDataInfo
{
    @LLSDSerialized
    public List<AgentDataEntry> AgentData;
    @LLSDSerialized
    public List<GroupDataEntry> GroupData;
    @LLSDSerialized
    public List<NewGroupDataEntry> NewGroupData;
    
    public static class AgentDataEntry
    {
        @LLSDSerialized
        public UUID AgentID;
        @LLSDSerialized
        public UUID AvatarID;
    }
    
    public static class GroupDataEntry
    {
        @LLSDSerialized
        public boolean AcceptNotices;
        @LLSDSerialized
        public int Contribution;
        @LLSDSerialized
        public UUID GroupID;
        @LLSDSerialized
        public UUID GroupInsigniaID;
        @LLSDSerialized
        public String GroupName;
        @LLSDSerialized
        public long GroupPowers;
        @LLSDSerialized
        public String GroupTitle;
        @LLSDSerialized
        public boolean ListInProfile;
    }
    
    public static class NewGroupDataEntry
    {
        @LLSDSerialized
        public boolean ListInProfile;
    }
}
