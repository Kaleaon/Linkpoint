package com.linkpoint.ui.chat.contacts;
import java.util.*;

import android.content.Context;
import android.support.v4.app.LoaderManager;
import android.widget.ListAdapter;
import com.linkpoint.slproto.users.manager.ChatterListType;
import com.linkpoint.slproto.users.manager.UserManager;
import com.linkpoint.ui.common.UserListFragment;

public class GroupListFragment extends UserListFragment {
    /* access modifiers changed from: protected */
    public ListAdapter createListAdapter(Context context, LoaderManager loaderManager, UserManager userManager) {
        return new ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Groups);
    }
}
