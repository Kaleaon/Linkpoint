// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.Flushable;
import java.io.Closeable;
import javax.annotation.Nullable;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.io.Writer;

class AppendableWriter extends Writer
{
    private boolean closed;
    private final Appendable target;
    
    AppendableWriter(final Appendable appendable) {
        this.target = Preconditions.checkNotNull(appendable);
    }
    
    private void checkNotClosed() throws IOException {
        if (!this.closed) {
            return;
        }
        throw new IOException("Cannot write to a closed writer.");
    }
    
    @Override
    public Writer append(final char c) throws IOException {
        this.checkNotClosed();
        this.target.append(c);
        return this;
    }
    
    @Override
    public Writer append(@Nullable final CharSequence charSequence) throws IOException {
        this.checkNotClosed();
        this.target.append(charSequence);
        return this;
    }
    
    @Override
    public Writer append(@Nullable final CharSequence charSequence, final int n, final int n2) throws IOException {
        this.checkNotClosed();
        this.target.append(charSequence, n, n2);
        return this;
    }
    
    @Override
    public void close() throws IOException {
        this.closed = true;
        if (this.target instanceof Closeable) {
            ((Closeable)this.target).close();
        }
    }
    
    @Override
    public void flush() throws IOException {
        this.checkNotClosed();
        if (this.target instanceof Flushable) {
            ((Flushable)this.target).flush();
        }
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.checkNotClosed();
        this.target.append((char)n);
    }
    
    @Override
    public void write(@Nullable final String s) throws IOException {
        this.checkNotClosed();
        this.target.append(s);
    }
    
    @Override
    public void write(@Nullable final String s, final int n, final int n2) throws IOException {
        this.checkNotClosed();
        this.target.append(s, n, n + n2);
    }
    
    @Override
    public void write(final char[] value, final int offset, final int count) throws IOException {
        this.checkNotClosed();
        this.target.append(new String(value, offset, count));
    }
}
