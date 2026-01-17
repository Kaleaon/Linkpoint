package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.dao.GroupMember;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.query.LazyList;
import java.util.UUID;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
public class GroupMembersProfileTab extends ChatterReloadableFragment implements LoadableMonitor.OnLoadableDataChangedListener {
    private static final String ROLE_TO_ADD_KEY = "roleToAdd";
    private final SubscriptionData<UUID, UUID> groupMemberList = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.2
        private final /* synthetic */ void $m$0(Object obj) {
            ((GroupMembersProfileTab) this).m484xe6cc6560((UUID) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });
    private final SubscriptionData<GroupManager.GroupMembersQuery, LazyList<GroupMember>> groupMembers = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList = new SubscriptionData<>(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor = new LoadableMonitor(this.groupMemberList, this.myGroupList, this.groupProfile, this.groupMembers).withOptionalLoadables(this.agentCircuit).withDataChangedListener(this);
    private GroupMemberListRecyclerAdapter adapter = null;

    /* loaded from: classes.dex */
    private class GroupMemberListRecyclerAdapter extends RecyclerView.Adapter<GroupMemberViewHolder> {
        private final int cardSelectedColor;
        private final LayoutInflater layoutInflater;
        private LazyList<GroupMember> data = null;
        private int selectedPosition = -1;

        GroupMemberListRecyclerAdapter(Context context) {
            this.layoutInflater = LayoutInflater.from(context);
            TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(R.attr.CardViewDetailsBackground, typedValue, true);
            this.cardSelectedColor = typedValue.data;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.data != null) {
                return this.data.size();
            }
            return 0;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(GroupMemberViewHolder groupMemberViewHolder, int i) {
            if (this.data == null || !(!this.data.isClosed()) || i < 0 || i >= this.data.size()) {
                return;
            }
            groupMemberViewHolder.bindToData(this.data.get(i), i == this.selectedPosition);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public GroupMemberViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new GroupMemberViewHolder(this.layoutInflater.inflate(R.layout.group_member_list_item, viewGroup, false), GroupMembersProfileTab.this.userManager.getUserID(), this.cardSelectedColor);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onViewRecycled(GroupMemberViewHolder groupMemberViewHolder) {
            groupMemberViewHolder.recycle();
        }

        public void setData(LazyList<GroupMember> lazyList) {
            this.data = lazyList;
            this.selectedPosition = -1;
            notifyDataSetChanged();
        }

        public void setSelectedPosition(int i) {
            if (i != this.selectedPosition) {
                int i2 = this.selectedPosition;
                this.selectedPosition = i;
                if (i2 != -1) {
                    notifyItemChanged(i2);
                }
                if (i != -1) {
                    notifyItemChanged(i);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class GroupMemberViewHolder extends RecyclerView.ViewHolder implements ChatterNameRetriever.OnChatterNameUpdated, View.OnClickListener {
        private final UUID agentUUID;
        private ChatterID.ChatterIDUser boundChatterID;
        private final int cardSelectedColor;
        private final float cardSelectedElevation;
        private final CardView cardView;
        private ChatterNameRetriever chatterNameRetriever;
        private final Button groupMemberChatButton;
        private final Button groupMemberEjectButton;
        private final Button groupMemberProfileButton;
        private final Button groupMemberRolesButton;
        private final View selectedLayout;
        private final TextView userNameTextView;
        private final TextView userOnlineStatusText;
        private final ChatterPicView userPicView;
        private final TextView userTitleText;

        GroupMemberViewHolder(View view, UUID uuid, int i) {
            super(view);
            this.boundChatterID = null;
            this.chatterNameRetriever = null;
            this.agentUUID = uuid;
            this.cardView = (CardView) view.findViewById(R.id.group_member_card_view);
            this.userNameTextView = (TextView) view.findViewById(R.id.userNameTextView);
            this.userPicView = (ChatterPicView) view.findViewById(R.id.userPicView);
            this.userTitleText = (TextView) view.findViewById(R.id.userTitleText);
            this.userOnlineStatusText = (TextView) view.findViewById(R.id.userOnlineStatusText);
            this.selectedLayout = view.findViewById(R.id.group_member_selected_layout);
            this.groupMemberChatButton = (Button) view.findViewById(R.id.group_member_chat_button);
            this.groupMemberProfileButton = (Button) view.findViewById(R.id.group_member_profile_button);
            this.groupMemberRolesButton = (Button) view.findViewById(R.id.group_member_roles_button);
            this.groupMemberEjectButton = (Button) view.findViewById(R.id.group_member_eject_button);
            this.cardSelectedElevation = this.cardView.getCardElevation();
            this.cardSelectedColor = i;
            this.cardView.setOnClickListener(this);
            this.groupMemberChatButton.setOnClickListener(this);
            this.groupMemberProfileButton.setOnClickListener(this);
            this.groupMemberRolesButton.setOnClickListener(this);
            this.groupMemberEjectButton.setOnClickListener(this);
        }

        void bindToData(GroupMember groupMember, boolean z) {
            ChatterID.ChatterIDUser userChatterID = groupMember != null ? ChatterID.getUserChatterID(this.agentUUID, groupMember.getUserID()) : null;
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
            this.userTitleText.setText(groupMember != null ? groupMember.getTitle() : null);
            this.userOnlineStatusText.setText(groupMember != null ? groupMember.getOnlineStatus() : null);
            if (z) {
                this.cardView.setCardElevation(this.cardSelectedElevation);
                this.cardView.setCardBackgroundColor(this.cardSelectedColor);
            } else {
                this.cardView.setCardElevation(0.0f);
                this.cardView.setCardBackgroundColor(0);
            }
            this.selectedLayout.setVisibility(z ? 0 : 8);
            AvatarGroupList.AvatarGroupEntry myGroupEntry = GroupMembersProfileTab.this.getMyGroupEntry();
            this.groupMemberEjectButton.setVisibility(GroupMembersProfileTab.this.agentCircuit != null && myGroupEntry != null && ((myGroupEntry.GroupPowers & 4) > 0L ? 1 : ((myGroupEntry.GroupPowers & 4) == 0L ? 0 : -1)) != 0 ? 0 : 8);
            this.groupMemberRolesButton.setVisibility(myGroupEntry == null ? 8 : 0);
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
                case R.id.group_member_card_view /* 2131755371 */:
                    if (GroupMembersProfileTab.this.getArguments().containsKey(GroupMembersProfileTab.ROLE_TO_ADD_KEY)) {
                        if (this.boundChatterID != null) {
                            GroupMembersProfileTab.this.addGroupRoleMember(this.boundChatterID);
                            return;
                        }
                        return;
                    } else if (GroupMembersProfileTab.this.adapter != null) {
                        GroupMembersProfileTab.this.adapter.setSelectedPosition(getAdapterPosition());
                        return;
                    } else {
                        return;
                    }
                case R.id.userTitleText /* 2131755372 */:
                case R.id.userOnlineStatusText /* 2131755373 */:
                case R.id.group_member_selected_layout /* 2131755374 */:
                default:
                    return;
                case R.id.group_member_chat_button /* 2131755375 */:
                    if (this.boundChatterID != null) {
                        DetailsActivity.showDetails(GroupMembersProfileTab.this.getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(this.boundChatterID));
                        return;
                    }
                    return;
                case R.id.group_member_profile_button /* 2131755376 */:
                    DetailsActivity.showEmbeddedDetails(GroupMembersProfileTab.this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.boundChatterID));
                    return;
                case R.id.group_member_roles_button /* 2131755377 */:
                    if (this.boundChatterID != null) {
                        DetailsActivity.showEmbeddedDetails(GroupMembersProfileTab.this.getActivity(), GroupMemberRolesFragment.class, GroupMemberRolesFragment.makeSelection(GroupMembersProfileTab.this.chatterID, this.boundChatterID.getChatterUUID()));
                        return;
                    }
                    return;
                case R.id.group_member_eject_button /* 2131755378 */:
                    if (this.boundChatterID != null) {
                        GroupMembersProfileTab.this.ejectGroupMember(this.boundChatterID);
                        return;
                    }
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

    /* JADX INFO: Access modifiers changed from: private */
    public void addGroupRoleMember(final ChatterID.ChatterIDUser chatterIDUser) {
        final UUID uuid = UUIDPool.getUUID(getArguments().getString(ROLE_TO_ADD_KEY));
        if (uuid != null) {
            new AlertDialog.Builder(getContext()).setTitle(R.string.add_role_member_confirm).setPositiveButton(R.string.yes_add_button, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.4
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    ((GroupMembersProfileTab) this).m485xf9973c0c((UUID) uuid, (ChatterID.ChatterIDUser) chatterIDUser, dialogInterface, i);
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY
                private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                    dialogInterface.cancel();
                }

                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    $m$0(dialogInterface, i);
                }
            }).create().show();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void ejectGroupMember(final ChatterID.ChatterIDUser chatterIDUser) {
        new AlertDialog.Builder(getContext()).setTitle(R.string.eject_member_confirm).setPositiveButton(R.string.yes_eject_button, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.3
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                ((GroupMembersProfileTab) this).m486xf997b3d4((ChatterID.ChatterIDUser) chatterIDUser, dialogInterface, i);
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.lumiyaviewer.lumiya.ui.chat.profiles.-$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY.1
            private final /* synthetic */ void $m$0(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }

            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                $m$0(dialogInterface, i);
            }
        }).create().show();
    }

    /* JADX INFO: Access modifiers changed from: private */
    @Nullable
    public AvatarGroupList.AvatarGroupEntry getMyGroupEntry() {
        try {
            return this.myGroupList.get().Groups.get(this.groupProfile.get().GroupData_Field.GroupID);
        } catch (SubscriptionData.DataNotReadyException e) {
            return null;
        }
    }

    public static Bundle makeSelection(ChatterID chatterID, @Nullable UUID uuid) {
        Bundle makeSelection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            makeSelection.putString(ROLE_TO_ADD_KEY, uuid.toString());
        }
        return makeSelection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onGroupMemberList */
    public void m484xe6cc6560(UUID uuid) {
        Debug.Printf("GroupMemberList: got dataset ID = %s", uuid);
        if (this.userManager == null || !(this.chatterID instanceof ChatterID.ChatterIDGroup)) {
            return;
        }
        this.groupMembers.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMembersList(), GroupManager.GroupMembersQuery.create(((ChatterID.ChatterIDGroup) this.chatterID).getChatterUUID(), uuid));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_13581  reason: not valid java name */
    public /* synthetic */ void m485xf9973c0c(UUID uuid, ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.AddMemberToRole(this.groupProfile.get().GroupData_Field.GroupID, uuid, chatterIDUser.getChatterUUID());
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
        dialogInterface.dismiss();
        FragmentActivity activity = getActivity();
        if (activity instanceof DetailsActivity) {
            ((DetailsActivity) activity).closeDetailsFragment(this);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$-com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_14656  reason: not valid java name */
    public /* synthetic */ void m486xf997b3d4(ChatterID.ChatterIDUser chatterIDUser, DialogInterface dialogInterface, int i) {
        try {
            this.agentCircuit.get().getModules().groupManager.RequestEjectFromGroup(this.groupProfile.get().GroupData_Field.GroupID, chatterIDUser.getChatterUUID());
        } catch (SubscriptionData.DataNotReadyException e) {
            Debug.Warning(e);
        }
        dialogInterface.dismiss();
    }

    @Override // android.support.v4.app.Fragment
    public View onCreateView(LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        View inflate = layoutInflater.inflate(R.layout.group_profile_tab_members, viewGroup, false);
        ((LoadingLayout) inflate.findViewById(R.id.loading_layout)).setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        this.adapter = new GroupMemberListRecyclerAdapter(getContext());
        ((RecyclerView) inflate.findViewById(R.id.group_profile_members_list)).setAdapter(this.adapter);
        this.loadableMonitor.setLoadingLayout((LoadingLayout) inflate.findViewById(R.id.loading_layout), getString(R.string.no_group_selected), getString(R.string.group_member_list_error));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout) inflate.findViewById(R.id.swipe_refresh_layout));
        return inflate;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
    public void onLoadableDataChanged() {
        if (this.adapter != null) {
            this.adapter.setData(this.groupMembers.getData());
            this.adapter.notifyDataSetChanged();
        }
        LazyList<GroupMember> data = this.groupMembers.getData();
        this.loadableMonitor.setEmptyMessage(data != null ? data.isEmpty() : false, getString(R.string.no_public_group_members));
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ChatterFragment
    protected void onShowUser(@Nullable ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.userManager == null || !(chatterID instanceof ChatterID.ChatterIDGroup)) {
            return;
        }
        UUID chatterUUID = ((ChatterID.ChatterIDGroup) chatterID).getChatterUUID();
        Debug.Printf("GroupMemberList: subscribing for group %s", chatterUUID);
        this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
        this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
        this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
        this.groupMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMembers(), chatterUUID);
    }
}
