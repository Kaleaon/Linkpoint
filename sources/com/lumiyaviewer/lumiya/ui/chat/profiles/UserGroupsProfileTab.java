// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.SerializableResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            GroupProfileFragment

public class UserGroupsProfileTab extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{
    private static class GroupsAdapter extends BaseAdapter
    {

        private ImmutableList avatarGroupList;
        private final LayoutInflater inflater;

        public int getCount()
        {
            if (avatarGroupList != null)
            {
                return avatarGroupList.size();
            } else
            {
                return 0;
            }
        }

        public com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry getItem(int i)
        {
            if (avatarGroupList != null && i >= 0 && i < avatarGroupList.size())
            {
                return (com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)avatarGroupList.get(i);
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
            View view1 = view;
            if (view == null)
            {
                view1 = inflater.inflate(0x1090003, viewgroup, false);
            }
            view = getItem(i);
            if (view != null)
            {
                ((TextView)view1.findViewById(0x1020014)).setText(((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) (view)).GroupName);
            }
            return view1;
        }

        public boolean hasStableIds()
        {
            return false;
        }

        void setData(AvatarGroupList avatargrouplist)
        {
            com.google.common.collect.ImmutableList.Builder builder = new com.google.common.collect.ImmutableList.Builder();
            builder.addAll(avatargrouplist.Groups.values());
            avatarGroupList = builder.build();
            notifyDataSetChanged();
        }

        private GroupsAdapter(Context context)
        {
            avatarGroupList = null;
            inflater = LayoutInflater.from(context);
        }

        GroupsAdapter(Context context, GroupsAdapter groupsadapter)
        {
            this(context);
        }
    }


    private final SubscriptionData avatarGroups = new SubscriptionData(UIThreadExecutor.getInstance());
    private GroupsAdapter groupsAdapter;
    private final LoadableMonitor loadableMonitor;

    public UserGroupsProfileTab()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            avatarGroups
        })).withDataChangedListener(this);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserGroupsProfileTab_2041(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = ((AdapterView) (adapterview.getAdapter().getItem(i)));
        if ((adapterview instanceof com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry) && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            adapterview = ChatterID.getGroupChatterID(chatterID.agentUUID, ((com.lumiyaviewer.lumiya.slproto.modules.groups.AvatarGroupList.AvatarGroupEntry)adapterview).GroupID);
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/GroupProfileFragment, GroupProfileFragment.makeSelection(adapterview));
        }
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        viewgroup = layoutinflater.inflate(0x7f0400b7, viewgroup, false);
        groupsAdapter = new GroupsAdapter(layoutinflater.getContext(), null);
        ((ListView)viewgroup.findViewById(0x7f1002bb)).setAdapter(groupsAdapter);
        ((ListView)viewgroup.findViewById(0x7f1002bb)).setOnItemClickListener(new _2D_.Lambda._cls929W_sYALf9zQuqLbMSJpktRAzI(this));
        ((LoadingLayout)viewgroup.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)viewgroup.findViewById(0x7f1000bb));
        loadableMonitor.setLoadingLayout((LoadingLayout)viewgroup.findViewById(0x7f1000bd), getString(0x7f0901f1), getString(0x7f090370));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)viewgroup.findViewById(0x7f1000bb));
        return viewgroup;
    }

    public void onLoadableDataChanged()
    {
        try
        {
            loadableMonitor.setEmptyMessage(((AvatarGroupList)avatarGroups.get()).Groups.isEmpty(), getString(0x7f0901e1));
            if (groupsAdapter != null)
            {
                groupsAdapter.setData((AvatarGroupList)avatarGroups.getData());
            }
            return;
        }
        catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
        {
            Debug.Warning(datanotreadyexception);
        }
    }

    protected void onShowUser(ChatterID chatterid)
    {
        loadableMonitor.unsubscribeAll();
        if (chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)
        {
            UserManager usermanager = chatterid.getUserManager();
            if (usermanager != null)
            {
                avatarGroups.subscribe(usermanager.getAvatarGroupLists().getPool(), ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID());
            }
        }
    }
}
