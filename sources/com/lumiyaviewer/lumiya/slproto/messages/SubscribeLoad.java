// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SubscribeLoad extends SLMessage
{

    public SubscribeLoad()
    {
        zeroCoded = false;
    }

    public int CalcPayloadSize()
    {
        return 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSubscribeLoad(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)7);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
    }
}
