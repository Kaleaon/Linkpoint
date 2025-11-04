// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import com.google.common.primitives.UnsignedInts;
import java.io.Serializable;
import com.google.common.primitives.Ints;
import javax.annotation.Nullable;
import javax.annotation.CheckReturnValue;
import com.google.common.base.Preconditions;
import com.google.common.annotations.Beta;

@Beta
public abstract class HashCode
{
    private static final char[] hexDigits;
    
    static {
        hexDigits = "0123456789abcdef".toCharArray();
    }
    
    HashCode() {
    }
    
    private static int decode(final char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c >= 'a' && c <= 'f') {
            return c - 'a' + 10;
        }
        throw new IllegalArgumentException("Illegal hexadecimal character: " + c);
    }
    
    @CheckReturnValue
    public static HashCode fromBytes(final byte[] array) {
        boolean b = true;
        if (array.length < 1) {
            b = false;
        }
        Preconditions.checkArgument(b, (Object)"A HashCode must contain at least 1 byte.");
        return fromBytesNoCopy(array.clone());
    }
    
    static HashCode fromBytesNoCopy(final byte[] array) {
        return new BytesHashCode(array);
    }
    
    @CheckReturnValue
    public static HashCode fromInt(final int n) {
        return new IntHashCode(n);
    }
    
    @CheckReturnValue
    public static HashCode fromLong(final long n) {
        return new LongHashCode(n);
    }
    
    @CheckReturnValue
    public static HashCode fromString(final String s) {
        int i = 0;
        Preconditions.checkArgument(s.length() >= 2, "input string (%s) must have at least 2 characters", s);
        Preconditions.checkArgument(s.length() % 2 == 0, "input string (%s) must have an even number of characters", s);
        final byte[] array = new byte[s.length() / 2];
        while (i < s.length()) {
            array[i / 2] = (byte)((decode(s.charAt(i)) << 4) + decode(s.charAt(i + 1)));
            i += 2;
        }
        return fromBytesNoCopy(array);
    }
    
    @CheckReturnValue
    public abstract byte[] asBytes();
    
    @CheckReturnValue
    public abstract int asInt();
    
    @CheckReturnValue
    public abstract long asLong();
    
    @CheckReturnValue
    public abstract int bits();
    
    @Override
    public final boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof HashCode)) {
            return false;
        }
        final HashCode hashCode = (HashCode)o;
        if (this.bits() == hashCode.bits() && this.equalsSameBits(hashCode)) {
            b = true;
        }
        return b;
    }
    
    abstract boolean equalsSameBits(final HashCode p0);
    
    byte[] getBytesInternal() {
        return this.asBytes();
    }
    
    @Override
    public final int hashCode() {
        if (this.bits() < 32) {
            final byte[] bytesInternal = this.getBytesInternal();
            int n = bytesInternal[0] & 0xFF;
            for (int i = 1; i < bytesInternal.length; ++i) {
                n |= (bytesInternal[i] & 0xFF) << i * 8;
            }
            return n;
        }
        return this.asInt();
    }
    
    @CheckReturnValue
    public abstract long padToLong();
    
    @Override
    public final String toString() {
        final byte[] bytesInternal = this.getBytesInternal();
        final StringBuilder sb = new StringBuilder(bytesInternal.length * 2);
        for (final byte b : bytesInternal) {
            sb.append(HashCode.hexDigits[b >> 4 & 0xF]).append(HashCode.hexDigits[b & 0xF]);
        }
        return sb.toString();
    }
    
    public int writeBytesTo(final byte[] array, final int n, int min) {
        min = Ints.min(min, this.bits() / 8);
        Preconditions.checkPositionIndexes(n, n + min, array.length);
        this.writeBytesToImpl(array, n, min);
        return min;
    }
    
    abstract void writeBytesToImpl(final byte[] p0, final int p1, final int p2);
    
    private static final class BytesHashCode extends HashCode implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final byte[] bytes;
        
        BytesHashCode(final byte[] array) {
            this.bytes = Preconditions.checkNotNull(array);
        }
        
        @Override
        public byte[] asBytes() {
            return this.bytes.clone();
        }
        
        @Override
        public int asInt() {
            Preconditions.checkState(this.bytes.length >= 4, "HashCode#asInt() requires >= 4 bytes (it only has %s bytes).", this.bytes.length);
            return (this.bytes[0] & 0xFF) | (this.bytes[1] & 0xFF) << 8 | (this.bytes[2] & 0xFF) << 16 | (this.bytes[3] & 0xFF) << 24;
        }
        
        @Override
        public long asLong() {
            Preconditions.checkState(this.bytes.length >= 8, "HashCode#asLong() requires >= 8 bytes (it only has %s bytes).", this.bytes.length);
            return this.padToLong();
        }
        
        @Override
        public int bits() {
            return this.bytes.length * 8;
        }
        
        @Override
        boolean equalsSameBits(final HashCode hashCode) {
            if (this.bytes.length == hashCode.getBytesInternal().length) {
                int i = 0;
                boolean b = true;
                while (i < this.bytes.length) {
                    b &= (this.bytes[i] == hashCode.getBytesInternal()[i]);
                    ++i;
                }
                return b;
            }
            return false;
        }
        
        @Override
        byte[] getBytesInternal() {
            return this.bytes;
        }
        
        @Override
        public long padToLong() {
            long n = this.bytes[0] & 0xFF;
            for (int i = 1; i < Math.min(this.bytes.length, 8); ++i) {
                n |= ((long)this.bytes[i] & 0xFFL) << i * 8;
            }
            return n;
        }
        
        @Override
        void writeBytesToImpl(final byte[] array, final int n, final int n2) {
            System.arraycopy(this.bytes, 0, array, n, n2);
        }
    }
    
    private static final class IntHashCode extends HashCode implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final int hash;
        
        IntHashCode(final int hash) {
            this.hash = hash;
        }
        
        @Override
        public byte[] asBytes() {
            return new byte[] { (byte)this.hash, (byte)(this.hash >> 8), (byte)(this.hash >> 16), (byte)(this.hash >> 24) };
        }
        
        @Override
        public int asInt() {
            return this.hash;
        }
        
        @Override
        public long asLong() {
            throw new IllegalStateException("this HashCode only has 32 bits; cannot create a long");
        }
        
        @Override
        public int bits() {
            return 32;
        }
        
        @Override
        boolean equalsSameBits(final HashCode hashCode) {
            return this.hash == hashCode.asInt();
        }
        
        @Override
        public long padToLong() {
            return UnsignedInts.toLong(this.hash);
        }
        
        @Override
        void writeBytesToImpl(final byte[] array, final int n, final int n2) {
            for (int i = 0; i < n2; ++i) {
                array[n + i] = (byte)(this.hash >> i * 8);
            }
        }
    }
    
    private static final class LongHashCode extends HashCode implements Serializable
    {
        private static final long serialVersionUID = 0L;
        final long hash;
        
        LongHashCode(final long hash) {
            this.hash = hash;
        }
        
        @Override
        public byte[] asBytes() {
            return new byte[] { (byte)this.hash, (byte)(this.hash >> 8), (byte)(this.hash >> 16), (byte)(this.hash >> 24), (byte)(this.hash >> 32), (byte)(this.hash >> 40), (byte)(this.hash >> 48), (byte)(this.hash >> 56) };
        }
        
        @Override
        public int asInt() {
            return (int)this.hash;
        }
        
        @Override
        public long asLong() {
            return this.hash;
        }
        
        @Override
        public int bits() {
            return 64;
        }
        
        @Override
        boolean equalsSameBits(final HashCode hashCode) {
            return this.hash == hashCode.asLong();
        }
        
        @Override
        public long padToLong() {
            return this.hash;
        }
        
        @Override
        void writeBytesToImpl(final byte[] array, final int n, final int n2) {
            for (int i = 0; i < n2; ++i) {
                array[n + i] = (byte)(this.hash >> i * 8);
            }
        }
    }
}
