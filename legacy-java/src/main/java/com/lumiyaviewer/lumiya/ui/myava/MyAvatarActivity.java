package com.lumiyaviewer.lumiya.ui.myava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.slproto.users.ChatterID;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;
import com.lumiyaviewer.lumiya.ui.outfits.OutfitsFragment;
import java.util.UUID;

/* loaded from: classes.dex */
public class MyAvatarActivity extends MasterDetailsActivity {
    private final FragmentActivityFactory detailsFragmentFactory = new FragmentActivityFactory() { // from class: com.lumiyaviewer.lumiya.ui.myava.MyAvatarActivity.1
        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Intent createIntent(Context context, Bundle bundle) {
            return null;
        }

        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Class<? extends Fragment> getFragmentClass() {
            return MyProfileFragment.class;
        }
    };

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return this.detailsFragmentFactory;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected Bundle getNewDetailsFragmentArguments(@Nullable Bundle bundle, @Nullable Bundle bundle2) {
        UUID activeAgentID;
        return (bundle != null || (activeAgentID = ActivityUtils.getActiveAgentID(getIntent())) == null) ? super.getNewDetailsFragmentArguments(bundle, bundle2) : MyProfileFragment.makeSelection(ChatterID.getUserChatterID(activeAgentID, activeAgentID));
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity, com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    protected boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        return cls == MyProfileFragment.class || cls == OutfitsFragment.class || cls == MuteListFragment.class;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return MyAvatarFragment.newInstance(ActivityUtils.getActiveAgentID(intent));
    }
}
