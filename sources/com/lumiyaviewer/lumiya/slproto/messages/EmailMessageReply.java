// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class EmailMessageReply extends SLMessage
{
    public static class DataBlock
    {

        public byte Data[];
        public byte FromAddress[];
        public byte MailFilter[];
        public int More;
        public UUID ObjectID;
        public byte Subject[];
        public int Time;

        public DataBlock()
        {
        }
    }


    public DataBlock DataBlock_Field;

    public EmailMessageReply()
    {
        zeroCoded = false;
        DataBlock_Field = new DataBlock();
    }

    public int CalcPayloadSize()
    {
        return DataBlock_Field.FromAddress.length + 25 + 1 + DataBlock_Field.Subject.length + 2 + DataBlock_Field.Data.length + 1 + DataBlock_Field.MailFilter.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleEmailMessageReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)1);
        bytebuffer.put((byte)80);
        packUUID(bytebuffer, DataBlock_Field.ObjectID);
        packInt(bytebuffer, DataBlock_Field.More);
        packInt(bytebuffer, DataBlock_Field.Time);
        packVariable(bytebuffer, DataBlock_Field.FromAddress, 1);
        packVariable(bytebuffer, DataBlock_Field.Subject, 1);
        packVariable(bytebuffer, DataBlock_Field.Data, 2);
        packVariable(bytebuffer, DataBlock_Field.MailFilter, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        DataBlock_Field.ObjectID = unpackUUID(bytebuffer);
        DataBlock_Field.More = unpackInt(bytebuffer);
        DataBlock_Field.Time = unpackInt(bytebuffer);
        DataBlock_Field.FromAddress = unpackVariable(bytebuffer, 1);
        DataBlock_Field.Subject = unpackVariable(bytebuffer, 1);
        DataBlock_Field.Data = unpackVariable(bytebuffer, 2);
        DataBlock_Field.MailFilter = unpackVariable(bytebuffer, 1);
    }
}
