package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
/* loaded from: classes.dex */
final class CharSequenceReader extends Reader {
    private int mark;
    private int pos;
    private CharSequence seq;

    public CharSequenceReader(CharSequence charSequence) {
        this.seq = (CharSequence) Preconditions.checkNotNull(charSequence);
    }

    private void checkOpen() throws IOException {
        if (this.seq == null) {
            throw new IOException("reader closed");
        }
    }

    private boolean hasRemaining() {
        return remaining() > 0;
    }

    private int remaining() {
        return this.seq.length() - this.pos;
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public synchronized void close() throws IOException {
        this.seq = null;
    }

    @Override // java.io.Reader
    public synchronized void mark(int i) throws IOException {
        synchronized (this) {
            Preconditions.checkArgument(i >= 0, "readAheadLimit (%s) may not be negative", Integer.valueOf(i));
            checkOpen();
            this.mark = this.pos;
        }
    }

    @Override // java.io.Reader
    public boolean markSupported() {
        return true;
    }

    @Override // java.io.Reader
    public synchronized int read() throws IOException {
        char charAt;
        checkOpen();
        if (hasRemaining()) {
            CharSequence charSequence = this.seq;
            int i = this.pos;
            this.pos = i + 1;
            charAt = charSequence.charAt(i);
        } else {
            charAt = 65535;
        }
        return charAt;
    }

    @Override // java.io.Reader, java.lang.Readable
    public synchronized int read(CharBuffer charBuffer) throws IOException {
        synchronized (this) {
            Preconditions.checkNotNull(charBuffer);
            checkOpen();
            if (hasRemaining()) {
                int min = Math.min(charBuffer.remaining(), remaining());
                for (int i = 0; i < min; i++) {
                    CharSequence charSequence = this.seq;
                    int i2 = this.pos;
                    this.pos = i2 + 1;
                    charBuffer.put(charSequence.charAt(i2));
                }
                return min;
            }
            return -1;
        }
    }

    @Override // java.io.Reader
    public synchronized int read(char[] cArr, int i, int i2) throws IOException {
        synchronized (this) {
            Preconditions.checkPositionIndexes(i, i + i2, cArr.length);
            checkOpen();
            if (hasRemaining()) {
                int min = Math.min(i2, remaining());
                for (int i3 = 0; i3 < min; i3++) {
                    int i4 = i + i3;
                    CharSequence charSequence = this.seq;
                    int i5 = this.pos;
                    this.pos = i5 + 1;
                    cArr[i4] = charSequence.charAt(i5);
                }
                return min;
            }
            return -1;
        }
    }

    @Override // java.io.Reader
    public synchronized boolean ready() throws IOException {
        checkOpen();
        return true;
    }

    @Override // java.io.Reader
    public synchronized void reset() throws IOException {
        checkOpen();
        this.pos = this.mark;
    }

    @Override // java.io.Reader
    public synchronized long skip(long j) throws IOException {
        long j2;
        synchronized (this) {
            Preconditions.checkArgument((j > 0L ? 1 : (j == 0L ? 0 : -1)) < 0 ? false : true, "n (%s) may not be negative", Long.valueOf(j));
            checkOpen();
            int min = (int) Math.min(remaining(), j);
            this.pos += min;
            j2 = min;
        }
        return j2;
    }
}
