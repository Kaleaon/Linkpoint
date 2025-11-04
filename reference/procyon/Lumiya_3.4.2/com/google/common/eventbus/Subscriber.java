// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.eventbus;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import com.google.common.base.Preconditions;
import com.google.common.annotations.VisibleForTesting;
import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import com.google.j2objc.annotations.Weak;

class Subscriber
{
    @Weak
    private EventBus bus;
    private final Executor executor;
    private final Method method;
    @VisibleForTesting
    final Object target;
    
    private Subscriber(final EventBus bus, final Object o, final Method method) {
        this.bus = bus;
        this.target = Preconditions.checkNotNull(o);
        (this.method = method).setAccessible(true);
        this.executor = bus.executor();
    }
    
    private SubscriberExceptionContext context(final Object o) {
        return new SubscriberExceptionContext(this.bus, o, this.target, this.method);
    }
    
    static Subscriber create(final EventBus eventBus, final Object o, final Method method) {
        Subscriber subscriber;
        if (!isDeclaredThreadSafe(method)) {
            subscriber = new SynchronizedSubscriber(eventBus, o, method);
        }
        else {
            subscriber = new Subscriber(eventBus, o, method);
        }
        return subscriber;
    }
    
    private static boolean isDeclaredThreadSafe(final Method method) {
        return method.getAnnotation(AllowConcurrentEvents.class) != null;
    }
    
    final void dispatchEvent(final Object o) {
        this.executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Subscriber.this.invokeSubscriberMethod(o);
                }
                catch (final InvocationTargetException ex) {
                    Subscriber.this.bus.handleSubscriberException(ex.getCause(), Subscriber.this.context(o));
                }
            }
        });
    }
    
    @Override
    public final boolean equals(@Nullable final Object o) {
        boolean b = false;
        if (!(o instanceof Subscriber)) {
            return false;
        }
        final Subscriber subscriber = (Subscriber)o;
        if (this.target == subscriber.target && this.method.equals(subscriber.method)) {
            b = true;
        }
        return b;
    }
    
    @Override
    public final int hashCode() {
        return (this.method.hashCode() + 31) * 31 + System.identityHashCode(this.target);
    }
    
    @VisibleForTesting
    void invokeSubscriberMethod(final Object o) throws InvocationTargetException {
        try {
            this.method.invoke(this.target, Preconditions.checkNotNull(o));
        }
        catch (final IllegalArgumentException cause) {
            throw new Error("Method rejected target/argument: " + o, cause);
        }
        catch (final IllegalAccessException cause2) {
            throw new Error("Method became inaccessible: " + o, cause2);
        }
        catch (final InvocationTargetException ex) {
            if (!(ex.getCause() instanceof Error)) {
                throw ex;
            }
            throw (Error)ex.getCause();
        }
    }
    
    @VisibleForTesting
    static final class SynchronizedSubscriber extends Subscriber
    {
        private SynchronizedSubscriber(final EventBus eventBus, final Object o, final Method method) {
            super(eventBus, o, method, null);
        }
        
        @Override
        void invokeSubscriberMethod(final Object o) throws InvocationTargetException {
            synchronized (this) {
                super.invokeSubscriberMethod(o);
            }
        }
    }
}
