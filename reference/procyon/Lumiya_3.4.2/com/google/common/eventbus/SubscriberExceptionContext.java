// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import java.lang.reflect.Method;

public class SubscriberExceptionContext
{
    private final Object event;
    private final EventBus eventBus;
    private final Object subscriber;
    private final Method subscriberMethod;
    
    SubscriberExceptionContext(final EventBus eventBus, final Object o, final Object o2, final Method method) {
        this.eventBus = Preconditions.checkNotNull(eventBus);
        this.event = Preconditions.checkNotNull(o);
        this.subscriber = Preconditions.checkNotNull(o2);
        this.subscriberMethod = Preconditions.checkNotNull(method);
    }
    
    public Object getEvent() {
        return this.event;
    }
    
    public EventBus getEventBus() {
        return this.eventBus;
    }
    
    public Object getSubscriber() {
        return this.subscriber;
    }
    
    public Method getSubscriberMethod() {
        return this.subscriberMethod;
    }
}
