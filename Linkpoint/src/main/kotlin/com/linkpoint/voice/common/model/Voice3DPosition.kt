package com.linkpoint.voice.common.model

import android.os.Bundle
import com.linkpoint.voice.common.model.Voice3DVector
import javax.annotation.Nonnull
import javax.annotation.concurrent.Immutable

@Immutable
class Voice3DPosition {
    val Voice3DVector atOrientation
    val Voice3DVector leftOrientation
    val Voice3DVector position
    val Voice3DVector upOrientation
    val Voice3DVector velocity

    public Voice3DPosition(Bundle bundle) {
        this.position = Voice3DVector(bundle.getBundle("position"))
        this.velocity = Voice3DVector(bundle.getBundle("velocity"))
        this.atOrientation = Voice3DVector(bundle.getBundle("atOrientation"))
        this.upOrientation = Voice3DVector(bundle.getBundle("upOrientation"))
        this.leftOrientation = Voice3DVector(bundle.getBundle("leftOrientation"))
    }

    public Voice3DPosition(Voice3DVector voice3DVector, Voice3DVector voice3DVector2, Voice3DVector voice3DVector3, Voice3DVector voice3DVector4, Voice3DVector voice3DVector5) {
        this.position = voice3DVector
        this.velocity = voice3DVector2
        this.atOrientation = voice3DVector3
        this.upOrientation = voice3DVector4
        this.leftOrientation = voice3DVector5
    }

     public fun toBundle(): Bundle {
        val bundle: Bundle = Bundle()
        bundle.putBundle("position", this.position.toBundle())
        bundle.putBundle("velocity", this.velocity.toBundle())
        bundle.putBundle("atOrientation", this.atOrientation.toBundle())
        bundle.putBundle("upOrientation", this.upOrientation.toBundle())
        bundle.putBundle("leftOrientation", this.leftOrientation.toBundle())
        return bundle
    }

     public fun toString(): String {
        return String.format("(pos %s vel %s at %s up %s left %s)", this.position.toString(), this.velocity.toString(), this.atOrientation.toString(), this.upOrientation.toString(), this.leftOrientation.toString())
    }
}

