package com.lumiyaviewer.lumiya.ui.objects;
import java.util.*;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.R;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class ObjectListNewActivity extends MasterDetailsActivity {

    public static class ObjectDetailsActivityFactory implements FragmentActivityFactory {

        private static class InstanceHolder {
            /* access modifiers changed from: private */
            public static final ObjectDetailsActivityFactory Instance = new ObjectDetailsActivityFactory();

            private InstanceHolder() {
            }
        }

        public static ObjectDetailsActivityFactory getInstance() {
            return InstanceHolder.Instance;
        }

        public Intent createIntent(Context context, Bundle bundle) {
            Intent intent = new Intent(context, ObjectListNewActivity.class);
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle);
            ActivityUtils.setActiveAgentID(intent, ActivityUtils.getActiveAgentID(bundle));
            return intent;
        }

        public Class<? extends Fragment> getFragmentClass() {
            return ObjectDetailsFragment.class;
        }
    }

    /* access modifiers changed from: protected */
    public FragmentActivityFactory getDetailsFragmentFactory() {
        return ObjectDetailsActivityFactory.getInstance();
    }

    /* access modifiers changed from: protected */
    public boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        if (cls != UserProfileFragment.class) {
            return super.isRootDetailsFragment(cls);
        }
        return true;
    }

    /* access modifiers changed from: protected */
    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setDefaultTitle(getString(R.string.objects_activity_caption), (String) null);
    }

    /* access modifiers changed from: protected */
    public Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), (Bundle) null));
    }
}
