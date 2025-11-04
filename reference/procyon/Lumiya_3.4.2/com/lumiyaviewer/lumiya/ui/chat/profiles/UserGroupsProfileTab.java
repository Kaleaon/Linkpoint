// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.widget.TextView;
import android.content.Context;
import com.google.common.collect.ImmutableList;
import android.widget.BaseAdapter;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.widget.AdapterView$OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.os.Bundle;
import javax.annotation.Nullable;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.view.View;
import android.widget.AdapterView;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class UserGroupsProfileTab extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    private final SubscriptionData<UUID, AvatarGroupList> avatarGroups;
    private GroupsAdapter groupsAdapter;
    private final LoadableMonitor loadableMonitor;
    
    public UserGroupsProfileTab() {
        this.avatarGroups = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.avatarGroups }).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968759, viewGroup, false);
        this.groupsAdapter = new GroupsAdapter(layoutInflater.getContext(), null);
        ((ListView)inflate.findViewById(2131755707)).setAdapter((ListAdapter)this.groupsAdapter);
        ((ListView)inflate.findViewById(2131755707)).setOnItemClickListener((AdapterView$OnItemClickListener)new _$Lambda$929W_sYALf9zQuqLbMSJpktRAzI(this));
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296753), this.getString(2131297136));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        try {
            this.loadableMonitor.setEmptyMessage(this.avatarGroups.get().Groups.isEmpty(), this.getString(2131296737));
            if (this.groupsAdapter != null) {
                this.groupsAdapter.setData(this.avatarGroups.getData());
            }
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            Debug.Warning(ex);
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (chatterID instanceof ChatterID.ChatterIDUser) {
            final UserManager userManager = chatterID.getUserManager();
            if (userManager != null) {
                this.avatarGroups.subscribe(userManager.getAvatarGroupLists().getPool(), ((ChatterID.ChatterIDUser)chatterID).getChatterUUID());
            }
        }
    }
    
    private static class GroupsAdapter extends BaseAdapter
    {
        private ImmutableList<AvatarGroupList.AvatarGroupEntry> avatarGroupList;
        private final LayoutInflater inflater;
        
        private GroupsAdapter(final Context context) {
            this.avatarGroupList = null;
            this.inflater = LayoutInflater.from(context);
        }
        
        public int getCount() {
            int size;
            if (this.avatarGroupList != null) {
                size = this.avatarGroupList.size();
            }
            else {
                size = 0;
            }
            return size;
        }
        
        public AvatarGroupList.AvatarGroupEntry getItem(final int n) {
            if (this.avatarGroupList != null && n >= 0 && n < this.avatarGroupList.size()) {
                return this.avatarGroupList.get(n);
            }
            return null;
        }
        
        public long getItemId(final int n) {
            return n;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            View inflate = view;
            if (view == null) {
                inflate = this.inflater.inflate(17367043, viewGroup, false);
            }
            final AvatarGroupList.AvatarGroupEntry item = this.getItem(n);
            if (item != null) {
                ((TextView)inflate.findViewById(16908308)).setText((CharSequence)item.GroupName);
            }
            return inflate;
        }
        
        public boolean hasStableIds() {
            return false;
        }
        
        void setData(final AvatarGroupList list) {
            final ImmutableList.Builder<AvatarGroupList.AvatarGroupEntry> builder = new ImmutableList.Builder<AvatarGroupList.AvatarGroupEntry>();
            builder.addAll((Iterable<? extends AvatarGroupList.AvatarGroupEntry>)list.Groups.values());
            this.avatarGroupList = builder.build();
            this.notifyDataSetChanged();
        }
    }
}
