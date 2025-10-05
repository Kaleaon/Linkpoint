package com.linkpoint.ui.search

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import com.linkpoint.ui.chat.profiles.GroupProfileFragment
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

class SearchGridActivity : MasterDetailsActivity() {
    override fun getDetailsFragmentFactory(): FragmentActivityFactory? {
        return null
    }

    override fun isRootDetailsFragment(cls: Class<out Fragment>): Boolean {
        return cls == UserProfileFragment::class.java || 
               cls == GroupProfileFragment::class.java || 
               cls == ParcelInfoFragment::class.java
    }

    override fun onCreateMasterFragment(intent: Intent, bundle: Bundle?): Fragment {
        return SearchGridFragment.newInstance(ActivityUtils.getActiveAgentID(intent))
    }
}