// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.util.zip.Checksum;
import com.google.common.base.Supplier;
import java.io.Serializable;

final class ChecksumHashFunction extends AbstractStreamingHashFunction implements Serializable
{
    private static final long serialVersionUID = 0L;
    private final int bits;
    private final Supplier<? extends Checksum> checksumSupplier;
    private final String toString;
    
    ChecksumHashFunction(final Supplier<? extends Checksum> supplier, final int n, final String s) {
        this.checksumSupplier = Preconditions.checkNotNull(supplier);
        Preconditions.checkArgument(n == 32 || n == 64, "bits (%s) must be either 32 or 64", n);
        this.bits = n;
        this.toString = Preconditions.checkNotNull(s);
    }
    
    @Override
    public int bits() {
        return this.bits;
    }
    
    @Override
    public Hasher newHasher() {
        return new ChecksumHasher((Checksum)this.checksumSupplier.get());
    }
    
    @Override
    public String toString() {
        return this.toString;
    }
    
    private final class ChecksumHasher extends AbstractByteHasher
    {
        private final Checksum checksum;
        
        private ChecksumHasher(final Checksum checksum) {
            this.checksum = Preconditions.checkNotNull(checksum);
        }
        
        @Override
        public HashCode hash() {
            final long value = this.checksum.getValue();
            if (ChecksumHashFunction.this.bits != 32) {
                return HashCode.fromLong(value);
            }
            return HashCode.fromInt((int)value);
        }
        
        @Override
        protected void update(final byte b) {
            this.checksum.update(b);
        }
        
        @Override
        protected void update(final byte[] array, final int n, final int n2) {
            this.checksum.update(array, n, n2);
        }
    }
}
