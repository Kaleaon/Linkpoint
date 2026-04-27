// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelProperties extends SLMessage
{
    public static class AgeVerificationBlock
    {

        public boolean RegionDenyAgeUnverified;

        public AgeVerificationBlock()
        {
        }
    }

    public static class ParcelData
    {

        public LLVector3 AABBMax;
        public LLVector3 AABBMin;
        public int Area;
        public int AuctionID;
        public UUID AuthBuyerID;
        public byte Bitmap[];
        public int Category;
        public int ClaimDate;
        public int ClaimPrice;
        public byte Desc[];
        public UUID GroupID;
        public int GroupPrims;
        public boolean IsGroupOwned;
        public int LandingType;
        public int LocalID;
        public int MaxPrims;
        public int MediaAutoScale;
        public UUID MediaID;
        public byte MediaURL[];
        public byte MusicURL[];
        public byte Name[];
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

        public ParcelData()
        {
        }
    }


    public AgeVerificationBlock AgeVerificationBlock_Field;
    public ParcelData ParcelData_Field;

    public ParcelProperties()
    {
        zeroCoded = true;
        ParcelData_Field = new ParcelData();
        AgeVerificationBlock_Field = new AgeVerificationBlock();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Field.Bitmap.length + 84 + 4 + 1 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4 + 1 + ParcelData_Field.Name.length + 1 + ParcelData_Field.Desc.length + 1 + ParcelData_Field.MusicURL.length + 1 + ParcelData_Field.MediaURL.length + 16 + 1 + 16 + 4 + 4 + 1 + 16 + 16 + 12 + 12 + 1 + 1 + 1 + 1 + 1 + 1 + 1;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelProperties(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)23);
        packInt(bytebuffer, ParcelData_Field.RequestResult);
        packInt(bytebuffer, ParcelData_Field.SequenceID);
        packBoolean(bytebuffer, ParcelData_Field.SnapSelection);
        packInt(bytebuffer, ParcelData_Field.SelfCount);
        packInt(bytebuffer, ParcelData_Field.OtherCount);
        packInt(bytebuffer, ParcelData_Field.PublicCount);
        packInt(bytebuffer, ParcelData_Field.LocalID);
        packUUID(bytebuffer, ParcelData_Field.OwnerID);
        packBoolean(bytebuffer, ParcelData_Field.IsGroupOwned);
        packInt(bytebuffer, ParcelData_Field.AuctionID);
        packInt(bytebuffer, ParcelData_Field.ClaimDate);
        packInt(bytebuffer, ParcelData_Field.ClaimPrice);
        packInt(bytebuffer, ParcelData_Field.RentPrice);
        packLLVector3(bytebuffer, ParcelData_Field.AABBMin);
        packLLVector3(bytebuffer, ParcelData_Field.AABBMax);
        packVariable(bytebuffer, ParcelData_Field.Bitmap, 2);
        packInt(bytebuffer, ParcelData_Field.Area);
        packByte(bytebuffer, (byte)ParcelData_Field.Status);
        packInt(bytebuffer, ParcelData_Field.SimWideMaxPrims);
        packInt(bytebuffer, ParcelData_Field.SimWideTotalPrims);
        packInt(bytebuffer, ParcelData_Field.MaxPrims);
        packInt(bytebuffer, ParcelData_Field.TotalPrims);
        packInt(bytebuffer, ParcelData_Field.OwnerPrims);
        packInt(bytebuffer, ParcelData_Field.GroupPrims);
        packInt(bytebuffer, ParcelData_Field.OtherPrims);
        packInt(bytebuffer, ParcelData_Field.SelectedPrims);
        packFloat(bytebuffer, ParcelData_Field.ParcelPrimBonus);
        packInt(bytebuffer, ParcelData_Field.OtherCleanTime);
        packInt(bytebuffer, ParcelData_Field.ParcelFlags);
        packInt(bytebuffer, ParcelData_Field.SalePrice);
        packVariable(bytebuffer, ParcelData_Field.Name, 1);
        packVariable(bytebuffer, ParcelData_Field.Desc, 1);
        packVariable(bytebuffer, ParcelData_Field.MusicURL, 1);
        packVariable(bytebuffer, ParcelData_Field.MediaURL, 1);
        packUUID(bytebuffer, ParcelData_Field.MediaID);
        packByte(bytebuffer, (byte)ParcelData_Field.MediaAutoScale);
        packUUID(bytebuffer, ParcelData_Field.GroupID);
        packInt(bytebuffer, ParcelData_Field.PassPrice);
        packFloat(bytebuffer, ParcelData_Field.PassHours);
        packByte(bytebuffer, (byte)ParcelData_Field.Category);
        packUUID(bytebuffer, ParcelData_Field.AuthBuyerID);
        packUUID(bytebuffer, ParcelData_Field.SnapshotID);
        packLLVector3(bytebuffer, ParcelData_Field.UserLocation);
        packLLVector3(bytebuffer, ParcelData_Field.UserLookAt);
        packByte(bytebuffer, (byte)ParcelData_Field.LandingType);
        packBoolean(bytebuffer, ParcelData_Field.RegionPushOverride);
        packBoolean(bytebuffer, ParcelData_Field.RegionDenyAnonymous);
        packBoolean(bytebuffer, ParcelData_Field.RegionDenyIdentified);
        packBoolean(bytebuffer, ParcelData_Field.RegionDenyTransacted);
        packBoolean(bytebuffer, AgeVerificationBlock_Field.RegionDenyAgeUnverified);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ParcelData_Field.RequestResult = unpackInt(bytebuffer);
        ParcelData_Field.SequenceID = unpackInt(bytebuffer);
        ParcelData_Field.SnapSelection = unpackBoolean(bytebuffer);
        ParcelData_Field.SelfCount = unpackInt(bytebuffer);
        ParcelData_Field.OtherCount = unpackInt(bytebuffer);
        ParcelData_Field.PublicCount = unpackInt(bytebuffer);
        ParcelData_Field.LocalID = unpackInt(bytebuffer);
        ParcelData_Field.OwnerID = unpackUUID(bytebuffer);
        ParcelData_Field.IsGroupOwned = unpackBoolean(bytebuffer);
        ParcelData_Field.AuctionID = unpackInt(bytebuffer);
        ParcelData_Field.ClaimDate = unpackInt(bytebuffer);
        ParcelData_Field.ClaimPrice = unpackInt(bytebuffer);
        ParcelData_Field.RentPrice = unpackInt(bytebuffer);
        ParcelData_Field.AABBMin = unpackLLVector3(bytebuffer);
        ParcelData_Field.AABBMax = unpackLLVector3(bytebuffer);
        ParcelData_Field.Bitmap = unpackVariable(bytebuffer, 2);
        ParcelData_Field.Area = unpackInt(bytebuffer);
        ParcelData_Field.Status = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.SimWideMaxPrims = unpackInt(bytebuffer);
        ParcelData_Field.SimWideTotalPrims = unpackInt(bytebuffer);
        ParcelData_Field.MaxPrims = unpackInt(bytebuffer);
        ParcelData_Field.TotalPrims = unpackInt(bytebuffer);
        ParcelData_Field.OwnerPrims = unpackInt(bytebuffer);
        ParcelData_Field.GroupPrims = unpackInt(bytebuffer);
        ParcelData_Field.OtherPrims = unpackInt(bytebuffer);
        ParcelData_Field.SelectedPrims = unpackInt(bytebuffer);
        ParcelData_Field.ParcelPrimBonus = unpackFloat(bytebuffer);
        ParcelData_Field.OtherCleanTime = unpackInt(bytebuffer);
        ParcelData_Field.ParcelFlags = unpackInt(bytebuffer);
        ParcelData_Field.SalePrice = unpackInt(bytebuffer);
        ParcelData_Field.Name = unpackVariable(bytebuffer, 1);
        ParcelData_Field.Desc = unpackVariable(bytebuffer, 1);
        ParcelData_Field.MusicURL = unpackVariable(bytebuffer, 1);
        ParcelData_Field.MediaURL = unpackVariable(bytebuffer, 1);
        ParcelData_Field.MediaID = unpackUUID(bytebuffer);
        ParcelData_Field.MediaAutoScale = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.GroupID = unpackUUID(bytebuffer);
        ParcelData_Field.PassPrice = unpackInt(bytebuffer);
        ParcelData_Field.PassHours = unpackFloat(bytebuffer);
        ParcelData_Field.Category = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.AuthBuyerID = unpackUUID(bytebuffer);
        ParcelData_Field.SnapshotID = unpackUUID(bytebuffer);
        ParcelData_Field.UserLocation = unpackLLVector3(bytebuffer);
        ParcelData_Field.UserLookAt = unpackLLVector3(bytebuffer);
        ParcelData_Field.LandingType = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.RegionPushOverride = unpackBoolean(bytebuffer);
        ParcelData_Field.RegionDenyAnonymous = unpackBoolean(bytebuffer);
        ParcelData_Field.RegionDenyIdentified = unpackBoolean(bytebuffer);
        ParcelData_Field.RegionDenyTransacted = unpackBoolean(bytebuffer);
        AgeVerificationBlock_Field.RegionDenyAgeUnverified = unpackBoolean(bytebuffer);
    }
}
