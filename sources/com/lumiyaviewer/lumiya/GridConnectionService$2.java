// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.google.common.base.Strings;
import com.lumiyaviewer.lumiya.slproto.SLGridConnection;
import com.lumiyaviewer.lumiya.slproto.users.manager.UserManager;

// Referenced classes of package com.lumiyaviewer.lumiya:
//            GridConnectionService

class this._cls0 extends BroadcastReceiver
{

    final GridConnectionService this$0;

    public void onReceive(Context context, Intent intent)
    {
        if (Strings.nullToEmpty(intent.getData().getSchemeSpecificPart()).equals("com.lumiyaviewer.lumiya.cloud") && GridConnectionService._2D_get1() != null && GridConnectionService._2D_get1().getConnectionState() == com.lumiyaviewer.lumiya.slproto.ionState.Connected)
        {
            context = GridConnectionService._2D_get1().getActiveAgentUUID();
            if (context != null)
            {
                context = UserManager.getUserManager(context);
                if (context != null)
                {
                    GridConnectionService._2D_wrap1(GridConnectionService.this, context);
                }
            }
        }
    }

    erManager()
    {
        this$0 = GridConnectionService.this;
        super();
    }
}
