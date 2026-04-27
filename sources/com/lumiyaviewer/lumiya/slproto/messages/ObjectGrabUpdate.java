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

public class ObjectGrabUpdate extends SLMessage
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

        public LLVector3 GrabOffsetInitial;
        public LLVector3 GrabPosition;
        public UUID ObjectID;
        public int TimeSinceLast;

        public ObjectData()
        {
        }
    }

    public static class SurfaceInfo
    {

        public LLVector3 Binormal;
        public int FaceIndex;
        public LLVector3 Normal;
        public LLVector3 Position;
        public LLVector3 STCoord;
        public LLVector3 UVCoord;

        public SurfaceInfo()
        {
        }
    }


    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    public ArrayList SurfaceInfo_Fields;

    public ObjectGrabUpdate()
    {
        SurfaceInfo_Fields = new ArrayList();
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return SurfaceInfo_Fields.size() * 64 + 81;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectGrabUpdate(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.putShort((short)-1);
        bytebuffer.put((byte)0);
        bytebuffer.put((byte)118);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, ObjectData_Field.ObjectID);
        packLLVector3(bytebuffer, ObjectData_Field.GrabOffsetInitial);
        packLLVector3(bytebuffer, ObjectData_Field.GrabPosition);
        packInt(bytebuffer, ObjectData_Field.TimeSinceLast);
        bytebuffer.put((byte)SurfaceInfo_Fields.size());
        SurfaceInfo surfaceinfo;
        for (Iterator iterator = SurfaceInfo_Fields.iterator(); iterator.hasNext(); packLLVector3(bytebuffer, surfaceinfo.Binormal))
        {
            surfaceinfo = (SurfaceInfo)iterator.next();
            packLLVector3(bytebuffer, surfaceinfo.UVCoord);
            packLLVector3(bytebuffer, surfaceinfo.STCoord);
            packInt(bytebuffer, surfaceinfo.FaceIndex);
            packLLVector3(bytebuffer, surfaceinfo.Position);
            packLLVector3(bytebuffer, surfaceinfo.Normal);
        }

    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        ObjectData_Field.ObjectID = unpackUUID(bytebuffer);
        ObjectData_Field.GrabOffsetInitial = unpackLLVector3(bytebuffer);
        ObjectData_Field.GrabPosition = unpackLLVector3(bytebuffer);
        ObjectData_Field.TimeSinceLast = unpackInt(bytebuffer);
        byte byte0 = bytebuffer.get();
        for (int i = 0; i < (byte0 & 0xff); i++)
        {
            SurfaceInfo surfaceinfo = new SurfaceInfo();
            surfaceinfo.UVCoord = unpackLLVector3(bytebuffer);
            surfaceinfo.STCoord = unpackLLVector3(bytebuffer);
            surfaceinfo.FaceIndex = unpackInt(bytebuffer);
            surfaceinfo.Position = unpackLLVector3(bytebuffer);
            surfaceinfo.Normal = unpackLLVector3(bytebuffer);
            surfaceinfo.Binormal = unpackLLVector3(bytebuffer);
            SurfaceInfo_Fields.add(surfaceinfo);
        }

    }
}
