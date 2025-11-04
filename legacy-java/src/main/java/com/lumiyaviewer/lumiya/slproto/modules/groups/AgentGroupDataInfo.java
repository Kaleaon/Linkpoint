package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.slproto.llsd.LLSDSerialized;
import java.util.List;
import java.util.UUID;

/* loaded from: classes.dex */
public class AgentGroupDataInfo {

    @LLSDSerialized
    public List<AgentDataEntry> AgentData;

    @LLSDSerialized
    public List<GroupDataEntry> GroupData;

    @LLSDSerialized
    public List<NewGroupDataEntry> NewGroupData;

    /* loaded from: classes.dex */
    public static class AgentDataEntry {

        @LLSDSerialized
        public UUID AgentID;

        @LLSDSerialized
        public UUID AvatarID;
    }

    /* loaded from: classes.dex */
    public static class GroupDataEntry {

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

    /* loaded from: classes.dex */
    public static class NewGroupDataEntry {

        @LLSDSerialized
        public boolean ListInProfile;
    }
}
