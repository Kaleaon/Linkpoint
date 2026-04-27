// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.Cursor;
import com.google.common.cache.CacheLoader;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;

// Referenced classes of package com.lumiyaviewer.lumiya.orm:
//            InventoryEntryList

class this._cls0 extends CacheLoader
{

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

    toryEntry()
    {
        this$0 = InventoryEntryList.this;
        super();
    }
}
