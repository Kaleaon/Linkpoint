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

public class ObjectExtraParams extends SLMessage
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

        public int ObjectLocalID;
        public byte ParamData[];
        public boolean ParamInUse;
        public int ParamSize;
        public int ParamType;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList ObjectData_Fields;

    public ObjectExtraParams()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        Iterator iterator = ObjectData_Fields.iterator();
        int i;
        for (i = 37; iterator.hasNext(); i = ((ObjectData)iterator.next()).ParamData.length + 12 + i) { }
        return i;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectExtraParams(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)99);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packVariable(bytebuffer, objectdata.ParamData, 1))
        {
            objectdata = (ObjectData)iterator.next();
            packInt(bytebuffer, objectdata.ObjectLocalID);
            packShort(bytebuffer, (short)objectdata.ParamType);
            packBoolean(bytebuffer, objectdata.ParamInUse);
            packInt(bytebuffer, objectdata.ParamSize);
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
            objectdata.ParamType = unpackShort(bytebuffer) & 0xffff;
            objectdata.ParamInUse = unpackBoolean(bytebuffer);
            objectdata.ParamSize = unpackInt(bytebuffer);
            objectdata.ParamData = unpackVariable(bytebuffer, 1);
            ObjectData_Fields.add(objectdata);
        }

    }
}
