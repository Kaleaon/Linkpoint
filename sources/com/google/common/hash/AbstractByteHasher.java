package com.google.common.hash;

import com.google.common.base.Preconditions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

abstract class AbstractByteHasher extends AbstractHasher {
    private final ByteBuffer scratch = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);

    AbstractByteHasher() {
    }

    private Hasher update(int i) {
        try {
            update(this.scratch.array(), 0, i);
            return this;
        } finally {
            this.scratch.clear();
        }
    }

    public Hasher putByte(byte b) {
        update(b);
        return this;
    }

    public Hasher putBytes(byte[] bArr) {
        Preconditions.checkNotNull(bArr);
        update(bArr);
        return this;
    }

    public Hasher putBytes(byte[] bArr, int i, int i2) {
        Preconditions.checkPositionIndexes(i, i + i2, bArr.length);
        update(bArr, i, i2);
        return this;
    }

    public Hasher putChar(char c) {
        this.scratch.putChar(c);
        return update(2);
    }

    public Hasher putInt(int i) {
        this.scratch.putInt(i);
        return update(4);
    }

    public Hasher putLong(long j) {
        this.scratch.putLong(j);
        return update(8);
    }

    public <T> Hasher putObject(T t, Funnel<? super T> funnel) {
        funnel.funnel(t, this);
        return this;
    }

    public Hasher putShort(short s) {
        this.scratch.putShort(s);
        return update(2);
    }

    /* access modifiers changed from: protected */
    public abstract void update(byte b);

    /* access modifiers changed from: protected */
    public void update(byte[] bArr) {
        update(bArr, 0, bArr.length);
    }

    /* access modifiers changed from: protected */
    public void update(byte[] bArr, int i, int i2) {
        for (int i3 = i; i3 < i + i2; i3++) {
            update(bArr[i3]);
        }
    }
}
