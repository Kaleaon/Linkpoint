package com.google.common.eventbus;

import com.google.common.base.Preconditions;
import com.google.common.collect.Queues;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

abstract class Dispatcher {

    private static final class ImmediateDispatcher extends Dispatcher {
        /* access modifiers changed from: private */
        public static final ImmediateDispatcher INSTANCE = new ImmediateDispatcher();

        private ImmediateDispatcher() {
        }

        /* access modifiers changed from: package-private */
        public void dispatch(Object obj, Iterator<Subscriber> it) {
            Preconditions.checkNotNull(obj);
            while (it.hasNext()) {
                it.next().dispatchEvent(obj);
            }
        }
    }

    private static final class LegacyAsyncDispatcher extends Dispatcher {
        private final ConcurrentLinkedQueue<EventWithSubscriber> queue;

        private static final class EventWithSubscriber {
            /* access modifiers changed from: private */
            public final Object event;
            /* access modifiers changed from: private */
            public final Subscriber subscriber;

            private EventWithSubscriber(Object obj, Subscriber subscriber2) {
                this.event = obj;
                this.subscriber = subscriber2;
            }
        }

        private LegacyAsyncDispatcher() {
            this.queue = Queues.newConcurrentLinkedQueue();
        }

        /* access modifiers changed from: package-private */
        public void dispatch(Object obj, Iterator<Subscriber> it) {
            Preconditions.checkNotNull(obj);
            while (it.hasNext()) {
                this.queue.add(new EventWithSubscriber(obj, it.next()));
            }
            while (true) {
                EventWithSubscriber poll = this.queue.poll();
                if (poll != null) {
                    poll.subscriber.dispatchEvent(poll.event);
                } else {
                    return;
                }
            }
        }
    }

    private static final class PerThreadQueuedDispatcher extends Dispatcher {
        private final ThreadLocal<Boolean> dispatching;
        private final ThreadLocal<Queue<Event>> queue;

        private static final class Event {
            /* access modifiers changed from: private */
            public final Object event;
            /* access modifiers changed from: private */
            public final Iterator<Subscriber> subscribers;

            private Event(Object obj, Iterator<Subscriber> it) {
                this.event = obj;
                this.subscribers = it;
            }
        }

        private PerThreadQueuedDispatcher() {
            this.queue = new ThreadLocal<Queue<Event>>() {
                /* access modifiers changed from: protected */
                public Queue<Event> initialValue() {
                    return Queues.newArrayDeque();
                }
            };
            this.dispatching = new ThreadLocal<Boolean>() {
                /* access modifiers changed from: protected */
                public Boolean initialValue() {
                    return false;
                }
            };
        }

        /* access modifiers changed from: package-private */
        /* JADX WARNING: Removed duplicated region for block: B:14:0x0038 A[SYNTHETIC] */
        /* JADX WARNING: Removed duplicated region for block: B:9:0x0054 A[Catch:{ all -> 0x005f }, LOOP:1: B:9:0x0054->B:8:?, LOOP_START] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void dispatch(java.lang.Object r5, java.util.Iterator<com.google.common.eventbus.Subscriber> r6) {
            /*
                r4 = this;
                r2 = 0
                com.google.common.base.Preconditions.checkNotNull(r5)
                com.google.common.base.Preconditions.checkNotNull(r6)
                java.lang.ThreadLocal<java.util.Queue<com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event>> r0 = r4.queue
                java.lang.Object r0 = r0.get()
                java.util.Queue r0 = (java.util.Queue) r0
                com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event r1 = new com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event
                r1.<init>(r5, r6)
                r0.offer(r1)
                java.lang.ThreadLocal<java.lang.Boolean> r1 = r4.dispatching
                java.lang.Object r1 = r1.get()
                java.lang.Boolean r1 = (java.lang.Boolean) r1
                boolean r1 = r1.booleanValue()
                if (r1 == 0) goto L_0x0026
            L_0x0025:
                return
            L_0x0026:
                java.lang.ThreadLocal<java.lang.Boolean> r1 = r4.dispatching
                r2 = 1
                java.lang.Boolean r2 = java.lang.Boolean.valueOf(r2)
                r1.set(r2)
            L_0x0030:
                java.lang.Object r1 = r0.poll()     // Catch:{ all -> 0x005f }
                com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event r1 = (com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.Event) r1     // Catch:{ all -> 0x005f }
                if (r1 != 0) goto L_0x0054
                java.lang.ThreadLocal<java.lang.Boolean> r0 = r4.dispatching
                r0.remove()
                java.lang.ThreadLocal<java.util.Queue<com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event>> r0 = r4.queue
                r0.remove()
                goto L_0x0025
            L_0x0043:
                java.util.Iterator r2 = r1.subscribers     // Catch:{ all -> 0x005f }
                java.lang.Object r2 = r2.next()     // Catch:{ all -> 0x005f }
                com.google.common.eventbus.Subscriber r2 = (com.google.common.eventbus.Subscriber) r2     // Catch:{ all -> 0x005f }
                java.lang.Object r3 = r1.event     // Catch:{ all -> 0x005f }
                r2.dispatchEvent(r3)     // Catch:{ all -> 0x005f }
            L_0x0054:
                java.util.Iterator r2 = r1.subscribers     // Catch:{ all -> 0x005f }
                boolean r2 = r2.hasNext()     // Catch:{ all -> 0x005f }
                if (r2 != 0) goto L_0x0043
                goto L_0x0030
            L_0x005f:
                r0 = move-exception
                java.lang.ThreadLocal<java.lang.Boolean> r1 = r4.dispatching
                r1.remove()
                java.lang.ThreadLocal<java.util.Queue<com.google.common.eventbus.Dispatcher$PerThreadQueuedDispatcher$Event>> r1 = r4.queue
                r1.remove()
                throw r0
            */
            throw new UnsupportedOperationException("Method not decompiled: com.google.common.eventbus.Dispatcher.PerThreadQueuedDispatcher.dispatch(java.lang.Object, java.util.Iterator):void");
        }
    }

    Dispatcher() {
    }

    static Dispatcher immediate() {
        return ImmediateDispatcher.INSTANCE;
    }

    static Dispatcher legacyAsync() {
        return new LegacyAsyncDispatcher();
    }

    static Dispatcher perThreadDispatchQueue() {
        return new PerThreadQueuedDispatcher();
    }

    /* access modifiers changed from: package-private */
    public abstract void dispatch(Object obj, Iterator<Subscriber> it);
}
