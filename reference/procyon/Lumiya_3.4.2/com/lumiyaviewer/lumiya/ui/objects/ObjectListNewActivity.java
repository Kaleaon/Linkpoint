// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

public class ObjectListNewActivity extends MasterDetailsActivity
{
    @Override
    protected FragmentActivityFactory getDetailsFragmentFactory() {
        return ObjectDetailsActivityFactory.getInstance();
    }
    
    @Override
    protected boolean isRootDetailsFragment(final Class<? extends Fragment> clazz) {
        return clazz == UserProfileFragment.class || super.isRootDetailsFragment(clazz);
    }
    
    @Override
    protected void onCreate(@Nullable final Bundle bundle) {
        super.onCreate(bundle);
        this.setDefaultTitle(this.getString(2131296843), null);
    }
    
    @Override
    protected Fragment onCreateMasterFragment(final Intent intent, @Nullable final Bundle bundle) {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), null));
    }
    
    public static class ObjectDetailsActivityFactory implements FragmentActivityFactory
    {
        public static ObjectDetailsActivityFactory getInstance() {
            return InstanceHolder.Instance;
        }
        
        @Override
        public Intent createIntent(final Context context, final Bundle bundle) {
            final Intent intent = new Intent(context, (Class)ObjectListNewActivity.class);
            intent.putExtra("selection", bundle);
            ActivityUtils.setActiveAgentID(intent, ActivityUtils.getActiveAgentID(bundle));
            return intent;
        }
        
        @Override
        public Class<? extends Fragment> getFragmentClass() {
            return ObjectDetailsFragment.class;
        }
        
        private static class InstanceHolder
        {
            private static final ObjectDetailsActivityFactory Instance;
            
            static {
                Instance = new ObjectDetailsActivityFactory();
            }
        }
    }
}
