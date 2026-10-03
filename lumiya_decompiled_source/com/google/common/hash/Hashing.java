package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.google.common.logging.nano.Vr;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
@Beta
@CheckReturnValue
/* loaded from: classes.dex */
public final class Hashing {
    private static final int GOOD_FAST_HASH_SEED = (int) System.currentTimeMillis();

    /* loaded from: classes.dex */
    private static class Adler32Holder {
        static final HashFunction ADLER_32 = Hashing.checksumHashFunction(ChecksumType.ADLER_32, "Hashing.adler32()");

        private Adler32Holder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public enum ChecksumType implements Supplier<Checksum> {
        CRC_32(32) { // from class: com.google.common.hash.Hashing.ChecksumType.1
            @Override // com.google.common.hash.Hashing.ChecksumType, com.google.common.base.Supplier
            public Checksum get() {
                return new CRC32();
            }
        },
        ADLER_32(32) { // from class: com.google.common.hash.Hashing.ChecksumType.2
            @Override // com.google.common.hash.Hashing.ChecksumType, com.google.common.base.Supplier
            public Checksum get() {
                return new Adler32();
            }
        };
        
        private final int bits;

        ChecksumType(int i) {
            this.bits = i;
        }

        @Override // com.google.common.base.Supplier
        public abstract Checksum get();
    }

    /* loaded from: classes.dex */
    private static final class ConcatenatedHashFunction extends AbstractCompositeHashFunction {
        private final int bits;

        private ConcatenatedHashFunction(HashFunction... hashFunctionArr) {
            super(hashFunctionArr);
            int length = hashFunctionArr.length;
            int i = 0;
            int i2 = 0;
            while (i < length) {
                HashFunction hashFunction = hashFunctionArr[i];
                int bits = hashFunction.bits() + i2;
                Preconditions.checkArgument(hashFunction.bits() % 8 == 0, "the number of bits (%s) in hashFunction (%s) must be divisible by 8", Integer.valueOf(hashFunction.bits()), hashFunction);
                i++;
                i2 = bits;
            }
            this.bits = i2;
        }

        @Override // com.google.common.hash.HashFunction
        public int bits() {
            return this.bits;
        }

        public boolean equals(@Nullable Object obj) {
            if (obj instanceof ConcatenatedHashFunction) {
                return Arrays.equals(this.functions, ((ConcatenatedHashFunction) obj).functions);
            }
            return false;
        }

        public int hashCode() {
            return (Arrays.hashCode(this.functions) * 31) + this.bits;
        }

        @Override // com.google.common.hash.AbstractCompositeHashFunction
        HashCode makeHash(Hasher[] hasherArr) {
            byte[] bArr = new byte[this.bits / 8];
            int i = 0;
            for (Hasher hasher : hasherArr) {
                HashCode hash = hasher.hash();
                i += hash.writeBytesTo(bArr, i, hash.bits() / 8);
            }
            return HashCode.fromBytesNoCopy(bArr);
        }
    }

    /* loaded from: classes.dex */
    private static class Crc32Holder {
        static final HashFunction CRC_32 = Hashing.checksumHashFunction(ChecksumType.CRC_32, "Hashing.crc32()");

