// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TransferInventoryAck extends SLMessage
{
    public static class InfoBlock
    {

        public UUID InventoryID;
        public UUID TransactionID;

        public InfoBlock()
        {
        }
    }


    public InfoBlock InfoBlock_Field;

    public TransferInventoryAck()
    {
        zeroCoded = true;
        InfoBlock_Field = new InfoBlock();
    }

    public int CalcPayloadSize()
    {
        return 36;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTransferInventoryAck(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)40);
        packUUID(bytebuffer, InfoBlock_Field.TransactionID);
        packUUID(bytebuffer, InfoBlock_Field.InventoryID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        InfoBlock_Field.TransactionID = unpackUUID(bytebuffer);
        InfoBlock_Field.InventoryID = unpackUUID(bytebuffer);
    }
}
