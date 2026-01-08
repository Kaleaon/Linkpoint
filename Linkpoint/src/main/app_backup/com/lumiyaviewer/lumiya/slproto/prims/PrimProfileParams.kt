package com.lumiyaviewer.lumiya.slproto.prims

import androidx.v4.internal.view.SupportMenu
import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate
import java.nio.ByteBuffer

class PrimProfileParams(
    var CurveType: Byte,
    var Begin: Float,
    var End: Float,
    var Hollow: Float
) {
    companion object {
        const val CUT_QUANTA: Float = 2.0E-5f
        const val HOLLOW_QUANTA: Float = 2.0E-5f
        
        const val LL_PCODE_HOLE_CIRCLE: Byte = 16
        const val LL_PCODE_HOLE_MASK: Byte = -16
        const val LL_PCODE_HOLE_SAME: Byte = 0
        const val LL_PCODE_HOLE_SQUARE: Byte = 32
        const val LL_PCODE_HOLE_TRIANGLE: Byte = 48
        
        const val LL_PCODE_PROFILE_CIRCLE: Byte = 0
        const val LL_PCODE_PROFILE_CIRCLE_HALF: Byte = 5
        const val LL_PCODE_PROFILE_EQUALTRI: Byte = 3
        const val LL_PCODE_PROFILE_ISOTRI: Byte = 2
        const val LL_PCODE_PROFILE_MASK: Byte = 15
        const val LL_PCODE_PROFILE_RIGHTTRI: Byte = 4
        const val LL_PCODE_PROFILE_SQUARE: Byte = 1

        fun createFromObjectUpdate(objectData: ObjectUpdate.ObjectData): PrimProfileParams {
            return PrimProfileParams(
                objectData.ProfileCurve.toByte(),
                (objectData.ProfileBegin.toInt() and 0xFFFF) * CUT_QUANTA,
                1.0f - ((objectData.ProfileEnd.toInt() and 0xFFFF) * CUT_QUANTA),
                (objectData.ProfileHollow.toInt() and 0xFFFF) * HOLLOW_QUANTA
            )
        }

        fun createFromPackedData(byteBuffer: ByteBuffer): PrimProfileParams {
            return PrimProfileParams(
                byteBuffer.get(),
                (byteBuffer.short.toInt() and 0xFFFF) * CUT_QUANTA,
                1.0f - ((byteBuffer.short.toInt() and 0xFFFF) * CUT_QUANTA),
                (byteBuffer.short.toInt() and 0xFFFF) * HOLLOW_QUANTA
            )
        }
    }

    private val hashValue: Int = calcHashValue()

    private fun calcHashValue(): Int {
        return (CurveType * 17) + java.lang.Float.floatToIntBits(Begin) + 
               java.lang.Float.floatToIntBits(End) + java.lang.Float.floatToIntBits(Hollow)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrimProfileParams) return false
        return CurveType == other.CurveType && Begin == other.Begin && End == other.End && Hollow == other.Hollow
    }

    override fun hashCode(): Int {
        return hashValue
    }

    override fun toString(): String {
        return String.format("CurveType: 0x%02x, Begin: %f, End: %f, Hollow: %f", CurveType, Begin, End, Hollow)
    }
}
