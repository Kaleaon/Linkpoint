// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.nio.charset.Charset;
import javax.annotation.CheckReturnValue;
import com.google.common.annotations.Beta;

@Beta
public interface Hasher extends PrimitiveSink
{
    @CheckReturnValue
    HashCode hash();
    
    @Deprecated
    int hashCode();
    
    Hasher putBoolean(final boolean p0);
    
    Hasher putByte(final byte p0);
    
    Hasher putBytes(final byte[] p0);
    
    Hasher putBytes(final byte[] p0, final int p1, final int p2);
    
    Hasher putChar(final char p0);
    
    Hasher putDouble(final double p0);
    
    Hasher putFloat(final float p0);
    
    Hasher putInt(final int p0);
    
    Hasher putLong(final long p0);
    
     <T> Hasher putObject(final T p0, final Funnel<? super T> p1);
    
    Hasher putShort(final short p0);
    
    Hasher putString(final CharSequence p0, final Charset p1);
    
    Hasher putUnencodedChars(final CharSequence p0);
}
