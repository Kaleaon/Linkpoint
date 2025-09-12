// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

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
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.dao.GroupRoleMember;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
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
import com.lumiyaviewer.lumiya.ui.chat.ChatterPicView;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import de.greenrobot.dao.query.LazyList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupMembersProfileTab

public class GroupRoleMembersFragment extends ChatterFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{
    private class MemberListAdapter extends android.support.v7.widget.RecyclerView.Adapter
    {

        private boolean canDeleteMembers;
        private boolean canDeleteMyself;
        private LazyList data;
        private final LayoutInflater layoutInflater;
        final GroupRoleMembersFragment this$0;

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
            onBindViewHolder((MemberViewHolder)viewholder, i);
        }

        public void onBindViewHolder(MemberViewHolder memberviewholder, int i)
        {
            if (data != null && i >= 0 && i < data.size())
            {
                memberviewholder.bindToData((GroupRoleMember)data.get(i), canDeleteMembers, canDeleteMyself);
            }
        }

        public volatile android.support.v7.widget.RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
        {
            return onCreateViewHolder(viewgroup, i);
        }

        public MemberViewHolder onCreateViewHolder(ViewGroup viewgroup, int i)
        {
            viewgroup = layoutInflater.inflate(0x7f04004d, viewgroup, false);
            return new MemberViewHolder(viewgroup, GroupRoleMembersFragment._2D_get0(GroupRoleMembersFragment.this).getUserID());
        }

        public volatile void onViewRecycled(android.support.v7.widget.RecyclerView.ViewHolder viewholder)
        {
            onViewRecycled((MemberViewHolder)viewholder);
        }

        public void onViewRecycled(MemberViewHolder memberviewholder)
        {
            memberviewholder.recycle();
        }

        public void setData(LazyList lazylist, boolean flag, boolean flag1)
        {
            data = lazylist;
            canDeleteMembers = flag;
            canDeleteMyself = flag1;
            notifyDataSetChanged();
        }

