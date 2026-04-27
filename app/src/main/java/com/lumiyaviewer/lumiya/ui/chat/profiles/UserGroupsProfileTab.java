package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import java.util.UUID;
import javax.annotation.Nullable;

public class UserGroupsProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private final SubscriptionData<UUID, AvatarGroupList> avatarGroups = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private GroupsAdapter groupsAdapter;
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.avatarGroups).withDataChangedListener(this);

    private static class GroupsAdapter extends BaseAdapter {
        private ImmutableList<AvatarGroupList.AvatarGroupEntry> avatarGroupList;
        private final LayoutInflater inflater;

        private GroupsAdapter(Context context) {
            this.avatarGroupList = null;
            this.inflater = LayoutInflater.from(context);
        }

        /* synthetic */ GroupsAdapter(Context context, GroupsAdapter groupsAdapter) {
            this(context);
        }

        public int getCount() {
            if (this.avatarGroupList != null) {
                return this.avatarGroupList.size();
            }
            return 0;
        }

        public AvatarGroupList.AvatarGroupEntry getItem(int i) {
            if (this.avatarGroupList == null || i < 0 || i >= this.avatarGroupList.size()) {
                return null;
            }
            return (AvatarGroupList.AvatarGroupEntry) this.avatarGroupList.get(i);
        }

        public long getItemId(int i) {
            return (long) i;
        }

        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.inflater.inflate(17367043, viewGroup, false);
            }
            AvatarGroupList.AvatarGroupEntry item = getItem(i);
            if (item != null) {
                ((TextView) view.findViewById(16908308)).setText(item.GroupName);
            }
            return view;
        }

        public boolean hasStableIds() {
            return false;
        }

        /* access modifiers changed from: package-private */
        public void setData(AvatarGroupList avatarGroupList2) {
            ImmutableList.Builder builder = new ImmutableList.Builder();
            builder.addAll((Iterable) avatarGroupList2.Groups.values());
            this.avatarGroupList = builder.build();
            notifyDataSetChanged();
        }
    }

    /* access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserGroupsProfileTab_2041  reason: not valid java name */
    public /* synthetic */ void m508lambda$com_lumiyaviewer_lumiya_ui_chat_profiles_UserGroupsProfileTab_2041(AdapterView adapterView, View view, int i, long j) {
        Object item = adapterView.getAdapter().getItem(i);
        if ((item instanceof AvatarGroupList.AvatarGroupEntry) && (this.chatterID instanceof ChatterID.ChatterIDUser)) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(ChatterID.getGroupChatterID(this.chatterID.agentUUID, ((AvatarGroupList.AvatarGroupEntry) item).GroupID)));
        }
    }

    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.user_profile_tab_groups, viewGroup, false);
        this.groupsAdapter = new GroupsAdapter(layoutInflater.getContext(), (GroupsAdapter) null);
        ((ListView) inflate.findViewById(R.id.groups_list_view)).setAdapter(this.groupsAdapter);
        ((ListView) inflate.findViewById(R.id.groups_list_view)).setOnItemClickListener(new $Lambda$929W_sYALf9zQuqLbMSJpktRAzI(this));
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_user_selected), getString(R.string.user_picks_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    public void onLoadableDataChanged() {
        try {
            this.loadableMonitor.setEmptyMessage(this.avatarGroups.get().Groups.isEmpty(), getString(R.string.no_groups));
            if (this.groupsAdapter != null) {
                this.groupsAdapter.setData(this.avatarGroups.getData());
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    /* access modifiers changed from: protected */
    public void onShowUser(@Nullable ChatterID chatterID) {
        UserManager userManager;
        this.loadableMonitor.unsubscribeAll();
        if ((chatterID instanceof ChatterID.ChatterIDUser) && (userManager = chatterID.getUserManager()) != null) {
            this.avatarGroups.subscribe(userManager.getAvatarGroupLists().getPool(), ((ChatterID.ChatterIDUser) chatterID).getChatterUUID());
        }
    }
}
