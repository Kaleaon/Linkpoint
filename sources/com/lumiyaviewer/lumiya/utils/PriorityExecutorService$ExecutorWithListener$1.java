// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.util.concurrent.ThreadFactory;

// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            PriorityExecutorService

static final class val.threadName
    implements ThreadFactory
{

    final String val$threadName;

    public Thread newThread(Runnable runnable)
    {
        return new Thread(runnable, val$threadName);
    }

    (String s)
    {
        val$threadName = s;
        super();
    }
}
