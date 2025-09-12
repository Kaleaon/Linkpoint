// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ImageNotInDatabase extends SLMessage
{
    public static class ImageID
    {

        public UUID ID;

        public ImageID()
        {
        }
    }


    public ImageID ImageID_Field;

    public ImageNotInDatabase()
    {
        zeroCoded = false;
        ImageID_Field = new ImageID();
    }

    public int CalcPayloadSize()
    {
        return 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleImageNotInDatabase(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)86);
        packUUID(bytebuffer, ImageID_Field.ID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        ImageID_Field.ID = unpackUUID(bytebuffer);
    }
}
