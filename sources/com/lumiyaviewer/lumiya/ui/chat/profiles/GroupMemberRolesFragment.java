// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import android.widget.ListView;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
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
import com.lumiyaviewer.lumiya.ui.common.BackButtonHandler;
import com.lumiyaviewer.lumiya.ui.common.ChatterFragment;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class GroupMemberRolesFragment extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener, BackButtonHandler
{
    private class MemberRoleAdapter extends BaseAdapter
    {

        private GroupRoleDataReply data;
        private final Set selectedRoles;
        final GroupMemberRolesFragment this$0;

        public int getCount()
        {
            if (data != null)
            {
                return data.RoleData_Fields.size();
            } else
            {
                return 0;
            }
        }

        public com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData getItem(int i)
        {
            if (data != null && i >= 0 && i < data.RoleData_Fields.size())
            {
                return (com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData)data.RoleData_Fields.get(i);
            } else
            {
                return null;
            }
        }

        public volatile Object getItem(int i)
        {
            return getItem(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }

        public Set getSelectedRoles()
        {
            return selectedRoles;
        }

        public View getView(int i, View view, ViewGroup viewgroup)
        {
            View view1 = view;
            if (view == null)
            {
                view1 = LayoutInflater.from(getContext()).inflate(0x7f040043, viewgroup, false);
            }
            view = getItem(i);
            if (view != null)
            {
                ((CheckedTextView)view1.findViewById(0x7f100173)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (view)).Name));
                viewgroup = (CheckedTextView)view1.findViewById(0x7f100173);
                boolean flag;
                if (!((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (view)).RoleID.equals(UUIDPool.ZeroUUID))
                {
                    flag = selectedRoles.contains(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (view)).RoleID);
                } else
                {
                    flag = true;
                }
                viewgroup.setChecked(flag);
            }
            return view1;
        }

        public boolean hasStableIds()
        {
            return false;
        }

        public void setData(GroupRoleDataReply grouproledatareply, Set set)
        {
            data = grouproledatareply;
            selectedRoles.clear();
            if (set != null)
            {
                selectedRoles.addAll(set);
            }
            GroupMemberRolesFragment._2D_wrap1(GroupMemberRolesFragment.this);
            notifyDataSetInvalidated();
        }

        public void toggleChecked(UUID uuid)
        {
            if (uuid.equals(UUIDPool.ZeroUUID) || GroupMemberRolesFragment._2D_get4(GroupMemberRolesFragment.this) == null || GroupMemberRolesFragment._2D_get0(GroupMemberRolesFragment.this) == null) goto _L2; else goto _L1
_L1:
            long l = GroupMemberRolesFragment._2D_wrap0(GroupMemberRolesFragment.this);
            boolean flag1;
            boolean flag2;
            flag1 = ((Set)GroupMemberRolesFragment._2D_get1(GroupMemberRolesFragment.this).get()).contains(uuid);
            flag2 = selectedRoles.contains(uuid) ^ true;
            if (flag1 != flag2) goto _L4; else goto _L3
_L3:
            if (!flag2) goto _L6; else goto _L5
_L5:
            selectedRoles.add(uuid);
_L7:
            boolean flag = true;
_L13:
            if (flag)
            {
                GroupMemberRolesFragment._2D_wrap1(GroupMemberRolesFragment.this);
                notifyDataSetChanged();
            }
_L2:
            return;
_L6:
            selectedRoles.remove(uuid);
              goto _L7
_L4:
            if (flag2) goto _L9; else goto _L8
_L8:
            if ((l & 512L) == 0L) goto _L11; else goto _L10
_L10:
            flag1 = uuid.equals(((GroupProfileReply)GroupMemberRolesFragment._2D_get2(GroupMemberRolesFragment.this).get()).GroupData_Field.OwnerRole);
            flag2 = GroupMemberRolesFragment._2D_get4(GroupMemberRolesFragment.this).getUserID().equals(GroupMemberRolesFragment._2D_get0(GroupMemberRolesFragment.this));
            if (flag1 && !flag2) goto _L11; else goto _L12
_L12:
            selectedRoles.remove(uuid);
            flag = true;
              goto _L13
_L21:
            if (!flag) goto _L11; else goto _L14
_L14:
            selectedRoles.add(uuid);
            flag = true;
              goto _L13
_L23:
            if ((l & 128L) == 0L) goto _L16; else goto _L15
_L15:
            Object obj = (GroupTitlesReply)GroupMemberRolesFragment._2D_get3(GroupMemberRolesFragment.this).getData();
            if (obj == null) goto _L16; else goto _L17
_L17:
            obj = ((GroupTitlesReply) (obj)).GroupData_Fields.iterator();
_L20:
            if (!((Iterator) (obj)).hasNext()) goto _L16; else goto _L18
_L18:
            flag1 = ((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)((Iterator) (obj)).next()).RoleID.equals(uuid);
            if (!flag1) goto _L20; else goto _L19
_L19:
            flag = true;
              goto _L21
_L11:
            flag = false;
              goto _L13
_L16:
            flag = false;
              goto _L21
            uuid;
            flag = false;
              goto _L13
_L9:
            if ((256L & l) == 0L) goto _L23; else goto _L22
_L22:
            flag = true;
              goto _L21
        }

        private MemberRoleAdapter()
        {
            this$0 = GroupMemberRolesFragment.this;
            super();
            data = null;
            selectedRoles = new HashSet();
        }

        MemberRoleAdapter(MemberRoleAdapter memberroleadapter)
        {
            this();
        }
    }


    private static final String MEMBER_ID_KEY = "memberID";
    private UUID MemberID;
    private final SubscriptionData activeRoles = new SubscriptionData(UIThreadExecutor.getInstance());
    private MemberRoleAdapter adapter;
    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupProfile = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupRoleMemberList = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.jWSiK5iq_2D_zZfaogto6grdML6fzQ._cls3(this));
    private final SubscriptionData groupRoles = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupTitles = new SubscriptionData(UIThreadExecutor.getInstance());
    private boolean hasChanged;
    private final LoadableMonitor loadableMonitor;
    private ChatterNameRetriever memberNameRetriever;
    private final SubscriptionData myGroupList = new SubscriptionData(UIThreadExecutor.getInstance());
    private MenuItem undoMenuItem;

    static UUID _2D_get0(GroupMemberRolesFragment groupmemberrolesfragment)
    {
        return groupmemberrolesfragment.MemberID;
    }

    static SubscriptionData _2D_get1(GroupMemberRolesFragment groupmemberrolesfragment)
    {
        return groupmemberrolesfragment.activeRoles;
    }

    static SubscriptionData _2D_get2(GroupMemberRolesFragment groupmemberrolesfragment)
    {
        return groupmemberrolesfragment.groupProfile;
    }

    static SubscriptionData _2D_get3(GroupMemberRolesFragment groupmemberrolesfragment)
    {
        return groupmemberrolesfragment.groupTitles;
    }

    static UserManager _2D_get4(GroupMemberRolesFragment groupmemberrolesfragment)
    {
        return groupmemberrolesfragment.userManager;
    }

    static long _2D_wrap0(GroupMemberRolesFragment groupmemberrolesfragment)
    {
        return groupmemberrolesfragment.getMyGroupPowers();
    }

    static void _2D_wrap1(GroupMemberRolesFragment groupmemberrolesfragment)
    {
        groupmemberrolesfragment.updateUnsavedChanges();
    }

    public GroupMemberRolesFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            groupRoles, groupProfile, myGroupList, groupRoleMemberList, activeRoles
        })).withOptionalLoadables(new Loadable[] {
            agentCircuit, groupTitles
        }).withDataChangedListener(this);
        MemberID = null;
        memberNameRetriever = null;
        adapter = null;
        hasChanged = false;
    }

    private boolean anyChanges()
    {
        Set set = (Set)activeRoles.getData();
        if (adapter != null && set != null)
        {
            return set.equals(adapter.getSelectedRoles()) ^ true;
        } else
        {
            return false;
        }
    }

    private void closeFragment()
    {
        android.support.v4.app.FragmentActivity fragmentactivity = getActivity();
        if (fragmentactivity instanceof DetailsActivity)
        {
            ((DetailsActivity)fragmentactivity).closeDetailsFragment(this);
        }
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

    public static Bundle makeSelection(ChatterID chatterid, UUID uuid)
    {
        chatterid = ChatterFragment.makeSelection(chatterid);
        if (uuid != null)
        {
            chatterid.putString("memberID", uuid.toString());
        }
        return chatterid;
    }

    private void onGroupRoleMemberList(UUID uuid)
    {
        if (userManager != null && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup) && MemberID != null)
        {
            activeRoles.subscribe(userManager.getChatterList().getGroupManager().getGroupMemberRoleList(), com.lumiyaviewer.lumiya.slproto.users.manager.GroupManager.GroupMemberRolesQuery.create(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID(), MemberID, uuid));
        }
    }

    private void onMemberNameUpdated(ChatterNameRetriever chatternameretriever)
    {
        chatternameretriever = chatternameretriever.getResolvedName();
        if (!Strings.isNullOrEmpty(chatternameretriever))
        {
            setTitle(getString(0x7f0901a3, new Object[] {
                chatternameretriever
            }), null);
            return;
        } else
        {
            setTitle(getString(0x7f0901c8), null);
            return;
        }
    }

    private void updateUnsavedChanges()
    {
        boolean flag = anyChanges();
        if (flag != hasChanged)
        {
            hasChanged = flag;
            if (undoMenuItem != null)
            {
                undoMenuItem.setVisible(hasChanged);
            }
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_2D_mthref_2D_0(UUID uuid)
    {
        onGroupRoleMemberList(uuid);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_2D_mthref_2D_1(ChatterNameRetriever chatternameretriever)
    {
        onMemberNameUpdated(chatternameretriever);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_10425(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
        closeFragment();
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_4191(AdapterView adapterview, View view, int i, long l)
    {
        if (adapter != null)
        {
            adapterview = adapter.getItem(i);
            if (adapterview != null)
            {
                adapter.toggleChecked(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (adapterview)).RoleID);
            }
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupMemberRolesFragment_9245(DialogInterface dialoginterface, int i)
    {
        dialoginterface.dismiss();
        try
        {
            if (adapter != null && userManager != null && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
            {
                dialoginterface = adapter.getSelectedRoles();
                Object obj = (Set)activeRoles.get();
                HashSet hashset = new HashSet(dialoginterface);
                hashset.removeAll(((java.util.Collection) (obj)));
                obj = new HashSet(((java.util.Collection) (obj)));
                ((Set) (obj)).removeAll(dialoginterface);
                ((SLAgentCircuit)agentCircuit.get()).getModules().groupManager.RequestMemberRoleChanges(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterID).getChatterUUID(), MemberID, hashset, ((java.util.Collection) (obj)));
            }
        }
        // Misplaced declaration of an exception variable
        catch (DialogInterface dialoginterface) { }
        closeFragment();
    }

    public boolean onBackButtonPressed()
    {
        if (anyChanges())
        {
            android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
            builder.setMessage(getString(0x7f0902e0)).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda.jWSiK5iq_2D_zZfaogto6grdML6fzQ(this)).setNegativeButton("No", new _2D_.Lambda.jWSiK5iq_2D_zZfaogto6grdML6fzQ._cls1(this));
            builder.create().show();
            return true;
        } else
        {
            return false;
        }
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setHasOptionsMenu(true);
        setShowChatterTitle(false);
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuinflater)
    {
        super.onCreateOptionsMenu(menu, menuinflater);
        menuinflater.inflate(0x7f120006, menu);
        undoMenuItem = menu.findItem(0x7f100302);
        undoMenuItem.setVisible(hasChanged);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f040044, viewgroup, false);
        if (adapter == null)
        {
            adapter = new MemberRoleAdapter(null);
        }
        ((ListView)layoutinflater.findViewById(0x7f100174)).setAdapter(adapter);
        ((ListView)layoutinflater.findViewById(0x7f100174)).setOnItemClickListener(new _2D_.Lambda.jWSiK5iq_2D_zZfaogto6grdML6fzQ._cls2(this));
        ((LoadingLayout)layoutinflater.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901e0), getString(0x7f090151));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        return layoutinflater;
    }

    public void onLoadableDataChanged()
    {
        if (adapter != null)
        {
            adapter.setData((GroupRoleDataReply)groupRoles.getData(), (Set)activeRoles.getData());
        }
        com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry avatargroupentry;
        if (userManager == null)
        {
            break MISSING_BLOCK_LABEL_126;
        }
        avatargroupentry = getMyGroupEntry();
        if (avatargroupentry != null)
        {
            try
            {
                Debug.Printf("GroupMemberRoles: my group powers are 0x%x", new Object[] {
                    Long.valueOf(avatargroupentry.GroupPowers)
                });
                if (!groupTitles.isSubscribed())
                {
                    groupTitles.subscribe(userManager.getGroupTitles().getPool(), ((GroupRoleDataReply)groupRoles.get()).GroupData_Field.GroupID);
                    return;
                }
            }
            catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception) { }
            break MISSING_BLOCK_LABEL_126;
        }
        Debug.Printf("GroupMemberRoles: not my group", new Object[0]);
        return;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem)
    {
        menuitem.getItemId();
        JVM INSTR tableswitch 2131755778 2131755778: default 24
    //                   2131755778 30;
           goto _L1 _L2
_L1:
        return super.onOptionsItemSelected(menuitem);
_L2:
        try
        {
            if (adapter != null)
            {
                adapter.setData((GroupRoleDataReply)groupRoles.get(), (Set)activeRoles.get());
            }
        }
        catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception) { }
        if (true) goto _L1; else goto _L3
_L3:
    }

    protected void onShowUser(ChatterID chatterid)
    {
        loadableMonitor.unsubscribeAll();
        if (memberNameRetriever != null)
        {
            memberNameRetriever.dispose();
            memberNameRetriever = null;
        }
        MemberID = UUIDPool.getUUID(getArguments().getString("memberID"));
        setTitle(getString(0x7f0901a2), null);
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            if (MemberID != null)
            {
                memberNameRetriever = new ChatterNameRetriever(ChatterID.getUserChatterID(userManager.getUserID(), MemberID), new _2D_.Lambda.jWSiK5iq_2D_zZfaogto6grdML6fzQ._cls4(this), UIThreadExecutor.getInstance());
            }
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID();
            groupRoles.subscribe(userManager.getGroupRoles().getPool(), uuid);
            groupProfile.subscribe(userManager.getCachedGroupProfiles().getPool(), uuid);
            myGroupList.subscribe(userManager.getAvatarGroupLists().getPool(), chatterid.agentUUID);
            agentCircuit.subscribe(UserManager.agentCircuits(), chatterid.agentUUID);
            groupRoleMemberList.subscribe(userManager.getChatterList().getGroupManager().getGroupRoleMembers(), uuid);
        } else
        if (adapter != null)
        {
            adapter.setData(null, null);
            return;
        }
    }
}
