// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import java.lang.ref.WeakReference;

// Referenced classes of package com.lumiyaviewer.lumiya.res.executors:
//            WeakExecutor

private static class <init>
    implements Runnable
{

    private final WeakReference runnableRef;

    public void run()
    {
        Runnable runnable = (Runnable)runnableRef.get();
        if (runnable != null)
        {
            runnable.run();
        }
    }

    private (Runnable runnable)
    {
        runnableRef = new WeakReference(runnable);
    }

    runnableRef(Runnable runnable, runnableRef runnableref)
    {
        this(runnable);
    }
}
