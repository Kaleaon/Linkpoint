// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelMediaUpdate extends SLMessage
{
    public static class DataBlock
    {

        public int MediaAutoScale;
        public UUID MediaID;
        public byte MediaURL[];

        public DataBlock()
        {
        }
    }

    public static class DataBlockExtended
    {

        public byte MediaDesc[];
        public int MediaHeight;
        public int MediaLoop;
        public byte MediaType[];
        public int MediaWidth;

        public DataBlockExtended()
        {
        }
    }


    public DataBlockExtended DataBlockExtended_Field;
    public DataBlock DataBlock_Field;

    public ParcelMediaUpdate()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
        DataBlockExtended_Field = new DataBlockExtended();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.MediaURL.length + 1 + 16 + 1 + 4 + (DataBlockExtended_Field.MediaType.length + 1 + 1 + DataBlockExtended_Field.MediaDesc.length + 4 + 4 + 1);
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelMediaUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)-92);
        packVariable(bytebuffer, DataBlock_Field.MediaURL, 1);
        packUUID(bytebuffer, DataBlock_Field.MediaID);
        packByte(bytebuffer, (byte)DataBlock_Field.MediaAutoScale);
        packVariable(bytebuffer, DataBlockExtended_Field.MediaType, 1);
        packVariable(bytebuffer, DataBlockExtended_Field.MediaDesc, 1);
        packInt(bytebuffer, DataBlockExtended_Field.MediaWidth);
        packInt(bytebuffer, DataBlockExtended_Field.MediaHeight);
        packByte(bytebuffer, (byte)DataBlockExtended_Field.MediaLoop);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.MediaURL = unpackVariable(bytebuffer, 1);
        DataBlock_Field.MediaID = unpackUUID(bytebuffer);
        DataBlock_Field.MediaAutoScale = unpackByte(bytebuffer) & 0xff;
        DataBlockExtended_Field.MediaType = unpackVariable(bytebuffer, 1);
        DataBlockExtended_Field.MediaDesc = unpackVariable(bytebuffer, 1);
        DataBlockExtended_Field.MediaWidth = unpackInt(bytebuffer);
        DataBlockExtended_Field.MediaHeight = unpackInt(bytebuffer);
        DataBlockExtended_Field.MediaLoop = unpackByte(bytebuffer) & 0xff;
    }
}
