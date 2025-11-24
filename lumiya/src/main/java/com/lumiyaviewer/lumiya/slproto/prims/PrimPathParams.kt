package com.lumiyaviewer.lumiya.slproto.prims

import com.lumiyaviewer.lumiya.slproto.messages.ObjectUpdate
import com.lumiyaviewer.lumiya.slproto.types.LLTersePacking
import com.lumiyaviewer.lumiya.slproto.types.LLVector2
import java.nio.ByteBuffer

class PrimPathParams {
    var begin: Float = 0f
    var curveType: Byte = 0
    var end: Float = 0f
    var radiusOffset: Float = 0f
    var revolutions: Float = 0f
    var scaleX: Float = 0f
    var scaleY: Float = 0f
    var shearX: Float = 0f
    var shearY: Float = 0f
    var skew: Float = 0f
    var taperX: Float = 0f
    var taperY: Float = 0f
    var twistBegin: Float = 0f
    var twistEnd: Float = 0f
    
    private var hashValue: Int = 0

    companion object {
        const val CUT_QUANTA: Float = 2.0E-5f
        const val LL_PCODE_PATH_CIRCLE: Byte = 32
        const val LL_PCODE_PATH_CIRCLE2: Byte = 48
        const val LL_PCODE_PATH_FLEXIBLE: Byte = Byte.MIN_VALUE
        const val LL_PCODE_PATH_LINE: Byte = 16
        const val LL_PCODE_PATH_TEST: Byte = 64
        const val REV_QUANTA: Float = 0.015f
        const val SCALE_QUANTA: Float = 0.01f
        const val SHEAR_QUANTA: Float = 0.01f
        const val TAPER_QUANTA: Float = 0.01f
    }

    constructor(
        curveType: Byte, begin: Float, end: Float, scaleX: Float, scaleY: Float,
        shearX: Float, shearY: Float, twistBegin: Float, twistEnd: Float,
        radiusOffset: Float, taperX: Float, taperY: Float, revolutions: Float, skew: Float
    ) {
        this.curveType = curveType
        this.begin = begin
        this.end = end
        this.scaleX = scaleX
        this.scaleY = scaleY
        this.shearX = shearX
        this.shearY = shearY
        this.twistBegin = twistBegin
        this.twistEnd = twistEnd
        this.radiusOffset = radiusOffset
        this.taperX = taperX
        this.taperY = taperY
        this.revolutions = revolutions
        this.skew = skew
        calculateHash()
    }

    constructor(objectData: ObjectUpdate.ObjectData) {
        this.curveType = objectData.PathCurve.toByte()
        this.begin = (objectData.PathBegin and 65535).toFloat() * CUT_QUANTA
        this.end = (50000 - (objectData.PathEnd and 65535)).toFloat() * CUT_QUANTA
        this.scaleX = (200 - (objectData.PathScaleX and 255)).toFloat() * SCALE_QUANTA
        this.scaleY = (200 - (objectData.PathScaleY and 255)).toFloat() * SCALE_QUANTA
        this.shearX = LLTersePacking.toFloat(objectData.PathShearX.toByte()) * SHEAR_QUANTA
        this.shearY = LLTersePacking.toFloat(objectData.PathShearY.toByte()) * SHEAR_QUANTA
        this.twistEnd = LLTersePacking.toFloat(objectData.PathTwist.toByte()) * 0.01f
        this.twistBegin = LLTersePacking.toFloat(objectData.PathTwistBegin.toByte()) * 0.01f
        this.radiusOffset = LLTersePacking.toFloat(objectData.PathRadiusOffset.toByte()) * 0.01f
        this.taperX = LLTersePacking.toFloat(objectData.PathTaperX.toByte()) * TAPER_QUANTA
        this.taperY = LLTersePacking.toFloat(objectData.PathTaperY.toByte()) * TAPER_QUANTA
        this.revolutions = ((objectData.PathRevolutions and 255).toFloat() * REV_QUANTA) + 1.0f
        this.skew = LLTersePacking.toFloat(objectData.PathSkew.toByte()) * 0.01f
        calculateHash()
    }

