package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import java.io.Serializable;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;
@Beta
/* loaded from: classes.dex */
public abstract class HashCode {
    private static final char[] hexDigits = "0123456789abcdef".toCharArray();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class BytesHashCode extends HashCode implements Serializable {
        private static final long serialVersionUID = 0;
        final byte[] bytes;

        BytesHashCode(byte[] bArr) {
            this.bytes = (byte[]) Preconditions.checkNotNull(bArr);
        }

        @Override // com.google.common.hash.HashCode
        public byte[] asBytes() {
            return (byte[]) this.bytes.clone();
        }

        @Override // com.google.common.hash.HashCode
        public int asInt() {
            Preconditions.checkState(this.bytes.length >= 4, "HashCode#asInt() requires >= 4 bytes (it only has %s bytes).", Integer.valueOf(this.bytes.length));
            return (this.bytes[0] & UnsignedBytes.MAX_VALUE) | ((this.bytes[1] & UnsignedBytes.MAX_VALUE) << 8) | ((this.bytes[2] & UnsignedBytes.MAX_VALUE) << 16) | ((this.bytes[3] & UnsignedBytes.MAX_VALUE) << 24);
        }

        @Override // com.google.common.hash.HashCode
        public long asLong() {
            Preconditions.checkState(this.bytes.length >= 8, "HashCode#asLong() requires >= 8 bytes (it only has %s bytes).", Integer.valueOf(this.bytes.length));
            return padToLong();
        }

        @Override // com.google.common.hash.HashCode
        public int bits() {
            return this.bytes.length * 8;
        }

        @Override // com.google.common.hash.HashCode
        boolean equalsSameBits(HashCode hashCode) {
            if (this.bytes.length == hashCode.getBytesInternal().length) {
                boolean z = true;
                for (int i = 0; i < this.bytes.length; i++) {
                    z &= this.bytes[i] == hashCode.getBytesInternal()[i];
                }
                return z;
            }
            return false;
        }

        @Override // com.google.common.hash.HashCode
        byte[] getBytesInternal() {
            return this.bytes;
        }

        @Override // com.google.common.hash.HashCode
        public long padToLong() {
            long j = this.bytes[0] & UnsignedBytes.MAX_VALUE;
            for (int i = 1; i < Math.min(this.bytes.length, 8); i++) {
                j |= (this.bytes[i] & 255) << (i * 8);
            }
            return j;
        }

        @Override // com.google.common.hash.HashCode
        void writeBytesToImpl(byte[] bArr, int i, int i2) {
            System.arraycopy(this.bytes, 0, bArr, i, i2);
        }
    }

    /* loaded from: classes.dex */
    private static final class IntHashCode extends HashCode implements Serializable {
        private static final long serialVersionUID = 0;
        final int hash;

        IntHashCode(int i) {
            this.hash = i;
        }

        @Override // com.google.common.hash.HashCode
        public byte[] asBytes() {
            return new byte[]{(byte) this.hash, (byte) (this.hash >> 8), (byte) (this.hash >> 16), (byte) (this.hash >> 24)};
        }

        @Override // com.google.common.hash.HashCode
        public int asInt() {
            return this.hash;
        }

        @Override // com.google.common.hash.HashCode
        public long asLong() {
            throw new IllegalStateException("this HashCode only has 32 bits; cannot create a long");
        }

        @Override // com.google.common.hash.HashCode
        public int bits() {
            return 32;
        }

        @Override // com.google.common.hash.HashCode
        boolean equalsSameBits(HashCode hashCode) {
            return this.hash == hashCode.asInt();
        }

        @Override // com.google.common.hash.HashCode
        public long padToLong() {
            return UnsignedInts.toLong(this.hash);
        }

