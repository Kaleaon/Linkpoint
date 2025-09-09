package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.IOException;
import java.io.Reader;
import java.util.Iterator;
import javax.annotation.Nullable;

class MultiReader extends Reader {
    private Reader current;
    private final Iterator<? extends CharSource> it;

    MultiReader(Iterator<? extends CharSource> it2) throws IOException {
        this.it = it2;
        advance();
    }

    private void advance() throws IOException {
        close();
        if (this.it.hasNext()) {
            this.current = ((CharSource) this.it.next()).openStream();
        }
    }

    public void close() throws IOException {
        if (this.current != null) {
            try {
                this.current.close();
            } finally {
                this.current = null;
            }
        }
    }

    public int read(@Nullable char[] cArr, int i, int i2) throws IOException {
        if (this.current == null) {
            return -1;
        }
        int read = this.current.read(cArr, i, i2);
        if (read != -1) {
            return read;
        }
        advance();
        return read(cArr, i, i2);
    }

    public boolean ready() throws IOException {
        return this.current != null && this.current.ready();
    }

    public long skip(long j) throws IOException {
        Preconditions.checkArgument(!((j > 0 ? 1 : (j == 0 ? 0 : -1)) < 0), "n is negative");
        if (!(j <= 0)) {
            while (this.current != null) {
                long skip = this.current.skip(j);
                if (!(skip <= 0)) {
                    return skip;
                }
                advance();
            }
        }
        return 0;
    }
}
