// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.Executor;

public class SubscribableList extends AbstractList
{

    private final List backingList = new ArrayList();
    private final Object lock = new Object();
    private final Map targets = new WeakHashMap();

    static void _2D_com_lumiyaviewer_lumiya_slproto_users_manager_SubscribableList_2D_mthref_2D_0(List list)
    {
        list.clear();
    }

    public SubscribableList()
    {
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SubscribableList_2280(List list, int i, Object obj)
    {
        list.set(i, obj);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SubscribableList_2957(List list, int i, Object obj)
    {
        list.add(i, obj);
    }

    static void lambda$_2D_com_lumiyaviewer_lumiya_slproto_users_manager_SubscribableList_3622(List list, int i)
    {
        list.remove(i);
    }

    public void add(int i, Object obj)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        ImmutableList immutablelist;
        backingList.add(i, obj);
        immutablelist = ImmutableList.copyOf(targets.entrySet());
        obj1;
        JVM INSTR monitorexit ;
        for (obj1 = immutablelist.iterator(); ((Iterator) (obj1)).hasNext();)
        {
            Object obj2 = (java.util.Map.Entry)((Iterator) (obj1)).next();
            List list = (List)((java.util.Map.Entry) (obj2)).getKey();
            obj2 = (Executor)((Optional)((java.util.Map.Entry) (obj2)).getValue()).orNull();
            if (obj2 != null)
            {
                ((Executor) (obj2)).execute(new _2D_.Lambda.Gzuh54B3D66vdv_2D_4A7qntNjZJmM._cls2(i, list, obj));
            } else
            {
                list.add(i, obj);
            }
        }

        break MISSING_BLOCK_LABEL_135;
        obj;
        throw obj;
    }

    public List addSubscription(List list, Optional optional)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        targets.put(list, optional);
        list = ImmutableList.copyOf(backingList);
        obj;
        JVM INSTR monitorexit ;
        return list;
        list;
        throw list;
    }

    public void clear()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        ImmutableList immutablelist;
        backingList.clear();
        immutablelist = ImmutableList.copyOf(targets.entrySet());
        obj;
        JVM INSTR monitorexit ;
        for (obj = immutablelist.iterator(); ((Iterator) (obj)).hasNext();)
        {
            Object obj2 = (java.util.Map.Entry)((Iterator) (obj)).next();
            Object obj1 = (List)((java.util.Map.Entry) (obj2)).getKey();
            obj2 = (Executor)((Optional)((java.util.Map.Entry) (obj2)).getValue()).orNull();
            if (obj2 != null)
            {
                obj1.getClass();
                ((Executor) (obj2)).execute(new _2D_.Lambda.Gzuh54B3D66vdv_2D_4A7qntNjZJmM(obj1));
            } else
            {
                ((List) (obj1)).clear();
            }
        }

        break MISSING_BLOCK_LABEL_123;
        obj1;
        throw obj1;
    }

    public Object get(int i)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        Object obj1 = backingList.get(i);
        obj;
        JVM INSTR monitorexit ;
        return obj1;
        Exception exception;
        exception;
        throw exception;
    }

    public Object remove(int i)
    {
        Object obj1 = lock;
        obj1;
        JVM INSTR monitorenter ;
        Object obj;
        ImmutableList immutablelist;
        obj = backingList.remove(i);
        immutablelist = ImmutableList.copyOf(targets.entrySet());
        obj1;
        JVM INSTR monitorexit ;
        for (obj1 = immutablelist.iterator(); ((Iterator) (obj1)).hasNext();)
        {
            Object obj2 = (java.util.Map.Entry)((Iterator) (obj1)).next();
            List list = (List)((java.util.Map.Entry) (obj2)).getKey();
            obj2 = (Executor)((Optional)((java.util.Map.Entry) (obj2)).getValue()).orNull();
            if (obj2 != null)
            {
                ((Executor) (obj2)).execute(new _2D_.Lambda.Gzuh54B3D66vdv_2D_4A7qntNjZJmM._cls1(i, list));
            } else
            {
                list.remove(i);
            }
        }

        break MISSING_BLOCK_LABEL_134;
        obj;
        throw obj;
        return obj;
    }

    public void removeSubscription(List list)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        targets.remove(list);
        obj;
        JVM INSTR monitorexit ;
        return;
        list;
        throw list;
    }

    public Object set(int i, Object obj)
    {
        Object obj2 = lock;
        obj2;
        JVM INSTR monitorenter ;
        Object obj1;
        ImmutableList immutablelist;
        obj1 = backingList.set(i, obj);
        immutablelist = ImmutableList.copyOf(targets.entrySet());
        obj2;
        JVM INSTR monitorexit ;
        for (obj2 = immutablelist.iterator(); ((Iterator) (obj2)).hasNext();)
        {
            Object obj3 = (java.util.Map.Entry)((Iterator) (obj2)).next();
            List list = (List)((java.util.Map.Entry) (obj3)).getKey();
            obj3 = (Executor)((Optional)((java.util.Map.Entry) (obj3)).getValue()).orNull();
            if (obj3 != null)
            {
                ((Executor) (obj3)).execute(new _2D_.Lambda.Gzuh54B3D66vdv_2D_4A7qntNjZJmM._cls3(i, list, obj));
            } else
            {
                list.set(i, obj);
            }
        }

        break MISSING_BLOCK_LABEL_144;
        obj;
        throw obj;
        return obj1;
    }

    public int size()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        int i = backingList.size();
        obj;
        JVM INSTR monitorexit ;
        return i;
        Exception exception;
        exception;
        throw exception;
    }
}
