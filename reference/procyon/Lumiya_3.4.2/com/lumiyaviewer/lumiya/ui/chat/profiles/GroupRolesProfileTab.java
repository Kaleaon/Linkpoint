// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import java.util.Iterator;
import java.util.HashMap;
import android.graphics.Typeface;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.widget.TextView;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Map;
import android.widget.BaseAdapter;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.View$OnClickListener;
import android.widget.AdapterView$OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.view.View;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class GroupRolesProfileTab extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    private GroupRoleAdapter adapter;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile;
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles;
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList;
    
    public GroupRolesProfileTab() {
        this.groupProfile = new SubscriptionData<UUID, GroupProfileReply>(UIThreadExecutor.getInstance());
        this.groupRoles = new SubscriptionData<UUID, GroupRoleDataReply>(UIThreadExecutor.getInstance());
        this.myGroupList = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.groupTitles = new SubscriptionData<UUID, GroupTitlesReply>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.groupProfile, this.groupRoles, this.myGroupList }).withOptionalLoadables(this.groupTitles).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.adapter = null;
    }
    
    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return (AvatarGroupList.AvatarGroupEntry)this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
        }
        catch (final SubscriptionData.DataNotReadyException ex) {
            return null;
        }
    }
    
    private long getMyGroupPowers() {
        final AvatarGroupList.AvatarGroupEntry myGroupEntry = this.getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0L;
    }
    
    private void onAddNewRoleButton(final View view) {
        if ((this.getMyGroupPowers() & 0x10L) != 0x0L) {
            DetailsActivity.showEmbeddedDetails(this.getActivity(), GroupRoleDetailsFragment.class, GroupRoleDetailsFragment.makeSelection(this.chatterID, null));
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968651, viewGroup, false);
        if (this.adapter == null) {
            this.adapter = new GroupRoleAdapter((GroupRoleAdapter)null);
        }
        ((ListView)inflate.findViewById(2131755423)).setAdapter((ListAdapter)this.adapter);
        ((ListView)inflate.findViewById(2131755423)).setOnItemClickListener((AdapterView$OnItemClickListener)new _$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU$1(this));
        inflate.findViewById(2131755424).setOnClickListener((View$OnClickListener)new _$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU(this));
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296736), this.getString(2131296593));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        while (true) {
            try {
                if (this.myGroupList.get().Groups.get(this.groupRoles.get().GroupData_Field.GroupID) != null && !this.groupTitles.isSubscribed()) {
                    this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID);
                }
                final long myGroupPowers = this.getMyGroupPowers();
                final View view = this.getView();
                if (view != null) {
                    final View viewById = view.findViewById(2131755424);
                    int visibility;
                    if ((myGroupPowers & 0x10L) != 0x0L) {
                        visibility = 0;
                    }
                    else {
                        visibility = 8;
                    }
                    viewById.setVisibility(visibility);
                }
                if (this.adapter != null) {
                    this.adapter.setData(this.groupRoles.getData(), this.groupTitles.getData(), this.groupProfile.getData());
                }
            }
            catch (final SubscriptionData.DataNotReadyException ex) {
                continue;
            }
            break;
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDGroup) {
            final UUID chatterUUID = ((ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        }
        else if (this.adapter != null) {
            this.adapter.setData(null, null, null);
        }
    }
    
    private class GroupRoleAdapter extends BaseAdapter
    {
        @Nullable
        private GroupRoleDataReply data;
        @Nullable
        private GroupProfileReply groupProfile;
        @Nullable
        private Map<UUID, GroupTitlesReply.GroupData> titlesByRole;
        
        private GroupRoleAdapter() {
            this.groupProfile = null;
            this.data = null;
            this.titlesByRole = null;
        }
        
        public int getCount() {
            int size;
            if (this.data != null) {
                size = this.data.RoleData_Fields.size();
            }
            else {
                size = 0;
            }
            return size;
        }
        
        public GroupRoleDataReply.RoleData getItem(final int index) {
            if (this.data != null && index >= 0 && index < this.data.RoleData_Fields.size()) {
                return (GroupRoleDataReply.RoleData)this.data.RoleData_Fields.get(index);
            }
            return null;
        }
        
        public long getItemId(final int n) {
            return n;
        }
        
        public View getView(int n, View viewById, final ViewGroup viewGroup) {
            final int n2 = 1;
            View inflate = viewById;
            if (viewById == null) {
                inflate = LayoutInflater.from(GroupRolesProfileTab.this.getContext()).inflate(2130968647, viewGroup, false);
            }
            final GroupRoleDataReply.RoleData item = this.getItem(n);
            if (item != null) {
                n = item.Members;
                if (item.RoleID.equals(UUIDPool.ZeroUUID) && this.groupProfile != null) {
                    n = this.groupProfile.GroupData_Field.GroupMembershipCount;
                }
                ((TextView)inflate.findViewById(2131755389)).setText((CharSequence)SLMessage.stringFromVariableOEM(item.Name));
                ((TextView)inflate.findViewById(2131755390)).setText((CharSequence)GroupRolesProfileTab.this.getResources().getQuantityString(2131820545, n, new Object[] { n }));
                while (true) {
                    Label_0228: {
                        if (this.titlesByRole == null) {
                            break Label_0228;
                        }
                        final GroupTitlesReply.GroupData groupData = this.titlesByRole.get(item.RoleID);
                        if (groupData == null) {
                            break Label_0228;
                        }
                        final int selected = groupData.Selected ? 1 : 0;
                        n = 1;
                        viewById = inflate.findViewById(2131755388);
                        if (n != 0) {
                            n = 0;
                        }
                        else {
                            n = 4;
                        }
                        viewById.setVisibility(n);
                        final TextView textView = (TextView)inflate.findViewById(2131755389);
                        if (selected != 0) {
                            n = n2;
                        }
                        else {
                            n = 0;
                        }
                        textView.setTypeface((Typeface)null, n);
                        return inflate;
                    }
                    final int selected = 0;
                    n = 0;
                    continue;
                }
            }
            return inflate;
        }
        
        public boolean hasStableIds() {
            return false;
        }
        
        public void setData(@Nullable final GroupRoleDataReply data, @Nullable final GroupTitlesReply groupTitlesReply, @Nullable final GroupProfileReply groupProfile) {
            this.data = data;
            if (groupTitlesReply != null) {
                this.titlesByRole = new HashMap<UUID, GroupTitlesReply.GroupData>();
                for (final GroupTitlesReply.GroupData groupData : groupTitlesReply.GroupData_Fields) {
                    this.titlesByRole.put(groupData.RoleID, groupData);
                }
            }
            else {
                this.titlesByRole = null;
            }
            this.groupProfile = groupProfile;
            this.notifyDataSetInvalidated();
        }
    }
}
