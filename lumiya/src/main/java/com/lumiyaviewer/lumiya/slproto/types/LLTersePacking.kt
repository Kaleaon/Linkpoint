package com.lumiyaviewer.lumiya.slproto.types

import androidx.core.view.InputDeviceCompat
import kotlin.math.abs

object LLTersePacking {
    @JvmStatic
    fun U16_to_float(
        value: Int,
        min: Float,
        max: Float,
    ): Float {
        return int_dequantize(1.5259022E-5f, value, min, max)
    }

    @JvmStatic
    fun U8_to_float(
        value: Int,
        min: Float,
        max: Float,
    ): Float {
        return int_dequantize(0.003921569f, value, min, max)
    }

    @JvmStatic
    fun getSignedByte(value: Int): Int {
        val unsigned = value and 255
        return if (unsigned >= 128) unsigned + InputDeviceCompat.SOURCE_ANY else unsigned
    }

    private fun int_dequantize(
        quantization: Float,
        value: Int,
        min: Float,
        max: Float,
    ): Float {
        val range = max - min
        val result = (value.toFloat() * quantization * range) + min
        return if (abs(result) < range * quantization) 0.0f else result
    }
}
