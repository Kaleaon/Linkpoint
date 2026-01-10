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
        fun createIntent(Context context, Bundle bundle): Intent {
            return null
        }

        fun getFragmentClass(): Class<? : Fragment> {
            return SettingsFragment.class
        }
    }

    /* access modifiers changed from: protected */
    fun getDetailsFragmentFactory(): FragmentActivityFactory {
        return this.detailsFragmentFactory
    }

    /* access modifiers changed from: protected */
    fun getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2): Bundle {
        return bundle == null ? SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId()) : super.getNewDetailsFragmentArguments(bundle, bundle2)
    }

    /* access modifiers changed from: protected */
    fun handleConnectionEvents(): Boolean {
        return false
    }

    /* access modifiers changed from: protected */
    fun isRootDetailsFragment(Class<? : Fragment> cls): Boolean {
        return cls == SettingsFragment.class
    }

    /* access modifiers changed from: protected */
    fun onCreate(@Nullable Bundle bundle)  {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.settings_title), (String) null)
    }

    /* access modifiers changed from: protected */
    fun onCreateMasterFragment(Intent intent, @Nullable Bundle bundle): Fragment {
        return SettingsSelectionFragment()
    }
}
