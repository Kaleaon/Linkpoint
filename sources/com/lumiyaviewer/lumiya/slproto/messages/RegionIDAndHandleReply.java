// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RegionIDAndHandleReply extends SLMessage
{
    public static class ReplyBlock
    {

        public long RegionHandle;
        public UUID RegionID;

        public ReplyBlock()
        {
        }
    }


    public ReplyBlock ReplyBlock_Field;

    public RegionIDAndHandleReply()
    {
        zeroCoded = false;
        ReplyBlock_Field = new ReplyBlock();
    }

    public int CalcPayloadSize()
    {
        return 28;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRegionIDAndHandleReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)54);
        packUUID(bytebuffer, ReplyBlock_Field.RegionID);
        packLong(bytebuffer, ReplyBlock_Field.RegionHandle);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ReplyBlock_Field.RegionID = unpackUUID(bytebuffer);
        ReplyBlock_Field.RegionHandle = unpackLong(bytebuffer);
    }
}
