// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.net.Inet4Address;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LogFailedMoneyTransaction extends SLMessage
{
    public static class TransactionData
    {

        public int Amount;
        public UUID DestID;
        public int FailureType;
        public int Flags;
        public int GridX;
        public int GridY;
        public Inet4Address SimulatorIP;
        public UUID SourceID;
        public UUID TransactionID;
        public int TransactionTime;
        public int TransactionType;

        public TransactionData()
        {
        }
    }


    public TransactionData TransactionData_Field;

    public LogFailedMoneyTransaction()
    {
        zeroCoded = false;
        TransactionData_Field = new TransactionData();
    }

    public int CalcPayloadSize()
    {
        return 78;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLogFailedMoneyTransaction(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)20);
        packUUID(bytebuffer, TransactionData_Field.TransactionID);
        packInt(bytebuffer, TransactionData_Field.TransactionTime);
        packInt(bytebuffer, TransactionData_Field.TransactionType);
        packUUID(bytebuffer, TransactionData_Field.SourceID);
        packUUID(bytebuffer, TransactionData_Field.DestID);
        packByte(bytebuffer, (byte)TransactionData_Field.Flags);
        packInt(bytebuffer, TransactionData_Field.Amount);
        packIPAddress(bytebuffer, TransactionData_Field.SimulatorIP);
        packInt(bytebuffer, TransactionData_Field.GridX);
        packInt(bytebuffer, TransactionData_Field.GridY);
        packByte(bytebuffer, (byte)TransactionData_Field.FailureType);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TransactionData_Field.TransactionID = unpackUUID(bytebuffer);
        TransactionData_Field.TransactionTime = unpackInt(bytebuffer);
        TransactionData_Field.TransactionType = unpackInt(bytebuffer);
        TransactionData_Field.SourceID = unpackUUID(bytebuffer);
        TransactionData_Field.DestID = unpackUUID(bytebuffer);
        TransactionData_Field.Flags = unpackByte(bytebuffer) & 0xff;
        TransactionData_Field.Amount = unpackInt(bytebuffer);
        TransactionData_Field.SimulatorIP = unpackIPAddress(bytebuffer);
        TransactionData_Field.GridX = unpackInt(bytebuffer);
        TransactionData_Field.GridY = unpackInt(bytebuffer);
        TransactionData_Field.FailureType = unpackByte(bytebuffer) & 0xff;
    }
}
