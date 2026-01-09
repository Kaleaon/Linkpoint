package com.linkpoint.slproto.prims

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer

class PrimFlexibleParams {
    Float AirFriction
    Float Gravity
    Int NumFlexiSections
    Float Tension
    LLVector3 UserForce
    Float WindSensitivity

    PrimFlexibleParams(ByteBuffer byteBuffer, Int i) {
        var i2: Int = 0
        Byte b = byteBuffer.get()
        Byte b2 = byteBuffer.get()
        this.Tension = ((Float) (b & Ascii.DEL)) / 10.0f
        this.AirFriction = ((Float) (b2 & Ascii.DEL)) / 10.0f
        i2 = (b & 128) != 0 ? 2 : i2
        this.NumFlexiSections = (1 << ((b2 & 128) != 0 ? i2 | 1 : i2)) + 1
        this.Gravity = (((Float) (byteBuffer.get() & UnsignedBytes.MAX_VALUE)) / 10.0f) - 10.0f
        this.WindSensitivity = ((Float) (byteBuffer.get() & UnsignedBytes.MAX_VALUE)) / 10.0f
        if (byteBuffer.position() < i) {
            this.UserForce = LLVector3.parseFloatVec(byteBuffer)
        } else {
            this.UserForce = LLVector3.Zero
        }
    }

    fun equals(Any obj): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj is PrimFlexibleParams)) {
            return false
        }
        PrimFlexibleParams primFlexibleParams = (PrimFlexibleParams) obj
        return this.Tension == primFlexibleParams.Tension && this.AirFriction == primFlexibleParams.AirFriction && this.Gravity == primFlexibleParams.Gravity && this.WindSensitivity == primFlexibleParams.WindSensitivity && this.NumFlexiSections == primFlexibleParams.NumFlexiSections && !(this.UserForce.equals(primFlexibleParams.UserForce) ^ true)
    }

    fun hashCode(): Int {
        return Float.floatToRawIntBits(this.Tension) + 0 + Float.floatToRawIntBits(this.AirFriction) + Float.floatToRawIntBits(this.Gravity) + Float.floatToRawIntBits(this.WindSensitivity) + this.NumFlexiSections + this.UserForce.hashCode()
    }
}
