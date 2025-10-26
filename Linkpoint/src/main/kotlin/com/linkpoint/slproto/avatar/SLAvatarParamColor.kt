package com.linkpoint.slproto.avatar

import java.util.Arrays
import javax.annotation.Nonnull

class SLAvatarParamColor {
    val ColorOperation colorOperation
    private val IntArray colorValues

    enum class ColorOperation {
        Default,
        Blend,
        Multiply
    }

    SLAvatarParamColor(ColorOperation colorOperation2, IntArray iArr) {
        this.colorOperation = colorOperation2
        this.colorValues = iArr
    }

    @JvmStatic
     fun colorAdd(i: Int, i2: Int): Int {
        val i3: Int = 255
        val i4: Int = (i & 255) + (i2 & 255)
        val i5: Int = ((i >> 8) & 255) + ((i2 >> 8) & 255)
        val i6: Int = ((i2 >> 16) & 255) + ((i >> 16) & 255)
        val i7: Int = ((i >> 24) & 255) + ((i2 >> 24) & 255)
        if (i4 > 255) {
            i4 = 255
        }
        if (i5 > 255) {
            i5 = 255
        }
        if (i6 > 255) {
            i6 = 255
        }
        if (i7 <= 255) {
            i3 = i7
        }
        return (i3 << 24) | (i6 << 16) | (i5 << 8) | i4
    }

    @JvmStatic
     fun colorLerp(i: Int, i2: Int, f: Float): Int {
        val i3: Int = 0
        val f2: Float = 1.0f - f
        val round: Int = Math.round((((Float) (i & 255)) * f2) + (((Float) (i2 & 255)) * f))
        val round2: Int = Math.round((f2 * ((Float) ((i >> 8) & 255))) + (f * ((Float) ((i2 >> 8) & 255))))
        val round3: Int = Math.round((f2 * ((Float) ((i >> 16) & 255))) + (f * ((Float) ((i2 >> 16) & 255))))
        val round4: Int = Math.round((f2 * ((Float) ((i >> 24) & 255))) + (f * ((Float) ((i2 >> 24) & 255))))
        if (round < 0) {
            round = 0
        } else if (round > 255) {
            round = 255
        }
        if (round2 < 0) {
            round2 = 0
        } else if (round2 > 255) {
            round2 = 255
        }
        if (round3 < 0) {
            round3 = 0
        } else if (round3 > 255) {
            round3 = 255
        }
        if (round4 >= 0) {
            i3 = round4 > 255 ? 255 : round4
        }
        return (i3 << 24) | (round3 << 16) | (round2 << 8) | round
    }

    @JvmStatic
     fun colorMult(i: Int, i2: Int): Int {
        val i3: Int = 255
        val i4: Int = ((i & 255) * (i2 & 255)) / 255
        val i5: Int = (((i >> 8) & 255) * ((i2 >> 8) & 255)) / 255
        val i6: Int = (((i >> 16) & 255) * ((i2 >> 16) & 255)) / 255
        val i7: Int = (((i >> 24) & 255) * ((i2 >> 24) & 255)) / 255
        if (i4 > 255) {
            i4 = 255
        }
        if (i5 > 255) {
            i5 = 255
        }
        if (i6 > 255) {
            i6 = 255
        }
        if (i7 <= 255) {
            i3 = i7
        }
        return (i3 << 24) | (i6 << 16) | (i5 << 8) | i4
    }

     public override fun equals(obj: Object): Boolean {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        val sLAvatarParamColor: SLAvatarParamColor = (SLAvatarParamColor) obj
        if (this.colorOperation == sLAvatarParamColor.colorOperation) {
            return Arrays.equals(this.colorValues, sLAvatarParamColor.colorValues)
        }
        return false
    }

     public fun getColor(f: Float): Int {
        if (this.colorValues.length == 0) {
            return 0
        }
        if (this.colorValues.length == 1) {
            return this.colorValues[0]
        }
        val length: Int = this.colorValues.length - 1
        val f2: Float = ((Float) length) * f
        val i: Int = (Int) f2
        val i2: Int = i + 1
        if (i >= length) {
            return this.colorValues[length]
        }
        return colorLerp(this.colorValues[i], this.colorValues[i2], f2 - ((Float) i))
    }

     public override fun hashCode(): Int {
        return (this.colorOperation.hashCode() * 31) + Arrays.hashCode(this.colorValues)
    }
}
