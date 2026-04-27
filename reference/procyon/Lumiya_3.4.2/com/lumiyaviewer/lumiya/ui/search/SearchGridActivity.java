// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.search;

import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.content.Intent;
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class SearchGridActivity extends MasterDetailsActivity
{
    @Override
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return null;
    }
    
    @Override
    protected boolean isRootDetailsFragment(final Class<? extends Fragment> clazz) {
        boolean b2;
        final boolean b = b2 = true;
        if (clazz != UserProfileFragment.class) {
            if (clazz == GroupProfileFragment.class) {
                b2 = b;
            }
            else {
                b2 = b;
                if (clazz != ParcelInfoFragment.class) {
                    b2 = false;
                }
            }
        }
        return b2;
    }
    
    @Override
    protected Fragment onCreateMasterFragment(final Intent intent, @Nullable final Bundle bundle) {
        return SearchGridFragment.newInstance(ActivityUtils.getActiveAgentID(intent));
    }
}
