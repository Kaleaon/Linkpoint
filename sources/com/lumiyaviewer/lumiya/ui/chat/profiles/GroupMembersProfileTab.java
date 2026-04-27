// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.GroupMember;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.modules.groups.SLGroupManager;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.ChatterList;
import com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.ChatFragment;
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.query.LazyList;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserProfileFragment, GroupMemberRolesFragment

public class GroupMembersProfileTab extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{
    private class GroupMemberListRecyclerAdapter extends android.support.v7.widget.RecyclerView.Adapter
    {

        private final int cardSelectedColor;
        private LazyList data;
        private final LayoutInflater layoutInflater;
        private int selectedPosition;
        final GroupMembersProfileTab this$0;

        public int getItemCount()
        {
            if (data != null)
            {
                return data.size();
            } else
            {
                return 0;
            }
        }

        public volatile void onBindViewHolder(android.support.v7.widget.RecyclerView.ViewHolder viewholder, int i)
        {
            onBindViewHolder((GroupMemberViewHolder)viewholder, i);
        }

        public void onBindViewHolder(GroupMemberViewHolder groupmemberviewholder, int i)
        {
            boolean flag = false;
            if (data != null && data.isClosed() ^ true && i >= 0 && i < data.size())
            {
                GroupMember groupmember = (GroupMember)data.get(i);
                if (i == selectedPosition)
                {
                    flag = true;
                }
                groupmemberviewholder.bindToData(groupmember, flag);
            }
        }

        public volatile android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
        {
            return onCreateViewHolder(viewgroup, i);
        }

        public GroupMemberViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
        {
            viewgroup = layoutInflater.inflate(0x7f040042, viewgroup, false);
            return new GroupMemberViewHolder(viewgroup, GroupMembersProfileTab._2D_get3(GroupMembersProfileTab.this).getUserID(), cardSelectedColor);
        }

        public volatile void onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
        {
            onViewRecycled((GroupMemberViewHolder)viewholder);
        }

        public void onViewRecycled(GroupMemberViewHolder groupmemberviewholder)
        {
            groupmemberviewholder.recycle();
        }

        public void setData(LazyList lazylist)
        {
            data = lazylist;
            selectedPosition = -1;
            notifyDataSetChanged();
        }

        public void setSelectedPosition(int i)
        {
            if (i != selectedPosition)
            {
                int j = selectedPosition;
                selectedPosition = i;
                if (j != -1)
                {
                    notifyItemChanged(j);
                }
                if (i != -1)
                {
                    notifyItemChanged(i);
                }
            }
        }

        GroupMemberListRecyclerAdapter(Context context)
        {
            this$0 = GroupMembersProfileTab.this;
            super();
            data = null;
            selectedPosition = -1;
            layoutInflater = LayoutInflater.from(context);
            groupmembersprofiletab = new TypedValue();
            context.getTheme().resolveAttribute(0x7f010002, GroupMembersProfileTab.this, true);
            cardSelectedColor = GroupMembersProfileTab.this.data;
        }
    }

