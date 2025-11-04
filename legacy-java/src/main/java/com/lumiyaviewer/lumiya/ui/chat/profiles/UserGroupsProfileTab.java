package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
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

/* loaded from: classes.dex */
public class UserGroupsProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private GroupsAdapter groupsAdapter;
    private final SubscriptionData<UUID, AvatarGroupList> avatarGroups = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.avatarGroups).withDataChangedListener(this);

    /* loaded from: classes.dex */
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

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.avatarGroupList != null) {
                return this.avatarGroupList.size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public AvatarGroupList.AvatarGroupEntry getItem(int i) {
            if (this.avatarGroupList == null || i < 0 || i >= this.avatarGroupList.size()) {
                return null;
            }
            return this.avatarGroupList.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = this.inflater.inflate(R.layout.simple_list_item_1, viewGroup, false);
            }
            AvatarGroupList.AvatarGroupEntry item = getItem(i);
            if (item != null) {
                ((TextView) view.findViewById(R.id.text1)).setText(item.GroupName);
            }
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return false;
        }

        void setData(AvatarGroupList avatarGroupList) {
            ImmutableList.Builder builder = new ImmutableList.Builder();
            builder.addAll((Iterable) avatarGroupList.Groups.values());
            this.avatarGroupList = builder.build();
            notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_UserGroupsProfileTab_2041, reason: not valid java name */
    public /* synthetic */ void m518x4b4ac4f6(AdapterView adapterView, View view, int i, long j) {
        Object item = adapterView.getAdapter().getItem(i);
        if ((item instanceof AvatarGroupList.AvatarGroupEntry) && (this.chatterID instanceof ChatterID.ChatterIDUser)) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupProfileFragment.class, GroupProfileFragment.makeSelection(ChatterID.getGroupChatterID(this.chatterID.agentUUID, ((AvatarGroupList.AvatarGroupEntry) item).GroupID)));
        }
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(com.lumiyaviewer.lumiya.R.layout.user_profile_tab_groups, viewGroup, false);
        this.groupsAdapter = new GroupsAdapter(layoutInflater.getContext(), null);
        ((ListView) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.groups_list_view)).setAdapter((ListAdapter) this.groupsAdapter);
        ((ListView) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.groups_list_view)).setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$929W_sYALf9zQuqLbMSJpktRAzI
            private final /* synthetic */ void $m$0(AdapterView adapterView, View view, int i, long j) {
                ((UserGroupsProfileTab) this).m518x4b4ac4f6(adapterView, view, i, j);
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                $m$0(adapterView, view, i, j);
            }
        });
        ((LoadingLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.loading_layout), getString(com.lumiyaviewer.lumiya.R.string.no_user_selected), getString(com.lumiyaviewer.lumiya.R.string.user_picks_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(com.lumiyaviewer.lumiya.R.id.swipe_refresh_layout));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        try {
            this.loadableMonitor.setEmptyMessage(this.avatarGroups.get().Groups.isEmpty(), getString(com.lumiyaviewer.lumiya.R.string.no_groups));
            if (this.groupsAdapter != null) {
                this.groupsAdapter.setData(this.avatarGroups.getData());
            }
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        UserManager userManager;
        this.loadableMonitor.unsubscribeAll();
        if (!(chatterID instanceof ChatterID.ChatterIDUser) || (userManager = chatterID.getUserManager()) == null) {
            return;
        }
        this.avatarGroups.subscribe(userManager.getAvatarGroupLists().getPool(), ((ChatterID.ChatterIDUser) chatterID).getChatterUUID());
    }
}
