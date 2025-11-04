// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import javax.annotation.Nullable;
import java.io.IOException;
import com.google.common.base.Preconditions;
import java.util.Iterator;
import java.io.InputStream;

final class MultiInputStream extends InputStream
{
    private InputStream in;
    private Iterator<? extends ByteSource> it;
    
    public MultiInputStream(final Iterator<? extends ByteSource> iterator) throws IOException {
        this.it = Preconditions.checkNotNull(iterator);
        this.advance();
    }
    
    private void advance() throws IOException {
        this.close();
        if (this.it.hasNext()) {
            this.in = ((ByteSource)this.it.next()).openStream();
        }
    }
    
    @Override
    public int available() throws IOException {
        if (this.in != null) {
            return this.in.available();
        }
        return 0;
    }
    
    @Override
    public void close() throws IOException {
        if (this.in != null) {
            try {
                this.in.close();
            }
            finally {
                this.in = null;
            }
        }
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public int read() throws IOException {
        if (this.in == null) {
            return -1;
        }
        final int read = this.in.read();
        if (read != -1) {
            return read;
        }
        this.advance();
        return this.read();
    }
    
    @Override
    public int read(@Nullable final byte[] b, final int off, final int len) throws IOException {
        if (this.in == null) {
            return -1;
        }
        final int read = this.in.read(b, off, len);
        if (read != -1) {
            return read;
        }
        this.advance();
        return this.read(b, off, len);
    }
    
    @Override
    public long skip(final long n) throws IOException {
        if (this.in != null) {
            int n2;
            if (n > 0L) {
                n2 = 1;
            }
            else {
                n2 = 0;
            }
            if (n2 != 0) {
                final long skip = this.in.skip(n);
                if (skip != 0L) {
                    return skip;
                }
                if (this.read() != -1) {
                    return this.in.skip(n - 1L) + 1L;
                }
                return 0L;
            }
        }
        return 0L;
    }
}
