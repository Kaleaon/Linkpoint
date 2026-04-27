// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterSubscription, UserManager, ChatterDisplayData, SortedChatterList

class ChatterGroupSubscription extends ChatterSubscription
{

    private final Subscription groupProfileSubscription;

    ChatterGroupSubscription(SortedChatterList sortedchatterlist, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup chatteridgroup, UserManager usermanager)
    {
        super(sortedchatterlist, chatteridgroup, usermanager);
        groupProfileSubscription = usermanager.getCachedGroupProfiles().getPool().subscribe(chatteridgroup.getChatterUUID(), new _2D_.Lambda.eTv5Cj2a9ssR4ZBNRV1Lgb181AY(this));
    }

    private void onGroupProfile(GroupProfileReply groupprofilereply)
    {
        groupprofilereply = SLMessage.stringFromVariableOEM(groupprofilereply.GroupData_Field.Name);
        if (!Objects.equal(groupprofilereply, displayData.displayName))
        {
            setChatterDisplayData(displayData.withDisplayName(groupprofilereply));
        }
    }

    void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_ChatterGroupSubscription_2D_mthref_2D_0(GroupProfileReply groupprofilereply)
    {
        onGroupProfile(groupprofilereply);
    }

    public void unsubscribe()
    {
        groupProfileSubscription.unsubscribe();
        super.unsubscribe();
    }
}
