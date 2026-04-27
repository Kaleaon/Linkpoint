// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TransferPacket extends SLMessage
{
    public static class TransferData
    {

        public int ChannelType;
        public byte Data[];
        public int Packet;
        public int Status;
        public UUID TransferID;

        public TransferData()
        {
        }
    }


    public TransferData TransferData_Field;

    public TransferPacket()
    {
        zeroCoded = false;
        TransferData_Field = new TransferData();
    }

    public int CalcPayloadSize()
    {
        return TransferData_Field.Data.length + 30 + 1;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTransferPacket(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)17);
        packUUID(bytebuffer, TransferData_Field.TransferID);
        packInt(bytebuffer, TransferData_Field.ChannelType);
        packInt(bytebuffer, TransferData_Field.Packet);
        packInt(bytebuffer, TransferData_Field.Status);
        packVariable(bytebuffer, TransferData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TransferData_Field.TransferID = unpackUUID(bytebuffer);
        TransferData_Field.ChannelType = unpackInt(bytebuffer);
        TransferData_Field.Packet = unpackInt(bytebuffer);
        TransferData_Field.Status = unpackInt(bytebuffer);
        TransferData_Field.Data = unpackVariable(bytebuffer, 2);
    }
}
