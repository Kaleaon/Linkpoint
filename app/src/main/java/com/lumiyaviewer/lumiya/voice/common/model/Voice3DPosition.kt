package com.lumiyaviewer.lumiya.voice.common.model

import android.os.Bundle

/**
 * Modern Kotlin Voice3DPosition data class
 * Represents a 3D position with orientation vectors for spatial audio
 */
data class Voice3DPosition(
    val position: Voice3DVector,
    val velocity: Voice3DVector,
    val atOrientation: Voice3DVector,
    val upOrientation: Voice3DVector,
    val leftOrientation: Voice3DVector,
) {
    /**
     * Creates a Voice3DPosition from a Bundle
     */
    constructor(bundle: Bundle) : this(
        position = Voice3DVector(bundle.getBundle("position")!!),
        velocity = Voice3DVector(bundle.getBundle("velocity")!!),
        atOrientation = Voice3DVector(bundle.getBundle("atOrientation")!!),
        upOrientation = Voice3DVector(bundle.getBundle("upOrientation")!!),
        leftOrientation = Voice3DVector(bundle.getBundle("leftOrientation")!!),
    )

    /**
     * Converts this position to a Bundle
     */
    fun toBundle(): Bundle =
        Bundle().apply {
            putBundle("position", position.toBundle())
            putBundle("velocity", velocity.toBundle())
            putBundle("atOrientation", atOrientation.toBundle())
            putBundle("upOrientation", upOrientation.toBundle())
            putBundle("leftOrientation", leftOrientation.toBundle())
        }

    /**
     * String representation
     */
    override fun toString(): String {
        return "(pos $position vel $velocity at $atOrientation up $upOrientation left $leftOrientation)"
    }
}
