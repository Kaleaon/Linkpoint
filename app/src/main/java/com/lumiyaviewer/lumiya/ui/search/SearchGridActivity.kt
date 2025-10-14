package com.lumiyaviewer.lumiya.ui.search

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import com.lumiyaviewer.lumiya.ui.chat.profiles.GroupProfileFragment
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity

class SearchGridActivity : MasterDetailsActivity {
    /* access modifiers changed from: protected */
    FragmentActivityFactory getDetailsFragmentFactory() {
        return null
    }

    /* access modifiers changed from: protected */
    Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        return cls == UserProfileFragment.class || cls == GroupProfileFragment.class || cls == ParcelInfoFragment.class
    }

    /* access modifiers changed from: protected */
    Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return SearchGridFragment.newInstance(ActivityUtils.getActiveAgentID(intent))
    }
}
