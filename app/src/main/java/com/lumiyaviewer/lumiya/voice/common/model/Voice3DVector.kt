package com.lumiyaviewer.lumiya.voice.common.model

import android.annotation.SuppressLint
import android.os.Bundle
import javax.annotation.concurrent.Immutable

@Immutable
class Voice3DVector(
    val x: Float,
    val y: Float,
    val z: Float
) {
    constructor(bundle: Bundle) : this(
        bundle.getFloat("x"),
        bundle.getFloat("y"),
        bundle.getFloat("z")
    )

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putFloat("x", x)
        bundle.putFloat("y", y)
        bundle.putFloat("z", z)
        return bundle
    }

    @SuppressLint("DefaultLocale")
    override fun toString(): String {
        return String.format("(%.2f, %.2f, %.2f)", x, y, z)
    }

    companion object {
        @JvmStatic
        fun fromLLCoords(f: Float, f2: Float, f3: Float): Voice3DVector {
            return Voice3DVector(f, f3, -f2)
        }
    }
}