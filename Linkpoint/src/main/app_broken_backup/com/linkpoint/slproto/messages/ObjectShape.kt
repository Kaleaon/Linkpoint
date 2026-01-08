package com.linkpoint.slproto.messages

import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.UUID

class ObjectShape : SLMessage {
    AgentData AgentData_Field
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()

    class AgentData {
        UUID AgentID
        UUID SessionID
    }

    class ObjectData {
        Int ObjectLocalID
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
    }

    ObjectShape() {
        this.zeroCoded = true
        this.AgentData_Field = AgentData()
    }

    fun CalcPayloadSize(): Int {
        return (this.ObjectData_Fields.size() * 27) + 37
    }

    fun Handle(SLMessageHandler sLMessageHandler): Unit {
        sLMessageHandler.HandleObjectShape(this)
    }

    fun PackPayload(ByteBuffer byteBuffer): Unit {
        byteBuffer.putShort(-1)
        byteBuffer.put((Byte) 0)
        byteBuffer.put((Byte) 98)
        packUUID(byteBuffer, this.AgentData_Field.AgentID)
        packUUID(byteBuffer, this.AgentData_Field.SessionID)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ObjectLocalID)
            packByte(byteBuffer, (objectData as Byte).PathCurve)
            packByte(byteBuffer, (objectData as Byte).ProfileCurve)
            packShort(byteBuffer, (objectData as Short).PathBegin)
            packShort(byteBuffer, (objectData as Short).PathEnd)
            packByte(byteBuffer, (objectData as Byte).PathScaleX)
            packByte(byteBuffer, (objectData as Byte).PathScaleY)
            packByte(byteBuffer, (objectData as Byte).PathShearX)
            packByte(byteBuffer, (objectData as Byte).PathShearY)
            packByte(byteBuffer, (objectData as Byte).PathTwist)
            packByte(byteBuffer, (objectData as Byte).PathTwistBegin)
            packByte(byteBuffer, (objectData as Byte).PathRadiusOffset)
            packByte(byteBuffer, (objectData as Byte).PathTaperX)
            packByte(byteBuffer, (objectData as Byte).PathTaperY)
            packByte(byteBuffer, (objectData as Byte).PathRevolutions)
            packByte(byteBuffer, (objectData as Byte).PathSkew)
            packShort(byteBuffer, (objectData as Short).ProfileBegin)
            packShort(byteBuffer, (objectData as Short).ProfileEnd)
            packShort(byteBuffer, (objectData as Short).ProfileHollow)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer): Unit {
        this.AgentData_Field.AgentID = unpackUUID(byteBuffer)
        this.AgentData_Field.SessionID = unpackUUID(byteBuffer)
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ObjectLocalID = unpackInt(byteBuffer)
            objectData.PathCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.ProfileCurve = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathBegin = unpackShort(byteBuffer) & 65535
            objectData.PathEnd = unpackShort(byteBuffer) & 65535
            objectData.PathScaleX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathScaleY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathShearX = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathShearY = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathTwist = unpackByte(byteBuffer)
            objectData.PathTwistBegin = unpackByte(byteBuffer)
            objectData.PathRadiusOffset = unpackByte(byteBuffer)
            objectData.PathTaperX = unpackByte(byteBuffer)
            objectData.PathTaperY = unpackByte(byteBuffer)
            objectData.PathRevolutions = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.PathSkew = unpackByte(byteBuffer)
            objectData.ProfileBegin = unpackShort(byteBuffer) & 65535
            objectData.ProfileEnd = unpackShort(byteBuffer) & 65535
            objectData.ProfileHollow = unpackShort(byteBuffer) & 65535
            this.ObjectData_Fields.add(objectData)
        }
    }
}
