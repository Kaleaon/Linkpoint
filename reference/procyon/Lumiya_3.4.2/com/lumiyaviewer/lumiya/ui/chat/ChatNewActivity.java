// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.chat;

import android.app.Activity;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import android.support.v4.app.Fragment;
import android.content.Intent;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.support.annotation.Nullable;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleKey;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class ChatNewActivity extends MasterDetailsActivity implements NotifyCapture
{
    private Subscription<SubscriptionSingleKey, CurrentLocationInfo> currentLocationInfoSubscription;
    private final Subscription.OnData<CurrentLocationInfo> onCurrentLocation;
    
    public ChatNewActivity() {
        this.onCurrentLocation = new _$Lambda$NRCeOQv_yeRY8P8t9O3BV_sPyX4(this);
    }
    
    @Override
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return ChatFragmentActivityFactory.getInstance();
    }
    
    @Override
    protected Bundle getNewDetailsFragmentArguments(@Nullable final Bundle bundle, @Nullable final Bundle bundle2) {
        if (bundle2 != null) {
            return super.getNewDetailsFragmentArguments(bundle, bundle2);
        }
        final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getIntent());
        if (activeAgentID != null) {
            return ChatFragment.makeSelection(ChatterID.getLocalChatterID(activeAgentID));
        }
        return super.getNewDetailsFragmentArguments(bundle, null);
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setDefaultTitle(this.getString(2131296324), null);
    }
    
    @Override
    protected Fragment onCreateMasterFragment(final Intent intent, @Nullable final Bundle bundle) {
        return ContactsFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), null));
    }
    
    @javax.annotation.Nullable
    @Override
    public Intent onGetNotifyCaptureIntent(@Nonnull final UnreadNotificationInfo unreadNotificationInfo, final Intent intent) {
        intent.addFlags(536870912);
        intent.putExtra("fromSameActivity", true);
        return intent;
    }
    
    @Override
    protected void onPause() {
        final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
        if (userManager != null) {
            userManager.getUnreadNotificationManager().clearNotifyCapture((UnreadNotificationManager.NotifyCapture)this);
        }
        super.onPause();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
        if (userManager != null) {
            userManager.getUnreadNotificationManager().setNotifyCapture((UnreadNotificationManager.NotifyCapture)this);
        }
    }
    
    @Override
    protected void onStart() {
        super.onStart();
        final UserManager userManager = ActivityUtils.getUserManager(this.getIntent());
        if (userManager != null) {
            this.currentLocationInfoSubscription = userManager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), this.onCurrentLocation);
        }
        final Intent intent = this.getIntent();
        if (intent.hasExtra("parcelData")) {
            final ParcelData parcelData = (ParcelData)intent.getSerializableExtra("parcelData");
            intent.removeExtra("parcelData");
            if (userManager != null) {
                DetailsActivity.showEmbeddedDetails(this, ParcelPropertiesFragment.class, ParcelPropertiesFragment.makeSelection(userManager.getUserID(), parcelData));
            }
        }
    }
    
    @Override
    protected void onStop() {
        if (this.currentLocationInfoSubscription != null) {
            this.currentLocationInfoSubscription.unsubscribe();
            this.currentLocationInfoSubscription = null;
        }
        super.onStop();
    }
}
