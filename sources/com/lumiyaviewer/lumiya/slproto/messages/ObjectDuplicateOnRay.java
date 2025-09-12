// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ObjectDuplicateOnRay extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public boolean BypassRaycast;
        public boolean CopyCenters;
        public boolean CopyRotates;
        public int DuplicateFlags;
        public UUID GroupID;
        public LLVector3 RayEnd;
        public boolean RayEndIsIntersection;
        public LLVector3 RayStart;
        public UUID RayTargetID;
        public UUID SessionID;

        public AgentData()
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
    public ArrayList ObjectData_Fields;

    public ObjectDuplicateOnRay()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Fields.size() * 4 + 101;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectDuplicateOnRay(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)91);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packLLVector3(bytebuffer, AgentData_Field.RayStart);
        packLLVector3(bytebuffer, AgentData_Field.RayEnd);
        packBoolean(bytebuffer, AgentData_Field.BypassRaycast);
        packBoolean(bytebuffer, AgentData_Field.RayEndIsIntersection);
        packBoolean(bytebuffer, AgentData_Field.CopyCenters);
        packBoolean(bytebuffer, AgentData_Field.CopyRotates);
        packUUID(bytebuffer, AgentData_Field.RayTargetID);
        packInt(bytebuffer, AgentData_Field.DuplicateFlags);
        bytebuffer.put((byte)ObjectData_Fields.size());
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packInt(bytebuffer, ((ObjectData)iterator.next()).ObjectLocalID)) { }
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        AgentData_Field.RayStart = unpackLLVector3(bytebuffer);
        AgentData_Field.RayEnd = unpackLLVector3(bytebuffer);
        AgentData_Field.BypassRaycast = unpackBoolean(bytebuffer);
        AgentData_Field.RayEndIsIntersection = unpackBoolean(bytebuffer);
        AgentData_Field.CopyCenters = unpackBoolean(bytebuffer);
        AgentData_Field.CopyRotates = unpackBoolean(bytebuffer);
        AgentData_Field.RayTargetID = unpackUUID(bytebuffer);
        AgentData_Field.DuplicateFlags = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            ObjectData objectdata = new ObjectData();
            objectdata.ObjectLocalID = unpackInt(bytebuffer);
            ObjectData_Fields.add(objectdata);
        }

    }
}
