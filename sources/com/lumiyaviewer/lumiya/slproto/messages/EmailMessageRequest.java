// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EmailMessageRequest extends SLMessage
{
    public static class DataBlock
    {

        public byte FromAddress[];
        public UUID ObjectID;
        public byte Subject[];

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public EmailMessageRequest()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.FromAddress.length + 17 + 1 + DataBlock_Field.Subject.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEmailMessageRequest(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)79);
        packUUID(bytebuffer, DataBlock_Field.ObjectID);
        packVariable(bytebuffer, DataBlock_Field.FromAddress, 1);
        packVariable(bytebuffer, DataBlock_Field.Subject, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.ObjectID = unpackUUID(bytebuffer);
        DataBlock_Field.FromAddress = unpackVariable(bytebuffer, 1);
        DataBlock_Field.Subject = unpackVariable(bytebuffer, 1);
    }
}
