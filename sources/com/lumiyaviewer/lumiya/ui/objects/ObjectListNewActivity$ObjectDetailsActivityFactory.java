// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.objects;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.lumiyaviewer.lumiya.ui.common.ActivityUtils;
import com.lumiyaviewer.lumiya.ui.common.FragmentActivityFactory;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.objects:
//            ObjectListNewActivity, ObjectDetailsFragment

public static class InstanceHolder
    implements FragmentActivityFactory
{
    private static class InstanceHolder
    {

        private static final ObjectListNewActivity.ObjectDetailsActivityFactory Instance = new ObjectListNewActivity.ObjectDetailsActivityFactory();

        static ObjectListNewActivity.ObjectDetailsActivityFactory _2D_get0()
        {
            return Instance;
        }


        private InstanceHolder()
        {
        }
    }


    public static <init> getInstance()
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

    public InstanceHolder()
    {
    }
}
