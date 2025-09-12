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

public class MultipleObjectUpdate extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ObjectData
    {

        public byte Data[];
        public int ObjectLocalID;
        public int Type;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList ObjectData_Fields;

    public MultipleObjectUpdate()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ObjectData_Fields.iterator();
        int i;
        for (i = 35; iterator.hasNext(); i = ((ObjectData)iterator.next()).Data.length + 6 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleMultipleObjectUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)2);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, objectdata.Data, 1))
        {
            objectdata = (ObjectData)iterator.next();
            packInt(bytebuffer, objectdata.ObjectLocalID);
            packByte(bytebuffer, (byte)objectdata.Type);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ObjectLocalID = unpackInt(bytebuffer);
            objectdata.Type = unpackByte(bytebuffer) & 0xff;
            objectdata.Data = unpackVariable(bytebuffer, 1);
            ObjectData_Fields.add(objectdata);
        }

    }
}