    private class GroupMemberViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
        implements com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated, android.view.View.OnClickListener
    {

        private final UUID agentUUID;
        private com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser boundChatterID;
        private final int cardSelectedColor;
        private final float cardSelectedElevation;
        private final CardView cardView;
        private ChatterNameRetriever chatterNameRetriever;
        private final Button groupMemberChatButton;
        private final Button groupMemberEjectButton;
        private final Button groupMemberProfileButton;
        private final Button groupMemberRolesButton;
        private final View selectedLayout;
        final GroupMembersProfileTab this$0;
        private final TextView userNameTextView;
        private final TextView userOnlineStatusText;
        private final ChatterPicView userPicView;
        private final TextView userTitleText;

        void bindToData(GroupMember groupmember, boolean flag)
        {
            boolean flag1 = false;
            Object obj1 = null;
            Object obj;
            int i;
            if (groupmember != null)
            {
                obj = ChatterID.getUserChatterID(agentUUID, groupmember.getUserID());
            } else
            {
                obj = null;
            }
            if (!Objects.equal(obj, boundChatterID))
            {
                if (chatterNameRetriever != null)
                {
                    chatterNameRetriever.dispose();
                    chatterNameRetriever = null;
                }
                userNameTextView.setText(null);
                boundChatterID = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser) (obj));
                TextView textview;
                if (obj != null)
                {
                    chatterNameRetriever = new ChatterNameRetriever(boundChatterID, this, UIThreadExecutor.getInstance());
                    userPicView.setChatterID(((ChatterID) (obj)), chatterNameRetriever.getResolvedName());
                } else
                {
                    userPicView.setChatterID(null, null);
                }
            }
            textview = userTitleText;
            if (groupmember != null)
            {
                obj = groupmember.getTitle();
            } else
            {
                obj = null;
            }
            textview.setText(((CharSequence) (obj)));
            textview = userOnlineStatusText;
            obj = obj1;
            if (groupmember != null)
            {
                obj = groupmember.getOnlineStatus();
            }
            textview.setText(((CharSequence) (obj)));
            if (flag)
            {
                cardView.setCardElevation(cardSelectedElevation);
                cardView.setCardBackgroundColor(cardSelectedColor);
            } else
            {
                cardView.setCardElevation(0.0F);
                cardView.setCardBackgroundColor(0);
            }
            groupmember = selectedLayout;
            if (flag)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            groupmember.setVisibility(i);
            groupmember = GroupMembersProfileTab._2D_wrap0(GroupMembersProfileTab.this);
            if (GroupMembersProfileTab._2D_get1(GroupMembersProfileTab.this) != null && groupmember != null && (((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) (groupmember)).GroupPowers & 4L) != 0L)
            {
                i = 1;
            } else
            {
                i = 0;
            }
            obj = groupMemberEjectButton;
            if (i != 0)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            ((Button) (obj)).setVisibility(i);
            obj = groupMemberRolesButton;
            if (groupmember != null)
            {
                i = ((flag1) ? 1 : 0);
            } else
            {
                i = 8;
            }
            ((Button) (obj)).setVisibility(i);
        }

        public void onChatterNameUpdated(ChatterNameRetriever chatternameretriever)
        {
            if (chatternameretriever != null)
            {
                userNameTextView.setText(chatternameretriever.getResolvedName());
                userPicView.setChatterID(chatternameretriever.chatterID, chatternameretriever.getResolvedName());
            }
        }

        public void onClick(View view)
        {
            view.getId();
            JVM INSTR tableswitch 2131755371 2131755378: default 52
        //                       2131755371 53
        //                       2131755372 52
        //                       2131755373 52
        //                       2131755374 52
        //                       2131755375 112
        //                       2131755376 140
        //                       2131755377 161
        //                       2131755378 200;
               goto _L1 _L2 _L1 _L1 _L1 _L3 _L4 _L5 _L6
_L1:
            return;
_L2:
            if (!getArguments().containsKey("roleToAdd"))
            {
                continue; /* Loop/switch isn't completed */
            }
            if (boundChatterID != null)
            {
                GroupMembersProfileTab._2D_wrap1(GroupMembersProfileTab.this, boundChatterID);
                return;
            }
            continue; /* Loop/switch isn't completed */
            if (GroupMembersProfileTab._2D_get0(GroupMembersProfileTab.this) == null) goto _L1; else goto _L7
_L7:
            GroupMembersProfileTab._2D_get0(GroupMembersProfileTab.this).setSelectedPosition(getAdapterPosition());
            return;
_L3:
            if (boundChatterID != null)
            {
                DetailsActivity.showDetails(getActivity(), ChatFragmentActivityFactory.getInstance(), ChatFragment.makeSelection(boundChatterID));
                return;
            }
            continue; /* Loop/switch isn't completed */
_L4:
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment, UserProfileFragment.makeSelection(boundChatterID));
            return;
_L5:
            if (boundChatterID != null)
            {
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupMemberRolesFragment, GroupMemberRolesFragment.makeSelection(GroupMembersProfileTab._2D_get2(GroupMembersProfileTab.this), boundChatterID.getChatterUUID()));
                return;
            }
            continue; /* Loop/switch isn't completed */
