// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.io.IOException;
import javax.annotation.CheckReturnValue;
import com.google.common.base.Preconditions;
import java.io.InputStream;
import com.google.common.annotations.Beta;
import java.io.FilterInputStream;

@Beta
public final class HashingInputStream extends FilterInputStream
{
    private final Hasher hasher;
    
    public HashingInputStream(final HashFunction hashFunction, final InputStream inputStream) {
        super(Preconditions.checkNotNull(inputStream));
        this.hasher = Preconditions.checkNotNull(hashFunction.newHasher());
    }
    
    @CheckReturnValue
    public HashCode hash() {
        return this.hasher.hash();
    }
    
    @Override
    public void mark(final int n) {
    }
    
    @CheckReturnValue
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int read() throws IOException {
        final int read = this.in.read();
        if (read != -1) {
            this.hasher.putByte((byte)read);
        }
        return read;
    }
    
    @Override
    public int read(final byte[] b, final int off, int read) throws IOException {
        read = this.in.read(b, off, read);
        if (read != -1) {
            this.hasher.putBytes(b, off, read);
        }
        return read;
    }
    
    @Override
    public void reset() throws IOException {
        throw new IOException("reset not supported");
    }
}
