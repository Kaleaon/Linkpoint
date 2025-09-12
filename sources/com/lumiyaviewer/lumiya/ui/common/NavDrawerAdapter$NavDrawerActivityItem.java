// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.ui.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.ui.common:
//            NavDrawerAdapter, ActivityUtils

private static class activityClass extends activityClass
{

    final Class activityClass;

    public void onClick(Context context)
    {
        Intent intent = new Intent(context, activityClass);
        intent.addFlags(0x20000);
        if (context instanceof Activity)
        {
            UUID uuid = ActivityUtils.getActiveAgentID(((Activity)context).getIntent());
            if (uuid != null)
            {
                intent.putExtra("activeAgentUUID", uuid.toString());
            }
        }
        context.startActivity(intent);
    }

    (int i, int j, int k, Class class1)
    {
        super(i, j, k);
        activityClass = class1;
    }
}
