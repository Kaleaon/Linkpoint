package com.lumiyaviewer.lumiya.slproto.inventory

import com.lumiyaviewer.lumiya.orm.InventoryDB
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory
import java.util.UUID
import javax.annotation.Nonnull

abstract class SLInventoryFetchRequest {
    protected InventoryDB db
    @Nonnull
    protected SLInventoryEntry folderEntry
    protected Long folderId
    protected UUID folderUUID
    protected SLInventory inventory

    SLInventoryFetchRequest(SLInventory sLInventory, UUID uuid) throws SLInventory.NoInventoryItemException {
        this.inventory = sLInventory
        this.db = sLInventory.getDatabase()
        this.folderUUID = uuid
        SLInventoryEntry findEntry = this.db.findEntry(uuid)
        if (findEntry == null) {
            throw SLInventory.NoInventoryItemException(uuid)
        }
        this.folderEntry = findEntry
        this.folderId = findEntry.getId()
    }

    abstract Unit cancel()

    /* access modifiers changed from: protected */
    Unit completeFetch(Boolean z, Boolean z2) {
        this.inventory.onFetchComplete(this, this.folderUUID, this.folderId, z, z2)
    }

    abstract Unit start()
}
