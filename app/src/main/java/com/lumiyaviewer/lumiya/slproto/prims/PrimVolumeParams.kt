package com.lumiyaviewer.lumiya.slproto.prims

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class PrimVolumeParams {
    Byte LL_SCULPT_FLAG_INVERT = 64
    Byte LL_SCULPT_FLAG_MIRROR = Byte.MIN_VALUE
    Byte LL_SCULPT_TYPE_CYLINDER = 4
    Byte LL_SCULPT_TYPE_MASK = 7
    Byte LL_SCULPT_TYPE_MESH = 5
    Byte LL_SCULPT_TYPE_NONE = 0
    Byte LL_SCULPT_TYPE_PLANE = 3
    Byte LL_SCULPT_TYPE_SPHERE = 1
    Byte LL_SCULPT_TYPE_TORUS = 2
    Short PARAMS_FLEXIBLE = 16
    Short PARAMS_LIGHT = 32
    Short PARAMS_LIGHT_IMAGE = 64
    Short PARAMS_MESH = 96
    Short PARAMS_RESERVED = 80
    Short PARAMS_SCULPT = 48
    PrimFlexibleParams FlexiParams
    PrimPathParams PathParams
    PrimProfileParams ProfileParams
    UUID SculptID
    Byte SculptType

    PrimVolumeParams createFromObjectUpdate(ObjectUpdate.ObjectData objectData) {
        PrimVolumeParams primVolumeParams = PrimVolumeParams()
        primVolumeParams.PathParams = PrimParamsPool.get(PrimPathParams(objectData))
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromObjectUpdate(objectData))
        return primVolumeParams
    }

    PrimVolumeParams createFromPackedData(ByteBuffer byteBuffer) {
        PrimVolumeParams primVolumeParams = PrimVolumeParams()
        primVolumeParams.PathParams = PrimParamsPool.get(PrimPathParams(byteBuffer))
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromPackedData(byteBuffer))
        return primVolumeParams
    }

    Boolean equals(Any obj) {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof PrimVolumeParams)) {
            return false
        }
        PrimVolumeParams primVolumeParams = (PrimVolumeParams) obj
        if (this.SculptType != primVolumeParams.SculptType) {
            return false
        }
        if ((this.SculptID == null) != (primVolumeParams.SculptID == null)) {
            return false
        }
        if (this.SculptID != null && !this.SculptID.equals(primVolumeParams.SculptID)) {
            return false
        }
        if ((this.ProfileParams == null) != (primVolumeParams.ProfileParams == null)) {
            return false
        }
        if (this.ProfileParams != null && !this.ProfileParams.equals(primVolumeParams.ProfileParams)) {
            return false
        }
        if ((this.PathParams == null) != (primVolumeParams.PathParams == null)) {
            return false
        }
        if (this.PathParams != null && !this.PathParams.equals(primVolumeParams.PathParams)) {
            return false
        }
        if ((this.FlexiParams == null) != (primVolumeParams.FlexiParams == null)) {
            return false
        }
        return this.FlexiParams == null || this.FlexiParams.equals(primVolumeParams.FlexiParams)
    }

    Int hashCode() {
        Int i = (this.SculptType * 17) + 0
        if (this.SculptID != null) {
            i += this.SculptID.hashCode() * 3
        }
        Int hashCode = i + (this.PathParams.hashCode() * 37) + this.ProfileParams.hashCode()
        return this.FlexiParams != null ? hashCode + this.FlexiParams.hashCode() : hashCode
    }

    Boolean isFlexible() {
        return this.FlexiParams != null
    }

    Boolean isMesh() {
        return this.SculptID != null && (this.SculptType & 7) == 5
    }

    Boolean isSculpt() {
        return this.SculptID != null
    }

    String toString() {
        return "{Volume: SculptType 0x" + Int.toHexString(this.SculptType) + ", SculptID " + (this.SculptID != null ? this.SculptID.toString() : "null") + ", Path = (" + this.PathParams.toString() + "), Profile = (" + this.ProfileParams.toString() + ")}"
    }

    Unit unpackExtraParams(ByteBuffer byteBuffer) {
        try {
            Byte b = byteBuffer.get()
            for (Int i = 0; i < b; i++) {
                Short s = byteBuffer.getShort()
                Int i2 = byteBuffer.getInt() + byteBuffer.position()
                switch (s) {
                    case 16:
                        this.FlexiParams = PrimFlexibleParams(byteBuffer, i2)
                        break
                    case 48:
                    case 96:
                        byteBuffer.order(ByteOrder.BIG_ENDIAN)
                        this.SculptID = UUIDPool.getUUID(UUID(byteBuffer.getLong(), byteBuffer.getLong()))
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                        this.SculptType = byteBuffer.get()
                        break
                }
                byteBuffer.position(i2)
            }
        } catch (BufferUnderflowException e) {
            Debug.Warning(e)
        }
    }
}
