package com.lumiyaviewer.lumiya.voice.common.model

import android.annotation.SuppressLint
import android.os.Bundle

/**
 * Modern Kotlin Voice3DVector data class
 * Represents a 3D vector for spatial audio positioning
 */
data class Voice3DVector(
    val x: Float,
    val y: Float,
    val z: Float
) {
    /**
     * Creates a Voice3DVector from a Bundle
     */
    constructor(bundle: Bundle) : this(
        x = bundle.getFloat("x"),
        y = bundle.getFloat("y"),
        z = bundle.getFloat("z")
    )
    
    companion object {
        /**
         * Creates a Voice3DVector from Linden Lab (Second Life) coordinates
         * Converts from LL coordinate system to voice coordinate system
         */
        @JvmStatic
        fun fromLLCoords(x: Float, y: Float, z: Float): Voice3DVector {
            return Voice3DVector(x, z, -y)
        }
    }
    
    /**
     * Converts this vector to a Bundle
     */
    fun toBundle(): Bundle = Bundle().apply {
        putFloat("x", x)
        putFloat("y", y)
        putFloat("z", z)
    }
    
    /**
     * String representation with formatted floats
     */
    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return "(%.2f, %.2f, %.2f)".format(x, y, z)
    }
}