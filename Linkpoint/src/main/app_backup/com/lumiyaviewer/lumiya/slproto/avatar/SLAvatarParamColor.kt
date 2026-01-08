package com.lumiyaviewer.lumiya.slproto.avatar

import java.util.Arrays
import kotlin.math.roundToInt

class SLAvatarParamColor(
    val colorOperation: ColorOperation,
    private val colorValues: IntArray
) {
    enum class ColorOperation {
        Default,
        Blend,
        Multiply
    }

    fun colorAdd(i: Int, i2: Int): Int {
        var i3 = 255
        var i4 = (i and 255) + (i2 and 255)
        var i5 = ((i shr 8) and 255) + ((i2 shr 8) and 255)
        var i6 = ((i2 shr 16) and 255) + ((i shr 16) and 255)
        val i7 = ((i shr 24) and 255) + ((i2 shr 24) and 255)
        
        if (i4 > 255) i4 = 255
        if (i5 > 255) i5 = 255
        if (i6 > 255) i6 = 255
        if (i7 <= 255) i3 = i7
        
        return (i3 shl 24) or (i6 shl 16) or (i5 shl 8) or i4
    }

    fun colorLerp(i: Int, i2: Int, f: Float): Int {
        var i3 = 0
        val f2 = 1.0f - f
        var round = (((i and 255).toFloat() * f2) + ((i2 and 255).toFloat() * f)).roundToInt()
        var round2 = (f2 * ((i shr 8) and 255).toFloat() + f * ((i2 shr 8) and 255).toFloat()).roundToInt()
        var round3 = (f2 * ((i shr 16) and 255).toFloat() + f * ((i2 shr 16) and 255).toFloat()).roundToInt()
        val round4 = (f2 * ((i shr 24) and 255).toFloat() + f * ((i2 shr 24) and 255).toFloat()).roundToInt()
        
        if (round < 0) round = 0 else if (round > 255) round = 255
        if (round2 < 0) round2 = 0 else if (round2 > 255) round2 = 255
        if (round3 < 0) round3 = 0 else if (round3 > 255) round3 = 255
        if (round4 >= 0) i3 = if (round4 > 255) 255 else round4
        
        return (i3 shl 24) or (round3 shl 16) or (round2 shl 8) or round
    }

    fun colorMult(i: Int, i2: Int): Int {
        var i3 = 255
        var i4 = ((i and 255) * (i2 and 255)) / 255
        var i5 = (((i shr 8) and 255) * ((i2 shr 8) and 255)) / 255
        var i6 = (((i shr 16) and 255) * ((i2 shr 16) and 255)) / 255
        val i7 = (((i shr 24) and 255) * ((i2 shr 24) and 255)) / 255
        
        if (i4 > 255) i4 = 255
        if (i5 > 255) i5 = 255
        if (i6 > 255) i6 = 255
        if (i7 <= 255) i3 = i7
        
        return (i3 shl 24) or (i6 shl 16) or (i5 shl 8) or i4
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val that = other as SLAvatarParamColor
        return if (colorOperation == that.colorOperation) {
            Arrays.equals(colorValues, that.colorValues)
        } else false
    }

    fun getColor(f: Float): Int {
        if (colorValues.isEmpty()) return 0
        if (colorValues.size == 1) return colorValues[0]
        
        val length = colorValues.size - 1
        val f2 = length.toFloat() * f
        val i = f2.toInt()
        val i2 = i + 1
        
        if (i >= length) return colorValues[length]
        
        return colorLerp(colorValues[i], colorValues[i2], f2 - i.toFloat())
    }

    override fun hashCode(): Int {
        return (colorOperation.hashCode() * 31) + Arrays.hashCode(colorValues)
    }
}
