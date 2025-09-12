// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MoneyTransferRequest extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class MoneyData
    {

        public int AggregatePermInventory;
        public int AggregatePermNextOwner;
        public int Amount;
        public byte Description[];
        public UUID DestID;
        public int Flags;
        public UUID SourceID;
        public int TransactionType;

        public MoneyData()
        {
        }
    }


    public AgentData AgentData_Field;
    public MoneyData MoneyData_Field;

    public MoneyTransferRequest()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        MoneyData_Field = new MoneyData();
    }

    public int CalcPayloadSize()
    {
        return MoneyData_Field.Description.length + 44 + 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMoneyTransferRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)55);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, MoneyData_Field.SourceID);
        packUUID(bytebuffer, MoneyData_Field.DestID);
        packByte(bytebuffer, (byte)MoneyData_Field.Flags);
        packInt(bytebuffer, MoneyData_Field.Amount);
        packByte(bytebuffer, (byte)MoneyData_Field.AggregatePermNextOwner);
        packByte(bytebuffer, (byte)MoneyData_Field.AggregatePermInventory);
        packInt(bytebuffer, MoneyData_Field.TransactionType);
        packVariable(bytebuffer, MoneyData_Field.Description, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        MoneyData_Field.SourceID = unpackUUID(bytebuffer);
        MoneyData_Field.DestID = unpackUUID(bytebuffer);
        MoneyData_Field.Flags = unpackByte(bytebuffer) & 0xff;
        MoneyData_Field.Amount = unpackInt(bytebuffer);
        MoneyData_Field.AggregatePermNextOwner = unpackByte(bytebuffer) & 0xff;
        MoneyData_Field.AggregatePermInventory = unpackByte(bytebuffer) & 0xff;
        MoneyData_Field.TransactionType = unpackInt(bytebuffer);
        MoneyData_Field.Description = unpackVariable(bytebuffer, 1);
    }
}
