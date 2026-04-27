// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.lumiyaviewer.lumiya.react.SimpleRequestHandler;
import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadNotificationManager

class this._cls0 extends SimpleRequestHandler
{

    final UnreadNotificationManager this$0;

    public void onRequest(Boolean boolean1)
    {
        UnreadNotificationManager._2D_get2(UnreadNotificationManager.this).execute(UnreadNotificationManager._2D_get1(UnreadNotificationManager.this));
    }

    public volatile void onRequest(Object obj)
    {
        onRequest((Boolean)obj);
    }

    ()
    {
        this$0 = UnreadNotificationManager.this;
        super();
    }
}
