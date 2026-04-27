package com.lumiyaviewer.lumiya.ui.minimap;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.react.SubscriptionData;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.ConnectedActivity;
import java.util.UUID;
import javax.annotation.Nullable;

public class MinimapActivity extends ConnectedActivity {
    private final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new $Lambda$HQUtmVzLYkemE78mCklVmVxMXms(this));
    @BindView(2131755653)
    ViewGroup detailsLayout;
    @BindView(2131755654)
    FrameLayout selectorLayout;
    @BindView(2131755652)
    LinearLayout splitMainLayout;
    @BindView(2131755657)
    View splitObjectPopupsLeftSpacer;

    /* access modifiers changed from: private */
    /* renamed from: onCurrentLocationInfo */
    public void m639com_lumiyaviewer_lumiya_ui_minimap_MinimapActivitymthref0(CurrentLocationInfo currentLocationInfo2) {
        String str = null;
        if (currentLocationInfo2 != null) {
            ParcelData parcelData = currentLocationInfo2.parcelData();
            if (parcelData != null) {
                str = parcelData.getName();
            }
            if (str == null) {
                str = getString(R.string.name_loading_title);
            }
            setActivityTitle(str, getString(R.string.nearby_users_format, new Object[]{Integer.valueOf(currentLocationInfo2.nearbyUsers())}));
        }
    }

    private void setActivityTitle(@Nullable String str, @Nullable String str2) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle((CharSequence) str);
            supportActionBar.setSubtitle((CharSequence) str2);
        }
        setTitle(str);
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.split_two_panels);
        ButterKnife.bind((Activity) this);
        if (getResources().getConfiguration().orientation == 2) {
            this.splitMainLayout.setOrientation(0);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.selectorLayout.getLayoutParams();
            layoutParams.width = -2;
            layoutParams.height = -1;
            layoutParams.weight = 0.0f;
            this.selectorLayout.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParams2 = (LinearLayout.LayoutParams) this.detailsLayout.getLayoutParams();
            layoutParams2.width = -1;
            layoutParams2.height = -1;
            layoutParams2.weight = 1.0f;
            this.detailsLayout.setLayoutParams(layoutParams2);
        } else {
            this.splitMainLayout.setOrientation(1);
            LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) this.selectorLayout.getLayoutParams();
            layoutParams3.width = -1;
            layoutParams3.height = -2;
            layoutParams3.weight = 0.0f;
            this.selectorLayout.setLayoutParams(layoutParams3);
            LinearLayout.LayoutParams layoutParams4 = (LinearLayout.LayoutParams) this.detailsLayout.getLayoutParams();
            layoutParams4.width = -1;
            layoutParams4.height = -1;
            layoutParams4.weight = 1.0f;
            this.detailsLayout.setLayoutParams(layoutParams4);
            this.splitObjectPopupsLeftSpacer.setVisibility(8);
        }
        UUID activeAgentID = ActivityUtils.getActiveAgentID(getIntent());
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        if (supportFragmentManager == null || activeAgentID == null) {
            finish();
            return;
        }
        FragmentTransaction beginTransaction = supportFragmentManager.beginTransaction();
        if (supportFragmentManager.findFragmentById(R.id.selector) == null) {
            beginTransaction.add((int) R.id.selector, MinimapFragment.newInstance(activeAgentID));
        }
        if (supportFragmentManager.findFragmentById(R.id.details) == null) {
            beginTransaction.add((int) R.id.details, NearbyPeopleMinimapFragment.newInstance(activeAgentID));
        }
        beginTransaction.commit();
    }

    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager != null) {
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        } else {
            this.currentLocationInfo.unsubscribe();
        }
    }

    /* access modifiers changed from: protected */
    public void onStop() {
        this.currentLocationInfo.unsubscribe();
        super.onStop();
    }
}
