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

public class ObjectShape extends SLMessage
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
        public int PathBegin;
        public int PathCurve;
        public int PathEnd;
        public int PathRadiusOffset;
        public int PathRevolutions;
        public int PathScaleX;
        public int PathScaleY;
        public int PathShearX;
        public int PathShearY;
        public int PathSkew;
        public int PathTaperX;
        public int PathTaperY;
        public int PathTwist;
        public int PathTwistBegin;
        public int ProfileBegin;
        public int ProfileCurve;
        public int ProfileEnd;
        public int ProfileHollow;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ArrayList ObjectData_Fields;

    public ObjectShape()
    {
        ObjectData_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
    }

    public int CalcPayloadSize()
    {
        return ObjectData_Fields.size() * 27 + 37;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectShape(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)98);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        bytebuffer.put((byte)ObjectData_Fields.size());
        ObjectData objectdata;
        for (Iterator iterator = ObjectData_Fields.iterator(); iterator.hasNext(); packShort(bytebuffer, (short)objectdata.ProfileHollow))
        {
            objectdata = (ObjectData)iterator.next();
            packInt(bytebuffer, objectdata.ObjectLocalID);
            packByte(bytebuffer, (byte)objectdata.PathCurve);
            packByte(bytebuffer, (byte)objectdata.ProfileCurve);
            packShort(bytebuffer, (short)objectdata.PathBegin);
            packShort(bytebuffer, (short)objectdata.PathEnd);
            packByte(bytebuffer, (byte)objectdata.PathScaleX);
            packByte(bytebuffer, (byte)objectdata.PathScaleY);
            packByte(bytebuffer, (byte)objectdata.PathShearX);
            packByte(bytebuffer, (byte)objectdata.PathShearY);
            packByte(bytebuffer, (byte)objectdata.PathTwist);
            packByte(bytebuffer, (byte)objectdata.PathTwistBegin);
            packByte(bytebuffer, (byte)objectdata.PathRadiusOffset);
            packByte(bytebuffer, (byte)objectdata.PathTaperX);
            packByte(bytebuffer, (byte)objectdata.PathTaperY);
            packByte(bytebuffer, (byte)objectdata.PathRevolutions);
            packByte(bytebuffer, (byte)objectdata.PathSkew);
            packShort(bytebuffer, (short)objectdata.ProfileBegin);
            packShort(bytebuffer, (short)objectdata.ProfileEnd);
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
            objectdata.PathCurve = unpackByte(bytebuffer) & 0xff;
            objectdata.ProfileCurve = unpackByte(bytebuffer) & 0xff;
            objectdata.PathBegin = unpackShort(bytebuffer) & 0xffff;
            objectdata.PathEnd = unpackShort(bytebuffer) & 0xffff;
            objectdata.PathScaleX = unpackByte(bytebuffer) & 0xff;
            objectdata.PathScaleY = unpackByte(bytebuffer) & 0xff;
            objectdata.PathShearX = unpackByte(bytebuffer) & 0xff;
            objectdata.PathShearY = unpackByte(bytebuffer) & 0xff;
            objectdata.PathTwist = unpackByte(bytebuffer);
            objectdata.PathTwistBegin = unpackByte(bytebuffer);
            objectdata.PathRadiusOffset = unpackByte(bytebuffer);
            objectdata.PathTaperX = unpackByte(bytebuffer);
            objectdata.PathTaperY = unpackByte(bytebuffer);
            objectdata.PathRevolutions = unpackByte(bytebuffer) & 0xff;
            objectdata.PathSkew = unpackByte(bytebuffer);
            objectdata.ProfileBegin = unpackShort(bytebuffer) & 0xffff;
            objectdata.ProfileEnd = unpackShort(bytebuffer) & 0xffff;
            objectdata.ProfileHollow = unpackShort(bytebuffer) & 0xffff;
            ObjectData_Fields.add(objectdata);
        }

    }
}
