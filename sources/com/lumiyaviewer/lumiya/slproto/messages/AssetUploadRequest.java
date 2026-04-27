// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AssetUploadRequest extends SLMessage
{
    public static class AssetBlock
    {

        public byte AssetData[];
        public boolean StoreLocal;
        public boolean Tempfile;
        public UUID TransactionID;
        public int Type;

        public AssetBlock()
        {
        }
    }


    public AssetBlock AssetBlock_Field;

    public AssetUploadRequest()
    {
        zeroCoded = false;
        AssetBlock_Field = new AssetBlock();
    }

    public int CalcPayloadSize()
    {
        return AssetBlock_Field.AssetData.length + 21 + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAssetUploadRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)77);
        packUUID(bytebuffer, AssetBlock_Field.TransactionID);
        packByte(bytebuffer, (byte)AssetBlock_Field.Type);
        packBoolean(bytebuffer, AssetBlock_Field.Tempfile);
        packBoolean(bytebuffer, AssetBlock_Field.StoreLocal);
        packVariable(bytebuffer, AssetBlock_Field.AssetData, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AssetBlock_Field.TransactionID = unpackUUID(bytebuffer);
        AssetBlock_Field.Type = unpackByte(bytebuffer);
        AssetBlock_Field.Tempfile = unpackBoolean(bytebuffer);
        AssetBlock_Field.StoreLocal = unpackBoolean(bytebuffer);
        AssetBlock_Field.AssetData = unpackVariable(bytebuffer, 2);
    }
}
