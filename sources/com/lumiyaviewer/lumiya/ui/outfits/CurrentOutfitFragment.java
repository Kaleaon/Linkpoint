// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.outfits;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.modules.SLModules;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DismissableAdapter;
import com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener;
import com.lumiyaviewer.lumiya.ui.common.loadmon.Loadable;
import com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.outfits:
//            CurrentOutfitAdapter

public class CurrentOutfitFragment extends Fragment
    implements com.lumiyaviewer.lumiya.ui.common.loadmon.LoadableMonitor.OnLoadableDataChangedListener, android.widget.AdapterView.OnItemClickListener
{

    private final SubscriptionData agentCircuit = new SubscriptionData(UIThreadExecutor.getInstance());
    private CurrentOutfitAdapter listAdapter;
    private final LoadableMonitor loadableMonitor;
    private final SubscriptionData wornItems = new SubscriptionData(UIThreadExecutor.getInstance());

    public CurrentOutfitFragment()
    {
        loadableMonitor = (new LoadableMonitor(new Loadable[] {
            wornItems
        })).withOptionalLoadables(new Loadable[] {
            agentCircuit
        }).withDataChangedListener(this);
    }

    public static Bundle makeSelection(UUID uuid)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        return bundle;
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        viewgroup = layoutinflater.inflate(0x7f040030, viewgroup, false);
        listAdapter = new CurrentOutfitAdapter(layoutinflater.getContext());
        layoutinflater = (ListView)viewgroup.findViewById(0x7f10014c);
        layoutinflater.setAdapter(listAdapter);
        layoutinflater.setOnItemClickListener(this);
        layoutinflater.setEmptyView(viewgroup.findViewById(0x1020004));
        bundle = new SwipeDismissListViewTouchListener(layoutinflater, new com.lumiyaviewer.lumiya.ui.common.SwipeDismissListViewTouchListener.DismissCallbacks() {

            final CurrentOutfitFragment this$0;

            public boolean canDismiss(ListView listview, int i)
            {
                listview = listview.getAdapter();
                if (listview instanceof DismissableAdapter)
                {
                    return ((DismissableAdapter)listview).canDismiss(i);
                } else
                {
                    return false;
                }
            }

            public void onDismiss(ListView listview, int i)
            {
                listview = listview.getAdapter();
                if (listview instanceof DismissableAdapter)
                {
                    ((DismissableAdapter)listview).onDismiss(i);
                }
            }

            
            {
                this$0 = CurrentOutfitFragment.this;
                super();
            }
        });
        layoutinflater.setOnTouchListener(bundle);
        layoutinflater.setOnScrollListener(bundle.makeScrollListener());
        return viewgroup;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = (SLAgentCircuit)agentCircuit.getData();
        if (listAdapter != null && adapterview != null)
        {
            view = listAdapter.getItem(i);
            if (view != null && view.getIsTouchable() && view.getWornOn() == null)
            {
                adapterview.TouchObject(view.getObjectLocalID());
            }
        }
    }

    public void onLoadableDataChanged()
    {
        if (listAdapter != null)
        {
            Object obj = (SLAgentCircuit)agentCircuit.getData();
            CurrentOutfitAdapter currentoutfitadapter = listAdapter;
            if (obj != null)
            {
                obj = ((SLAgentCircuit) (obj)).getModules().avatarAppearance;
            } else
            {
                obj = null;
            }
            currentoutfitadapter.setAvatarAppearance(((com.lumiyaviewer.lumiya.slproto.modules.SLAvatarAppearance) (obj)));
            listAdapter.setData((ImmutableList)wornItems.getData());
        }
    }

    public void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            agentCircuit.subscribe(UserManager.agentCircuits(), usermanager.getUserID());
            wornItems.subscribe(usermanager.wornItems(), SubscriptionSingleKey.Value);
            return;
        } else
        {
            loadableMonitor.unsubscribeAll();
            return;
        }
    }

    public void onStop()
    {
        loadableMonitor.unsubscribeAll();
        super.onStop();
    }
}
