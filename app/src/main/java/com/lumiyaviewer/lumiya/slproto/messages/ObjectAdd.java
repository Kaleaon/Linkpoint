package com.lumiyaviewer.lumiya.slproto.messages;

import com.google.common.primitives.UnsignedBytes;
import com.lumiyaviewer.lumiya.slproto.SLMessage;
import com.lumiyaviewer.lumiya.slproto.types.LLQuaternion;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;
import java.nio.ByteBuffer;
import java.util.UUID;

public class ObjectAdd extends SLMessage {
    public AgentData AgentData_Field = new AgentData();
    public ObjectData ObjectData_Field = new ObjectData();

    public static class AgentData {
        public UUID AgentID;
        public UUID GroupID;
        public UUID SessionID;
    }

    public static class ObjectData {
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

    public ObjectAdd() {
        this.zeroCoded = true;
    }

    public int CalcPayloadSize() {
        return 146;
    }

    public void Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectAdd(this);
    }

    public void PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((byte) -1);
        byteBuffer.put((byte) 1);
        packUUID(byteBuffer, this.AgentData_Field.AgentID);
        packUUID(byteBuffer, this.AgentData_Field.SessionID);
        packUUID(byteBuffer, this.AgentData_Field.GroupID);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PCode);
        packByte(byteBuffer, (byte) this.ObjectData_Field.Material);
        packInt(byteBuffer, this.ObjectData_Field.AddFlags);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathCurve);
        packByte(byteBuffer, (byte) this.ObjectData_Field.ProfileCurve);
        packShort(byteBuffer, (short) this.ObjectData_Field.PathBegin);
        packShort(byteBuffer, (short) this.ObjectData_Field.PathEnd);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathScaleX);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathScaleY);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathShearX);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathShearY);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathTwist);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathTwistBegin);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathRadiusOffset);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathTaperX);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathTaperY);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathRevolutions);
        packByte(byteBuffer, (byte) this.ObjectData_Field.PathSkew);
        packShort(byteBuffer, (short) this.ObjectData_Field.ProfileBegin);
        packShort(byteBuffer, (short) this.ObjectData_Field.ProfileEnd);
        packShort(byteBuffer, (short) this.ObjectData_Field.ProfileHollow);
        packByte(byteBuffer, (byte) this.ObjectData_Field.BypassRaycast);
        packLLVector3(byteBuffer, this.ObjectData_Field.RayStart);
        packLLVector3(byteBuffer, this.ObjectData_Field.RayEnd);
        packUUID(byteBuffer, this.ObjectData_Field.RayTargetID);
        packByte(byteBuffer, (byte) this.ObjectData_Field.RayEndIsIntersection);
        packLLVector3(byteBuffer, this.ObjectData_Field.Scale);
        packLLQuaternion(byteBuffer, this.ObjectData_Field.Rotation);
        packByte(byteBuffer, (byte) this.ObjectData_Field.State);
    }

    public void UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer);
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer);
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer);
        this.ObjectData_Field.PCode = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.Material = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.AddFlags = unpackInt(byteBuffer);
        this.ObjectData_Field.PathCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.ProfileCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.PathBegin = unpackShort(byteBuffer) & 65535;
        this.ObjectData_Field.PathEnd = unpackShort(byteBuffer) & 65535;
        this.ObjectData_Field.PathScaleX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.PathScaleY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.PathShearX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.PathShearY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.PathTwist = unpackByte(byteBuffer);
        this.ObjectData_Field.PathTwistBegin = unpackByte(byteBuffer);
        this.ObjectData_Field.PathRadiusOffset = unpackByte(byteBuffer);
        this.ObjectData_Field.PathTaperX = unpackByte(byteBuffer);
        this.ObjectData_Field.PathTaperY = unpackByte(byteBuffer);
        this.ObjectData_Field.PathRevolutions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.PathSkew = unpackByte(byteBuffer);
        this.ObjectData_Field.ProfileBegin = unpackShort(byteBuffer) & 65535;
        this.ObjectData_Field.ProfileEnd = unpackShort(byteBuffer) & 65535;
        this.ObjectData_Field.ProfileHollow = unpackShort(byteBuffer) & 65535;
        this.ObjectData_Field.BypassRaycast = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.RayStart = unpackLLVector3(byteBuffer);
        this.ObjectData_Field.RayEnd = unpackLLVector3(byteBuffer);
        this.ObjectData_Field.RayTargetID = unpackUUID(byteBuffer);
        this.ObjectData_Field.RayEndIsIntersection = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
        this.ObjectData_Field.Scale = unpackLLVector3(byteBuffer);
        this.ObjectData_Field.Rotation = unpackLLQuaternion(byteBuffer);
        this.ObjectData_Field.State = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE;
    }
}
