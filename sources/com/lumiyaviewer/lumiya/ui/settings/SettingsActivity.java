// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.settings;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.settings:
//            SettingsPage, SettingsFragment, SettingsSelectionFragment

public class SettingsActivity extends MasterDetailsActivity
{

    private final FragmentActivityFactory detailsFragmentFactory = new FragmentActivityFactory() {

        final SettingsActivity this$0;

        public Intent createIntent(Context context, Bundle bundle)
        {
            return null;
        }

        public Class getFragmentClass()
        {
            return com/lumiyaviewer/lumiya/ui/settings/SettingsFragment;
        }

            
            {
                this$0 = SettingsActivity.this;
                super();
            }
    };

    public SettingsActivity()
    {
    }

    protected FragmentActivityFactory getDetailsFragmentFactory()
    {
        return detailsFragmentFactory;
    }

    protected Bundle getNewDetailsFragmentArguments(Bundle bundle, Bundle bundle1)
    {
        if (bundle == null)
        {
            return SettingsFragment.makeSelection(SettingsPage.PageConnection.getPageResourceId());
        } else
        {
            return super.getNewDetailsFragmentArguments(bundle, bundle1);
        }
    }

    protected boolean handleConnectionEvents()
    {
        return false;
    }

    protected boolean isRootDetailsFragment(Class class1)
    {
        return class1 == com/lumiyaviewer/lumiya/ui/settings/SettingsFragment;
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setDefaultTitle(getString(0x7f090303), null);
    }

    protected Fragment onCreateMasterFragment(Intent intent, Bundle bundle)
    {
        return new SettingsSelectionFragment();
    }
}
