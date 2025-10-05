package com.linkpoint.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.linkpoint.ui.chat.profiles.GroupProfileFragment;
import com.linkpoint.ui.chat.profiles.UserProfileFragment;
import com.linkpoint.ui.common.ActivityUtils;
import com.linkpoint.ui.common.FragmentActivityFactory;
import com.linkpoint.ui.common.MasterDetailsActivity;

public class SearchGridActivity extends MasterDetailsActivity {
    /* access modifiers changed from: protected */
    public FragmentActivityFactory getDetailsFragmentFactory() {
        return null;
    }

    /* access modifiers changed from: protected */
    public boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        return cls == UserProfileFragment.class || cls == GroupProfileFragment.class || cls == ParcelInfoFragment.class;
    }

    /* access modifiers changed from: protected */
    public Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return SearchGridFragment.newInstance(ActivityUtils.getActiveAgentID(intent));
    }
}
