package com.linkpoint.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.linkpoint.R
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

class SettingsActivity : MasterDetailsActivity {
    private FragmentActivityFactory detailsFragmentFactory = FragmentActivityFactory() {
        Intent createIntent(Context context, Bundle bundle) {
            return null
        }

        Class<? : Fragment> getFragmentClass() {
            return SettingsFragment.class
        }
    }

    /* access modifiers changed from: protected */
    FragmentActivityFactory getDetailsFragmentFactory() {
        return this.detailsFragmentFactory
    }

    /* access modifiers changed from: protected */
    Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        return bundle == null ? SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId()) : super.getNewDetailsFragmentArguments(bundle, bundle2)
    }

    /* access modifiers changed from: protected */
    Boolean handleConnectionEvents() {
        return false
    }

    /* access modifiers changed from: protected */
    Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        return cls == SettingsFragment.class
    }

    /* access modifiers changed from: protected */
    Unit onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.settings_title), (String) null)
    }

    /* access modifiers changed from: protected */
    Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return SettingsSelectionFragment()
    }
}
