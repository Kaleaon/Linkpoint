// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AbortXfer extends SLMessage
{
    public static class XferID
    {

        public long ID;
        public int Result;

        public XferID()
        {
        }
    }


    public XferID XferID_Field;

    public AbortXfer()
    {
        zeroCoded = false;
        XferID_Field = new XferID();
    }

    public int CalcPayloadSize()
    {
        return 16;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAbortXfer(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-99);
        packLong(bytebuffer, XferID_Field.ID);
        packInt(bytebuffer, XferID_Field.Result);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        XferID_Field.ID = unpackLong(bytebuffer);
        XferID_Field.Result = unpackInt(bytebuffer);
    }
}
