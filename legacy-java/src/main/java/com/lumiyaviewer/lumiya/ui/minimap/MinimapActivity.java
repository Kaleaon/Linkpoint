package com.lumiyaviewer.lumiya.ui.minimap;

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
import com.lumiyaviewer.lumiya.react.Subscription;
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

/* loaded from: classes.dex */
public class MinimapActivity extends ConnectedActivity {
    private final SubscriptionData<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfo = new SubscriptionData<>(UIThreadExecutor.getInstance(), new Subscription.OnData() { // from class: com.lumiyaviewer.lumiya.ui.minimap.-$Lambda$HQUtmVzLYkemE78mCklVmVxMXms
        private final /* synthetic */ void $m$0(Object obj) {
            ((MinimapActivity) this).m649com_lumiyaviewer_lumiya_ui_minimap_MinimapActivitymthref0((CurrentLocationInfo) obj);
        }

        @Override // com.lumiyaviewer.lumiya.react.Subscription.OnData
        public final void onData(Object obj) {
            $m$0(obj);
        }
    });

    @BindView(R.id.detailsWithOnlineStatus)
    ViewGroup detailsLayout;

    @BindView(R.id.selector)
    FrameLayout selectorLayout;

    @BindView(R.id.splitMainLayout)
    LinearLayout splitMainLayout;

    @BindView(R.id.split_object_popups_left_spacer)
    View splitObjectPopupsLeftSpacer;

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onCurrentLocationInfo, reason: merged with bridge method [inline-methods] */
    public void m649com_lumiyaviewer_lumiya_ui_minimap_MinimapActivitymthref0(CurrentLocationInfo currentLocationInfo) {
        if (currentLocationInfo != null) {
            ParcelData parcelData = currentLocationInfo.parcelData();
            String name = parcelData != null ? parcelData.getName() : null;
            if (name == null) {
                name = getString(R.string.name_loading_title);
            }
            setActivityTitle(name, getString(R.string.nearby_users_format, new Object[]{Integer.valueOf(currentLocationInfo.nearbyUsers())}));
        }
    }

    private void setActivityTitle(@Nullable String str, @Nullable String str2) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setTitle(str);
            supportActionBar.setSubtitle(str2);
        }
        setTitle(str);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.split_two_panels);
        ButterKnife.bind(this);
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
            beginTransaction.add(R.id.selector, MinimapFragment.newInstance(activeAgentID));
        }
        if (supportFragmentManager.findFragmentById(R.id.details) == null) {
            beginTransaction.add(R.id.details, NearbyPeopleMinimapFragment.newInstance(activeAgentID));
        }
        beginTransaction.commit();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        UserManager userManager = ActivityUtils.getUserManager(getIntent());
        if (userManager != null) {
            this.currentLocationInfo.subscribe(userManager.getCurrentLocationInfo(), SubscriptionSingleKey.Value);
        } else {
            this.currentLocationInfo.unsubscribe();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        this.currentLocationInfo.unsubscribe();
        super.onStop();
    }
}
