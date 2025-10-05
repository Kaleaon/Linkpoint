package com.linkpoint.slproto.inventory

import com.linkpoint.orm.InventoryDB
import java.util.UUID

abstract class SLInventoryFetchRequest @Throws(SLInventory.NoInventoryItemException::class) constructor(
    protected val inventory: SLInventory,
    protected val folderUUID: UUID
) {
    protected val db: InventoryDB = inventory.getDatabase()
    protected val folderEntry: SLInventoryEntry
    protected val folderId: Long

    init {
        val findEntry = db.findEntry(folderUUID)
            ?: throw SLInventory.NoInventoryItemException(folderUUID)
        folderEntry = findEntry
        folderId = findEntry.id
    }

    abstract fun cancel()

    abstract fun start()

    protected fun completeFetch(success: Boolean, recursive: Boolean) {
        inventory.onFetchComplete(this, folderUUID, folderId, success, recursive)
    }
}