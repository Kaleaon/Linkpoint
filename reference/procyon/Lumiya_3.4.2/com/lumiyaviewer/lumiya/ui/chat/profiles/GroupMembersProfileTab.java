// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.google.common.base.Objects;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import android.widget.TextView;
import android.widget.Button;
import android.support.v7.widget.CardView;
import android.view.View$OnClickListener;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import android.util.TypedValue;
import android.content.Context;
import com.lumiyaviewer.lumiya.react.Subscribable;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import android.os.Bundle;
import android.content.DialogInterface;
import javax.annotation.Nullable;
import android.content.DialogInterface$OnClickListener;
import android.support.v7.app.AlertDialog;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.dao.GroupMember;
import de.greenrobot.dao.query.LazyList;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import java.util.UUID;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;

public class GroupMembersProfileTab extends ChatterReloadableFragment implements OnLoadableDataChangedListener
{
    private static final String ROLE_TO_ADD_KEY = "roleToAdd";
    private GroupMemberListRecyclerAdapter adapter;
    private final SubscriptionData<UUID, SLAgentCircuit> agentCircuit;
    private final SubscriptionData<UUID, UUID> groupMemberList;
    private final SubscriptionData<GroupManager.GroupMembersQuery, LazyList<GroupMember>> groupMembers;
    private final SubscriptionData<UUID, GroupProfileReply> groupProfile;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData<UUID, AvatarGroupList> myGroupList;
    
    public GroupMembersProfileTab() {
        this.groupMemberList = new SubscriptionData<UUID, UUID>(UIThreadExecutor.getInstance(), new _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$2(this));
        this.groupMembers = new SubscriptionData<GroupManager.GroupMembersQuery, LazyList<GroupMember>>(UIThreadExecutor.getInstance());
        this.agentCircuit = new SubscriptionData<UUID, SLAgentCircuit>(UIThreadExecutor.getInstance());
        this.groupProfile = new SubscriptionData<UUID, GroupProfileReply>(UIThreadExecutor.getInstance());
        this.myGroupList = new SubscriptionData<UUID, AvatarGroupList>(UIThreadExecutor.getInstance());
        this.loadableMonitor = new LoadableMonitor(new Loadable[] { this.groupMemberList, this.myGroupList, this.groupProfile, this.groupMembers }).withOptionalLoadables(this.agentCircuit).withDataChangedListener((LoadableMonitor.OnLoadableDataChangedListener)this);
        this.adapter = null;
    }
    
    private void addGroupRoleMember(final ChatterID.ChatterIDUser chatterIDUser) {
        final UUID uuid = UUIDPool.getUUID(this.getArguments().getString("roleToAdd"));
        if (uuid != null) {
            new AlertDialog.Builder(this.getContext()).setTitle(2131296320).setPositiveButton(2131297164, (DialogInterface$OnClickListener)new _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$4(this, uuid, chatterIDUser)).setNegativeButton(2131296424, (DialogInterface$OnClickListener)new _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY()).create().show();
        }
    }
    
    private void ejectGroupMember(final ChatterID.ChatterIDUser chatterIDUser) {
        new AlertDialog.Builder(this.getContext()).setTitle(2131296523).setPositiveButton(2131297165, (DialogInterface$OnClickListener)new _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$3(this, chatterIDUser)).setNegativeButton(2131296424, (DialogInterface$OnClickListener)new _$Lambda$MA84Fd9rUtD4VNMgzavMq_NILXY$1()).create().show();
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
    
    public static Bundle makeSelection(final ChatterID chatterID, @Nullable final UUID uuid) {
        final Bundle selection = ChatterFragment.makeSelection(chatterID);
        if (uuid != null) {
            selection.putString("roleToAdd", uuid.toString());
        }
        return selection;
    }
    
    private void onGroupMemberList(final UUID uuid) {
        Debug.Printf("GroupMemberList: got dataset ID = %s", uuid);
        if (this.userManager != null && this.chatterID instanceof ChatterID.ChatterIDGroup) {
            this.groupMembers.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMembersList(), GroupManager.GroupMembersQuery.create(((ChatterID.ChatterIDGroup)this.chatterID).getChatterUUID(), uuid));
        }
    }
    
