// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat.profiles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.modules.SLUserProfiles;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.SLMessageResponseCacher;
import com.lumiyaviewer.lumiya.slproto.users.manager.AvatarPickKey;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ChatterReloadableFragment;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.LoadingLayout;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.ArrayList;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat.profiles:
//            UserPickFragment

public class UserPicksProfileTab extends ChatterReloadableFragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener
{
    private static class PicksAdapter extends BaseAdapter
    {

        private final LayoutInflater inflater;
        private AvatarPicksReply picksReply;

        public int getCount()
        {
            if (picksReply != null)
            {
                return picksReply.Data_Fields.size();
            } else
            {
                return 0;
            }
        }

        public com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply.Data getItem(int i)
        {
            if (picksReply != null && i >= 0 && i < picksReply.Data_Fields.size())
            {
                return (com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply.Data)picksReply.Data_Fields.get(i);
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
                ((TextView)view1.findViewById(0x1020014)).setText(SLMessage.stringFromVariableUTF(((com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply.Data) (view)).PickName));
            }
            return view1;
        }

        public boolean hasStableIds()
        {
            return false;
        }

        void setData(AvatarPicksReply avatarpicksreply)
        {
            picksReply = avatarpicksreply;
            notifyDataSetChanged();
        }

        private PicksAdapter(Context context)
        {
            picksReply = null;
            inflater = LayoutInflater.from(context);
        }

        PicksAdapter(Context context, PicksAdapter picksadapter)
        {
            this(context);
        }
    }


    private final SubscriptionData avatarPicks = new SubscriptionData(UIThreadExecutor.getInstance());
    private final LoadableMonitor loadableMonitor;
    private PicksAdapter picksAdapter;

    public UserPicksProfileTab()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            avatarPicks
        })).withDataChangedListener(this);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_5724(DialogInterface dialoginterface, int i)
    {
        dialoginterface.cancel();
    }

    private void onAddNewPick(View view)
    {
        view = null;
        if (userManager != null && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            Object obj = userManager.getCurrentLocationInfoSnapshot();
            if (obj != null)
            {
                view = ((CurrentLocationInfo) (obj)).parcelData();
            }
            obj = userManager.getActiveAgentCircuit();
            if (view != null && obj != null)
            {
                android.app.AlertDialog.Builder builder;
                String s;
                int i;
                if (picksAdapter != null)
                {
                    i = picksAdapter.getCount();
                } else
                {
                    i = 0;
                }
                builder = new android.app.AlertDialog.Builder(getContext());
                s = (String)Optional.fromNullable(Strings.emptyToNull(view.getName())).or(getString(0x7f0901c8));
                builder.setMessage(getString(0x7f0900d3, new Object[] {
                    s
                })).setCancelable(true).setPositiveButton("Yes", new _2D_.Lambda._cls0JruYUVxhc8cYQ6nJZD1LVnQE5A._cls3(i, this, obj, s, view)).setNegativeButton("No", new _2D_.Lambda._cls0JruYUVxhc8cYQ6nJZD1LVnQE5A());
                builder.create().show();
            }
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_2D_mthref_2D_0(View view)
    {
        onAddNewPick(view);
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_2539(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = ((AdapterView) (adapterview.getAdapter().getItem(i)));
        if ((adapterview instanceof com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply.Data) && (chatterID instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser))
        {
            DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserPickFragment, UserPickFragment.makeSelection(chatterID.agentUUID, new AvatarPickKey(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID).getChatterUUID(), ((com.lumiyaviewer.lumiya.slproto.messages.AvatarPicksReply.Data)adapterview).PickID)));
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_profiles_UserPicksProfileTab_4543(SLAgentCircuit slagentcircuit, String s, ParcelData parceldata, int i, DialogInterface dialoginterface, int j)
    {
        UUID uuid = UUID.randomUUID();
        slagentcircuit.getModules().userProfiles.UpdatePickInfo(uuid, userManager.getUserID(), UUIDPool.ZeroUUID, s, Strings.nullToEmpty(parceldata.getDescription()), (UUID)Optional.fromNullable(parceldata.getSnapshotUUID()).or(UUIDPool.ZeroUUID), slagentcircuit.getAgentGlobalPosition(), i, true);
        DetailsActivity.showEmbeddedDetails(getActivity(), com/lumiyaviewer/lumiya/ui/chat/profiles/UserPickFragment, UserPickFragment.makeSelection(chatterID.agentUUID, new AvatarPickKey(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterID).getChatterUUID(), uuid)));
        dialoginterface.dismiss();
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        viewgroup = layoutinflater.inflate(0x7f0400b9, viewgroup, false);
        picksAdapter = new PicksAdapter(layoutinflater.getContext(), null);
        viewgroup.findViewById(0x7f1002ce).setOnClickListener(new _2D_.Lambda._cls0JruYUVxhc8cYQ6nJZD1LVnQE5A._cls1(this));
        ((ListView)viewgroup.findViewById(0x7f1002cd)).setAdapter(picksAdapter);
        ((ListView)viewgroup.findViewById(0x7f1002cd)).setOnItemClickListener(new _2D_.Lambda._cls0JruYUVxhc8cYQ6nJZD1LVnQE5A._cls2(this));
        ((LoadingLayout)viewgroup.findViewById(0x7f1000bd)).setSwipeRefreshLayout((SwipeRefreshLayout)viewgroup.findViewById(0x7f1000bb));
        loadableMonitor.setLoadingLayout((LoadingLayout)viewgroup.findViewById(0x7f1000bd), getString(0x7f0901f1), getString(0x7f090370));
        loadableMonitor.setSwipeRefreshLayout((SwipeRefreshLayout)viewgroup.findViewById(0x7f1000bb));
        return viewgroup;
    }

    public void onLoadableDataChanged()
    {
        try
        {
            if (picksAdapter != null)
            {
                picksAdapter.setData((AvatarPicksReply)avatarPicks.getData());
            }
            loadableMonitor.setEmptyMessage(((AvatarPicksReply)avatarPicks.get()).Data_Fields.isEmpty(), getString(0x7f0901ee));
            return;
        }
        catch (com.lumiyaviewer.lumiya.react.SubscriptionData.DataNotReadyException datanotreadyexception)
        {
            Debug.Warning(datanotreadyexception);
        }
    }

    protected void onShowUser(ChatterID chatterid)
    {
        int i;
        i = 0;
        loadableMonitor.unsubscribeAll();
        if (!(chatterid instanceof com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)) goto _L2; else goto _L1
_L1:
        UserManager usermanager = chatterid.getUserManager();
        if (usermanager == null) goto _L2; else goto _L3
_L3:
        boolean flag;
        flag = usermanager.getUserID().equals(((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID());
        avatarPicks.subscribe(usermanager.getAvatarPicks().getPool(), ((com.lumiyaviewer.lumiya.slproto.users.ChatterID.ChatterIDUser)chatterid).getChatterUUID());
_L5:
        chatterid = getView();
        if (chatterid != null)
        {
            chatterid = chatterid.findViewById(0x7f1002ce);
            if (!flag)
            {
                i = 8;
            }
            chatterid.setVisibility(i);
        }
        return;
_L2:
        flag = false;
        if (true) goto _L5; else goto _L4
_L4:
    }
}
