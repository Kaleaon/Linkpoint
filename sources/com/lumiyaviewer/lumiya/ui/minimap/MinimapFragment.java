// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.minimap:
//            MinimapView, NearbyPeopleMinimapFragment

public class MinimapFragment extends Fragment
    implements MinimapView.OnUserClickListener
{

    private final SubscriptionData minimapBitmap = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.XqnH7RvGuiq1TzRqXD2eGyM2ulM(this));
    private final SubscriptionData userLocations = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.XqnH7RvGuiq1TzRqXD2eGyM2ulM._cls1(this));

    public MinimapFragment()
    {
    }

    static Fragment newInstance(UUID uuid)
    {
        MinimapFragment minimapfragment = new MinimapFragment();
        minimapfragment.setArguments(ActivityUtils.makeFragmentArguments(uuid, null));
        return minimapfragment;
    }

    private void onMinimapBitmap(com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.MinimapBitmap minimapbitmap)
    {
        View view = getView();
        if (view != null)
        {
            ((MinimapView)view.findViewById(0x7f1001dd)).setMinimapBitmap(minimapbitmap);
        }
    }

    private void onUserLocations(com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocations userlocations)
    {
        View view = getView();
        if (view != null)
        {
            ((MinimapView)view.findViewById(0x7f1001dd)).setUserLocations(userlocations);
        }
    }

    void _2D_com_lumiyaviewer_lumiya_ui_minimap_MinimapFragment_2D_mthref_2D_0(com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.MinimapBitmap minimapbitmap)
    {
        onMinimapBitmap(minimapbitmap);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_minimap_MinimapFragment_2D_mthref_2D_1(com.lumiyaviewer.lumiya.slproto.modules.SLMinimap.UserLocations userlocations)
    {
        onUserLocations(userlocations);
    }

    public void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
    }

    public View onCreateView(LayoutInflater layoutinflater, ViewGroup viewgroup, Bundle bundle)
    {
        super.onCreateView(layoutinflater, viewgroup, bundle);
        layoutinflater = layoutinflater.inflate(0x7f04005e, viewgroup, false);
        ((MinimapView)layoutinflater.findViewById(0x7f1001dd)).setOnUserClickListener(this);
        return layoutinflater;
    }

    public void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getArguments());
        if (usermanager != null)
        {
            minimapBitmap.subscribe(usermanager.getMinimapBitmapPool(), SubscriptionSingleKey.Value);
            userLocations.subscribe(usermanager.getUserLocationsPool(), SubscriptionSingleKey.Value);
            return;
        } else
        {
            minimapBitmap.unsubscribe();
            userLocations.unsubscribe();
            return;
        }
    }

    public void onStop()
    {
        minimapBitmap.unsubscribe();
        userLocations.unsubscribe();
        super.onStop();
    }

    public void onUserClick(UUID uuid)
    {
        Object obj = getFragmentManager();
        if (obj != null)
        {
            obj = ((FragmentManager) (obj)).findFragmentById(0x7f100114);
            if (obj instanceof NearbyPeopleMinimapFragment)
            {
                ((NearbyPeopleMinimapFragment)obj).setSelectedUser(uuid);
            }
        }
        obj = getView();
        if (obj != null)
        {
            ((MinimapView)((View) (obj)).findViewById(0x7f1001dd)).setSelectedUser(uuid);
        }
    }
}
