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
    Int colorAdd(Int i, Int i2) {
        Int i3 = 255
        Int i4 = (i & 255) + (i2 & 255)
        Int i5 = ((i >> 8) & 255) + ((i2 >> 8) & 255)
        Int i6 = ((i2 >> 16) & 255) + ((i >> 16) & 255)
        Int i7 = ((i >> 24) & 255) + ((i2 >> 24) & 255)
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
    Int colorLerp(Int i, Int i2, Float f) {
        Int i3 = 0
        Float f2 = 1.0f - f
        Int round = Math.round((((Float) (i & 255)) * f2) + (((Float) (i2 & 255)) * f))
        Int round2 = Math.round((f2 * ((Float) ((i >> 8) & 255))) + (f * ((Float) ((i2 >> 8) & 255))))
        Int round3 = Math.round((f2 * ((Float) ((i >> 16) & 255))) + (f * ((Float) ((i2 >> 16) & 255))))
        Int round4 = Math.round((f2 * ((Float) ((i >> 24) & 255))) + (f * ((Float) ((i2 >> 24) & 255))))
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
    Int colorMult(Int i, Int i2) {
        Int i3 = 255
        Int i4 = ((i & 255) * (i2 & 255)) / 255
        Int i5 = (((i >> 8) & 255) * ((i2 >> 8) & 255)) / 255
        Int i6 = (((i >> 16) & 255) * ((i2 >> 16) & 255)) / 255
        Int i7 = (((i >> 24) & 255) * ((i2 >> 24) & 255)) / 255
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

    public Boolean equals(Object obj) {
        if (this == obj) {
            return true
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false
        }
        SLAvatarParamColor sLAvatarParamColor = (SLAvatarParamColor) obj
        if (this.colorOperation == sLAvatarParamColor.colorOperation) {
            return Arrays.equals(this.colorValues, sLAvatarParamColor.colorValues)
        }
        return false
    }

    public Int getColor(Float f) {
        if (this.colorValues.length == 0) {
            return 0
        }
        if (this.colorValues.length == 1) {
            return this.colorValues[0]
        }
        Int length = this.colorValues.length - 1
        Float f2 = (length.toFloat()) * f
        Int i = f2.toInt()
        Int i2 = i + 1
        if (i >= length) {
            return this.colorValues[length]
        }
        return colorLerp(this.colorValues[i], this.colorValues[i2], f2 - (i.toFloat()))
    }

    public Int hashCode() {
        return (this.colorOperation.hashCode() * 31) + Arrays.hashCode(this.colorValues)
    }
}
