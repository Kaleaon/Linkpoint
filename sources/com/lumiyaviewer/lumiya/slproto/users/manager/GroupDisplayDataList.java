// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import java.util.Iterator;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterDisplayDataList, UserManager, ChatterList, GroupManager, 
//            OnListUpdated

class GroupDisplayDataList extends ChatterDisplayDataList
{

    public GroupDisplayDataList(UserManager usermanager, OnListUpdated onlistupdated)
    {
        super(usermanager, onlistupdated, null);
    }

    protected List getChatters()
    {
        Object obj = userManager.getChatterList().getGroupManager().getAvatarGroupList();
        if (obj == null)
        {
            return ImmutableList.of();
        }
        com.google.common.collect.ImmutableList.Builder builder = new com.google.common.collect.ImmutableList.Builder();
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
        for (obj = ((AvatarGroupList) (obj)).Groups.values().iterator(); ((Iterator) (obj)).hasNext(); builder.add(ChatterID.getGroupChatterID(userManager.getUserID(), avatargroupentry.GroupID)))
        {
            avatargroupentry = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((Iterator) (obj)).next();
        }

        return builder.build();
    }
}
