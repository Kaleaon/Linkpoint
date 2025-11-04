package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

/* loaded from: classes.dex */
public class SettingsActivity extends MasterDetailsActivity {
    private final FragmentActivityFactory detailsFragmentFactory = new FragmentActivityFactory() { // from class: com.lumiyaviewer.lumiya.ui.settings.SettingsActivity.1
        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Intent createIntent(Context context, Bundle bundle) {
            return null;
        }

        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Class<? extends Fragment> getFragmentClass() {
            return SettingsFragment.class;
        }
    };

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return this.detailsFragmentFactory;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        return bundle == null ? SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId()) : super.getNewDetailsFragmentArguments(bundle, bundle2);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.ConnectedActivity
    protected boolean handleConnectionEvents() {
        return false;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity, com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    protected boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        return cls == SettingsFragment.class;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity, com.lumiyaviewer.lumiya.ui.common.DetailsActivity, com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setDefaultTitle(getString(R.string.settings_title), null);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return new SettingsSelectionFragment();
    }
}
