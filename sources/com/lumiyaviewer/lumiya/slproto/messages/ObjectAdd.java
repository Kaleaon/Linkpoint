// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

// Referenced classes of package com.lumiyaviewer.lumiya.slproto.messages:
//            SLMessageHandler

public class ObjectAdd extends SLMessage
{
    public static class AgentData
    {

        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;

        public AgentData()
        {
        }
    }

    public static class ObjectData
    {

        public int AddFlags;
        public int BypassRaycast;
        public int Material;
        public int PCode;
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
        public LLVector3 RayEnd;
        public int RayEndIsIntersection;
        public LLVector3 RayStart;
        public UUID RayTargetID;
        public LLQuaternion Rotation;
        public LLVector3 Scale;
        public int State;

        public ObjectData()
        {
        }
    }


    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;

    public ObjectAdd()
    {
        zeroCoded = true;
        AgentData_Field = new AgentData();
        ObjectData_Field = new ObjectData();
    }

    public int CalcPayloadSize()
    {
        return 146;
    }

    public void Handle(SLMessageHandler slmessagehandler)
    {
        slmessagehandler.HandleObjectAdd(this);
    }

    public void PackPayload(ByteBuffer bytebuffer)
    {
        bytebuffer.put((byte)-1);
        bytebuffer.put((byte)1);
        packUUID(bytebuffer, AgentData_Field.AgentID);
        packUUID(bytebuffer, AgentData_Field.SessionID);
        packUUID(bytebuffer, AgentData_Field.GroupID);
        packByte(bytebuffer, (byte)ObjectData_Field.PCode);
        packByte(bytebuffer, (byte)ObjectData_Field.Material);
        packInt(bytebuffer, ObjectData_Field.AddFlags);
        packByte(bytebuffer, (byte)ObjectData_Field.PathCurve);
        packByte(bytebuffer, (byte)ObjectData_Field.ProfileCurve);
        packShort(bytebuffer, (short)ObjectData_Field.PathBegin);
        packShort(bytebuffer, (short)ObjectData_Field.PathEnd);
        packByte(bytebuffer, (byte)ObjectData_Field.PathScaleX);
        packByte(bytebuffer, (byte)ObjectData_Field.PathScaleY);
        packByte(bytebuffer, (byte)ObjectData_Field.PathShearX);
        packByte(bytebuffer, (byte)ObjectData_Field.PathShearY);
        packByte(bytebuffer, (byte)ObjectData_Field.PathTwist);
        packByte(bytebuffer, (byte)ObjectData_Field.PathTwistBegin);
        packByte(bytebuffer, (byte)ObjectData_Field.PathRadiusOffset);
        packByte(bytebuffer, (byte)ObjectData_Field.PathTaperX);
        packByte(bytebuffer, (byte)ObjectData_Field.PathTaperY);
        packByte(bytebuffer, (byte)ObjectData_Field.PathRevolutions);
        packByte(bytebuffer, (byte)ObjectData_Field.PathSkew);
        packShort(bytebuffer, (short)ObjectData_Field.ProfileBegin);
        packShort(bytebuffer, (short)ObjectData_Field.ProfileEnd);
        packShort(bytebuffer, (short)ObjectData_Field.ProfileHollow);
        packByte(bytebuffer, (byte)ObjectData_Field.BypassRaycast);
        packLLVector3(bytebuffer, ObjectData_Field.RayStart);
        packLLVector3(bytebuffer, ObjectData_Field.RayEnd);
        packUUID(bytebuffer, ObjectData_Field.RayTargetID);
        packByte(bytebuffer, (byte)ObjectData_Field.RayEndIsIntersection);
        packLLVector3(bytebuffer, ObjectData_Field.Scale);
        packLLQuaternion(bytebuffer, ObjectData_Field.Rotation);
        packByte(bytebuffer, (byte)ObjectData_Field.State);
    }

    public void UnpackPayload(ByteBuffer bytebuffer)
    {
        AgentData_Field.AgentID = unpackUUID(bytebuffer);
        AgentData_Field.SessionID = unpackUUID(bytebuffer);
        AgentData_Field.GroupID = unpackUUID(bytebuffer);
        ObjectData_Field.PCode = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.Material = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.AddFlags = unpackInt(bytebuffer);
        ObjectData_Field.PathCurve = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.ProfileCurve = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.PathBegin = unpackShort(bytebuffer) & 0xffff;
        ObjectData_Field.PathEnd = unpackShort(bytebuffer) & 0xffff;
        ObjectData_Field.PathScaleX = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.PathScaleY = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.PathShearX = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.PathShearY = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.PathTwist = unpackByte(bytebuffer);
        ObjectData_Field.PathTwistBegin = unpackByte(bytebuffer);
        ObjectData_Field.PathRadiusOffset = unpackByte(bytebuffer);
        ObjectData_Field.PathTaperX = unpackByte(bytebuffer);
        ObjectData_Field.PathTaperY = unpackByte(bytebuffer);
        ObjectData_Field.PathRevolutions = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.PathSkew = unpackByte(bytebuffer);
        ObjectData_Field.ProfileBegin = unpackShort(bytebuffer) & 0xffff;
        ObjectData_Field.ProfileEnd = unpackShort(bytebuffer) & 0xffff;
        ObjectData_Field.ProfileHollow = unpackShort(bytebuffer) & 0xffff;
        ObjectData_Field.BypassRaycast = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.RayStart = unpackLLVector3(bytebuffer);
        ObjectData_Field.RayEnd = unpackLLVector3(bytebuffer);
        ObjectData_Field.RayTargetID = unpackUUID(bytebuffer);
        ObjectData_Field.RayEndIsIntersection = unpackByte(bytebuffer) & 0xff;
        ObjectData_Field.Scale = unpackLLVector3(bytebuffer);
        ObjectData_Field.Rotation = unpackLLQuaternion(bytebuffer);
        ObjectData_Field.State = unpackByte(bytebuffer) & 0xff;
    }
}