_L6:
            if (boundChatterID != null)
            {
                GroupMembersProfileTab._2D_wrap2(GroupMembersProfileTab.this, boundChatterID);
                return;
            }
            if (true) goto _L1; else goto _L8
_L8:
        }

        void recycle()
        {
            if (chatterNameRetriever != null)
            {
                chatterNameRetriever.dispose();
                chatterNameRetriever = null;
            }
            boundChatterID = null;
            userPicView.setChatterID(null, null);
        }

        GroupMemberViewHolder(View view, UUID uuid, int i)
        {
            this$0 = GroupMembersProfileTab.this;
            super(view);
            boundChatterID = null;
            chatterNameRetriever = null;
            agentUUID = uuid;
            cardView = (CardView)view.findViewById(0x7f10016b);
            userNameTextView = (TextView)view.findViewById(0x7f100140);
            userPicView = (ChatterPicView)view.findViewById(0x7f10013f);
            userTitleText = (TextView)view.findViewById(0x7f10016c);
            userOnlineStatusText = (TextView)view.findViewById(0x7f10016d);
            selectedLayout = view.findViewById(0x7f10016e);
            groupMemberChatButton = (Button)view.findViewById(0x7f10016f);
            groupMemberProfileButton = (Button)view.findViewById(0x7f100170);
            groupMemberRolesButton = (Button)view.findViewById(0x7f100171);
            groupMemberEjectButton = (Button)view.findViewById(0x7f100172);
            cardSelectedElevation = cardView.getCardElevation();
            cardSelectedColor = i;
            cardView.setOnClickListener(this);
            groupMemberChatButton.setOnClickListener(this);
            groupMemberProfileButton.setOnClickListener(this);
            groupMemberRolesButton.setOnClickListener(this);
            groupMemberEjectButton.setOnClickListener(this);
        }
    }


    private static final String ROLE_TO_ADD_KEY = "roleToAdd";
    private GroupMemberListRecyclerAdapter adapter;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupMemberList = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.MA84Fd9rUtD4VNMgzavMq_NILXY._cls2(this));
    private final SubscriptionData groupMembers = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupProfile = new SubscriptionData(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData myGroupList = new SubscriptionData(UIThreadExecutor.getInstance());

    static GroupMemberListRecyclerAdapter _2D_get0(GroupMembersProfileTab groupmembersprofiletab)
    {
        return groupmembersprofiletab.adapter;
    }

    static SubscriptionData _2D_get1(GroupMembersProfileTab groupmembersprofiletab)
    {
        return groupmembersprofiletab.agentCircuit;
    }

    static ChatterID _2D_get2(GroupMembersProfileTab groupmembersprofiletab)
    {
        return groupmembersprofiletab.chatterID;
    }

    static UserManager _2D_get3(GroupMembersProfileTab groupmembersprofiletab)
    {
        return groupmembersprofiletab.userManager;
    }

    static com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry _2D_wrap0(GroupMembersProfileTab groupmembersprofiletab)
    {
        return groupmembersprofiletab.getMyGroupEntry();
    }

    static void _2D_wrap1(GroupMembersProfileTab groupmembersprofiletab, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        groupmembersprofiletab.addGroupRoleMember(chatteriduser);
    }

    static void _2D_wrap2(GroupMembersProfileTab groupmembersprofiletab, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        groupmembersprofiletab.ejectGroupMember(chatteriduser);
    }

    public GroupMembersProfileTab()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            groupMemberList, myGroupList, groupProfile, groupMembers
        })).withOptionalLoadables(new Loadable[] {
            agentCircuit
        }).withDataChangedListener(this);
        adapter = null;
    }

    private void addGroupRoleMember(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        UUID uuid = UUIDPool.getUUID(getArguments().getString("roleToAdd"));
        if (uuid != null)
        {
            (new android.support.v7.app.AlertDialog.Builder(getContext())).setTitle(0x7f090040).setPositiveButton(0x7f09038c, new _2D_.Lambda.MA84Fd9rUtD4VNMgzavMq_NILXY._cls4(this, uuid, chatteriduser)).setNegativeButton(0x7f0900a8, new _2D_.Lambda.MA84Fd9rUtD4VNMgzavMq_NILXY()).create().show();
        }
    }

    private void ejectGroupMember(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        (new android.support.v7.app.AlertDialog.Builder(getContext())).setTitle(0x7f09010b).setPositiveButton(0x7f09038d, new _2D_.Lambda.MA84Fd9rUtD4VNMgzavMq_NILXY._cls3(this, chatteriduser)).setNegativeButton(0x7f0900a8, new _2D_.Lambda.MA84Fd9rUtD4VNMgzavMq_NILXY._cls1()).create().show();
    }

    private com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry getMyGroupEntry()
    {
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
        try
        {
            avatargroupentry = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID);
        }
        catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
        {
            return null;
        }
        return avatargroupentry;
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_14315(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_15102(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(ChatterID chatterid, UUID uuid)
    {
        chatterid = ChatterFragment.makeSelection(chatterid);
        if (uuid != null)
        {
            chatterid.putString("roleToAdd", uuid.toString());
        }
        return chatterid;
    }

    private void onGroupMemberList(UUID uuid)
    {
        Debug.Printf("GroupMemberList: got dataset ID = %s", new Object[] {
            uuid
        });
        if (userManager != null && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            UUID uuid1 = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID();
            groupMembers.subscribe(userManager.getChatterList().getGroupManager().getGroupMembersList(), com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager.GroupMembersQuery.create(uuid1, uuid));
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_2D_mthref_2D_0(UUID uuid)
    {
        onGroupMemberList(uuid);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_13581(UUID uuid, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, DialogInterface dialoginterface, int i)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.AddMemberToRole(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID, uuid, chatteriduser.getChatterUUID());
        }
        // Misplaced declaration of an exception variable
        catch (UUID uuid)
        {
            Debug.Warning(uuid);
        }
        dialoginterface.dismiss();
        uuid = getActivity();
        if (uuid instanceof DetailsActivity)
        {
            ((DetailsActivity)uuid).closeDetailsFragment(this);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMembersProfileTab_14656(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, DialogInterface dialoginterface, int i)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.RequestEjectFromGroup(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID, chatteriduser.getChatterUUID());
        }
        // Misplaced declaration of an exception variable
        catch (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
        {
            Debug.Warning(chatteriduser);
        }
        dialoginterface.dismiss();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f04004a, viewgroup, false);
        ((LoadingLayout)layoutinflater.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        adapter = new GroupMemberListRecyclerAdapter(getContext());
        ((RecyclerView)layoutinflater.findViewById(0x7f10019e)).setAdapter(adapter);
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901e0), getString(0x7f090143));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        return layoutinflater;
    }

    public void onLoadableDataChanged()
    {
        if (adapter != null)
        {
            adapter.setData((LazyList)groupMembers.getData());
            adapter.notifyDataSetChanged();
        }
        LazyList lazylist = (LazyList)groupMembers.getData();
        LoadableMonitor loadablemonitor = loadableMonitor;
        boolean flag;
        if (lazylist != null)
        {
            flag = lazylist.isEmpty();
        } else
        {
            flag = false;
        }
        loadablemonitor.setEmptyMessage(flag, getString(0x7f0901ef));
    }

    protected void onShowUser(ChatterID chatterid)
    {
        loadableMonitor.unsubscribeAll();
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID();
            Debug.Printf("GroupMemberList: subscribing for group %s", new Object[] {
                uuid
            });
            agentCircuit.subscribe(UserManager.agentCircuits(), chatterid.agentUUID);
            groupProfile.subscribe(userManager.getCachedGroupProfiles().getPool(), uuid);
            myGroupList.subscribe(userManager.getAvatarGroupLists().getPool(), chatterid.agentUUID);
            groupMemberList.subscribe(userManager.getChatterList().getGroupManager().getGroupMembers(), uuid);
        }
    }
}
