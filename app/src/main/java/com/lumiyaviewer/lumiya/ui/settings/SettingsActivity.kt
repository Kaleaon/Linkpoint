package com.lumiyaviewer.lumiya.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity

class SettingsActivity : MasterDetailsActivity() {

    private val detailsFragmentFactory = object : FragmentActivityFactory {
        override fun createIntent(context: Context, args: Bundle?): Intent? = null

        override fun getFragmentClass(): Class<out Fragment> = SettingsFragment::class.java
    }

    override fun getDetailsFragmentFactory(): FragmentActivityFactory {
        return detailsFragmentFactory
    }

    override fun getNewDetailsFragmentArguments(
        selectedItem: Bundle?,
        itemFroDetails: Bundle?
    ): Bundle? {
        return if (selectedItem == null) {
            SettingsFragment.makeSelection(SettingsPage.PageConnection.pageResourceId)
        } else {
            super.getNewDetailsFragmentArguments(selectedItem, itemFroDetails)
        }
    }

    override fun handleConnectionEvents(): Boolean = false

    override fun isRootDetailsFragment(fragmentClass: Class<out Fragment>): Boolean {
        return fragmentClass == SettingsFragment::class.java
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultTitle(getString(R.string.settings_title), null)
    }

    override fun onCreateMasterFragment(intent: Intent, savedInstanceState: Bundle?): Fragment {
        return SettingsSelectionFragment()
    }
}
