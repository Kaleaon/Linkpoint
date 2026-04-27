// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ImagePacket extends SLMessage
{
    public static class ImageData
    {

        public byte Data[];

        public ImageData()
        {
        }
    }

    public static class ImageID
    {

        public UUID ID;
        public int Packet;

        public ImageID()
        {
        }
    }


    public ImageData ImageData_Field;
    public ImageID ImageID_Field;

    public ImagePacket()
    {
        zeroCoded = false;
        ImageID_Field = new ImageID();
        ImageData_Field = new ImageData();
    }

    public int CalcPayloadSize()
    {
        return ImageData_Field.Data.length + 2 + 19;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleImagePacket(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)10);
        packUUID(bytebuffer, ImageID_Field.ID);
        packShort(bytebuffer, (short)ImageID_Field.Packet);
        packVariable(bytebuffer, ImageData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ImageID_Field.ID = unpackUUID(bytebuffer);
        ImageID_Field.Packet = unpackShort(bytebuffer) & 0xffff;
        ImageData_Field.Data = unpackVariable(bytebuffer, 2);
    }
}
