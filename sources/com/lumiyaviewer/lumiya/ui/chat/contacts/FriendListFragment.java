// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.UserListFragment;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.contacts:
//            ChatterListSubscriptionAdapter

public class FriendListFragment extends UserListFragment
{

    public FriendListFragment()
    {
    }

    protected ListAdapter createListAdapter(Context context, LoaderManager loadermanager, UserManager usermanager)
    {
        return new ChatterListSubscriptionAdapter(context, usermanager, ChatterListType.Friends);
    }
}
