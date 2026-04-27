// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.io;

import java.io.IOException;
import com.google.common.base.Preconditions;
import java.io.OutputStream;
import com.google.common.annotations.Beta;
import java.io.FilterOutputStream;

@Beta
public final class CountingOutputStream extends FilterOutputStream
{
    private long count;
    
    public CountingOutputStream(final OutputStream outputStream) {
        super(Preconditions.checkNotNull(outputStream));
    }
    
    @Override
    public void close() throws IOException {
        this.out.close();
    }
    
    public long getCount() {
        return this.count;
    }
    
    @Override
    public void write(final int n) throws IOException {
        this.out.write(n);
        ++this.count;
    }
    
    @Override
    public void write(final byte[] b, final int off, final int len) throws IOException {
        this.out.write(b, off, len);
        this.count += len;
    }
}
