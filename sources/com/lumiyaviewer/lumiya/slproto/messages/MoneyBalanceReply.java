// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class MoneyBalanceReply extends SLMessage
{
    public static class MoneyData
    {

        public UUID AgentID;
        public byte Description[];
        public int MoneyBalance;
        public int SquareMetersCommitted;
        public int SquareMetersCredit;
        public UUID TransactionID;
        public boolean TransactionSuccess;

        public MoneyData()
        {
        }
    }

    public static class TransactionInfo
    {

        public int Amount;
        public UUID DestID;
        public boolean IsDestGroup;
        public boolean IsSourceGroup;
        public byte ItemDescription[];
        public UUID SourceID;
        public int TransactionType;

        public TransactionInfo()
        {
        }
    }


    public MoneyData MoneyData_Field;
    public TransactionInfo TransactionInfo_Field;

    public MoneyBalanceReply()
    {
        zeroCoded = true;
        MoneyData_Field = new MoneyData();
        TransactionInfo_Field = new TransactionInfo();
    }

    public int CalcPayloadSize()
    {
        return MoneyData_Field.Description.length + 46 + 4 + (TransactionInfo_Field.ItemDescription.length + 43);
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMoneyBalanceReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)58);
        packUUID(bytebuffer, MoneyData_Field.AgentID);
        packUUID(bytebuffer, MoneyData_Field.TransactionID);
        packBoolean(bytebuffer, MoneyData_Field.TransactionSuccess);
        packInt(bytebuffer, MoneyData_Field.MoneyBalance);
        packInt(bytebuffer, MoneyData_Field.SquareMetersCredit);
        packInt(bytebuffer, MoneyData_Field.SquareMetersCommitted);
        packVariable(bytebuffer, MoneyData_Field.Description, 1);
        packInt(bytebuffer, TransactionInfo_Field.TransactionType);
        packUUID(bytebuffer, TransactionInfo_Field.SourceID);
        packBoolean(bytebuffer, TransactionInfo_Field.IsSourceGroup);
        packUUID(bytebuffer, TransactionInfo_Field.DestID);
        packBoolean(bytebuffer, TransactionInfo_Field.IsDestGroup);
        packInt(bytebuffer, TransactionInfo_Field.Amount);
        packVariable(bytebuffer, TransactionInfo_Field.ItemDescription, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        MoneyData_Field.AgentID = unpackUUID(bytebuffer);
        MoneyData_Field.TransactionID = unpackUUID(bytebuffer);
        MoneyData_Field.TransactionSuccess = unpackBoolean(bytebuffer);
        MoneyData_Field.MoneyBalance = unpackInt(bytebuffer);
        MoneyData_Field.SquareMetersCredit = unpackInt(bytebuffer);
        MoneyData_Field.SquareMetersCommitted = unpackInt(bytebuffer);
        MoneyData_Field.Description = unpackVariable(bytebuffer, 1);
        TransactionInfo_Field.TransactionType = unpackInt(bytebuffer);
        TransactionInfo_Field.SourceID = unpackUUID(bytebuffer);
        TransactionInfo_Field.IsSourceGroup = unpackBoolean(bytebuffer);
        TransactionInfo_Field.DestID = unpackUUID(bytebuffer);
        TransactionInfo_Field.IsDestGroup = unpackBoolean(bytebuffer);
        TransactionInfo_Field.Amount = unpackInt(bytebuffer);
        TransactionInfo_Field.ItemDescription = unpackVariable(bytebuffer, 1);
    }
}
