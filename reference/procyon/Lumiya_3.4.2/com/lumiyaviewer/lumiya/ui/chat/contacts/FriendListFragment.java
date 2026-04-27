// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.contacts;

import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v4.app.LoaderManager;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.common.UserListFragment;

public class FriendListFragment extends UserListFragment
{
    @Override
    protected ListAdapter createListAdapter(final Context context, final LoaderManager loaderManager, final UserManager userManager) {
        return (ListAdapter)new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Friends);
    }
}
