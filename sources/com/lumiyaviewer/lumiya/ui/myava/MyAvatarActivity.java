// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.myava:
//            MyProfileFragment, MuteListFragment, MyAvatarFragment

public class MyAvatarActivity extends MasterDetailsActivity
{

    private final FragmentActivityFactory detailsFragmentFactory = new FragmentActivityFactory() {

        final MyAvatarActivity this$0;

        public Intent createIntent(Context context, Bundle bundle)
        {
            return null;
        }

        public Class getFragmentClass()
        {
            return com/lumiyaviewer/lumiya/ui/myava/MyProfileFragment;
        }

            
            {
                this$0 = MyAvatarActivity.this;
                super();
            }
    };

    public MyAvatarActivity()
    {
    }

    protected FragmentActivityFactory getDetailsFragmentFactory()
    {
        return detailsFragmentFactory;
    }

    protected Bundle getNewDetailsFragmentArguments(Bundle bundle, Bundle bundle1)
    {
        if (bundle == null)
        {
            java.util.UUID uuid = ActivityUtils.getActiveAgentID(getIntent());
            if (uuid != null)
            {
                return MyProfileFragment.makeSelection(ChatterID.getUserChatterID(uuid, uuid));
            }
        }
        return super.getNewDetailsFragmentArguments(bundle, bundle1);
    }

    protected boolean isRootDetailsFragment(Class class1)
    {
        while (class1 == com/lumiyaviewer/lumiya/ui/myava/MyProfileFragment || class1 == com/lumiyaviewer/lumiya/ui/outfits/OutfitsFragment || class1 == com/lumiyaviewer/lumiya/ui/myava/MuteListFragment) 
        {
            return true;
        }
        return false;
    }

    protected Fragment onCreateMasterFragment(Intent intent, Bundle bundle)
    {
        return MyAvatarFragment.newInstance(ActivityUtils.getActiveAgentID(intent));
    }
}
