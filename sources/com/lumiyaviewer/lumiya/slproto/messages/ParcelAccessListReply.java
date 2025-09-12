// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ParcelAccessListReply extends SLMessage
{
    public static class Data
    {

        public UUID AgentID;
        public int Flags;
        public int LocalID;
        public int SequenceID;

        public Data()
        {
        }
    }

    public static class List
    {

        public int Flags;
        public UUID ID;
        public int Time;

        public List()
        {
        }
    }


    public Data Data_Field;
    public ArrayList List_Fields;

    public ParcelAccessListReply()
    {
        List_Fields = new ArrayList();
        zeroCoded = true;
        Data_Field = new Data();
    }

    public int CalcPayloadSize()
    {
        return List_Fields.size() * 24 + 33;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleParcelAccessListReply(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)-40);
        packUUID(bytebuffer, Data_Field.AgentID);
        packInt(bytebuffer, Data_Field.SequenceID);
        packInt(bytebuffer, Data_Field.Flags);
        packInt(bytebuffer, Data_Field.LocalID);
        bytebuffer.put((byte)List_Fields.size());
        List list;
        for (Iterator iterator = List_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, list.Flags))
        {
            list = (List)iterator.next();
            packUUID(bytebuffer, list.ID);
            packInt(bytebuffer, list.Time);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        Data_Field.AgentID = unpackUUID(bytebuffer);
        Data_Field.SequenceID = unpackInt(bytebuffer);
        Data_Field.Flags = unpackInt(bytebuffer);
        Data_Field.LocalID = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            List list = new List();
            list.ID = unpackUUID(bytebuffer);
            list.Time = unpackInt(bytebuffer);
            list.Flags = unpackInt(bytebuffer);
            List_Fields.add(list);
        }

    }
}
