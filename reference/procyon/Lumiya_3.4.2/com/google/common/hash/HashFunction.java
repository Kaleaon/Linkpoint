// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.nio.charset.Charset;
import com.google.common.annotations.Beta;

@Beta
public interface HashFunction
{
    int bits();
    
    HashCode hashBytes(final byte[] p0);
    
    HashCode hashBytes(final byte[] p0, final int p1, final int p2);
    
    HashCode hashInt(final int p0);
    
    HashCode hashLong(final long p0);
    
     <T> HashCode hashObject(final T p0, final Funnel<? super T> p1);
    
    HashCode hashString(final CharSequence p0, final Charset p1);
    
    HashCode hashUnencodedChars(final CharSequence p0);
    
    Hasher newHasher();
    
    Hasher newHasher(final int p0);
}
