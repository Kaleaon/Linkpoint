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

public class ObjectOwner extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class HeaderData
    {

        public UUID GroupID;
        public boolean Override;
        public UUID OwnerID;

        public HeaderData()
        {
        }
    }

    public static class ObjectData
    {

        public int ObjectLocalID;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public HeaderData HeaderData_Field;
    public ArrayList ObjectData_Fields;

    public ObjectOwner()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        HeaderData_Field = new HeaderData();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Fields.size() * 4 + 70;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectOwner(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)100);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packBoolean(bytebuffer, HeaderData_Field.Override);
        packUUID(bytebuffer, HeaderData_Field.OwnerID);
        packUUID(bytebuffer, HeaderData_Field.GroupID);
        bytebuffer.put((byte)ObjectData_Fields.size());
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, ((ObjectData)iterator.next()).ObjectLocalID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        HeaderData_Field.Override = unpackBoolean(bytebuffer);
        HeaderData_Field.OwnerID = unpackUUID(bytebuffer);
        HeaderData_Field.GroupID = unpackUUID(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ObjectLocalID = unpackInt(bytebuffer);
            ObjectData_Fields.add(objectdata);
        }

    }
}
