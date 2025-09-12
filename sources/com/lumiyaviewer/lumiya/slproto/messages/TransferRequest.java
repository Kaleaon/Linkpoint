// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class TransferRequest extends SLMessage
{
    public static class TransferInfo
    {

        public int ChannelType;
        public byte Params[];
        public float Priority;
        public int SourceType;
        public UUID TransferID;

        public TransferInfo()
        {
        }
    }


    public TransferInfo TransferInfo_Field;

    public TransferRequest()
    {
        zeroCoded = true;
        TransferInfo_Field = new TransferInfo();
    }

    public int CalcPayloadSize()
    {
        return TransferInfo_Field.Params.length + 30 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleTransferRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-103);
        packUUID(bytebuffer, TransferInfo_Field.TransferID);
        packInt(bytebuffer, TransferInfo_Field.ChannelType);
        packInt(bytebuffer, TransferInfo_Field.SourceType);
        packFloat(bytebuffer, TransferInfo_Field.Priority);
        packVariable(bytebuffer, TransferInfo_Field.Params, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TransferInfo_Field.TransferID = unpackUUID(bytebuffer);
        TransferInfo_Field.ChannelType = unpackInt(bytebuffer);
        TransferInfo_Field.SourceType = unpackInt(bytebuffer);
        TransferInfo_Field.Priority = unpackFloat(bytebuffer);
        TransferInfo_Field.Params = unpackVariable(bytebuffer, 2);
    }
}
