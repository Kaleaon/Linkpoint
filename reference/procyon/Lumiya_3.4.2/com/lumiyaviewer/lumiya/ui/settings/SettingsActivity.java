// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.settings;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.content.Context;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class SettingsActivity extends MasterDetailsActivity
{
    private final FragmentActivityFactory detailsFragmentFactory;
    
    public SettingsActivity() {
        this.detailsFragmentFactory = new FragmentActivityFactory() {
            @Override
            public Intent createIntent(final Context context, final Bundle bundle) {
                return null;
            }
            
            @Override
            public Class<? extends Fragment> getFragmentClass() {
                return SettingsFragment.class;
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
            return SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId());
        }
        return super.getNewDetailsFragmentArguments(bundle, bundle2);
    }
    
    @Override
    protected boolean handleConnectionEvents() {
        return false;
    }
    
    @Override
    protected boolean isRootDetailsFragment(final Class<? extends Fragment> clazz) {
        return clazz == SettingsFragment.class;
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setDefaultTitle(this.getString(2131297027), null);
    }
    
    @Override
    protected Fragment onCreateMasterFragment(final Intent intent, @Nullable final Bundle bundle) {
        return new SettingsSelectionFragment();
    }
}
