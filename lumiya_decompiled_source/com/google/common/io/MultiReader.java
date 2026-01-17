package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import javax.annotation.Nullable;
/* loaded from: classes.dex */
class MultiReader extends Reader {
    private Reader current;
    private final Iterator<? extends CharSource> it;

    /* JADX INFO: Access modifiers changed from: package-private */
    public MultiReader(Iterator<? extends CharSource> it) throws IOException {
        this.it = it;
        advance();
    }

    private void advance() throws IOException {
        close();
        if (this.it.hasNext()) {
            this.current = this.it.next().openStream();
        }
    }

    @Override // java.io.Reader, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        if (this.current == null) {
            return;
        }
        try {
            this.current.close();
        } finally {
            this.current = null;
        }
    }

    @Override // java.io.Reader
    public int read(@Nullable char[] cArr, int i, int i2) throws IOException {
        if (this.current != null) {
            int read = this.current.read(cArr, i, i2);
            if (read != -1) {
                return read;
            }
            advance();
            return read(cArr, i, i2);
        }
        return -1;
    }

    @Override // java.io.Reader
    public boolean ready() throws IOException {
        return this.current != null && this.current.ready();
    }

    @Override // java.io.Reader
    public long skip(long j) throws IOException {
        Preconditions.checkArgument(!((j > 0L ? 1 : (j == 0L ? 0 : -1)) < 0), "n is negative");
        if (!(j <= 0)) {
            while (this.current != null) {
                long skip = this.current.skip(j);
                if (!(skip <= 0)) {
                    return skip;
                }
                advance();
            }
        }
        return 0L;
    }
}
