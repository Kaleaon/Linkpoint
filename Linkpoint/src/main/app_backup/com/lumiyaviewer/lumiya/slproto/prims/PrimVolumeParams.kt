package com.lumiyaviewer.lumiya.slproto.prims

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate
import com.lumiyaviewer.lumiya.utils.UUIDPool
import java.nio.BufferUnderflowException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID

class PrimVolumeParams {
    companion object {
        const val LL_SCULPT_FLAG_INVERT: Byte = 64
        const val LL_SCULPT_FLAG_MIRROR: Byte = Byte.MIN_VALUE
        const val LL_SCULPT_TYPE_CYLINDER: Byte = 4
        const val LL_SCULPT_TYPE_MASK: Byte = 7
        const val LL_SCULPT_TYPE_MESH: Byte = 5
        const val LL_SCULPT_TYPE_NONE: Byte = 0
        const val LL_SCULPT_TYPE_PLANE: Byte = 3
        const val LL_SCULPT_TYPE_SPHERE: Byte = 1
        const val LL_SCULPT_TYPE_TORUS: Byte = 2
        const val PARAMS_FLEXIBLE: Short = 16
        const val PARAMS_LIGHT: Short = 32
        const val PARAMS_LIGHT_IMAGE: Short = 64
        const val PARAMS_MESH: Short = 96
        const val PARAMS_RESERVED: Short = 80
        const val PARAMS_SCULPT: Short = 48

        fun createFromObjectUpdate(objectData: ObjectUpdate.ObjectData): PrimVolumeParams {
            val params = PrimVolumeParams()
            params.PathParams = PrimParamsPool.get(PrimPathParams(objectData))
            params.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromObjectUpdate(objectData))
            return params
        }

        fun createFromPackedData(byteBuffer: ByteBuffer): PrimVolumeParams {
            val params = PrimVolumeParams()
            params.PathParams = PrimParamsPool.get(PrimPathParams(byteBuffer))
            params.ProfileParams = PrimParamsPool.get(PrimProfileParams.createFromPackedData(byteBuffer))
            return params
        }
    }

    var FlexiParams: PrimFlexibleParams? = null
    var PathParams: PrimPathParams? = null
    var ProfileParams: PrimProfileParams? = null
    var SculptID: UUID? = null
    var SculptType: Byte = 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrimVolumeParams) return false
        
        if (SculptType != other.SculptType) return false
        if (SculptID != other.SculptID) return false
        
        if (ProfileParams != other.ProfileParams) return false
        if (PathParams != other.PathParams) return false
        
        return FlexiParams == other.FlexiParams
    }

    override fun hashCode(): Int {
        var result = (SculptType * 17).toInt()
        result += (SculptID?.hashCode() ?: 0) * 3
        result += (PathParams?.hashCode() ?: 0) * 37
        result += (ProfileParams?.hashCode() ?: 0)
        result += FlexiParams?.hashCode() ?: 0
        return result
    }

    fun isFlexible(): Boolean = FlexiParams != null

    fun isMesh(): Boolean {
        return SculptID != null && (SculptType.toInt() and 7) == 5
    }

    fun isSculpt(): Boolean {
        return SculptID != null
    }

    override fun toString(): String {
        return "{Volume: SculptType 0x${Integer.toHexString(SculptType.toInt())}, SculptID ${SculptID ?: "null"}, Path = ($PathParams), Profile = ($ProfileParams)}"
    }

    fun unpackExtraParams(byteBuffer: ByteBuffer) {
        try {
            val count = byteBuffer.get().toInt()
            for (i in 0 until count) {
                val type = byteBuffer.short.toInt()
                val nextPos = byteBuffer.int + byteBuffer.position()
                
                when (type) {
                    16 -> FlexiParams = PrimFlexibleParams(byteBuffer, nextPos)
                    48, 96 -> {
                        byteBuffer.order(ByteOrder.BIG_ENDIAN)
                        val msb = byteBuffer.long
                        val lsb = byteBuffer.long
                        SculptID = UUIDPool.getUUID(msb, lsb)
                        byteBuffer.order(ByteOrder.LITTLE_ENDIAN)
                        SculptType = byteBuffer.get()
                    }
                }
                byteBuffer.position(nextPos)
            }
        } catch (e: BufferUnderflowException) {
            Debug.Warning(e)
        }
    }
}
