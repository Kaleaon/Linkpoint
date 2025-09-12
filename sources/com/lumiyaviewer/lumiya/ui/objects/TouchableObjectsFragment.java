// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.SLAgentCircuit;
import com.lumiyaviewer.lumiya.slproto.objects.SLObjectInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.ObjectsManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.utils.UUIDPool;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            TouchableObjectListAdapter

public class TouchableObjectsFragment extends Fragment
    implements android.widget.AdapterView.OnItemClickListener
{

    private static final String OBJECT_UUID_KEY = "objectUUID";
    private TouchableObjectListAdapter listAdapter;
    private final SubscriptionData touchableObjects = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.LilZ3G1QEr_14fK4lPNJzUyzlBg(this));

    public TouchableObjectsFragment()
    {
    }

    private UUID getObjectUUID()
    {
        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey("objectUUID"))
        {
            return UUIDPool.getUUID(bundle.getString("objectUUID"));
        } else
        {
            return null;
        }
    }

    public static Bundle makeSelection(UUID uuid, UUID uuid1)
    {
        Bundle bundle = new Bundle();
        ActivityUtils.setActiveAgentID(bundle, uuid);
        bundle.putString("objectUUID", uuid1.toString());
        return bundle;
    }

    private void onTouchableObjects(ImmutableList immutablelist)
    {
        if (listAdapter != null)
        {
            listAdapter.setData(immutablelist);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_objects_TouchableObjectsFragment_2D_mthref_2D_0(ImmutableList immutablelist)
    {
        onTouchableObjects(immutablelist);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        viewgroup = layoutinflater.inflate(0x7f0400ad, viewgroup, false);
        listAdapter = new TouchableObjectListAdapter(layoutinflater.getContext());
        ((ListView)viewgroup.findViewById(0x7f100298)).setAdapter(listAdapter);
        ((ListView)viewgroup.findViewById(0x7f100298)).setOnItemClickListener(this);
        ((ListView)viewgroup.findViewById(0x7f100298)).setEmptyView(viewgroup.findViewById(0x1020004));
        return viewgroup;
    }

    public void onItemClick(AdapterView adapterview, View view, int i, long l)
    {
        adapterview = null;
        if (listAdapter != null)
        {
            view = listAdapter.getItem(i);
            UserManager usermanager = ActivityUtils.getUserManager(getArguments());
            if (usermanager != null)
            {
                adapterview = usermanager.getActiveAgentCircuit();
            }
            if (view != null && adapterview != null)
            {
                adapterview.TouchObject(((SLObjectInfo) (view)).localID);
            }
        }
    }

    public void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        UUID uuid = getObjectUUID();
        if (usermanager != null && uuid != null)
        {
            touchableObjects.subscribe(usermanager.getObjectsManager().touchableObjects(), uuid);
            return;
        } else
        {
            touchableObjects.unsubscribe();
            return;
        }
    }

    public void onStop()
    {
        touchableObjects.unsubscribe();
        super.onStop();
    }
}
