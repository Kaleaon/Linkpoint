package com.linkpoint.orm

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import com.linkpoint.orm.DBObject
import com.linkpoint.slproto.inventory.SLInventoryEntry
import java.util.UUID
import javax.annotation.Nonnull
import javax.annotation.Nullable

class InventoryDB {
    const val MAX_UPDATES_PER_TRANSACTION: Int = 16
    private val SQLiteDatabase db

    public InventoryDB(SQLiteDatabase sQLiteDatabase) {
        if (sQLiteDatabase == null) {
            throw IllegalArgumentException("SQLiteDatabase cannot be null")
        }
        this.db = sQLiteDatabase
    }

    public Unit beginTransaction() {
        if (Build.VERSION.SDK_INT >= 11) {
            this.db.beginTransactionNonExclusive()
        } else {
            this.db.beginTransaction()
        }
    }

    public Unit deleteEntry(SLInventoryEntry sLInventoryEntry) throws DBObject.DatabaseBindingException {
        if (sLInventoryEntry == null) {
            throw IllegalArgumentException("SLInventoryEntry cannot be null")
        }
        sLInventoryEntry.delete(this.db)
    }

    public Unit endTransaction() {
        this.db.endTransaction()
    }

    public SLInventoryEntry findEntry(UUID uuid) {
        return SLInventoryEntry.find(this.db, uuid)
    }

    public SLInventoryEntry findEntryOrCreate(UUID uuid) {
        if (uuid == null) {
            throw IllegalArgumentException("UUID cannot be null")
        }
        SLInventoryEntry findEntry = findEntry(uuid)
        if (findEntry != null) {
            return findEntry
        }
        SLInventoryEntry sLInventoryEntry = SLInventoryEntry()
        sLInventoryEntry.uuid = uuid
        return sLInventoryEntry
    }

    public SLInventoryEntry findSpecialFolder(Long j, Int i) {
        SLInventoryEntry sLInventoryEntry = null
        Cursor query = null
        try {
            query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parent_id = ?", String[]{Integer.toString(i), Long.toString(j)}, null)
            if (query.moveToNext()) {
                sLInventoryEntry = SLInventoryEntry(query)
            }
        } finally {
            if (query != null) {
                query.close()
            }
        }
        return sLInventoryEntry
    }

    public SLInventoryEntry findSpecialFolder(UUID uuid, Int i) {
        if (uuid == null) {
            return null
        }
        SLInventoryEntry sLInventoryEntry = null
        Cursor query = null
        try {
            query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parentUUID_high = ? AND parentUUID_low = ?", String[]{Integer.toString(i), Long.toString(uuid.getMostSignificantBits()), Long.toString(uuid.getLeastSignificantBits())}, null)
            if (query.moveToNext()) {
                sLInventoryEntry = SLInventoryEntry(query)
            }
        } finally {
            if (query != null) {
                query.close()
            }
        }
        return sLInventoryEntry
    }

    public SQLiteDatabase getDatabase() {
        return this.db
    }

    public Long getSpecialFolderId(Long j, Int i) {
        SLInventoryEntry findSpecialFolder = findSpecialFolder(j, i)
        if (findSpecialFolder != null) {
            return findSpecialFolder.getId()
        }
        return 0
    }

    public UUID getSpecialFolderUUID(Long j, Int i) {
        SLInventoryEntry findSpecialFolder = findSpecialFolder(j, i)
        if (findSpecialFolder != null) {
            return findSpecialFolder.uuid
        }
        return null
    }

    public SLInventoryEntry loadEntry(Long j) throws DBObject.DatabaseBindingException {
        return SLInventoryEntry(this.db, j)
    }

