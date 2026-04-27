// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RegionHandleRequest extends SLMessage
{
    public static class RequestBlock
    {

        public UUID RegionID;

        public RequestBlock()
        {
        }
    }


    public RequestBlock RequestBlock_Field;

    public RegionHandleRequest()
    {
        zeroCoded = false;
        RequestBlock_Field = new RequestBlock();
    }

    public int CalcPayloadSize()
    {
        return 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRegionHandleRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)53);
        packUUID(bytebuffer, RequestBlock_Field.RegionID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        RequestBlock_Field.RegionID = unpackUUID(bytebuffer);
    }
}
