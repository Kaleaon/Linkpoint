package com.linkpoint.slproto.prims

import com.google.common.base.Ascii
import com.google.common.primitives.UnsignedBytes
import com.linkpoint.slproto.types.LLVector3
import java.nio.ByteBuffer

class PrimFlexibleParams {
    public Float AirFriction
    public Float Gravity
    public Int NumFlexiSections
    public Float Tension
    public LLVector3 UserForce
    public Float WindSensitivity

    public PrimFlexibleParams(ByteBuffer byteBuffer, Int i) {
        val i2: Int = 0
        val b: Byte = byteBuffer.get()
        val b2: Byte = byteBuffer.get()
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

     public override fun equals(obj: Object): Boolean {
        if (obj == this) {
            return true
        }
        if (!(obj instanceof PrimFlexibleParams)) {
            return false
        }
        val primFlexibleParams: PrimFlexibleParams = (PrimFlexibleParams) obj
        return this.Tension == primFlexibleParams.Tension && this.AirFriction == primFlexibleParams.AirFriction && this.Gravity == primFlexibleParams.Gravity && this.WindSensitivity == primFlexibleParams.WindSensitivity && this.NumFlexiSections == primFlexibleParams.NumFlexiSections && !(this.UserForce.equals(primFlexibleParams.UserForce) ^ true)
    }

     public override fun hashCode(): Int {
        return Float.floatToRawIntBits(this.Tension) + 0 + Float.floatToRawIntBits(this.AirFriction) + Float.floatToRawIntBits(this.Gravity) + Float.floatToRawIntBits(this.WindSensitivity) + this.NumFlexiSections + this.UserForce.hashCode()
    }
}
