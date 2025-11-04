// 
// Decompiled by Procyon v0.6.0
// 

package com.lumiyaviewer.lumiya.slproto.prims;

import java.nio.ByteBuffer;
import com.lumiyaviewer.lumiya.slproto.types.LLVector3;

public class PrimFlexibleParams
{
    public float AirFriction;
    public float Gravity;
    public int NumFlexiSections;
    public float Tension;
    public LLVector3 UserForce;
    public float WindSensitivity;
    
    public PrimFlexibleParams(final ByteBuffer byteBuffer, final int n) {
        int n2 = 0;
        final byte value = byteBuffer.get();
        final byte value2 = byteBuffer.get();
        this.Tension = (value & 0x7F) / 10.0f;
        this.AirFriction = (value2 & 0x7F) / 10.0f;
        if ((value & 0x80) != 0x0) {
            n2 = 2;
        }
        int n3 = n2;
        if ((value2 & 0x80) != 0x0) {
            n3 = (n2 | 0x1);
        }
        this.NumFlexiSections = (1 << n3) + 1;
        this.Gravity = (byteBuffer.get() & 0xFF) / 10.0f - 10.0f;
        this.WindSensitivity = (byteBuffer.get() & 0xFF) / 10.0f;
        if (byteBuffer.position() < n) {
            this.UserForce = LLVector3.parseFloatVec(byteBuffer);
        }
        else {
            this.UserForce = LLVector3.Zero;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof PrimFlexibleParams)) {
            return false;
        }
        final PrimFlexibleParams primFlexibleParams = (PrimFlexibleParams)o;
        return this.Tension == primFlexibleParams.Tension && this.AirFriction == primFlexibleParams.AirFriction && this.Gravity == primFlexibleParams.Gravity && this.WindSensitivity == primFlexibleParams.WindSensitivity && this.NumFlexiSections == primFlexibleParams.NumFlexiSections && !(this.UserForce.equals(primFlexibleParams.UserForce) ^ true);
    }
    
    @Override
    public int hashCode() {
        return Float.floatToRawIntBits(this.Tension) + 0 + Float.floatToRawIntBits(this.AirFriction) + Float.floatToRawIntBits(this.Gravity) + Float.floatToRawIntBits(this.WindSensitivity) + this.NumFlexiSections + this.UserForce.hashCode();
    }
}
