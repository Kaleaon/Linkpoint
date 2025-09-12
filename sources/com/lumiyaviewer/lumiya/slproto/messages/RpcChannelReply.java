// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RpcChannelReply extends SLMessage
{
    public static class DataBlock
    {

        public UUID ChannelID;
        public UUID ItemID;
        public UUID TaskID;

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public RpcChannelReply()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return 52;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRpcChannelReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-98);
        packUUID(bytebuffer, DataBlock_Field.TaskID);
        packUUID(bytebuffer, DataBlock_Field.ItemID);
        packUUID(bytebuffer, DataBlock_Field.ChannelID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.TaskID = unpackUUID(bytebuffer);
        DataBlock_Field.ItemID = unpackUUID(bytebuffer);
        DataBlock_Field.ChannelID = unpackUUID(bytebuffer);
    }
}
