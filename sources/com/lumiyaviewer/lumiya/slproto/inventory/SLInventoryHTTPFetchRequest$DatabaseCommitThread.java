// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import android.database.sqlite.SQLiteStatement;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryHTTPFetchRequest, SLInventoryEntry

private final class <init> extends Thread
{

    private volatile boolean aborted;
    private final BlockingQueue commitEntryQueue;
    private final SLInventoryEntry stopEntry;
    final SLInventoryHTTPFetchRequest this$0;

    void addEntry(SLInventoryEntry slinventoryentry)
        throws InterruptedException
    {
        commitEntryQueue.put(slinventoryentry);
    }

    public void run()
    {
        SQLiteStatement sqlitestatement;
        Object obj1;
        HashSet hashset;
        boolean flag;
        int i;
        sqlitestatement = null;
        hashset = new HashSet();
        obj1 = null;
        flag = false;
        i = 0;
_L6:
        SQLiteStatement sqlitestatement1;
        SQLiteStatement sqlitestatement2;
        boolean flag1;
        boolean flag2;
        boolean flag3;
        flag3 = flag;
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        if (Thread.interrupted()) goto _L2; else goto _L1
_L1:
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        SLInventoryEntry slinventoryentry = (SLInventoryEntry)commitEntryQueue.poll();
        if (slinventoryentry != null)
        {
            break MISSING_BLOCK_LABEL_656;
        }
        flag3 = flag;
        if (!flag)
        {
            break MISSING_BLOCK_LABEL_139;
        }
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        db.setTransactionSuccessful();
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        db.endTransaction();
        flag3 = false;
        i = 0;
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag3;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag3;
        slinventoryentry = (SLInventoryEntry)commitEntryQueue.take();
_L12:
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag3;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag3;
        if (slinventoryentry != stopEntry) goto _L3; else goto _L2
_L2:
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag3;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag3;
        boolean flag4 = Thread.interrupted();
        if (!flag4)
        {
            flag = true;
        } else
        {
            flag = false;
        }
        if (flag3)
        {
            Object obj;
            InterruptedException interruptedexception;
            if (flag)
            {
                obj = "true";
            } else
            {
                obj = "false";
            }
            Debug.Printf("InvFetch: commit thread ending transaction (success: %s, count %d).", new Object[] {
                obj, Integer.valueOf(hashset.size())
            });
            db.setTransactionSuccessful();
            db.endTransaction();
        }
        if (obj1 != null)
        {
            ((SQLiteStatement) (obj1)).close();
        }
        if (sqlitestatement != null)
        {
            sqlitestatement.close();
        }
        if (flag && aborted ^ true)
        {
            Debug.Printf("InvFetch: commit thread successful, calling retainChildren.", new Object[0]);
            db.retainChildren(folderId, hashset);
        }
        return;
_L3:
        flag = flag3;
        if (flag3)
        {
            break MISSING_BLOCK_LABEL_371;
        }
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag3;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag3;
        db.beginTransaction();
        flag = true;
        i++;
        if (i < 16)
        {
            break MISSING_BLOCK_LABEL_411;
        }
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        db.yieldIfContendedSafely();
        i = 0;
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        hashset.add(slinventoryentry.uuid);
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        if (android.os.uid < 11) goto _L5; else goto _L4
_L4:
        obj = obj1;
        if (obj1 != null)
        {
            break MISSING_BLOCK_LABEL_494;
        }
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        obj = SLInventoryEntry.getInsertStatement(db.getDatabase());
        if (sqlitestatement != null)
        {
            break MISSING_BLOCK_LABEL_528;
        }
        sqlitestatement1 = ((SQLiteStatement) (obj));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj));
        flag2 = flag;
        obj1 = SLInventoryEntry.getUpdateStatement(db.getDatabase());
        sqlitestatement = ((SQLiteStatement) (obj1));
        slinventoryentry.updateOrInsert(sqlitestatement, ((SQLiteStatement) (obj)));
_L7:
        obj1 = obj;
          goto _L6
_L5:
        sqlitestatement1 = ((SQLiteStatement) (obj1));
        flag1 = flag;
        sqlitestatement2 = ((SQLiteStatement) (obj1));
        flag2 = flag;
        slinventoryentry.updateOrInsert(db.getDatabase());
        obj = obj1;
          goto _L7
        obj1;
        flag = flag1;
        obj = sqlitestatement1;
_L11:
        Debug.Warning(((Throwable) (obj1)));
        flag1 = false;
        obj1 = obj;
        flag3 = flag;
        flag = flag1;
        break MISSING_BLOCK_LABEL_217;
        interruptedexception;
        flag = flag2;
        obj = sqlitestatement2;
_L9:
        Debug.Warning(interruptedexception);
        flag1 = false;
        interruptedexception = ((InterruptedException) (obj));
        flag3 = flag;
        flag = flag1;
        break MISSING_BLOCK_LABEL_217;
        interruptedexception;
        if (true) goto _L9; else goto _L8
_L8:
        interruptedexception;
        if (true) goto _L11; else goto _L10
_L10:
        flag3 = flag;
          goto _L12
    }

    void stopAndWait(boolean flag)
        throws InterruptedException
    {
        if (!flag)
        {
            aborted = true;
        }
        commitEntryQueue.put(stopEntry);
        join();
    }

    private ()
    {
        this$0 = SLInventoryHTTPFetchRequest.this;
        super();
        commitEntryQueue = new LinkedBlockingQueue(100);
        stopEntry = new SLInventoryEntry();
        aborted = false;
    }

    aborted(aborted aborted1)
    {
        this();
    }
}
