// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.GroupProfileReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply;
import com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupRoleDetailsFragment

public class GroupRolesProfileTab extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{
    private class GroupRoleAdapter extends BaseAdapter
    {

        private GroupRoleDataReply data;
        private GroupProfileReply groupProfile;
        final GroupRolesProfileTab this$0;
        private Map titlesByRole;

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

        public View getView(int i, View view, ViewGroup viewgroup)
        {
            View view1;
            boolean flag;
            flag = true;
            view1 = view;
            if (view == null)
            {
                view1 = LayoutInflater.from(getContext()).inflate(0x7f040047, viewgroup, false);
            }
            view = getItem(i);
            if (view == null) goto _L2; else goto _L1
_L1:
            i = ((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (view)).Members;
            if (((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (view)).RoleID.equals(UUIDPool.ZeroUUID) && groupProfile != null)
            {
                i = groupProfile.GroupData_Field.GroupMembershipCount;
            }
            ((TextView)view1.findViewById(0x7f10017d)).setText(SLMessage.stringFromVariableOEM(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (view)).Name));
            ((TextView)view1.findViewById(0x7f10017e)).setText(getResources().getQuantityString(0x7f110001, i, new Object[] {
                Integer.valueOf(i)
            }));
            if (titlesByRole == null) goto _L4; else goto _L3
_L3:
            view = (com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)titlesByRole.get(((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (view)).RoleID);
            if (view == null) goto _L4; else goto _L5
_L5:
            boolean flag1;
            flag1 = ((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData) (view)).Selected;
            i = 1;
_L7:
            view = view1.findViewById(0x7f10017c);
            if (i != 0)
            {
                i = 0;
            } else
            {
                i = 4;
            }
            view.setVisibility(i);
            view = (TextView)view1.findViewById(0x7f10017d);
            if (flag1)
            {
                i = ((flag) ? 1 : 0);
            } else
            {
                i = 0;
            }
            view.setTypeface(null, i);
_L2:
            return view1;
_L4:
            flag1 = false;
            i = 0;
            if (true) goto _L7; else goto _L6
_L6:
        }

        public boolean hasStableIds()
        {
            return false;
        }

        public void setData(GroupRoleDataReply grouproledatareply, GroupTitlesReply grouptitlesreply, GroupProfileReply groupprofilereply)
        {
            data = grouproledatareply;
            if (grouptitlesreply != null)
            {
                titlesByRole = new HashMap();
                for (grouproledatareply = grouptitlesreply.GroupData_Fields.iterator(); grouproledatareply.hasNext(); titlesByRole.put(((com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData) (grouptitlesreply)).RoleID, grouptitlesreply))
                {
                    grouptitlesreply = (com.lumiyaviewer.lumiya.slproto.messages.GroupTitlesReply.GroupData)grouproledatareply.next();
                }

            } else
            {
                titlesByRole = null;
            }
            groupProfile = groupprofilereply;
            notifyDataSetInvalidated();
        }

        private GroupRoleAdapter()
        {
            this$0 = GroupRolesProfileTab.this;
            super();
            groupProfile = null;
            data = null;
            titlesByRole = null;
        }

        GroupRoleAdapter(GroupRoleAdapter grouproleadapter)
        {
            this();
        }
    }


    private GroupRoleAdapter adapter;
    private final SubscriptionData groupProfile = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupRoles = new SubscriptionData(UIThreadExecutor.getInstance());
    private final SubscriptionData groupTitles = new SubscriptionData(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData myGroupList = new SubscriptionData(UIThreadExecutor.getInstance());

    public GroupRolesProfileTab()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            groupProfile, groupRoles, myGroupList
        })).withOptionalLoadables(new Loadable[] {
            groupTitles
        }).withDataChangedListener(this);
        adapter = null;
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

    private void onAddNewRoleButton(View view)
    {
        if ((getMyGroupPowers() & 16L) != 0L)
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupRoleDetailsFragment, GroupRoleDetailsFragment.makeSelection(chatterID, null));
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTab_2D_mthref_2D_0(View view)
    {
        onAddNewRoleButton(view);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_GroupRolesProfileTab_2802(AdapterView adapterview, View view, int i, long l)
    {
        if (adapter != null)
        {
            adapterview = adapter.getItem(i);
            if (adapterview != null)
            {
                DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupRoleDetailsFragment, GroupRoleDetailsFragment.makeSelection(chatterID, ((com.lumiyaviewer.lumiya.slproto.messages.GroupRoleDataReply.RoleData) (adapterview)).RoleID));
            }
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        layoutinflater = layoutinflater.inflate(0x7f04004b, viewgroup, false);
        if (adapter == null)
        {
            adapter = new GroupRoleAdapter(null);
        }
        ((ListView)layoutinflater.findViewById(0x7f10019f)).setAdapter(adapter);
        ((ListView)layoutinflater.findViewById(0x7f10019f)).setOnItemClickListener(new _2D_.Lambda.zWKNEqUupU__bUM7E0seQ8xMgmU._cls1(this));
        layoutinflater.findViewById(0x7f1001a0).setOnClickListener(new _2D_.Lambda.zWKNEqUupU__bUM7E0seQ8xMgmU(this));
        ((LoadingLayout)layoutinflater.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        loadableMonitor.setLoadingLayout((LoadingLayout)layoutinflater.findViewById(0x7f1000bd), getString(0x7f0901e0), getString(0x7f090151));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)layoutinflater.findViewById(0x7f1000bb));
        return layoutinflater;
    }

    public void onLoadableDataChanged()
    {
        View view;
        long l;
        try
        {
            if ((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)((AvatarGroupList)myGroupList.get()).Groups.get(((GroupRoleDataReply)groupRoles.get()).GroupData_Field.GroupID) != null && !groupTitles.isSubscribed())
            {
                groupTitles.subscribe(userManager.getGroupTitles().getPool(), ((GroupRoleDataReply)groupRoles.get()).GroupData_Field.GroupID);
            }
        }
        catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception) { }
        l = getMyGroupPowers();
        view = getView();
        if (view != null)
        {
            view = view.findViewById(0x7f1001a0);
            int i;
            if ((l & 16L) != 0L)
            {
                i = 0;
            } else
            {
                i = 8;
            }
            view.setVisibility(i);
        }
        if (adapter != null)
        {
            adapter.setData((GroupRoleDataReply)groupRoles.getData(), (GroupTitlesReply)groupTitles.getData(), (GroupProfileReply)groupProfile.getData());
        }
    }

    protected void onShowUser(ChatterID chatterid)
    {
        loadableMonitor.unsubscribeAll();
        if (userManager != null && (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup))
        {
            UUID uuid = ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDGroup)chatterid).getChatterUUID();
            groupRoles.subscribe(userManager.getGroupRoles().getPool(), uuid);
            groupProfile.subscribe(userManager.getCachedGroupProfiles().getPool(), uuid);
            myGroupList.subscribe(userManager.getAvatarGroupLists().getPool(), chatterid.agentUUID);
        } else
        if (adapter != null)
        {
            adapter.setData(null, null, null);
            return;
        }
    }
}
