// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.executors;

import java.lang.ref.WeakReference;
import java.util.concurrent.Callable;

// Referenced classes of package com.lumiyaviewer.lumiya.res.executors:
//            WeakExecutor

private static class <init>
    implements Callable
{

    private final WeakReference callableRef;

    public Object call()
        throws Exception
    {
        Callable callable = (Callable)callableRef.get();
        if (callable != null)
        {
            return callable.call();
        } else
        {
            return null;
        }
    }

    private (Callable callable)
    {
        callableRef = new WeakReference(callable);
    }

    callableRef(Callable callable, callableRef callableref)
    {
        this(callable);
    }
}
