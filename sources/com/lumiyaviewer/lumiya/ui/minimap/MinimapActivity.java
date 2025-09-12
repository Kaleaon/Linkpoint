// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.minimap;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.ButterKnife;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ConnectedActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.minimap:
//            MinimapFragment, NearbyPeopleMinimapFragment

public class MinimapActivity extends ConnectedActivity
{

    private final SubscriptionData currentLocationInfo = new SubscriptionData(UIThreadExecutor.getInstance(), new _2D_.Lambda.HQUtmVzLYkemE78mCklVmVxMXms(this));
    ViewGroup detailsLayout;
    FrameLayout selectorLayout;
    LinearLayout splitMainLayout;
    View splitObjectPopupsLeftSpacer;

    public MinimapActivity()
    {
    }

    private void onCurrentLocationInfo(CurrentLocationInfo currentlocationinfo)
    {
        String s = null;
        if (currentlocationinfo != null)
        {
            Object obj = currentlocationinfo.parcelData();
            if (obj != null)
            {
                s = ((ParcelData) (obj)).getName();
            }
            obj = s;
            if (s == null)
            {
                obj = getString(0x7f0901c8);
            }
            setActivityTitle(((String) (obj)), getString(0x7f0901d5, new Object[] {
                Integer.valueOf(currentlocationinfo.nearbyUsers())
            }));
        }
    }

    private void setActivityTitle(String s, String s1)
    {
        ActionBar actionbar = getSupportActionBar();
        if (actionbar != null)
        {
            actionbar.setTitle(s);
            actionbar.setSubtitle(s1);
        }
        setTitle(s);
    }

    void _2D_com_lumiyaviewer_lumiya_ui_minimap_MinimapActivity_2D_mthref_2D_0(CurrentLocationInfo currentlocationinfo)
    {
        onCurrentLocationInfo(currentlocationinfo);
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setContentView(0x7f0400a4);
        ButterKnife.bind(this);
        FragmentManager fragmentmanager;
        if (getResources().getConfiguration().orientation == 2)
        {
            splitMainLayout.setOrientation(0);
            bundle = (android.widget.LinearLayout.LayoutParams)selectorLayout.getLayoutParams();
            bundle.width = -2;
            bundle.height = -1;
            bundle.weight = 0.0F;
            selectorLayout.setLayoutParams(bundle);
            bundle = (android.widget.LinearLayout.LayoutParams)detailsLayout.getLayoutParams();
            bundle.width = -1;
            bundle.height = -1;
            bundle.weight = 1.0F;
            detailsLayout.setLayoutParams(bundle);
        } else
        {
            splitMainLayout.setOrientation(1);
            bundle = (android.widget.LinearLayout.LayoutParams)selectorLayout.getLayoutParams();
            bundle.width = -1;
            bundle.height = -2;
            bundle.weight = 0.0F;
            selectorLayout.setLayoutParams(bundle);
            bundle = (android.widget.LinearLayout.LayoutParams)detailsLayout.getLayoutParams();
            bundle.width = -1;
            bundle.height = -1;
            bundle.weight = 1.0F;
            detailsLayout.setLayoutParams(bundle);
            splitObjectPopupsLeftSpacer.setVisibility(8);
        }
        bundle = ActivityUtils.getActiveAgentID(getIntent());
        fragmentmanager = getSupportFragmentManager();
        if (fragmentmanager != null && bundle != null)
        {
            FragmentTransaction fragmenttransaction = fragmentmanager.beginTransaction();
            if (fragmentmanager.findFragmentById(0x7f100286) == null)
            {
                fragmenttransaction.add(0x7f100286, MinimapFragment.newInstance(bundle));
            }
            if (fragmentmanager.findFragmentById(0x7f100114) == null)
            {
                fragmenttransaction.add(0x7f100114, NearbyPeopleMinimapFragment.newInstance(bundle));
            }
            fragmenttransaction.commit();
            return;
        } else
        {
            finish();
            return;
        }
    }

    protected void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getIntent());
        if (usermanager != null)
        {
            currentLocationInfo.subscribe(usermanager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
            return;
        } else
        {
            currentLocationInfo.unsubscribe();
            return;
        }
    }

    protected void onStop()
    {
        currentLocationInfo.unsubscribe();
        super.onStop();
    }
}
