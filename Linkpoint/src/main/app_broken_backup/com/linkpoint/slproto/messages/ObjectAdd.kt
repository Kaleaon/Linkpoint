package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLQuaternion
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.UUID

class ObjectAdd : SLMessage {
    AgentData AgentData_Field = AgentData()
    ObjectData ObjectData_Field = ObjectData()

    class AgentData {
        UUID AgentID
        UUID GroupID
        UUID SessionID
    }

    class ObjectData {
        Int AddFlags
        Int BypassRaycast
        Int Material
        Int PCode
        Int PathBegin
        Int PathCurve
        Int PathEnd
        Int PathRadiusOffset
        Int PathRevolutions
        Int PathScaleX
        Int PathScaleY
        Int PathShearX
        Int PathShearY
        Int PathSkew
        Int PathTaperX
        Int PathTaperY
        Int PathTwist
        Int PathTwistBegin
        Int ProfileBegin
        Int ProfileCurve
        Int ProfileEnd
        Int ProfileHollow
        LLVector3 RayEnd
        Int RayEndIsIntersection
        LLVector3 RayStart
        UUID RayTargetID
        LLQuaternion Rotation
        LLVector3 Scale
        Int State
    }

    ObjectAdd() {
        this.zeroCoded = true
    }

    fun CalcPayloadSize(): Int {
        return 146
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleObjectAdd(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put((Byte) -1)
        byteBuffer.put((Byte) 1)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        packUUID(byteBuffer, this.AgentData_Field.GroupID)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PCode)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.Material)
        packInt(byteBuffer, this.ObjectData_Field.AddFlags)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathCurve)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.ProfileCurve)
        packShort(byteBuffer, (this as Short).ObjectData_Field.PathBegin)
        packShort(byteBuffer, (this as Short).ObjectData_Field.PathEnd)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathScaleX)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathScaleY)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathShearX)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathShearY)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathTwist)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathTwistBegin)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathRadiusOffset)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathTaperX)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathTaperY)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathRevolutions)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.PathSkew)
        packShort(byteBuffer, (this as Short).ObjectData_Field.ProfileBegin)
        packShort(byteBuffer, (this as Short).ObjectData_Field.ProfileEnd)
        packShort(byteBuffer, (this as Short).ObjectData_Field.ProfileHollow)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.BypassRaycast)
        packLLVector3(byteBuffer, this.ObjectData_Field.RayStart)
        packLLVector3(byteBuffer, this.ObjectData_Field.RayEnd)
        packUUID(byteBuffer, this.ObjectData_Field.RayTargetID)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.RayEndIsIntersection)
        packLLVector3(byteBuffer, this.ObjectData_Field.Scale)
        packLLQuaternion(byteBuffer, this.ObjectData_Field.Rotation)
        packByte(byteBuffer, (this as Byte).ObjectData_Field.State)
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
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