        MemberListAdapter(Context context)
        {
            this$0 = GroupRoleMembersFragment.this;
            super();
            data = null;
            canDeleteMembers = false;
            canDeleteMyself = false;
            layoutInflater = LayoutInflater.from(context);
        }
    }

    private class MemberViewHolder extends android.support.v7.widget.RecyclerView.ViewHolder
        implements com.lumiyaviewer.lumiya.slproto.users.ChatterNameRetriever.OnChatterNameUpdated, android.view.View.OnClickListener
    {

        private final UUID agentUUID;
        private com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser boundChatterID;
        private boolean canDelete;
        private ChatterNameRetriever chatterNameRetriever;
        private final ImageButton roleMemberRemoveButton;
        final GroupRoleMembersFragment this$0;
        private final TextView userNameTextView;
        private final ChatterPicView userPicView;

        void bindToData(GroupRoleMember grouprolemember, boolean flag, boolean flag1)
        {
            int i = 0;
            if (grouprolemember != null)
            {
                grouprolemember = ChatterID.getUserChatterID(agentUUID, grouprolemember.getUserID());
            } else
            {
                grouprolemember = null;
            }
            if (!Objects.equal(grouprolemember, boundChatterID))
            {
                if (chatterNameRetriever != null)
                {
                    chatterNameRetriever.dispose();
                    chatterNameRetriever = null;
                }
                userNameTextView.setText(null);
                boundChatterID = grouprolemember;
                if (grouprolemember != null)
                {
                    chatterNameRetriever = new ChatterNameRetriever(boundChatterID, this, UIThreadExecutor.getInstance());
                    userPicView.setChatterID(grouprolemember, chatterNameRetriever.getResolvedName());
                } else
                {
                    userPicView.setChatterID(null, null);
                }
            }
            if (!flag)
            {
                if (!agentUUID.equals(boundChatterID.getChatterUUID()))
                {
                    flag1 = false;
                }
            } else
            {
                flag1 = true;
            }
            canDelete = flag1;
            grouprolemember = roleMemberRemoveButton;
            if (!canDelete)
            {
                i = 8;
            }
            grouprolemember.setVisibility(i);
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
            JVM INSTR tableswitch 2131755436 2131755436: default 24
        //                       2131755436 25;
               goto _L1 _L2
_L1:
            return;
_L2:
            if (boundChatterID != null && canDelete)
            {
                GroupRoleMembersFragment._2D_wrap0(GroupRoleMembersFragment.this, boundChatterID);
                return;
            }
            if (true) goto _L1; else goto _L3
_L3:
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

        MemberViewHolder(View view, UUID uuid)
        {
            this$0 = GroupRoleMembersFragment.this;
            super(view);
            canDelete = false;
            boundChatterID = null;
            chatterNameRetriever = null;
            agentUUID = uuid;
            userNameTextView = (TextView)view.findViewById(0x7f100140);
            userPicView = (ChatterPicView)view.findViewById(0x7f10013f);
            roleMemberRemoveButton = (ImageButton)view.findViewById(0x7f1001ac);
            roleMemberRemoveButton.setOnClickListener(this);
        }
    }


    private static final String ROLE_ID_KEY = "role_id";
    private UUID RoleID;
    private MemberListAdapter adapter;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance());
    private boolean canAddMembers;
    private final SubscriptionData groupProfile = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupRoleMemberList = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.TbI0imFFmZKCR9nUgEGSvi_2D__8Q0._cls2(this));
    private final SubscriptionData groupTitles = new SubscriptionData(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData myGroupList = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData roleMembers = new SubscriptionData(UIThreadExecutor.getInstance());

    static UserManager _2D_get0(GroupRoleMembersFragment grouprolemembersfragment)
    {
        return grouprolemembersfragment.userManager;
    }

    static void _2D_wrap0(GroupRoleMembersFragment grouprolemembersfragment, com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        grouprolemembersfragment.removeMemberFromRole(chatteriduser);
    }

    public GroupRoleMembersFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            groupRoleMemberList, myGroupList, groupProfile, roleMembers
        })).withOptionalLoadables(new Loadable[] {
            agentCircuit, groupTitles
        }).withDataChangedListener(this);
        canAddMembers = false;
        adapter = null;
    }

    private com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry getMyGroupEntry()
    {
        if (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)
        {
            com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
            try
            {
                avatargroupentry = (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID());
            }
            catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
            {
                return null;
            }
            return avatargroupentry;
        } else
        {
            return null;
        }
    }

    private long getMyGroupPowers()
    {
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry = getMyGroupEntry();
        if (avatargroupentry != null)
        {
            return avatargroupentry.GroupPowers;
        } else
        {
            return 0L;
        }
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_11957(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    public static Bundle makeSelection(ChatterID chatterid, UUID uuid)
    {
        chatterid = ChatterFragment.makeSelection(chatterid);
        if (uuid != null)
        {
            chatterid.putString("role_id", uuid.toString());
        }
        return chatterid;
    }

    private void onGroupRoleMemberList(UUID uuid)
    {
        if (userManager != null && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup) && RoleID != null)
        {
            roleMembers.subscribe(userManager.getChatterList().getGroupManager().getGroupRoleMemberList(), com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager.GroupRoleMembersQuery.create(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID(), RoleID, uuid));
        }
    }

    private void removeMemberFromRole(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
    {
        (new android.app.AlertDialog.Builder(getContext())).setTitle(0x7f0902a2).setPositiveButton(0x7f090390, new _2D_.Lambda.TbI0imFFmZKCR9nUgEGSvi_2D__8Q0._cls3(this, chatteriduser)).setNegativeButton(0x7f0900a8, new _2D_.Lambda.TbI0imFFmZKCR9nUgEGSvi_2D__8Q0()).create().show();
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_2D_mthref_2D_0(UUID uuid)
    {
        onGroupRoleMemberList(uuid);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_11502(com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser, DialogInterface dialoginterface, int i)
    {
        try
        {
            ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.RemoveMemberFromRole(((GroupProfileReply)groupProfile.get()).GroupData_Field.GroupID, RoleID, chatteriduser.getChatterUUID());
        }
        // Misplaced declaration of an exception variable
        catch (com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser chatteriduser)
        {
            Debug.Warning(chatteriduser);
        }
        dialoginterface.dismiss();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRoleMembersFragment_4418(View view)
    {
        if (canAddMembers)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupMembersProfileTab, GroupMembersProfileTab.makeSelection(chatterID, RoleID));
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f040048, viewgroup, false);
        ((LoadingLayout)layoutinflater.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        layoutinflater.findViewById(0x7f100180).setOnClickListener(new _2D_.Lambda.TbI0imFFmZKCR9nUgEGSvi_2D__8Q0._cls1(this));
        adapter = new MemberListAdapter(getContext());
        ((RecyclerView)layoutinflater.findViewById(0x7f10017f)).setAdapter(adapter);
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901e0), getString(0x7f090151));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        return layoutinflater;
    }

    public void onLoadableDataChanged()
    {
        boolean flag;
        long l;
        boolean flag3;
        flag3 = true;
        flag = false;
        if (userManager != null)
        {
            com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry = getMyGroupEntry();
            if (avatargroupentry != null && !groupTitles.isSubscribed())
            {
                groupTitles.subscribe(userManager.getGroupTitles().getPool(), avatargroupentry.GroupID);
            }
        }
        canAddMembers = false;
        l = getMyGroupPowers();
        if ((256L & l) == 0L) goto _L2; else goto _L1
_L1:
        canAddMembers = true;
_L4:
        int i;
        boolean flag1;
        boolean flag2;
        if ((l & 512L) != 0L)
        {
            Object obj = (GroupProfileReply)groupProfile.getData();
            if (obj != null && RoleID != null)
            {
                flag1 = RoleID.equals(((GroupProfileReply) (obj)).GroupData_Field.OwnerRole);
            } else
            {
                flag1 = false;
            }
            if (flag1)
            {
                flag2 = false;
                flag1 = flag3;
            } else
            {
                flag2 = true;
                flag1 = false;
            }
        } else
        {
            flag1 = false;
            flag2 = false;
        }
        obj = getView();
        if (obj != null)
        {
            obj = ((View) (obj)).findViewById(0x7f100180);
            if (canAddMembers)
            {
                i = ((flag) ? 1 : 0);
            } else
            {
                i = 8;
            }
            ((View) (obj)).setVisibility(i);
        }
        if (adapter != null)
        {
            adapter.setData((LazyList)roleMembers.getData(), flag2, flag1);
        }
        return;
_L2:
        if ((128L & l) == 0L) goto _L4; else goto _L3
_L3:
        obj = (GroupTitlesReply)groupTitles.getData();
        if (obj == null) goto _L4; else goto _L5
_L5:
        obj = ((GroupTitlesReply) (obj)).GroupData_Fields.iterator();
        do
        {
            if (!((Iterator) (obj)).hasNext())
            {
                break MISSING_BLOCK_LABEL_311;
            }
        } while (!((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)((Iterator) (obj)).next()).RoleID.equals(RoleID));
        i = 1;
_L6:
        if (i != 0)
        {
            canAddMembers = true;
        }
          goto _L4
        i = 0;
          goto _L6
    }

    protected void onShowUser(ChatterID chatterid)
    {
        loadableMonitor.unsubscribeAll();
        RoleID = UUIDPool.getUUID(getArguments().getString("role_id"));
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID();
            Debug.Printf("GroupRoleMemberList: subscribing for group %s", new Object[] {
                uuid
            });
            agentCircuit.subscribe(UserManager.agentCircuits(), chatterid.agentUUID);
            groupProfile.subscribe(userManager.getCachedGroupProfiles().getPool(), uuid);
            myGroupList.subscribe(userManager.getAvatarGroupLists().getPool(), chatterid.agentUUID);
            groupRoleMemberList.subscribe(userManager.getChatterList().getGroupManager().getGroupRoleMembers(), uuid);
        }
    }
}
