// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class AttachedSound extends SLMessage
{
    public static class DataBlock
    {

        public int Flags;
        public float Gain;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID SoundID;

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public AttachedSound()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return 55;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleAttachedSound(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)13);
        packUUID(bytebuffer, DataBlock_Field.SoundID);
        packUUID(bytebuffer, DataBlock_Field.ObjectID);
        packUUID(bytebuffer, DataBlock_Field.OwnerID);
        packFloat(bytebuffer, DataBlock_Field.Gain);
        packByte(bytebuffer, (byte)DataBlock_Field.Flags);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.SoundID = unpackUUID(bytebuffer);
        DataBlock_Field.ObjectID = unpackUUID(bytebuffer);
        DataBlock_Field.OwnerID = unpackUUID(bytebuffer);
        DataBlock_Field.Gain = unpackFloat(bytebuffer);
        DataBlock_Field.Flags = unpackByte(bytebuffer) & 0xff;
    }
}
