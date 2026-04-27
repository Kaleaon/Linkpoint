// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class DeRezAck extends SLMessage
{
    public static class TransactionData
    {

        public boolean Success;
        public UUID TransactionID;

        public TransactionData()
        {
        }
    }


    public TransactionData TransactionData_Field;

    public DeRezAck()
    {
        zeroCoded = false;
        TransactionData_Field = new TransactionData();
    }

    public int CalcPayloadSize()
    {
        return 21;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleDeRezAck(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)36);
        packUUID(bytebuffer, TransactionData_Field.TransactionID);
        packBoolean(bytebuffer, TransactionData_Field.Success);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TransactionData_Field.TransactionID = unpackUUID(bytebuffer);
        TransactionData_Field.Success = unpackBoolean(bytebuffer);
    }
}
