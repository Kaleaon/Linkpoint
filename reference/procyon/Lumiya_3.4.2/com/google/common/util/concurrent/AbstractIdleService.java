// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.util.concurrent;

import java.util.concurrent.TimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executor;
import com.google.common.base.Supplier;
import com.google.common.annotations.Beta;

@Beta
public abstract class AbstractIdleService implements Service
{
    private final Service delegate;
    private final Supplier<String> threadNameSupplier;
    
    protected AbstractIdleService() {
        this.threadNameSupplier = new ThreadNameSupplier();
        this.delegate = new DelegateService();
    }
    
    @Override
    public final void addListener(final Listener listener, final Executor executor) {
        this.delegate.addListener(listener, executor);
    }
    
    @Override
    public final void awaitRunning() {
        this.delegate.awaitRunning();
    }
    
    @Override
    public final void awaitRunning(final long n, final TimeUnit timeUnit) throws TimeoutException {
        this.delegate.awaitRunning(n, timeUnit);
    }
    
    @Override
    public final void awaitTerminated() {
        this.delegate.awaitTerminated();
    }
    
    @Override
    public final void awaitTerminated(final long n, final TimeUnit timeUnit) throws TimeoutException {
        this.delegate.awaitTerminated(n, timeUnit);
    }
    
    protected Executor executor() {
        return new Executor() {
            @Override
            public void execute(final Runnable runnable) {
                MoreExecutors.newThread(AbstractIdleService.this.threadNameSupplier.get(), runnable).start();
            }
        };
    }
    
    @Override
    public final Throwable failureCause() {
        return this.delegate.failureCause();
    }
    
    @Override
    public final boolean isRunning() {
        return this.delegate.isRunning();
    }
    
    protected String serviceName() {
        return this.getClass().getSimpleName();
    }
    
    protected abstract void shutDown() throws Exception;
    
    @Override
    public final Service startAsync() {
        this.delegate.startAsync();
        return this;
    }
    
    protected abstract void startUp() throws Exception;
    
    @Override
    public final State state() {
        return this.delegate.state();
    }
    
    @Override
    public final Service stopAsync() {
        this.delegate.stopAsync();
        return this;
    }
    
    @Override
    public String toString() {
        return this.serviceName() + " [" + this.state() + "]";
    }
    
    private final class DelegateService extends AbstractService
    {
        @Override
        protected final void doStart() {
            MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        AbstractIdleService.this.startUp();
                        DelegateService.this.notifyStarted();
                    }
                    catch (final Throwable t) {
                        DelegateService.this.notifyFailed(t);
                    }
                }
            });
        }
        
        @Override
        protected final void doStop() {
            MoreExecutors.renamingDecorator(AbstractIdleService.this.executor(), AbstractIdleService.this.threadNameSupplier).execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        AbstractIdleService.this.shutDown();
                        DelegateService.this.notifyStopped();
                    }
                    catch (final Throwable t) {
                        DelegateService.this.notifyFailed(t);
                    }
                }
            });
        }
        
        @Override
        public String toString() {
            return AbstractIdleService.this.toString();
        }
    }
    
    private final class ThreadNameSupplier implements Supplier<String>
    {
        @Override
        public String get() {
            return AbstractIdleService.this.serviceName() + " " + AbstractIdleService.this.state();
        }
    }
}
