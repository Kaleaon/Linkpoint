package com.lumiyaviewer.lumiya.render.avatar

import com.lumiyaviewer.lumiya.Debug
import com.lumiyaviewer.lumiya.slproto.messages.AvatarAppearance
import java.util.Arrays

class AvatarShapeParams(private val visualParamValues: IntArray) {

    companion object {
        fun create(previous: AvatarShapeParams?, appearance: AvatarAppearance): AvatarShapeParams {
            // Debug logging omitted for brevity/compilation speed, but logic preserved
            val newValues = IntArray(218)
            val numParams = appearance.VisualParam_Fields.size
            
            for (i in 0 until 218) {
                if (i < numParams) {
                    newValues[i] = appearance.VisualParam_Fields[i].ParamValue
                } else {
                    newValues[i] = previous?.visualParamValues?.getOrNull(i) ?: 0
                }
            }
            return AvatarShapeParams(newValues)
        }

        fun create(previous: AvatarShapeParams?, values: IntArray): AvatarShapeParams {
            if (values.size == 218) return AvatarShapeParams(values)
            
            val newValues = IntArray(218)
            System.arraycopy(values, 0, newValues, 0, Math.min(values.size, 218))
            
            if (previous != null && values.size < 218) {
                val remaining = 218 - values.size
                System.arraycopy(previous.visualParamValues, values.size, newValues, values.size, remaining)
            }
            
            return AvatarShapeParams(newValues)
        }
    }

    override fun equals(other: Any?): Boolean {
        return other is AvatarShapeParams && Arrays.equals(visualParamValues, other.visualParamValues)
    }

    override fun hashCode(): Int {
        return Arrays.hashCode(visualParamValues)
    }

    fun getParamCount(): Int = 218

    fun getParamValue(index: Int): Int {
        return if (index in 0 until 218) visualParamValues[index] else 0
    }
}
