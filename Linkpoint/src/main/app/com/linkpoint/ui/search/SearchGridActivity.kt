package com.linkpoint.ui.search

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.linkpoint.ui.chat.profiles.GroupProfileFragment
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

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
