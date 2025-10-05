package com.linkpoint.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import com.linkpoint.R
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

class SettingsActivity : MasterDetailsActivity() {
    private val FragmentActivityFactory detailsFragmentFactory = FragmentActivityFactory() {
        public Intent createIntent(Context context, Bundle bundle) {
            return null
        }

        public Class<? : Fragment> getFragmentClass() {
            return SettingsFragment.class
        }
    }

    /* access modifiers changed from: protected */
    public FragmentActivityFactory getDetailsFragmentFactory() {
        return this.detailsFragmentFactory
    }

    /* access modifiers changed from: protected */
    public Bundle getNewDetailsFragmentArguments(Bundle bundle, Bundle bundle2) {
        return bundle == null ? SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId()) : super.getNewDetailsFragmentArguments(bundle, bundle2)
    }

    /* access modifiers changed from: protected */
    public Boolean handleConnectionEvents() {
        return false
    }

    /* access modifiers changed from: protected */
    public Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        return cls == SettingsFragment.class
    }

    /* access modifiers changed from: protected */
    public Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.settings_title), (String) null)
    }

    /* access modifiers changed from: protected */
    public Fragment onCreateMasterFragment(Intent intent, Bundle bundle) {
        return SettingsSelectionFragment()
    }
}
