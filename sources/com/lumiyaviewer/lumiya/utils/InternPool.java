// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.utils;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

public class InternPool
{

    private final WeakHashMap pool = new WeakHashMap();

    public InternPool()
    {
    }

    public final Object intern(Object obj)
    {
        this;
        JVM INSTR monitorenter ;
        Object obj1 = (WeakReference)pool.get(obj);
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_31;
        }
        obj1 = ((WeakReference) (obj1)).get();
        if (obj1 == null)
        {
            break MISSING_BLOCK_LABEL_31;
        }
        this;
        JVM INSTR monitorexit ;
        return obj1;
        pool.put(obj, new WeakReference(obj));
        this;
        JVM INSTR monitorexit ;
        return obj;
        obj;
        throw obj;
    }
}
