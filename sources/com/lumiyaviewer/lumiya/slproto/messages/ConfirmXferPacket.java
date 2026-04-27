// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ConfirmXferPacket extends SLMessage
{
    public static class XferID
    {

        public long ID;
        public int Packet;

        public XferID()
        {
        }
    }


    public XferID XferID_Field;

    public ConfirmXferPacket()
    {
        zeroCoded = false;
        XferID_Field = new XferID();
    }

    public int CalcPayloadSize()
    {
        return 13;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleConfirmXferPacket(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)19);
        packLong(bytebuffer, XferID_Field.ID);
        packInt(bytebuffer, XferID_Field.Packet);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        XferID_Field.ID = unpackLong(bytebuffer);
        XferID_Field.Packet = unpackInt(bytebuffer);
    }
}
