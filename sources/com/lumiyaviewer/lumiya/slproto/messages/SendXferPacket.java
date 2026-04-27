// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SendXferPacket extends SLMessage
{
    public static class DataPacket
    {

        public byte Data[];

        public DataPacket()
        {
        }
    }

    public static class XferID
    {

        public long ID;
        public int Packet;

        public XferID()
        {
        }
    }


    public DataPacket DataPacket_Field;
    public XferID XferID_Field;

    public SendXferPacket()
    {
        zeroCoded = false;
        XferID_Field = new XferID();
        DataPacket_Field = new DataPacket();
    }

    public int CalcPayloadSize()
    {
        return DataPacket_Field.Data.length + 2 + 13;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSendXferPacket(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)18);
        packLong(bytebuffer, XferID_Field.ID);
        packInt(bytebuffer, XferID_Field.Packet);
        packVariable(bytebuffer, DataPacket_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        XferID_Field.ID = unpackLong(bytebuffer);
        XferID_Field.Packet = unpackInt(bytebuffer);
        DataPacket_Field.Data = unpackVariable(bytebuffer, 2);
    }
}
