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

    fun beginTransaction() {
        if (Build.VERSION.SDK_INT >= 11) {
            this.db.beginTransactionNonExclusive()
        } else {
            this.db.beginTransaction()
        }
    }

    fun deleteEntry(sLInventoryEntry: SLInventoryEntry) throws DBObject.DatabaseBindingException {
        if (sLInventoryEntry == null) {
            throw IllegalArgumentException("SLInventoryEntry cannot be null")
        }
        sLInventoryEntry.delete(this.db)
    }

    fun endTransaction() {
        this.db.endTransaction()
    }

     public fun findEntry(uuid: UUID): SLInventoryEntry {
        return SLInventoryEntry.find(this.db, uuid)
    }

     public fun findEntryOrCreate(uuid: UUID): SLInventoryEntry {
        if (uuid == null) {
            throw IllegalArgumentException("UUID cannot be null")
        }
        val findEntry: SLInventoryEntry = findEntry(uuid)
        if (findEntry != null) {
            return findEntry
        }
        val sLInventoryEntry: SLInventoryEntry = SLInventoryEntry()
        sLInventoryEntry.uuid = uuid
        return sLInventoryEntry
    }

     public fun findSpecialFolder(j: Long, i: Int): SLInventoryEntry {
        val sLInventoryEntry: SLInventoryEntry = null
        val query: Cursor = null
        try {
            query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parent_id = ?", Array<String>{Integer.toString(i), Long.toString(j)}, null)
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

     public fun findSpecialFolder(uuid: UUID, i: Int): SLInventoryEntry {
        if (uuid == null) {
            return null
        }
        val sLInventoryEntry: SLInventoryEntry = null
        val query: Cursor = null
        try {
            query = SLInventoryEntry.query(this.db, "isFolder AND typeDefault = ? AND parentUUID_high = ? AND parentUUID_low = ?", Array<String>{Integer.toString(i), Long.toString(uuid.getMostSignificantBits()), Long.toString(uuid.getLeastSignificantBits())}, null)
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

     public fun getDatabase(): SQLiteDatabase {
        return this.db
    }

     public fun getSpecialFolderId(j: Long, i: Int): Long {
        val findSpecialFolder: SLInventoryEntry = findSpecialFolder(j, i)
        if (findSpecialFolder != null) {
            return findSpecialFolder.getId()
        }
        return 0
    }

     public fun getSpecialFolderUUID(j: Long, i: Int): UUID {
        val findSpecialFolder: SLInventoryEntry = findSpecialFolder(j, i)
        if (findSpecialFolder != null) {
            return findSpecialFolder.uuid
        }
        return null
    }

     public fun loadEntry(j: Long) throws DBObject.DatabaseBindingException {
        return SLInventoryEntry(this.db, j)
    }

     public fun resolveLink(sLInventoryEntry: SLInventoryEntry): SLInventoryEntry {
        return (sLInventoryEntry == null || !sLInventoryEntry.isLink()) ? sLInventoryEntry : findEntry(sLInventoryEntry.assetUUID)
    }

    /* JADX WARNING: Code restructure failed: missing block: B:29:?, code lost:
        com.lumiyaviewer.lumiya.Debug.Log("retainChildren: parentId = " + r16 + ", deleteCount = " + r3)
     */
    /* JADX WARNING: Code restructure failed: missing block: B:45:?, code lost:
        return
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    fun retainChildren(r16: Long, java.util.Set<java.util.UUID> r18) {
        /*
            r15 = this
            android.database.sqlite.SQLiteDatabase r2 = r15.db     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r3 = "Entries"
            r4 = 3
            java.lang.Array<String> r4 = java.lang.String[r4]     // Catch:{ SQLiteException -> 0x0086 }
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
            java.lang.Array<String> r6 = java.lang.String[r6]     // Catch:{ SQLiteException -> 0x0086 }
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
            val r4: Int = r2.getCount()     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.StringBuilder r3 = r3.append(r4)     // Catch:{ SQLiteException -> 0x0086 }
            java.lang.String r3 = r3.toString()     // Catch:{ SQLiteException -> 0x0086 }
            com.lumiyaviewer.lumiya.Debug.Log(r3)     // Catch:{ SQLiteException -> 0x0086 }
            r3 = 0
            java.util.ArrayList r6 = java.util.ArrayList     // Catch:{ SQLiteException -> 0x0086 }
            r6.<init>()     // Catch:{ SQLiteException -> 0x0086 }
        L_0x005c:
            val r4: Boolean = r2.moveToNext()     // Catch:{ SQLiteException -> 0x0086 }
            if (r4 == 0) goto L_0x008b
            java.util.UUID r4 = java.util.UUID     // Catch:{ SQLiteException -> 0x0086 }
            r5 = 2
            val r8: Long = r2.getLong(r5)     // Catch:{ SQLiteException -> 0x0086 }
            r5 = 1
            val r10: Long = r2.getLong(r5)     // Catch:{ SQLiteException -> 0x0086 }
            r4.<init>(r8, r10)     // Catch:{ SQLiteException -> 0x0086 }
            r0 = r18
            val r4: Boolean = r0.contains(r4)     // Catch:{ SQLiteException -> 0x0086 }
            if (r4 != 0) goto L_0x005c
            r4 = 0
            val r4: Long = r2.getLong(r4)     // Catch:{ SQLiteException -> 0x0086 }
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
            val r2: Boolean = r7.hasNext()     // Catch:{ all -> 0x00fc }
            if (r2 == 0) goto L_0x00d1
            java.lang.Object r2 = r7.next()     // Catch:{ all -> 0x00fc }
            java.lang.Long r2 = (java.lang.Long) r2     // Catch:{ all -> 0x00fc }
            android.database.sqlite.SQLiteDatabase r8 = r15.db     // Catch:{ all -> 0x00fc }
            java.lang.String r9 = "Entries"
            java.lang.String r10 = "_id = ?"
            r11 = 1
            java.lang.Array<String> r11 = java.lang.String[r11]     // Catch:{ all -> 0x00fc }
            val r12: Long = r2.longValue()     // Catch:{ all -> 0x00fc }
            java.lang.String r2 = java.lang.Long.toString(r12)     // Catch:{ all -> 0x00fc }
            r12 = 0
            r11[r12] = r2     // Catch:{ all -> 0x00fc }
            r8.delete(r9, r10, r11)     // Catch:{ all -> 0x00fc }
            val r3: Int = r3 + 1
            val r2: Int = r4 + 1
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
            val r2: Int = r5 + 1
            r5 = r2
            goto L_0x0090
        */
        throw UnsupportedOperationException("Method not decompiled: com.lumiyaviewer.lumiya.orm.InventoryDB.retainChildren(Long, java.util.Set):Unit")
    }

    fun saveEntry(sLInventoryEntry: SLInventoryEntry) throws DBObject.DatabaseBindingException {
        if (sLInventoryEntry == null) {
            throw IllegalArgumentException("SLInventoryEntry cannot be null")
        }
        sLInventoryEntry.save(this.db)
    }

    fun setTransactionSuccessful() {
        this.db.setTransactionSuccessful()
    }

    fun yieldIfContendedSafely() {
        this.db.yieldIfContendedSafely()
    }
}
