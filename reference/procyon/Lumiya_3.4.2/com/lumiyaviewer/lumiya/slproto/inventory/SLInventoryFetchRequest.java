// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.inventory;

import java.util.UUID;
import javax.annotation.Nonnull;
import com.lumiyaviewer.lumiya.orm.InventoryDB;

public abstract class SLInventoryFetchRequest
{
    protected final InventoryDB db;
    @Nonnull
    protected final SLInventoryEntry folderEntry;
    protected final long folderId;
    protected final UUID folderUUID;
    protected final SLInventory inventory;
    
    SLInventoryFetchRequest(final SLInventory inventory, final UUID folderUUID) throws SLInventory.NoInventoryItemException {
        this.inventory = inventory;
        this.db = inventory.getDatabase();
        this.folderUUID = folderUUID;
        final SLInventoryEntry entry = this.db.findEntry(folderUUID);
        if (entry == null) {
            throw new SLInventory.NoInventoryItemException(folderUUID);
        }
        this.folderEntry = entry;
        this.folderId = entry.getId();
    }
    
    public abstract void cancel();
    
    protected void completeFetch(final boolean b, final boolean b2) {
        this.inventory.onFetchComplete(this, this.folderUUID, this.folderId, b, b2);
    }
    
    public abstract void start();
}
