package com.lumiyaviewer.lumiya.ui.chat.profiles;

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
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class GroupRolesProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupProfile, this.groupRoles, this.myGroupList).withOptionalLoadables(this.groupTitles).withDataChangedListener(this);
    private GroupRoleAdapter adapter = null;

    /* loaded from: classes.dex */
    private class GroupRoleAdapter extends BaseAdapter {

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

        /* synthetic */ GroupRoleAdapter(GroupRolesProfileTab groupRolesProfileTab, GroupRoleAdapter groupRoleAdapter) {
            this();
        }

        @Override // android.widget.Adapter
        public int getCount() {
            if (this.data != null) {
                return this.data.RoleData_Fields.size();
            }
            return 0;
        }

        @Override // android.widget.Adapter
        public GroupRoleDataReply.RoleData getItem(int i) {
            if (this.data == null || i < 0 || i >= this.data.RoleData_Fields.size()) {
                return null;
            }
            return this.data.RoleData_Fields.get(i);
        }

        @Override // android.widget.Adapter
        public long getItemId(int i) {
            return i;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            boolean z;
            boolean z2;
            GroupTitlesReply.GroupData groupData;
            if (view == null) {
                view = LayoutInflater.from(GroupRolesProfileTab.this.getContext()).inflate(R.layout.group_profile_role_list_item, viewGroup, false);
            }
            GroupRoleDataReply.RoleData item = getItem(i);
            if (item != null) {
                int i2 = (!item.RoleID.equals(UUIDPool.ZeroUUID) || this.groupProfile == null) ? item.Members : this.groupProfile.GroupData_Field.GroupMembershipCount;
                ((TextView) view.findViewById(R.id.role_name)).setText(SLMessage.stringFromVariableOEM(item.Name));
                ((TextView) view.findViewById(R.id.role_member_count)).setText(GroupRolesProfileTab.this.getResources().getQuantityString(R.plurals.members, i2, Integer.valueOf(i2)));
                if (this.titlesByRole == null || (groupData = this.titlesByRole.get(item.RoleID)) == null) {
                    z = false;
                    z2 = false;
                } else {
                    z = groupData.Selected;
                    z2 = true;
                }
                view.findViewById(R.id.role_mine_check_mark).setVisibility(z2 ? 0 : 4);
                ((TextView) view.findViewById(R.id.role_name)).setTypeface(null, z ? 1 : 0);
            }
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return false;
        }

        public void setData(@Nullable GroupRoleDataReply groupRoleDataReply, @Nullable GroupTitlesReply groupTitlesReply, @Nullable GroupProfileReply groupProfileReply) {
            this.data = groupRoleDataReply;
            if (groupTitlesReply != null) {
                this.titlesByRole = new HashMap();
                for (GroupTitlesReply.GroupData groupData : groupTitlesReply.GroupData_Fields) {
                    this.titlesByRole.put(groupData.RoleID, groupData);
                }
            } else {
                this.titlesByRole = null;
            }
            this.groupProfile = groupProfileReply;
            notifyDataSetInvalidated();
        }
    }

    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
        } catch (SubscriptionData.DataNotReadyException e) {
            return null;
        }
    }

    private long getMyGroupPowers() {
        AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0L;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onAddNewRoleButton, reason: merged with bridge method [inline-methods] */
    public void m507x6f5149fc(View view) {
        if ((getMyGroupPowers() & 16) != 0) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleDetailsFragment.class, GroupRoleDetailsFragment.makeSelection(this.chatterID, null));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTab_2802, reason: not valid java name */
    public /* synthetic */ void m508x3dfd52a4(AdapterView adapterView, View view, int i, long j) {
        GroupRoleDataReply.RoleData item;
        if (this.adapter == null || (item = this.adapter.getItem(i)) == null) {
            return;
        }
        DetailsActivity.showEmbeddedDetails(getActivity(), GroupRoleDetailsFragment.class, GroupRoleDetailsFragment.makeSelection(this.chatterID, item.RoleID));
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        GroupRoleAdapter groupRoleAdapter = null;
        View inflate = layoutInflater.inflate(R.layout.group_profile_tab_roles, viewGroup, false);
        if (this.adapter == null) {
            this.adapter = new GroupRoleAdapter(this, groupRoleAdapter);
        }
        ((ListView) inflate.findViewById(R.id.group_profile_roles_list)).setAdapter((ListAdapter) this.adapter);
        ((ListView) inflate.findViewById(R.id.group_profile_roles_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU.1
            private final /* synthetic */ void $m$0(AdapterView adapterView, View view, int i, long j) {
                ((GroupRolesProfileTab) this).m508x3dfd52a4(adapterView, view, i, j);
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                $m$0(adapterView, view, i, j);
            }
        });
        inflate.findViewById(R.id.add_new_role_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$zWKNEqUupU__bUM7E0seQ8xMgmU
            private final /* synthetic */ void $m$0(View view) {
                ((GroupRolesProfileTab) this).m507x6f5149fc(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        try {
            if (this.myGroupList.get().Groups.get(this.groupRoles.get().GroupData_Field.GroupID) != null && !this.groupTitles.isSubscribed()) {
                this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        long myGroupPowers = getMyGroupPowers();
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.add_new_role_button).setVisibility((myGroupPowers & 16) != 0 ? 0 : 8);
        }
        if (this.adapter != null) {
            this.adapter.setData(this.groupRoles.getData(), this.groupTitles.getData(), this.groupProfile.getData());
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDGroup)) {
            if (this.adapter != null) {
                this.adapter.setData(null, null, null);
            }
        } else {
            UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        }
    }
}
