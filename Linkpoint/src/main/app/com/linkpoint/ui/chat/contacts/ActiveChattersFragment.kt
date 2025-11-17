package com.linkpoint.ui.chat.contacts

import android.content.Context
import android.widget.ListAdapter
import androidx.fragment.app.LoaderManager
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.UserListFragment

class ActiveChattersFragment : UserListFragment() {
    override fun createListAdapter(
        context: Context,
        loaderManager: LoaderManager,
        userManager: UserManager,
    ): ListAdapter {
        return ActiveChatsListAdapter(requireActivity(), userManager)
    }

    override fun itemsMayBeDismissed(): Boolean {
        return true
    }
}
