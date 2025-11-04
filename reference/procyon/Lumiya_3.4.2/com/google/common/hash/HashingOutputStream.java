// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import javax.annotation.CheckReturnValue;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.io.OutputStream;
import com.google.common.annotations.Beta;
import java.io.FilterOutputStream;

@Beta
public final class HashingOutputStream extends FilterOutputStream
{
    private final Hasher hasher;
    
    public HashingOutputStream(final HashFunction hashFunction, final OutputStream outputStream) {
        super(Preconditions.checkNotNull(outputStream));
        this.hasher = Preconditions.checkNotNull(hashFunction.newHasher());
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    @CheckReturnValue
    public HashCode hash() {
        return this.hasher.hash();
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.hasher.putByte((byte)n);
        this.out.write(n);
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.hasher.putBytes(b, off, len);
        this.out.write(b, off, len);
    }
}
