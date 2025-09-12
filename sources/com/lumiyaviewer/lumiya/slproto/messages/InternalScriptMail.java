// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class InternalScriptMail extends SLMessage
{
    public static class DataBlock
    {

        public byte Body[];
        public byte From[];
        public byte Subject[];
        public UUID To;

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public InternalScriptMail()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.From.length + 1 + 16 + 1 + DataBlock_Field.Subject.length + 2 + DataBlock_Field.Body.length + 2;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleInternalScriptMail(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)16);
        packVariable(bytebuffer, DataBlock_Field.From, 1);
        packUUID(bytebuffer, DataBlock_Field.To);
        packVariable(bytebuffer, DataBlock_Field.Subject, 1);
        packVariable(bytebuffer, DataBlock_Field.Body, 2);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.From = unpackVariable(bytebuffer, 1);
        DataBlock_Field.To = unpackUUID(bytebuffer);
        DataBlock_Field.Subject = unpackVariable(bytebuffer, 1);
        DataBlock_Field.Body = unpackVariable(bytebuffer, 2);
    }
}
