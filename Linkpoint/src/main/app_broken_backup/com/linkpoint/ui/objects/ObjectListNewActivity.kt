package com.linkpoint.ui.objects
import java.util.*

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.linkpoint.R
import com.linkpoint.ui.chat.profiles.UserProfileFragment
import com.linkpoint.ui.common.ActivityUtils
import com.linkpoint.ui.common.FragmentActivityFactory
import com.linkpoint.ui.common.MasterDetailsActivity

class ObjectListNewActivity : MasterDetailsActivity {

    class ObjectDetailsActivityFactory : FragmentActivityFactory {

        private class InstanceHolder {
            /* access modifiers changed from: private */
            ObjectDetailsActivityFactory Instance = ObjectDetailsActivityFactory()

            private InstanceHolder() {
            }
        }

        fun getInstance(): ObjectDetailsActivityFactory {
            return InstanceHolder.Instance
        }

        fun createIntent(Context context, Bundle bundle): Intent {
            Intent intent = Intent(context, ObjectListNewActivity.class)
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
            ActivityUtils.setActiveAgentID(intent, ActivityUtils.getActiveAgentID(bundle))
            return intent
        }

        fun getFragmentClass(): Class<? : Fragment> {
            return ObjectDetailsFragment.class
        }
    }

    /* access modifiers changed from: protected */
    fun getDetailsFragmentFactory(): FragmentActivityFactory {
        return ObjectDetailsActivityFactory.getInstance()
    }

    /* access modifiers changed from: protected */
    fun isRootDetailsFragment(Class<? : Fragment> cls): Boolean {
        if (cls != UserProfileFragment.class) {
            return super.isRootDetailsFragment(cls)
        }
        return true
    }

    /* access modifiers changed from: protected */
    fun onCreate(@Nullable Bundle bundle): Unit {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.objects_activity_caption), (String) null)
    }

    /* access modifiers changed from: protected */
    fun onCreateMasterFragment(Intent intent, @Nullable Bundle bundle): Fragment {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), (Bundle) null))
    }
}
