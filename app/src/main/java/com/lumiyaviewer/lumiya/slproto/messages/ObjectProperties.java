package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

public class ObjectProperties extends SLMessage {
    public ArrayList<ObjectData> ObjectData_Fields = new ArrayList<>();

    public static class ObjectData {
        public int AggregatePermTextures;
        public int AggregatePermTexturesOwner;
        public int AggregatePerms;
        public int BaseMask;
        public int Category;
        public long CreationDate;
        public UUID CreatorID;
        public byte[] Description;
        public int EveryoneMask;
        public UUID FolderID;
        public UUID FromTaskID;
        public UUID GroupID;
        public int GroupMask;
        public int InventorySerial;
        public UUID ItemID;
        public UUID LastOwnerID;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID ObjectID;
        public UUID OwnerID;
        public int OwnerMask;
        public int OwnershipCost;
        public int SalePrice;
        public int SaleType;
        public byte[] SitName;
        public byte[] TextureID;
        public byte[] TouchName;
    }

    public ObjectProperties() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        int i = 3;
        Iterator<T> it = this.ObjectData_Fields.iterator();
        while (true) {
            int i2 = i;
            if (!it.hasNext()) {
                return i2;
            }
            ObjectData objectData = (ObjectData) it.next();
            i = objectData.TextureID.length + objectData.Name.length + 175 + 1 + objectData.Description.length + 1 + objectData.TouchName.length + 1 + objectData.SitName.length + 1 + i2;
        }
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectProperties(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 9);
        byteBuffer.put((byte) this.ObjectData_Fields.size());
        for (ObjectData objectData : this.ObjectData_Fields) {
            packUUID(byteBuffer, objectData.ObjectID);
            packUUID(byteBuffer, objectData.CreatorID);
            packUUID(byteBuffer, objectData.OwnerID);
            packUUID(byteBuffer, objectData.GroupID);
            packLong(byteBuffer, objectData.CreationDate);
            packInt(byteBuffer, objectData.BaseMask);
            packInt(byteBuffer, objectData.OwnerMask);
            packInt(byteBuffer, objectData.GroupMask);
            packInt(byteBuffer, objectData.EveryoneMask);
            packInt(byteBuffer, objectData.NextOwnerMask);
            packInt(byteBuffer, objectData.OwnershipCost);
            packByte(byteBuffer, (byte) objectData.SaleType);
            packInt(byteBuffer, objectData.SalePrice);
            packByte(byteBuffer, (byte) objectData.AggregatePerms);
            packByte(byteBuffer, (byte) objectData.AggregatePermTextures);
            packByte(byteBuffer, (byte) objectData.AggregatePermTexturesOwner);
            packInt(byteBuffer, objectData.Category);
            packShort(byteBuffer, (short) objectData.InventorySerial);
            packUUID(byteBuffer, objectData.ItemID);
            packUUID(byteBuffer, objectData.FolderID);
            packUUID(byteBuffer, objectData.FromTaskID);
            packUUID(byteBuffer, objectData.LastOwnerID);
            packVariable(byteBuffer, objectData.Name, 1);
            packVariable(byteBuffer, objectData.Description, 1);
            packVariable(byteBuffer, objectData.TouchName, 1);
            packVariable(byteBuffer, objectData.SitName, 1);
            packVariable(byteBuffer, objectData.TextureID, 1);
        }
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE;
        for (int i = 0; i < b; i++) {
            ObjectData objectData = new ObjectData();
            objectData.ObjectID = unpackUUID(byteBuffer);
            objectData.CreatorID = unpackUUID(byteBuffer);
            objectData.OwnerID = unpackUUID(byteBuffer);
            objectData.GroupID = unpackUUID(byteBuffer);
            objectData.CreationDate = unpackLong(byteBuffer);
            objectData.BaseMask = unpackInt(byteBuffer);
            objectData.OwnerMask = unpackInt(byteBuffer);
            objectData.GroupMask = unpackInt(byteBuffer);
            objectData.EveryoneMask = unpackInt(byteBuffer);
            objectData.NextOwnerMask = unpackInt(byteBuffer);
            objectData.OwnershipCost = unpackInt(byteBuffer);
            objectData.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.SalePrice = unpackInt(byteBuffer);
            objectData.AggregatePerms = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.AggregatePermTextures = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.AggregatePermTexturesOwner = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
            objectData.Category = unpackInt(byteBuffer);
            objectData.InventorySerial = unpackShort(byteBuffer);
            objectData.ItemID = unpackUUID(byteBuffer);
            objectData.FolderID = unpackUUID(byteBuffer);
            objectData.FromTaskID = unpackUUID(byteBuffer);
            objectData.LastOwnerID = unpackUUID(byteBuffer);
            objectData.Name = unpackVariable(byteBuffer, 1);
            objectData.Description = unpackVariable(byteBuffer, 1);
            objectData.TouchName = unpackVariable(byteBuffer, 1);
            objectData.SitName = unpackVariable(byteBuffer, 1);
            objectData.TextureID = unpackVariable(byteBuffer, 1);
            this.ObjectData_Fields.add(objectData);
        }
    }
}
