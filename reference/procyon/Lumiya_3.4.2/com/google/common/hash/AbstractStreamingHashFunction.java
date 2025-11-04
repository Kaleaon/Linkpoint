// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.hash;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import com.google.common.base.Preconditions;
import java.nio.charset.Charset;

abstract class AbstractStreamingHashFunction implements HashFunction
{
    @Override
    public HashCode hashBytes(final byte[] array) {
        return this.newHasher().putBytes(array).hash();
    }
    
    @Override
    public HashCode hashBytes(final byte[] array, final int n, final int n2) {
        return this.newHasher().putBytes(array, n, n2).hash();
    }
    
    @Override
    public HashCode hashInt(final int n) {
        return this.newHasher().putInt(n).hash();
    }
    
    @Override
    public HashCode hashLong(final long n) {
        return this.newHasher().putLong(n).hash();
    }
    
    @Override
    public <T> HashCode hashObject(final T t, final Funnel<? super T> funnel) {
        return this.newHasher().putObject(t, funnel).hash();
    }
    
    @Override
    public HashCode hashString(final CharSequence charSequence, final Charset charset) {
        return this.newHasher().putString(charSequence, charset).hash();
    }
    
    @Override
    public HashCode hashUnencodedChars(final CharSequence charSequence) {
        return this.newHasher().putUnencodedChars(charSequence).hash();
    }
    
    @Override
    public Hasher newHasher(final int n) {
        boolean b = false;
        if (n >= 0) {
            b = true;
        }
        Preconditions.checkArgument(b);
        return this.newHasher();
    }
    
    protected abstract static class AbstractStreamingHasher extends AbstractHasher
    {
        private final ByteBuffer buffer;
        private final int bufferSize;
        private final int chunkSize;
        
        protected AbstractStreamingHasher(final int n) {
            this(n, n);
        }
        
        protected AbstractStreamingHasher(final int chunkSize, final int bufferSize) {
            boolean b = false;
            if (bufferSize % chunkSize == 0) {
                b = true;
            }
            Preconditions.checkArgument(b);
            this.buffer = ByteBuffer.allocate(bufferSize + 7).order(ByteOrder.LITTLE_ENDIAN);
            this.bufferSize = bufferSize;
            this.chunkSize = chunkSize;
        }
        
        private void munch() {
            this.buffer.flip();
            while (this.buffer.remaining() >= this.chunkSize) {
                this.process(this.buffer);
            }
            this.buffer.compact();
        }
        
        private void munchIfFull() {
            if (this.buffer.remaining() < 8) {
                this.munch();
            }
        }
        
        private Hasher putBytes(final ByteBuffer byteBuffer) {
            if (byteBuffer.remaining() > this.buffer.remaining()) {
                for (int bufferSize = this.bufferSize, position = this.buffer.position(), i = 0; i < bufferSize - position; ++i) {
                    this.buffer.put(byteBuffer.get());
                }
                this.munch();
                while (byteBuffer.remaining() >= this.chunkSize) {
                    this.process(byteBuffer);
                }
                this.buffer.put(byteBuffer);
                return this;
            }
            this.buffer.put(byteBuffer);
            this.munchIfFull();
            return this;
        }
        
        @Override
        public final HashCode hash() {
            this.munch();
            this.buffer.flip();
            if (this.buffer.remaining() > 0) {
                this.processRemaining(this.buffer);
            }
            return this.makeHash();
        }
        
        abstract HashCode makeHash();
        
        protected abstract void process(final ByteBuffer p0);
        
        protected void processRemaining(final ByteBuffer byteBuffer) {
            byteBuffer.position();
            byteBuffer.limit();
            while (byteBuffer.position() < this.chunkSize) {
                byteBuffer.putLong(0L);
            }
            byteBuffer.limit();
            byteBuffer.flip();
            this.process(byteBuffer);
        }
        
        @Override
        public final Hasher putByte(final byte b) {
            this.buffer.put(b);
            this.munchIfFull();
            return this;
        }
        
        @Override
        public final Hasher putBytes(final byte[] array) {
            return this.putBytes(array, 0, array.length);
        }
        
        @Override
        public final Hasher putBytes(final byte[] array, final int offset, final int length) {
            return this.putBytes(ByteBuffer.wrap(array, offset, length).order(ByteOrder.LITTLE_ENDIAN));
        }
        
        @Override
        public final Hasher putChar(final char c) {
            this.buffer.putChar(c);
            this.munchIfFull();
            return this;
        }
        
        @Override
        public final Hasher putInt(final int n) {
            this.buffer.putInt(n);
            this.munchIfFull();
            return this;
        }
        
        @Override
        public final Hasher putLong(final long n) {
            this.buffer.putLong(n);
            this.munchIfFull();
            return this;
        }
        
        @Override
        public final <T> Hasher putObject(final T t, final Funnel<? super T> funnel) {
            funnel.funnel(t, this);
            return this;
        }
        
        @Override
        public final Hasher putShort(final short n) {
            this.buffer.putShort(n);
            this.munchIfFull();
            return this;
        }
        
        @Override
        public final Hasher putUnencodedChars(final CharSequence charSequence) {
            for (int i = 0; i < charSequence.length(); ++i) {
                this.putChar(charSequence.charAt(i));
            }
            return this;
        }
    }
}
