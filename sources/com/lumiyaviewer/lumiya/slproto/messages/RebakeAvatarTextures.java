// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class RebakeAvatarTextures extends SLMessage
{
    public static class TextureData
    {

        public UUID TextureID;

        public TextureData()
        {
        }
    }


    public TextureData TextureData_Field;

    public RebakeAvatarTextures()
    {
        zeroCoded = false;
        TextureData_Field = new TextureData();
    }

    public int CalcPayloadSize()
    {
        return 20;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleRebakeAvatarTextures(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)87);
        packUUID(bytebuffer, TextureData_Field.TextureID);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        TextureData_Field.TextureID = unpackUUID(bytebuffer);
    }
}
