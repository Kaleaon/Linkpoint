// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import java.util.concurrent.Executor;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            UnreadNotificationManager

class this._cls0
    implements Runnable
{

    final UnreadNotificationManager this$0;

    public void run()
    {
        UnreadNotificationManager._2D_wrap1(UnreadNotificationManager.this);
        UnreadNotificationManager._2D_get2(UnreadNotificationManager.this).execute(UnreadNotificationManager._2D_get3(UnreadNotificationManager.this));
    }

    ()
    {
        this$0 = UnreadNotificationManager.this;
        super();
    }
}
