package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class UpdateParcel extends SLMessage {
    public ParcelData ParcelData_Field = new ParcelData();

    public static class ParcelData {
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

    public UpdateParcel() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return this.ParcelData_Field.Name.length + 43 + 1 + this.ParcelData_Field.Description.length + 1 + this.ParcelData_Field.MusicURL.length + 4 + 4 + 4 + 4 + 1 + 1 + 1 + 16 + 12 + 4 + 16 + 1 + 1 + 4;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleUpdateParcel(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.putShort(-1);
        byteBuffer.put((byte) 0);
        byteBuffer.put((byte) -35);
        packUUID(byteBuffer, this.ParcelData_Field.ParcelID);
        packLong(byteBuffer, this.ParcelData_Field.RegionHandle);
        packUUID(byteBuffer, this.ParcelData_Field.OwnerID);
        packBoolean(byteBuffer, this.ParcelData_Field.GroupOwned);
        packByte(byteBuffer, (byte) this.ParcelData_Field.Status);
        packVariable(byteBuffer, this.ParcelData_Field.Name, 1);
        packVariable(byteBuffer, this.ParcelData_Field.Description, 1);
        packVariable(byteBuffer, this.ParcelData_Field.MusicURL, 1);
        packFloat(byteBuffer, this.ParcelData_Field.RegionX);
        packFloat(byteBuffer, this.ParcelData_Field.RegionY);
        packInt(byteBuffer, this.ParcelData_Field.ActualArea);
        packInt(byteBuffer, this.ParcelData_Field.BillableArea);
        packBoolean(byteBuffer, this.ParcelData_Field.ShowDir);
        packBoolean(byteBuffer, this.ParcelData_Field.IsForSale);
        packByte(byteBuffer, (byte) this.ParcelData_Field.Category);
        packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
        packLLVector3(byteBuffer, this.ParcelData_Field.UserLocation);
        packInt(byteBuffer, this.ParcelData_Field.SalePrice);
        packUUID(byteBuffer, this.ParcelData_Field.AuthorizedBuyerID);
        packBoolean(byteBuffer, this.ParcelData_Field.AllowPublish);
        packBoolean(byteBuffer, this.ParcelData_Field.MaturePublish);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.ParcelData_Field.ParcelID = unpackUUID(byteBuffer);
        this.ParcelData_Field.RegionHandle = unpackLong(byteBuffer);
        this.ParcelData_Field.OwnerID = unpackUUID(byteBuffer);
        this.ParcelData_Field.GroupOwned = unpackBoolean(byteBuffer);
        this.ParcelData_Field.Status = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ParcelData_Field.Name = unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.Description = unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MusicURL = unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.RegionX = unpackFloat(byteBuffer);
        this.ParcelData_Field.RegionY = unpackFloat(byteBuffer);
        this.ParcelData_Field.ActualArea = unpackInt(byteBuffer);
        this.ParcelData_Field.BillableArea = unpackInt(byteBuffer);
        this.ParcelData_Field.ShowDir = unpackBoolean(byteBuffer);
        this.ParcelData_Field.IsForSale = unpackBoolean(byteBuffer);
        this.ParcelData_Field.Category = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ParcelData_Field.SnapshotID = unpackUUID(byteBuffer);
        this.ParcelData_Field.UserLocation = unpackLLVector3(byteBuffer);
        this.ParcelData_Field.SalePrice = unpackInt(byteBuffer);
        this.ParcelData_Field.AuthorizedBuyerID = unpackUUID(byteBuffer);
        this.ParcelData_Field.AllowPublish = unpackBoolean(byteBuffer);
        this.ParcelData_Field.MaturePublish = unpackBoolean(byteBuffer);
    }
}
