// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.widget.TextView;
import android.widget.ImageButton;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.content.Context;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.Iterator;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.support.v7.widget.RecyclerView;
import android.view.View$OnClickListener;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.Fragment;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import android.view.View;
import com.lumiyaviewer.lumiya.Debug;
import android.content.DialogInterface$OnClickListener;
import android.app.AlertDialog$Builder;
import android.os.Bundle;
import android.content.DialogInterface;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import javax.annotation.Nullable;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;

public class GroupRoleMembersFragment extends ChatterFragment implements OnLoadableDataChangedListener
{
    private static final String ROLE_ID_KEY = "role_id";
    @Nullable
    private UUID RoleID;
    private MemberListAdapter adapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private boolean canAddMembers;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile;
    private final SubscriptionData<UUID, UUID> groupRoleMemberList;
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList;
    private final SubscriptionData<GroupManager.GroupRoleMembersQuery, LazyList<GroupRoleMember>> roleMembers;
    
    public GroupRoleMembersFragment() {
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance());
        this.groupProfile = new SubscriptionData<UUID, GroupProfileReply>(UIThreadExecutor.getInstance());
        this.myGroupList = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.groupTitles = new SubscriptionData<UUID, GroupTitlesReply>(UIThreadExecutor.getInstance());
        this.groupRoleMemberList = new SubscriptionData<UUID, UUID>(UIThreadExecutor.getInstance(), new _$Lambda$TbI0imFFmZKCR9nUgEGSvi__8Q0$2(this));
        this.roleMembers = new SubscriptionData<GroupManager.GroupRoleMembersQuery, LazyList<GroupRoleMember>>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.groupRoleMemberList, this.myGroupList, this.groupProfile, this.roleMembers }).withOptionalLoadables(this.agentCircuit, this.groupTitles).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.canAddMembers = false;
        this.adapter = null;
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
    
    public static Bundle makeSelection(final ChatterID chatterID, @Nullable final UUID uuid) {
        final Bundle selection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            selection.putString("role_id", uuid.toString());
        }
        return selection;
    }
    
    private void onGroupRoleMemberList(final UUID uuid) {
        if (this.userManager != null && this.chatterID instanceof ChatterID.ChatterIDGroup && this.RoleID != null) {
            this.roleMembers.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMemberList(), GroupManager.GroupRoleMembersQuery.create(((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID(), this.RoleID, uuid));
        }
    }
    
    private void removeMemberFromRole(final ChatterID.ChatterIDUser chatterIDUser) {
        new AlertDialog$Builder(this.getContext()).setTitle(2131296930).setPositiveButton(2131297168, (DialogInterface$OnClickListener)new _$Lambda$TbI0imFFmZKCR9nUgEGSvi__8Q0$3(this, chatterIDUser)).setNegativeButton(2131296424, (DialogInterface$OnClickListener)new _$Lambda$TbI0imFFmZKCR9nUgEGSvi__8Q0()).create().show();
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968648, viewGroup, false);
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        inflate.findViewById(2131755392).setOnClickListener((View$OnClickListener)new _$Lambda$TbI0imFFmZKCR9nUgEGSvi__8Q0$1(this));
        this.adapter = new MemberListAdapter(this.getContext());
        ((RecyclerView)inflate.findViewById(2131755391)).setAdapter((RecyclerView.Adapter)this.adapter);
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296736), this.getString(2131296593));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        final boolean b = true;
        final int n = 0;
        if (this.userManager != null) {
            final AvatarGroupList.AvatarGroupEntry myGroupEntry = this.getMyGroupEntry();
            if (myGroupEntry != null && !this.groupTitles.isSubscribed()) {
                this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), myGroupEntry.GroupID);
            }
        }
        this.canAddMembers = false;
        final long myGroupPowers = this.getMyGroupPowers();
        Label_0078: {
            if ((0x100L & myGroupPowers) != 0x0L) {
                this.canAddMembers = true;
            }
            else if ((0x80L & myGroupPowers) != 0x0L) {
                final GroupTitlesReply groupTitlesReply = this.groupTitles.getData();
                if (groupTitlesReply != null) {
                    final Iterator<Object> iterator = groupTitlesReply.GroupData_Fields.iterator();
                    while (true) {
                        while (iterator.hasNext()) {
                            if (iterator.next().RoleID.equals(this.RoleID)) {
                                final int n2 = 1;
                                if (n2 != 0) {
                                    this.canAddMembers = true;
                                }
                                break Label_0078;
                            }
                        }
                        final int n2 = 0;
                        continue;
                    }
                }
            }
        }
        boolean b2;
        boolean b3;
        if ((myGroupPowers & 0x200L) != 0x0L) {
            final GroupProfileReply groupProfileReply = this.groupProfile.getData();
            if (groupProfileReply != null && this.RoleID != null && this.RoleID.equals(groupProfileReply.GroupData_Field.OwnerRole)) {
                b2 = false;
                b3 = b;
            }
            else {
                b2 = true;
                b3 = false;
            }
        }
        else {
            b3 = false;
            b2 = false;
        }
        final View view = this.getView();
        if (view != null) {
            final View viewById = view.findViewById(2131755392);
            int visibility;
            if (this.canAddMembers) {
                visibility = n;
            }
            else {
                visibility = 8;
            }
            viewById.setVisibility(visibility);
        }
        if (this.adapter != null) {
            this.adapter.setData(this.roleMembers.getData(), b2, b3);
        }
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        this.RoleID = UUIDPool.getUUID(this.getArguments().getString("role_id"));
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDGroup) {
            final UUID chatterUUID = ((ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
            Debug.Printf("GroupRoleMemberList: subscribing for group %s", chatterUUID);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.groupRoleMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMembers(), chatterUUID);
        }
    }
    
    private class MemberListAdapter extends Adapter<MemberViewHolder>
    {
        private boolean canDeleteMembers;
        private boolean canDeleteMyself;
        private LazyList<GroupRoleMember> data;
        private final LayoutInflater layoutInflater;
        
        MemberListAdapter(final Context context) {
            this.data = null;
            this.canDeleteMembers = false;
            this.canDeleteMyself = false;
            this.layoutInflater = LayoutInflater.from(context);
        }
        
        @Override
        public int getItemCount() {
            int size;
            if (this.data != null) {
                size = this.data.size();
            }
            else {
                size = 0;
            }
            return size;
        }
        
        public void onBindViewHolder(final MemberViewHolder memberViewHolder, final int n) {
            if (this.data != null && n >= 0 && n < this.data.size()) {
                memberViewHolder.bindToData(this.data.get(n), this.canDeleteMembers, this.canDeleteMyself);
            }
        }
        
        public MemberViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
            return new MemberViewHolder(this.layoutInflater.inflate(2130968653, viewGroup, false), GroupRoleMembersFragment.this.userManager.getUserID());
        }
        
        public void onViewRecycled(final MemberViewHolder memberViewHolder) {
            memberViewHolder.recycle();
        }
        
        public void setData(final LazyList<GroupRoleMember> data, final boolean canDeleteMembers, final boolean canDeleteMyself) {
            this.data = data;
            this.canDeleteMembers = canDeleteMembers;
            this.canDeleteMyself = canDeleteMyself;
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
    }
    
    private class MemberViewHolder extends ViewHolder implements OnChatterNameUpdated, View$OnClickListener
    {
        private final UUID agentUUID;
        private ChatterID.ChatterIDUser boundChatterID;
        private boolean canDelete;
        private ChatterNameRetriever chatterNameRetriever;
        private final ImageButton roleMemberRemoveButton;
        private final TextView userNameTextView;
        private final ChatterPicView userPicView;
        
        MemberViewHolder(final View view, final UUID agentUUID) {
            super(view);
            this.canDelete = false;
            this.boundChatterID = null;
            this.chatterNameRetriever = null;
            this.agentUUID = agentUUID;
            this.userNameTextView = (TextView)view.findViewById(2131755328);
            this.userPicView = (ChatterPicView)view.findViewById(2131755327);
            (this.roleMemberRemoveButton = (ImageButton)view.findViewById(2131755436)).setOnClickListener((View$OnClickListener)this);
        }
        
        void bindToData(final GroupRoleMember groupRoleMember, final boolean b, boolean canDelete) {
            int visibility = 0;
            ChatterID.ChatterIDWithUUID userChatterID;
            if (groupRoleMember != null) {
                userChatterID = ChatterID.getUserChatterID(this.agentUUID, groupRoleMember.getUserID());
            }
            else {
                userChatterID = null;
            }
            if (!Objects.equal(userChatterID, this.boundChatterID)) {
                if (this.chatterNameRetriever != null) {
                    this.chatterNameRetriever.dispose();
                    this.chatterNameRetriever = null;
                }
                this.userNameTextView.setText((CharSequence)null);
                if ((this.boundChatterID = (ChatterID.ChatterIDUser)userChatterID) != null) {
                    this.chatterNameRetriever = new ChatterNameRetriever(this.boundChatterID, (ChatterNameRetriever.OnChatterNameUpdated)this, UIThreadExecutor.getInstance());
                    this.userPicView.setChatterID(userChatterID, this.chatterNameRetriever.getResolvedName());
                }
                else {
                    this.userPicView.setChatterID(null, null);
                }
            }
            if (!b) {
                if (!this.agentUUID.equals(this.boundChatterID.getChatterUUID())) {
                    canDelete = false;
                }
            }
            else {
                canDelete = true;
            }
            this.canDelete = canDelete;
            final ImageButton roleMemberRemoveButton = this.roleMemberRemoveButton;
            if (!this.canDelete) {
                visibility = 8;
            }
            roleMemberRemoveButton.setVisibility(visibility);
        }
        
        @Override
        public void onChatterNameUpdated(final ChatterNameRetriever chatterNameRetriever) {
            if (chatterNameRetriever != null) {
                this.userNameTextView.setText((CharSequence)chatterNameRetriever.getResolvedName());
                this.userPicView.setChatterID(chatterNameRetriever.chatterID, chatterNameRetriever.getResolvedName());
            }
        }
        
        public void onClick(final View view) {
            switch (view.getId()) {
                case 2131755436: {
                    if (this.boundChatterID != null && this.canDelete) {
                        GroupRoleMembersFragment.this.removeMemberFromRole(this.boundChatterID);
                        break;
                    }
                    break;
                }
            }
        }
        
        void recycle() {
            if (this.chatterNameRetriever != null) {
                this.chatterNameRetriever.dispose();
                this.chatterNameRetriever = null;
            }
            this.boundChatterID = null;
            this.userPicView.setChatterID(null, null);
        }
    }
}
