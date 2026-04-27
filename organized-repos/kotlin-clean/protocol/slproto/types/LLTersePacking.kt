package com.linkpoint.slproto.types

import androidx.core.view.InputDeviceCompat

val class LLTersePacking {
const val U16_: Floatto_float(Int i, Float f, Float f2) {
        return int_dequantize(1.5259022E-5f, i, f, f2)
    }

const val U8_: Floatto_float(Int i, Float f, Float f2) {
        return int_dequantize(0.003921569f, i, f, f2)
    }

    const val Int getSignedByte(Int i) {
        Int i2 = i & 255
        return i2 >= 128 ? i2 + InputDeviceCompat.SOURCE_ANY : i2
    }

    private const val Float int_dequantize(Float f, Int i, Float f2, Float f3) {
        Float f4 = f3 - f2
        Float f5 = ((i.toFloat()) * f * f4) + f2
        if (Math.abs(f5) < f4 * f) {
            return 0.0f
        }
        return f5
    }
}
