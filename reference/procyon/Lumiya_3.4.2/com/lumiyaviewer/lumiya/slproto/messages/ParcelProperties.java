// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import java.util.UUID;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ParcelProperties extends SLMessage
{
    public AgeVerificationBlock AgeVerificationBlock_Field;
    public ParcelData ParcelData_Field;
    
    public ParcelProperties() {
        this.zeroCoded = true;
        this.ParcelData_Field = new ParcelData();
        this.AgeVerificationBlock_Field = new AgeVerificationBlock();
    }
    
    @Override
    public int CalcPayloadSize() {
        return this.ParcelData_Field.Bitmap.length + 84 + 4 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + this.ParcelData_Field.Name.length + 1 + this.ParcelData_Field.Desc.length + 1 + this.ParcelData_Field.MusicURL.length + 1 + this.ParcelData_Field.MediaURL.length + 16 + 1 + 16 + 4 + 4 + 1 + 16 + 16 + 12 + 12 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleParcelProperties(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)23);
        this.packInt(byteBuffer, this.ParcelData_Field.RequestResult);
        this.packInt(byteBuffer, this.ParcelData_Field.SequenceID);
        this.packBoolean(byteBuffer, this.ParcelData_Field.SnapSelection);
        this.packInt(byteBuffer, this.ParcelData_Field.SelfCount);
        this.packInt(byteBuffer, this.ParcelData_Field.OtherCount);
        this.packInt(byteBuffer, this.ParcelData_Field.PublicCount);
        this.packInt(byteBuffer, this.ParcelData_Field.LocalID);
        this.packUUID(byteBuffer, this.ParcelData_Field.OwnerID);
        this.packBoolean(byteBuffer, this.ParcelData_Field.IsGroupOwned);
        this.packInt(byteBuffer, this.ParcelData_Field.AuctionID);
        this.packInt(byteBuffer, this.ParcelData_Field.ClaimDate);
        this.packInt(byteBuffer, this.ParcelData_Field.ClaimPrice);
        this.packInt(byteBuffer, this.ParcelData_Field.RentPrice);
        this.packLLVector3(byteBuffer, this.ParcelData_Field.AABBMin);
        this.packLLVector3(byteBuffer, this.ParcelData_Field.AABBMax);
        this.packVariable(byteBuffer, this.ParcelData_Field.Bitmap, 2);
        this.packInt(byteBuffer, this.ParcelData_Field.Area);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.Status);
        this.packInt(byteBuffer, this.ParcelData_Field.SimWideMaxPrims);
        this.packInt(byteBuffer, this.ParcelData_Field.SimWideTotalPrims);
        this.packInt(byteBuffer, this.ParcelData_Field.MaxPrims);
        this.packInt(byteBuffer, this.ParcelData_Field.TotalPrims);
        this.packInt(byteBuffer, this.ParcelData_Field.OwnerPrims);
        this.packInt(byteBuffer, this.ParcelData_Field.GroupPrims);
        this.packInt(byteBuffer, this.ParcelData_Field.OtherPrims);
        this.packInt(byteBuffer, this.ParcelData_Field.SelectedPrims);
        this.packFloat(byteBuffer, this.ParcelData_Field.ParcelPrimBonus);
        this.packInt(byteBuffer, this.ParcelData_Field.OtherCleanTime);
        this.packInt(byteBuffer, this.ParcelData_Field.ParcelFlags);
        this.packInt(byteBuffer, this.ParcelData_Field.SalePrice);
        this.packVariable(byteBuffer, this.ParcelData_Field.Name, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.Desc, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.MusicURL, 1);
        this.packVariable(byteBuffer, this.ParcelData_Field.MediaURL, 1);
        this.packUUID(byteBuffer, this.ParcelData_Field.MediaID);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.MediaAutoScale);
        this.packUUID(byteBuffer, this.ParcelData_Field.GroupID);
        this.packInt(byteBuffer, this.ParcelData_Field.PassPrice);
        this.packFloat(byteBuffer, this.ParcelData_Field.PassHours);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.Category);
        this.packUUID(byteBuffer, this.ParcelData_Field.AuthBuyerID);
        this.packUUID(byteBuffer, this.ParcelData_Field.SnapshotID);
        this.packLLVector3(byteBuffer, this.ParcelData_Field.UserLocation);
        this.packLLVector3(byteBuffer, this.ParcelData_Field.UserLookAt);
        this.packByte(byteBuffer, (byte)this.ParcelData_Field.LandingType);
        this.packBoolean(byteBuffer, this.ParcelData_Field.RegionPushOverride);
        this.packBoolean(byteBuffer, this.ParcelData_Field.RegionDenyAnonymous);
        this.packBoolean(byteBuffer, this.ParcelData_Field.RegionDenyIdentified);
        this.packBoolean(byteBuffer, this.ParcelData_Field.RegionDenyTransacted);
        this.packBoolean(byteBuffer, this.AgeVerificationBlock_Field.RegionDenyAgeUnverified);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.ParcelData_Field.RequestResult = this.unpackInt(byteBuffer);
        this.ParcelData_Field.SequenceID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.SnapSelection = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.SelfCount = this.unpackInt(byteBuffer);
        this.ParcelData_Field.OtherCount = this.unpackInt(byteBuffer);
        this.ParcelData_Field.PublicCount = this.unpackInt(byteBuffer);
        this.ParcelData_Field.LocalID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.OwnerID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.IsGroupOwned = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.AuctionID = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ClaimDate = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ClaimPrice = this.unpackInt(byteBuffer);
        this.ParcelData_Field.RentPrice = this.unpackInt(byteBuffer);
        this.ParcelData_Field.AABBMin = this.unpackLLVector3(byteBuffer);
        this.ParcelData_Field.AABBMax = this.unpackLLVector3(byteBuffer);
        this.ParcelData_Field.Bitmap = this.unpackVariable(byteBuffer, 2);
        this.ParcelData_Field.Area = this.unpackInt(byteBuffer);
        this.ParcelData_Field.Status = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.SimWideMaxPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.SimWideTotalPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.MaxPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.TotalPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.OwnerPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.GroupPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.OtherPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.SelectedPrims = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ParcelPrimBonus = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.OtherCleanTime = this.unpackInt(byteBuffer);
        this.ParcelData_Field.ParcelFlags = this.unpackInt(byteBuffer);
        this.ParcelData_Field.SalePrice = this.unpackInt(byteBuffer);
        this.ParcelData_Field.Name = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.Desc = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MusicURL = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MediaURL = this.unpackVariable(byteBuffer, 1);
        this.ParcelData_Field.MediaID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.MediaAutoScale = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.PassPrice = this.unpackInt(byteBuffer);
        this.ParcelData_Field.PassHours = this.unpackFloat(byteBuffer);
        this.ParcelData_Field.Category = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.AuthBuyerID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.SnapshotID = this.unpackUUID(byteBuffer);
        this.ParcelData_Field.UserLocation = this.unpackLLVector3(byteBuffer);
        this.ParcelData_Field.UserLookAt = this.unpackLLVector3(byteBuffer);
        this.ParcelData_Field.LandingType = (this.unpackByte(byteBuffer) & 0xFF);
        this.ParcelData_Field.RegionPushOverride = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.RegionDenyAnonymous = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.RegionDenyIdentified = this.unpackBoolean(byteBuffer);
        this.ParcelData_Field.RegionDenyTransacted = this.unpackBoolean(byteBuffer);
        this.AgeVerificationBlock_Field.RegionDenyAgeUnverified = this.unpackBoolean(byteBuffer);
    }
    
    public static class AgeVerificationBlock
    {
        public boolean RegionDenyAgeUnverified;
    }
    
    public static class ParcelData
    {
        public LLVector3 AABBMax;
        public LLVector3 AABBMin;
        public int Area;
        public int AuctionID;
        public UUID AuthBuyerID;
        public byte[] Bitmap;
        public int Category;
        public int ClaimDate;
        public int ClaimPrice;
        public byte[] Desc;
        public UUID GroupID;
        public int GroupPrims;
        public boolean IsGroupOwned;
        public int LandingType;
        public int LocalID;
        public int MaxPrims;
        public int MediaAutoScale;
        public UUID MediaID;
        public byte[] MediaURL;
        public byte[] MusicURL;
        public byte[] Name;
        public int OtherCleanTime;
        public int OtherCount;
        public int OtherPrims;
        public UUID OwnerID;
        public int OwnerPrims;
        public int ParcelFlags;
        public float ParcelPrimBonus;
        public float PassHours;
        public int PassPrice;
        public int PublicCount;
        public boolean RegionDenyAnonymous;
        public boolean RegionDenyIdentified;
        public boolean RegionDenyTransacted;
        public boolean RegionPushOverride;
        public int RentPrice;
        public int RequestResult;
        public int SalePrice;
        public int SelectedPrims;
        public int SelfCount;
        public int SequenceID;
        public int SimWideMaxPrims;
        public int SimWideTotalPrims;
        public boolean SnapSelection;
        public UUID SnapshotID;
        public int Status;
        public int TotalPrims;
        public LLVector3 UserLocation;
        public LLVector3 UserLookAt;
    }
}
