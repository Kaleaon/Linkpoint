package com.lumiyaviewer.lumiya.ui.chat.contacts

import android.content.Context
import android.widget.ListAdapter
import androidx.fragment.app.LoaderManager
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterListType
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager
import com.lumiyaviewer.lumiya.ui.common.UserListFragment

class NearbyUsersFragment : UserListFragment() {
    
    override fun createListAdapter(
        context: Context,
        loaderManager: LoaderManager,
        userManager: UserManager
    ): ListAdapter {
        return ChatterListSubscriptionAdapter(context, userManager, ChatterListType.Nearby).apply {
            setUserDistanceInline(false)
        }
    }
}