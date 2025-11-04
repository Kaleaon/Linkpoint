// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.nio.charset.Charset;

abstract class AbstractHasher implements Hasher
{
    @Override
    public final Hasher putBoolean(final boolean b) {
        final byte b2 = 0;
        byte b3;
        if (!b) {
            b3 = b2;
        }
        else {
            b3 = 1;
        }
        return this.putByte(b3);
    }
    
    @Override
    public final Hasher putDouble(final double n) {
        return this.putLong(Double.doubleToRawLongBits(n));
    }
    
    @Override
    public final Hasher putFloat(final float n) {
        return this.putInt(Float.floatToRawIntBits(n));
    }
    
    @Override
    public Hasher putString(final CharSequence charSequence, final Charset charset) {
        return this.putBytes(charSequence.toString().getBytes(charset));
    }
    
    @Override
    public Hasher putUnencodedChars(final CharSequence charSequence) {
        for (int i = 0; i < charSequence.length(); ++i) {
            this.putChar(charSequence.charAt(i));
        }
        return this;
    }
}
