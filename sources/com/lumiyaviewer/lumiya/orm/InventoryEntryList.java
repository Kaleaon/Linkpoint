// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.Cursor;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.AbstractList;
import java.util.concurrent.ExecutionException;

public class InventoryEntryList extends AbstractList
{

    private final Cursor cursor;
    private final LoadingCache entryCache;
    private final SLInventoryEntry folder;
    private final Object lock;
    private final int size;
    private final String title;

    static Cursor _2D_get0(InventoryEntryList inventoryentrylist)
    {
        return inventoryentrylist.cursor;
    }

    static Object _2D_get1(InventoryEntryList inventoryentrylist)
    {
        return inventoryentrylist.lock;
    }

    public InventoryEntryList()
    {
        lock = new Object();
        entryCache = CacheBuilder.newBuilder().maximumSize(1000L).weakValues().build(new CacheLoader() {

            final InventoryEntryList this$0;

            public SLInventoryEntry load(Integer integer)
            {
                if (InventoryEntryList._2D_get0(InventoryEntryList.this) == null) goto _L2; else goto _L1
_L1:
                if (!(InventoryEntryList._2D_get0(InventoryEntryList.this).isClosed() ^ true))
                {
                    break MISSING_BLOCK_LABEL_107;
                }
                Object obj = InventoryEntryList._2D_get1(InventoryEntryList.this);
                obj;
                JVM INSTR monitorenter ;
                InventoryEntryList._2D_get0(InventoryEntryList.this).moveToPosition(integer.intValue());
                integer = new SLInventoryEntry(InventoryEntryList._2D_get0(InventoryEntryList.this));
_L4:
                obj;
                JVM INSTR monitorexit ;
_L3:
                obj = integer;
                if (integer == null)
                {
                    obj = new SLInventoryEntry();
                }
                return ((SLInventoryEntry) (obj));
_L2:
                integer = null;
                  goto _L3
                integer;
                Debug.Warning(integer);
                integer = null;
                  goto _L4
                integer;
                throw integer;
                integer = null;
                  goto _L3
            }

            public volatile Object load(Object obj)
                throws Exception
            {
                return load((Integer)obj);
            }

            
            {
                this$0 = InventoryEntryList.this;
                super();
            }
        });
        title = null;
        cursor = null;
        folder = null;
        size = 0;
    }

    InventoryEntryList(String s, SLInventoryEntry slinventoryentry, Cursor cursor1)
    {
        lock = new Object();
        entryCache = CacheBuilder.newBuilder().maximumSize(1000L).weakValues().build(new _cls1());
        title = s;
        folder = slinventoryentry;
        cursor = cursor1;
        int i;
        if (cursor1 != null)
        {
            i = cursor1.getCount();
        } else
        {
            i = 0;
        }
        size = i;
    }

    public void close()
    {
        Object obj = lock;
        obj;
        JVM INSTR monitorenter ;
        if (cursor != null && !cursor.isClosed())
        {
            cursor.close();
        }
        obj;
        JVM INSTR monitorexit ;
        return;
        Exception exception;
        exception;
        throw exception;
    }

    public SLInventoryEntry get(int i)
    {
        if (cursor != null && cursor.isClosed() ^ true)
        {
            SLInventoryEntry slinventoryentry;
            try
            {
                slinventoryentry = (SLInventoryEntry)entryCache.get(Integer.valueOf(i));
            }
            catch (ExecutionException executionexception)
            {
                Debug.Warning(executionexception);
                return null;
            }
            return slinventoryentry;
        }
        String s;
        if (cursor == null)
        {
            s = "null";
        } else
        {
            s = "closed";
        }
        Debug.Printf("InventoryEntryList: returning null for %d because cursor is %s", new Object[] {
            Integer.valueOf(i), s
        });
        return null;
    }

    public volatile Object get(int i)
    {
        return get(i);
    }

    public SLInventoryEntry getFolder()
    {
        return folder;
    }

    public String getTitle()
    {
        return title;
    }

    public int size()
    {
        return size;
    }
}
