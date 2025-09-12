// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class InventoryDB
{

    public static final int MAX_UPDATES_PER_TRANSACTION = 16;
    private final SQLiteDatabase db;

    public InventoryDB(SQLiteDatabase sqlitedatabase)
    {
        db = sqlitedatabase;
    }

    public void beginTransaction()
    {
        if (android.os.Build.VERSION.SDK_INT >= 11)
        {
            db.beginTransactionNonExclusive();
            return;
        } else
        {
            db.beginTransaction();
            return;
        }
    }

    public void deleteEntry(SLInventoryEntry slinventoryentry)
        throws DBObject.DatabaseBindingException
    {
        slinventoryentry.delete(db);
    }

    public void endTransaction()
    {
        db.endTransaction();
    }

    public SLInventoryEntry findEntry(UUID uuid)
    {
        return SLInventoryEntry.find(db, uuid);
    }

    public SLInventoryEntry findEntryOrCreate(UUID uuid)
    {
        SLInventoryEntry slinventoryentry1 = findEntry(uuid);
        SLInventoryEntry slinventoryentry = slinventoryentry1;
        if (slinventoryentry1 == null)
        {
            slinventoryentry = new SLInventoryEntry();
            slinventoryentry.uuid = uuid;
        }
        return slinventoryentry;
    }

    public SLInventoryEntry findSpecialFolder(long l, int i)
    {
        SLInventoryEntry slinventoryentry = null;
        Cursor cursor = SLInventoryEntry.query(db, "isFolder AND typeDefault = ? AND parent_id = ?", new String[] {
            Integer.toString(i), Long.toString(l)
        }, null);
        if (cursor.moveToNext())
        {
            slinventoryentry = new SLInventoryEntry(cursor);
        }
        cursor.close();
        return slinventoryentry;
    }

    public SLInventoryEntry findSpecialFolder(UUID uuid, int i)
    {
        Object obj = null;
        Cursor cursor = SLInventoryEntry.query(db, "isFolder AND typeDefault = ? AND parentUUID_high = ? AND parentUUID_low = ?", new String[] {
            Integer.toString(i), Long.toString(uuid.getMostSignificantBits()), Long.toString(uuid.getLeastSignificantBits())
        }, null);
        uuid = obj;
        if (cursor.moveToNext())
        {
            uuid = new SLInventoryEntry(cursor);
        }
        cursor.close();
        return uuid;
    }

    public SQLiteDatabase getDatabase()
    {
        return db;
    }

    public long getSpecialFolderId(long l, int i)
    {
        SLInventoryEntry slinventoryentry = findSpecialFolder(l, i);
        if (slinventoryentry != null)
        {
            return slinventoryentry.getId();
        } else
        {
            return 0L;
        }
    }

    public UUID getSpecialFolderUUID(long l, int i)
    {
        UUID uuid = null;
        SLInventoryEntry slinventoryentry = findSpecialFolder(l, i);
        if (slinventoryentry != null)
        {
            uuid = slinventoryentry.uuid;
        }
        return uuid;
    }

    public SLInventoryEntry loadEntry(long l)
        throws DBObject.DatabaseBindingException
    {
        return new SLInventoryEntry(db, l);
    }

    public SLInventoryEntry resolveLink(SLInventoryEntry slinventoryentry)
    {
        if (slinventoryentry != null && slinventoryentry.isLink())
        {
            return findEntry(slinventoryentry.assetUUID);
        } else
        {
            return slinventoryentry;
        }
    }

    public void retainChildren(long l, Set set)
    {
        ArrayList arraylist;
        Object obj;
        int i;
        try
        {
            SQLiteDatabase sqlitedatabase = db;
            obj = Long.toString(l);
            obj = sqlitedatabase.query("Entries", new String[] {
                "_id", "uuid_low", "uuid_high"
            }, "parent_id = ?", new String[] {
                obj
            }, null, null, null);
            Debug.Log((new StringBuilder()).append("retainChildren: parentId = ").append(l).append(", count = ").append(((Cursor) (obj)).getCount()).toString());
        }
        // Misplaced declaration of an exception variable
        catch (Set set)
        {
            Debug.Warning(set);
            return;
        }
        i = 0;
        arraylist = new ArrayList();
        do
        {
            if (!((Cursor) (obj)).moveToNext())
            {
                break;
            }
            if (!set.contains(new UUID(((Cursor) (obj)).getLong(2), ((Cursor) (obj)).getLong(1))))
            {
                arraylist.add(Long.valueOf(((Cursor) (obj)).getLong(0)));
            }
        } while (true);
        ((Cursor) (obj)).close();
        int i1 = 0;
_L3:
        int j = i;
        if (i1 >= 2) goto _L2; else goto _L1
_L1:
        int k = i;
        beginTransaction();
        j = i;
        set = arraylist.iterator();
        int j1 = 0;
_L4:
        j = i;
        if (!set.hasNext())
        {
            break MISSING_BLOCK_LABEL_316;
        }
        j = i;
        obj = (Long)set.next();
        j = i;
        db.delete("Entries", "_id = ?", new String[] {
            Long.toString(((Long) (obj)).longValue())
        });
        k = i + 1;
        j = j1 + 1;
        i = j;
        if (j < 16)
        {
            break MISSING_BLOCK_LABEL_402;
        }
        i = 0;
        j = k;
        db.yieldIfContendedSafely();
        break MISSING_BLOCK_LABEL_402;
        j = i;
        setTransactionSuccessful();
        k = i;
        endTransaction();
        j = i;
_L2:
        Debug.Log((new StringBuilder()).append("retainChildren: parentId = ").append(l).append(", deleteCount = ").append(j).toString());
        return;
        set;
        k = j;
        endTransaction();
        k = j;
        try
        {
            throw set;
        }
        // Misplaced declaration of an exception variable
        catch (Set set) { }
        Debug.Warning(set);
        i1++;
        i = k;
          goto _L3
        j1 = i;
        i = k;
          goto _L4
    }

    public void saveEntry(SLInventoryEntry slinventoryentry)
        throws DBObject.DatabaseBindingException
    {
        slinventoryentry.save(db);
    }

    public void setTransactionSuccessful()
    {
        db.setTransactionSuccessful();
    }

    public void yieldIfContendedSafely()
    {
        db.yieldIfContendedSafely();
    }
}
