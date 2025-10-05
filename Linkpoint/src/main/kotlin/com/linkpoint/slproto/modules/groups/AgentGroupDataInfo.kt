package com.linkpoint.slproto.modules.groups

import com.linkpoint.slproto.llsd.LLSDSerialized
import java.util.List
import java.util.UUID

class AgentGroupDataInfo {
    @LLSDSerialized
    public List<AgentDataEntry> AgentData
    @LLSDSerialized
    public List<GroupDataEntry> GroupData
    @LLSDSerialized
    public List<NewGroupDataEntry> NewGroupData

    @JvmStatic
    class AgentDataEntry {
        @LLSDSerialized
        public UUID AgentID
        @LLSDSerialized
        public UUID AvatarID
    }

    @JvmStatic
    class GroupDataEntry {
        @LLSDSerialized
        public Boolean AcceptNotices
        @LLSDSerialized
        public Int Contribution
        @LLSDSerialized
        public UUID GroupID
        @LLSDSerialized
        public UUID GroupInsigniaID
        @LLSDSerialized
        public String GroupName
        @LLSDSerialized
        public Long GroupPowers
        @LLSDSerialized
        public String GroupTitle
        @LLSDSerialized
        public Boolean ListInProfile
    }

    @JvmStatic
    class NewGroupDataEntry {
        @LLSDSerialized
        public Boolean ListInProfile
    }
}
