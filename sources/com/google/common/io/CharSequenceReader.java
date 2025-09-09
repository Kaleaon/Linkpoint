package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;

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

    public synchronized void close() throws IOException {
        this.seq = null;
    }

    public synchronized void mark(int i) throws IOException {
        boolean z = false;
        synchronized (this) {
            if (i >= 0) {
                z = true;
            }
            Preconditions.checkArgument(z, "readAheadLimit (%s) may not be negative", Integer.valueOf(i));
            checkOpen();
            this.mark = this.pos;
        }
    }

    public boolean markSupported() {
        return true;
    }

    public synchronized int read() throws IOException {
        char charAt;
        checkOpen();
        if (!hasRemaining()) {
            charAt = 65535;
        } else {
            CharSequence charSequence = this.seq;
            int i = this.pos;
            this.pos = i + 1;
            charAt = charSequence.charAt(i);
        }
        return charAt;
    }

    public synchronized int read(CharBuffer charBuffer) throws IOException {
        synchronized (this) {
            Preconditions.checkNotNull(charBuffer);
            checkOpen();
            if (!hasRemaining()) {
                return -1;
            }
            int min = Math.min(charBuffer.remaining(), remaining());
            for (int i = 0; i < min; i++) {
                CharSequence charSequence = this.seq;
                int i2 = this.pos;
                this.pos = i2 + 1;
                charBuffer.put(charSequence.charAt(i2));
            }
            return min;
        }
    }

    public synchronized int read(char[] cArr, int i, int i2) throws IOException {
        synchronized (this) {
            Preconditions.checkPositionIndexes(i, i + i2, cArr.length);
            checkOpen();
            if (!hasRemaining()) {
                return -1;
            }
            int min = Math.min(i2, remaining());
            for (int i3 = 0; i3 < min; i3++) {
                int i4 = i + i3;
                CharSequence charSequence = this.seq;
                int i5 = this.pos;
                this.pos = i5 + 1;
                cArr[i4] = (char) charSequence.charAt(i5);
            }
            return min;
        }
    }

    public synchronized boolean ready() throws IOException {
        checkOpen();
        return true;
    }

    public synchronized void reset() throws IOException {
        checkOpen();
        this.pos = this.mark;
    }

    public synchronized long skip(long j) throws IOException {
        long j2;
        boolean z = true;
        synchronized (this) {
            if (j < 0) {
                z = false;
            }
            Preconditions.checkArgument(z, "n (%s) may not be negative", Long.valueOf(j));
            checkOpen();
            int min = (int) Math.min((long) remaining(), j);
            this.pos += min;
            j2 = (long) min;
        }
        return j2;
    }
}