        private Crc32Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static final class Crc32cHolder {
        static final HashFunction CRC_32_C = new Crc32cHashFunction();

        private Crc32cHolder() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class LinearCongruentialGenerator {
        private long state;

        public LinearCongruentialGenerator(long j) {
            this.state = j;
        }

        public double nextDouble() {
            this.state = (this.state * 2862933555777941757L) + 1;
            return (((int) (this.state >>> 33)) + 1) / 2.147483648E9d;
        }
    }

    /* loaded from: classes.dex */
    private static class Md5Holder {
        static final HashFunction MD5 = new MessageDigestHashFunction("MD5", "Hashing.md5()");

        private Md5Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static class Murmur3_128Holder {
        static final HashFunction MURMUR3_128 = new Murmur3_128HashFunction(0);
        static final HashFunction GOOD_FAST_HASH_FUNCTION_128 = Hashing.murmur3_128(Hashing.GOOD_FAST_HASH_SEED);

        private Murmur3_128Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static class Murmur3_32Holder {
        static final HashFunction MURMUR3_32 = new Murmur3_32HashFunction(0);
        static final HashFunction GOOD_FAST_HASH_FUNCTION_32 = Hashing.murmur3_32(Hashing.GOOD_FAST_HASH_SEED);

        private Murmur3_32Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static class Sha1Holder {
        static final HashFunction SHA_1 = new MessageDigestHashFunction("SHA-1", "Hashing.sha1()");

        private Sha1Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static class Sha256Holder {
        static final HashFunction SHA_256 = new MessageDigestHashFunction("SHA-256", "Hashing.sha256()");

        private Sha256Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static class Sha384Holder {
        static final HashFunction SHA_384 = new MessageDigestHashFunction("SHA-384", "Hashing.sha384()");

        private Sha384Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static class Sha512Holder {
        static final HashFunction SHA_512 = new MessageDigestHashFunction("SHA-512", "Hashing.sha512()");

        private Sha512Holder() {
        }
    }

    /* loaded from: classes.dex */
    private static class SipHash24Holder {
        static final HashFunction SIP_HASH_24 = new SipHashFunction(2, 4, 506097522914230528L, 1084818905618843912L);

        private SipHash24Holder() {
        }
    }

    private Hashing() {
    }

    public static HashFunction adler32() {
        return Adler32Holder.ADLER_32;
    }

    static int checkPositiveAndMakeMultipleOf32(int i) {
        Preconditions.checkArgument(i > 0, "Number of bits must be positive");
        return (i + 31) & (-32);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static HashFunction checksumHashFunction(ChecksumType checksumType, String str) {
        return new ChecksumHashFunction(checksumType, checksumType.bits, str);
    }

    public static HashCode combineOrdered(Iterable<HashCode> iterable) {
        Iterator<HashCode> it = iterable.iterator();
        Preconditions.checkArgument(it.hasNext(), "Must be at least 1 hash code to combine.");
        byte[] bArr = new byte[it.next().bits() / 8];
        for (HashCode hashCode : iterable) {
            byte[] asBytes = hashCode.asBytes();
            Preconditions.checkArgument(asBytes.length == bArr.length, "All hashcodes must have the same bit length.");
            for (int i = 0; i < asBytes.length; i++) {
                bArr[i] = (byte) ((bArr[i] * 37) ^ asBytes[i]);
            }
        }
        return HashCode.fromBytesNoCopy(bArr);
    }

    public static HashCode combineUnordered(Iterable<HashCode> iterable) {
        Iterator<HashCode> it = iterable.iterator();
        Preconditions.checkArgument(it.hasNext(), "Must be at least 1 hash code to combine.");
        byte[] bArr = new byte[it.next().bits() / 8];
        for (HashCode hashCode : iterable) {
            byte[] asBytes = hashCode.asBytes();
            Preconditions.checkArgument(asBytes.length == bArr.length, "All hashcodes must have the same bit length.");
            for (int i = 0; i < asBytes.length; i++) {
                bArr[i] = (byte) (bArr[i] + asBytes[i]);
            }
        }
        return HashCode.fromBytesNoCopy(bArr);
    }

    public static HashFunction concatenating(HashFunction hashFunction, HashFunction hashFunction2, HashFunction... hashFunctionArr) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(hashFunction);
        arrayList.add(hashFunction2);
        for (HashFunction hashFunction3 : hashFunctionArr) {
            arrayList.add(hashFunction3);
        }
        return new ConcatenatedHashFunction((HashFunction[]) arrayList.toArray(new HashFunction[0]));
    }

    public static HashFunction concatenating(Iterable<HashFunction> iterable) {
        Preconditions.checkNotNull(iterable);
        ArrayList arrayList = new ArrayList();
        for (HashFunction hashFunction : iterable) {
            arrayList.add(hashFunction);
        }
        Preconditions.checkArgument(arrayList.size() > 0, "number of hash functions (%s) must be > 0", Integer.valueOf(arrayList.size()));
        return new ConcatenatedHashFunction((HashFunction[]) arrayList.toArray(new HashFunction[0]));
    }

    public static int consistentHash(long j, int i) {
        int i2;
        Preconditions.checkArgument(i > 0, "buckets must be positive: %s", Integer.valueOf(i));
        LinearCongruentialGenerator linearCongruentialGenerator = new LinearCongruentialGenerator(j);
        while (true) {
            int nextDouble = (int) ((i2 + 1) / linearCongruentialGenerator.nextDouble());
            i2 = (nextDouble >= 0 && nextDouble < i) ? nextDouble : 0;
        }
        return i2;
    }

    public static int consistentHash(HashCode hashCode, int i) {
        return consistentHash(hashCode.padToLong(), i);
    }

    public static HashFunction crc32() {
        return Crc32Holder.CRC_32;
    }

    public static HashFunction crc32c() {
        return Crc32cHolder.CRC_32_C;
    }

    public static HashFunction goodFastHash(int i) {
        int checkPositiveAndMakeMultipleOf32 = checkPositiveAndMakeMultipleOf32(i);
        if (checkPositiveAndMakeMultipleOf32 != 32) {
            if (checkPositiveAndMakeMultipleOf32 > 128) {
                int i2 = (checkPositiveAndMakeMultipleOf32 + Vr.VREvent.VrCore.ErrorCode.CONTROLLER_UNSTUCK) / 128;
                HashFunction[] hashFunctionArr = new HashFunction[i2];
                hashFunctionArr[0] = Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
                int i3 = GOOD_FAST_HASH_SEED;
                for (int i4 = 1; i4 < i2; i4++) {
                    i3 += 1500450271;
                    hashFunctionArr[i4] = murmur3_128(i3);
                }
                return new ConcatenatedHashFunction(hashFunctionArr);
            }
            return Murmur3_128Holder.GOOD_FAST_HASH_FUNCTION_128;
        }
        return Murmur3_32Holder.GOOD_FAST_HASH_FUNCTION_32;
    }

    public static HashFunction md5() {
        return Md5Holder.MD5;
    }

    public static HashFunction murmur3_128() {
        return Murmur3_128Holder.MURMUR3_128;
    }

    public static HashFunction murmur3_128(int i) {
        return new Murmur3_128HashFunction(i);
    }

    public static HashFunction murmur3_32() {
        return Murmur3_32Holder.MURMUR3_32;
    }

    public static HashFunction murmur3_32(int i) {
        return new Murmur3_32HashFunction(i);
    }

    public static HashFunction sha1() {
        return Sha1Holder.SHA_1;
    }

    public static HashFunction sha256() {
        return Sha256Holder.SHA_256;
    }

    public static HashFunction sha384() {
        return Sha384Holder.SHA_384;
    }

    public static HashFunction sha512() {
        return Sha512Holder.SHA_512;
    }

    public static HashFunction sipHash24() {
        return SipHash24Holder.SIP_HASH_24;
    }

    public static HashFunction sipHash24(long j, long j2) {
        return new SipHashFunction(2, 4, j, j2);
    }
}
