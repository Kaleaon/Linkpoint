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
     fun getInstance(): ObjectDetailsActivityFactory {
            return InstanceHolder.Instance
        }

         public fun createIntent(context: Context, bundle: Bundle): Intent {
            val intent: Intent = Intent(context, ObjectListNewActivity.class)
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
            ActivityUtils.setActiveAgentID(intent, ActivityUtils.getActiveAgentID(bundle))
            return intent
        }

        public Class<? : Fragment> getFragmentClass() {
            return ObjectDetailsFragment.class
        }
    }

    /* access modifiers changed from: protected */
     public fun getDetailsFragmentFactory(): FragmentActivityFactory {
        return ObjectDetailsActivityFactory.getInstance()
    }

    /* access modifiers changed from: protected */
     public fun isRootDetailsFragment(cls: Class<? : Fragment>): Boolean {
        if (cls != UserProfileFragment.class) {
            return super.isRootDetailsFragment(cls)
        }
        return true
    }

    /* access modifiers changed from: protected */
    override fun onCreate(bundle: Bundle) {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.objects_activity_caption), (String) null)
    }

    /* access modifiers changed from: protected */
     public override fun onCreateMasterFragment(intent: Intent, bundle: Bundle): Fragment {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), (Bundle) null))
    }
}
