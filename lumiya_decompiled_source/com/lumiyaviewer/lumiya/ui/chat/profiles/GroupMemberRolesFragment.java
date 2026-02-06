package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class GroupMemberRolesFragment extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener, BackButtonHandler {
    private static final String MEMBER_ID_KEY = "memberID";
    private MenuItem undoMenuItem;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, UUID> groupRoleMemberList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.3
        private final /* synthetic */ void $m$0(Object obj) {
            ((GroupMemberRolesFragment) this).m470xd12733d2((UUID) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<GroupManager.GroupMemberRolesQuery, Set<UUID>> activeRoles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupRoles, this.groupProfile, this.myGroupList, this.groupRoleMemberList, this.activeRoles).withOptionalLoadables(this.agentCircuit, this.groupTitles).withDataChangedListener(this);
    @Nullable
    private UUID MemberID = null;
    @Nullable
    private ChatterNameRetriever memberNameRetriever = null;
    private MemberRoleAdapter adapter = null;
    private boolean hasChanged = false;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MemberRoleAdapter extends BaseAdapter {
        @Nullable
        private GroupRoleDataReply data;
        private final Set<UUID> selectedRoles;

        private MemberRoleAdapter() {
            this.data = null;
            this.selectedRoles = new HashSet();
        }

        /* synthetic */ MemberRoleAdapter(GroupMemberRolesFragment groupMemberRolesFragment, MemberRoleAdapter memberRoleAdapter) {
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

        public Set<UUID> getSelectedRoles() {
            return this.selectedRoles;
        }

        @Override // android.widget.Adapter
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = LayoutInflater.from(GroupMemberRolesFragment.this.getContext()).inflate(R.layout.group_member_role_list_item, viewGroup, false);
            }
            GroupRoleDataReply.RoleData item = getItem(i);
            if (item != null) {
                ((CheckedTextView) view.findViewById(R.id.role_name_checked_text)).setText(SLMessage.stringFromVariableOEM(item.Name));
                ((CheckedTextView) view.findViewById(R.id.role_name_checked_text)).setChecked(!item.RoleID.equals(UUIDPool.ZeroUUID) ? this.selectedRoles.contains(item.RoleID) : true);
            }
            return view;
        }

        @Override // android.widget.BaseAdapter, android.widget.Adapter
        public boolean hasStableIds() {
            return false;
        }

        public void setData(@Nullable GroupRoleDataReply groupRoleDataReply, Set<UUID> set) {
            this.data = groupRoleDataReply;
            this.selectedRoles.clear();
            if (set != null) {
                this.selectedRoles.addAll(set);
            }
            GroupMemberRolesFragment.this.updateUnsavedChanges();
            notifyDataSetInvalidated();
        }

        public void toggleChecked(UUID uuid) {
            boolean z;
            boolean z2;
            GroupTitlesReply groupTitlesReply;
            if (uuid.equals(UUIDPool.ZeroUUID) || GroupMemberRolesFragment.this.userManager == null || GroupMemberRolesFragment.this.MemberID == null) {
                return;
            }
            long myGroupPowers = GroupMemberRolesFragment.this.getMyGroupPowers();
            try {
                boolean contains = ((Set) GroupMemberRolesFragment.this.activeRoles.get()).contains(uuid);
                boolean z3 = !this.selectedRoles.contains(uuid);
                if (contains == z3) {
                    if (z3) {
                        this.selectedRoles.add(uuid);
                    } else {
                        this.selectedRoles.remove(uuid);
                    }
                    z = true;
                } else {
                    if (z3) {
                        if ((256 & myGroupPowers) != 0) {
                            z2 = true;
                        } else {
                            if ((myGroupPowers & 128) != 0 && (groupTitlesReply = (GroupTitlesReply) GroupMemberRolesFragment.this.groupTitles.getData()) != null) {
                                for (GroupTitlesReply.GroupData groupData : groupTitlesReply.GroupData_Fields) {
                                    if (groupData.RoleID.equals(uuid)) {
                                        z2 = true;
                                        break;
                                    }
                                }
                            }
                            z2 = false;
                        }
                        if (z2) {
                            this.selectedRoles.add(uuid);
                            z = true;
                        }
                    } else if ((myGroupPowers & 512) != 0) {
                        boolean equals = uuid.equals(((GroupProfileReply) GroupMemberRolesFragment.this.groupProfile.get()).GroupData_Field.OwnerRole);
                        boolean equals2 = GroupMemberRolesFragment.this.userManager.getUserID().equals(GroupMemberRolesFragment.this.MemberID);
                        if (!equals || equals2) {
                            this.selectedRoles.remove(uuid);
                            z = true;
                        }
                    }
                    z = false;
                }
            } catch (SubscriptionData.DataNotReadyException e) {
                z = false;
            }
            if (z) {
                GroupMemberRolesFragment.this.updateUnsavedChanges();
                notifyDataSetChanged();
            }
        }
    }

    private boolean anyChanges() {
        Set<UUID> data = this.activeRoles.getData();
        if (this.adapter == null || data == null) {
            return false;
        }
        return !data.equals(this.adapter.getSelectedRoles());
    }

    private void closeFragment() {
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
            try {
                return this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID());
            } catch (SubscriptionData.DataNotReadyException e) {
                return null;
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getMyGroupPowers() {
        AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0L;
    }

    public static Bundle makeSelection(ChatterID chatterID, UUID uuid) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            makeSelection.putString(MEMBER_ID_KEY, uuid.toString());
        }
        return makeSelection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onGroupRoleMemberList */
    public void m470xd12733d2(UUID uuid) {
        if (this.userManager == null || !(this.chatterID instanceof ChatterID.ChatterIDGroup) || this.MemberID == null) {
            return;
        }
        this.activeRoles.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMemberRoleList(), GroupManager.GroupMemberRolesQuery.create(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.MemberID, uuid));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onMemberNameUpdated */
    public void m471xd12733d3(ChatterNameRetriever chatterNameRetriever) {
        String resolvedName = chatterNameRetriever.getResolvedName();
        if (Strings.isNullOrEmpty(resolvedName)) {
            setTitle(getString(R.string.name_loading_title), null);
        } else {
            setTitle(getString(R.string.member_roles_title_format, resolvedName), null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUnsavedChanges() {
        boolean anyChanges = anyChanges();
        if (anyChanges != this.hasChanged) {
            this.hasChanged = anyChanges;
            if (this.undoMenuItem != null) {
                this.undoMenuItem.setVisible(this.hasChanged);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_10425  reason: not valid java name */
    public /* synthetic */ void m472x25600606(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        closeFragment();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_4191  reason: not valid java name */
    public /* synthetic */ void m473x6c90dd87(AdapterView adapterView, View view, int i, long j) {
        GroupRoleDataReply.RoleData item;
        if (this.adapter == null || (item = this.adapter.getItem(i)) == null) {
            return;
        }
        this.adapter.toggleChecked(item.RoleID);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_9245  reason: not valid java name */
    public /* synthetic */ void m474x6c93268c(DialogInterface dialogInterface, int i) {
        dialogInterface.dismiss();
        try {
            if (this.adapter != null && this.userManager != null && (this.chatterID instanceof ChatterID.ChatterIDGroup)) {
                Set<UUID> selectedRoles = this.adapter.getSelectedRoles();
                Set<UUID> set = this.activeRoles.get();
                HashSet hashSet = new HashSet(selectedRoles);
                hashSet.removeAll(set);
                HashSet hashSet2 = new HashSet(set);
                hashSet2.removeAll(selectedRoles);
                this.agentCircuit.get().getModules().groupManager.RequestMemberRoleChanges(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.MemberID, hashSet, hashSet2);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
        closeFragment();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.BackButtonHandler
    public boolean onBackButtonPressed() {
        if (anyChanges()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage(getString(R.string.save_changes_question)).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((GroupMemberRolesFragment) this).m474x6c93268c(dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.1
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((GroupMemberRolesFragment) this).m472x25600606(dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            });
            builder.create().show();
            return true;
        }
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.FragmentWithTitle, android.support.v4.app.Fragment
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        setShowChatterTitle(false);
    }

    @Override // android.support.v4.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(R.menu.group_member_roles_menu, menu);
        this.undoMenuItem = menu.findItem(R.id.item_undo);
        this.undoMenuItem.setVisible(this.hasChanged);
    }

    @Override // android.support.v4.app.Fragment
    @Nullable
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_member_roles, viewGroup, false);
        if (this.adapter == null) {
            this.adapter = new MemberRoleAdapter(this, null);
        }
        ((ListView) inflate.findViewById(R.id.member_roles_list)).setAdapter((ListAdapter) this.adapter);
        ((ListView) inflate.findViewById(R.id.member_roles_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.2
            private final /* synthetic */ void $m$0(AdapterView adapterView, View view, int i, long j) {
                ((GroupMemberRolesFragment) this).m473x6c90dd87(adapterView, view, i, j);
            }

            @Override // android.widget.AdapterView.OnItemClickListener
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                $m$0(adapterView, view, i, j);
            }
        });
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        if (this.adapter != null) {
            this.adapter.setData(this.groupRoles.getData(), this.activeRoles.getData());
        }
        try {
            if (this.userManager != null) {
                AvatarGroupList.AvatarGroupEntry myGroupEntry = getMyGroupEntry();
                if (myGroupEntry == null) {
                    Debug.Printf("GroupMemberRoles: not my group", new Object[0]);
                    return;
                }
                Debug.Printf("GroupMemberRoles: my group powers are 0x%x", Long.valueOf(myGroupEntry.GroupPowers));
                if (this.groupTitles.isSubscribed()) {
                    return;
                }
                this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID);
            }
        } catch (SubscriptionData.DataNotReadyException e) {
        }
    }

    @Override // android.support.v4.app.Fragment
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.item_undo /* 2131755778 */:
                try {
                    if (this.adapter != null) {
                        this.adapter.setData(this.groupRoles.get(), this.activeRoles.get());
                        break;
                    }
                } catch (SubscriptionData.DataNotReadyException e) {
                    break;
                }
                break;
        }
        return super.onOptionsItemSelected(menuItem);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.memberNameRetriever != null) {
            this.memberNameRetriever.dispose();
            this.memberNameRetriever = null;
        }
        this.MemberID = UUIDPool.getUUID(getArguments().getString(MEMBER_ID_KEY));
        setTitle(getString(R.string.member_roles_title_default), null);
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDGroup)) {
            if (this.adapter != null) {
                this.adapter.setData(null, null);
                return;
            }
            return;
        }
        if (this.MemberID != null) {
            this.memberNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(this.userManager.getUserID(), this.MemberID), new ChatterNameRetriever.OnChatterNameUpdated() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$jWSiK5iq-zZfaogto6grdML6fzQ.4
                private final /* synthetic */ void $m$0(ChatterNameRetriever chatterNameRetriever) {
                    ((GroupMemberRolesFragment) this).m471xd12733d3(chatterNameRetriever);
                }

                @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
                public final void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
                    $m$0(chatterNameRetriever);
                }
            }, UIThreadExecutor.getInstance());
        }
        UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
        this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
        this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
        this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
        this.groupRoleMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMembers(), chatterUUID);
    }
}
