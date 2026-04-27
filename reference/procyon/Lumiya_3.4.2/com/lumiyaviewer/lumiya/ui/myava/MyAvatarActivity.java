// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.myava;

import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import java.util.UUID;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class MyAvatarActivity extends MasterDetailsActivity
{
    private final FragmentActivityFactory detailsFragmentFactory;
    
    public MyAvatarActivity() {
        this.detailsFragmentFactory = new FragmentActivityFactory() {
            @Override
            public Intent createIntent(final Context context, final Bundle bundle) {
                return null;
            }
            
            @Override
            public Class<? extends Fragment> getFragmentClass() {
                return MyProfileFragment.class;
            }
        };
    }
    
    @Override
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return this.detailsFragmentFactory;
    }
    
    @Override
    protected Bundle getNewDetailsFragmentArguments(@Nullable final Bundle bundle, @Nullable final Bundle bundle2) {
        if (bundle == null) {
            final UUID activeAgentID = ActivityUtils.getActiveAgentID(this.getIntent());
            if (activeAgentID != null) {
                return UserProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, activeAgentID));
            }
        }
        return super.getNewDetailsFragmentArguments(bundle, bundle2);
    }
    
    @Override
    protected boolean isRootDetailsFragment(final Class<? extends Fragment> clazz) {
        boolean b2;
        final boolean b = b2 = true;
        if (clazz != MyProfileFragment.class) {
            if (clazz == OutfitsFragment.class) {
                b2 = b;
            }
            else {
                b2 = b;
                if (clazz != MuteListFragment.class) {
                    b2 = false;
                }
            }
        }
        return b2;
    }
    
    @Override
    protected Fragment onCreateMasterFragment(final Intent intent, @Nullable final Bundle bundle) {
        return MyAvatarFragment.newInstance(ActivityUtils.getActiveAgentID(intent));
    }
}
