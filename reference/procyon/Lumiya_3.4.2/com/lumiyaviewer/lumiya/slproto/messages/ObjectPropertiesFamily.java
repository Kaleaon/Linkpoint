// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectPropertiesFamily extends SLMessage
{
    public ObjectData ObjectData_Field;
    
    public ObjectPropertiesFamily() {
        this.zeroCoded = true;
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ObjectData_Field.Name.length + 102 + 1 + this.ObjectData_Field.Description.length + 2;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectPropertiesFamily(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)10);
        this.packInt(byteBuffer, this.ObjectData_Field.RequestFlags);
        this.packUUID(byteBuffer, this.ObjectData_Field.ObjectID);
        this.packUUID(byteBuffer, this.ObjectData_Field.OwnerID);
        this.packUUID(byteBuffer, this.ObjectData_Field.GroupID);
        this.packInt(byteBuffer, this.ObjectData_Field.BaseMask);
        this.packInt(byteBuffer, this.ObjectData_Field.OwnerMask);
        this.packInt(byteBuffer, this.ObjectData_Field.GroupMask);
        this.packInt(byteBuffer, this.ObjectData_Field.EveryoneMask);
        this.packInt(byteBuffer, this.ObjectData_Field.NextOwnerMask);
        this.packInt(byteBuffer, this.ObjectData_Field.OwnershipCost);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.SaleType);
        this.packInt(byteBuffer, this.ObjectData_Field.SalePrice);
        this.packInt(byteBuffer, this.ObjectData_Field.Category);
        this.packUUID(byteBuffer, this.ObjectData_Field.LastOwnerID);
        this.packVariable(byteBuffer, this.ObjectData_Field.Name, 1);
        this.packVariable(byteBuffer, this.ObjectData_Field.Description, 1);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ObjectData_Field.RequestFlags = this.unpackInt(byteBuffer);
        this.ObjectData_Field.ObjectID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.BaseMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.OwnerMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.GroupMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.EveryoneMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.NextOwnerMask = this.unpackInt(byteBuffer);
        this.ObjectData_Field.OwnershipCost = this.unpackInt(byteBuffer);
        this.ObjectData_Field.SaleType = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.SalePrice = this.unpackInt(byteBuffer);
        this.ObjectData_Field.Category = this.unpackInt(byteBuffer);
        this.ObjectData_Field.LastOwnerID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.ObjectData_Field.Description = this.unpackVariable(byteBuffer, 1);
    }
    
    public static class ObjectData
    {
        public int BaseMask;
        public int Category;
        public byte[] Description;
        public int EveryoneMask;
        public UUID GroupID;
        public int GroupMask;
        public UUID LastOwnerID;
        public byte[] Name;
        public int NextOwnerMask;
        public UUID ObjectID;
        public UUID OwnerID;
        public int OwnerMask;
        public int OwnershipCost;
        public int RequestFlags;
        public int SalePrice;
        public int SaleType;
    }
}
