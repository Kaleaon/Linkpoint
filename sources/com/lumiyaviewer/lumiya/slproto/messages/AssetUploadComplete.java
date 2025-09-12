// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AssetUploadComplete extends SLMessage
{
    public static class AssetBlock
    {

        public boolean Success;
        public int Type;
        public UUID UUID;

        public AssetBlock()
        {
        }
    }


    public AssetBlock AssetBlock_Field;

    public AssetUploadComplete()
    {
        zeroCoded = false;
        AssetBlock_Field = new AssetBlock();
    }

    public int CalcPayloadSize()
    {
        return 22;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAssetUploadComplete(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)78);
        packUUID(bytebuffer, AssetBlock_Field.UUID);
        packByte(bytebuffer, (byte)AssetBlock_Field.Type);
        packBoolean(bytebuffer, AssetBlock_Field.Success);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AssetBlock_Field.UUID = unpackUUID(bytebuffer);
        AssetBlock_Field.Type = unpackByte(bytebuffer);
        AssetBlock_Field.Success = unpackBoolean(bytebuffer);
    }
}
