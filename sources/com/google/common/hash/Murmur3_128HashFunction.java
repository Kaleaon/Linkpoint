package com.google.common.hash;

import com.google.common.hash.AbstractStreamingHashFunction;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import javax.annotation.Nullable;

final class Murmur3_128HashFunction extends AbstractStreamingHashFunction implements Serializable {
    private static final long serialVersionUID = 0;
    private final int seed;

    private static final class Murmur3_128Hasher extends AbstractStreamingHashFunction.AbstractStreamingHasher {
        private static final long C1 = -8663945395140668459L;
        private static final long C2 = 5545529020109919103L;
        private static final int CHUNK_SIZE = 16;
        private long h1;
        private long h2;
        private int length = 0;

        Murmur3_128Hasher(int i) {
            super(16);
            this.h1 = (long) i;
            this.h2 = (long) i;
        }

        private void bmix64(long j, long j2) {
            this.h1 ^= mixK1(j);
            this.h1 = Long.rotateLeft(this.h1, 27);
            this.h1 += this.h2;
            this.h1 = (this.h1 * 5) + 1390208809;
            this.h2 ^= mixK2(j2);
            this.h2 = Long.rotateLeft(this.h2, 31);
            this.h2 += this.h1;
            this.h2 = (this.h2 * 5) + 944331445;
        }

        private static long fmix64(long j) {
            long j2 = ((j >>> 33) ^ j) * -49064778989728563L;
            long j3 = (j2 ^ (j2 >>> 33)) * -4265267296055464877L;
            return j3 ^ (j3 >>> 33);
        }

        private static long mixK1(long j) {
            return Long.rotateLeft(C1 * j, 31) * C2;
        }

        private static long mixK2(long j) {
            return Long.rotateLeft(C2 * j, 33) * C1;
        }

        public HashCode makeHash() {
            this.h1 ^= (long) this.length;
            this.h2 ^= (long) this.length;
            this.h1 += this.h2;
            this.h2 += this.h1;
            this.h1 = fmix64(this.h1);
            this.h2 = fmix64(this.h2);
            this.h1 += this.h2;
            this.h2 += this.h1;
            return HashCode.fromBytesNoCopy(ByteBuffer.wrap(new byte[16]).order(ByteOrder.LITTLE_ENDIAN).putLong(this.h1).putLong(this.h2).array());
        }

        /* access modifiers changed from: protected */
        public void process(ByteBuffer byteBuffer) {
            bmix64(byteBuffer.getLong(), byteBuffer.getLong());
            this.length += 16;
        }

