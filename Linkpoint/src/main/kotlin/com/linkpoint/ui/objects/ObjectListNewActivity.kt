package com.linkpoint.ui.objects
import java.util.*

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import com.linkpoint.R
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

class ObjectListNewActivity : MasterDetailsActivity() {

    @JvmStatic
    class ObjectDetailsActivityFactory : FragmentActivityFactory {

        @JvmStatic
private class InstanceHolder {
            /* access modifiers changed from: private */
            const val ObjectDetailsActivityFactory Instance = ObjectDetailsActivityFactory()

            private InstanceHolder() {
            }
        }

        @JvmStatic
    ObjectDetailsActivityFactory getInstance() {
            return InstanceHolder.Instance
        }

        public Intent createIntent(Context context, Bundle bundle) {
            Intent intent = Intent(context, ObjectListNewActivity.class)
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
            ActivityUtils.setActiveAgentID(intent, ActivityUtils.getActiveAgentID(bundle))
            return intent
        }

        public Class<? : Fragment> getFragmentClass() {
            return ObjectDetailsFragment.class
        }
    }

    /* access modifiers changed from: protected */
    public FragmentActivityFactory getDetailsFragmentFactory() {
        return ObjectDetailsActivityFactory.getInstance()
    }

    /* access modifiers changed from: protected */
    public Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        if (cls != UserProfileFragment.class) {
            return super.isRootDetailsFragment(cls)
        }
        return true
    }

    /* access modifiers changed from: protected */
    public Unit onCreate(Bundle bundle) {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.objects_activity_caption), (String) null)
    }

    /* access modifiers changed from: protected */
    public Fragment onCreateMasterFragment(Intent intent, Bundle bundle) {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), (Bundle) null))
    }
}