    public SLInventoryEntry resolveLink(SLInventoryEntry sLInventoryEntry) {
        return (sLInventoryEntry == null || !sLInventoryEntry.isLink()) ? sLInventoryEntry : findEntry(sLInventoryEntry.assetUUID)
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        com.lumiyaviewer.lumiya.Debug.Log("retainChildren: parentId = " + r16 + ", deleteCount = " + r3)
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public Unit retainChildren(Long r16, java.util.Set<java.util.UUID> r18) {
        /*
            r15 = this
            android.database.sqlite.SQLiteDatabase r2 = r15.db     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r3 = "Entries"
            r4 = 3
            java.lang.String[] r4 = java.lang.String[r4]     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r5 = "_id"
            r6 = 0
            r4[r6] = r5     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r5 = "uuid_low"
            r6 = 1
            r4[r6] = r5     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r5 = "uuid_high"
            r6 = 2
            r4[r6] = r5     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r5 = "parent_id = ?"
            r6 = 1
            java.lang.String[] r6 = java.lang.String[r6]     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r7 = java.lang.Long.toString(r16)     // Catch:{ SQLiteException -> 0x0086 }
            r8 = 0
            r6[r8] = r7     // Catch:{ SQLiteException -> 0x0086 }
            r7 = 0
            r8 = 0
            r9 = 0
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7, r8, r9)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.StringBuilder r3 = java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x0086 }
            r3.<init>()     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r4 = "retainChildren: parentId = "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLiteException -> 0x0086 }
            r0 = r16
            java.lang.StringBuilder r3 = r3.append(r0)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r4 = ", count = "
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLiteException -> 0x0086 }
            Int r4 = r2.getCount()     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r3 = r3.toString()     // Catch:{ SQLiteException -> 0x0086 }
            com.lumiyaviewer.lumiya.Debug.Log(r3)     // Catch:{ SQLiteException -> 0x0086 }
            r3 = 0
            java.util.ArrayList r6 = java.util.ArrayList     // Catch:{ SQLiteException -> 0x0086 }
            r6.<init>()     // Catch:{ SQLiteException -> 0x0086 }
        L_0x005c:
            Boolean r4 = r2.moveToNext()     // Catch:{ SQLiteException -> 0x0086 }
            if (r4 == 0) goto L_0x008b
            java.util.UUID r4 = java.util.UUID     // Catch:{ SQLiteException -> 0x0086 }
            r5 = 2
            Long r8 = r2.getLong(r5)     // Catch:{ SQLiteException -> 0x0086 }
            r5 = 1
            Long r10 = r2.getLong(r5)     // Catch:{ SQLiteException -> 0x0086 }
            r4.<init>(r8, r10)     // Catch:{ SQLiteException -> 0x0086 }
            r0 = r18
            Boolean r4 = r0.contains(r4)     // Catch:{ SQLiteException -> 0x0086 }
            if (r4 != 0) goto L_0x005c
            r4 = 0
            Long r4 = r2.getLong(r4)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.Long r4 = java.lang.Long.valueOf(r4)     // Catch:{ SQLiteException -> 0x0086 }
            r6.add(r4)     // Catch:{ SQLiteException -> 0x0086 }
            goto L_0x005c
        L_0x0086:
            r2 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r2)
        L_0x008a:
            return
        L_0x008b:
            r2.close()     // Catch:{ SQLiteException -> 0x0086 }
            r2 = 0
            r5 = r2
        L_0x0090:
            r2 = 2
            if (r5 >= r2) goto L_0x00d7
            r2 = 0
            r15.beginTransaction()     // Catch:{ SQLiteException -> 0x0101 }
            java.util.Iterator r7 = r6.iterator()     // Catch:{ all -> 0x00fc }
            r4 = r2
        L_0x009c:
            Boolean r2 = r7.hasNext()     // Catch:{ all -> 0x00fc }
            if (r2 == 0) goto L_0x00d1
            java.lang.Object r2 = r7.next()     // Catch:{ all -> 0x00fc }
            java.lang.Long r2 = (java.lang.Long) r2     // Catch:{ all -> 0x00fc }
            android.database.sqlite.SQLiteDatabase r8 = r15.db     // Catch:{ all -> 0x00fc }
            java.lang.String r9 = "Entries"
            java.lang.String r10 = "_id = ?"
            r11 = 1
            java.lang.String[] r11 = java.lang.String[r11]     // Catch:{ all -> 0x00fc }
            Long r12 = r2.longValue()     // Catch:{ all -> 0x00fc }
            java.lang.String r2 = java.lang.Long.toString(r12)     // Catch:{ all -> 0x00fc }
            r12 = 0
            r11[r12] = r2     // Catch:{ all -> 0x00fc }
            r8.delete(r9, r10, r11)     // Catch:{ all -> 0x00fc }
            Int r3 = r3 + 1
            Int r2 = r4 + 1
            r4 = 16
            if (r2 < r4) goto L_0x00cf
            r2 = 0
            android.database.sqlite.SQLiteDatabase r4 = r15.db     // Catch:{ all -> 0x00fc }
            r4.yieldIfContendedSafely()     // Catch:{ all -> 0x00fc }
        L_0x00cf:
            r4 = r2
            goto L_0x009c
        L_0x00d1:
            r15.setTransactionSuccessful()     // Catch:{ all -> 0x00fc }
            r15.endTransaction()     // Catch:{ SQLiteException -> 0x0101 }
        L_0x00d7:
            java.lang.StringBuilder r2 = java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x0086 }
            r2.<init>()     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r4 = "retainChildren: parentId = "
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ SQLiteException -> 0x0086 }
            r0 = r16
            java.lang.StringBuilder r2 = r2.append(r0)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r4 = ", deleteCount = "
            java.lang.StringBuilder r2 = r2.append(r4)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.StringBuilder r2 = r2.append(r3)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r2 = r2.toString()     // Catch:{ SQLiteException -> 0x0086 }
            com.lumiyaviewer.lumiya.Debug.Log(r2)     // Catch:{ SQLiteException -> 0x0086 }
            goto L_0x008a
        L_0x00fc:
            r2 = move-exception
            r15.endTransaction()     // Catch:{ SQLiteException -> 0x0101 }
            throw r2     // Catch:{ SQLiteException -> 0x0101 }
        L_0x0101:
            r2 = move-exception
            com.lumiyaviewer.lumiya.Debug.Warning(r2)     // Catch:{ SQLiteException -> 0x0086 }
            Int r2 = r5 + 1
            r5 = r2
            goto L_0x0090
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.orm.InventoryDB.retainChildren(Long, java.util.Set):Unit")
    }

    public Unit saveEntry(SLInventoryEntry sLInventoryEntry) throws DBObject.DatabaseBindingException {
        if (sLInventoryEntry == null) {
            throw IllegalArgumentException("SLInventoryEntry cannot be null")
        }
        sLInventoryEntry.save(this.db)
    }

    public Unit setTransactionSuccessful() {
        this.db.setTransactionSuccessful()
    }

    public Unit yieldIfContendedSafely() {
        this.db.yieldIfContendedSafely()
    }
}
