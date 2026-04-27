// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLMinimap;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import java.util.List;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterDisplayDataList, ChatterDisplayData, UserManager, OnListUpdated

class NearbyChattersDisplayDataList extends ChatterDisplayDataList
{

    public NearbyChattersDisplayDataList(UserManager usermanager, OnListUpdated onlistupdated)
    {
        super(usermanager, onlistupdated, new _2D_.Lambda.D8uG4BZ932XmwoX97ZE2tEBU1gE());
    }

    static int lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_NearbyChattersDisplayDataList_807(ChatterDisplayData chatterdisplaydata, ChatterDisplayData chatterdisplaydata1)
    {
        int i = Float.compare(chatterdisplaydata.distanceToUser, chatterdisplaydata1.distanceToUser);
        if (i != 0)
        {
            return i;
        } else
        {
            return chatterdisplaydata.compareTo(chatterdisplaydata1);
        }
    }

    protected List getChatters()
    {
        Object obj = null;
        Object obj1 = userManager.getActiveAgentCircuit();
        List list = ((List) (obj));
        if (obj1 != null)
        {
            obj1 = ((SLAgentCircuit) (obj1)).getModules();
            list = ((List) (obj));
            if (obj1 != null)
            {
                list = ((SLModules) (obj1)).minimap.getNearbyChatterList();
            }
        }
        obj = list;
        if (list == null)
        {
            obj = ImmutableList.of();
        }
        return ((List) (obj));
    }
}
