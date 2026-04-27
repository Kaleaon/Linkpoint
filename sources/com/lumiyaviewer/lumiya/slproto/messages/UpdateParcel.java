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

public class UpdateParcel extends SLMessage
{
    public static class ParcelData
    {

        public int ActualArea;
        public boolean AllowPublish;
        public UUID AuthorizedBuyerID;
        public int BillableArea;
        public int Category;
        public byte Description[];
        public boolean GroupOwned;
        public boolean IsForSale;
        public boolean MaturePublish;
        public byte MusicURL[];
        public byte Name[];
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

        public ParcelData()
        {
        }
    }


    public ParcelData ParcelData_Field;

    public UpdateParcel()
    {
        zeroCoded = true;
        ParcelData_Field = new ParcelData();
    }

    public int CalcPayloadSize()
    {
        return ParcelData_Field.Name.length + 43 + 1 + ParcelData_Field.Description.length + 1 + ParcelData_Field.MusicURL.length + 4 + 4 + 4 + 4 + 1 + 1 + 1 + 16 + 12 + 4 + 16 + 1 + 1 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleUpdateParcel(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-35);
        packUUID(bytebuffer, ParcelData_Field.ParcelID);
        packLong(bytebuffer, ParcelData_Field.RegionHandle);
        packUUID(bytebuffer, ParcelData_Field.OwnerID);
        packBoolean(bytebuffer, ParcelData_Field.GroupOwned);
        packByte(bytebuffer, (byte)ParcelData_Field.Status);
        packVariable(bytebuffer, ParcelData_Field.Name, 1);
        packVariable(bytebuffer, ParcelData_Field.Description, 1);
        packVariable(bytebuffer, ParcelData_Field.MusicURL, 1);
        packFloat(bytebuffer, ParcelData_Field.RegionX);
        packFloat(bytebuffer, ParcelData_Field.RegionY);
        packInt(bytebuffer, ParcelData_Field.ActualArea);
        packInt(bytebuffer, ParcelData_Field.BillableArea);
        packBoolean(bytebuffer, ParcelData_Field.ShowDir);
        packBoolean(bytebuffer, ParcelData_Field.IsForSale);
        packByte(bytebuffer, (byte)ParcelData_Field.Category);
        packUUID(bytebuffer, ParcelData_Field.SnapshotID);
        packLLVector3(bytebuffer, ParcelData_Field.UserLocation);
        packInt(bytebuffer, ParcelData_Field.SalePrice);
        packUUID(bytebuffer, ParcelData_Field.AuthorizedBuyerID);
        packBoolean(bytebuffer, ParcelData_Field.AllowPublish);
        packBoolean(bytebuffer, ParcelData_Field.MaturePublish);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ParcelData_Field.ParcelID = unpackUUID(bytebuffer);
        ParcelData_Field.RegionHandle = unpackLong(bytebuffer);
        ParcelData_Field.OwnerID = unpackUUID(bytebuffer);
        ParcelData_Field.GroupOwned = unpackBoolean(bytebuffer);
        ParcelData_Field.Status = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.Name = unpackVariable(bytebuffer, 1);
        ParcelData_Field.Description = unpackVariable(bytebuffer, 1);
        ParcelData_Field.MusicURL = unpackVariable(bytebuffer, 1);
        ParcelData_Field.RegionX = unpackFloat(bytebuffer);
        ParcelData_Field.RegionY = unpackFloat(bytebuffer);
        ParcelData_Field.ActualArea = unpackInt(bytebuffer);
        ParcelData_Field.BillableArea = unpackInt(bytebuffer);
        ParcelData_Field.ShowDir = unpackBoolean(bytebuffer);
        ParcelData_Field.IsForSale = unpackBoolean(bytebuffer);
        ParcelData_Field.Category = unpackByte(bytebuffer) & 0xff;
        ParcelData_Field.SnapshotID = unpackUUID(bytebuffer);
        ParcelData_Field.UserLocation = unpackLLVector3(bytebuffer);
        ParcelData_Field.SalePrice = unpackInt(bytebuffer);
        ParcelData_Field.AuthorizedBuyerID = unpackUUID(bytebuffer);
        ParcelData_Field.AllowPublish = unpackBoolean(bytebuffer);
        ParcelData_Field.MaturePublish = unpackBoolean(bytebuffer);
    }
}
