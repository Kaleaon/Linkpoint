package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

abstract class AbstractStreamingHashFunction implements HashFunction {

    protected static abstract class AbstractStreamingHasher extends AbstractHasher {
        private final ByteBuffer buffer;
        private final int bufferSize;
        private final int chunkSize;

        protected AbstractStreamingHasher(int i) {
            this(i, i);
        }

        protected AbstractStreamingHasher(int i, int i2) {
            Preconditions.checkArgument(i2 % i == 0);
            this.buffer = ByteBuffer.allocate(i2 + 7).order(ByteOrder.LITTLE_ENDIAN);
            this.bufferSize = i2;
            this.chunkSize = i;
        }

        private void munch() {
            this.buffer.flip();
            while (this.buffer.remaining() >= this.chunkSize) {
                process(this.buffer);
            }
            this.buffer.compact();
        }

        private void munchIfFull() {
            if (this.buffer.remaining() < 8) {
                munch();
            }
        }

        private Hasher putBytes(ByteBuffer byteBuffer) {
            if (byteBuffer.remaining() > this.buffer.remaining()) {
                int position = this.bufferSize - this.buffer.position();
                for (int i = 0; i < position; i++) {
                    this.buffer.put(byteBuffer.get());
                }
                munch();
                while (byteBuffer.remaining() >= this.chunkSize) {
                    process(byteBuffer);
                }
                this.buffer.put(byteBuffer);
                return this;
            }
            this.buffer.put(byteBuffer);
            munchIfFull();
            return this;
        }

        public final HashCode hash() {
            munch();
            this.buffer.flip();
            if (this.buffer.remaining() > 0) {
                processRemaining(this.buffer);
            }
            return makeHash();
        }

        /* access modifiers changed from: package-private */
        public abstract HashCode makeHash();

        /* access modifiers changed from: protected */
        public abstract void process(ByteBuffer byteBuffer);

        /* access modifiers changed from: protected */
        public void processRemaining(ByteBuffer byteBuffer) {
            byteBuffer.position(byteBuffer.limit());
            byteBuffer.limit(this.chunkSize + 7);
            while (byteBuffer.position() < this.chunkSize) {
                byteBuffer.putLong(0);
            }
            byteBuffer.limit(this.chunkSize);
            byteBuffer.flip();
            process(byteBuffer);
        }

        public final Hasher putByte(byte b) {
            this.buffer.put(b);
            munchIfFull();
            return this;
        }

        public final Hasher putBytes(byte[] bArr) {
            return putBytes(bArr, 0, bArr.length);
        }

        public final Hasher putBytes(byte[] bArr, int i, int i2) {
            return putBytes(ByteBuffer.wrap(bArr, i, i2).order(ByteOrder.LITTLE_ENDIAN));
        }

        public final Hasher putChar(char c) {
            this.buffer.putChar(c);
            munchIfFull();
            return this;
        }

        public final Hasher putInt(int i) {
            this.buffer.putInt(i);
            munchIfFull();
            return this;
        }

        public final Hasher putLong(long j) {
            this.buffer.putLong(j);
            munchIfFull();
            return this;
        }

        public final <T> Hasher putObject(T t, Funnel<? super T> funnel) {
            funnel.funnel(t, this);
            return this;
        }

        public final Hasher putShort(short s) {
            this.buffer.putShort(s);
            munchIfFull();
            return this;
        }

        public final Hasher putUnencodedChars(CharSequence charSequence) {
            for (int i = 0; i < charSequence.length(); i++) {
                putChar(charSequence.charAt(i));
            }
            return this;
        }
    }

    AbstractStreamingHashFunction() {
    }

    public HashCode hashBytes(byte[] bArr) {
        return newHasher().putBytes(bArr).hash();
    }

    public HashCode hashBytes(byte[] bArr, int i, int i2) {
        return newHasher().putBytes(bArr, i, i2).hash();
    }

    public HashCode hashInt(int i) {
        return newHasher().putInt(i).hash();
    }

    public HashCode hashLong(long j) {
        return newHasher().putLong(j).hash();
    }

    public <T> HashCode hashObject(T t, Funnel<? super T> funnel) {
        return newHasher().putObject(t, funnel).hash();
    }

    public HashCode hashString(CharSequence charSequence, Charset charset) {
        return newHasher().putString(charSequence, charset).hash();
    }

    public HashCode hashUnencodedChars(CharSequence charSequence) {
        return newHasher().putUnencodedChars(charSequence).hash();
    }

    public Hasher newHasher(int i) {
        boolean z = false;
        if (i >= 0) {
            z = true;
        }
        Preconditions.checkArgument(z);
        return newHasher();
    }
}
