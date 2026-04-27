// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.handler;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.handler:
//            SLMessageRouter

private static class <init> extends LinkedList
{

    public void deleteAll(Object obj)
    {
        LinkedList linkedlist = new LinkedList();
        Iterator iterator = iterator();
        do
        {
            if (!iterator.hasNext())
            {
                break;
            }
            <init> <init>1 = (<init>)iterator.next();
            Object obj1 = _2D_get0(<init>1).get();
            if (obj1 == null || obj1 == obj)
            {
                linkedlist.add(<init>1);
            }
        } while (true);
        removeAll(linkedlist);
    }

    public void invokeAll(Object obj)
    {
        for (Iterator iterator = iterator(); iterator.hasNext(); ((removeAll)iterator.next()).invoke(obj)) { }
    }

    private I()
    {
    }

    I(I i)
    {
        this();
    }
}
