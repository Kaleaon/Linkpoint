// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.users.manager;

import com.google.common.collect.ImmutableList;
import com.lumiyaviewer.lumiya.Debug;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.users.manager:
//            ChatterDisplayData, OnListUpdated

class SortedChatterList
{

    private final SortedSet chatters;
    private final Object lock = new Object();
    private final OnListUpdated onListUpdatedListener;
    private ImmutableList sortedList;

    SortedChatterList(OnListUpdated onlistupdated, Comparator comparator)
    {
        sortedList = null;
        chatters = new TreeSet(comparator);
        onListUpdatedListener = onlistupdated;
    }

    void addChatter(ChatterDisplayData chatterdisplaydata)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag;
        flag = chatters.add(chatterdisplaydata);
        Debug.Printf("FriendList: added chatter data %s, needUpdate %s, count %d", new Object[] {
            chatterdisplaydata.displayName, Boolean.toString(flag), Integer.valueOf(chatters.size())
        });
        if (sortedList != null)
        {
            Debug.Printf("FriendList: dropping instance because of addChatter", new Object[0]);
        }
        sortedList = null;
        obj;
        JVM INSTR monitorexit ;
        if (flag && onListUpdatedListener != null)
        {
            onListUpdatedListener.onListUpdated();
        }
        return;
        chatterdisplaydata;
        throw chatterdisplaydata;
    }

    public ImmutableList getChatterList()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        ImmutableList immutablelist;
        if (sortedList == null)
        {
            Debug.Printf("FriendList: creating new list instance", new Object[0]);
            sortedList = ImmutableList.copyOf(chatters);
        }
        immutablelist = sortedList;
        obj;
        JVM INSTR monitorexit ;
        return immutablelist;
        Exception exception;
        exception;
        throw exception;
    }

    void removeChatter(ChatterDisplayData chatterdisplaydata)
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        boolean flag;
        flag = chatters.remove(chatterdisplaydata);
        if (sortedList != null)
        {
            Debug.Printf("FriendList: dropping instance because of removeChatter", new Object[0]);
        }
        sortedList = null;
        obj;
        JVM INSTR monitorexit ;
        if (flag && onListUpdatedListener != null)
        {
            onListUpdatedListener.onListUpdated();
        }
        return;
        chatterdisplaydata;
        throw chatterdisplaydata;
    }

    void replaceChatter(ChatterDisplayData chatterdisplaydata, ChatterDisplayData chatterdisplaydata1)
    {
        boolean flag = false;
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (!chatters.remove(chatterdisplaydata))
        {
            break MISSING_BLOCK_LABEL_37;
        }
        flag = true;
        chatters.add(chatterdisplaydata1);
        if (sortedList != null)
        {
            Debug.Printf("FriendList: dropping instance because of replaceChatter", new Object[0]);
        }
        sortedList = null;
        obj;
        JVM INSTR monitorexit ;
        if (flag && onListUpdatedListener != null)
        {
            onListUpdatedListener.onListUpdated();
        }
        return;
        chatterdisplaydata;
        throw chatterdisplaydata;
    }
}
