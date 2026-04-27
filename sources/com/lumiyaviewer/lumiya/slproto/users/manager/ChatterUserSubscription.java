// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.GlobalOptions;
import com.lumiyaviewer.lumiya.dao.UserName;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterSubscription, UserManager, ChatterList, FriendManager, 
//            ChatterDisplayData, SortedChatterList

class ChatterUserSubscription extends ChatterSubscription
{

    private final Subscription distanceSubscription;
    private final Subscription nameSubscription;
    private final Subscription onlineStatusSubscription;

    ChatterUserSubscription(SortedChatterList sortedchatterlist, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, UserManager usermanager)
    {
        super(sortedchatterlist, chatteriduser, usermanager);
        nameSubscription = usermanager.getUserNames().subscribe(chatteriduser.getChatterUUID(), new _2D_.Lambda.o86h7H3WuAxnvPtFprunJr0Jq8o(this));
        onlineStatusSubscription = usermanager.getChatterList().getFriendManager().getOnlineStatus().subscribe(chatteriduser.getChatterUUID(), new _2D_.Lambda.o86h7H3WuAxnvPtFprunJr0Jq8o._cls1(this));
        distanceSubscription = usermanager.getChatterList().getDistanceToUser().subscribe(chatteriduser.getChatterUUID(), new _2D_.Lambda.o86h7H3WuAxnvPtFprunJr0Jq8o._cls2(this));
    }

    private void onDistance(Float float1)
    {
        if (float1 != null)
        {
            float f = float1.floatValue();
            if (Float.compare(f, displayData.distanceToUser) != 0)
            {
                setChatterDisplayData(displayData.withDistanceToUser(f));
            }
        }
    }

    private void onOnlineStatus(Boolean boolean1)
    {
        if (boolean1 != null && displayData.isOnline != boolean1.booleanValue())
        {
            setChatterDisplayData(displayData.withOnlineStatus(boolean1.booleanValue()));
        }
    }

    private void onUserName(UserName username)
    {
        if (GlobalOptions.getInstance().isLegacyUserNames())
        {
            username = username.getUserName();
        } else
        {
            username = username.getDisplayName();
        }
        if (!Objects.equal(username, displayData.displayName))
        {
            setChatterDisplayData(displayData.withDisplayName(username));
        }
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_ChatterUserSubscription_2D_mthref_2D_0(UserName username)
    {
        onUserName(username);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_ChatterUserSubscription_2D_mthref_2D_1(Boolean boolean1)
    {
        onOnlineStatus(boolean1);
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_ChatterUserSubscription_2D_mthref_2D_2(Float float1)
    {
        onDistance(float1);
    }

    public void unsubscribe()
    {
        nameSubscription.unsubscribe();
        onlineStatusSubscription.unsubscribe();
        distanceSubscription.unsubscribe();
        super.unsubscribe();
    }
}
