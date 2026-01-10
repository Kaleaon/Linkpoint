package com.linkpoint.slproto.prims

import com.linkpoint.Debug
import com.linkpoint.slproto.messages.ObjectUpdate
import com.linkpoint.utils.UUIDPool
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class PrimVolumeParams {
    val LL_SCULPT_FLAG_INVERT: Byte = 64
    val LL_SCULPT_FLAG_MIRROR: Byte = Byte.MIN_VALUE
    val LL_SCULPT_TYPE_CYLINDER: Byte = 4
    val LL_SCULPT_TYPE_MASK: Byte = 7
    val LL_SCULPT_TYPE_MESH: Byte = 5
    val LL_SCULPT_TYPE_NONE: Byte = 0
    val LL_SCULPT_TYPE_PLANE: Byte = 3
    val LL_SCULPT_TYPE_SPHERE: Byte = 1
    val LL_SCULPT_TYPE_TORUS: Byte = 2
    val PARAMS_FLEXIBLE: Short = 16
    val PARAMS_LIGHT: Short = 32
    val PARAMS_LIGHT_IMAGE: Short = 64
    val PARAMS_MESH: Short = 96
    val PARAMS_RESERVED: Short = 80
    val PARAMS_SCULPT: Short = 48
    PrimFlexibleParams FlexiParams
    PrimPathParams PathParams
    PrimProfileParams ProfileParams
    UUID SculptID
    Byte SculptType

    fun createFromObjectUpdate(ObjectUpdate.ObjectData objectData): PrimVolumeParams {
        PrimVolumeParams primVolumeParams = PrimVolumeParams()
        primVolumeParams.PathParams = PrimParamsPool.get(PrimPathParams(objectData))
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromObjectUpdate(objectData))
        return primVolumeParams
    }

    fun createFromPackedData(ByteBuffer byteBuffer): PrimVolumeParams {
        PrimVolumeParams primVolumeParams = PrimVolumeParams()
        primVolumeParams.PathParams = PrimParamsPool.get(PrimPathParams(byteBuffer))
        primVolumeParams.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromPackedData(byteBuffer))
        return primVolumeParams
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is PrimVolumeParams)) {
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

    fun hashCode(): Int {
        var i: Int = (this.SculptType * 17) + 0
        if (this.SculptID != null) {
            i += this.SculptID.hashCode() * 3
        }
        var hashCode: Int = i + (this.PathParams.hashCode() * 37) + this.ProfileParams.hashCode()
        return this.FlexiParams != null ? hashCode + this.FlexiParams.hashCode() : hashCode
    }

    fun isFlexible(): Boolean {
        return this.FlexiParams != null
    }

    fun isMesh(): Boolean {
        return this.SculptID != null && (this.SculptType & 7) == 5
    }

    fun isSculpt(): Boolean {
        return this.SculptID != null
    }

    fun toString(): String {
        return "{Volume: SculptType 0x" + Int.toHexString(this.SculptType) + ", SculptID " + (this.SculptID != null ? this.SculptID.toString() : "null") + ", Path = (" + this.PathParams.toString() + "), Profile = (" + this.ProfileParams.toString() + ")}"
    }

    fun unpackExtraParams(ByteBuffer byteBuffer)  {
        try {
            Byte b = byteBuffer.get()
            for (i in 0 until b) {
                Short s = byteBuffer.getShort()
                var i2: Int = byteBuffer.getInt() + byteBuffer.position()
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
