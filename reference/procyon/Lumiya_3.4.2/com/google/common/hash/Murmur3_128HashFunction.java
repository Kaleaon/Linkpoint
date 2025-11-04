// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import com.google.common.primitives.UnsignedBytes;
import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import java.io.Serializable;

final class Murmur3_128HashFunction extends AbstractStreamingHashFunction implements Serializable
{
    private static final long serialVersionUID = 0L;
    private final int seed;
    
    Murmur3_128HashFunction(final int seed) {
        this.seed = seed;
    }
    
    @Override
    public int bits() {
        return 128;
    }
    
    @Override
    public boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof Murmur3_128HashFunction)) {
            return false;
        }
        if (this.seed == ((Murmur3_128HashFunction)o).seed) {
            b = true;
        }
        return b;
    }
    
    @Override
    public int hashCode() {
        return this.getClass().hashCode() ^ this.seed;
    }
    
    @Override
    public Hasher newHasher() {
        return new Murmur3_128Hasher(this.seed);
    }
    
    @Override
    public String toString() {
        return "Hashing.murmur3_128(" + this.seed + ")";
    }
    
    private static final class Murmur3_128Hasher extends AbstractStreamingHasher
    {
        private static final long C1 = -8663945395140668459L;
        private static final long C2 = 5545529020109919103L;
        private static final int CHUNK_SIZE = 16;
        private long h1;
        private long h2;
        private int length;
        
        Murmur3_128Hasher(final int n) {
            super(16);
            this.h1 = n;
            this.h2 = n;
            this.length = 0;
        }
        
        private void bmix64(final long n, final long n2) {
            this.h1 ^= mixK1(n);
            this.h1 = Long.rotateLeft(this.h1, 27);
            this.h1 += this.h2;
            this.h1 = this.h1 * 5L + 1390208809L;
            this.h2 ^= mixK2(n2);
            this.h2 = Long.rotateLeft(this.h2, 31);
            this.h2 += this.h1;
            this.h2 = this.h2 * 5L + 944331445L;
        }
        
        private static long fmix64(long n) {
            n = (n >>> 33 ^ n) * -49064778989728563L;
            n = (n ^ n >>> 33) * -4265267296055464877L;
            return n ^ n >>> 33;
        }
        
        private static long mixK1(final long n) {
            return Long.rotateLeft(-8663945395140668459L * n, 31) * 5545529020109919103L;
        }
        
        private static long mixK2(final long n) {
            return Long.rotateLeft(5545529020109919103L * n, 33) * -8663945395140668459L;
        }
        
        public HashCode makeHash() {
            this.h1 ^= this.length;
            this.h2 ^= this.length;
            this.h1 += this.h2;
            this.h2 += this.h1;
            this.h1 = fmix64(this.h1);
            this.h2 = fmix64(this.h2);
            this.h1 += this.h2;
            this.h2 += this.h1;
            return HashCode.fromBytesNoCopy(ByteBuffer.wrap(new byte[16]).order(ByteOrder.LITTLE_ENDIAN).putLong(this.h1).putLong(this.h2).array());
        }
        
        @Override
        protected void process(final ByteBuffer byteBuffer) {
            this.bmix64(byteBuffer.getLong(), byteBuffer.getLong());
            this.length += 16;
        }
        
        @Override
        protected void processRemaining(final ByteBuffer byteBuffer) {
            this.length += byteBuffer.remaining();
        Label_0351:
            while (true) {
                long n13 = 0L;
            Label_0330:
                while (true) {
                    long n12 = 0L;
                Label_0309:
                    while (true) {
                        long n11 = 0L;
                    Label_0288:
                        while (true) {
                            long n10 = 0L;
                        Label_0270:
                            while (true) {
                                long n = 0L;
                                long n2;
                                long n3;
                                long n4 = 0L;
                                long n5 = 0L;
                                long n6 = 0L;
                                long n7;
                                long n8;
                                long n9 = 0L;
                                long n14;
                                Label_0257:Label_0119_Outer:
                                while (true) {
                                    while (true) {
                                    Label_0217:
                                        while (true) {
                                        Label_0197:
                                            while (true) {
                                            Label_0177:
                                                while (true) {
                                                Label_0104_Outer:
                                                    while (true) {
                                                        switch (byteBuffer.remaining()) {
                                                            default: {
                                                                throw new AssertionError((Object)"Should never get here.");
                                                            }
                                                            case 1: {
                                                                n = 0L;
                                                                break;
                                                            }
                                                            case 2: {
                                                                n2 = 0L;
                                                                break Label_0157;
                                                            }
                                                            case 3: {
                                                                n3 = 0L;
                                                                break Label_0177;
                                                            }
                                                            case 4: {
                                                                n4 = 0L;
                                                                break Label_0197;
                                                            }
                                                            case 5: {
                                                                n5 = 0L;
                                                                break Label_0217;
                                                            }
                                                            case 6: {
                                                                n6 = 0L;
                                                                break Label_0197;
                                                            }
                                                            case 8: {
                                                                n7 = 0L;
                                                                break Label_0257;
                                                            }
                                                            case 9: {
                                                                n8 = 0L;
                                                                break Label_0270;
                                                            }
                                                            case 10: {
                                                                n9 = 0L;
                                                                break Label_0288;
                                                            }
                                                            case 11: {
                                                                n10 = 0L;
                                                                break Label_0309;
                                                            }
                                                            case 12: {
                                                                n11 = 0L;
                                                                break Label_0330;
                                                            }
                                                            case 13: {
                                                                n12 = 0L;
                                                                break Label_0351;
                                                            }
                                                            case 14: {
                                                                n13 = 0L;
                                                                break Label_0330;
                                                            }
                                                            case 15: {
                                                                n13 = ((long)UnsignedBytes.toInt(byteBuffer.get(14)) << 48 ^ 0x0L);
                                                                break Label_0330;
                                                            }
                                                            case 7: {
                                                                n6 = ((long)UnsignedBytes.toInt(byteBuffer.get(6)) << 48 ^ 0x0L);
                                                                break Label_0197;
                                                            }
                                                        }
                                                        while (true) {
                                                            n14 = (n ^ (long)UnsignedBytes.toInt(byteBuffer.get(0)));
                                                            n7 = 0L;
                                                            this.h1 ^= mixK1(n14);
                                                            this.h2 ^= mixK2(n7);
                                                            return;
                                                            n = (n2 ^ (long)UnsignedBytes.toInt(byteBuffer.get(1)) << 8);
                                                            continue Label_0119_Outer;
                                                        }
                                                        n2 = (n3 ^ (long)UnsignedBytes.toInt(byteBuffer.get(2)) << 16);
                                                        continue Label_0104_Outer;
                                                    }
                                                    n3 = (n4 ^ (long)UnsignedBytes.toInt(byteBuffer.get(3)) << 24);
                                                    continue Label_0177;
                                                }
                                                n4 = (n5 ^ (long)UnsignedBytes.toInt(byteBuffer.get(4)) << 32);
                                                continue Label_0197;
                                            }
                                            n5 = (n6 ^ (long)UnsignedBytes.toInt(byteBuffer.get(5)) << 40);
                                            continue Label_0217;
                                        }
                                        n14 = (0x0L ^ byteBuffer.getLong());
                                        continue;
                                    }
                                    n7 = (n8 ^ (long)UnsignedBytes.toInt(byteBuffer.get(8)));
                                    continue Label_0257;
                                }
                                n8 = (n9 ^ (long)UnsignedBytes.toInt(byteBuffer.get(9)) << 8);
                                continue Label_0270;
                            }
                            long n9 = n10 ^ (long)UnsignedBytes.toInt(byteBuffer.get(10)) << 16;
                            continue Label_0288;
                        }
                        long n10 = n11 ^ (long)UnsignedBytes.toInt(byteBuffer.get(11)) << 24;
                        continue Label_0309;
                    }
                    long n11 = n12 ^ (long)UnsignedBytes.toInt(byteBuffer.get(12)) << 32;
                    continue Label_0330;
                }
                long n12 = n13 ^ (long)UnsignedBytes.toInt(byteBuffer.get(13)) << 40;
                continue Label_0351;
            }
        }
    }
}
