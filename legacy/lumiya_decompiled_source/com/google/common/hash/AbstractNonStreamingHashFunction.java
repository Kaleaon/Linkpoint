package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
/* loaded from: classes.dex */
abstract class AbstractNonStreamingHashFunction implements HashFunction {

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class BufferingHasher extends AbstractHasher {
        static final int BOTTOM_BYTE = 255;
        final ExposedByteArrayOutputStream stream;

        BufferingHasher(int i) {
            this.stream = new ExposedByteArrayOutputStream(i);
        }

        @Override // com.google.common.hash.Hasher
        public HashCode hash() {
            return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
        }

        @Override // com.google.common.hash.PrimitiveSink
        public Hasher putByte(byte b) {
            this.stream.write(b);
            return this;
        }

        @Override // com.google.common.hash.PrimitiveSink
        public Hasher putBytes(byte[] bArr) {
            try {
                this.stream.write(bArr);
                return this;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override // com.google.common.hash.PrimitiveSink
        public Hasher putBytes(byte[] bArr, int i, int i2) {
            this.stream.write(bArr, i, i2);
            return this;
        }

        @Override // com.google.common.hash.PrimitiveSink
        public Hasher putChar(char c) {
            this.stream.write(c & 255);
            this.stream.write((c >>> '\b') & 255);
            return this;
        }

        @Override // com.google.common.hash.PrimitiveSink
        public Hasher putInt(int i) {
            this.stream.write(i & 255);
            this.stream.write((i >>> 8) & 255);
            this.stream.write((i >>> 16) & 255);
            this.stream.write((i >>> 24) & 255);
            return this;
        }

        @Override // com.google.common.hash.PrimitiveSink
        public Hasher putLong(long j) {
            for (int i = 0; i < 64; i += 8) {
                this.stream.write((byte) ((j >>> i) & 255));
            }
            return this;
        }

        @Override // com.google.common.hash.Hasher
        public <T> Hasher putObject(T t, Funnel<? super T> funnel) {
            funnel.funnel(t, this);
            return this;
        }

        @Override // com.google.common.hash.PrimitiveSink
        public Hasher putShort(short s) {
            this.stream.write(s & 255);
            this.stream.write((s >>> 8) & 255);
            return this;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream {
        ExposedByteArrayOutputStream(int i) {
            super(i);
        }

        byte[] byteArray() {
            return this.buf;
        }

        int length() {
            return this.count;
        }
    }

    AbstractNonStreamingHashFunction() {
    }

    @Override // com.google.common.hash.HashFunction
    public HashCode hashBytes(byte[] bArr) {
        return hashBytes(bArr, 0, bArr.length);
    }

    @Override // com.google.common.hash.HashFunction
    public HashCode hashInt(int i) {
        return newHasher(4).putInt(i).hash();
    }

    @Override // com.google.common.hash.HashFunction
    public HashCode hashLong(long j) {
        return newHasher(8).putLong(j).hash();
    }

    @Override // com.google.common.hash.HashFunction
    public <T> HashCode hashObject(T t, Funnel<? super T> funnel) {
        return newHasher().putObject(t, funnel).hash();
    }

    @Override // com.google.common.hash.HashFunction
    public HashCode hashString(CharSequence charSequence, Charset charset) {
        return hashBytes(charSequence.toString().getBytes(charset));
    }

    @Override // com.google.common.hash.HashFunction
    public HashCode hashUnencodedChars(CharSequence charSequence) {
        int length = charSequence.length();
        Hasher newHasher = newHasher(length * 2);
        for (int i = 0; i < length; i++) {
            newHasher.putChar(charSequence.charAt(i));
        }
        return newHasher.hash();
    }

    @Override // com.google.common.hash.HashFunction
    public Hasher newHasher() {
        return new BufferingHasher(32);
    }

    @Override // com.google.common.hash.HashFunction
    public Hasher newHasher(int i) {
        Preconditions.checkArgument(i >= 0);
        return new BufferingHasher(i);
    }
}
