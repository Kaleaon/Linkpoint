// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RequestParcelTransfer extends SLMessage
{
    public static class Data
    {

        public int ActualArea;
        public int Amount;
        public int BillableArea;
        public UUID DestID;
        public boolean Final;
        public int Flags;
        public UUID OwnerID;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;

        public Data()
        {
        }
    }

    public static class RegionData
    {

        public int GridX;
        public int GridY;
        public UUID RegionID;

        public RegionData()
        {
        }
    }


    public Data Data_Field;
    public RegionData RegionData_Field;

    public RequestParcelTransfer()
    {
        zeroCoded = true;
        Data_Field = new Data();
        RegionData_Field = new RegionData();
    }

    public int CalcPayloadSize()
    {
        return 114;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRequestParcelTransfer(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-36);
        packUUID(bytebuffer, Data_Field.TransactionID);
        packInt(bytebuffer, Data_Field.TransactionTime);
        packUUID(bytebuffer, Data_Field.SourceID);
        packUUID(bytebuffer, Data_Field.DestID);
        packUUID(bytebuffer, Data_Field.OwnerID);
        packByte(bytebuffer, (byte)Data_Field.Flags);
        packInt(bytebuffer, Data_Field.TransactionType);
        packInt(bytebuffer, Data_Field.Amount);
        packInt(bytebuffer, Data_Field.BillableArea);
        packInt(bytebuffer, Data_Field.ActualArea);
        packBoolean(bytebuffer, Data_Field.Final);
        packUUID(bytebuffer, RegionData_Field.RegionID);
        packInt(bytebuffer, RegionData_Field.GridX);
        packInt(bytebuffer, RegionData_Field.GridY);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.TransactionID = unpackUUID(bytebuffer);
        Data_Field.TransactionTime = unpackInt(bytebuffer);
        Data_Field.SourceID = unpackUUID(bytebuffer);
        Data_Field.DestID = unpackUUID(bytebuffer);
        Data_Field.OwnerID = unpackUUID(bytebuffer);
        Data_Field.Flags = unpackByte(bytebuffer) & 0xff;
        Data_Field.TransactionType = unpackInt(bytebuffer);
        Data_Field.Amount = unpackInt(bytebuffer);
        Data_Field.BillableArea = unpackInt(bytebuffer);
        Data_Field.ActualArea = unpackInt(bytebuffer);
        Data_Field.Final = unpackBoolean(bytebuffer);
        RegionData_Field.RegionID = unpackUUID(bytebuffer);
        RegionData_Field.GridX = unpackInt(bytebuffer);
        RegionData_Field.GridY = unpackInt(bytebuffer);
    }
}
