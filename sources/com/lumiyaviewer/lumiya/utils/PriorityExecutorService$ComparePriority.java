// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.util.Comparator;

// Referenced classes of package com.lumiyaviewer.lumiya.utils:
//            PriorityExecutorService, HasPriority

private static class 
    implements Comparator
{

    public volatile int compare(Object obj, Object obj1)
    {
        return compare((Runnable)obj, (Runnable)obj1);
    }

    public int compare(Runnable runnable, Runnable runnable1)
    {
        int j = 0;
        int i;
        if (runnable instanceof HasPriority)
        {
            i = ((HasPriority)runnable).getPriority();
        } else
        {
            i = 0;
        }
        if (runnable1 instanceof HasPriority)
        {
            j = ((HasPriority)runnable1).getPriority();
        }
        return i - j;
    }

    private ()
    {
    }
}
