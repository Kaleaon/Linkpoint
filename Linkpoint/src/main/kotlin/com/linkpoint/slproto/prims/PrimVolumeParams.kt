package com.linkpoint.slproto.prims

import com.linkpoint.Debug
import com.linkpoint.slproto.messages.ObjectUpdate
import com.linkpoint.utils.UUIDPool
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class PrimVolumeParams {
    const val Byte LL_SCULPT_FLAG_INVERT = 64
    const val Byte LL_SCULPT_FLAG_MIRROR = Byte.MIN_VALUE
    const val Byte LL_SCULPT_TYPE_CYLINDER = 4
    const val Byte LL_SCULPT_TYPE_MASK = 7
    const val Byte LL_SCULPT_TYPE_MESH = 5
    const val Byte LL_SCULPT_TYPE_NONE = 0
    const val Byte LL_SCULPT_TYPE_PLANE = 3
    const val Byte LL_SCULPT_TYPE_SPHERE = 1
    const val Byte LL_SCULPT_TYPE_TORUS = 2
    const val Short PARAMS_FLEXIBLE = 16
    const val Short PARAMS_LIGHT = 32
    const val Short PARAMS_LIGHT_IMAGE = 64
    const val Short PARAMS_MESH = 96
    const val Short PARAMS_RESERVED = 80
    const val Short PARAMS_SCULPT = 48
    public PrimFlexibleParams FlexiParams
    public PrimPathParams PathParams
    public PrimProfileParams ProfileParams
    public UUID SculptID
    public Byte SculptType

    @JvmStatic
    PrimVolumeParams createFromObjectUpdate(ObjectUpdate.ObjectData objectData) {
        PrimVolumeParams primVolumeParams = PrimVolumeParams()
        primVolumeParams.PathParams = PrimParamsPool.get(PrimPathParams(objectData))
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromObjectUpdate(objectData))
        return primVolumeParams
    }

    @JvmStatic
    PrimVolumeParams createFromPackedData(ByteBuffer byteBuffer) {
        PrimVolumeParams primVolumeParams = PrimVolumeParams()
        primVolumeParams.PathParams = PrimParamsPool.get(PrimPathParams(byteBuffer))
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromPackedData(byteBuffer))
        return primVolumeParams
    }

    public Boolean equals(Object obj) {
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

    public Int hashCode() {
        Int i = (this.SculptType * 17) + 0
        if (this.SculptID != null) {
            i += this.SculptID.hashCode() * 3
        }
        Int hashCode = i + (this.PathParams.hashCode() * 37) + this.ProfileParams.hashCode()
        return this.FlexiParams != null ? hashCode + this.FlexiParams.hashCode() : hashCode
    }

    public Boolean isFlexible() {
        return this.FlexiParams != null
    }

    public Boolean isMesh() {
        return this.SculptID != null && (this.SculptType & 7) == 5
    }

    public Boolean isSculpt() {
        return this.SculptID != null
    }

    public String toString() {
        return "{Volume: SculptType 0x" + Integer.toHexString(this.SculptType) + ", SculptID " + (this.SculptID != null ? this.SculptID.toString() : "null") + ", Path = (" + this.PathParams.toString() + "), Profile = (" + this.ProfileParams.toString() + ")}"
    }

    fun unpackExtraParams(ByteBuffer byteBuffer) {
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
