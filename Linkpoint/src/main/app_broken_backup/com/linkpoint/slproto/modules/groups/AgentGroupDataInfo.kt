package com.linkpoint.slproto.modules.groups

import com.linkpoint.slproto.llsd.LLSDSerialized
import java.util.List
import java.util.UUID

class AgentGroupDataInfo {
    @LLSDSerialized
    List<AgentDataEntry> AgentData
    @LLSDSerialized
    List<GroupDataEntry> GroupData
    @LLSDSerialized
    List<NewGroupDataEntry> NewGroupData

    class AgentDataEntry {
        @LLSDSerialized
        UUID AgentID
        @LLSDSerialized
        UUID AvatarID
    }

    class GroupDataEntry {
        @LLSDSerialized
        Boolean AcceptNotices
        @LLSDSerialized
        Int Contribution
        @LLSDSerialized
        UUID GroupID
        @LLSDSerialized
        UUID GroupInsigniaID
        @LLSDSerialized
        String GroupName
        @LLSDSerialized
        Long GroupPowers
        @LLSDSerialized
        String GroupTitle
        @LLSDSerialized
        Boolean ListInProfile
    }

    class NewGroupDataEntry {
        @LLSDSerialized
        Boolean ListInProfile
    }
}