    constructor(byteBuffer: ByteBuffer) {
        this.curveType = byteBuffer.get()
        this.begin = (byteBuffer.short.toInt() and 65535).toFloat() * CUT_QUANTA
        this.end = (50000 - (byteBuffer.short.toInt() and 65535)).toFloat() * CUT_QUANTA
        this.scaleX = (200 - (byteBuffer.get().toInt() and 255)).toFloat() * SCALE_QUANTA
        this.scaleY = (200 - (byteBuffer.get().toInt() and 255)).toFloat() * SCALE_QUANTA
        this.shearX = LLTersePacking.toFloat(byteBuffer.get()) * SHEAR_QUANTA
        this.shearY = LLTersePacking.toFloat(byteBuffer.get()) * SHEAR_QUANTA
        this.twistEnd = LLTersePacking.toFloat(byteBuffer.get()) * 0.01f
        this.twistBegin = LLTersePacking.toFloat(byteBuffer.get()) * 0.01f
        this.radiusOffset = LLTersePacking.toFloat(byteBuffer.get()) * 0.01f
        this.taperX = LLTersePacking.toFloat(byteBuffer.get()) * TAPER_QUANTA
        this.taperY = LLTersePacking.toFloat(byteBuffer.get()) * TAPER_QUANTA
        this.revolutions = ((byteBuffer.get().toInt() and 255).toFloat() * REV_QUANTA) + 1.0f
        this.skew = LLTersePacking.toFloat(byteBuffer.get()) * 0.01f
        calculateHash()
    }

    private fun calculateHash() {
        hashValue = (curveType * 17) + 0 + 
            java.lang.Float.floatToIntBits(begin) + 
            java.lang.Float.floatToIntBits(end) + 
            java.lang.Float.floatToIntBits(scaleX) + 
            java.lang.Float.floatToIntBits(scaleY) + 
            java.lang.Float.floatToIntBits(shearX) + 
            java.lang.Float.floatToIntBits(shearY) + 
            java.lang.Float.floatToIntBits(twistBegin) + 
            java.lang.Float.floatToIntBits(twistEnd) + 
            java.lang.Float.floatToIntBits(radiusOffset) + 
            java.lang.Float.floatToIntBits(taperX) + 
            java.lang.Float.floatToIntBits(taperY) + 
            java.lang.Float.floatToIntBits(revolutions) + 
            java.lang.Float.floatToIntBits(skew)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PrimPathParams) return false
        
        return curveType == other.curveType && 
               begin == other.begin && 
               end == other.end && 
               scaleX == other.scaleX && 
               scaleY == other.scaleY && 
               shearX == other.shearX && 
               shearY == other.shearY && 
               twistBegin == other.twistBegin && 
               twistEnd == other.twistEnd && 
               radiusOffset == other.radiusOffset && 
               taperX == other.taperX && 
               taperY == other.taperY && 
               revolutions == other.revolutions && 
               skew == other.skew
    }

    override fun hashCode(): Int {
        return hashValue
    }

    fun getBeginScale(): LLVector2 {
        val vector = LLVector2(1.0f, 1.0f)
        if (scaleX > 1.0f) vector.x = 2.0f - scaleX
        if (scaleY > 1.0f) vector.y = 2.0f - scaleY
        return vector
    }

    fun getEndScale(): LLVector2 {
        val vector = LLVector2(1.0f, 1.0f)
        if (scaleX < 1.0f) vector.x = scaleX
        if (scaleY < 1.0f) vector.y = scaleY
        return vector
    }

    override fun toString(): String {
        return "CurveType: 0x%02x, Begin: %f, End: %f, Scale: (%f, %f), Shear: (%f, %f), TwistBegin: %f, TwistEnd: %f, RadiusOffset: %f, Taper: (%f, %f), Revolutions: %f, Skew: %f".format(
            curveType, begin, end, scaleX, scaleY, shearX, shearY, twistBegin, twistEnd, radiusOffset, taperX, taperY, revolutions, skew
        )
    }
}
