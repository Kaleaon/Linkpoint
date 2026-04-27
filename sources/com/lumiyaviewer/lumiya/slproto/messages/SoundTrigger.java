// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class SoundTrigger extends SLMessage
{
    public static class SoundData
    {

        public float Gain;
        public long Handle;
        public UUID ObjectID;
        public UUID OwnerID;
        public UUID ParentID;
        public LLVector3 Position;
        public UUID SoundID;

        public SoundData()
        {
        }
    }


    public SoundData SoundData_Field;

    public SoundTrigger()
    {
        zeroCoded = false;
        SoundData_Field = new SoundData();
    }

    public int CalcPayloadSize()
    {
        return 89;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleSoundTrigger(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)29);
        packUUID(bytebuffer, SoundData_Field.SoundID);
        packUUID(bytebuffer, SoundData_Field.OwnerID);
        packUUID(bytebuffer, SoundData_Field.ObjectID);
        packUUID(bytebuffer, SoundData_Field.ParentID);
        packLong(bytebuffer, SoundData_Field.Handle);
        packLLVector3(bytebuffer, SoundData_Field.Position);
        packFloat(bytebuffer, SoundData_Field.Gain);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        SoundData_Field.SoundID = unpackUUID(bytebuffer);
        SoundData_Field.OwnerID = unpackUUID(bytebuffer);
        SoundData_Field.ObjectID = unpackUUID(bytebuffer);
        SoundData_Field.ParentID = unpackUUID(bytebuffer);
        SoundData_Field.Handle = unpackLong(bytebuffer);
        SoundData_Field.Position = unpackLLVector3(bytebuffer);
        SoundData_Field.Gain = unpackFloat(bytebuffer);
    }
}
