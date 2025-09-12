package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.DBObject;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.inventory.SLInventory;
import com.lumiyaviewer.lumiya.slproto.messages.FetchInventoryDescendents;
import com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

class SLInventoryUDPFetchRequest extends SLInventoryFetchRequest {
    private final Set<UUID> existingChildren = new HashSet();
    private int receivedCount = 0;

    SLInventoryUDPFetchRequest(SLInventory sLInventory, UUID uuid) throws SLInventory.NoInventoryItemException {
        super(sLInventory, uuid);
    }

    /* access modifiers changed from: package-private */
    public boolean HandleInventoryDescendents(InventoryDescendents inventoryDescendents) {
        Debug.Log("Inventory: UDP fetch: exp count " + inventoryDescendents.AgentData_Field.Descendents + ", recv count " + this.receivedCount + ", " + " with this: " + (this.receivedCount + inventoryDescendents.FolderData_Fields.size() + inventoryDescendents.ItemData_Fields.size()));
        int i = inventoryDescendents.AgentData_Field.Descendents;
        int i2 = 0;
        this.db.beginTransaction();
        if (this.folderEntry.version != inventoryDescendents.AgentData_Field.Version) {
            this.folderEntry.version = inventoryDescendents.AgentData_Field.Version;
            this.db.saveEntry(this.folderEntry);
        }
        for (InventoryDescendents.FolderData folderData : inventoryDescendents.FolderData_Fields) {
            if (folderData.ParentID.equals(this.folderEntry.uuid) && !(folderData.FolderID.getLeastSignificantBits() == 0 && folderData.FolderID.getMostSignificantBits() == 0)) {
                try {
                    SLInventoryEntry sLInventoryEntry = new SLInventoryEntry();
                    sLInventoryEntry.uuid = folderData.FolderID;
                    sLInventoryEntry.parent_id = this.folderEntry.getId();
                    sLInventoryEntry.name = SLMessage.stringFromVariableOEM(folderData.Name);
                    sLInventoryEntry.typeDefault = folderData.Type;
                    sLInventoryEntry.parentUUID = folderData.ParentID;
                    sLInventoryEntry.agentUUID = inventoryDescendents.AgentData_Field.AgentID;
                    sLInventoryEntry.isFolder = true;
                    sLInventoryEntry.updateOrInsert(this.db.getDatabase());
                    i2++;
                    if (i2 > 16) {
                        this.db.yieldIfContendedSafely();
                        i2 = 0;
                    }
                    this.existingChildren.add(sLInventoryEntry.uuid);
                } catch (DBObject.DatabaseBindingException e) {
                    Debug.Warning(e);
                }
            }
            try {
                this.receivedCount++;
            } catch (DBObject.DatabaseBindingException e2) {
                Debug.Warning(e2);
                this.db.endTransaction();
            } catch (Throwable th) {
                this.db.endTransaction();
                throw th;
            }
        }
        for (InventoryDescendents.ItemData itemData : inventoryDescendents.ItemData_Fields) {
            if (!(itemData.ItemID.getLeastSignificantBits() == 0 && itemData.ItemID.getMostSignificantBits() == 0)) {
                try {
                    SLInventoryEntry sLInventoryEntry2 = new SLInventoryEntry();
                    sLInventoryEntry2.uuid = itemData.ItemID;
                    sLInventoryEntry2.name = SLMessage.stringFromVariableOEM(itemData.Name);
                    sLInventoryEntry2.description = SLMessage.stringFromVariableUTF(itemData.Description);
                    if (itemData.FolderID.equals(this.folderEntry.uuid)) {
                        sLInventoryEntry2.parent_id = this.folderEntry.getId();
                        sLInventoryEntry2.parentUUID = itemData.FolderID;
                    } else {
                        SLInventoryEntry findEntry = this.db.findEntry(itemData.FolderID);
                        if (findEntry != null) {
                            sLInventoryEntry2.parent_id = findEntry.getId();
                        } else {
                            sLInventoryEntry2.parent_id = 0;
                        }
                        sLInventoryEntry2.parentUUID = itemData.FolderID;
                    }
                    sLInventoryEntry2.agentUUID = inventoryDescendents.AgentData_Field.AgentID;
                    sLInventoryEntry2.isFolder = false;
                    sLInventoryEntry2.assetType = itemData.Type;
                    sLInventoryEntry2.assetUUID = itemData.AssetID;
                    sLInventoryEntry2.invType = itemData.InvType;
                    sLInventoryEntry2.flags = itemData.Flags;
                    sLInventoryEntry2.creationDate = itemData.CreationDate;
                    sLInventoryEntry2.creatorUUID = itemData.CreatorID;
                    sLInventoryEntry2.groupUUID = itemData.GroupID;
                    sLInventoryEntry2.lastOwnerUUID = new UUID(0, 0);
                    sLInventoryEntry2.ownerUUID = itemData.OwnerID;
                    sLInventoryEntry2.isGroupOwned = itemData.GroupOwned;
                    sLInventoryEntry2.baseMask = itemData.BaseMask;
                    sLInventoryEntry2.ownerMask = itemData.OwnerMask;
                    sLInventoryEntry2.groupMask = itemData.GroupMask;
                    sLInventoryEntry2.everyoneMask = itemData.EveryoneMask;
                    sLInventoryEntry2.nextOwnerMask = itemData.NextOwnerMask;
                    sLInventoryEntry2.salePrice = itemData.SalePrice;
                    sLInventoryEntry2.saleType = itemData.SaleType;
                    sLInventoryEntry2.updateOrInsert(this.db.getDatabase());
                    int i3 = i2 + 1;
                    if (i3 > 16) {
                        this.db.yieldIfContendedSafely();
                        i3 = 0;
                    }
                    if (sLInventoryEntry2.parent_id == this.folderEntry.getId()) {
                        this.existingChildren.add(sLInventoryEntry2.uuid);
                    }
                } catch (DBObject.DatabaseBindingException e3) {
                    Debug.Warning(e3);
                }
            }
            this.receivedCount++;
        }
        this.db.setTransactionSuccessful();
        this.db.endTransaction();
        if (this.receivedCount < i) {
            return false;
        }
        this.db.retainChildren(this.folderEntry.getId(), this.existingChildren);
        completeFetch(true, false);
        return true;
    }

    public void cancel() {
    }

    public void start() {
        Debug.Log("Inventory: UDP fetching folder " + this.folderUUID.toString());
        FetchInventoryDescendents fetchInventoryDescendents = new FetchInventoryDescendents();
        fetchInventoryDescendents.AgentData_Field.AgentID = this.inventory.getCircuitInfo().agentID;
        fetchInventoryDescendents.AgentData_Field.SessionID = this.inventory.getCircuitInfo().sessionID;
        fetchInventoryDescendents.InventoryData_Field.FolderID = this.folderUUID;
        fetchInventoryDescendents.InventoryData_Field.OwnerID = this.inventory.getCircuitInfo().agentID;
        fetchInventoryDescendents.InventoryData_Field.FetchFolders = true;
        fetchInventoryDescendents.InventoryData_Field.FetchItems = true;
        fetchInventoryDescendents.isReliable = true;
        this.inventory.SendMessage(fetchInventoryDescendents);
    }
}
