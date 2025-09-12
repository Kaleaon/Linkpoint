// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MoneyTransferBackend extends SLMessage
{
    public static class MoneyData
    {

        public int AggregatePermInventory;
        public int AggregatePermNextOwner;
        public int Amount;
        public byte Description[];
        public UUID DestID;
        public int Flags;
        public int GridX;
        public int GridY;
        public UUID RegionID;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;

        public MoneyData()
        {
        }
    }


    public MoneyData MoneyData_Field;

    public MoneyTransferBackend()
    {
        zeroCoded = true;
        MoneyData_Field = new MoneyData();
    }

    public int CalcPayloadSize()
    {
        return MoneyData_Field.Description.length + 88 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMoneyTransferBackend(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)56);
        packUUID(bytebuffer, MoneyData_Field.TransactionID);
        packInt(bytebuffer, MoneyData_Field.TransactionTime);
        packUUID(bytebuffer, MoneyData_Field.SourceID);
        packUUID(bytebuffer, MoneyData_Field.DestID);
        packByte(bytebuffer, (byte)MoneyData_Field.Flags);
        packInt(bytebuffer, MoneyData_Field.Amount);
        packByte(bytebuffer, (byte)MoneyData_Field.AggregatePermNextOwner);
        packByte(bytebuffer, (byte)MoneyData_Field.AggregatePermInventory);
        packInt(bytebuffer, MoneyData_Field.TransactionType);
        packUUID(bytebuffer, MoneyData_Field.RegionID);
        packInt(bytebuffer, MoneyData_Field.GridX);
        packInt(bytebuffer, MoneyData_Field.GridY);
        packVariable(bytebuffer, MoneyData_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        MoneyData_Field.TransactionID = unpackUUID(bytebuffer);
        MoneyData_Field.TransactionTime = unpackInt(bytebuffer);
        MoneyData_Field.SourceID = unpackUUID(bytebuffer);
        MoneyData_Field.DestID = unpackUUID(bytebuffer);
        MoneyData_Field.Flags = unpackByte(bytebuffer) & 0xff;
        MoneyData_Field.Amount = unpackInt(bytebuffer);
        MoneyData_Field.AggregatePermNextOwner = unpackByte(bytebuffer) & 0xff;
        MoneyData_Field.AggregatePermInventory = unpackByte(bytebuffer) & 0xff;
        MoneyData_Field.TransactionType = unpackInt(bytebuffer);
        MoneyData_Field.RegionID = unpackUUID(bytebuffer);
        MoneyData_Field.GridX = unpackInt(bytebuffer);
        MoneyData_Field.GridY = unpackInt(bytebuffer);
        MoneyData_Field.Description = unpackVariable(bytebuffer, 1);
    }
}
