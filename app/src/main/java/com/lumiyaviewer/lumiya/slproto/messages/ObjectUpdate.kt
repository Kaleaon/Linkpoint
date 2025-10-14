package com.lumiyaviewer.lumiya.slproto.messages

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.lumiyaviewer.lumiya.slproto.SLMessage
import com.lumiyaviewer.lumiya.slproto.types.LLVector3
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
        Byte[] Data
        Byte[] ExtraParams
        Int Flags
        UUID FullID
        Float Gain
        Int ID
        LLVector3 JointAxisOrAnchor
        LLVector3 JointPivot
        Int JointType
        Int Material
        Byte[] MediaURL
        Byte[] NameValue
        Byte[] ObjectData
        UUID OwnerID
        Int PCode
        Byte[] PSBlock
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
        Byte[] Text
        Byte[] TextColor
        Byte[] TextureAnim
        Byte[] TextureEntry
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

    Int CalcPayloadSize() {
        Int i = 12
        Iterator<T> it = this.ObjectData_Fields.iterator()
        while (true) {
            Int i2 = i
            if (!it.hasNext()) {
                return i2
            }
            ObjectData objectData = (ObjectData) it.next()
            i = objectData.ExtraParams.length + objectData.ObjectData.length + 41 + 4 + 4 + 1 + 1 + 2 + 2 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 1 + 2 + 2 + 2 + 2 + objectData.TextureEntry.length + 1 + objectData.TextureAnim.length + 2 + objectData.NameValue.length + 2 + objectData.Data.length + 1 + objectData.Text.length + 4 + 1 + objectData.MediaURL.length + 1 + objectData.PSBlock.length + 1 + 16 + 16 + 4 + 1 + 4 + 1 + 12 + 12 + i2
        }
    }

    Unit Handle(SLMessageHandler sLMessageHandler) {
        sLMessageHandler.HandleObjectUpdate(this)
    }

    Unit PackPayload(ByteBuffer byteBuffer) {
        byteBuffer.put(Ascii.FF)
        packLong(byteBuffer, this.RegionData_Field.RegionHandle)
        packShort(byteBuffer, (Short) this.RegionData_Field.TimeDilation)
        byteBuffer.put((Byte) this.ObjectData_Fields.size())
        for (ObjectData objectData : this.ObjectData_Fields) {
            packInt(byteBuffer, objectData.ID)
            packByte(byteBuffer, (Byte) objectData.State)
            packUUID(byteBuffer, objectData.FullID)
            packInt(byteBuffer, objectData.CRC)
            packByte(byteBuffer, (Byte) objectData.PCode)
            packByte(byteBuffer, (Byte) objectData.Material)
            packByte(byteBuffer, (Byte) objectData.ClickAction)
            packLLVector3(byteBuffer, objectData.Scale)
            packVariable(byteBuffer, objectData.ObjectData, 1)
            packInt(byteBuffer, objectData.ParentID)
            packInt(byteBuffer, objectData.UpdateFlags)
            packByte(byteBuffer, (Byte) objectData.PathCurve)
            packByte(byteBuffer, (Byte) objectData.ProfileCurve)
            packShort(byteBuffer, (Short) objectData.PathBegin)
            packShort(byteBuffer, (Short) objectData.PathEnd)
            packByte(byteBuffer, (Byte) objectData.PathScaleX)
            packByte(byteBuffer, (Byte) objectData.PathScaleY)
            packByte(byteBuffer, (Byte) objectData.PathShearX)
            packByte(byteBuffer, (Byte) objectData.PathShearY)
            packByte(byteBuffer, (Byte) objectData.PathTwist)
            packByte(byteBuffer, (Byte) objectData.PathTwistBegin)
            packByte(byteBuffer, (Byte) objectData.PathRadiusOffset)
            packByte(byteBuffer, (Byte) objectData.PathTaperX)
            packByte(byteBuffer, (Byte) objectData.PathTaperY)
            packByte(byteBuffer, (Byte) objectData.PathRevolutions)
            packByte(byteBuffer, (Byte) objectData.PathSkew)
            packShort(byteBuffer, (Short) objectData.ProfileBegin)
            packShort(byteBuffer, (Short) objectData.ProfileEnd)
            packShort(byteBuffer, (Short) objectData.ProfileHollow)
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
            packByte(byteBuffer, (Byte) objectData.Flags)
            packFloat(byteBuffer, objectData.Radius)
            packByte(byteBuffer, (Byte) objectData.JointType)
            packLLVector3(byteBuffer, objectData.JointPivot)
            packLLVector3(byteBuffer, objectData.JointAxisOrAnchor)
        }
    }

    Unit UnpackPayload(ByteBuffer byteBuffer) {
        this.RegionData_Field.RegionHandle = unpackLong(byteBuffer)
        this.RegionData_Field.TimeDilation = unpackShort(byteBuffer) & 65535
        Byte b = byteBuffer.get() & UnsignedBytes.MAX_VALUE
        for (Int i = 0; i < b; i++) {
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
