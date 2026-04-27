package com.linkpoint.ui.chat.contacts
import java.util.*

import android.content.Context
import android.support.v4.app.LoaderManager
import android.widget.ListAdapter
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.UserListFragment

class ActiveChattersFragment : UserListFragment() {
    /* access modifiers changed from: protected */
     public fun createListAdapter(context: Context, loaderManager: LoaderManager, userManager: UserManager): ListAdapter {
        return ActiveChatsListAdapter(getActivity(), userManager)
    }

    /* access modifiers changed from: protected */
     public fun itemsMayBeDismissed(): Boolean {
        return true
    }
}
