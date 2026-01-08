package com.lumiyaviewer.lumiya.orm

import android.database.sqlite.SQLiteDatabase
import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventoryEntry
import java.util.ArrayList
import java.util.UUID

class InventoryDB(private val db: SQLiteDatabase) {
    
    fun beginTransaction() {
        db.beginTransactionNonExclusive()
    }
    
    fun endTransaction() {
        db.endTransaction()
    }
    
    fun setTransactionSuccessful() {
        db.setTransactionSuccessful()
    }
    
    fun yieldIfContendedSafely() {
        db.yieldIfContendedSafely()
    }
    
    fun getDatabase(): SQLiteDatabase = db
    
    fun findEntry(uuid: UUID): SLInventoryEntry? {
        return SLInventoryEntry.find(db, uuid)
    }
    
    fun findEntryOrCreate(uuid: UUID): SLInventoryEntry {
        val entry = findEntry(uuid)
        if (entry != null) return entry
        
        val newEntry = SLInventoryEntry()
        newEntry.uuid = uuid
        return newEntry
    }
    
    fun saveEntry(entry: SLInventoryEntry) {
        entry.save(db)
    }
    
    fun deleteEntry(entry: SLInventoryEntry) {
        entry.delete(db)
    }
    
    fun loadEntry(id: Long): SLInventoryEntry {
        return SLInventoryEntry(db, id)
    }
    
    fun findSpecialFolder(parentId: Long, typeDefault: Int): SLInventoryEntry? {
        val cursor = SLInventoryEntry.query(
            db,
            "isFolder AND typeDefault = ? AND parent_id = ?",
            arrayOf(typeDefault.toString(), parentId.toString()),
            null
        )
        return cursor.use {
            if (it.moveToNext()) SLInventoryEntry(it) else null
        }
    }
    
    fun findSpecialFolder(parentUUID: UUID, typeDefault: Int): SLInventoryEntry? {
        val cursor = SLInventoryEntry.query(
            db,
            "isFolder AND typeDefault = ? AND parentUUID_high = ? AND parentUUID_low = ?",
            arrayOf(
                typeDefault.toString(),
                parentUUID.mostSignificantBits.toString(),
                parentUUID.leastSignificantBits.toString()
            ),
            null
        )
        return cursor.use {
            if (it.moveToNext()) SLInventoryEntry(it) else null
        }
    }
    
    fun getSpecialFolderId(parentId: Long, typeDefault: Int): Long {
        return findSpecialFolder(parentId, typeDefault)?.getId() ?: 0L
    }
    
    fun getSpecialFolderUUID(parentId: Long, typeDefault: Int): UUID? {
        return findSpecialFolder(parentId, typeDefault)?.uuid
    }
    
    fun resolveLink(entry: SLInventoryEntry?): SLInventoryEntry? {
        if (entry == null || !entry.isLink()) return entry
        return findEntry(entry.assetUUID)
    }
    
    fun retainChildren(parentId: Long, childrenUUIDs: Set<UUID>) {
        try {
            val cursor = db.query(
                "Entries",
                arrayOf("_id", "uuid_low", "uuid_high"),
                "parent_id = ?",
                arrayOf(parentId.toString()),
                null, null, null
            )
            
            val toDelete = ArrayList<Long>()
            
            cursor.use {
                Debug.Log("retainChildren: parentId = $parentId, count = ${it.count}")
                while (it.moveToNext()) {
                    val uuid = UUID(it.getLong(2), it.getLong(1))
                    if (!childrenUUIDs.contains(uuid)) {
                        toDelete.add(it.getLong(0))
                    }
                }
            }
            
            if (toDelete.isEmpty()) return
            
            beginTransaction()
            try {
                var count = 0
                for (id in toDelete) {
                    db.delete("Entries", "_id = ?", arrayOf(id.toString()))
                    count++
                    if (count % 16 == 0) {
                        yieldIfContendedSafely()
                    }
                }
                setTransactionSuccessful()
            } finally {
                endTransaction()
            }
            
            Debug.Log("retainChildren: parentId = $parentId, deleteCount = ${toDelete.size}")
            
        } catch (e: Exception) {
            Debug.Warning(e)
        }
    }
}
