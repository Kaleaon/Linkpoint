package com.lumiyaviewer.lumiya.orm;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Build;
import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.DBObject;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/* loaded from: classes.dex */
public class InventoryDB {
    public static final int MAX_UPDATES_PER_TRANSACTION = 16;
    private final SQLiteDatabase db;

    public InventoryDB(SQLiteDatabase sQLiteDatabase) {
        this.db = sQLiteDatabase;
    }

    public void beginTransaction() {
        if (Build.VERSION.SDK_INT >= 11) {
            this.db.beginTransactionNonExclusive();
        } else {
            this.db.beginTransaction();
        }
    }

    public void deleteEntry(@Nonnull SLInventoryEntry sLInventoryEntry) throws DBObject.DatabaseBindingException {
        sLInventoryEntry.delete(this.db);
    }

    public void endTransaction() {
        this.db.endTransaction();
    }

    @Nullable
    public SLInventoryEntry findEntry(UUID uuid) {
        return SLInventoryEntry.find(this.db, uuid);
    }

    @Nonnull
    public SLInventoryEntry findEntryOrCreate(UUID uuid) {
        SLInventoryEntry findEntry = findEntry(uuid);
        if (findEntry != null) {
            return findEntry;
        }
        SLInventoryEntry sLInventoryEntry = new SLInventoryEntry();
        sLInventoryEntry.uuid = uuid;
        return sLInventoryEntry;
    }

    @Nullable
    public SLInventoryEntry findSpecialFolder(long j, int i) {
        Cursor query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parent_id = ?", new String[]{Integer.toString(i), Long.toString(j)}, (String) null);
        SLInventoryEntry sLInventoryEntry = query.moveToNext() ? new SLInventoryEntry(query) : null;
        query.close();
        return sLInventoryEntry;
    }

    @Nullable
    public SLInventoryEntry findSpecialFolder(UUID uuid, int i) {
        Cursor query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parentUUID_high = ? AND parentUUID_low = ?", new String[]{Integer.toString(i), Long.toString(uuid.getMostSignificantBits()), Long.toString(uuid.getLeastSignificantBits())}, (String) null);
        SLInventoryEntry sLInventoryEntry = query.moveToNext() ? new SLInventoryEntry(query) : null;
        query.close();
        return sLInventoryEntry;
    }

    public SQLiteDatabase getDatabase() {
        return this.db;
    }

    public long getSpecialFolderId(long j, int i) {
        SLInventoryEntry findSpecialFolder = findSpecialFolder(j, i);
        if (findSpecialFolder != null) {
            return findSpecialFolder.getId();
        }
        return 0L;
    }

    @Nullable
    public UUID getSpecialFolderUUID(long j, int i) {
        SLInventoryEntry findSpecialFolder = findSpecialFolder(j, i);
        if (findSpecialFolder != null) {
            return findSpecialFolder.uuid;
        }
        return null;
    }

    @Nonnull
    public SLInventoryEntry loadEntry(long j) throws DBObject.DatabaseBindingException {
        return new SLInventoryEntry(this.db, j);
    }

    @Nullable
    public SLInventoryEntry resolveLink(@Nullable SLInventoryEntry sLInventoryEntry) {
        return (sLInventoryEntry == null || !sLInventoryEntry.isLink()) ? sLInventoryEntry : findEntry(sLInventoryEntry.assetUUID);
    }

    public void retainChildren(long j, Set<UUID> set) {
        try {
            Cursor query = this.db.query(InventoryEntryDBObject.tableName, new String[]{"_id", "uuid_low", "uuid_high"}, "parent_id = ?", new String[]{Long.toString(j)}, null, null, null);
            Debug.Log("retainChildren: parentId = " + j + ", count = " + query.getCount());
            int i = 0;
            ArrayList arrayList = new ArrayList();
            while (query.moveToNext()) {
                if (!set.contains(new UUID(query.getLong(2), query.getLong(1)))) {
                    arrayList.add(Long.valueOf(query.getLong(0)));
                }
            }
            query.close();
            int i2 = 0;
            while (true) {
                int i3 = i2;
                if (i3 >= 2) {
                    break;
                }
                try {
                    beginTransaction();
                    try {
                        Iterator it = arrayList.iterator();
                        int i4 = 0;
                        while (it.hasNext()) {
                            this.db.delete(InventoryEntryDBObject.tableName, "_id = ?", new String[]{Long.toString(((Long) it.next()).longValue())});
                            i++;
                            int i5 = i4 + 1;
                            if (i5 >= 16) {
                                i5 = 0;
                                this.db.yieldIfContendedSafely();
                            }
                            i4 = i5;
                        }
                        setTransactionSuccessful();
                        break;
                    } finally {
                        endTransaction();
                    }
                } catch (SQLiteException e) {
                    Debug.Warning(e);
                    i2 = i3 + 1;
                }
            }
            Debug.Log("retainChildren: parentId = " + j + ", deleteCount = " + i);
        } catch (SQLiteException e2) {
            Debug.Warning(e2);
        }
    }

    public void saveEntry(@Nonnull SLInventoryEntry sLInventoryEntry) throws DBObject.DatabaseBindingException {
        sLInventoryEntry.save(this.db);
    }

    public void setTransactionSuccessful() {
        this.db.setTransactionSuccessful();
    }

    public void yieldIfContendedSafely() {
        this.db.yieldIfContendedSafely();
    }
}
