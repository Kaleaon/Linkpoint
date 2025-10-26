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
         public fun createIntent(context: Context, bundle: Bundle): Intent {
            return null
        }

        public Class<? : Fragment> getFragmentClass() {
            return SettingsFragment.class
        }
    }

    /* access modifiers changed from: protected */
     public fun getDetailsFragmentFactory(): FragmentActivityFactory {
        return this.detailsFragmentFactory
    }

    /* access modifiers changed from: protected */
     public fun getNewDetailsFragmentArguments(bundle: Bundle, bundle2: Bundle): Bundle {
        return bundle == null ? SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId()) : super.getNewDetailsFragmentArguments(bundle, bundle2)
    }

    /* access modifiers changed from: protected */
     public fun handleConnectionEvents(): Boolean {
        return false
    }

    /* access modifiers changed from: protected */
     public fun isRootDetailsFragment(cls: Class<? : Fragment>): Boolean {
        return cls == SettingsFragment.class
    }

    /* access modifiers changed from: protected */
    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.settings_title), (String) null)
    }

    /* access modifiers changed from: protected */
     public fun onCreateMasterFragment(intent: Intent, bundle: Bundle): Fragment {
        return SettingsSelectionFragment()
    }
}
