package com.lumiyaviewer.lumiya.ui.objects;

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

/* loaded from: classes.dex */
public class ObjectListNewActivity extends MasterDetailsActivity {

    /* loaded from: classes.dex */
    public static class ObjectDetailsActivityFactory implements FragmentActivityFactory {

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes.dex */
        public static class InstanceHolder {
            private static final ObjectDetailsActivityFactory Instance = new ObjectDetailsActivityFactory();

            private InstanceHolder() {
            }
        }

        public static ObjectDetailsActivityFactory getInstance() {
            return InstanceHolder.Instance;
        }

        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Intent createIntent(Context context, Bundle bundle) {
            Intent intent = new Intent(context, (Class<?>) ObjectListNewActivity.class);
            intent.putExtra(MasterDetailsActivity.INTENT_SELECTION_KEY, bundle);
            ActivityUtils.setActiveAgentID(intent, ActivityUtils.getActiveAgentID(bundle));
            return intent;
        }

        @Override // com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory
        public Class<? extends Fragment> getFragmentClass() {
            return ObjectDetailsFragment.class;
        }
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return ObjectDetailsActivityFactory.getInstance();
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity, com.lumiyaviewer.lumiya.ui.common.DetailsActivity
    protected boolean isRootDetailsFragment(Class<? extends Fragment> cls) {
        if (cls != UserProfileFragment.class) {
            return super.isRootDetailsFragment(cls);
        }
        return true;
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity, com.lumiyaviewer.lumiya.ui.common.DetailsActivity, com.lumiyaviewer.lumiya.ui.common.ConnectedActivity, com.lumiyaviewer.lumiya.ui.common.ThemedActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        setDefaultTitle(getString(R.string.objects_activity_caption), null);
    }

    @Override // com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity
    protected Fragment onCreateMasterFragment(Intent intent, @Nullable Bundle bundle) {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), null));
    }
}
