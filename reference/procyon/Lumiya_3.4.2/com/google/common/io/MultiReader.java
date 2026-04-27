// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import com.google.common.base.Preconditions;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Iterator;
import java.io.Reader;

class MultiReader extends Reader
{
    private Reader current;
    private final Iterator<? extends CharSource> it;
    
    MultiReader(final Iterator<? extends CharSource> it) throws IOException {
        this.it = it;
        this.advance();
    }
    
    private void advance() throws IOException {
        this.close();
        if (this.it.hasNext()) {
            this.current = ((CharSource)this.it.next()).openStream();
        }
    }
    
    @Override
    public void close() throws IOException {
        if (this.current != null) {
            try {
                this.current.close();
            }
            finally {
                this.current = null;
            }
        }
    }
    
    @Override
    public int read(@Nullable final char[] array, final int n, final int n2) throws IOException {
        if (this.current == null) {
            return -1;
        }
        final int read = this.current.read(array, n, n2);
        if (read != -1) {
            return read;
        }
        this.advance();
        return this.read(array, n, n2);
    }
    
    @Override
    public boolean ready() throws IOException {
        boolean b = false;
        if (this.current != null && this.current.ready()) {
            b = true;
        }
        return b;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        boolean b;
        if (n < 0L) {
            b = true;
        }
        else {
            b = false;
        }
        Preconditions.checkArgument(!b, (Object)"n is negative");
        int n2;
        if (n <= 0L) {
            n2 = 1;
        }
        else {
            n2 = 0;
        }
        if (n2 == 0) {
            while (this.current != null) {
                final long skip = this.current.skip(n);
                int n3;
                if (skip <= 0L) {
                    n3 = 1;
                }
                else {
                    n3 = 0;
                }
                if (n3 == 0) {
                    return skip;
                }
                this.advance();
            }
        }
        return 0L;
    }
}