        @Override // com.google.common.hash.HashCode
        void writeBytesToImpl(byte[] bArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                bArr[i + i3] = (byte) (this.hash >> (i3 * 8));
            }
        }
    }

    /* loaded from: classes.dex */
    private static final class LongHashCode extends HashCode implements Serializable {
        private static final long serialVersionUID = 0;
        final long hash;

        LongHashCode(long j) {
            this.hash = j;
        }

        @Override // com.google.common.hash.HashCode
        public byte[] asBytes() {
            return new byte[]{(byte) this.hash, (byte) (this.hash >> 8), (byte) (this.hash >> 16), (byte) (this.hash >> 24), (byte) (this.hash >> 32), (byte) (this.hash >> 40), (byte) (this.hash >> 48), (byte) (this.hash >> 56)};
        }

        @Override // com.google.common.hash.HashCode
        public int asInt() {
            return (int) this.hash;
        }

        @Override // com.google.common.hash.HashCode
        public long asLong() {
            return this.hash;
        }

        @Override // com.google.common.hash.HashCode
        public int bits() {
            return 64;
        }

        @Override // com.google.common.hash.HashCode
        boolean equalsSameBits(HashCode hashCode) {
            return this.hash == hashCode.asLong();
        }

        @Override // com.google.common.hash.HashCode
        public long padToLong() {
            return this.hash;
        }

        @Override // com.google.common.hash.HashCode
        void writeBytesToImpl(byte[] bArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                bArr[i + i3] = (byte) (this.hash >> (i3 * 8));
            }
        }
    }

    HashCode() {
    }

    private static int decode(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return (c - 'a') + 10;
        }
        throw new IllegalArgumentException("Illegal hexadecimal character: " + c);
    }

    @CheckReturnValue
    public static HashCode fromBytes(byte[] bArr) {
        Preconditions.checkArgument(bArr.length >= 1, "A HashCode must contain at least 1 byte.");
        return fromBytesNoCopy((byte[]) bArr.clone());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static HashCode fromBytesNoCopy(byte[] bArr) {
        return new BytesHashCode(bArr);
    }

    @CheckReturnValue
    public static HashCode fromInt(int i) {
        return new IntHashCode(i);
    }

    @CheckReturnValue
    public static HashCode fromLong(long j) {
        return new LongHashCode(j);
    }

    @CheckReturnValue
    public static HashCode fromString(String str) {
        Preconditions.checkArgument(str.length() >= 2, "input string (%s) must have at least 2 characters", str);
        Preconditions.checkArgument(str.length() % 2 == 0, "input string (%s) must have an even number of characters", str);
        byte[] bArr = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            bArr[i / 2] = (byte) ((decode(str.charAt(i)) << 4) + decode(str.charAt(i + 1)));
        }
        return fromBytesNoCopy(bArr);
    }

    @CheckReturnValue
    public abstract byte[] asBytes();

    @CheckReturnValue
    public abstract int asInt();

    @CheckReturnValue
    public abstract long asLong();

    @CheckReturnValue
    public abstract int bits();

    public final boolean equals(@Nullable Object obj) {
        if (obj instanceof HashCode) {
            HashCode hashCode = (HashCode) obj;
            return bits() == hashCode.bits() && equalsSameBits(hashCode);
        }
        return false;
    }

    abstract boolean equalsSameBits(HashCode hashCode);

    /* JADX INFO: Access modifiers changed from: package-private */
    public byte[] getBytesInternal() {
        return asBytes();
    }

    public final int hashCode() {
        if (bits() < 32) {
            byte[] bytesInternal = getBytesInternal();
            int i = bytesInternal[0] & UnsignedBytes.MAX_VALUE;
            for (int i2 = 1; i2 < bytesInternal.length; i2++) {
                i |= (bytesInternal[i2] & UnsignedBytes.MAX_VALUE) << (i2 * 8);
            }
            return i;
        }
        return asInt();
    }

    @CheckReturnValue
    public abstract long padToLong();

    public final String toString() {
        byte[] bytesInternal = getBytesInternal();
        StringBuilder sb = new StringBuilder(bytesInternal.length * 2);
        for (byte b : bytesInternal) {
            sb.append(hexDigits[(b >> 4) & 15]).append(hexDigits[b & 15]);
        }
        return sb.toString();
    }

    public int writeBytesTo(byte[] bArr, int i, int i2) {
        int min = Ints.min(i2, bits() / 8);
        Preconditions.checkPositionIndexes(i, i + min, bArr.length);
        writeBytesToImpl(bArr, i, min);
        return min;
    }

    abstract void writeBytesToImpl(byte[] bArr, int i, int i2);
}
