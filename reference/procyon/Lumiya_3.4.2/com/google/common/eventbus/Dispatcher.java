// 
// Decompiled by Procyon v0.6.0
// 

package com.google.common.eventbus;

import java.util.Queue;
import com.google.common.collect.Queues;
import java.util.concurrent.ConcurrentLinkedQueue;
import com.google.common.base.Preconditions;
import java.util.Iterator;

abstract class Dispatcher
{
    static Dispatcher immediate() {
        return ImmediateDispatcher.INSTANCE;
    }
    
    static Dispatcher legacyAsync() {
        return new LegacyAsyncDispatcher();
    }
    
    static Dispatcher perThreadDispatchQueue() {
        return new PerThreadQueuedDispatcher();
    }
    
    abstract void dispatch(final Object p0, final Iterator<Subscriber> p1);
    
    private static final class ImmediateDispatcher extends Dispatcher
    {
        private static final ImmediateDispatcher INSTANCE;
        
        static {
            INSTANCE = new ImmediateDispatcher();
        }
        
        @Override
        void dispatch(final Object o, final Iterator<Subscriber> iterator) {
            Preconditions.checkNotNull(o);
            while (iterator.hasNext()) {
                iterator.next().dispatchEvent(o);
            }
        }
    }
    
    private static final class LegacyAsyncDispatcher extends Dispatcher
    {
        private final ConcurrentLinkedQueue<EventWithSubscriber> queue;
        
        private LegacyAsyncDispatcher() {
            this.queue = Queues.newConcurrentLinkedQueue();
        }
        
        @Override
        void dispatch(final Object o, final Iterator<Subscriber> iterator) {
            Preconditions.checkNotNull(o);
            while (iterator.hasNext()) {
                this.queue.add(new EventWithSubscriber(o, (Subscriber)iterator.next()));
            }
            while (true) {
                final EventWithSubscriber eventWithSubscriber = this.queue.poll();
                if (eventWithSubscriber == null) {
                    break;
                }
                eventWithSubscriber.subscriber.dispatchEvent(eventWithSubscriber.event);
            }
        }
        
        private static final class EventWithSubscriber
        {
            private final Object event;
            private final Subscriber subscriber;
            
            private EventWithSubscriber(final Object event, final Subscriber subscriber) {
                this.event = event;
                this.subscriber = subscriber;
            }
        }
    }
    
    private static final class PerThreadQueuedDispatcher extends Dispatcher
    {
        private final ThreadLocal<Boolean> dispatching;
        private final ThreadLocal<Queue<Event>> queue;
        
        private PerThreadQueuedDispatcher() {
            this.queue = new ThreadLocal<Queue<Event>>() {
                @Override
                protected Queue<Event> initialValue() {
                    return (Queue<Event>)Queues.newArrayDeque();
                }
            };
            this.dispatching = new ThreadLocal<Boolean>() {
                @Override
                protected Boolean initialValue() {
                    return false;
                }
            };
        }
        
        @Override
        void dispatch(final Object o, final Iterator<Subscriber> iterator) {
            Preconditions.checkNotNull(o);
            Preconditions.checkNotNull(iterator);
            final Queue queue = this.queue.get();
            queue.offer(new Event(o, (Iterator)iterator));
            if (!this.dispatching.get()) {
                this.dispatching.set(true);
                try {
                    while (true) {
                        final Event event = (Event)queue.poll();
                        if (event == null) {
                            break;
                        }
                        while (event.subscribers.hasNext()) {
                            ((Subscriber)event.subscribers.next()).dispatchEvent(event.event);
                        }
                    }
                }
                finally {
                    this.dispatching.remove();
                    this.queue.remove();
                }
            }
        }
        
        private static final class Event
        {
            private final Object event;
            private final Iterator<Subscriber> subscribers;
            
            private Event(final Object event, final Iterator<Subscriber> subscribers) {
                this.event = event;
                this.subscribers = subscribers;
            }
        }
    }
}
