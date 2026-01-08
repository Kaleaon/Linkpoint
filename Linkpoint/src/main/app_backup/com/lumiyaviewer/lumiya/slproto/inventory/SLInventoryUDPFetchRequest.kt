package com.lumiyaviewer.lumiya.slproto.inventory

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.orm.DBObject
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.messages.FetchInventoryDescendents
import com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents
import java.util.HashSet
import java.util.UUID

class SLInventoryUDPFetchRequest(
    inventory: SLInventory,
    folderUUID: UUID
) : SLInventoryFetchRequest(inventory, folderUUID) {

    private val existingChildren = HashSet<UUID>()
    private var receivedCount = 0

    override fun start() {
        Debug.Log("Inventory: UDP fetching folder " + folderUUID.toString())
        val fetchInventoryDescendents = FetchInventoryDescendents()
        
        // Assuming getCircuitInfo() returns non-nullable or we handle it
        val circuit = inventory.getCircuitInfo()
        fetchInventoryDescendents.AgentData_Field.AgentID = circuit.agentID
        fetchInventoryDescendents.AgentData_Field.SessionID = circuit.sessionID
        
        fetchInventoryDescendents.InventoryData_Field.FolderID = folderUUID
        fetchInventoryDescendents.InventoryData_Field.OwnerID = circuit.agentID
        fetchInventoryDescendents.InventoryData_Field.FetchFolders = true
        fetchInventoryDescendents.InventoryData_Field.FetchItems = true
        fetchInventoryDescendents.isReliable = true
        
        inventory.SendMessage(fetchInventoryDescendents)
    }

    override fun cancel() {
        // No-op for UDP request usually, or we could set a flag
    }

    fun HandleInventoryDescendents(inventoryDescendents: InventoryDescendents): Boolean {
        val totalExpected = inventoryDescendents.AgentData_Field.Descendents
        Debug.Log("Inventory: UDP fetch: exp count $totalExpected, recv count $receivedCount")
        
        val db = this.db
        if (db == null) return false // Should not happen if properly initialized

        db.beginTransaction()
        try {
             // Update folder version
            if (folderEntry.version != inventoryDescendents.AgentData_Field.Version) {
                folderEntry.version = inventoryDescendents.AgentData_Field.Version
                db.saveEntry(folderEntry)
            }

            // Process Folders
            for (folderData in inventoryDescendents.FolderData_Fields) {
                if (folderData.ParentID == folderEntry.uuid && 
                    !(folderData.FolderID.mostSignificantBits == 0L && folderData.FolderID.leastSignificantBits == 0L)) {
                    
                    try {
                        val entry = SLInventoryEntry()
                        entry.uuid = folderData.FolderID
                        entry.parent_id = folderEntry.getId()
                        entry.name = SLMessage.stringFromVariableOEM(folderData.Name)
                        entry.typeDefault = folderData.Type
                        entry.parentUUID = folderData.ParentID
                        entry.agentUUID = inventoryDescendents.AgentData_Field.AgentID
                        entry.isFolder = true
                        
                        entry.updateOrInsert(db.getDatabase())
                        existingChildren.add(entry.uuid)
                    } catch (e: DBObject.DatabaseBindingException) {
                        Debug.Log("Error saving folder: " + e.message)
                    }
                }
                receivedCount++
            }

            // Process Items
            for (itemData in inventoryDescendents.ItemData_Fields) {
                 if (!(itemData.ItemID.mostSignificantBits == 0L && itemData.ItemID.leastSignificantBits == 0L)) {
                    try {
                        val entry = SLInventoryEntry()
                        entry.uuid = itemData.ItemID
                        entry.name = SLMessage.stringFromVariableOEM(itemData.Name)
                        entry.description = SLMessage.stringFromVariableUTF(itemData.Description)
                        
                        if (itemData.FolderID == folderEntry.uuid) {
                            entry.parent_id = folderEntry.getId()
                            entry.parentUUID = itemData.FolderID
                        } else {
                            val findEntry = db.findEntry(itemData.FolderID)
                            if (findEntry != null) {
                                entry.parent_id = findEntry.getId()
                            } else {
                                entry.parent_id = 0
                            }
                            entry.parentUUID = itemData.FolderID
                        }
                        
                        entry.agentUUID = inventoryDescendents.AgentData_Field.AgentID
                        entry.isFolder = false
                        entry.assetType = itemData.Type
                        entry.assetUUID = itemData.AssetID
                        entry.invType = itemData.InvType
                        entry.flags = itemData.Flags
                        entry.creationDate = itemData.CreationDate
                        entry.creatorUUID = itemData.CreatorID
                        entry.groupUUID = itemData.GroupID
                        entry.lastOwnerUUID = UUID(0, 0)
                        entry.ownerUUID = itemData.OwnerID
                        entry.isGroupOwned = itemData.GroupOwned
                        entry.baseMask = itemData.BaseMask
                        entry.ownerMask = itemData.OwnerMask
                        entry.groupMask = itemData.GroupMask
                        entry.everyoneMask = itemData.EveryoneMask
                        entry.nextOwnerMask = itemData.NextOwnerMask
                        entry.salePrice = itemData.SalePrice
                        entry.saleType = itemData.SaleType
                        
                        entry.updateOrInsert(db.getDatabase())
                        
                        if (entry.parent_id == folderEntry.getId()) {
                            existingChildren.add(entry.uuid)
                        }
                    } catch (e: DBObject.DatabaseBindingException) {
                        Debug.Log("Error saving item: " + e.message)
                    }
                 }
                 receivedCount++
            }

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Debug.Log("Error handling inventory descendents: " + e.message)
            throw e
        } finally {
            db.endTransaction()
        }

        if (receivedCount < totalExpected) {
            return false
        }

        db.retainChildren(folderEntry.getId(), existingChildren)
        completeFetch(true, false)
        return true
    }
}
