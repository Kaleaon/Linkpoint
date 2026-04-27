// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.orm;

import java.util.Iterator;
import android.database.sqlite.SQLiteException;
import java.util.ArrayList;
import com.lumiyaviewer.lumiya.Debug;
import java.util.Set;
import android.database.Cursor;
import javax.annotation.Nullable;
import java.util.UUID;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry;
import android.os.Build$VERSION;
import android.database.sqlite.SQLiteDatabase;

public class InventoryDB
{
    public static final int MAX_UPDATES_PER_TRANSACTION = 16;
    private final SQLiteDatabase db;
    
    public InventoryDB(final SQLiteDatabase db) {
        this.db = db;
    }
    
    public void beginTransaction() {
        if (Build$VERSION.SDK_INT >= 11) {
            this.db.beginTransactionNonExclusive();
        }
        else {
            this.db.beginTransaction();
        }
    }
    
    public void deleteEntry(@Nonnull final SLInventoryEntry slInventoryEntry) throws DBObject.DatabaseBindingException {
        slInventoryEntry.delete(this.db);
    }
    
    public void endTransaction() {
        this.db.endTransaction();
    }
    
    @Nullable
    public SLInventoryEntry findEntry(final UUID uuid) {
        return SLInventoryEntry.find(this.db, uuid);
    }
    
    @Nonnull
    public SLInventoryEntry findEntryOrCreate(final UUID uuid) {
        SLInventoryEntry entry;
        if ((entry = this.findEntry(uuid)) == null) {
            entry = new SLInventoryEntry();
            entry.uuid = uuid;
        }
        return entry;
    }
    
    @Nullable
    public SLInventoryEntry findSpecialFolder(final long i, final int j) {
        SLInventoryEntry slInventoryEntry = null;
        final Cursor query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parent_id = ?", new String[] { Integer.toString(j), Long.toString(i) }, null);
        if (query.moveToNext()) {
            slInventoryEntry = new SLInventoryEntry(query);
        }
        query.close();
        return slInventoryEntry;
    }
    
    @Nullable
    public SLInventoryEntry findSpecialFolder(final UUID uuid, final int i) {
        final SLInventoryEntry slInventoryEntry = null;
        final Cursor query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parentUUID_high = ? AND parentUUID_low = ?", new String[] { Integer.toString(i), Long.toString(uuid.getMostSignificantBits()), Long.toString(uuid.getLeastSignificantBits()) }, null);
        SLInventoryEntry slInventoryEntry2 = slInventoryEntry;
        if (query.moveToNext()) {
            slInventoryEntry2 = new SLInventoryEntry(query);
        }
        query.close();
        return slInventoryEntry2;
    }
    
    public SQLiteDatabase getDatabase() {
        return this.db;
    }
    
    public long getSpecialFolderId(long id, final int n) {
        final SLInventoryEntry specialFolder = this.findSpecialFolder(id, n);
        if (specialFolder != null) {
            id = specialFolder.getId();
        }
        else {
            id = 0L;
        }
        return id;
    }
    
    @Nullable
    public UUID getSpecialFolderUUID(final long n, final int n2) {
        UUID uuid = null;
        final SLInventoryEntry specialFolder = this.findSpecialFolder(n, n2);
        if (specialFolder != null) {
            uuid = specialFolder.uuid;
        }
        return uuid;
    }
    
    @Nonnull
    public SLInventoryEntry loadEntry(final long n) throws DBObject.DatabaseBindingException {
        return new SLInventoryEntry(this.db, n);
    }
    
    @Nullable
    public SLInventoryEntry resolveLink(@Nullable final SLInventoryEntry slInventoryEntry) {
        if (slInventoryEntry != null && slInventoryEntry.isLink()) {
            return this.findEntry(slInventoryEntry.assetUUID);
        }
        return slInventoryEntry;
    }
    
    public void retainChildren(final long lng, final Set<UUID> set) {
        Cursor query = null;
        int n = 0;
        ArrayList list = null;
        Label_0184: {
            try {
                query = this.db.query("Entries", new String[] { "_id", "uuid_low", "uuid_high" }, "parent_id = ?", new String[] { Long.toString(lng) }, (String)null, (String)null, (String)null);
                Debug.Log("retainChildren: parentId = " + lng + ", count = " + query.getCount());
                n = 0;
                list = new ArrayList();
                while (query.moveToNext()) {
                    if (!set.contains(new UUID(query.getLong(2), query.getLong(1)))) {
                        list.add(query.getLong(0));
                    }
                }
                break Label_0184;
            }
            catch (final SQLiteException ex) {
                Debug.Warning((Throwable)ex);
            }
            return;
        }
        query.close();
        int n2 = 0;
        while (true) {
            int i = n;
            Label_0355: {
                if (n2 >= 2) {
                    break Label_0355;
                }
                int n3 = n;
                try {
                    this.beginTransaction();
                    i = n;
                    try {
                        final Iterator iterator = list.iterator();
                        int n4 = 0;
                        while (true) {
                            i = n;
                            if (!iterator.hasNext()) {
                                break;
                            }
                            i = n;
                            final Long n5 = (Long)iterator.next();
                            i = n;
                            this.db.delete("Entries", "_id = ?", new String[] { Long.toString(n5) });
                            n3 = n + 1;
                            i = n4 + 1;
                            int n6;
                            if ((n6 = i) >= 16) {
                                n6 = 0;
                                i = n3;
                                this.db.yieldIfContendedSafely();
                            }
                            n4 = n6;
                            n = n3;
                        }
                        i = n;
                        this.setTransactionSuccessful();
                        n3 = n;
                        this.endTransaction();
                        i = n;
                        Debug.Log("retainChildren: parentId = " + lng + ", deleteCount = " + i);
                    }
                    finally {
                        n3 = i;
                        this.endTransaction();
                        n3 = i;
                    }
                }
                catch (final SQLiteException ex2) {
                    Debug.Warning((Throwable)ex2);
                    ++n2;
                    n = n3;
                }
            }
        }
    }
    
    public void saveEntry(@Nonnull final SLInventoryEntry slInventoryEntry) throws DBObject.DatabaseBindingException {
        slInventoryEntry.save(this.db);
    }
    
    public void setTransactionSuccessful() {
        this.db.setTransactionSuccessful();
    }
    
    public void yieldIfContendedSafely() {
        this.db.yieldIfContendedSafely();
    }
}
