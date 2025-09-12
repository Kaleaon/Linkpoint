// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.modules.groups;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.SLMessageEventListener;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.modules.groups:
//            SLGroupManager

class val.memberID
    implements SLMessageEventListener
{

    final SLGroupManager this$0;
    final UUID val$groupUUID;
    final boolean val$isMyRoles;
    final UUID val$memberID;

    public void onMessageAcknowledged(SLMessage slmessage)
    {
        SLGroupManager._2D_get4(SLGroupManager.this).getChatterList().getGroupManager().requestGroupRoleMembersRefresh(val$groupUUID);
        if (val$isMyRoles)
        {
            SLGroupManager._2D_get4(SLGroupManager.this).getCachedGroupProfiles().requestUpdate(val$groupUUID);
            SLGroupManager._2D_get4(SLGroupManager.this).getGroupTitles().requestUpdate(val$groupUUID);
            SLGroupManager._2D_get4(SLGroupManager.this).getAvatarGroupLists().requestUpdate(val$memberID);
        }
    }

    public void onMessageTimeout(SLMessage slmessage)
    {
    }

    ()
    {
        this$0 = final_slgroupmanager;
        val$groupUUID = uuid;
        val$isMyRoles = flag;
        val$memberID = UUID.this;
        super();
    }
}
