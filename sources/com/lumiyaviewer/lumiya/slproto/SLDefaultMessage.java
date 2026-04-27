// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto;

import com.lumiyaviewer.lumiya.slproto.messages.SLMessageHandler;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto:
//            SLMessage

public class SLDefaultMessage extends SLMessage
{

    public SLDefaultMessage()
    {
    }

    public int CalcPayloadSize()
    {
        return 0;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.DefaultMessageHandler(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
    }
}
