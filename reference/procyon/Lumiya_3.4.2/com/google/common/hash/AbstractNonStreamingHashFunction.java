// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.nio.charset.Charset;

abstract class AbstractNonStreamingHashFunction implements HashFunction
{
    @Override
    public HashCode hashBytes(final byte[] array) {
        return this.hashBytes(array, 0, array.length);
    }
    
    @Override
    public HashCode hashInt(final int n) {
        return this.newHasher(4).putInt(n).hash();
    }
    
    @Override
    public HashCode hashLong(final long n) {
        return this.newHasher(8).putLong(n).hash();
    }
    
    @Override
    public <T> HashCode hashObject(final T t, final Funnel<? super T> funnel) {
        return this.newHasher().putObject(t, funnel).hash();
    }
    
    @Override
    public HashCode hashString(final CharSequence charSequence, final Charset charset) {
        return this.hashBytes(charSequence.toString().getBytes(charset));
    }
    
    @Override
    public HashCode hashUnencodedChars(final CharSequence charSequence) {
        final int length = charSequence.length();
        final Hasher hasher = this.newHasher(length * 2);
        for (int i = 0; i < length; ++i) {
            hasher.putChar(charSequence.charAt(i));
        }
        return hasher.hash();
    }
    
    @Override
    public Hasher newHasher() {
        return new BufferingHasher(32);
    }
    
    @Override
    public Hasher newHasher(final int n) {
        boolean b = false;
        if (n >= 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        return new BufferingHasher(n);
    }
    
    private final class BufferingHasher extends AbstractHasher
    {
        static final int BOTTOM_BYTE = 255;
        final ExposedByteArrayOutputStream stream;
        
        BufferingHasher(final int n) {
            this.stream = new ExposedByteArrayOutputStream(n);
        }
        
        @Override
        public HashCode hash() {
            return AbstractNonStreamingHashFunction.this.hashBytes(this.stream.byteArray(), 0, this.stream.length());
        }
        
        @Override
        public Hasher putByte(final byte b) {
            this.stream.write(b);
            return this;
        }
        
        @Override
        public Hasher putBytes(final byte[] b) {
            try {
                this.stream.write(b);
                return this;
            }
            catch (final IOException cause) {
                throw new RuntimeException(cause);
            }
        }
        
        @Override
        public Hasher putBytes(final byte[] b, final int off, final int len) {
            this.stream.write(b, off, len);
            return this;
        }
        
        @Override
        public Hasher putChar(final char c) {
            this.stream.write(c & '\u00ff');
            this.stream.write(c >>> 8 & 0xFF);
            return this;
        }
        
        @Override
        public Hasher putInt(final int n) {
            this.stream.write(n & 0xFF);
            this.stream.write(n >>> 8 & 0xFF);
            this.stream.write(n >>> 16 & 0xFF);
            this.stream.write(n >>> 24 & 0xFF);
            return this;
        }
        
        @Override
        public Hasher putLong(final long n) {
            for (int i = 0; i < 64; i += 8) {
                this.stream.write((byte)(n >>> i & 0xFFL));
            }
            return this;
        }
        
        @Override
        public <T> Hasher putObject(final T t, final Funnel<? super T> funnel) {
            funnel.funnel(t, this);
            return this;
        }
        
        @Override
        public Hasher putShort(final short n) {
            this.stream.write(n & 0xFF);
            this.stream.write(n >>> 8 & 0xFF);
            return this;
        }
    }
    
    private static final class ExposedByteArrayOutputStream extends ByteArrayOutputStream
    {
        ExposedByteArrayOutputStream(final int size) {
            super(size);
        }
        
        byte[] byteArray() {
            return this.buf;
        }
        
        int length() {
            return this.count;
        }
    }
}
