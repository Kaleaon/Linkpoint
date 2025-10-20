package com.linkpoint.ui.myava
import java.util.*

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity
import com.linkpoint.ui.outfits.OutfitsFragment

class MyAvatarActivity : MasterDetailsActivity() {
    private val FragmentActivityFactory detailsFragmentFactory = FragmentActivityFactory() {
        public Intent createIntent(Context context, Bundle bundle) {
            return null
        }

        public Class<? : Fragment> getFragmentClass() {
            return MyProfileFragment.class
        }
    }

    /* access modifiers changed from: protected */
    public FragmentActivityFactory getDetailsFragmentFactory() {
        return this.detailsFragmentFactory
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:1:0x0002, code lost:
        r0 = com.lumiyaviewer.lumiya.ui.common.ActivityUtils.getActiveAgentID(getIntent())
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public android.os.Bundle getNewDetailsFragmentArguments(@android.support.annotation.Nullable android.os.Bundle r2, @android.support.annotation.Nullable android.os.Bundle r3) {
        /*
            r1 = this
            if (r2 != 0) goto L_0x0015
            android.content.Intent r0 = r1.getIntent()
            java.util.UUID r0 = com.lumiyaviewer.lumiya.ui.common.ActivityUtils.getActiveAgentID((android.content.Intent) r0)
            if (r0 == 0) goto L_0x0015
            com.lumiyaviewer.lumiya.slproto.users.ChatterID$ChatterIDUser r0 = com.lumiyaviewer.lumiya.slproto.users.ChatterID.getUserChatterID(r0, r0)
            android.os.Bundle r0 = com.lumiyaviewer.lumiya.ui.myava.MyProfileFragment.makeSelection(r0)
            return r0
        L_0x0015:
            android.os.Bundle r0 = super.getNewDetailsFragmentArguments(r2, r3)
            return r0
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.ui.myava.MyAvatarActivity.getNewDetailsFragmentArguments(android.os.Bundle, android.os.Bundle):android.os.Bundle")
    }

    /* access modifiers changed from: protected */
    public Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        return cls == MyProfileFragment.class || cls == OutfitsFragment.class || cls == MuteListFragment.class
    }

    /* access modifiers changed from: protected */
    public Fragment onCreateMasterFragment(Intent intent, Bundle bundle) {
        return MyAvatarFragment.newInstance(ActivityUtils.getActiveAgentID(intent))
    }
}
