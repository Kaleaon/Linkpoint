package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class SettingsActivity extends MasterDetailsActivity {
    private final FragmentActivityFactory detailsFragmentFactory = new FragmentActivityFactory() {
        public Intent createIntent(Context context, Bundle bundle) {
            return null;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return SettingsFragment.class;
        }
    };

    /* access modifiers changed from: protected */
    public FragmentActivityFactory getDetailsFragmentFactory() {
        return this.detailsFragmentFactory;
    }

    /* access modifiers changed from: protected */
    public Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        return bundle == null ? SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId()) : super.getNewDetailsFragmentArguments(bundle, bundle2);
    }

    /* access modifiers changed from: protected */
    public boolean handleConnectionEvents() {
        return false;
    }

    /* access modifiers changed from: protected */
    public boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        return cls == SettingsFragment.class;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setDefaultTitle(getString(R.string.settings_title), (String) null);
    }

    /* access modifiers changed from: protected */
    public Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return new SettingsSelectionFragment();
    }
}
