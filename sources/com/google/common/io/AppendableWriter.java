package com.google.common.io;

import com.google.common.base.Preconditions;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import javax.annotation.Nullable;

class AppendableWriter extends Writer {
    private boolean closed;
    private final Appendable target;

    AppendableWriter(Appendable appendable) {
        this.target = (Appendable) Preconditions.checkNotNull(appendable);
    }

    private void checkNotClosed() throws IOException {
        if (this.closed) {
            throw new IOException("Cannot write to a closed writer.");
        }
    }

    public Writer append(char c) throws IOException {
        checkNotClosed();
        this.target.append(c);
        return this;
    }

    public Writer append(@Nullable CharSequence charSequence) throws IOException {
        checkNotClosed();
        this.target.append(charSequence);
        return this;
    }

    public Writer append(@Nullable CharSequence charSequence, int i, int i2) throws IOException {
        checkNotClosed();
        this.target.append(charSequence, i, i2);
        return this;
    }

    public void close() throws IOException {
        this.closed = true;
        if (this.target instanceof Closeable) {
            ((Closeable) this.target).close();
        }
    }

    public void flush() throws IOException {
        checkNotClosed();
        if (this.target instanceof Flushable) {
            ((Flushable) this.target).flush();
        }
    }

    public void write(int i) throws IOException {
        checkNotClosed();
        this.target.append((char) i);
    }

    public void write(@Nullable String str) throws IOException {
        checkNotClosed();
        this.target.append(str);
    }

    public void write(@Nullable String str, int i, int i2) throws IOException {
        checkNotClosed();
        this.target.append(str, i, i + i2);
    }

    public void write(char[] cArr, int i, int i2) throws IOException {
        checkNotClosed();
        this.target.append(new String(cArr, i, i2));
    }
}
