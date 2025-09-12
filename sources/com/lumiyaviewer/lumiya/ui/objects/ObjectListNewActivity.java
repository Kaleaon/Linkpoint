// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.lumiyaviewer.lumiya.ui.chat.profiles.UserProfileFragment;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;
import com.lumiyaviewer.lumiya.ui.common.MasterDetailsActivity;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectSelectorFragment, ObjectDetailsFragment

public class ObjectListNewActivity extends MasterDetailsActivity
{
    public static class ObjectDetailsActivityFactory
        implements FragmentActivityFactory
    {

        public static ObjectDetailsActivityFactory getInstance()
        {
            return InstanceHolder._2D_get0();
        }

        public Intent createIntent(Context context, Bundle bundle)
        {
            context = new Intent(context, com/lumiyaviewer/lumiya/ui/objects/ObjectListNewActivity);
            context.putExtra("selection", bundle);
            ActivityUtils.setActiveAgentID(context, ActivityUtils.getActiveAgentID(bundle));
            return context;
        }

        public Class getFragmentClass()
        {
            return com/lumiyaviewer/lumiya/ui/objects/ObjectDetailsFragment;
        }

        public ObjectDetailsActivityFactory()
        {
        }
    }

    private static class ObjectDetailsActivityFactory.InstanceHolder
    {

        private static final ObjectDetailsActivityFactory Instance = new ObjectDetailsActivityFactory();

        static ObjectDetailsActivityFactory _2D_get0()
        {
            return Instance;
        }


        private ObjectDetailsActivityFactory.InstanceHolder()
        {
        }
    }


    public ObjectListNewActivity()
    {
    }

    protected FragmentActivityFactory getDetailsFragmentFactory()
    {
        return ObjectDetailsActivityFactory.getInstance();
    }

    protected boolean isRootDetailsFragment(Class class1)
    {
        if (class1 != com/lumiyaviewer/lumiya/ui/chat/profiles/UserProfileFragment)
        {
            return super.isRootDetailsFragment(class1);
        } else
        {
            return true;
        }
    }

    protected void onCreate(Bundle bundle)
    {
        super.onCreate(bundle);
        setDefaultTitle(getString(0x7f09024b), null);
    }

    protected Fragment onCreateMasterFragment(Intent intent, Bundle bundle)
    {
        return ObjectSelectorFragment.newInstance(ActivityUtils.makeFragmentArguments(ActivityUtils.getActiveAgentID(intent), null));
    }
}
