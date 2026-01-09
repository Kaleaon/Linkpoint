package com.linkpoint.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.SLMessage
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer
import java.util.ArrayList
import java.util.Iterator
import java.util.UUID

class ObjectUpdate : SLMessage {
    ArrayList<ObjectData> ObjectData_Fields = ArrayList<>()
    RegionData RegionData_Field

    class ObjectData {
        Int CRC
        Int ClickAction
        ByteArray Data
        ByteArray ExtraParams
        Int Flags
        UUID FullID
        Float Gain
        Int ID
        LLVector3 JointAxisOrAnchor
        LLVector3 JointPivot
        Int JointType
        Int Material
        ByteArray MediaURL
        ByteArray NameValue
        ByteArray ObjectData
        UUID OwnerID
        Int PCode
        ByteArray PSBlock
        Int ParentID
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
        Float Radius
        LLVector3 Scale
        UUID Sound
        Int State
        ByteArray Text
        ByteArray TextColor
        ByteArray TextureAnim
        ByteArray TextureEntry
        Int UpdateFlags
    }

    class RegionData {
        Long RegionHandle
        Int TimeDilation
    }

    ObjectUpdate() {
        this.zeroCoded = true
        this.RegionData_Field = RegionData()
    }

    fun CalcPayloadSize(): Int {
        var i: Int = 12
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            var i2: Int = i
            if (!it.hasNext()) {
                return i2
            }
            ObjectData objectData = (it as ObjectData).next()
            i = objectData.ExtraParams.size + objectData.ObjectData.size + 41 + 4 + 4 + 1 + 1 + 2 + 2 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 2 + 2 + 2 + 2 + objectData.TextureEntry.size + 1 + objectData.TextureAnim.size + 2 + objectData.NameValue.size + 2 + objectData.Data.size + 1 + objectData.Text.size + 4 + 1 + objectData.MediaURL.size + 1 + objectData.PSBlock.size + 1 + 16 + 16 + 4 + 1 + 4 + 1 + 12 + 12 + i2
        }
    }

    fun Handle(SLMessageHandler sLMessageHandler)  {
        sLMessageHandler.HandleObjectUpdate(this)
    }

    fun PackPayload(ByteBuffer byteBuffer)  {
        byteBuffer.put(Ascii.FF)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (this as Short).RegionData_Field.TimeDilation)
        byteBuffer.put((this as Byte).ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID)
            packByte(byteBuffer, (objectData as Byte).State)
            packUUID(byteBuffer, objectData.FullID)
            packInt(byteBuffer, objectData.CRC)
            packByte(byteBuffer, (objectData as Byte).PCode)
            packByte(byteBuffer, (objectData as Byte).Material)
            packByte(byteBuffer, (objectData as Byte).ClickAction)
            packLLVector3(byteBuffer, objectData.Scale)
            packVariable(byteBuffer, objectData.ObjectData, 1)
            packInt(byteBuffer, objectData.ParentID)
            packInt(byteBuffer, objectData.UpdateFlags)
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
            packVariable(byteBuffer, objectData.TextureEntry, 2)
            packVariable(byteBuffer, objectData.TextureAnim, 1)
            packVariable(byteBuffer, objectData.NameValue, 2)
            packVariable(byteBuffer, objectData.Data, 2)
            packVariable(byteBuffer, objectData.Text, 1)
            packFixed(byteBuffer, objectData.TextColor, 4)
            packVariable(byteBuffer, objectData.MediaURL, 1)
            packVariable(byteBuffer, objectData.PSBlock, 1)
            packVariable(byteBuffer, objectData.ExtraParams, 1)
            packUUID(byteBuffer, objectData.Sound)
            packUUID(byteBuffer, objectData.OwnerID)
            packFloat(byteBuffer, objectData.Gain)
            packByte(byteBuffer, (objectData as Byte).Flags)
            packFloat(byteBuffer, objectData.Radius)
            packByte(byteBuffer, (objectData as Byte).JointType)
            packLLVector3(byteBuffer, objectData.JointPivot)
            packLLVector3(byteBuffer, objectData.JointAxisOrAnchor)
        }
    }

    fun UnpackPayload(ByteBuffer byteBuffer)  {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (i in 0 until b) {
            ObjectData objectData = ObjectData()
            objectData.ID = unpackInt(byteBuffer)
            objectData.State = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.FullID = unpackUUID(byteBuffer)
            objectData.CRC = unpackInt(byteBuffer)
            objectData.PCode = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Material = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.ClickAction = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Scale = unpackLLVector3(byteBuffer)
            objectData.ObjectData = unpackVariable(byteBuffer, 1)
            objectData.ParentID = unpackInt(byteBuffer)
            objectData.UpdateFlags = unpackInt(byteBuffer)
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
            objectData.TextureEntry = unpackVariable(byteBuffer, 2)
            objectData.TextureAnim = unpackVariable(byteBuffer, 1)
            objectData.NameValue = unpackVariable(byteBuffer, 2)
            objectData.Data = unpackVariable(byteBuffer, 2)
            objectData.Text = unpackVariable(byteBuffer, 1)
            objectData.TextColor = unpackFixed(byteBuffer, 4)
            objectData.MediaURL = unpackVariable(byteBuffer, 1)
            objectData.PSBlock = unpackVariable(byteBuffer, 1)
            objectData.ExtraParams = unpackVariable(byteBuffer, 1)
            objectData.Sound = unpackUUID(byteBuffer)
            objectData.OwnerID = unpackUUID(byteBuffer)
            objectData.Gain = unpackFloat(byteBuffer)
            objectData.Flags = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.Radius = unpackFloat(byteBuffer)
            objectData.JointType = unpackByte(byteBuffer) & UnsignedBytes.MAX_VALUE
            objectData.JointPivot = unpackLLVector3(byteBuffer)
            objectData.JointAxisOrAnchor = unpackLLVector3(byteBuffer)
            this.ObjectData_Fields.add(objectData)
        }
    }
}
