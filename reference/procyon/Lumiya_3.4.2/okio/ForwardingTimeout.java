// 
// Decompiled by Procyon v0.6.0
// 

package okio;

import java.util.concurrent.TimeUnit;
import java.io.IOException;

public class ForwardingTimeout extends Timeout
{
    private Timeout delegate;
    
    public ForwardingTimeout(final Timeout delegate) {
        if (delegate != null) {
            this.delegate = delegate;
            return;
        }
        throw new IllegalArgumentException("delegate == null");
    }
    
    @Override
    public Timeout clearDeadline() {
        return this.delegate.clearDeadline();
    }
    
    @Override
    public Timeout clearTimeout() {
        return this.delegate.clearTimeout();
    }
    
    @Override
    public long deadlineNanoTime() {
        return this.delegate.deadlineNanoTime();
    }
    
    @Override
    public Timeout deadlineNanoTime(final long n) {
        return this.delegate.deadlineNanoTime(n);
    }
    
    public final Timeout delegate() {
        return this.delegate;
    }
    
    @Override
    public boolean hasDeadline() {
        return this.delegate.hasDeadline();
    }
    
    public final ForwardingTimeout setDelegate(final Timeout delegate) {
        if (delegate != null) {
            this.delegate = delegate;
            return this;
        }
        throw new IllegalArgumentException("delegate == null");
    }
    
    @Override
    public void throwIfReached() throws IOException {
        this.delegate.throwIfReached();
    }
    
    @Override
    public Timeout timeout(final long n, final TimeUnit timeUnit) {
        return this.delegate.timeout(n, timeUnit);
    }
    
    @Override
    public long timeoutNanos() {
        return this.delegate.timeoutNanos();
    }
}
