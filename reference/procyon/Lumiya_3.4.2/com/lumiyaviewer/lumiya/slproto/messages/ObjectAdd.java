// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.messages;

import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.util.UUID;
import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.SLMessage;

public class ObjectAdd extends SLMessage
{
    public AgentData AgentData_Field;
    public ObjectData ObjectData_Field;
    
    public ObjectAdd() {
        this.zeroCoded = true;
        this.AgentData_Field = new AgentData();
        this.ObjectData_Field = new ObjectData();
    }
    
    @Override
    public int CalcPayloadSize() {
        return 146;
    }
    
    @Override
    public void Handle(final SLMessageHandler slMessageHandler) {
        slMessageHandler.HandleObjectAdd(this);
    }
    
    @Override
    public void PackPayload(final ByteBuffer byteBuffer) {
        byteBuffer.put((byte)(-1));
        byteBuffer.put((byte)1);
        this.packUUID(byteBuffer, this.AgentData_Field.AgentID);
        this.packUUID(byteBuffer, this.AgentData_Field.SessionID);
        this.packUUID(byteBuffer, this.AgentData_Field.GroupID);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PCode);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.Material);
        this.packInt(byteBuffer, this.ObjectData_Field.AddFlags);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathCurve);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.ProfileCurve);
        this.packShort(byteBuffer, (short)this.ObjectData_Field.PathBegin);
        this.packShort(byteBuffer, (short)this.ObjectData_Field.PathEnd);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathScaleX);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathScaleY);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathShearX);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathShearY);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathTwist);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathTwistBegin);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathRadiusOffset);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathTaperX);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathTaperY);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathRevolutions);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.PathSkew);
        this.packShort(byteBuffer, (short)this.ObjectData_Field.ProfileBegin);
        this.packShort(byteBuffer, (short)this.ObjectData_Field.ProfileEnd);
        this.packShort(byteBuffer, (short)this.ObjectData_Field.ProfileHollow);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.BypassRaycast);
        this.packLLVector3(byteBuffer, this.ObjectData_Field.RayStart);
        this.packLLVector3(byteBuffer, this.ObjectData_Field.RayEnd);
        this.packUUID(byteBuffer, this.ObjectData_Field.RayTargetID);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.RayEndIsIntersection);
        this.packLLVector3(byteBuffer, this.ObjectData_Field.Scale);
        this.packLLQuaternion(byteBuffer, this.ObjectData_Field.Rotation);
        this.packByte(byteBuffer, (byte)this.ObjectData_Field.State);
    }
    
    @Override
    public void UnpackPayload(final ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = this.unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.PCode = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.Material = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.AddFlags = this.unpackInt(byteBuffer);
        this.ObjectData_Field.PathCurve = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.ProfileCurve = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.PathBegin = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.ObjectData_Field.PathEnd = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.ObjectData_Field.PathScaleX = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.PathScaleY = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.PathShearX = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.PathShearY = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.PathTwist = this.unpackByte(byteBuffer);
        this.ObjectData_Field.PathTwistBegin = this.unpackByte(byteBuffer);
        this.ObjectData_Field.PathRadiusOffset = this.unpackByte(byteBuffer);
        this.ObjectData_Field.PathTaperX = this.unpackByte(byteBuffer);
        this.ObjectData_Field.PathTaperY = this.unpackByte(byteBuffer);
        this.ObjectData_Field.PathRevolutions = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.PathSkew = this.unpackByte(byteBuffer);
        this.ObjectData_Field.ProfileBegin = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.ObjectData_Field.ProfileEnd = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.ObjectData_Field.ProfileHollow = (this.unpackShort(byteBuffer) & 0xFFFF);
        this.ObjectData_Field.BypassRaycast = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.RayStart = this.unpackLLVector3(byteBuffer);
        this.ObjectData_Field.RayEnd = this.unpackLLVector3(byteBuffer);
        this.ObjectData_Field.RayTargetID = this.unpackUUID(byteBuffer);
        this.ObjectData_Field.RayEndIsIntersection = (this.unpackByte(byteBuffer) & 0xFF);
        this.ObjectData_Field.Scale = this.unpackLLVector3(byteBuffer);
        this.ObjectData_Field.Rotation = this.unpackLLQuaternion(byteBuffer);
        this.ObjectData_Field.State = (this.unpackByte(byteBuffer) & 0xFF);
    }
    
    public static class AgentData
    {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
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
    }
}
