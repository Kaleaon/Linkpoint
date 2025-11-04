// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;

abstract class AbstractByteHasher extends AbstractHasher
{
    private final ByteBuffer scratch;
    
    AbstractByteHasher() {
        this.scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
    }
    
    private Hasher update(final int n) {
        try {
            this.update(this.scratch.array(), 0, n);
            return this;
        }
        finally {
            this.scratch.clear();
        }
    }
    
    @Override
    public Hasher putByte(final byte b) {
        this.update(b);
        return this;
    }
    
    @Override
    public Hasher putBytes(final byte[] array) {
        Preconditions.checkNotNull(array);
        this.update(array);
        return this;
    }
    
    @Override
    public Hasher putBytes(final byte[] array, final int n, final int n2) {
        Preconditions.checkPositionIndexes(n, n + n2, array.length);
        this.update(array, n, n2);
        return this;
    }
    
    @Override
    public Hasher putChar(final char c) {
        this.scratch.putChar(c);
        return this.update(2);
    }
    
    @Override
    public Hasher putInt(final int n) {
        this.scratch.putInt(n);
        return this.update(4);
    }
    
    @Override
    public Hasher putLong(final long n) {
        this.scratch.putLong(n);
        return this.update(8);
    }
    
    @Override
    public <T> Hasher putObject(final T t, final Funnel<? super T> funnel) {
        funnel.funnel(t, this);
        return this;
    }
    
    @Override
    public Hasher putShort(final short n) {
        this.scratch.putShort(n);
        return this.update(2);
    }
    
    protected abstract void update(final byte p0);
    
    protected void update(final byte[] array) {
        this.update(array, 0, array.length);
    }
    
    protected void update(final byte[] array, final int n, final int n2) {
        for (int i = n; i < n + n2; ++i) {
            this.update(array[i]);
        }
    }
}
