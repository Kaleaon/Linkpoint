package com.lumiyaviewer.lumiya.ui.chat.contacts;
import java.util.*;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.widget.ListAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.UserListFragment;

public class GroupListFragment extends UserListFragment {
    /* access modifiers changed from: protected */
    public ListAdapter createListAdapter(Context context, LoaderManager loaderManager, UserManager userManager) {
        return new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Groups);
    }
}
