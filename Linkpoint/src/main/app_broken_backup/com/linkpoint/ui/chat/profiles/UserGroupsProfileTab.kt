package com.linkpoint.ui.chat.profiles

import android.content.Context
import android.os.Bundle
import androidx.core.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.common.collect.ImmutableList
import com.linkpoint.Debug
import com.linkpoint.R
import com.linkpoint.react.SubscriptionData
import com.linkpoint.react.UIThreadExecutor
import com.linkpoint.slproto.modules.groups.AvatarGroupList
import com.linkpoint.slproto.users.ChatterID
import com.linkpoint.slproto.users.manager.UserManager
import com.linkpoint.ui.common.ChatterReloadableFragment
import com.linkpoint.ui.common.DetailsActivity
import com.linkpoint.ui.common.LoadingLayout
import com.linkpoint.ui.common.loadmon.LoadableMonitor
import java.util.UUID
import androidx.annotation.Nullable

class UserGroupsProfileTab : ChatterReloadableFragment : LoadableMonitor.OnLoadableDataChangedListener {
    private SubscriptionData<UUID, AvatarGroupList> avatarGroups = SubscriptionData<>(UIThreadExecutor.getInstance())
    private GroupsAdapter groupsAdapter
    private LoadableMonitor loadableMonitor = LoadableMonitor(this.avatarGroups).withDataChangedListener(this)

    private class GroupsAdapter : BaseAdapter {
        private ImmutableList<AvatarGroupList.AvatarGroupEntry> avatarGroupList
        private LayoutInflater inflater

        private GroupsAdapter(Context context) {
            this.avatarGroupList = null
            this.inflater = LayoutInflater.from(context)
        }

        /* synthetic */ GroupsAdapter(Context context, GroupsAdapter groupsAdapter) {
            this(context)
        }

        fun getCount(): Int {
            if (this.avatarGroupList != null) {
                return this.avatarGroupList.size()
            }
            return 0
        }

        AvatarGroupList.AvatarGroupEntry getItem(Int i) {
            if (this.avatarGroupList == null || i < 0 || i >= this.avatarGroupList.size()) {
                return null
            }
            return (AvatarGroupList.AvatarGroupEntry) this.avatarGroupList.get(i)
        }

        fun getItemId(Int i): Long {
            return (Long) i
        }

        fun getView(Int i, View view, ViewGroup viewGroup): View {
            if (view == null) {
                view = this.inflater.inflate(17367043, viewGroup, false)
            }
            AvatarGroupList.AvatarGroupEntry item = getItem(i)
            if (item != null) {
                ((view as TextView).findViewById(16908308)).setText(item.GroupName)
            }
            return view
        }

        fun hasStableIds(): Boolean {
            return false
        }

        /* access modifiers changed from: package-private */
        fun setData(AvatarGroupList avatarGroupList2)  {
            ImmutableList.Builder builder = ImmutableList.Builder()
            builder.addAll((avatarGroupList2 as Iterable).Groups.values())
            this.avatarGroupList = builder.build()
            notifyDataSetChanged()
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserGroupsProfileTab_2041  reason: not valid java name */
    /* synthetic */ Unit m508lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_UserGroupsProfileTab_2041(AdapterView adapterView, View view, Int i, Long j) {
        Any item = adapterView.getAdapter().getItem(i)
        if ((item is AvatarGroupList.AvatarGroupEntry) && (this.chatterID is ChatterID.ChatterIDUser)) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(ChatterID.getGroupChatterID(this.chatterID.agentUUID, ((AvatarGroupList.AvatarGroupEntry) item).GroupID)))
        }
    }

    @Nullable
    fun onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle): View {
        View inflate = layoutInflater.inflate(R.layout.user_profile_tab_groups, viewGroup, false)
        this.groupsAdapter = GroupsAdapter(layoutInflater.getContext(), (GroupsAdapter) null)
        ((inflate as ListView).findViewById(R.id.groups_list_view)).setAdapter(this.groupsAdapter)
        ((inflate as ListView).findViewById(R.id.groups_list_view)).setOnItemClickListener($Lambda$929W_sYALf9zQuqLbMSJpktRAzI(this))
        ((inflate as LoadingLayout).findViewById(R.id.loading_layout)).setSwipeRefreshLayout((inflate as SwipeRefreshLayout).findViewById(R.id.swipe_refresh_layout))
        this.loadableMonitor.setLoadingLayout((inflate as LoadingLayout).findViewById(R.id.loading_layout), getString(R.string.no_user_selected), getString(R.string.user_picks_fail))
        this.loadableMonitor.setSwipeRefreshLayout((inflate as SwipeRefreshLayout).findViewById(R.id.swipe_refresh_layout))
        return inflate
    }

    fun onLoadableDataChanged()  {
        try {
            this.loadableMonitor.setEmptyMessage(this.avatarGroups.get().Groups.isEmpty(), getString(R.string.no_groups))
            if (this.groupsAdapter != null) {
                this.groupsAdapter.setData(this.avatarGroups.getData())
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e)
        }
    }

    /* access modifiers changed from: protected */
    fun onShowUser(@Nullable ChatterID chatterID)  {
        UserManager userManager
        this.loadableMonitor.unsubscribeAll()
        if ((chatterID is ChatterID.ChatterIDUser) && (userManager = chatterID.getUserManager()) != null) {
            this.avatarGroups.subscribe(userManager.getAvatarGroupLists().getPool(), ((ChatterID.ChatterIDUser) chatterID).getChatterUUID())
        }
    }
}
