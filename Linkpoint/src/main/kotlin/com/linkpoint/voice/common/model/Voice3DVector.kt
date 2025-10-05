package com.linkpoint.voice.common.model

import android.annotation.SuppressLint
import android.os.Bundle
import javax.annotation.concurrent.Immutable

@Immutable
class Voice3DVector {
    val Float x
    val Float y
    val Float z

    public Voice3DVector(Float f, Float f2, Float f3) {
        this.x = f
        this.y = f2
        this.z = f3
    }

    public Voice3DVector(Bundle bundle) {
        this.x = bundle.getFloat("x")
        this.y = bundle.getFloat("y")
        this.z = bundle.getFloat("z")
    }

    @JvmStatic
    Voice3DVector fromLLCoords(Float f, Float f2, Float f3) {
        return Voice3DVector(f, f3, -f2)
    }

    public Bundle toBundle() {
        Bundle bundle = Bundle()
        bundle.putFloat("x", this.x)
        bundle.putFloat("y", this.y)
        bundle.putFloat("z", this.z)
        return bundle
    }

    @SuppressLint(value={"DefaultLocale"})
    public String toString() {
        return String.format("(%.2f, %.2f, %.2f)", Float.valueOf(this.x), Float.valueOf(this.y), Float.valueOf(this.z))
    }
}

