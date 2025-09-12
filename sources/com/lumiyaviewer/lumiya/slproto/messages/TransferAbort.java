// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TransferAbort extends SLMessage
{
    public static class TransferInfo
    {

        public int ChannelType;
        public UUID TransferID;

        public TransferInfo()
        {
        }
    }


    public TransferInfo TransferInfo_Field;

    public TransferAbort()
    {
        zeroCoded = true;
        TransferInfo_Field = new TransferInfo();
    }

    public int CalcPayloadSize()
    {
        return 24;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTransferAbort(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-101);
        packUUID(bytebuffer, TransferInfo_Field.TransferID);
        packInt(bytebuffer, TransferInfo_Field.ChannelType);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TransferInfo_Field.TransferID = unpackUUID(bytebuffer);
        TransferInfo_Field.ChannelType = unpackInt(bytebuffer);
    }
}
