package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import java.util.UUID;
import javax.annotation.Nonnull;

public abstract class SLInventoryFetchRequest {
    protected final InventoryDB db;
    @Nonnull
    protected final SLInventoryEntry folderEntry;
    protected final long folderId;
    protected final UUID folderUUID;
    protected final SLInventory inventory;

    SLInventoryFetchRequest(SLInventory sLInventory, UUID uuid) throws SLInventory.NoInventoryItemException {
        this.inventory = sLInventory;
        this.db = sLInventory.getDatabase();
        this.folderUUID = uuid;
        SLInventoryEntry findEntry = this.db.findEntry(uuid);
        if (findEntry == null) {
            throw new SLInventory.NoInventoryItemException(uuid);
        }
        this.folderEntry = findEntry;
        this.folderId = findEntry.getId();
    }

    public abstract void cancel();

    /* access modifiers changed from: protected */
    public void completeFetch(boolean z, boolean z2) {
        this.inventory.onFetchComplete(this, this.folderUUID, this.folderId, z, z2);
    }

    public abstract void start();
}
