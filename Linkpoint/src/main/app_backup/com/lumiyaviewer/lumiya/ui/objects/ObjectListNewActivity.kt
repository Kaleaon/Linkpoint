package com.lumiyaviewer.lumiya.ui.objects
import java.util.*

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.lumiyaviewer.lumiya.R
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity

class ObjectListNewActivity : MasterDetailsActivity {

    class ObjectDetailsActivityFactory : FragmentActivityFactory {

        private class InstanceHolder {
            /* access modifiers changed from: private */
            ObjectDetailsActivityFactory Instance = ObjectDetailsActivityFactory()

            private InstanceHolder() {
            }
        }

        ObjectDetailsActivityFactory getInstance() {
            return InstanceHolder.Instance
        }

        Intent createIntent(Context context, Bundle bundle) {
            Intent intent = Intent(context, ObjectListNewActivity.class)
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle)
            ActivityUtils.setActiveAgentID(intent, ActivityUtils.getActiveAgentID(bundle))
            return intent
        }

        Class<? : Fragment> getFragmentClass() {
            return ObjectDetailsFragment.class
        }
    }

    /* access modifiers changed from: protected */
    FragmentActivityFactory getDetailsFragmentFactory() {
        return ObjectDetailsActivityFactory.getInstance()
    }

    /* access modifiers changed from: protected */
    Boolean isRootDetailsFragment(Class<? : Fragment> cls) {
        if (cls != UserProfileFragment.class) {
            return super.isRootDetailsFragment(cls)
        }
        return true
    }

    /* access modifiers changed from: protected */
    Unit onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle)
        setDefaultTitle(getString(R.string.objects_activity_caption), (String) null)
    }

    /* access modifiers changed from: protected */
    Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), (Bundle) null))
    }
}
