// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import java.util.List;
import java.util.UUID;

public class AgentGroupDataInfo
{
    public static class AgentDataEntry
    {

        public UUID AgentID;
        public UUID AvatarID;

        public AgentDataEntry()
        {
        }
    }

    public static class GroupDataEntry
    {

        public boolean AcceptNotices;
        public int Contribution;
        public UUID GroupID;
        public UUID GroupInsigniaID;
        public String GroupName;
        public long GroupPowers;
        public String GroupTitle;
        public boolean ListInProfile;

        public GroupDataEntry()
        {
        }
    }

    public static class NewGroupDataEntry
    {

        public boolean ListInProfile;

        public NewGroupDataEntry()
        {
        }
    }


    public List AgentData;
    public List GroupData;
    public List NewGroupData;

    public AgentGroupDataInfo()
    {
    }
}
