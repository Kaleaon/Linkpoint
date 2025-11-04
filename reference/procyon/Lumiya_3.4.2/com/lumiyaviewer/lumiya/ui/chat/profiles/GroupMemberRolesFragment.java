// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import java.util.Iterator;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import android.widget.CheckedTextView;
import android.widget.BaseAdapter;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.Debug;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.widget.AdapterView$OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.Menu;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import java.util.Collection;
import java.util.HashSet;
import android.view.View;
import android.widget.AdapterView;
import android.content.DialogInterface;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.view.MenuItem;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.Set;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import javax.annotation.Nullable;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class GroupMemberRolesFragment extends ChatterReloadableFragment implements OnLoadableDataChangedListener, BackButtonHandler
{
    private static final String MEMBER_ID_KEY = "memberID";
    @Nullable
    private UUID MemberID;
    private final SubscriptionData<GroupManager.GroupMemberRolesQuery, Set<UUID>> activeRoles;
    private MemberRoleAdapter adapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile;
    private final SubscriptionData<UUID, UUID> groupRoleMemberList;
    private final SubscriptionData<UUID, GroupRoleDataReply> groupRoles;
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles;
    private boolean hasChanged;
    private final LoadableMonitor loadableMonitor;
    @Nullable
    private ChatterNameRetriever memberNameRetriever;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList;
    private MenuItem undoMenuItem;
    
    public GroupMemberRolesFragment() {
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance());
        this.groupProfile = new SubscriptionData<UUID, GroupProfileReply>(UIThreadExecutor.getInstance());
        this.groupRoles = new SubscriptionData<UUID, GroupRoleDataReply>(UIThreadExecutor.getInstance());
        this.myGroupList = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.groupTitles = new SubscriptionData<UUID, GroupTitlesReply>(UIThreadExecutor.getInstance());
        this.groupRoleMemberList = new SubscriptionData<UUID, UUID>(UIThreadExecutor.getInstance(), new _$Lambda$jWSiK5iq_zZfaogto6grdML6fzQ$3(this));
        this.activeRoles = new SubscriptionData<GroupManager.GroupMemberRolesQuery, Set<UUID>>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.groupRoles, this.groupProfile, this.myGroupList, this.groupRoleMemberList, this.activeRoles }).withOptionalLoadables(this.agentCircuit, this.groupTitles).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.MemberID = null;
        this.memberNameRetriever = null;
        this.adapter = null;
        this.hasChanged = false;
    }
    
    private boolean anyChanges() {
        final Set set = this.activeRoles.getData();
        return this.adapter != null && set != null && (set.equals(this.adapter.getSelectedRoles()) ^ true);
    }
    
    private void closeFragment() {
        final FragmentActivity activity = this.getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity)activity).closeDetailsFragment(this);
        }
    }
    
    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        if (this.chatterID instanceof ChatterID.ChatterIDGroup) {
            try {
                return (AvatarGroupList.AvatarGroupEntry)this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID());
            }
            catch (final SubscriptionData.DataNotReadyException ex) {
                return null;
            }
        }
        return null;
    }
    
    private long getMyGroupPowers() {
        final AvatarGroupList.AvatarGroupEntry myGroupEntry = this.getMyGroupEntry();
        if (myGroupEntry != null) {
            return myGroupEntry.GroupPowers;
        }
        return 0L;
    }
    
    public static Bundle makeSelection(final ChatterID chatterID, final UUID uuid) {
        final Bundle selection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            selection.putString("memberID", uuid.toString());
        }
        return selection;
    }
    
    private void onGroupRoleMemberList(final UUID uuid) {
        if (this.userManager != null && this.chatterID instanceof ChatterID.ChatterIDGroup && this.MemberID != null) {
            this.activeRoles.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMemberRoleList(), GroupManager.GroupMemberRolesQuery.create(((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID(), this.MemberID, uuid));
        }
    }
    
    private void onMemberNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
        final String resolvedName = chatterNameRetriever.getResolvedName();
        if (!Strings.isNullOrEmpty(resolvedName)) {
            this.setTitle(this.getString(2131296675, resolvedName), null);
        }
        else {
            this.setTitle(this.getString(2131296712), null);
        }
    }
    
    private void updateUnsavedChanges() {
        final boolean anyChanges = this.anyChanges();
        if (anyChanges != this.hasChanged) {
            this.hasChanged = anyChanges;
            if (this.undoMenuItem != null) {
                this.undoMenuItem.setVisible(this.hasChanged);
            }
        }
    }
    
    @Override
    public boolean onBackButtonPressed() {
        if (this.anyChanges()) {
            final AlertDialog$Builder alertDialog$Builder = new AlertDialog$Builder(this.getContext());
            alertDialog$Builder.setMessage((CharSequence)this.getString(2131296992)).setCancelable(true).setPositiveButton((CharSequence)"Yes", (DialogInterface$OnClickListener)new _$Lambda$jWSiK5iq_zZfaogto6grdML6fzQ(this)).setNegativeButton((CharSequence)"No", (DialogInterface$OnClickListener)new _$Lambda$jWSiK5iq_zZfaogto6grdML6fzQ$1(this));
            alertDialog$Builder.create().show();
            return true;
        }
        return false;
    }
    
    @Override
    public void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setHasOptionsMenu(true);
        this.setShowChatterTitle(false);
    }
    
    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater menuInflater) {
        super.onCreateOptionsMenu(menu, menuInflater);
        menuInflater.inflate(2131886086, menu);
        (this.undoMenuItem = menu.findItem(2131755778)).setVisible(this.hasChanged);
    }
    
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968644, viewGroup, false);
        if (this.adapter == null) {
            this.adapter = new MemberRoleAdapter((MemberRoleAdapter)null);
        }
        ((ListView)inflate.findViewById(2131755380)).setAdapter((ListAdapter)this.adapter);
        ((ListView)inflate.findViewById(2131755380)).setOnItemClickListener((AdapterView$OnItemClickListener)new _$Lambda$jWSiK5iq_zZfaogto6grdML6fzQ$2(this));
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296736), this.getString(2131296593));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        if (this.adapter != null) {
            this.adapter.setData(this.groupRoles.getData(), this.activeRoles.getData());
        }
        while (true) {
            try {
                if (this.userManager != null) {
                    final AvatarGroupList.AvatarGroupEntry myGroupEntry = this.getMyGroupEntry();
                    if (myGroupEntry != null) {
                        Debug.Printf("GroupMemberRoles: my group powers are 0x%x", myGroupEntry.GroupPowers);
                        if (!this.groupTitles.isSubscribed()) {
                            this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), this.groupRoles.get().GroupData_Field.GroupID);
                        }
                    }
                    else {
                        Debug.Printf("GroupMemberRoles: not my group", new Object[0]);
                    }
                }
            }
            catch (final SubscriptionData.DataNotReadyException ex) {}
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(final MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case 2131755778: {
                try {
                    if (this.adapter != null) {
                        this.adapter.setData(this.groupRoles.get(), this.activeRoles.get());
                    }
                }
                catch (final SubscriptionData.DataNotReadyException ex) {}
                break;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.memberNameRetriever != null) {
            this.memberNameRetriever.dispose();
            this.memberNameRetriever = null;
        }
        this.MemberID = UUIDPool.getUUID(this.getArguments().getString("memberID"));
        this.setTitle(this.getString(2131296674), null);
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDGroup) {
            if (this.MemberID != null) {
                this.memberNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(this.userManager.getUserID(), this.MemberID), (ChatterNameRetriever.OnChatterNameUpdated)new _$Lambda$jWSiK5iq_zZfaogto6grdML6fzQ$4(this), UIThreadExecutor.getInstance());
            }
            final UUID chatterUUID = ((ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
            this.groupRoles.subscribe(this.userManager.getGroupRoles().getPool(), chatterUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.groupRoleMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMembers(), chatterUUID);
        }
        else if (this.adapter != null) {
            this.adapter.setData(null, null);
        }
    }
    
    private class MemberRoleAdapter extends BaseAdapter
    {
        @Nullable
        private GroupRoleDataReply data;
        private final Set<UUID> selectedRoles;
        
        private MemberRoleAdapter() {
            this.data = null;
            this.selectedRoles = new HashSet<UUID>();
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
        
        public Set<UUID> getSelectedRoles() {
            return this.selectedRoles;
        }
        
        public View getView(final int n, final View view, final ViewGroup viewGroup) {
            View inflate = view;
            if (view == null) {
                inflate = LayoutInflater.from(GroupMemberRolesFragment.this.getContext()).inflate(2130968643, viewGroup, false);
            }
            final GroupRoleDataReply.RoleData item = this.getItem(n);
            if (item != null) {
                ((CheckedTextView)inflate.findViewById(2131755379)).setText((CharSequence)SLMessage.stringFromVariableOEM(item.Name));
                ((CheckedTextView)inflate.findViewById(2131755379)).setChecked(item.RoleID.equals(UUIDPool.ZeroUUID) || this.selectedRoles.contains(item.RoleID));
            }
            return inflate;
        }
        
        public boolean hasStableIds() {
            return false;
        }
        
        public void setData(@Nullable final GroupRoleDataReply data, final Set<UUID> set) {
            this.data = data;
            this.selectedRoles.clear();
            if (set != null) {
                this.selectedRoles.addAll(set);
            }
            GroupMemberRolesFragment.this.updateUnsavedChanges();
            this.notifyDataSetInvalidated();
        }
        
        public void toggleChecked(final UUID obj) {
            if (obj.equals(UUIDPool.ZeroUUID) || GroupMemberRolesFragment.this.userManager == null || GroupMemberRolesFragment.this.MemberID == null) {
                return;
            }
            while (true) {
                final long -wrap0 = GroupMemberRolesFragment.this.getMyGroupPowers();
                while (true) {
                    boolean b;
                    try {
                        final boolean contains = GroupMemberRolesFragment.this.activeRoles.get().contains(obj);
                        b = (this.selectedRoles.contains(obj) ^ true);
                        if (contains == b) {
                            if (b) {
                                this.selectedRoles.add(obj);
                            }
                            else {
                                this.selectedRoles.remove(obj);
                            }
                            final int n = 1;
                            if (n != 0) {
                                GroupMemberRolesFragment.this.updateUnsavedChanges();
                                this.notifyDataSetChanged();
                            }
                            return;
                        }
                    }
                    catch (final SubscriptionData.DataNotReadyException ex) {
                        final int n = 0;
                        continue;
                    }
                    if (!b) {
                        if ((-wrap0 & 0x200L) != 0x0L) {
                            final boolean equals = obj.equals(GroupMemberRolesFragment.this.groupProfile.get().GroupData_Field.OwnerRole);
                            final boolean equals2 = GroupMemberRolesFragment.this.userManager.getUserID().equals(GroupMemberRolesFragment.this.MemberID);
                            if (!equals || equals2) {
                                this.selectedRoles.remove(obj);
                                final int n = 1;
                                continue;
                            }
                        }
                    }
                    else {
                        int n2 = 0;
                        Label_0239: {
                            if ((0x100L & -wrap0) != 0x0L) {
                                n2 = 1;
                            }
                            else {
                                if ((-wrap0 & 0x80L) != 0x0L) {
                                    final GroupTitlesReply groupTitlesReply = GroupMemberRolesFragment.this.groupTitles.getData();
                                    if (groupTitlesReply != null) {
                                        final Iterator<Object> iterator = groupTitlesReply.GroupData_Fields.iterator();
                                        while (iterator.hasNext()) {
                                            if (iterator.next().RoleID.equals(obj)) {
                                                n2 = 1;
                                                break Label_0239;
                                            }
                                        }
                                    }
                                }
                                n2 = 0;
                            }
                        }
                        if (n2 != 0) {
                            this.selectedRoles.add(obj);
                            final int n = 1;
                            continue;
                        }
                    }
                    final int n = 0;
                    continue;
                }
            }
        }
    }
}