        /* access modifiers changed from: protected */
        /* JADX WARNING: Code restructure failed: missing block: B:11:0x0054, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(2))) << 16);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:13:0x0062, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(3))) << 24);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:15:0x0070, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(4))) << 32);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:17:0x007e, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(5))) << 40);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:19:0x008e, code lost:
            r2 = 0 ^ r14.getLong();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:21:0x0095, code lost:
            r0 = r0 ^ ((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(8)));
         */
        /* JADX WARNING: Code restructure failed: missing block: B:23:0x00a1, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(9))) << 8);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:25:0x00b0, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(10))) << 16);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:27:0x00bf, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(11))) << 24);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:29:0x00ce, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(12))) << 32);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:0x00dd, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(13))) << 40);
         */
        /* JADX WARNING: Code restructure failed: missing block: B:5:0x0024, code lost:
            r2 = r0 ^ ((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(0)));
            r0 = 0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:6:0x0032, code lost:
            r13.h1 = mixK1(r2) ^ r13.h1;
            r13.h2 = mixK2(r0) ^ r13.h2;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:7:0x0044, code lost:
            return;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:9:0x0046, code lost:
            r0 = r0 ^ (((long) com.google.common.primitives.UnsignedBytes.toInt(r14.get(1))) << 8);
         */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void processRemaining(java.nio.ByteBuffer r14) {
            /*
                r13 = this;
                r10 = 32
                r9 = 24
                r8 = 16
                r7 = 8
                r2 = 0
                int r0 = r13.length
                int r1 = r14.remaining()
                int r0 = r0 + r1
                r13.length = r0
                int r0 = r14.remaining()
                switch(r0) {
                    case 1: goto L_0x0023;
                    case 2: goto L_0x0045;
                    case 3: goto L_0x0053;
                    case 4: goto L_0x0061;
                    case 5: goto L_0x006f;
                    case 6: goto L_0x007d;
                    case 7: goto L_0x00fd;
                    case 8: goto L_0x008d;
                    case 9: goto L_0x0094;
                    case 10: goto L_0x00a0;
                    case 11: goto L_0x00af;
                    case 12: goto L_0x00be;
                    case 13: goto L_0x00cd;
                    case 14: goto L_0x00dc;
                    case 15: goto L_0x00ed;
                    default: goto L_0x001a;
                }
            L_0x001a:
                java.lang.AssertionError r0 = new java.lang.AssertionError
                java.lang.String r1 = "Should never get here."
                r0.<init>(r1)
                throw r0
            L_0x0023:
                r0 = r2
            L_0x0024:
                r4 = 0
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r0 = r0 ^ r4
                r11 = r2
                r2 = r0
                r0 = r11
            L_0x0032:
                long r4 = r13.h1
                long r2 = mixK1(r2)
                long r2 = r2 ^ r4
                r13.h1 = r2
                long r2 = r13.h2
                long r0 = mixK2(r0)
                long r0 = r0 ^ r2
                r13.h2 = r0
                return
            L_0x0045:
                r0 = r2
            L_0x0046:
                r4 = 1
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r7
                long r0 = r0 ^ r4
                goto L_0x0024
            L_0x0053:
                r0 = r2
            L_0x0054:
                r4 = 2
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r8
                long r0 = r0 ^ r4
                goto L_0x0046
            L_0x0061:
                r0 = r2
            L_0x0062:
                r4 = 3
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r9
                long r0 = r0 ^ r4
                goto L_0x0054
            L_0x006f:
                r0 = r2
            L_0x0070:
                r4 = 4
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r10
                long r0 = r0 ^ r4
                goto L_0x0062
            L_0x007d:
                r0 = r2
            L_0x007e:
                r4 = 5
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                r6 = 40
                long r4 = r4 << r6
                long r0 = r0 ^ r4
                goto L_0x0070
            L_0x008d:
                r0 = r2
            L_0x008e:
                long r4 = r14.getLong()
                long r2 = r2 ^ r4
                goto L_0x0032
            L_0x0094:
                r0 = r2
            L_0x0095:
                byte r4 = r14.get(r7)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r0 = r0 ^ r4
                goto L_0x008e
            L_0x00a0:
                r0 = r2
            L_0x00a1:
                r4 = 9
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r7
                long r0 = r0 ^ r4
                goto L_0x0095
            L_0x00af:
                r0 = r2
            L_0x00b0:
                r4 = 10
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r8
                long r0 = r0 ^ r4
                goto L_0x00a1
            L_0x00be:
                r0 = r2
            L_0x00bf:
                r4 = 11
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r9
                long r0 = r0 ^ r4
                goto L_0x00b0
            L_0x00cd:
                r0 = r2
            L_0x00ce:
                r4 = 12
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                long r4 = r4 << r10
                long r0 = r0 ^ r4
                goto L_0x00bf
            L_0x00dc:
                r0 = r2
            L_0x00dd:
                r4 = 13
                byte r4 = r14.get(r4)
                int r4 = com.google.common.primitives.UnsignedBytes.toInt(r4)
                long r4 = (long) r4
                r6 = 40
                long r4 = r4 << r6
                long r0 = r0 ^ r4
                goto L_0x00ce
            L_0x00ed:
                r0 = 14
                byte r0 = r14.get(r0)
                int r0 = com.google.common.primitives.UnsignedBytes.toInt(r0)
                long r0 = (long) r0
                r4 = 48
                long r0 = r0 << r4
                long r0 = r0 ^ r2
                goto L_0x00dd
            L_0x00fd:
                r0 = 6
                byte r0 = r14.get(r0)
                int r0 = com.google.common.primitives.UnsignedBytes.toInt(r0)
                long r0 = (long) r0
                r4 = 48
                long r0 = r0 << r4
                long r0 = r0 ^ r2
                goto L_0x007e
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.hash.Murmur3_128HashFunction.Murmur3_128Hasher.processRemaining(java.nio.ByteBuffer):void");
        }
    }

    Murmur3_128HashFunction(int i) {
        this.seed = i;
    }

    public int bits() {
        return 128;
    }

    public boolean equals(@Nullable Object obj) {
        return (obj instanceof Murmur3_128HashFunction) && this.seed == ((Murmur3_128HashFunction) obj).seed;
    }

    public int hashCode() {
        return getClass().hashCode() ^ this.seed;
    }

    public Hasher newHasher() {
        return new Murmur3_128Hasher(this.seed);
    }

    public String toString() {
        return "Hashing.murmur3_128(" + this.seed + ")";
    }
}
