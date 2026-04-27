package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UpdateAttachment extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public AttachmentBlock AttachmentBlock_Field = new AttachmentBlock();
    public InventoryData InventoryData_Field = new InventoryData();
    public OperationData OperationData_Field = new OperationData();

    public static class AgentData {
        public UUID AgentID;
        public UUID SessionID;
    }

    public static class AttachmentBlock {
        public int AttachmentPoint;
    }

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

    public static class OperationData {
        public boolean AddItem;
        public boolean UseExistingAsset;
    }

    public UpdateAttachment() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.InventoryData_Field.Name.length + 129 + 1 + this.InventoryData_Field.Description.length + 4 + 4 + 39;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateAttachment(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 1);
        byteBuffer.put((byte) 75);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packByte(byteBuffer, (byte) this.AttachmentBlock_Field.AttachmentPoint);
        packBoolean(byteBuffer, this.OperationData_Field.AddItem);
        packBoolean(byteBuffer, this.OperationData_Field.UseExistingAsset);
        packUUID(byteBuffer, this.InventoryData_Field.ItemID);
        packUUID(byteBuffer, this.InventoryData_Field.FolderID);
        packUUID(byteBuffer, this.InventoryData_Field.CreatorID);
        packUUID(byteBuffer, this.InventoryData_Field.OwnerID);
        packUUID(byteBuffer, this.InventoryData_Field.GroupID);
        packInt(byteBuffer, this.InventoryData_Field.BaseMask);
        packInt(byteBuffer, this.InventoryData_Field.OwnerMask);
        packInt(byteBuffer, this.InventoryData_Field.GroupMask);
        packInt(byteBuffer, this.InventoryData_Field.EveryoneMask);
        packInt(byteBuffer, this.InventoryData_Field.NextOwnerMask);
        packBoolean(byteBuffer, this.InventoryData_Field.GroupOwned);
        packUUID(byteBuffer, this.InventoryData_Field.AssetID);
        packByte(byteBuffer, (byte) this.InventoryData_Field.Type);
        packByte(byteBuffer, (byte) this.InventoryData_Field.InvType);
        packInt(byteBuffer, this.InventoryData_Field.Flags);
        packByte(byteBuffer, (byte) this.InventoryData_Field.SaleType);
        packInt(byteBuffer, this.InventoryData_Field.SalePrice);
        packVariable(byteBuffer, this.InventoryData_Field.Name, 1);
        packVariable(byteBuffer, this.InventoryData_Field.Description, 1);
        packInt(byteBuffer, this.InventoryData_Field.CreationDate);
        packInt(byteBuffer, this.InventoryData_Field.CRC);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AttachmentBlock_Field.AttachmentPoint = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.OperationData_Field.AddItem = unpackBoolean(byteBuffer);
        this.OperationData_Field.UseExistingAsset = unpackBoolean(byteBuffer);
        this.InventoryData_Field.ItemID = unpackUUID(byteBuffer);
        this.InventoryData_Field.FolderID = unpackUUID(byteBuffer);
        this.InventoryData_Field.CreatorID = unpackUUID(byteBuffer);
        this.InventoryData_Field.OwnerID = unpackUUID(byteBuffer);
        this.InventoryData_Field.GroupID = unpackUUID(byteBuffer);
        this.InventoryData_Field.BaseMask = unpackInt(byteBuffer);
        this.InventoryData_Field.OwnerMask = unpackInt(byteBuffer);
        this.InventoryData_Field.GroupMask = unpackInt(byteBuffer);
        this.InventoryData_Field.EveryoneMask = unpackInt(byteBuffer);
        this.InventoryData_Field.NextOwnerMask = unpackInt(byteBuffer);
        this.InventoryData_Field.GroupOwned = unpackBoolean(byteBuffer);
        this.InventoryData_Field.AssetID = unpackUUID(byteBuffer);
        this.InventoryData_Field.Type = unpackByte(byteBuffer);
        this.InventoryData_Field.InvType = unpackByte(byteBuffer);
        this.InventoryData_Field.Flags = unpackInt(byteBuffer);
        this.InventoryData_Field.SaleType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.InventoryData_Field.SalePrice = unpackInt(byteBuffer);
        this.InventoryData_Field.Name = unpackVariable(byteBuffer, 1);
        this.InventoryData_Field.Description = unpackVariable(byteBuffer, 1);
        this.InventoryData_Field.CreationDate = unpackInt(byteBuffer);
        this.InventoryData_Field.CRC = unpackInt(byteBuffer);
    }
}
