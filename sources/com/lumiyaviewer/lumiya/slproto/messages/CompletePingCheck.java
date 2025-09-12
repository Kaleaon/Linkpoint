// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class CompletePingCheck extends SLMessage
{
    public static class PingID
    {

        public int PingID;

        public PingID()
        {
        }
    }


    public PingID PingID_Field;

    public CompletePingCheck()
    {
        zeroCoded = false;
        PingID_Field = new PingID();
    }

    public int CalcPayloadSize()
    {
        return 2;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleCompletePingCheck(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)2);
        packByte(bytebuffer, (byte)PingID_Field.PingID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        PingID_Field.PingID = unpackByte(bytebuffer) & 0xff;
    }
}
