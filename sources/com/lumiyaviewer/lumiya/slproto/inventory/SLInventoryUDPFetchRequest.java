// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.inventory;

import com.lumiyaviewer.lumiya.Debug;
import com.lumiyaviewer.lumiya.orm.InventoryDB;
import com.lumiyaviewer.lumiya.slproto.SLCircuitInfo;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.messages.FetchInventoryDescendents;
import com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.inventory:
//            SLInventoryFetchRequest, SLInventoryEntry, SLInventory

class SLInventoryUDPFetchRequest extends SLInventoryFetchRequest
{

    private final Set existingChildren = new HashSet();
    private int receivedCount;

    SLInventoryUDPFetchRequest(SLInventory slinventory, UUID uuid)
        throws SLInventory.NoInventoryItemException
    {
        super(slinventory, uuid);
        receivedCount = 0;
    }

    boolean HandleInventoryDescendents(InventoryDescendents inventorydescendents)
    {
        int i;
        int i1;
        Debug.Log((new StringBuilder()).append("Inventory: UDP fetch: exp count ").append(inventorydescendents.AgentData_Field.Descendents).append(", recv count ").append(receivedCount).append(", ").append(" with this: ").append(receivedCount + inventorydescendents.FolderData_Fields.size() + inventorydescendents.ItemData_Fields.size()).toString());
        i1 = inventorydescendents.AgentData_Field.Descendents;
        i = 0;
        db.beginTransaction();
        Iterator iterator;
        if (folderEntry.version != inventorydescendents.AgentData_Field.Version)
        {
            folderEntry.version = inventorydescendents.AgentData_Field.Version;
            db.saveEntry(folderEntry);
        }
        iterator = inventorydescendents.FolderData_Fields.iterator();
_L16:
        if (!iterator.hasNext()) goto _L2; else goto _L1
_L1:
        com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents.FolderData folderdata = (com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents.FolderData)iterator.next();
        int j = i;
        long l1;
        if (!folderdata.ParentID.equals(folderEntry.uuid))
        {
            break MISSING_BLOCK_LABEL_392;
        }
        if (folderdata.FolderID.getLeastSignificantBits() != 0L)
        {
            break MISSING_BLOCK_LABEL_216;
        }
        l1 = folderdata.FolderID.getMostSignificantBits();
        j = i;
        if (l1 == 0L)
        {
            break MISSING_BLOCK_LABEL_392;
        }
        j = i;
        SLInventoryEntry slinventoryentry = new SLInventoryEntry();
        j = i;
        slinventoryentry.uuid = folderdata.FolderID;
        j = i;
        slinventoryentry.parent_id = folderEntry.getId();
        j = i;
        slinventoryentry.name = SLMessage.stringFromVariableOEM(folderdata.Name);
        j = i;
        slinventoryentry.typeDefault = folderdata.Type;
        j = i;
        slinventoryentry.parentUUID = folderdata.ParentID;
        j = i;
        slinventoryentry.agentUUID = inventorydescendents.AgentData_Field.AgentID;
        j = i;
        slinventoryentry.isFolder = true;
        j = i;
        slinventoryentry.updateOrInsert(db.getDatabase());
        j = i + 1;
        i = j;
        if (j <= 16)
        {
            break MISSING_BLOCK_LABEL_369;
        }
        db.yieldIfContendedSafely();
        i = 0;
        j = i;
        existingChildren.add(slinventoryentry.uuid);
        j = i;
_L3:
        receivedCount = receivedCount + 1;
        i = j;
        continue; /* Loop/switch isn't completed */
        inventorydescendents;
        Debug.Warning(inventorydescendents);
        db.endTransaction();
        break MISSING_BLOCK_LABEL_421;
        databasebindingexception;
        Debug.Warning(databasebindingexception);
          goto _L3
        inventorydescendents;
        db.endTransaction();
        throw inventorydescendents;
_L2:
        iterator1 = inventorydescendents.ItemData_Fields.iterator();
_L10:
        if (!iterator1.hasNext())
        {
            break MISSING_BLOCK_LABEL_1079;
        }
        itemdata = (com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents.ItemData)iterator1.next();
        if (itemdata.ItemID.getLeastSignificantBits() != 0L) goto _L5; else goto _L4
_L4:
        l2 = itemdata.ItemID.getMostSignificantBits();
        l = i;
        if (l2 == 0L) goto _L6; else goto _L5
_L5:
        k = i;
        slinventoryentry1 = new SLInventoryEntry();
        k = i;
        slinventoryentry1.uuid = itemdata.ItemID;
        k = i;
        slinventoryentry1.name = SLMessage.stringFromVariableOEM(itemdata.Name);
        k = i;
        slinventoryentry1.description = SLMessage.stringFromVariableUTF(itemdata.Description);
        k = i;
        if (!itemdata.FolderID.equals(folderEntry.uuid)) goto _L8; else goto _L7
_L7:
        k = i;
        slinventoryentry1.parent_id = folderEntry.getId();
        k = i;
        slinventoryentry1.parentUUID = itemdata.FolderID;
_L13:
        k = i;
        slinventoryentry1.agentUUID = inventorydescendents.AgentData_Field.AgentID;
        k = i;
        slinventoryentry1.isFolder = false;
        k = i;
        slinventoryentry1.assetType = itemdata.Type;
        k = i;
        slinventoryentry1.assetUUID = itemdata.AssetID;
        k = i;
        slinventoryentry1.invType = itemdata.InvType;
        k = i;
        slinventoryentry1.flags = itemdata.Flags;
        k = i;
        slinventoryentry1.creationDate = itemdata.CreationDate;
        k = i;
        slinventoryentry1.creatorUUID = itemdata.CreatorID;
        k = i;
        slinventoryentry1.groupUUID = itemdata.GroupID;
        k = i;
        slinventoryentry1.lastOwnerUUID = new UUID(0L, 0L);
        k = i;
        slinventoryentry1.ownerUUID = itemdata.OwnerID;
        k = i;
        slinventoryentry1.isGroupOwned = itemdata.GroupOwned;
        k = i;
        slinventoryentry1.baseMask = itemdata.BaseMask;
        k = i;
        slinventoryentry1.ownerMask = itemdata.OwnerMask;
        k = i;
        slinventoryentry1.groupMask = itemdata.GroupMask;
        k = i;
        slinventoryentry1.everyoneMask = itemdata.EveryoneMask;
        k = i;
        slinventoryentry1.nextOwnerMask = itemdata.NextOwnerMask;
        k = i;
        slinventoryentry1.salePrice = itemdata.SalePrice;
        k = i;
        slinventoryentry1.saleType = itemdata.SaleType;
        k = i;
        slinventoryentry1.updateOrInsert(db.getDatabase());
        k = i + 1;
        i = k;
        if (k <= 16)
        {
            break MISSING_BLOCK_LABEL_938;
        }
        db.yieldIfContendedSafely();
        i = 0;
        l = i;
        k = i;
        if (slinventoryentry1.parent_id != folderEntry.getId()) goto _L6; else goto _L9
_L9:
        k = i;
        existingChildren.add(slinventoryentry1.uuid);
        l = i;
_L6:
        receivedCount = receivedCount + 1;
        i = l;
          goto _L10
_L8:
        k = i;
        slinventoryentry2 = db.findEntry(itemdata.FolderID);
        if (slinventoryentry2 == null) goto _L12; else goto _L11
_L11:
        k = i;
        slinventoryentry1.parent_id = slinventoryentry2.getId();
_L14:
        k = i;
        slinventoryentry1.parentUUID = itemdata.FolderID;
          goto _L13
        itemdata;
        Debug.Warning(itemdata);
        l = k;
          goto _L6
_L12:
        k = i;
        slinventoryentry1.parent_id = 0L;
          goto _L14
        db.setTransactionSuccessful();
        db.endTransaction();
        Iterator iterator1;
        com.lumiyaviewer.lumiya.orm.DBObject.DatabaseBindingException databasebindingexception;
        com.lumiyaviewer.lumiya.slproto.messages.InventoryDescendents.ItemData itemdata;
        SLInventoryEntry slinventoryentry1;
        SLInventoryEntry slinventoryentry2;
        int k;
        int l;
        long l2;
        if (receivedCount >= i1)
        {
            db.retainChildren(folderEntry.getId(), existingChildren);
            completeFetch(true, false);
            return true;
        }
        return false;
        if (true) goto _L16; else goto _L15
_L15:
    }

    public void cancel()
    {
    }

    public void start()
    {
        Debug.Log((new StringBuilder()).append("Inventory: UDP fetching folder ").append(folderUUID.toString()).toString());
        FetchInventoryDescendents fetchinventorydescendents = new FetchInventoryDescendents();
        fetchinventorydescendents.AgentData_Field.AgentID = inventory.getCircuitInfo().agentID;
        fetchinventorydescendents.AgentData_Field.SessionID = inventory.getCircuitInfo().sessionID;
        fetchinventorydescendents.InventoryData_Field.FolderID = folderUUID;
        fetchinventorydescendents.InventoryData_Field.OwnerID = inventory.getCircuitInfo().agentID;
        fetchinventorydescendents.InventoryData_Field.FetchFolders = true;
        fetchinventorydescendents.InventoryData_Field.FetchItems = true;
        fetchinventorydescendents.isReliable = true;
        inventory.SendMessage(fetchinventorydescendents);
    }
}
