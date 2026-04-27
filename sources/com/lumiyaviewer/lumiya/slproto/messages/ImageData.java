// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ImageData extends SLMessage
{
    public static class ImageDataData
    {

        public byte Data[];

        public ImageDataData()
        {
        }
    }

    public static class ImageID
    {

        public int Codec;
        public UUID ID;
        public int Packets;
        public int Size;

        public ImageID()
        {
        }
    }


    public ImageDataData ImageDataData_Field;
    public ImageID ImageID_Field;

    public ImageData()
    {
        zeroCoded = false;
        ImageID_Field = new ImageID();
        ImageDataData_Field = new ImageDataData();
    }

    public int CalcPayloadSize()
    {
        return ImageDataData_Field.Data.length + 2 + 24;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleImageData(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)9);
        packUUID(bytebuffer, ImageID_Field.ID);
        packByte(bytebuffer, (byte)ImageID_Field.Codec);
        packInt(bytebuffer, ImageID_Field.Size);
        packShort(bytebuffer, (short)ImageID_Field.Packets);
        packVariable(bytebuffer, ImageDataData_Field.Data, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ImageID_Field.ID = unpackUUID(bytebuffer);
        ImageID_Field.Codec = unpackByte(bytebuffer) & 0xff;
        ImageID_Field.Size = unpackInt(bytebuffer);
        ImageID_Field.Packets = unpackShort(bytebuffer) & 0xffff;
        ImageDataData_Field.Data = unpackVariable(bytebuffer, 2);
    }
}
