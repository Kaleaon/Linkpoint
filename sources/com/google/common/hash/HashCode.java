package com.google.common.hash;

import com.google.common.annotations.Beta;
import com.google.common.base.Ascii;
import com.google.common.base.Preconditions;
import com.google.common.primitives.Ints;
import com.google.common.primitives.UnsignedBytes;
import com.google.common.primitives.UnsignedInts;
import java.io.Serializable;
import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

@Beta
public abstract class HashCode {
    private static final char[] hexDigits = "0123456789abcdef".toCharArray();

    private static final class BytesHashCode extends HashCode implements Serializable {
        private static final long serialVersionUID = 0;
        final byte[] bytes;

        BytesHashCode(byte[] bArr) {
            this.bytes = (byte[]) Preconditions.checkNotNull(bArr);
        }

        public byte[] asBytes() {
            return (byte[]) this.bytes.clone();
        }

        public int asInt() {
            Preconditions.checkState(this.bytes.length >= 4, "HashCode#asInt() requires >= 4 bytes (it only has %s bytes).", Integer.valueOf(this.bytes.length));
            return (this.bytes[0] & UnsignedBytes.MAX_VALUE) | ((this.bytes[1] & UnsignedBytes.MAX_VALUE) << 8) | ((this.bytes[2] & UnsignedBytes.MAX_VALUE) << 16) | ((this.bytes[3] & UnsignedBytes.MAX_VALUE) << Ascii.CAN);
        }

        public long asLong() {
            Preconditions.checkState(this.bytes.length >= 8, "HashCode#asLong() requires >= 8 bytes (it only has %s bytes).", Integer.valueOf(this.bytes.length));
            return padToLong();
        }

        public int bits() {
            return this.bytes.length * 8;
        }

        /* access modifiers changed from: package-private */
        public boolean equalsSameBits(HashCode hashCode) {
            if (this.bytes.length != hashCode.getBytesInternal().length) {
                return false;
            }
            boolean z = true;
            for (int i = 0; i < this.bytes.length; i++) {
                z &= this.bytes[i] == hashCode.getBytesInternal()[i];
            }
            return z;
        }

        /* access modifiers changed from: package-private */
        public byte[] getBytesInternal() {
            return this.bytes;
        }

        public long padToLong() {
            long j = (long) (this.bytes[0] & UnsignedBytes.MAX_VALUE);
            for (int i = 1; i < Math.min(this.bytes.length, 8); i++) {
                j |= (((long) this.bytes[i]) & 255) << (i * 8);
            }
            return j;
        }

        /* access modifiers changed from: package-private */
        public void writeBytesToImpl(byte[] bArr, int i, int i2) {
            System.arraycopy(this.bytes, 0, bArr, i, i2);
        }
    }

    private static final class IntHashCode extends HashCode implements Serializable {
        private static final long serialVersionUID = 0;
        final int hash;

        IntHashCode(int i) {
            this.hash = i;
        }

        public byte[] asBytes() {
            return new byte[]{(byte) ((byte) this.hash), (byte) ((byte) (this.hash >> 8)), (byte) ((byte) (this.hash >> 16)), (byte) ((byte) (this.hash >> 24))};
        }

        public int asInt() {
            return this.hash;
        }

        public long asLong() {
            throw new IllegalStateException("this HashCode only has 32 bits; cannot create a long");
        }

        public int bits() {
            return 32;
        }

        /* access modifiers changed from: package-private */
        public boolean equalsSameBits(HashCode hashCode) {
            return this.hash == hashCode.asInt();
        }

        public long padToLong() {
            return UnsignedInts.toLong(this.hash);
        }

        /* access modifiers changed from: package-private */
        public void writeBytesToImpl(byte[] bArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                bArr[i + i3] = (byte) ((byte) (this.hash >> (i3 * 8)));
            }
        }
    }

    private static final class LongHashCode extends HashCode implements Serializable {
        private static final long serialVersionUID = 0;
        final long hash;

        LongHashCode(long j) {
            this.hash = j;
        }

        public byte[] asBytes() {
            return new byte[]{(byte) ((byte) ((int) this.hash)), (byte) ((byte) ((int) (this.hash >> 8))), (byte) ((byte) ((int) (this.hash >> 16))), (byte) ((byte) ((int) (this.hash >> 24))), (byte) ((byte) ((int) (this.hash >> 32))), (byte) ((byte) ((int) (this.hash >> 40))), (byte) ((byte) ((int) (this.hash >> 48))), (byte) ((byte) ((int) (this.hash >> 56)))};
        }

        public int asInt() {
            return (int) this.hash;
        }

        public long asLong() {
            return this.hash;
        }

        public int bits() {
            return 64;
        }

        /* access modifiers changed from: package-private */
        public boolean equalsSameBits(HashCode hashCode) {
            return this.hash == hashCode.asLong();
        }

        public long padToLong() {
            return this.hash;
        }

        /* access modifiers changed from: package-private */
        public void writeBytesToImpl(byte[] bArr, int i, int i2) {
            for (int i3 = 0; i3 < i2; i3++) {
                bArr[i + i3] = (byte) ((byte) ((int) (this.hash >> (i3 * 8))));
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
        boolean z = true;
        if (bArr.length < 1) {
            z = false;
        }
        Preconditions.checkArgument(z, "A HashCode must contain at least 1 byte.");
        return fromBytesNoCopy((byte[]) bArr.clone());
    }

    static HashCode fromBytesNoCopy(byte[] bArr) {
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
        byte[] bArr = new byte[(str.length() / 2)];
        for (int i = 0; i < str.length(); i += 2) {
            bArr[i / 2] = (byte) ((byte) ((decode(str.charAt(i)) << 4) + decode(str.charAt(i + 1))));
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
        if (!(obj instanceof HashCode)) {
            return false;
        }
        HashCode hashCode = (HashCode) obj;
        return bits() == hashCode.bits() && equalsSameBits(hashCode);
    }

    /* access modifiers changed from: package-private */
    public abstract boolean equalsSameBits(HashCode hashCode);

    /* access modifiers changed from: package-private */
    public byte[] getBytesInternal() {
        return asBytes();
    }

    public final int hashCode() {
        if (bits() >= 32) {
            return asInt();
        }
        byte[] bytesInternal = getBytesInternal();
        byte b = bytesInternal[0] & UnsignedBytes.MAX_VALUE;
        for (int i = 1; i < bytesInternal.length; i++) {
            b |= (bytesInternal[i] & UnsignedBytes.MAX_VALUE) << (i * 8);
        }
        return b;
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

    /* access modifiers changed from: package-private */
    public abstract void writeBytesToImpl(byte[] bArr, int i, int i2);
}
