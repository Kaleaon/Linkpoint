// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.io.Serializable;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.groups:
//            AvatarGroupList

public static class .Contribution
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

    public try(com.lumiyaviewer.lumiya.slproto.messages.Entry entry)
    {
        GroupName = SLMessage.stringFromVariableOEM(entry.oupName);
        GroupTitle = null;
        AcceptNotices = entry.ceptNotices;
        GroupPowers = entry.oupPowers;
        GroupInsigniaID = entry.oupInsigniaID;
        ListInProfile = true;
        GroupID = entry.oupID;
        Contribution = entry.ntribution;
    }

    public tion(com.lumiyaviewer.lumiya.slproto.messages.Entry entry, com.lumiyaviewer.lumiya.slproto.messages.Entry entry1)
    {
        GroupName = SLMessage.stringFromVariableOEM(entry.Name);
        GroupTitle = SLMessage.stringFromVariableOEM(entry.Title);
        AcceptNotices = entry.tNotices;
        GroupPowers = entry.Powers;
        GroupInsigniaID = entry.InsigniaID;
        boolean flag;
        if (entry1 != null)
        {
            flag = entry1.stInProfile;
        } else
        {
            flag = true;
        }
        ListInProfile = flag;
        GroupID = entry.ID;
        Contribution = 0;
    }

    public try( , try try1)
    {
        GroupName = .GroupName;
        GroupTitle = .GroupTitle;
        AcceptNotices = .AcceptNotices;
        GroupPowers = .GroupPowers;
        GroupInsigniaID = .GroupInsigniaID;
        boolean flag;
        if (try1 != null)
        {
            flag = try1.ListInProfile;
        } else
        {
            flag = .ListInProfile;
        }
        ListInProfile = flag;
        GroupID = .GroupID;
        Contribution = .Contribution;
    }
}
