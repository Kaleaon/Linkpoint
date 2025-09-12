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

public class ObjectImage extends SLMessage
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

        public byte MediaURL[];
        public int ObjectLocalID;
        public byte TextureEntry[];

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList ObjectData_Fields;

    public ObjectImage()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ObjectData_Fields.iterator();
        ObjectData objectdata;
        int i;
        int j;
        for (i = 37; iterator.hasNext(); i = objectdata.TextureEntry.length + (j + 5 + 2) + i)
        {
            objectdata = (ObjectData)iterator.next();
            j = objectdata.MediaURL.length;
        }

        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectImage(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)96);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, objectdata.TextureEntry, 2))
        {
            objectdata = (ObjectData)iterator.next();
            packInt(bytebuffer, objectdata.ObjectLocalID);
            packVariable(bytebuffer, objectdata.MediaURL, 1);
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
            objectdata.MediaURL = unpackVariable(bytebuffer, 1);
            objectdata.TextureEntry = unpackVariable(bytebuffer, 2);
            ObjectData_Fields.add(objectdata);
        }

    }
}
