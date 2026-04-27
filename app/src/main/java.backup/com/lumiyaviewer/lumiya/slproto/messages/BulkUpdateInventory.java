package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class BulkUpdateInventory extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<FolderData> FolderData_Fields = new ArrayList<>();
    public ArrayList<ItemData> ItemData_Fields = new ArrayList<>();

    public static class AgentData {
        public UUID AgentID;
        public UUID TransactionID;
    }

    public static class FolderData {
        public UUID FolderID;
        public byte[] Name;
        public UUID ParentID;
        public int Type;
    }

    public static class ItemData {
        public UUID AssetID;
        public int BaseMask;
        public int CRC;
        public int CallbackID;
        public int CreationDate;
        public UUID CreatorID;
        public byte[] Description;
        public int EveryoneMask;
        public int Flags;
        public UUID FolderID;
        public UUID GroupID;
        public int GroupMask;
        public boolean GroupOwned;
        public int InvType;
        public UUID ItemID;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID OwnerID;
        public int OwnerMask;
        public int SalePrice;
        public int SaleType;
        public int Type;
    }

    public BulkUpdateInventory() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize() {
        int i;
        int i2 = 37;
        Iterator<T> it = this.FolderData_Fields.iterator();
        while (true) {
            i = i2;
            if (!it.hasNext()) {
                break;
            }
            i2 = ((FolderData) it.next()).Name.length + 34 + i;
        }
        int i3 = i + 1;
        Iterator<T> it2 = this.ItemData_Fields.iterator();
        while (true) {
            int i4 = i3;
            if (!it2.hasNext()) {
                return i4;
            }
            ItemData itemData = (ItemData) it2.next();
            i3 = itemData.Description.length + itemData.Name.length + 133 + 1 + 4 + 4 + i4;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleBulkUpdateInventory(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put(Ascii.EM);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.TransactionID);
        byteBuffer.put((byte) this.FolderData_Fields.size());
        for (FolderData folderData : this.FolderData_Fields) {
            packUUID(byteBuffer, folderData.FolderID);
            packUUID(byteBuffer, folderData.ParentID);
            packByte(byteBuffer, (byte) folderData.Type);
            packVariable(byteBuffer, folderData.Name, 1);
        }
        byteBuffer.put((byte) this.ItemData_Fields.size());
        for (ItemData itemData : this.ItemData_Fields) {
            packUUID(byteBuffer, itemData.ItemID);
            packInt(byteBuffer, itemData.CallbackID);
            packUUID(byteBuffer, itemData.FolderID);
            packUUID(byteBuffer, itemData.CreatorID);
            packUUID(byteBuffer, itemData.OwnerID);
            packUUID(byteBuffer, itemData.GroupID);
            packInt(byteBuffer, itemData.BaseMask);
            packInt(byteBuffer, itemData.OwnerMask);
            packInt(byteBuffer, itemData.GroupMask);
            packInt(byteBuffer, itemData.EveryoneMask);
            packInt(byteBuffer, itemData.NextOwnerMask);
            packBoolean(byteBuffer, itemData.GroupOwned);
            packUUID(byteBuffer, itemData.AssetID);
            packByte(byteBuffer, (byte) itemData.Type);
            packByte(byteBuffer, (byte) itemData.InvType);
            packInt(byteBuffer, itemData.Flags);
            packByte(byteBuffer, (byte) itemData.SaleType);
            packInt(byteBuffer, itemData.SalePrice);
            packVariable(byteBuffer, itemData.Name, 1);
            packVariable(byteBuffer, itemData.Description, 1);
            packInt(byteBuffer, itemData.CreationDate);
            packInt(byteBuffer, itemData.CRC);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.TransactionID = unpackUUID(byteBuffer);
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            FolderData folderData = new FolderData();
            folderData.FolderID = unpackUUID(byteBuffer);
            folderData.ParentID = unpackUUID(byteBuffer);
            folderData.Type = unpackByte(byteBuffer);
            folderData.Name = unpackVariable(byteBuffer, 1);
            this.FolderData_Fields.add(folderData);
        }
        byte b2 = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < b2; i2++) {
            ItemData itemData = new ItemData();
            itemData.ItemID = unpackUUID(byteBuffer);
            itemData.CallbackID = unpackInt(byteBuffer);
            itemData.FolderID = unpackUUID(byteBuffer);
            itemData.CreatorID = unpackUUID(byteBuffer);
            itemData.OwnerID = unpackUUID(byteBuffer);
            itemData.GroupID = unpackUUID(byteBuffer);
            itemData.BaseMask = unpackInt(byteBuffer);
            itemData.OwnerMask = unpackInt(byteBuffer);
            itemData.GroupMask = unpackInt(byteBuffer);
            itemData.EveryoneMask = unpackInt(byteBuffer);
            itemData.NextOwnerMask = unpackInt(byteBuffer);
            itemData.GroupOwned = unpackBoolean(byteBuffer);
            itemData.AssetID = unpackUUID(byteBuffer);
            itemData.Type = unpackByte(byteBuffer);
            itemData.InvType = unpackByte(byteBuffer);
            itemData.Flags = unpackInt(byteBuffer);
            itemData.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            itemData.SalePrice = unpackInt(byteBuffer);
            itemData.Name = unpackVariable(byteBuffer, 1);
            itemData.Description = unpackVariable(byteBuffer, 1);
            itemData.CreationDate = unpackInt(byteBuffer);
            itemData.CRC = unpackInt(byteBuffer);
            this.ItemData_Fields.add(itemData);
        }
    }
}
