// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class UpdateParcel extends SLMessage
{
    public ParcelData ParcelData_Field;
    
    public UpdateParcel() {
        this.zeroCoded = true;
        this.ParcelData_Field = new ParcelData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 43 + 1 + this.ParcelData_Field.Description.length + 1 + this.ParcelData_Field.MusicURL.length + 4 + 4 + 4 + 4 + 1 + 1 + 1 + 16 + 12 + 4 + 16 + 1 + 1 + 4;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleUpdateParcel(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.putShort((short)(-1));
        byteBuffer.put((byte)0);
        byteBuffer.put((byte)(-35));
        this.packUUID(byteBuffer, this.ParcelData_Field.ParcelID);
        this.packLong(byteBuffer, this.ParcelData_Field.RegionHandle);
        this.packUUID(byteBuffer, this.ParcelData_Field.OwnerID);
        this.packBoolean(byteBuffer, this.ParcelData_Field.GroupOwned);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.Status);
        this.packVariable(byteBuffer, this.ParcelData_Field.Name, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.Description, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.MusicURL, 1);
        this.packFloat(byteBuffer, this.ParcelData_Field.RegionX);
        this.packFloat(byteBuffer, this.ParcelData_Field.RegionY);
        this.packInt(byteBuffer, this.ParcelData_Field.ActualArea);
        this.packInt(byteBuffer, this.ParcelData_Field.BillableArea);
        this.packBoolean(byteBuffer, this.ParcelData_Field.ShowDir);
        this.packBoolean(byteBuffer, this.ParcelData_Field.IsForSale);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.Category);
        this.packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
        this.packLLVector3(byteBuffer, this.ParcelData_Field.UserLocation);
        this.packInt(byteBuffer, this.ParcelData_Field.SalePrice);
        this.packUUID(byteBuffer, this.ParcelData_Field.AuthorizedBuyerID);
        this.packBoolean(byteBuffer, this.ParcelData_Field.AllowPublish);
        this.packBoolean(byteBuffer, this.ParcelData_Field.MaturePublish);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ParcelData_Field.ParcelID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.RegionHandle = this.unpackLong(byteBuffer);
        this.ParcelData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.GroupOwned = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.Status = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.Description = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MusicURL = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.RegionX = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.RegionY = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.ActualArea = this.unpackInt(byteBuffer);
        this.ParcelData_Field.BillableArea = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ShowDir = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.IsForSale = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.Category = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.SnapshotID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.UserLocation = this.unpackLLVector3(byteBuffer);
        this.ParcelData_Field.SalePrice = this.unpackInt(byteBuffer);
        this.ParcelData_Field.AuthorizedBuyerID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.AllowPublish = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.MaturePublish = this.unpackBoolean(byteBuffer);
    }
    
    public static class ParcelData
    {
        public int ActualArea;
        public boolean AllowPublish;
        public UUID AuthorizedBuyerID;
        public int BillableArea;
        public int Category;
        public byte[] Description;
        public boolean GroupOwned;
        public boolean IsForSale;
        public boolean MaturePublish;
        public byte[] MusicURL;
        public byte[] Name;
        public UUID OwnerID;
        public UUID ParcelID;
        public long RegionHandle;
        public float RegionX;
        public float RegionY;
        public int SalePrice;
        public boolean ShowDir;
        public UUID SnapshotID;
        public int Status;
        public LLVector3 UserLocation;
    }
}
