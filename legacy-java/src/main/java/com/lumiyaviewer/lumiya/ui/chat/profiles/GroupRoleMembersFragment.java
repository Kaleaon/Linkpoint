package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.query.LazyList;
import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class GroupRoleMembersFragment extends ChatterFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private static final String ROLE_ID_KEY = "role_id";

    @Nullable
    private UUID RoleID;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupTitlesReply> groupTitles = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, UUID> groupRoleMemberList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.2
        private final /* synthetic */ void $m$0(Object obj) {
            ((GroupRoleMembersFragment) this).m504x9104b852((UUID) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<GroupManager.GroupRoleMembersQuery, LazyList<GroupRoleMember>> roleMembers = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupRoleMemberList, this.myGroupList, this.groupProfile, this.roleMembers).withOptionalLoadables(this.agentCircuit, this.groupTitles).withDataChangedListener(this);
    private boolean canAddMembers = false;
    private MemberListAdapter adapter = null;

    /* loaded from: classes.dex */
    private class MemberListAdapter extends RecyclerView.Adapter<MemberViewHolder> {
        private final LayoutInflater layoutInflater;
        private LazyList<GroupRoleMember> data = null;
        private boolean canDeleteMembers = false;
        private boolean canDeleteMyself = false;

        MemberListAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.data != null) {
                return this.data.size();
            }
            return 0;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(MemberViewHolder memberViewHolder, int i) {
            if (this.data == null || i < 0 || i >= this.data.size()) {
                return;
            }
            memberViewHolder.bindToData(this.data.get(i), this.canDeleteMembers, this.canDeleteMyself);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public MemberViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new MemberViewHolder(this.layoutInflater.inflate(R.layout.group_role_member_list_item, viewGroup, false), GroupRoleMembersFragment.this.userManager.getUserID());
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onViewRecycled(MemberViewHolder memberViewHolder) {
            memberViewHolder.recycle();
        }

        public void setData(LazyList<GroupRoleMember> lazyList, boolean z, boolean z2) {
            this.data = lazyList;
            this.canDeleteMembers = z;
            this.canDeleteMyself = z2;
            notifyDataSetChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class MemberViewHolder extends RecyclerView.ViewHolder implements ChatterNameRetriever.OnChatterNameUpdated, View.OnClickListener {
        private final UUID agentUUID;
        private ChatterID.ChatterIDUser boundChatterID;
        private boolean canDelete;
        private ChatterNameRetriever chatterNameRetriever;
        private final ImageButton roleMemberRemoveButton;
        private final TextView userNameTextView;
        private final ChatterPicView userPicView;

        MemberViewHolder(View view, UUID uuid) {
            super(view);
            this.canDelete = false;
            this.boundChatterID = null;
            this.chatterNameRetriever = null;
            this.agentUUID = uuid;
            this.userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
            this.userPicView = (ChatterPicView) view.findViewById(R.id.userPicView);
            this.roleMemberRemoveButton = (ImageButton) view.findViewById(R.id.role_member_remove_button);
            this.roleMemberRemoveButton.setOnClickListener(this);
        }

        void bindToData(GroupRoleMember groupRoleMember, boolean z, boolean z2) {
            ChatterID.ChatterIDUser userChatterID = groupRoleMember != null ? ChatterID.getUserChatterID(this.agentUUID, groupRoleMember.getUserID()) : null;
            if (!Objects.equal(userChatterID, this.boundChatterID)) {
                if (this.chatterNameRetriever != null) {
                    this.chatterNameRetriever.dispose();
                    this.chatterNameRetriever = null;
                }
                this.userNameTextView.setText((CharSequence) null);
                this.boundChatterID = userChatterID;
                if (userChatterID != null) {
                    this.chatterNameRetriever = new ChatterNameRetriever(this.boundChatterID, this, UIThreadExecutor.getInstance());
                    this.userPicView.setChatterID(userChatterID, this.chatterNameRetriever.getResolvedName());
                } else {
                    this.userPicView.setChatterID(null, null);
                }
            }
            if (z) {
                z2 = true;
            } else if (!this.agentUUID.equals(this.boundChatterID.getChatterUUID())) {
                z2 = false;
            }
            this.canDelete = z2;
            this.roleMemberRemoveButton.setVisibility(this.canDelete ? 0 : 8);
        }

        @Override // com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated
        public void onChatterNameUpdated(ChatterNameRetriever chatterNameRetriever) {
            if (chatterNameRetriever != null) {
                this.userNameTextView.setText(chatterNameRetriever.getResolvedName());
                this.userPicView.setChatterID(chatterNameRetriever.chatterID, chatterNameRetriever.getResolvedName());
            }
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.role_member_remove_button /* 2131755436 */:
                    if (this.boundChatterID == null || !this.canDelete) {
                        return;
                    }
                    GroupRoleMembersFragment.this.removeMemberFromRole(this.boundChatterID);
                    return;
                default:
                    return;
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

    @Nullable
    private AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        if (!(this.chatterID instanceof ChatterID.ChatterIDGroup)) {
            return null;
        }
        try {
            return this.myGroupList.get().Groups.get(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID());
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

    public static Bundle makeSelection(ChatterID chatterID, @Nullable UUID uuid) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            makeSelection.putString(ROLE_ID_KEY, uuid.toString());
        }
        return makeSelection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onGroupRoleMemberList, reason: merged with bridge method [inline-methods] */
    public void m504x9104b852(UUID uuid) {
        if (this.userManager == null || !(this.chatterID instanceof ChatterID.ChatterIDGroup) || this.RoleID == null) {
            return;
        }
        this.roleMembers.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMemberList(), GroupManager.GroupRoleMembersQuery.create(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), this.RoleID, uuid));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeMemberFromRole(final ChatterID.ChatterIDUser chatterIDUser) {
        new AlertDialog.Builder(getContext()).setTitle(R.string.remove_member_from_role_confirm).setPositiveButton(R.string.yes_remove, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.3
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((GroupRoleMembersFragment) this).m505xdf854965((ChatterID.ChatterIDUser) chatterIDUser, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_11502, reason: not valid java name */
    public /* synthetic */ void m505xdf854965(ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.RemoveMemberFromRole(this.groupProfile.get().GroupData_Field.GroupID, this.RoleID, chatterIDUser.getChatterUUID());
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
        dialogInterface.dismiss();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_4418, reason: not valid java name */
    public /* synthetic */ void m506xbce4ac59(View view) {
        if (this.canAddMembers) {
            DetailsActivity.showEmbeddedDetails(getActivity(), GroupMembersProfileTab.class, GroupMembersProfileTab.makeSelection(this.chatterID, this.RoleID));
        }
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_profile_role_members, viewGroup, false);
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        inflate.findViewById(R.id.add_role_member_button).setOnClickListener(new View.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$TbI0imFFmZKCR9nUgEGSvi-_8Q0.1
            private final /* synthetic */ void $m$0(View view) {
                ((GroupRoleMembersFragment) this).m506xbce4ac59(view);
            }

            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                $m$0(view);
            }
        });
        this.adapter = new MemberListAdapter(getContext());
        ((RecyclerView) inflate.findViewById(R.id.group_profile_role_members_list)).setAdapter(this.adapter);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_profile_fail));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        GroupTitlesReply data;
        boolean z;
        boolean z2;
        AvatarGroupList.AvatarGroupEntry myGroupEntry;
        boolean z3 = true;
        if (this.userManager != null && (myGroupEntry = getMyGroupEntry()) != null && !this.groupTitles.isSubscribed()) {
            this.groupTitles.subscribe(this.userManager.getGroupTitles().getPool(), myGroupEntry.GroupID);
        }
        this.canAddMembers = false;
        long myGroupPowers = getMyGroupPowers();
        if ((256 & myGroupPowers) != 0) {
            this.canAddMembers = true;
        } else if ((128 & myGroupPowers) != 0 && (data = this.groupTitles.getData()) != null) {
            Iterator<T> it = data.GroupData_Fields.iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                } else if (((GroupTitlesReply.GroupData) it.next()).RoleID.equals(this.RoleID)) {
                    z = true;
                    break;
                }
            }
            if (z) {
                this.canAddMembers = true;
            }
        }
        if ((myGroupPowers & 512) != 0) {
            GroupProfileReply data2 = this.groupProfile.getData();
            if ((data2 == null || this.RoleID == null) ? false : this.RoleID.equals(data2.GroupData_Field.OwnerRole)) {
                z2 = false;
            } else {
                z2 = true;
                z3 = false;
            }
        } else {
            z3 = false;
            z2 = false;
        }
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.add_role_member_button).setVisibility(this.canAddMembers ? 0 : 8);
        }
        if (this.adapter != null) {
            this.adapter.setData(this.roleMembers.getData(), z2, z3);
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        this.RoleID = UUIDPool.getUUID(getArguments().getString(ROLE_ID_KEY));
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDGroup)) {
            return;
        }
        UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
        Debug.Printf("GroupRoleMemberList: subscribing for group %s", chatterUUID);
        this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
        this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
        this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        this.groupRoleMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupRoleMembers(), chatterUUID);
    }
}
