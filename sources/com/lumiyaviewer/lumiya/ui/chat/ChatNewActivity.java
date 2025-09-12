// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.react.Subscribable;
import com.lumiyaviewer.lumiya.react.Subscription;
import com.lumiyaviewer.lumiya.react.SubscriptionSingleDataPool;
import com.lumiyaviewer.lumiya.react.UIThreadExecutor;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.slproto.users.ParcelData;
import com.lumiyaviewer.lumiya.slproto.users.manager.CurrentLocationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationInfo;
import com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;
import com.lumiyaviewer.lumiya.ui.chat.contacts.ChatFragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.chat.profiles.ParcelPropertiesFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.DetailsActivity;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.chat:
//            ChatFragment, ContactsFragment

public class ChatNewActivity extends MasterDetailsActivity
    implements com.lumiyaviewer.lumiya.slproto.users.manager.UnreadNotificationManager.NotifyCapture
{

    private Subscription currentLocationInfoSubscription;
    private final com.lumiyaviewer.lumiya.react.Subscription.OnData onCurrentLocation = new _2D_.Lambda.NRCeOQv_2D_yeRY8P8t9O3BV_sPyX4(this);

    public ChatNewActivity()
    {
    }

    protected FragmentActivityFactory getDetailsFragmentFactory()
    {
        return ChatFragmentActivityFactory.getInstance();
    }

    protected Bundle getNewDetailsFragmentArguments(Bundle bundle, Bundle bundle1)
    {
        if (bundle1 != null)
        {
            return super.getNewDetailsFragmentArguments(bundle, bundle1);
        }
        bundle1 = ActivityUtils.getActiveAgentID(getIntent());
        if (bundle1 != null)
        {
            return ChatFragment.makeSelection(ChatterID.getLocalChatterID(bundle1));
        } else
        {
            return super.getNewDetailsFragmentArguments(bundle, null);
        }
    }

    void lambda$_2D_com_lumiyaviewer_lumiya_ui_chat_ChatNewActivity_4384(CurrentLocationInfo currentlocationinfo)
    {
        currentlocationinfo = currentlocationinfo.parcelData();
        if (currentlocationinfo != null)
        {
            currentlocationinfo = currentlocationinfo.getName();
        } else
        {
            currentlocationinfo = null;
        }
        if (currentlocationinfo == null)
        {
            currentlocationinfo = getString(0x7f0901c8);
        }
        setDefaultTitle(currentlocationinfo, null);
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setDefaultTitle(getString(0x7f090044), null);
    }

    protected Fragment onCreateMasterFragment(Intent intent, Bundle bundle)
    {
        return ContactsFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), null));
    }

    public Intent onGetNotifyCaptureIntent(UnreadNotificationInfo unreadnotificationinfo, Intent intent)
    {
        intent.addFlags(0x20000000);
        intent.putExtra("fromSameActivity", true);
        return intent;
    }

    protected void onPause()
    {
        UserManager usermanager = ActivityUtils.getUserManager(getIntent());
        if (usermanager != null)
        {
            usermanager.getUnreadNotificationManager().clearNotifyCapture(this);
        }
        super.onPause();
    }

    protected void onResume()
    {
        super.onResume();
        UserManager usermanager = ActivityUtils.getUserManager(getIntent());
        if (usermanager != null)
        {
            usermanager.getUnreadNotificationManager().setNotifyCapture(this);
        }
    }

    protected void onStart()
    {
        super.onStart();
        UserManager usermanager = ActivityUtils.getUserManager(getIntent());
        if (usermanager != null)
        {
            currentLocationInfoSubscription = usermanager.getCurrentLocationInfo().subscribe(SubscriptionSingleDataPool.getSingleDataKey(), UIThreadExecutor.getInstance(), onCurrentLocation);
        }
        Intent intent = getIntent();
        if (intent.hasExtra("parcelData"))
        {
            ParcelData parceldata = (ParcelData)intent.getSerializableExtra("parcelData");
            intent.removeExtra("parcelData");
            if (usermanager != null)
            {
                DetailsActivity.showEmbeddedDetails(this, com/lumiyaviewer/lumiya/ui/chat/profiles/ParcelPropertiesFragment, ParcelPropertiesFragment.makeSelection(usermanager.getUserID(), parceldata));
            }
        }
    }

    protected void onStop()
    {
        if (currentLocationInfoSubscription != null)
        {
            currentLocationInfoSubscription.unsubscribe();
            currentLocationInfoSubscription = null;
        }
        super.onStop();
    }
}
