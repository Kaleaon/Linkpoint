package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
final class MultiInputStream extends InputStream {
    private InputStream in;
    private Iterator<? extends ByteSource> it;

    public MultiInputStream(Iterator<? extends ByteSource> it) throws IOException {
        this.it = (Iterator) Preconditions.checkNotNull(it);
        advance();
    }

    private void advance() throws IOException {
        close();
        if (this.it.hasNext()) {
            this.in = this.it.next().openStream();
        }
    }

    @Override // java.io.InputStream
    public int available() throws IOException {
        if (this.in != null) {
            return this.in.available();
        }
        return 0;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.in == null) {
            return;
        }
        try {
            this.in.close();
        } finally {
            this.in = null;
        }
    }

    @Override // java.io.InputStream
    public boolean markSupported() {
        return false;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        if (this.in != null) {
            int read = this.in.read();
            if (read != -1) {
                return read;
            }
            advance();
            return read();
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(@Nullable byte[] bArr, int i, int i2) throws IOException {
        if (this.in != null) {
            int read = this.in.read(bArr, i, i2);
            if (read != -1) {
                return read;
            }
            advance();
            return read(bArr, i, i2);
        }
        return -1;
    }

    @Override // java.io.InputStream
    public long skip(long j) throws IOException {
        if (this.in != null) {
            if (j > 0) {
                long skip = this.in.skip(j);
                if (skip != 0) {
                    return skip;
                }
                if (read() != -1) {
                    return this.in.skip(j - 1) + 1;
                }
                return 0L;
            }
        }
        return 0L;
    }
}
