// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.io.IOException;

public abstract class ForwardingSource implements Source
{
    private final Source delegate;
    
    public ForwardingSource(final Source delegate) {
        if (delegate != null) {
            this.delegate = delegate;
            return;
        }
        throw new IllegalArgumentException("delegate == null");
    }
    
    @Override
    public void close() throws IOException {
        this.delegate.close();
    }
    
    public final Source delegate() {
        return this.delegate;
    }
    
    @Override
    public long read(final Buffer buffer, final long n) throws IOException {
        return this.delegate.read(buffer, n);
    }
    
    @Override
    public Timeout timeout() {
        return this.delegate.timeout();
    }
    
    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "(" + this.delegate.toString() + ")";
    }
}
