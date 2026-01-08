package com.linkpoint.ui.chat.contacts

import android.content.Context
import android.widget.ListAdapter
import androidx.fragment.app.LoaderManager
import com.linkpoint.slproto.users.manager.ChatterListType
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.UserListFragment

class NearbyUsersFragment : UserListFragment() {
    override fun createListAdapter(
        context: Context,
        loaderManager: LoaderManager,
        userManager: UserManager,
    ): ListAdapter {
        return ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Nearby).apply {
            setUserDistanceInline(false)
        }
    }
}
