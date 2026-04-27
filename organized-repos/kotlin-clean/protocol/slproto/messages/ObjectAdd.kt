package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ObjectAdd : SLMessage() {
    public AgentData AgentData_Field = AgentData()
    public ObjectData ObjectData_Field = ObjectData()

    @JvmStatic
    class AgentData {
        public UUID AgentID
        public UUID GroupID
        public UUID SessionID
    }

    @JvmStatic
    class ObjectData {
        public Int AddFlags
        public Int BypassRaycast
        public Int Material
        public Int PCode
        public Int PathBegin
        public Int PathCurve
        public Int PathEnd
        public Int PathRadiusOffset
        public Int PathRevolutions
        public Int PathScaleX
        public Int PathScaleY
        public Int PathShearX
        public Int PathShearY
        public Int PathSkew
        public Int PathTaperX
        public Int PathTaperY
        public Int PathTwist
        public Int PathTwistBegin
        public Int ProfileBegin
        public Int ProfileCurve
        public Int ProfileEnd
        public Int ProfileHollow
        public LLVector3 RayEnd
        public Int RayEndIsIntersection
        public LLVector3 RayStart
        public UUID RayTargetID
        public LLQuaternion Rotation
        public LLVector3 Scale
        public Int State
    }

    public ObjectAdd() {
        this.zeroCoded = true
    }

    public Int CalcPayloadSize() {
        return 146
    }

    fun Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectAdd(this)
    }

    fun PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 1)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PCode)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.Material)
        packInt(byteBuffer, this.ObjectData_Field.AddFlags)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathCurve)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.ProfileCurve)
        packShort(byteBuffer, (Short) this.ObjectData_Field.PathBegin)
        packShort(byteBuffer, (Short) this.ObjectData_Field.PathEnd)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathScaleX)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathScaleY)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathShearX)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathShearY)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathTwist)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathTwistBegin)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathRadiusOffset)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathTaperX)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathTaperY)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathRevolutions)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.PathSkew)
        packShort(byteBuffer, (Short) this.ObjectData_Field.ProfileBegin)
        packShort(byteBuffer, (Short) this.ObjectData_Field.ProfileEnd)
        packShort(byteBuffer, (Short) this.ObjectData_Field.ProfileHollow)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.BypassRaycast)
        packLLVector3(byteBuffer, this.ObjectData_Field.RayStart)
        packLLVector3(byteBuffer, this.ObjectData_Field.RayEnd)
        packUUID(byteBuffer, this.ObjectData_Field.RayTargetID)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.RayEndIsIntersection)
        packLLVector3(byteBuffer, this.ObjectData_Field.Scale)
        packLLQuaternion(byteBuffer, this.ObjectData_Field.Rotation)
        packByte(byteBuffer, (Byte) this.ObjectData_Field.State)
    }

    fun UnpackPayload(ByteBuffer byteBuffer) {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        this.AgentData_Field.GroupID = unpackUUID(byteBuffer)
        this.ObjectData_Field.PCode = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.Material = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.AddFlags = unpackInt(byteBuffer)
        this.ObjectData_Field.PathCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.ProfileCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.PathBegin = unpackShort(byteBuffer) & 65535
        this.ObjectData_Field.PathEnd = unpackShort(byteBuffer) & 65535
        this.ObjectData_Field.PathScaleX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.PathScaleY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.PathShearX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.PathShearY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.PathTwist = unpackByte(byteBuffer)
        this.ObjectData_Field.PathTwistBegin = unpackByte(byteBuffer)
        this.ObjectData_Field.PathRadiusOffset = unpackByte(byteBuffer)
        this.ObjectData_Field.PathTaperX = unpackByte(byteBuffer)
        this.ObjectData_Field.PathTaperY = unpackByte(byteBuffer)
        this.ObjectData_Field.PathRevolutions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.PathSkew = unpackByte(byteBuffer)
        this.ObjectData_Field.ProfileBegin = unpackShort(byteBuffer) & 65535
        this.ObjectData_Field.ProfileEnd = unpackShort(byteBuffer) & 65535
        this.ObjectData_Field.ProfileHollow = unpackShort(byteBuffer) & 65535
        this.ObjectData_Field.BypassRaycast = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.RayStart = unpackLLVector3(byteBuffer)
        this.ObjectData_Field.RayEnd = unpackLLVector3(byteBuffer)
        this.ObjectData_Field.RayTargetID = unpackUUID(byteBuffer)
        this.ObjectData_Field.RayEndIsIntersection = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
        this.ObjectData_Field.Scale = unpackLLVector3(byteBuffer)
        this.ObjectData_Field.Rotation = unpackLLQuaternion(byteBuffer)
        this.ObjectData_Field.State = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
    }
}
