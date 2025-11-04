// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.minimap;

import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.view.ViewGroup$LayoutParams;
import android.widget.LinearLayout$LayoutParams;
import android.app.Activity;
import butterknife.ButterKnife;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import javax.annotation.Nullable;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import butterknife.BindView;
import android.view.ViewGroup;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.ui.common.ConnectedActivity;

public class MinimapActivity extends ConnectedActivity
{
    private final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo;
    @BindView(2131755653)
    ViewGroup detailsLayout;
    @BindView(2131755654)
    FrameLayout selectorLayout;
    @BindView(2131755652)
    LinearLayout splitMainLayout;
    @BindView(2131755657)
    View splitObjectPopupsLeftSpacer;
    
    public MinimapActivity() {
        this.currentLocationInfo = new SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo>(UIThreadExecutor.getInstance(), new _$Lambda$HQUtmVzLYkemE78mCklVmVxMXms(this));
    }
    
    private void onCurrentLocationInfo(final CurrentLocationInfo currentLocationInfo) {
        String name = null;
        if (currentLocationInfo != null) {
            final ParcelData parcelData = currentLocationInfo.parcelData();
            if (parcelData != null) {
                name = parcelData.getName();
            }
            String string;
            if ((string = name) == null) {
                string = this.getString(2131296712);
            }
            this.setActivityTitle(string, this.getString(2131296725, new Object[] { currentLocationInfo.nearbyUsers() }));
        }
    }
    
    private void setActivityTitle(@Nullable final String s, @Nullable final String subtitle) {
        final ActionBar supportActionBar = this.getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(s);
            supportActionBar.setSubtitle(subtitle);
        }
        this.setTitle((CharSequence)s);
    }
    
    @Override
    protected void onCreate(final Bundle bundle) {
        super.onCreate(bundle);
        this.setContentView(2130968740);
        ButterKnife.bind(this);
        if (this.getResources().getConfiguration().orientation == 2) {
            this.splitMainLayout.setOrientation(0);
            final LinearLayout$LayoutParams layoutParams = (LinearLayout$LayoutParams)this.selectorLayout.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -1;
            layoutParams.weight = 0.0f;
            this.selectorLayout.setLayoutParams((ViewGroup$LayoutParams)layoutParams);
            final LinearLayout$LayoutParams layoutParams2 = (LinearLayout$LayoutParams)this.detailsLayout.getLayoutParams();
            layoutParams2.width = -1;
            layoutParams2.height = -1;
            layoutParams2.weight = 1.0f;
            this.detailsLayout.setLayoutParams((ViewGroup$LayoutParams)layoutParams2);
        }
        else {
            this.splitMainLayout.setOrientation(1);
            final LinearLayout$LayoutParams layoutParams3 = (LinearLayout$LayoutParams)this.selectorLayout.getLayoutParams();
            layoutParams3.width = -1;
            layoutParams3.height = -2;
            layoutParams3.weight = 0.0f;
            this.selectorLayout.setLayoutParams((ViewGroup$LayoutParams)layoutParams3);
            final LinearLayout$LayoutParams layoutParams4 = (LinearLayout$LayoutParams)this.detailsLayout.getLayoutParams();
            layoutParams4.width = -1;
            layoutParams4.height = -1;
            layoutParams4.weight = 1.0f;
            this.detailsLayout.setLayoutParams((ViewGroup$LayoutParams)layoutParams4);
            this.splitObjectPopupsLeftSpacer.setVisibility(8);
        }
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getIntent());
        final FragmentManager supportFragmentManager = this.getSupportFragmentManager();
        if (supportFragmentManager != null && activeAgentID != null) {
            final FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
            if (supportFragmentManager.findFragmentById(2131755654) == null) {
                beginTransaction.add(2131755654, MinimapFragment.newInstance(activeAgentID));
            }
            if (supportFragmentManager.findFragmentById(2131755284) == null) {
                beginTransaction.add(2131755284, NearbyPeopleMinimapFragment.newInstance(activeAgentID));
            }
            beginTransaction.commit();
        }
        else {
            this.finish();
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
        if (userManager != null) {
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        }
        else {
            this.currentLocationInfo.unsubscribe();
        }
    }
    
    @Override
    protected void onStop() {
        this.currentLocationInfo.unsubscribe();
        super.onStop();
    }
}