    @Override
    public View onCreateView(final LayoutInflater layoutInflater, @Nullable final ViewGroup viewGroup, @Nullable final Bundle bundle) {
        final View inflate = layoutInflater.inflate(2130968650, viewGroup, false);
        ((LoadingLayout)inflate.findViewById(2131755197)).setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        this.adapter = new GroupMemberListRecyclerAdapter(this.getContext());
        ((RecyclerView)inflate.findViewById(2131755422)).setAdapter((RecyclerView.Adapter)this.adapter);
        this.loadableMonitor.setLoadingLayout((LoadingLayout)inflate.findViewById(2131755197), this.getString(2131296736), this.getString(2131296579));
        this.loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)inflate.findViewById(2131755195));
        return inflate;
    }
    
    @Override
    public void onLoadableDataChanged() {
        if (this.adapter != null) {
            this.adapter.setData(this.groupMembers.getData());
            ((RecyclerView.Adapter)this.adapter).notifyDataSetChanged();
        }
        final LazyList list = this.groupMembers.getData();
        this.loadableMonitor.setEmptyMessage(list != null && list.isEmpty(), this.getString(2131296751));
    }
    
    @Override
    protected void onShowUser(@Nullable final ChatterID chatterID) {
        this.loadableMonitor.unsubscribeAll();
        if (this.userManager != null && chatterID instanceof ChatterID.ChatterIDGroup) {
            final UUID chatterUUID = ((ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
            Debug.Printf("GroupMemberList: subscribing for group %s", chatterUUID);
            this.agentCircuit.subscribe(UserManager.agentCircuits(), chatterID.agentUUID);
            this.groupProfile.subscribe(this.userManager.getCachedGroupProfiles().getPool(), chatterUUID);
            this.myGroupList.subscribe(this.userManager.getAvatarGroupLists().getPool(), chatterID.agentUUID);
            this.groupMemberList.subscribe(this.userManager.getChatterList().getGroupManager().getGroupMembers(), chatterUUID);
        }
    }
    
    private class GroupMemberListRecyclerAdapter extends Adapter<GroupMemberViewHolder>
    {
        private final int cardSelectedColor;
        private LazyList<GroupMember> data;
        private final LayoutInflater layoutInflater;
        private int selectedPosition;
        
        GroupMemberListRecyclerAdapter(final Context context) {
            this.data = null;
            this.selectedPosition = -1;
            this.layoutInflater = LayoutInflater.from(context);
            final TypedValue typedValue = new TypedValue();
            context.getTheme().resolveAttribute(2130771970, typedValue, true);
            this.cardSelectedColor = typedValue.data;
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
        
        public void onBindViewHolder(final GroupMemberViewHolder groupMemberViewHolder, final int n) {
            boolean b = false;
            if (this.data != null && (this.data.isClosed() ^ true) && n >= 0 && n < this.data.size()) {
                final GroupMember groupMember = this.data.get(n);
                if (n == this.selectedPosition) {
                    b = true;
                }
                groupMemberViewHolder.bindToData(groupMember, b);
            }
        }
        
        public GroupMemberViewHolder onCreateViewHolder(final ViewGroup viewGroup, final int n) {
            return new GroupMemberViewHolder(this.layoutInflater.inflate(2130968642, viewGroup, false), GroupMembersProfileTab.this.userManager.getUserID(), this.cardSelectedColor);
        }
        
        public void onViewRecycled(final GroupMemberViewHolder groupMemberViewHolder) {
            groupMemberViewHolder.recycle();
        }
        
        public void setData(final LazyList<GroupMember> data) {
            this.data = data;
            this.selectedPosition = -1;
            ((RecyclerView.Adapter)this).notifyDataSetChanged();
        }
        
        public void setSelectedPosition(final int selectedPosition) {
            if (selectedPosition != this.selectedPosition) {
                final int selectedPosition2 = this.selectedPosition;
                this.selectedPosition = selectedPosition;
                if (selectedPosition2 != -1) {
                    ((RecyclerView.Adapter)this).notifyItemChanged(selectedPosition2);
                }
                if (selectedPosition != -1) {
                    ((RecyclerView.Adapter)this).notifyItemChanged(selectedPosition);
                }
            }
        }
    }
    
    private class GroupMemberViewHolder extends ViewHolder implements OnChatterNameUpdated, View$OnClickListener
    {
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
        
        GroupMemberViewHolder(final View view, final UUID agentUUID, final int cardSelectedColor) {
            super(view);
            this.boundChatterID = null;
            this.chatterNameRetriever = null;
            this.agentUUID = agentUUID;
            this.cardView = (CardView)view.findViewById(2131755371);
            this.userNameTextView = (TextView)view.findViewById(2131755328);
            this.userPicView = (ChatterPicView)view.findViewById(2131755327);
            this.userTitleText = (TextView)view.findViewById(2131755372);
            this.userOnlineStatusText = (TextView)view.findViewById(2131755373);
            this.selectedLayout = view.findViewById(2131755374);
            this.groupMemberChatButton = (Button)view.findViewById(2131755375);
            this.groupMemberProfileButton = (Button)view.findViewById(2131755376);
            this.groupMemberRolesButton = (Button)view.findViewById(2131755377);
            this.groupMemberEjectButton = (Button)view.findViewById(2131755378);
            this.cardSelectedElevation = this.cardView.getCardElevation();
            this.cardSelectedColor = cardSelectedColor;
            this.cardView.setOnClickListener((View$OnClickListener)this);
            this.groupMemberChatButton.setOnClickListener((View$OnClickListener)this);
            this.groupMemberProfileButton.setOnClickListener((View$OnClickListener)this);
            this.groupMemberRolesButton.setOnClickListener((View$OnClickListener)this);
            this.groupMemberEjectButton.setOnClickListener((View$OnClickListener)this);
        }
        
        void bindToData(final GroupMember groupMember, final boolean b) {
            final int n = 0;
            final CharSequence charSequence = null;
            ChatterID.ChatterIDWithUUID userChatterID;
            if (groupMember != null) {
                userChatterID = ChatterID.getUserChatterID(this.agentUUID, groupMember.getUserID());
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
            final TextView userTitleText = this.userTitleText;
            String title;
            if (groupMember != null) {
                title = groupMember.getTitle();
            }
            else {
                title = null;
            }
            userTitleText.setText((CharSequence)title);
            final TextView userOnlineStatusText = this.userOnlineStatusText;
            CharSequence onlineStatus = charSequence;
            if (groupMember != null) {
                onlineStatus = groupMember.getOnlineStatus();
            }
            userOnlineStatusText.setText(onlineStatus);
            if (b) {
                this.cardView.setCardElevation(this.cardSelectedElevation);
                this.cardView.setCardBackgroundColor(this.cardSelectedColor);
            }
            else {
                this.cardView.setCardElevation(0.0f);
                this.cardView.setCardBackgroundColor(0);
            }
            final View selectedLayout = this.selectedLayout;
            int visibility;
            if (b) {
                visibility = 0;
            }
            else {
                visibility = 8;
            }
            selectedLayout.setVisibility(visibility);
            final AvatarGroupList.AvatarGroupEntry -wrap0 = GroupMembersProfileTab.this.getMyGroupEntry();
            int n2;
            if (GroupMembersProfileTab.this.agentCircuit != null && -wrap0 != null && (-wrap0.GroupPowers & 0x4L) != 0x0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            final Button groupMemberEjectButton = this.groupMemberEjectButton;
            int visibility2;
            if (n2 != 0) {
                visibility2 = 0;
            }
            else {
                visibility2 = 8;
            }
            groupMemberEjectButton.setVisibility(visibility2);
            final Button groupMemberRolesButton = this.groupMemberRolesButton;
            int visibility3;
            if (-wrap0 != null) {
                visibility3 = n;
            }
            else {
                visibility3 = 8;
            }
            groupMemberRolesButton.setVisibility(visibility3);
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
                case 2131755371: {
                    if (GroupMembersProfileTab.this.getArguments().containsKey("roleToAdd")) {
                        if (this.boundChatterID != null) {
                            GroupMembersProfileTab.this.addGroupRoleMember(this.boundChatterID);
                            break;
                        }
                        break;
                    }
                    else {
                        if (GroupMembersProfileTab.this.adapter != null) {
                            GroupMembersProfileTab.this.adapter.setSelectedPosition(((RecyclerView.ViewHolder)this).getAdapterPosition());
                            break;
                        }
                        break;
                    }
                    break;
                }
                case 2131755375: {
                    if (this.boundChatterID != null) {
                        DetailsActivity.showDetails(GroupMembersProfileTab.this.getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(this.boundChatterID));
                        break;
                    }
                    break;
                }
                case 2131755376: {
                    DetailsActivity.showEmbeddedDetails(GroupMembersProfileTab.this.getActivity(), UserProfileFragment.class, UserProfileFragment.makeSelection(this.boundChatterID));
                    break;
                }
                case 2131755377: {
                    if (this.boundChatterID != null) {
                        DetailsActivity.showEmbeddedDetails(GroupMembersProfileTab.this.getActivity(), GroupMemberRolesFragment.class, GroupMemberRolesFragment.makeSelection(GroupMembersProfileTab.this.chatterID, this.boundChatterID.getChatterUUID()));
                        break;
                    }
                    break;
                }
                case 2131755378: {
                    if (this.boundChatterID != null) {
                        GroupMembersProfileTab.this.ejectGroupMember(this.boundChatterID);
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
