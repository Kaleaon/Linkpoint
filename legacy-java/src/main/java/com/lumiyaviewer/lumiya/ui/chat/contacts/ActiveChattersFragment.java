package com.lumiyaviewer.lumiya.ui.chat.contacts;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.UserListFragment;

/* loaded from: classes.dex */
public class ActiveChattersFragment extends UserListFragment {
    @Override // com.lumiyaviewer.lumiya.ui.common.UserListFragment
    protected ListAdapter createListAdapter(Context context, LoaderManager loaderManager, UserManager userManager) {
        return new ActiveChatsListAdapter(getActivity(), userManager);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.UserListFragment
    protected boolean itemsMayBeDismissed() {
        return true;
    }
}
