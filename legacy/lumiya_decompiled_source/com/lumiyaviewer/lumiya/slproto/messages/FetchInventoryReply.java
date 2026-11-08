package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.base.Ascii;
import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;
/* loaded from: classes.dex */
public class FetchInventoryReply extends SLMessage {
    public AgentData AgentData_Field;
    public ArrayList<InventoryData> InventoryData_Fields = new ArrayList<>();

    /* loaded from: classes.dex */
    public static class AgentData {
        public UUID AgentID;
    }

    /* loaded from: classes.dex */
    public static class InventoryData {
        public UUID AssetID;
        public int BaseMask;
        public int CRC;
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

    public FetchInventoryReply() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public int CalcPayloadSize() {
        int i = 21;
        Iterator<T> it = this.InventoryData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            InventoryData inventoryData = (InventoryData) it.next();
            i = inventoryData.Description.length + inventoryData.Name.length + 129 + 1 + 4 + 4 + i2;
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleFetchInventoryReply(this);
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort((short) -1);
        byteBuffer.put((byte) 1);
        byteBuffer.put(Ascii.CAN);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        byteBuffer.put((byte) this.InventoryData_Fields.size());
        for (InventoryData inventoryData : this.InventoryData_Fields) {
            packUUID(byteBuffer, inventoryData.ItemID);
            packUUID(byteBuffer, inventoryData.FolderID);
            packUUID(byteBuffer, inventoryData.CreatorID);
            packUUID(byteBuffer, inventoryData.OwnerID);
            packUUID(byteBuffer, inventoryData.GroupID);
            packInt(byteBuffer, inventoryData.BaseMask);
            packInt(byteBuffer, inventoryData.OwnerMask);
            packInt(byteBuffer, inventoryData.GroupMask);
            packInt(byteBuffer, inventoryData.EveryoneMask);
            packInt(byteBuffer, inventoryData.NextOwnerMask);
            packBoolean(byteBuffer, inventoryData.GroupOwned);
            packUUID(byteBuffer, inventoryData.AssetID);
            packByte(byteBuffer, (byte) inventoryData.Type);
            packByte(byteBuffer, (byte) inventoryData.InvType);
            packInt(byteBuffer, inventoryData.Flags);
            packByte(byteBuffer, (byte) inventoryData.SaleType);
            packInt(byteBuffer, inventoryData.SalePrice);
            packVariable(byteBuffer, inventoryData.Name, 1);
            packVariable(byteBuffer, inventoryData.Description, 1);
            packInt(byteBuffer, inventoryData.CreationDate);
            packInt(byteBuffer, inventoryData.CRC);
        }
    }

    @Override // com.lumiyaviewer.lumiya.slproto.SLMessage
    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        int i = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i2 = 0; i2 < i; i2++) {
            InventoryData inventoryData = new InventoryData();
            inventoryData.ItemID = unpackUUID(byteBuffer);
            inventoryData.FolderID = unpackUUID(byteBuffer);
            inventoryData.CreatorID = unpackUUID(byteBuffer);
            inventoryData.OwnerID = unpackUUID(byteBuffer);
            inventoryData.GroupID = unpackUUID(byteBuffer);
            inventoryData.BaseMask = unpackInt(byteBuffer);
            inventoryData.OwnerMask = unpackInt(byteBuffer);
            inventoryData.GroupMask = unpackInt(byteBuffer);
            inventoryData.EveryoneMask = unpackInt(byteBuffer);
            inventoryData.NextOwnerMask = unpackInt(byteBuffer);
            inventoryData.GroupOwned = unpackBoolean(byteBuffer);
            inventoryData.AssetID = unpackUUID(byteBuffer);
            inventoryData.Type = unpackByte(byteBuffer);
            inventoryData.InvType = unpackByte(byteBuffer);
            inventoryData.Flags = unpackInt(byteBuffer);
            inventoryData.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            inventoryData.SalePrice = unpackInt(byteBuffer);
            inventoryData.Name = unpackVariable(byteBuffer, 1);
            inventoryData.Description = unpackVariable(byteBuffer, 1);
            inventoryData.CreationDate = unpackInt(byteBuffer);
            inventoryData.CRC = unpackInt(byteBuffer);
            this.InventoryData_Fields.add(inventoryData);
        }
    }
}
