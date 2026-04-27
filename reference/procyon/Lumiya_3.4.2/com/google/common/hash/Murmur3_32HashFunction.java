// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import com.google.common.primitives.UnsignedBytes;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import java.io.Serializable;

final class Murmur3_32HashFunction extends AbstractStreamingHashFunction implements Serializable
{
    private static final int C1 = -862048943;
    private static final int C2 = 461845907;
    private static final long serialVersionUID = 0L;
    private final int seed;
    
    Murmur3_32HashFunction(final int seed) {
        this.seed = seed;
    }
    
    private static HashCode fmix(int n, final int n2) {
        n ^= n2;
        n = (n ^ n >>> 16) * -2048144789;
        n = (n ^ n >>> 13) * -1028477387;
        return HashCode.fromInt(n ^ n >>> 16);
    }
    
    private static int mixH1(final int n, final int n2) {
        return Integer.rotateLeft(n ^ n2, 13) * 5 - 430675100;
    }
    
    private static int mixK1(final int n) {
        return Integer.rotateLeft(-862048943 * n, 15) * 461845907;
    }
    
    @Override
    public int bits() {
        return 32;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof Murmur3_32HashFunction)) {
            return false;
        }
        if (this.seed == ((Murmur3_32HashFunction)o).seed) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() ^ this.seed;
    }
    
    @Override
    public HashCode hashInt(int mixK1) {
        mixK1 = mixK1(mixK1);
        return fmix(mixH1(this.seed, mixK1), 4);
    }
    
    @Override
    public HashCode hashLong(final long n) {
        return fmix(mixH1(mixH1(this.seed, mixK1((int)n)), mixK1((int)(n >>> 32))), 8);
    }
    
    @Override
    public HashCode hashUnencodedChars(final CharSequence charSequence) {
        int n = this.seed;
        for (int i = 1; i < charSequence.length(); i += 2) {
            n = mixH1(n, mixK1(charSequence.charAt(i - 1) | charSequence.charAt(i) << 16));
        }
        if ((charSequence.length() & 0x1) == 0x1) {
            n ^= mixK1(charSequence.charAt(charSequence.length() - 1));
        }
        return fmix(n, charSequence.length() * 2);
    }
    
    @Override
    public Hasher newHasher() {
        return new Murmur3_32Hasher(this.seed);
    }
    
    @Override
    public String toString() {
        return "Hashing.murmur3_32(" + this.seed + ")";
    }
    
    private static final class Murmur3_32Hasher extends AbstractStreamingHasher
    {
        private static final int CHUNK_SIZE = 4;
        private int h1;
        private int length;
        
        Murmur3_32Hasher(final int h1) {
            super(4);
            this.h1 = h1;
            this.length = 0;
        }
        
        public HashCode makeHash() {
            return fmix(this.h1, this.length);
        }
        
        @Override
        protected void process(final ByteBuffer byteBuffer) {
            this.h1 = mixH1(this.h1, mixK1(byteBuffer.getInt()));
            this.length += 4;
        }
        
        @Override
        protected void processRemaining(final ByteBuffer byteBuffer) {
            int n = 0;
            this.length += byteBuffer.remaining();
            int n2 = 0;
            while (byteBuffer.hasRemaining()) {
                n2 ^= UnsignedBytes.toInt(byteBuffer.get()) << n;
                n += 8;
            }
            this.h1 ^= mixK1(n2);
        }
    }
}
