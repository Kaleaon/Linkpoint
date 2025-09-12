// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class LoadURL extends SLMessage
{
    public static class Data
    {

        public byte Message[];
        public UUID ObjectID;
        public byte ObjectName[];
        public UUID OwnerID;
        public boolean OwnerIsGroup;
        public byte URL[];

        public Data()
        {
        }
    }


    public Data Data_Field;

    public LoadURL()
    {
        zeroCoded = false;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return Data_Field.ObjectName.length + 1 + 16 + 16 + 1 + 1 + Data_Field.Message.length + 1 + Data_Field.URL.length + 4;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleLoadURL(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-62);
        packVariable(bytebuffer, Data_Field.ObjectName, 1);
        packUUID(bytebuffer, Data_Field.ObjectID);
        packUUID(bytebuffer, Data_Field.OwnerID);
        packBoolean(bytebuffer, Data_Field.OwnerIsGroup);
        packVariable(bytebuffer, Data_Field.Message, 1);
        packVariable(bytebuffer, Data_Field.URL, 1);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.ObjectName = unpackVariable(bytebuffer, 1);
        Data_Field.ObjectID = unpackUUID(bytebuffer);
        Data_Field.OwnerID = unpackUUID(bytebuffer);
        Data_Field.OwnerIsGroup = unpackBoolean(bytebuffer);
        Data_Field.Message = unpackVariable(bytebuffer, 1);
        Data_Field.URL = unpackVariable(bytebuffer, 1);
    }
}
