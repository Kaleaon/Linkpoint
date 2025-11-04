// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.eventbus;

import java.lang.reflect.Method;
import com.google.common.base.MoreObjects;
import java.util.Iterator;
import java.util.Locale;
import java.util.logging.Level;
import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import com.google.common.annotations.Beta;

@Beta
public class EventBus
{
    private static final Logger logger;
    private final Dispatcher dispatcher;
    private final SubscriberExceptionHandler exceptionHandler;
    private final Executor executor;
    private final String identifier;
    private final SubscriberRegistry subscribers;
    
    static {
        logger = Logger.getLogger(EventBus.class.getName());
    }
    
    public EventBus() {
        this("default");
    }
    
    public EventBus(final SubscriberExceptionHandler subscriberExceptionHandler) {
        this("default", MoreExecutors.directExecutor(), Dispatcher.perThreadDispatchQueue(), subscriberExceptionHandler);
    }
    
    public EventBus(final String s) {
        this(s, MoreExecutors.directExecutor(), Dispatcher.perThreadDispatchQueue(), LoggingHandler.INSTANCE);
    }
    
    EventBus(final String s, final Executor executor, final Dispatcher dispatcher, final SubscriberExceptionHandler subscriberExceptionHandler) {
        this.subscribers = new SubscriberRegistry(this);
        this.identifier = Preconditions.checkNotNull(s);
        this.executor = Preconditions.checkNotNull(executor);
        this.dispatcher = Preconditions.checkNotNull(dispatcher);
        this.exceptionHandler = Preconditions.checkNotNull(subscriberExceptionHandler);
    }
    
    final Executor executor() {
        return this.executor;
    }
    
    void handleSubscriberException(final Throwable t, final SubscriberExceptionContext subscriberExceptionContext) {
        Preconditions.checkNotNull(t);
        Preconditions.checkNotNull(subscriberExceptionContext);
        try {
            this.exceptionHandler.handleException(t, subscriberExceptionContext);
        }
        catch (final Throwable thrown) {
            EventBus.logger.log(Level.SEVERE, String.format(Locale.ROOT, "Exception %s thrown while handling exception: %s", thrown, t), thrown);
        }
    }
    
    public final String identifier() {
        return this.identifier;
    }
    
    public void post(final Object o) {
        final Iterator<Subscriber> subscribers = this.subscribers.getSubscribers(o);
        if (!subscribers.hasNext()) {
            if (!(o instanceof DeadEvent)) {
                this.post(new DeadEvent(this, o));
            }
        }
        else {
            this.dispatcher.dispatch(o, subscribers);
        }
    }
    
    public void register(final Object o) {
        this.subscribers.register(o);
    }
    
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).addValue(this.identifier).toString();
    }
    
    public void unregister(final Object o) {
        this.subscribers.unregister(o);
    }
    
    static final class LoggingHandler implements SubscriberExceptionHandler
    {
        static final LoggingHandler INSTANCE;
        
        static {
            INSTANCE = new LoggingHandler();
        }
        
        private static Logger logger(final SubscriberExceptionContext subscriberExceptionContext) {
            return Logger.getLogger(EventBus.class.getName() + "." + subscriberExceptionContext.getEventBus().identifier());
        }
        
        private static String message(final SubscriberExceptionContext subscriberExceptionContext) {
            final Method subscriberMethod = subscriberExceptionContext.getSubscriberMethod();
            return "Exception thrown by subscriber method " + subscriberMethod.getName() + '(' + subscriberMethod.getParameterTypes()[0].getName() + ')' + " on subscriber " + subscriberExceptionContext.getSubscriber() + " when dispatching event: " + subscriberExceptionContext.getEvent();
        }
        
        @Override
        public void handleException(final Throwable thrown, final SubscriberExceptionContext subscriberExceptionContext) {
            final Logger logger = logger(subscriberExceptionContext);
            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, message(subscriberExceptionContext), thrown);
            }
        }
    }
}
