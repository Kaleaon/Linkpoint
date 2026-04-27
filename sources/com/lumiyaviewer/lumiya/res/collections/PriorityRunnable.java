// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.res.collections;

import com.lumiyaviewer.lumiya.utils.HasPriority;

public abstract class PriorityRunnable
    implements Runnable, HasPriority
{

    private final int priority;

    public PriorityRunnable(int i)
    {
        priority = i;
    }

    public int getPriority()
    {
        return priority;
    }
}
